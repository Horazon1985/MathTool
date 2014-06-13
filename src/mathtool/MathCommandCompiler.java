package mathtool;

import expressionbuilder.BinaryOperation;
import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.Constant;
import expressionbuilder.ExpressionException;
import expressionbuilder.NumericalMethods;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;
import expressionbuilder.TypeBinary;
import expressionbuilder.Variable;
import java.awt.Graphics;
import java.util.Arrays;
import javax.swing.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MathCommandCompiler {

    private final String[] commands = {"plot", "define"};
    
    
    /** Wichtig: Der String command und die Parameter params entahlten keine Leerzeichen mehr.
     * Diese wurden bereits im Vorfeld beseitigt. 
     */
    private static Command getCommand(String command, String[] params) throws ExpressionException {

        Command result = new Command();
        
        //PLOT
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

                double x_0, x_1;
                
                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }

                try{
                    x_0 = Double.parseDouble(params[1]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    x_1 = Double.parseDouble(params[2]);
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
                    result.setName(command);
                    result.setParams(params);
                    result.setLeft(expr);
                    return result;
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }
            
            } else {
        
                double x_0, x_1, y_0, y_1;
                
                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }

                try{
                    x_0 = Double.parseDouble(params[1]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    x_1 = Double.parseDouble(params[2]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try{
                    y_0 = Double.parseDouble(params[3]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    y_1 = Double.parseDouble(params[4]);
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
                    result.setName(command);
                    result.setParams(params);
                    result.setLeft(expr);
                    return result;
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }
        
            }
        }
        
        //DEFINE
        if (command.equals("define")){

            String par = params[0];
            String eq = "=";

            if (!par.contains(eq)){
                throw new ExpressionException("im Befehl 'define' muss ein Gleichheitszeichen als Zuweisungsoperator vorhanden sein.");
            }
            
            String function_name_and_params = par.substring(0, par.indexOf(eq));
            String function_term = par.substring(par.indexOf(eq) + 1, par.length());
            
            try{
                HashSet vars = new HashSet();
                Expression expr = Expression.build(function_term, vars);
            } catch (ExpressionException e){
                throw new ExpressionException("Ungültiger Ausdruck auf der rechten Seite.");
            }

            /** Falls man hier ankommt, muss das obige try funktioniert haben.
             * Jetzt wird die rechte Seite gelesen (durch den die Funktion auf der linken Seite von "=" definiert wird).
             */
            HashSet vars = new HashSet();
            Expression expr = Expression.build(function_term, vars);
            
            try{
                String function_name = Expression.getOperatorAndArguments(function_name_and_params)[0];
                String[] function_vars = Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);
            } catch (ExpressionException e){
                throw new ExpressionException("Ungültige Funktionsdefinition.");
            }
            
            /** Funktionsnamen und Variablen auslesen.
             */
            String function_name = Expression.getOperatorAndArguments(function_name_and_params)[0];
            String[] function_vars = Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);
            
            /** Prüfen, ob alle Variablen, die in expr auftreten, auch als Funktionsparameter vorhanden sind.
             * Sonst -> Fehler ausgeben.
             */
            
            List<String> vars_list = Arrays.asList(function_vars);
            Iterator iter = vars.iterator();
            String var;
            for (int i = 0; i < vars.size(); i++){
                var = (String) iter.next();
                if (!vars_list.contains(var)){
                    throw new ExpressionException("Auf der rechten Seite taucht eine Variable auf, die nicht als Funktionsparameter vorkommt.");
                }
            }
            
            String[] command_param_left = new String[1];
            command_param_left[0] = function_name_and_params; 
            
            result.setName(command);
            result.setParams(command_param_left);
            result.setLeft(expr);
            return result;
        
        }
        
        return result;
        
    }
            
    
    //Führt den Befehl aus.
    public void executeCommand(String commandLine, Graphics g2D, Graphics g3D, JTextArea area,
            NumericalMethods numericalMethods, GraphicMethods2D graphicMethods2D,
            GraphicMethods3D graphicMethods3D) throws ExpressionException, EvaluationException {
        
        int n = commandLine.length();

        //Leerzeichen beseitigen und alles zu Kleinbuchstaben machen
        char part;
        for(int i = 0; i < n; i++){
            part = commandLine.charAt(i);
            //Falls es ein Leerzeichen ist -> beseitigen
            if ((int)part == 32){
                commandLine = commandLine.substring(0, i)+commandLine.substring(i + 1, n);
                n--;
                i--;
            }
            //Falls es ein Großbuchstabe ist -> zu Kleinbuchstaben machen
            if (((int)part >= 65) && ((int)part <= 90)){
                part = (char)((int)part + 32);  //Macht Großbuchstaben zu Kleinbuchstaben
                commandLine = commandLine.substring(0, i)+part+commandLine.substring(i + 1, n);
            }
        } 
        
        String[] command_and_params = Expression.getOperatorAndArguments(commandLine);
        String command = command_and_params[0];
        String[] params = Expression.getArguments(command_and_params[1]);
        
        Command c = getCommand(command, params);
        
        if ((c.getName().equals("plot")) && (c.getParams().length == 3)){
            executePlot2D(c, g2D, graphicMethods2D);
        } else 
        if ((c.getName().equals("plot")) && (c.getParams().length == 5)){
            executePlot3D(c, g3D, graphicMethods3D);
        } else 
        if ((c.getName().equals("define")) && (c.getParams().length == 1)){
            executeDefine(c, area);
        } else {
            throw new ExpressionException("Ungültiger Befehl.");
        }

    } 
    

    /** Die folgenden Prozeduren führen einzelne Befehle aus.
     * executePlot2D zeichnet einen 2D-Graphen,
     * executePlot3D zeichnet einen 3D-Graphen,
     * etc.
     */
    
    /** c Enthält genau 3 Parameter 
     * Parameter: Expression, double, double (als Strings)
     */
    private void executePlot2D(Command c, Graphics g, GraphicMethods2D graphicMethods2D) throws ExpressionException,
            EvaluationException {
        
        HashSet vars = new HashSet();
        Expression expr = Expression.build(c.getParams()[0], vars);
        expr = expr.simplify();
        
        //Falls der Ausdruck expr konstant ist, soll die Achse die Bezeichnung "x" tragen.
        if (expr instanceof Constant){
            vars.add("x");
        }
        
        double x_0 = Double.parseDouble(c.getParams()[1]);
        double x_1 = Double.parseDouble(c.getParams()[2]);

        Iterator iter = vars.iterator();
        String var = (String) iter.next();
        
        boolean critical_line_exists = false;
        double pos_of_critical_line = 0;
        
        graphicMethods2D.expressionToGraph(expr, var, x_0, x_1);
        graphicMethods2D.setParameters(var, 0, 0, critical_line_exists, pos_of_critical_line);
        graphicMethods2D.drawGraph();
        
    }

    
    /** c Enthält genau 5 Parameter 
     * Parameter: Expression, double, double, double, double (als Strings)
     */
    private void executePlot3D(Command c, Graphics g, GraphicMethods3D graphicMethods3D) throws ExpressionException,
            EvaluationException {
    
        HashSet vars = new HashSet();
        Expression expr = Expression.build(c.getParams()[0], vars);
        expr = expr.simplify();
        
        //Falls der Ausdruck expr konstant ist, sollen die Achsen die Bezeichnungen "x" und "y" tragen.
        if (expr instanceof Constant){
            vars.add("x");
            vars.add("y");
        }

        /** Falls der Ausdruck expr nur von einer Variablen abhängt, 
         * sollen die andere Achse eine fest gewählte Bezeichnung tragen.
         * 
         */
        if (vars.size() == 1){
            if (vars.contains("z")) {
                vars.add("z_1");
            } else {
                vars.add("z");
            }
        }
        
        double x_0 = Double.parseDouble(c.getParams()[1]);
        double x_1 = Double.parseDouble(c.getParams()[2]);
        double y_0 = Double.parseDouble(c.getParams()[3]);
        double y_1 = Double.parseDouble(c.getParams()[4]);

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
        
        graphicMethods3D.setParameters(var1_alphabetical, var2_alphabetical, 150, 75, 75, 30);
        graphicMethods3D.expressionToGraph(expr, x_0, x_1, y_0, y_1);
        graphicMethods3D.drawGraph();

    }
        

    private void executeDefine(Command c, JTextArea area) throws ExpressionException,
            EvaluationException {

        
        
        
        
    }    
        
    
    
}