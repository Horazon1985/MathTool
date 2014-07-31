package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.Constant;
import expressionbuilder.ExpressionException;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;
import expressionbuilder.SelfDefinedFunction;
import expressionbuilder.Variable;
import expressionbuilder.AnalysisMethods;
import expressionbuilder.NumericalMethods;

import javax.swing.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class MathCommandCompiler {

    /** Liste aller gültiger Befehle.
     * Dies benötigt das Hauptprogramm MathToolForm, um zu prüfen, ob es sich um einen gültigen Befehl
     * handelt.
     */
    public static final String[] commands = {"clear", "def", "defvars", "latex", "plot", 
        "solve", "solvedgl", "taylordgl", "undef", "undefall"};
    
    
    /** Wichtig: Der String command und die Parameter params entahlten keine Leerzeichen mehr.
     * Diese wurden bereits im Vorfeld beseitigt. 
     */
    private static Command getCommand(String command, String[] params) throws ExpressionException {

        Command result = new Command();
        Object[] command_params;
        
        //CLEAR
        /** Struktur: clear()
         */
        if (command.equals("clear")){

            /** Prüft, ob der Befehl keine Parameter besitzt.
             */
            if (params.length > 0){
                throw new ExpressionException("Im Befehl 'clear' dürfen keine Parameter stehen.");
            }
            
            command_params = new Object[0];
            result.setName(command);
            result.setParams(command_params);
            return result;
        
        }

        //DEFINE
        /** Struktur: def(var = value)
         * var = Variablenname
         * reelle Zahl.
         * ODER:
         * def(func(var_1, ..., var_k) = EXPRESSION)
         * func = Funktionsname
         * var_i: Variablennamen
         * EXPRESSION: Funktionsterm, durch den func definiert wird.
         */
        if (command.equals("def")){

            String eq = "=";

            if (!params[0].contains(eq)){
                throw new ExpressionException("Im Befehl 'def' muss ein Gleichheitszeichen als Zuweisungsoperator vorhanden sein.");
            }
            
            String function_name_and_params = params[0].substring(0, params[0].indexOf(eq));
            String function_term = params[0].substring(params[0].indexOf(eq) + 1, params[0].length());

            /** Falls der linke Teil eine Variable ist, dann ist es eine Zuweisung, die dieser Variablen einen
             * festen Wert zuweist.
             * Beispiel: def(x = 2) liefert:
             * result.name = "def"
             * result.params = {"x"}
             * result.left = 2 (als Expression)
             */
            if (Expression.isValidVariable(function_name_and_params) && !Expression.isPI(function_name_and_params)){
                try{
                    double value = Double.parseDouble(function_term);
                    command_params = new Object[2];
                    command_params[0] = Variable.create(function_name_and_params);
                    command_params[1] = value;
                    result.setName(command);
                    result.setParams(command_params);
                    return result;
                } catch (NumberFormatException e){
                    throw new ExpressionException("Bei einer Variablenzuweisung muss der Variablen ein reeller Wert zugewiesen werden.");
                }                
            }
            
            /** Nun wird geprüft, ob es sich um eine Funktionsdeklaration handelt.
             * Zunächst wird versucht, den rechten Teilstring vom "=" in einen Ausdruck umzuwandeln.
             */
            try{
                HashSet vars = new HashSet();
                Expression.build(function_term, vars);
            } catch (ExpressionException e){
                throw new ExpressionException("Ungültiger Ausdruck auf der rechten Seite.");
            }

            /** Falls man hier ankommt, muss das obige try funktioniert haben.
             * Jetzt wird die rechte Seite gelesen (durch den die Funktion auf der linken Seite von "=" definiert wird).
             */
            HashSet vars = new HashSet();
            Expression expr = Expression.build(function_term, vars);
            
            try{
                /** function_name = Funktionsname
                 * function_vars = Funktionsvariablen
                 * Beispiel: def(f(x, y) = x^2+y)
                 * Dann: 
                 * function_name = "f"
                 * function_vars = {"x_ABSTRACT", "y_ABSTRACT"}
                 */
                Expression.getOperatorAndArguments(function_name_and_params);
                Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);
            } catch (ExpressionException e){
                throw new ExpressionException("Ungültige Variable- oder Funktionsdefinition.");
            }
            
            /** Funktionsnamen und Variablen auslesen.
             */
            String function_name = Expression.getOperatorAndArguments(function_name_and_params)[0];
            String[] function_vars = Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);

            /** Nun wird geprüft, ob die einzelnen Parameter in der Funktionsklammer gültige Variablen sind
             */
            for (int i = 0; i < function_vars.length; i++){
                if (!Expression.isValidVariable(function_vars[i])){
                    throw new ExpressionException("'" + function_vars[i] + "' ist keine gültige Variable.");
                }
            }
            
            
            /** Nun wird geprüft, ob die Variablen in function_vars auch alle verschieden sind!
             */
            HashSet function_vars_as_hashset = new HashSet();
            for (int i = 0; i < function_vars.length; i++){
                if (function_vars_as_hashset.contains(function_vars[i])){
                    throw new ExpressionException("In der Funktionsdeklaration der Funktion " + function_name + " dürfen"
                            + " nicht mehrmals dieselben Variablen vorkommen.");
                }
                function_vars_as_hashset.add(function_vars[i]);
            }
            
            /** Hier wird den Variablen der Index "_ABSTRACT" angehängt.
             * Dies dient der Kennzeichnung, dass diese Variablen Platzhalter für weitere Ausdrücke  
             * und keine echten Variablen sind. Solche Variablen können niemals in einem geparsten Ausdruck
             * vorkommen, da der Parser Expression.build solche Variablen nicht akzeptiert.
             */
            for (int i = 0; i < function_vars.length; i++){
                function_vars[i] = function_vars[i] + "_ABSTRACT";
            }
            
            /** Prüfen, ob alle Variablen, die in expr auftreten, auch als Funktionsparameter vorhanden sind.
             * Sonst -> Fehler ausgeben.
             *
             * Zugleich: Im Ausdruck expr werden alle Variablen der Form var durch Variablen der Form
             * var_ABSTRACT ersetzt und alle Variablen im HashSet vars ebenfalls. 
             */
            
            List<String> function_vars_list = Arrays.asList(function_vars);
            Iterator iter = vars.iterator();
            String var;
            for (int i = 0; i < vars.size(); i++){
                var = (String) iter.next(); 
                expr = expr.replaceVariable(var, new Variable(var + "_ABSTRACT"));
                var = var + "_ABSTRACT";
                if (!function_vars_list.contains(var)){
                    throw new ExpressionException("Auf der rechten Seite taucht eine Variable auf, die nicht als Funktionsparameter vorkommt.");
                }
            }
            
            /** result.params werden gesetzt.
             */
            command_params = new Object[2 + function_vars.length];
            command_params[0] = function_name; 
            for (int i = 1; i <= function_vars.length; i++){
                command_params[i] = Variable.create(function_vars[i - 1]);
            }
            command_params[1 + function_vars.length] = expr;
            
            /** Für das obige Beispiel def(f(x, y) = x^2+y) gilt dann:
             * result.name = "def"
             * result.params = {"f", "x_ABSTRACT", "y_ABSTRACT"}
             * result.left = x_ABSTRACT^2+y_ABSTRACT (als Expression).
             */
            result.setName(command);
            result.setParams(command_params);
            return result;
        
        }
        
        //DEFINEDVARS
        /** Struktur: defvars()
         */
        if (command.equals("defvars")){

            /** Prüft, ob der Befehl keine Parameter besitzt.
             */
            if (params.length > 0){
                throw new ExpressionException("Im Befehl 'defvars' dürfen keine Parameter stehen.");
            }
            
            command_params = new Object[0];
            result.setName(command);
            result.setParams(command_params);
            return result;
        
        }
		
	//LATEX
        /** Struktur: latex(EXPRESSION)
         * EXPRESSION: Ausdruck, welcher in einen Latex-Code umgewandelt werden soll.
         */
	if (command.equals("latex")){
		
            if (params.length != 1){
                throw new ExpressionException("Im Befehl 'latex' muss genau ein Parameter stehen. Dieser muss ein gültiger Ausdruck sein.");
            }
			
            try{
		Expression expr = Expression.build(params[0], new HashSet());
                command_params = new Object[1];
                command_params[0] = expr;
		result.setName(command);
		result.setParams(command_params);
		return result;
            } catch (ExpressionException e){
		throw new ExpressionException(e.getMessage());
            }
			
	}

        //PLOT
        /** Struktur: PLOT(EXPRESSION, value_1, value_2)
         * EXPRESSION: Ausdruck in einer Variablen.
         * value_1 < value_2: Grenzen des Zeichenbereichs
         * ODER:
         * PLOT(EXPRESSION, value_1, value_2, value_3, value_4)
         * EXPRESSION: Ausdruck in höchstens zwei Variablen.
         * value_1 < value_2, value_3 < value_4: Grenzen des Zeichenbereichs.
         * Die beiden Variablen werden dabei alphabetisch geordnet.
         */
        if (command.equals("plot")){
            if (params.length < 3){
                throw new ExpressionException("Zu wenig Parameter im Befehl 'plot'");
            }
            if (params.length == 4){
                throw new ExpressionException("Falsche Anzahl von Parametern im Befehl 'plot'");
            }
            if (params.length > 5){
                throw new ExpressionException("Zu viele Parameter im Befehl 'plot'");
            }
            
            if (params.length == 3){

                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }

                try{
                    Double.parseDouble(params[1]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    Double.parseDouble(params[2]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                    if (vars.size() > 1){
                        throw new ExpressionException("Der Ausdruck im Befehl 'plot' darf höchstens eine Variable enthalten. Dieser enthält jedoch " 
                                + String.valueOf(vars.size()) + " Variablen.");
                    }
                    double x_0 = Double.parseDouble(params[1]);
                    double x_1 = Double.parseDouble(params[2]);
                    command_params = new Object[3];
                    command_params[0] = expr;
                    command_params[1] = x_0;
                    command_params[2] = x_1;
                    result.setName(command);
                    result.setParams(command_params);
                    return result;
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }
            
            } else {
        
                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }

                try{
                    Double.parseDouble(params[1]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    Double.parseDouble(params[2]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try{
                    Double.parseDouble(params[3]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    Double.parseDouble(params[4]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der vierte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                    if (vars.size() > 2){
                        throw new ExpressionException("Der Ausdruck im Befehl 'plot' darf höchstens zwei Variablen enthalten. Dieser enthält jedoch " 
                                + String.valueOf(vars.size()) + " Variablen.");
                    }
                    double x_0 = Double.parseDouble(params[1]);
                    double x_1 = Double.parseDouble(params[2]);
                    double y_0 = Double.parseDouble(params[3]);
                    double y_1 = Double.parseDouble(params[4]);
                    command_params = new Object[5];
                    command_params[0] = expr;
                    command_params[1] = x_0;
                    command_params[2] = x_1;
                    command_params[3] = y_0;
                    command_params[4] = y_1;
                    result.setName(command);
                    result.setParams(command_params);
                    return result;
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }
        
            }
        }

        //SOLVE
        /** Struktur: solve(expr, x_1, x_2) ODER solve(expr, x_1, x_2, n)
         * var = Variable in der DGL
         * x_0, x_1 legen den Lösungsbereich fest
         * y_0 = Funktionswert an der Stelle x_0
         */
        if (command.equals("solve")){
            if (params.length < 3){
                throw new ExpressionException("Zu wenig Parameter im Befehl 'solve'");
            }
            if (params.length > 4){
                throw new ExpressionException("Zu viele Parameter im Befehl 'solve'");
            }
            
            HashSet vars = new HashSet();
            Expression expr = Expression.build(params[0], vars);
            if (vars.size() > 1){
                throw new ExpressionException("In der Gleichung darf höchstens eine Veränderliche auftreten.");
            }

            try{
                Double.parseDouble(params[1]);
            } catch (NumberFormatException e){
                throw new ExpressionException("Der zweite Parameter im Befehl 'solve' muss eine reelle Zahl sein.");
            }
                
            try{
                Double.parseDouble(params[2]);
            } catch (NumberFormatException e){
                throw new ExpressionException("Der dritte Parameter im Befehl 'solve' muss eine reelle Zahl sein.");
            }

            if (params.length == 4){
                try{
                    Integer.parseInt(params[3]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der vierte Parameter im Befehl 'solve' muss positive ganze Zahl sein.");
                }
            }

            double x_1 = Double.parseDouble(params[1]);
            double x_2 = Double.parseDouble(params[2]);

            if (params.length == 3){
                command_params = new Object[3];
                command_params[0] = expr;
                command_params[1] = x_1;
                command_params[2] = x_2;
            } else {
                int n = Integer.parseInt(params[3]);
                if (n < 1) {
                    throw new ExpressionException("Der vierte Parameter im Befehl 'solve' muss positive ganze Zahl sein.");
                }
                command_params = new Object[4];
                command_params[0] = expr;
                command_params[1] = x_1;
                command_params[2] = x_2;
                command_params[3] = n;
            }
                
            result.setName(command);
            result.setParams(command_params);
            return result;
            
        }

        //SOLVEDGL
        /** Struktur: solvedgl(EXPRESSION, var, x_0, x_1, y_0)
         * EXPRESSION: Rechte Seite der DGL y' = EXPRESSION.
         * var = Variable in der DGL
         * x_0, x_1 legen den Lösungsbereich fest
         * y_0 = Funktionswert an der Stelle x_0
         */
        if (command.equals("solvedgl")){
            if (params.length < 5){
                throw new ExpressionException("Zu wenig Parameter im Befehl 'solvedgl'");
            }
            if (params.length > 5){
                throw new ExpressionException("Zu viele Parameter im Befehl 'solvedgl'");
            }
            
            if (params.length == 5){

                HashSet vars = new HashSet();
                Expression expr = Expression.build(params[0], vars);
                if (vars.size() > 2){
                    throw new ExpressionException("In der Differentialgleichung dürfen höchstens zwei Veränderliche auftreten.");
                }

                if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
                    if (vars.size() == 2){
                        if (!vars.contains(params[1])){
                            throw new ExpressionException("Die Variable " + params[1] + " muss in der Differentialgleichung vorkommen.");
                        }
                    }
                    
                } else {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'solvedgl' muss eine gültige Variable sein.");
                }

                try{
                    Double.parseDouble(params[2]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'solvedgl' muss eine reelle Zahl sein.");
                }
                
                try{
                    Double.parseDouble(params[3]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der vierte Parameter im Befehl 'solvedgl' muss eine reelle Zahl sein.");
                }

                try{
                    Double.parseDouble(params[4]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der fünfte Parameter im Befehl 'solvedgl' muss eine reelle Zahl sein.");
                }

                double x_0 = Double.parseDouble(params[2]);
                double x_1 = Double.parseDouble(params[3]);
                double y_0 = Double.parseDouble(params[4]);
                command_params = new Object[5];
                command_params[0] = expr;
                command_params[1] = params[1];
                command_params[2] = x_0;
                command_params[3] = x_1;
                command_params[4] = y_0;
                
                result.setName(command);
                result.setParams(command_params);
                return result;
            
            } 
        }
        
        //TAYLORDGL
        /** Struktur: taylordgl(EXPRESSION, var, ord, x_0, y_0, y'(0), ..., y^(ord - 1)(0), k)
         * EXPRESSION: Rechte Seite der DGL y^{(ord)} = EXPRESSION.
         * Anzahl der parameter ist also = ord + 5
         * var = Variable in der DGL
         * ord = Ordnung der DGL. 
         * x_0, y_0 legen das AWP fest
         * k = Ordnung des Taylorpolynoms (an der Stelle x_0)
         */
        if (command.equals("taylordgl")){
            if (params.length < 6){
                throw new ExpressionException("Zu wenig Parameter im Befehl 'taylordgl'");
            }

            if (params.length >= 6){

                //Ermittelt die Ordnung der DGL
                try{
                    Integer.parseInt(params[2]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'taylordgl' muss eine positive ganze Zahl sein.");
                }
                
                int ord = Integer.parseInt(params[2]);

                if (ord < 1){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'taylordgl' muss eine positive ganze Zahl sein.");
                }

                /** Prüft, ob es sich um eine korrekte DGL handelt:
                 * Beispielsweise darf in einer DGL der ordnung 3 nicht y''', y'''' etc. auf der rechten Seite auftreten.
                 */ 
                HashSet vars = new HashSet();
                Expression expr = Expression.build(params[0], vars);
                
                HashSet vars_without_primes = new HashSet();
                Iterator iter = vars.iterator();
                String var_without_primes;
                for (int i = 0; i < vars.size(); i++){
                    var_without_primes = (String) iter.next();
                    if (!var_without_primes.replaceAll("'", "").equals(params[1])){
                        if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord){
                            throw new ExpressionException("Die Differentialgleichung besitzt die Ordnung " + ord + ". " 
                                    + "Es dürfen daher nur Ableitungen höchstens " + (ord - 1) + ". Ordnung auftreten.");
                        }
                        var_without_primes = var_without_primes.replaceAll("'", "");
                    }
                    vars_without_primes.add(var_without_primes);
                }
                
                if (vars_without_primes.size() > 2){
                    throw new ExpressionException("In der Differentialgleichung dürfen höchstens zwei Veränderliche auftreten.");
                }

                if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
                    if (vars_without_primes.size() == 2){
                        if (!vars.contains(params[1])){
                            throw new ExpressionException("Die Variable " + params[1] + " muss in der Differentialgleichung vorkommen.");
                        }
                    }
                } else {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'taylordgl' muss eine gültige Variable sein.");
                }
                
                if (params.length < ord + 5){
                    throw new ExpressionException("Zu wenig Parameter im Befehl 'taylordgl'");
                }
                if (params.length > ord + 5){
                    throw new ExpressionException("Zu viele Parameter im Befehl 'taylordgl'");
                }

                //Prüft, ob die AWP-Daten korrekt sind
                for (int i = 3; i < ord + 4; i++){
                    try{
                        Double.parseDouble(params[i]);
                    } catch (NumberFormatException e){
                        throw new ExpressionException("Der " + String.valueOf(i + 1) + ". Parameter im Befehl 'taylordgl' muss eine reelle Zahl sein.");
                    }
                }
                
                try{
                    Integer.parseInt(params[ord + 4]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der letzte Parameter im Befehl 'taylordgl' muss eine nichtnegative ganze Zahl sein.");
                }

                command_params = new Object[ord + 5];
                command_params[0] = expr;
                command_params[1] = Variable.create(params[1]);
                command_params[2] = ord;
                for (int i = 3; i < ord + 4; i++){
                    command_params[i] = Double.parseDouble(params[i]);                
                }
                command_params[ord + 4] = Integer.parseInt(params[ord + 4]);
                
                result.setName(command);
                result.setParams(command_params);
                return result;
            
            } 
        }

        //UNDEFINE
        /** Struktur: undef(var_1, ..., var_k)
         * var_i: Variablenname
         */
        if (command.equals("undef")){

            /** Prüft, ob alle Parameter gültige Variablen sind.
             */
            for (int i = 0; i < params.length; i++){
                if (!Expression.isValidVariable(params[i]) && !Expression.isPI(params[i])){
                    throw new ExpressionException("Die " + (i + 1) + " im Befehl 'undef' ist keine gültige Variablen.");
                }
            }
            
            command_params = new Object[params.length];
            for (int i = 0; i < params.length; i++){
                command_params[i] = Variable.create(params[i]);
            }
            
            result.setName(command);
            result.setParams(command_params);
            return result;
        
        }

        //UNDEFINEALL
        /** Struktur: undefall()
         */
        if (command.equals("undefall")){

            /** Prüft, ob der Befehl keine Parameter besitzt.
             */
            if (params.length > 0){
                throw new ExpressionException("Im Befehl 'undefall' dürfen keine Parameter stehen.");
            }
            
            command_params = new Object[0];
            result.setName(command);
            result.setParams(command_params);
            return result;
        
        }

        return result;
        
    }
            
    
    //Führt den Befehl aus.
    public static void executeCommand(String commandLine, JTextArea area, GraphicMethods2D graphicMethods2D,
            GraphicMethods3D graphicMethods3D, Hashtable definedVars, HashSet definedVarsSet) 
            throws ExpressionException, EvaluationException {
        
        int n = commandLine.length();

        //Leerzeichen beseitigen und alles zu Kleinbuchstaben machen
        commandLine = commandLine.replaceAll(" ", "");

        //Falls Großbuchstaben auftreten -> zu Kleinbuchstaben machen
        char part;
        for(int i = 0; i < n; i++){
            part = commandLine.charAt(i);
            if (((int)part >= 65) && ((int)part <= 90)){
                part = (char)((int)part + 32);  //Macht Großbuchstaben zu Kleinbuchstaben
                commandLine = commandLine.substring(0, i)+part+commandLine.substring(i + 1, n);
            }
        } 
        
        String[] command_and_params = Expression.getOperatorAndArguments(commandLine);
        String command = command_and_params[0];
        String[] params = Expression.getArguments(command_and_params[1]);
        
        Command c = getCommand(command, params);
        
        if (c.getName().equals("clear")){
            executeClear(c, area);
        } else 
        if ((c.getName().equals("def")) && (c.getParams().length >= 1)){
            executeDefine(c, area, definedVars, definedVarsSet);
        } else 
        if (c.getName().equals("defvars")){
            executeDefVars(c, area, definedVars, definedVarsSet);
        } else 
        if (c.getName().equals("latex")){
            executeLatex(c, area);
	} else 
	if ((c.getName().equals("plot")) && (c.getParams().length == 3)){
	    executePlot2D(c, graphicMethods2D);
	} else 
	if ((c.getName().equals("plot")) && (c.getParams().length == 5)){
	    executePlot3D(c, graphicMethods3D);
        } else 
	if (c.getName().equals("solve")){
	    executeSolve(c, area, graphicMethods2D);
        } else 
	if (c.getName().equals("solvedgl")){
	    executeSolveDGL(c, area, graphicMethods2D);
        } else 
	if (c.getName().equals("taylordgl")){
	    executeTaylorDGL(c, area);
        } else 
        if (c.getName().equals("undef")){
            executeUndefine(c, area, definedVars, definedVarsSet);
        } else 
        if (c.getName().equals("undefall")){
            executeUndefineAll(c, area, definedVars, definedVarsSet);
        } else {
            throw new ExpressionException("Ungültiger Befehl.");
        }

    } 
    

    /** Die folgenden Prozeduren führen einzelne Befehle aus.
     * executePlot2D zeichnet einen 2D-Graphen,
     * executePlot3D zeichnet einen 3D-Graphen,
     * etc.
     */
    
    private static void executeClear(Command c, JTextArea area) 
	throws ExpressionException {
        area.setText("");
    }    

    
    private static void executeDefine(Command c, JTextArea area, Hashtable definedVars, HashSet definedVarsSet) 
            throws ExpressionException, EvaluationException {

        /** Falls ein Variablenwert definiert wird.
         */
        if (c.getParams().length == 2){
            String var = ((Variable) c.getParams()[0]).getName();
            double value = (double) c.getParams()[1];
            Variable.setValue(var, value);
            definedVars.put(var, value);
            definedVarsSet.add(var);
            area.append("Der Wert der Variablen " + var + " wurde auf " + value + " gesetzt. \n");
        } else {
        /** Falls eine Funktion definiert wird.
         */
            String function_name = (String)(c.getParams()[0]);
            String[] vars = new String[c.getParams().length - 2];
            Expression[] exprs_for_vars = new Expression[c.getParams().length - 2];
            for (int i = 0; i < c.getParams().length - 2; i++){
                vars[i] = ((Variable) c.getParams()[i + 1]).getName();
                exprs_for_vars[i] = (Variable) c.getParams()[i + 1];
            }
            SelfDefinedFunction.abstractExpressionsForSelfDefinedFunctions.put(function_name, (Expression) c.getParams()[c.getParams().length - 1]);
            SelfDefinedFunction.innerExpressionsForSelfDefinedFunctions.put(function_name, exprs_for_vars);
            SelfDefinedFunction.varsForSelfDefinedFunctions.put(function_name, vars);
        }
        
    }    
        
    
    private static void executeDefVars(Command c, JTextArea area, Hashtable definedVars, HashSet definedVarsSet) 
            throws ExpressionException, EvaluationException {
        area.append("Liste aller Variablen mit vordefinierten Werten: " + definedVars + "\n");
    }    

    
    private static void executeLatex(Command c, JTextArea area) 
	throws ExpressionException {
        area.append("Latex-Code: " + ((Expression) c.getParams()[0]).expressionToLatex() + "\n");
    }    
	
	
    /** c Enthält genau 3 Parameter 
     * Parameter: Expression, double, double (als Strings)
     */
    private static void executePlot2D(Command c, GraphicMethods2D graphicMethods2D) throws ExpressionException,
            EvaluationException {
        
        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr.getContainedVars(vars);
        expr = expr.simplify();
        
        //Falls der Ausdruck expr konstant ist, soll die Achse die Bezeichnung "x" tragen.
        if (expr instanceof Constant){
            vars.add("x");
        }
        
        double x_0 = (double) c.getParams()[1];
        double x_1 = (double) c.getParams()[2];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();
        
        graphicMethods2D.setExpression(expr);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.expressionToGraph(var, x_0, x_1);
        graphicMethods2D.computeMaxXMaxY();
        graphicMethods2D.setParameters(var, graphicMethods2D.getAxeCenterX(), graphicMethods2D.getAxeCenterY());
        graphicMethods2D.drawGraph();
        
    }

    
    /** c Enthält genau 5 Parameter 
     * Parameter: Expression, double, double, double, double (als Strings)
     */
    private static void executePlot3D(Command c, GraphicMethods3D graphicMethods3D) throws ExpressionException,
            EvaluationException {
    
        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr.getContainedVars(vars);
        expr = expr.simplify();
        
        //Falls der Ausdruck expr konstant ist, sollen die Achsen die Bezeichnungen "x" und "y" tragen.
        if (expr instanceof Constant){
            vars.add("x");
            vars.add("y");
        }

        /** Falls der Ausdruck expr nur von einer Variablen abhängt, 
         * sollen die andere Achse eine fest gewählte Bezeichnung tragen.
         * Dies wirdim Folgenden geregelt.
         */
        if (vars.size() == 1){
            if (vars.contains("y")) {
                vars.add("z");
            } else {
                vars.add("y");
            }
        }
        
        double x_1 = (double) c.getParams()[1];
        double x_2 = (double) c.getParams()[2];
        double y_1 = (double) c.getParams()[3];
        double y_2 = (double) c.getParams()[4];

        if (x_1 >= x_2){
            throw new ExpressionException("Der dritte Parameter muss größer sein als der zweite Parameter.");
        }
        if (y_1 >= y_2){
            throw new ExpressionException("Der fünfte Parameter muss größer sein als der vierte Parameter.");
        }
        
        Iterator iter = vars.iterator();
        String var1 = (String) iter.next();
        String var2 = (String) iter.next();
        
        /** Die Variablen var1 und var2 sind evtl. noch nicht in alphabetischer Reihenfolge.
         * Dies wird hier nachgeholt.
         * GRUND: Der Zeichenbereich wird durch vier Zahlen eingegrenzt, welche den Variablen in
         * ALPHABETISCHER Reihenfolge entsprechen.
         */
        
        String var1_alphabetical = var1;
        String var2_alphabetical = var2;
        
        if ((int) var1.charAt(0) > (int) var2.charAt(0)){
            var1_alphabetical = var2;
            var2_alphabetical = var1;
        }
        if ((int) var1.charAt(0) == (int) var2.charAt(0)){
            if ((var1.length() > 1) && (var2.length() == 1)){
                var1_alphabetical = var2;
                var2_alphabetical = var1;
            }
            if ((var1.length() > 1) && (var2.length() > 1)){
                int index_var1 = Integer.parseInt(var1.substring(2));
                int index_var2 = Integer.parseInt(var2.substring(2));
                if (index_var1 > index_var2){
                    var1_alphabetical = var2;
                    var2_alphabetical = var1;
                }
            }
        }
        
        graphicMethods3D.setParameters(var1_alphabetical, var2_alphabetical, 150, 200, 30, 30);
        graphicMethods3D.expressionToGraph(expr, x_1, x_2, y_1, y_2);
        graphicMethods3D.drawGraph();

    }

    
    private static void executeSolve(Command c, JTextArea area, GraphicMethods2D graphicMethods2D) 
	throws ExpressionException, EvaluationException {

        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr.getContainedVars(vars);
        //Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
        String var = "x";
        if (!vars.isEmpty()){
            Iterator iter = vars.iterator();
            var = (String) iter.next();
        }
        
        double x_1 = (double) c.getParams()[1];
        double x_2 = (double) c.getParams()[2];
        /** Falls die Anzahl der Unterteilungen nicht angegeben wird, so soll das Intervall in 1000000 Teile unterteilt werden.
         * 
         */
        int n = 1000000;
        
        if (c.getParams().length == 4){
            n = (int) c.getParams()[3];
        }
        
        Hashtable<Integer, Double> result = NumericalMethods.solve(expr, x_1, x_2, n);
        
        area.append("Lösungen der Gleichung: " + expr.writeFormula() + " = 0 \n"); 
        for (int i = 0; i < result.size(); i++){
            area.append(var + "_" + (i + 1) + " = " + result.get(i + 1) + "\n");
        }

        graphicMethods2D.setExpression(expr);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.expressionToGraph(var, x_1, x_2);
        graphicMethods2D.computeMaxXMaxY();
        graphicMethods2D.setParameters(var, graphicMethods2D.getAxeCenterX(), graphicMethods2D.getAxeCenterY());
        graphicMethods2D.drawGraph();
        
        /** Nullstellen rot markieren
         */
        double[][] zeros = new double[result.size()][2];
        for (int i = 0; i < zeros.length; i++){
            zeros[i][0] = result.get(i + 1);
            zeros[i][1] = 0;
        }
        
        graphicMethods2D.drawZeros(zeros);
    }    

    
    private static void executeSolveDGL(Command c, JTextArea area, GraphicMethods2D graphicMethods2D) 
	throws ExpressionException, EvaluationException {

        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr.getContainedVars(vars);
        String var1 = (String) c.getParams()[1];
        double x_0 = (double) c.getParams()[2];
        double x_1 = (double) c.getParams()[3];
        double y_0 = (double) c.getParams()[4];
        
        /** zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt werden. 
        */
        String var2 = new String();

        if (vars.isEmpty()){
            if (var1.equals("y")){
                var2 = "z";
            } else {
                var2 = "y";
            }
        } else 
        if (vars.size() == 1){
            if (vars.contains(var1)){
                if (var1.equals("y")){
                    var2 = "z";
                } else {
                    var2 = "y";
                }
            } else {
                Iterator iter = vars.iterator();
                var2 = (String) iter.next();
            }
        } else {
            Iterator iter = vars.iterator();
            var2 = (String) iter.next();
            if (var2.equals(var1)){
                var2 = (String) iter.next();
            }
        }
        
        double[][] solution = NumericalMethods.solveDGL(expr, var1, var2, x_0, x_1, y_0, 1000);
        
        area.append("Lösung der Differentialgleichung: " + var2 + "'(" + var1 + ") = " + expr.writeFormula() 
                + ", " + var2 + "(" + String.valueOf(x_0) + ") = " + String.valueOf(y_0) + "\n");
        for (int i = 0; i < solution.length; i++){
            area.append(var1 + " = " + solution[i][0] + "; " + var2 + " = " + solution[i][1] + "\n");
        }

        /** Falls die Lösung innerhalb des Berechnungsbereichs unendlich/undefiniert ist.
         * 
         */
        double pos_of_critical_line = 0;
        if (solution.length < 1001){
            pos_of_critical_line = x_0 + (solution.length)*(x_1 - x_0)/1000;
            area.append("Die Lösung der Differentialgleichung ist an der Stelle " + pos_of_critical_line + " nicht definiert. \n");
        }

        graphicMethods2D.setGraphArray(solution);
        graphicMethods2D.setGraphIsFixed(true);
        graphicMethods2D.computeMaxXMaxY();
        graphicMethods2D.setParameters(var1, graphicMethods2D.getAxeCenterX(), graphicMethods2D.getAxeCenterY());
        graphicMethods2D.drawGraph();

    }    

    
    private static void executeTaylorDGL(Command c, JTextArea area) 
	throws ExpressionException, EvaluationException {

        int ord = (int) c.getParams()[2];
        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr.getContainedVars(vars);
        
        HashSet vars_without_primes = new HashSet();
        Iterator iter = vars.iterator();
        String var_without_primes;
        for (int i = 0; i < vars.size(); i++){
            var_without_primes = (String) iter.next();
            if (!var_without_primes.replaceAll("'", "").equals(c.getParams()[1])){
                if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord){
                    throw new ExpressionException("Die Differentialgleichung besitzt die Ordnung " + ord + ". " 
                            + "Es dürfen daher nur Ableitungen höchstens " + (ord - 1) + ". Ordnung auftreten.");
                }
                var_without_primes = var_without_primes.replaceAll("'", "");
            }
            vars_without_primes.add(var_without_primes);
        }
        
        String var1 = ((Variable) c.getParams()[1]).getName();
        double x_0 = (double) c.getParams()[3];
        double[] y_0 = new double[ord];
        for (int i = 0; i < y_0.length; i++){
            y_0[i] = (double) c.getParams()[i + 4];
        }
                
        int k = (int) c.getParams()[ord + 4];
        
        /** zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt werden. 
        */
        String var2 = new String();

        if (vars_without_primes.isEmpty()){
            if (var1.equals("y")){
                var2 = "z";
            } else {
                var2 = "y";
            }
        } else 
        if (vars_without_primes.size() == 1){
            if (vars_without_primes.contains(var1)){
                if (var1.equals("y")){
                    var2 = "z";
                } else {
                    var2 = "y";
                }
            } else {
                iter = vars_without_primes.iterator();
                var2 = (String) iter.next();
            }
        } else {
            iter = vars_without_primes.iterator();
            var2 = (String) iter.next();
            if (var2.equals(var1)){
                var2 = (String) iter.next();
            }
        }
        
        Expression result = AnalysisMethods.getTaylorPolynomialFromDGL(expr, var1, ord, x_0, y_0, k);
        area.append(result.writeFormula() + "\n");

    }    

    
    private static void executeUndefine(Command c, JTextArea area, Hashtable definedVars, HashSet definedVarsSet) 
            throws ExpressionException, EvaluationException {

        /** Falls ein Variablenwert freigegeben wird.
         */
        String current_var;
        for (int i = 0; i < c.getParams().length; i++){
            current_var = ((Variable) c.getParams()[i]).getName();
            if (definedVarsSet.contains(current_var)){
                definedVarsSet.remove(current_var);
                definedVars.remove(current_var);
                area.append("Die Variable " + current_var + " ist wieder eine Unbestimmte. \n");
            }
        }
        
    }    

    
    private static void executeUndefineAll(Command c, JTextArea area, Hashtable definedVars, HashSet definedVarsSet) 
            throws ExpressionException, EvaluationException {

        /** Entleert definedVarsSet und definedVars
         */
        definedVarsSet.clear();
        definedVars.clear();
        area.append("Alle Variablen sind wieder Unbestimmte. \n");
        
    }    
    
    
}
