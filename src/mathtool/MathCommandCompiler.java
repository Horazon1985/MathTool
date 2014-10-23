package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.Constant;
import expressionbuilder.ExpressionException;
import graphic.GraphicMethods2D;
import graphic.GraphicMethodsCurves2D;
import graphic.GraphicMethods3D;
import graphic.GraphicMethodsCurves3D;
import graphic.GraphicMethodsPolar2D;
import expressionbuilder.SelfDefinedFunction;
import expressionbuilder.Variable;
import expressionbuilder.AnalysisMethods;
import expressionbuilder.BinaryOperation;
import expressionbuilder.NumericalMethods;
import expressionbuilder.SolveMethods;
import expressionbuilder.TypeBinary;
import java.math.BigDecimal;

import javax.swing.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MathCommandCompiler {

    /**
     * Liste aller gültiger Befehle. Dies benötigt das Hauptprogramm
     * MathToolForm, um zu prüfen, ob es sich um einen gültigen Befehl handelt.
     */
    public static final String[] commands = {"approx", "clear", "def", "deffuncs", "defvars", "euler", "latex",
        "pi", "plot2d", "plot3d", "plotcurve", "plotpolar", "solve", "solveexact", "solvedgl", "tangent", "taylordgl", "undef", "undefall"};

    /**
     * Hier werden zusätzliche Hinweise/Meldungen/Warnungen etc. gespeichert,
     * die dem Benutzer nach Beenden der Befehlsausführung mitgeteilt werden.
     */
    private static String[] output = new String[0];

    /**
     * Diese Funktion wird zum Prüfen für die Vergabe neuer Funktionsnamen
     * benötigt. Sie prüft nach, ob name keine bereits definierte Funktion,
     * Operator oder Befehl ist. Ferner dürfen die Zeichen + - * / ^ nicht
     * enthalten sein.
     */
    private static boolean checkForbiddenNames(String name) {

        /**
         * Prüfen, ob nicht geschützte Funktionen (wie z.B. sin, tan etc.)
         * überschrieben werden.
         */
        for (String protected_function : Expression.functions) {
            if (protected_function.equals(name)) {
                return false;
            }
        }
        /**
         * Prüfen, ob nicht geschützte Operatoren (wie z.B. taylor, int etc.)
         * überschrieben werden.
         */
        for (String protected_operator : Expression.operators) {
            if (protected_operator.equals(name)) {
                return false;
            }
        }
        /**
         * Prüfen, ob nicht geschützte Befehle (wie z.B. approx etc.)
         * überschrieben werden.
         */
        for (String protected_command : commands) {
            if (protected_command.equals(name)) {
                return false;
            }
        }
        /**
         * Prüfen, ob name nicht + - * / oder ^ enthält.
         */
        if (name.contains("+") || name.contains("-") || name.contains("*")
                || name.contains("/") || name.contains("^") || name.contains("!")) {
            return false;
        }

        return true;

    }

    /**
     * Wichtig: Der String command und die Parameter params entahlten keine
     * Leerzeichen mehr. Diese wurden bereits im Vorfeld beseitigt.
     */
    public static Command getCommand(String command, String[] params) throws ExpressionException, EvaluationException {

        Command result = new Command();
        Object[] command_params;

        //APPROX
        /**
         * Struktur: approx(expr)
         */
        if (command.equals("approx")) {

            /**
             * Prüft, ob der Befehl genau einen Parameter besitzt.
             */
            if (params.length != 1) {
                throw new ExpressionException("Im Befehl 'approx' muss genau ein Parameter stehen, der einen gültigen Ausdruck darstellt.");
            }

            try {
                Expression.build(params[0], new HashSet());
            } catch (ExpressionException e) {
                throw new ExpressionException("Im Befehl 'approx' muss der Parameter ein gültiger Ausdruck sein. Gemeldeter Fehler: "
                        + e.getMessage());
            }

            command_params = new Object[1];
            command_params[0] = Expression.build(params[0], new HashSet());
            result.setName(command);
            result.setParams(command_params);
            return result;

        }

        //CLEAR
        /**
         * Struktur: clear()
         */
        if (command.equals("clear")) {

            /**
             * Prüft, ob der Befehl keine Parameter besitzt.
             */
            if (params.length > 0) {
                throw new ExpressionException("Im Befehl 'clear' dürfen keine Parameter stehen.");
            }

            command_params = new Object[0];
            result.setName(command);
            result.setParams(command_params);
            return result;

        }

        //DEFINE
        /**
         * Struktur: def(var = value) var = Variablenname reelle Zahl. ODER:
         * def(func(var_1, ..., var_k) = EXPRESSION) func = Funktionsname var_i:
         * Variablennamen EXPRESSION: Funktionsterm, durch den func definiert
         * wird.
         */
        if (command.equals("def")) {

            if (!params[0].contains("=")) {
                throw new ExpressionException("Im Befehl 'def' muss ein Gleichheitszeichen als Zuweisungsoperator vorhanden sein.");
            }

            String function_name_and_params = params[0].substring(0, params[0].indexOf("="));
            String function_term = params[0].substring(params[0].indexOf("=") + 1, params[0].length());

            /**
             * Falls der linke Teil eine Variable ist, dann ist es eine
             * Zuweisung, die dieser Variablen einen festen Wert zuweist.
             * Beispiel: def(x = 2) liefert: result.name = "def" result.params =
             * {"x"} result.left = 2 (als Expression)
             */
            if (Expression.isValidVariable(function_name_and_params) && !Expression.isPI(function_name_and_params)) {
                try {
                    Expression.build(function_term, new HashSet());
                } catch (ExpressionException e) {
                    throw new ExpressionException("Bei einer Variablenzuweisung muss der Veränderlichen ein reeller Wert zugewiesen werden.");
                }
                Expression preciseExpression = Expression.build(function_term, new HashSet());
                HashSet vars = new HashSet();
                preciseExpression.getContainedVars(vars);
                if (!vars.isEmpty()) {
                    throw new ExpressionException("Bei einer Variablenzuweisung muss der Veränderlichen ein reeller Wert zugewiesen werden.");
                }
                command_params = new Object[2];
                command_params[0] = Variable.create(function_name_and_params);
                command_params[1] = preciseExpression;
                result.setName(command);
                result.setParams(command_params);
                return result;
            }

            /**
             * Nun wird geprüft, ob es sich um eine Funktionsdeklaration
             * handelt. Zunächst wird versucht, den rechten Teilstring vom "="
             * in einen Ausdruck umzuwandeln.
             */
            try {
                Expression.build(function_term, new HashSet());
            } catch (ExpressionException e) {
                throw new ExpressionException("Ungültiger Ausdruck auf der rechten Seite. Gemeldeter Fehler: " + e.getMessage());
            }

            /**
             * Falls man hier ankommt, muss das obige try funktioniert haben.
             * Jetzt wird die rechte Seite gelesen (durch den die Funktion auf
             * der linken Seite von "=" definiert wird).
             */
            HashSet vars = new HashSet();
            Expression expr = Expression.build(function_term, new HashSet());

            /**
             * WICHTIG! Falls expr bereits vom Benutzer vordefinierte Funktionen
             * enthält (der Benutzer kann beispielsweise eine weitere Funktion
             * mit Hilfe bereits definierter Funktionen definieren), dann werden
             * hier alle neu definierten Funktionen durch vordefinierte
             * Funktionen ersetzt.
             */
            expr = expr.replaceOperatorsAndSelfDefinedFunctionsByPredefinedFunctions();

            expr.getContainedVars(vars);

            try {
                /**
                 * Funktionsname und Funktionsvariablen werden ermittelt.
                 */
                Expression.getOperatorAndArguments(function_name_and_params);
                Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);
            } catch (ExpressionException e) {
                throw new ExpressionException("Ungültige Variablen- oder Funktionsdefinition.");
            }

            /**
             * Funktionsnamen und Variablen auslesen.
             */
            String function_name = Expression.getOperatorAndArguments(function_name_and_params)[0];
            String[] function_vars = Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);

            /**
             * Nun wird geprüft, ob die einzelnen Parameter in der
             * Funktionsklammer gültige Variablen sind
             */
            for (int i = 0; i < function_vars.length; i++) {
                if (!Expression.isValidVariable(function_vars[i])) {
                    throw new ExpressionException("'" + function_vars[i] + "' ist keine gültige Veränderliche.");
                }
            }

            /**
             * Nun wird geprüft, ob die Variablen in function_vars auch alle
             * verschieden sind!
             */
            HashSet function_vars_as_hashset = new HashSet();
            for (int i = 0; i < function_vars.length; i++) {
                if (function_vars_as_hashset.contains(function_vars[i])) {
                    throw new ExpressionException("In der Funktionsdeklaration der Funktion " + function_name + " dürfen"
                            + " nicht mehrmals dieselben Veränderlichen vorkommen.");
                }
                function_vars_as_hashset.add(function_vars[i]);
            }

            /**
             * Hier wird den Variablen der Index "_ABSTRACT" angehängt. Dies
             * dient der Kennzeichnung, dass diese Variablen Platzhalter für
             * weitere Ausdrücke und keine echten Variablen sind. Solche
             * Variablen können niemals in einem geparsten Ausdruck vorkommen,
             * da der Parser Expression.build solche Variablen nicht akzeptiert.
             */
            for (int i = 0; i < function_vars.length; i++) {
                function_vars[i] = function_vars[i] + "_ABSTRACT";
            }

            /**
             * Prüfen, ob nicht geschützte Funktionen (wie z.B. sin, tan etc.)
             * überschrieben werden.
             */
            if (!checkForbiddenNames(function_name)) {
                throw new ExpressionException("Der Funktionsname '" + function_name + "' ist der Name einer geschützten Funktion, eines geschützten Operators "
                        + "oder eines geschützten Befehls. Bitte wählen Sie einen anderen Namen.");
            }

            /**
             * Prüfen, ob alle Variablen, die in expr auftreten, auch als
             * Funktionsparameter vorhanden sind. Sonst -> Fehler ausgeben.
             *
             * Zugleich: Im Ausdruck expr werden alle Variablen der Form var
             * durch Variablen der Form var_ABSTRACT ersetzt und alle Variablen
             * im HashSet vars ebenfalls.
             */
            List<String> function_vars_list = Arrays.asList(function_vars);
            Iterator iter = vars.iterator();
            String var;
            for (int i = 0; i < vars.size(); i++) {
                var = (String) iter.next();
                expr = expr.replaceVariable(var, Variable.create(var + "_ABSTRACT"));
                var = var + "_ABSTRACT";
                if (!function_vars_list.contains(var)) {
                    throw new ExpressionException("Auf der rechten Seite taucht eine Veränderliche auf, die nicht als Funktionsparameter vorkommt.");
                }
            }

            /**
             * result.params werden gesetzt.
             */
            command_params = new Object[2 + function_vars.length];
            command_params[0] = function_name;
            for (int i = 1; i <= function_vars.length; i++) {
                command_params[i] = Variable.create(function_vars[i - 1]);
            }
            command_params[1 + function_vars.length] = expr;

            /**
             * Für das obige Beispiel def(f(x, y) = x^2+y) gilt dann:
             * result.name = "def" result.params = {"f", "x_ABSTRACT",
             * "y_ABSTRACT"} result.left = x_ABSTRACT^2+y_ABSTRACT (als
             * Expression).
             */
            result.setName(command);
            result.setParams(command_params);
            return result;

        }

        //DEFINEDFUNCS
        /**
         * Struktur: defvars()
         */
        if (command.equals("deffuncs")) {

            /**
             * Prüft, ob der Befehl keine Parameter besitzt.
             */
            if (params.length > 0) {
                throw new ExpressionException("Im Befehl 'deffuncs' dürfen keine Parameter stehen.");
            }

            command_params = new Object[0];
            result.setName(command);
            result.setParams(command_params);
            return result;

        }

        //DEFINEDVARS
        /**
         * Struktur: defvars()
         */
        if (command.equals("defvars")) {

            /**
             * Prüft, ob der Befehl keine Parameter besitzt.
             */
            if (params.length > 0) {
                throw new ExpressionException("Im Befehl 'defvars' dürfen keine Parameter stehen.");
            }

            command_params = new Object[0];
            result.setName(command);
            result.setParams(command_params);
            return result;

        }

        //EULER
        /**
         * Struktur: euler(int) int: nichtnegative ganze Zahl; bestimmt die
         * Anzahl der Stellen, die von e ausgegeben werden sollen.
         */
        if (command.equals("euler")) {

            if (params.length != 1) {
                throw new ExpressionException("Im Befehl 'euler' muss genau ein Parameter stehen. Dieser muss eine nichtnegative ganze Zahl sein.");
            }

            try {
                command_params = new Object[1];
                command_params[0] = Integer.parseInt(params[0]);
                result.setName(command);
                result.setParams(command_params);
                return result;
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der Parameter im Befehl 'euler' muss eine nichtnegative ganze Zahl sein.");
            }

        }

        //LATEX
        /**
         * Struktur: latex(EXPRESSION) EXPRESSION: Ausdruck, welcher in einen
         * Latex-Code umgewandelt werden soll.
         */
        if (command.equals("latex")) {

            if (params.length != 1) {
                throw new ExpressionException("Im Befehl 'latex' muss genau ein Parameter stehen. Dieser muss ein gültiger Ausdruck sein.");
            }

            try {
                Expression expr = Expression.build(params[0], new HashSet());
                command_params = new Object[1];
                command_params[0] = expr;
                result.setName(command);
                result.setParams(command_params);
                return result;
            } catch (ExpressionException e) {
                throw new ExpressionException("Fehler im Parameter des Befehls 'latex': " + e.getMessage());
            }

        }

        //PI
        /**
         * Struktur: pi(int) int: nichtnegative ganze Zahl; bestimmt die Anzahl
         * der Stellen, die von pi ausgegeben werden sollen.
         */
        if (command.equals("pi")) {

            if (params.length != 1) {
                throw new ExpressionException("Im Befehl 'pi' muss genau ein Parameter stehen. Dieser muss eine nichtnegative ganze Zahl sein.");
            }

            try {
                command_params = new Object[1];
                command_params[0] = Integer.parseInt(params[0]);
                result.setName(command);
                result.setParams(command_params);
                return result;
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der Parameter im Befehl 'pi' muss eine nichtnegative ganze Zahl sein.");
            }

        }

        //PLOT2D
        /**
         * Struktur: PLOT(EXPRESSION_1(var), ..., EXPRESSION_n(var), value_1,
         * value_2) EXPRESSION_i(var): Ausdruck in einer Variablen. value_1 <
         * value_2: Grenzen des Zeichenbereichs ODER: PLOT(EXPRESSION_1(var1,
         * var2) = EXPRESSION_2(var1, var2), value_1, value_2, value_3, value_4)
         * (Plot der Lösungsmenge {EXPRESSION_1 = EXPRESSION_2}) EXPRESSION_1,
         * EXPRESSION_2: Ausdrücke in höchstens zwei Variablen. value_1 <
         * value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         * Variablen werden dabei alphabetisch geordnet.
         */
        if (command.equals("plot2d")) {
            if (params.length < 3) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'plot2d'");
            }

            HashSet vars = new HashSet();

            if (params.length != 5 || !params[0].contains("=")) {

                for (int i = 0; i < params.length - 2; i++) {
                    try {
                        Expression.build(params[i], new HashSet()).getContainedVars(vars);
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der " + (i + 1) + ". Parameter im Befehl 'plot2d' muss ein gültiger Ausdruck in einer Veränderlichen sein.");
                    }
                }

                if (vars.size() > 1) {
                    throw new ExpressionException("Die Ausdrücke im Befehl 'plot2d' dürfen höchstens eine Veränderliche enthalten. "
                            + "Diese enthalten jedoch " + vars.size() + " Veränderliche.");
                }

                HashSet vars_in_limits = new HashSet();
                try {
                    Expression.build(params[params.length - 2], new HashSet()).getContainedVars(vars_in_limits);
                    if (!vars_in_limits.isEmpty()) {
                        throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                }

                try {
                    Expression.build(params[params.length - 1], new HashSet()).getContainedVars(vars_in_limits);
                    if (!vars_in_limits.isEmpty()) {
                        throw new ExpressionException("Der " + params.length + ". Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException("Der " + params.length + ". Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                }

                Expression x_0 = Expression.build(params[params.length - 2], vars_in_limits);
                Expression x_1 = Expression.build(params[params.length - 1], vars_in_limits);
                if (x_0.evaluate() >= x_1.evaluate()) {
                    throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plot2d' muss größer sein als der "
                            + (params.length) + ". Parameter.");
                }

                command_params = new Object[params.length];
                for (int i = 0; i < params.length - 2; i++) {
                    command_params[i] = Expression.build(params[i], vars);
                }
                command_params[params.length - 2] = x_0;
                command_params[params.length - 1] = x_1;
                result.setName(command);
                result.setParams(command_params);
                return result;

            } else {

                if (params[0].contains("=")) {

                    if (params.length != 5) {
                        throw new ExpressionException("Beim Plotten impliziter Funktionen muss der Befehl 'plot2d' genau 5 Parameter enthalten: der erste ist die Gleichung, "
                                + "die anderen vier sind die Grenzen, innerhalb derer der Graph geplottet wird.");
                    }

                    try {
                        Expression.build(params[0].substring(0, params[0].indexOf("=")), new HashSet()).getContainedVars(vars);
                        Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), new HashSet()).getContainedVars(vars);
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der erste Parameter im Befehl 'plot2d' muss aus zwei gültigen Ausdrücken bestehen,"
                                + " welche durch ein '=' verbunden sind. Gemeldeter Fehler: " + e.getMessage());
                    }

                    if (vars.size() > 2) {
                        throw new ExpressionException("Die beiden Ausdrücke im Befehl 'plot2d' dürfen höchstens zwei Veränderliche enthalten. Diese enthalten jedoch "
                                + String.valueOf(vars.size()) + " Veränderliche.");
                    }

                    HashSet vars_in_limits = new HashSet();
                    try {
                        Expression.build(params[1], new HashSet()).getContainedVars(vars_in_limits);
                        if (!vars_in_limits.isEmpty()) {
                            throw new ExpressionException("Der zweite Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                        }
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der zweite Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                    }

                    try {
                        Expression.build(params[2], new HashSet()).getContainedVars(vars_in_limits);
                        if (!vars_in_limits.isEmpty()) {
                            throw new ExpressionException("Der dritte Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                        }
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                    }

                    try {
                        Expression.build(params[3], new HashSet()).getContainedVars(vars_in_limits);
                        if (!vars_in_limits.isEmpty()) {
                            throw new ExpressionException("Der vierte Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                        }
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der vierte Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                    }

                    try {
                        Expression.build(params[4], new HashSet()).getContainedVars(vars_in_limits);
                        if (!vars_in_limits.isEmpty()) {
                            throw new ExpressionException("Der fünfte Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                        }
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der fünfte Parameter im Befehl 'plot2d' muss eine reelle Zahl sein.");
                    }

                    Expression expr_left = Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
                    Expression expr_right = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
                    Expression x_0 = Expression.build(params[1], vars);
                    Expression x_1 = Expression.build(params[2], vars);
                    Expression y_0 = Expression.build(params[3], vars);
                    Expression y_1 = Expression.build(params[4], vars);
                    if (x_0.evaluate() >= x_1.evaluate()) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot2d' muss größer sein als der zweite Parameter.");
                    }
                    if (y_0.evaluate() >= y_1.evaluate()) {
                        throw new ExpressionException("Der fünfte Parameter im Befehl 'plot2d' muss größer sein als der vierte Parameter.");
                    }

                    command_params = new Object[6];
                    command_params[0] = expr_left;
                    command_params[1] = expr_right;
                    command_params[2] = x_0;
                    command_params[3] = x_1;
                    command_params[4] = y_0;
                    command_params[5] = y_1;
                    result.setName(command);
                    result.setParams(command_params);
                    return result;

                }

            }
        }

        //PLOT3D
        /**
         * Struktur: PLOT(EXPRESSION(var1, var2), value_1, value_2, value_3,
         * value_4) EXPRESSION: Ausdruck in höchstens zwei Variablen. value_1 <
         * value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         * Variablen werden dabei alphabetisch geordnet.
         */
        if (command.equals("plot3d")) {
            if (params.length < 5) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'plot3d'");
            } else if (params.length > 5) {
                throw new ExpressionException("Zu viele Parameter im Befehl 'plot3d'");
            }

            HashSet vars = new HashSet();

            try {
                Expression expr = Expression.build(params[0], new HashSet());
                expr.getContainedVars(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException("Der erste Parameter im Befehl 'plot3d' muss ein gültiger Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
            }

            if (vars.size() > 2) {
                throw new ExpressionException("Der Ausdruck im Befehl 'plot3d' darf höchstens zwei Veränderliche enthalten. Dieser enthält jedoch "
                        + String.valueOf(vars.size()) + " Veränderliche.");
            }

            HashSet vars_in_limits = new HashSet();
            try {
                Expression.build(params[1], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der zweite Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
            }

            try {
                Expression.build(params[2], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
            }

            try {
                Expression.build(params[3], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der vierte Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der vierte Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
            }

            try {
                Expression.build(params[4], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der fünfte Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der fünfte Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
            }

            Expression expr = Expression.build(params[0], vars);
            Expression x_0 = Expression.build(params[1], vars);
            Expression x_1 = Expression.build(params[2], vars);
            Expression y_0 = Expression.build(params[3], vars);
            Expression y_1 = Expression.build(params[4], vars);
            if (x_0.evaluate() >= x_1.evaluate()) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'plot3d' muss größer sein als der zweite Parameter.");
            }
            if (y_0.evaluate() >= y_1.evaluate()) {
                throw new ExpressionException("Der fünfte Parameter im Befehl 'plot3d' muss größer sein als der vierte Parameter.");
            }
            command_params = new Object[5];
            command_params[0] = expr;
            command_params[1] = x_0;
            command_params[2] = x_1;
            command_params[3] = y_0;
            command_params[4] = y_1;
            result.setName(command);
            result.setParams(command_params);
            return result;

        }

        //PLOTCURVE
        /**
         * Struktur: PLOTCURVE([FUNCTION_1(var), FUNCTION_2(var)], value_1,
         * value_2). FUNCTION_i(var) = Funktion in einer Variablen. value_1 <
         * value_2: Parametergrenzen. ODER: PLOTCURVE([FUNCTION_1(var),
         * FUNCTION_2(var), FUNCTION_3(var)], value_1, value_2). FUNCTION_i(var)
         * = Funktion in einer Variablen. value_1 < value_2: Parametergrenzen.
         */
        if (command.equals("plotcurve")) {
            if (params.length != 3) {
                throw new ExpressionException("Im Befehl 'plotcurve' müssen genau drei Parameter stehen.");
            }

            HashSet vars = new HashSet();

            /**
             * Es wird nun geprüft, ob der erste Parameter die Form "(expr_1,
             * expr_2)" oder "(expr_1, expr_2, expr_3)" besitzt.
             */
            if (!params[0].substring(0, 1).equals("(") || !params[0].substring(params[0].length() - 1, params[0].length()).equals(")")) {
                throw new ExpressionException("Der erste Parameter im Befehl 'plotcurve' muss eine parametrisierte Kurve sein.");
            }

            String[] curve_components = Expression.getArguments(params[0].substring(1, params[0].length() - 1));
            if (curve_components.length != 2 && curve_components.length != 3) {
                throw new ExpressionException("Die parametrisierte Kurve im Befehl 'plotcurve' muss entweder aus zwei oder aus drei Komponenten bestehen.");
            }

            for (int i = 0; i < curve_components.length; i++) {
                try {
                    Expression.build(curve_components[i], vars);
                } catch (ExpressionException e) {
                    throw new ExpressionException("Die " + (i + 1) + ". Kompomente in der Kurvendarstellung muss ein gültiger "
                            + "Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
                }
            }

            if (vars.size() > 1) {
                throw new ExpressionException("Die Kurve im Befehl 'plotcurve' darf durch höchstens einen Parameter parametrisiert werden.");
            }

            HashSet vars_in_limits = new HashSet();
            try {
                Expression.build(params[1], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plotcurve' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der zweite Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
            }

            try {
                Expression.build(params[2], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plotcurve' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'plot3d' muss eine reelle Zahl sein.");
            }

            if (curve_components.length == 2) {
                command_params = new Object[4];
                command_params[0] = Expression.build(curve_components[0], vars);
                command_params[1] = Expression.build(curve_components[1], vars);
                command_params[2] = Expression.build(params[1], vars);
                command_params[3] = Expression.build(params[2], vars);
            } else {
                command_params = new Object[5];
                command_params[0] = Expression.build(curve_components[0], vars);
                command_params[1] = Expression.build(curve_components[1], vars);
                command_params[2] = Expression.build(curve_components[2], vars);
                command_params[3] = Expression.build(params[1], vars);
                command_params[4] = Expression.build(params[2], vars);
            }

            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //PLOTPOLAR
        /**
         * Struktur: PLOT(EXPRESSION_1(var), ..., EXPRESSION_n(var), value_1,
         * value_2) EXPRESSION_i(var): Ausdruck in einer Variablen. value_1 <
         * value_2: Grenzen des Zeichenbereichs ODER: PLOT(EXPRESSION_1(var1,
         * var2) = EXPRESSION_2(var1, var2), value_1, value_2, value_3, value_4)
         * (Plot der Lösungsmenge {EXPRESSION_1 = EXPRESSION_2}) EXPRESSION_1,
         * EXPRESSION_2: Ausdrücke in höchstens zwei Variablen. value_1 <
         * value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         * Variablen werden dabei alphabetisch geordnet.
         */
        if (command.equals("plotpolar")) {
            if (params.length < 3) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'plotpolar'");
            }

            HashSet vars = new HashSet();

            for (int i = 0; i < params.length - 2; i++) {
                try {
                    Expression.build(params[i], new HashSet()).getContainedVars(vars);
                } catch (ExpressionException e) {
                    throw new ExpressionException("Der " + (i + 1) + ". Parameter im Befehl 'plotpolar' muss ein gültiger Ausdruck in einer Veränderlichen sein.");
                }
            }

            if (vars.size() > 1) {
                throw new ExpressionException("Die Ausdrücke im Befehl 'plotpolar' dürfen höchstens eine Veränderliche enthalten. "
                        + "Diese enthalten jedoch " + vars.size() + " Veränderliche.");
            }

            HashSet vars_in_limits = new HashSet();
            try {
                Expression.build(params[params.length - 2], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plotpolar' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plotpolar' muss eine reelle Zahl sein.");
            }

            try {
                Expression.build(params[params.length - 1], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException("Der " + params.length + ". Parameter im Befehl 'plotpolar' muss eine reelle Zahl sein.");
                }
            } catch (ExpressionException e) {
                throw new ExpressionException("Der " + params.length + ". Parameter im Befehl 'plotpolar' muss eine reelle Zahl sein.");
            }

            Expression x_0 = Expression.build(params[params.length - 2], vars_in_limits);
            Expression x_1 = Expression.build(params[params.length - 1], vars_in_limits);
            if (x_0.evaluate() >= x_1.evaluate()) {
                throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plotpolar' muss größer sein als der "
                        + (params.length) + ". Parameter.");
            }

            command_params = new Object[params.length];
            for (int i = 0; i < params.length - 2; i++) {
                command_params[i] = Expression.build(params[i], vars);
            }
            command_params[params.length - 2] = x_0;
            command_params[params.length - 1] = x_1;
            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //SOLVE
        /**
         * Struktur: solve(expr_1 = expr_2, x_1, x_2) ODER solve(expr_1 =
         * expr_2, x_1, x_2, n) var = Variable in der GLeichung, x_1 und x_2
         * legen den Lösungsbereich fest; n = Anzahl der Unterteilungen des
         * Intervalls [x_1, x_2]
         */
        if (command.equals("solve")) {
            if (params.length < 3) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'solve'");
            }
            if (params.length > 4) {
                throw new ExpressionException("Zu viele Parameter im Befehl 'solve'");
            }

            if (!params[0].contains("=")) {
                throw new ExpressionException("Der erste Parameter im Befehl 'solve' muss von der Form 'f(x) = g(x)' sein, "
                        + "wobei f und g Funktionen und x eine gültiger Veränderliche sind.");
            }
            HashSet vars = new HashSet();
            try {
                Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
                Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
            } catch (ExpressionException e) {
                throw new ExpressionException("Der erste Parameter im Befehl 'solve' muss zwei gültige Ausdrücke enthalten, "
                        + "welche durch ein '=' verbunden sind. Gemeldeter Fehler: " + e.getMessage());
            }

            Expression expr_1 = Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
            Expression expr_2 = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
            if (vars.size() > 1) {
                throw new ExpressionException("In der Gleichung darf höchstens eine Veränderliche auftreten.");
            }

            HashSet vars_in_limit = new HashSet();
            try {
                Expression expr = Expression.build(params[1], vars_in_limit);
                if (!vars_in_limit.isEmpty()) {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'solve' muss eine Konstante sein, deren Betrag höchstens 1.7E308 beträgt.");
                }
                expr.evaluate();
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException("Der zweite Parameter im Befehl 'solve' muss eine Konstante sein, deren Betrag höchstens 1.7E308 beträgt.");
            }

            try {
                Expression expr = Expression.build(params[2], vars_in_limit);
                if (!vars_in_limit.isEmpty()) {
                    throw new ExpressionException("Der dritte Parameter im Befehl 'solve' muss eine Konstante sein, deren Betrag höchstens 1.7E308 beträgt.");
                }
                expr.evaluate();
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'solve' muss eine Konstante sein, deren Betrag höchstens 1.7E308 beträgt.");
            }

            if (params.length == 4) {
                try {
                    Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Der vierte Parameter im Befehl 'solve' muss positive ganze Zahl sein.");
                }
            }

            Expression x_1 = Expression.build(params[1], vars);
            Expression x_2 = Expression.build(params[2], vars);

            if (params.length == 3) {
                command_params = new Object[4];
                command_params[0] = expr_1;
                command_params[1] = expr_2;
                command_params[2] = x_1;
                command_params[3] = x_2;
            } else {
                int n = Integer.parseInt(params[3]);
                if (n < 1) {
                    throw new ExpressionException("Der vierte Parameter im Befehl 'solve' muss positive ganze Zahl sein.");
                }
                command_params = new Object[5];
                command_params[0] = expr_1;
                command_params[1] = expr_2;
                command_params[2] = x_1;
                command_params[3] = x_2;
                command_params[4] = n;
            }

            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //SOLVEEXACT
        /**
         * Struktur: solveexact(expr_1 = expr_2).
         */
        if (command.equals("solveexact")) {
            if (params.length < 1) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'solveexact'");
            }
            if (params.length > 2) {
                throw new ExpressionException("Zu viele Parameter im Befehl 'solveexact'");
            }

            if (!params[0].contains("=")) {
                throw new ExpressionException("Der erste Parameter im Befehl 'solveexact' muss von der Form 'f(x) = g(x)' sein, "
                        + "wobei f und g Funktionen und x eine gültiger Veränderliche sind.");
            }
            HashSet vars = new HashSet();
            try {
                Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
                Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
            } catch (ExpressionException e) {
                throw new ExpressionException("Der erste Parameter im Befehl 'solveexact' muss zwei gültige Ausdrücke enthalten, "
                        + "welche durch ein '=' verbunden sind. Gemeldeter Fehler: " + e.getMessage());
            }

            Expression expr_1 = Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
            Expression expr_2 = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
            if (vars.size() > 1 && params.length == 1) {
                throw new ExpressionException("In der Gleichung darf höchstens eine Veränderliche auftreten. Falls mehrere Veränderliche auftreten, so geben "
                        + "Sie bitte die Veränderliche, nach der die Gleichung gelöst werden soll, als zweiten Parameter an.");
            }

            if (params.length == 2) {
                if (!Expression.isValidVariable(params[1])) {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'solveexact' muss eine gültige Veränderliche sein.");
                }
            }

            if (params.length == 1) {
                command_params = new Object[2];
                command_params[0] = expr_1;
                command_params[1] = expr_2;
            } else {
                command_params = new Object[3];
                command_params[0] = expr_1;
                command_params[1] = expr_2;
                command_params[2] = params[1];
            }

            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //SOLVEDGL
        /**
         * Struktur: solvedgl(EXPRESSION, var, ord, x_0, x_1, y_0, y'(0), ...,
         * y^(ord - 1)(0)) EXPRESSION: Rechte Seite der DGL y^{(ord)} =
         * EXPRESSION. Anzahl der parameter ist also = ord + 5 var = Variable in
         * der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         * fest x_1 = Obere x-Schranke für die numerische Berechnung
         */
        if (command.equals("solvedgl")) {
            if (params.length < 6) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'solvedgl'");
            }

            if (params.length >= 6) {

                //Ermittelt die Ordnung der DGL
                try {
                    Integer.parseInt(params[2]);
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Der dritte Parameter im Befehl 'solvedgl' muss eine positive ganze Zahl sein.");
                }

                int ord = Integer.parseInt(params[2]);

                if (ord < 1) {
                    throw new ExpressionException("Der dritte Parameter im Befehl 'solvedgl' muss eine positive ganze Zahl sein.");
                }

                /**
                 * Prüft, ob es sich um eine korrekte DGL handelt:
                 * Beispielsweise darf in einer DGL der ordnung 3 nicht y''',
                 * y'''' etc. auf der rechten Seite auftreten.
                 */
                HashSet vars = new HashSet();
                try {
                    Expression.build(params[0], vars);
                } catch (ExpressionException e) {
                    throw new ExpressionException("Der erste Parameter im Befehl 'solvedgl' muss ein gültiger Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
                }
                Expression expr = Expression.build(params[0], vars);

                HashSet vars_without_primes = new HashSet();
                Iterator iter = vars.iterator();
                String var_without_primes;
                for (int i = 0; i < vars.size(); i++) {
                    var_without_primes = (String) iter.next();
                    if (!var_without_primes.replaceAll("'", "").equals(params[1])) {
                        if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord) {
                            throw new ExpressionException("Die Differentialgleichung besitzt die Ordnung " + ord + ". "
                                    + "Es dürfen daher nur Ableitungen höchstens " + (ord - 1) + ". Ordnung auftreten.");
                        }
                        var_without_primes = var_without_primes.replaceAll("'", "");
                    }
                    vars_without_primes.add(var_without_primes);
                }

                if (vars_without_primes.size() > 2) {
                    throw new ExpressionException("In der Differentialgleichung dürfen höchstens zwei Veränderliche auftreten.");
                }

                if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
                    if (vars_without_primes.size() == 2) {
                        if (!vars.contains(params[1])) {
                            throw new ExpressionException("Die Veränderliche " + params[1] + " muss in der Differentialgleichung vorkommen.");
                        }
                    }
                } else {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'solvedgl' muss eine gültige Veränderliche sein.");
                }

                if (params.length < ord + 5) {
                    throw new ExpressionException("Zu wenig Parameter im Befehl 'solvedgl'");
                }
                if (params.length > ord + 5) {
                    throw new ExpressionException("Zu viele Parameter im Befehl 'solvedgl'");
                }

                //Prüft, ob die AWP-Daten korrekt sind
                HashSet vars_in_limits = new HashSet();
                for (int i = 3; i < ord + 5; i++) {
                    try {
                        Expression limit = Expression.build(params[i], vars_in_limits);
                        if (!vars_in_limits.isEmpty()) {
                            throw new ExpressionException("Der " + String.valueOf(i + 1) + ". Parameter im Befehl 'solvedgl' muss eine reelle Zahl sein.");
                        }
                        limit.evaluate();
                    } catch (ExpressionException | EvaluationException e) {
                        throw new ExpressionException("Der " + String.valueOf(i + 1) + ". Parameter im Befehl 'solvedgl' muss eine reelle Zahl sein.");
                    }
                }

                command_params = new Object[ord + 5];
                command_params[0] = expr;
                command_params[1] = params[1];
                command_params[2] = ord;
                for (int i = 3; i < ord + 5; i++) {
                    command_params[i] = Expression.build(params[i], vars);
                }

                result.setName(command);
                result.setParams(command_params);
                return result;

            }
        }

        //TANGENT
        /**
         * Struktur: tangent(EXPRESSION, var_1 = value_1, ..., var_n = value_n)
         * EXPRESSION: Ausdruck, welcher eine Funktion repräsentiert. var_i =
         * Variable value_i = reelle Zahl. Es müssen alle Variablen unter den
         * var_i vorkommen, welche auch in expr vorkommen.
         */
        if (command.equals("tangent")) {
            if (params.length < 2) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'tangent'");
            }

            try {
                Expression.build(params[0], new HashSet());
            } catch (ExpressionException e) {
                throw new ExpressionException("Der erste Parameter im Befehl 'tangent' muss ein gültiger Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
            }

            HashSet vars = new HashSet();
            Expression expr = Expression.build(params[0], vars);

            /**
             * Ermittelt die Anzahl der einzugebenen Parameter.
             */
            HashMap<String, Expression> vars_contained_in_params = new HashMap<>();
            for (int i = 1; i < params.length; i++) {
                if (!params[i].contains("=")) {
                    throw new ExpressionException("Der " + (i + 1) + ". Parameter muss von der Form 'VARIABLE = WERT' sein, "
                            + "wobei die Veränderliche VARIABLE im Ausdruck vorkommen muss.");
                }
                if (!Expression.isValidVariable(params[i].substring(0, params[i].indexOf("=")))) {
                    throw new ExpressionException(params[i].substring(0, params[i].indexOf("=")) + " ist keine gültige Veränderliche.");
                }
                try {
                    Expression point = Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet());
                    if (!point.isConstant()) {
                        throw new ExpressionException("Der Veränderlichen im " + (i + 1) + ". Parameter im Befehl 'tangent' muss eine reelle Zahl zugewiesen werden.");
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException("Der Veränderlichen im " + (i + 1) + ". Parameter im Befehl 'tangent' muss eine reelle Zahl zugewiesen werden.");
                }
            }

            for (int i = 1; i < params.length; i++) {
                vars_contained_in_params.put(params[i].substring(0, params[i].indexOf("=")),
                        Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet()));
            }

            Iterator iter = vars.iterator();
            String var;
            for (int i = 0; i < vars.size(); i++) {
                var = (String) iter.next();
                if (!vars_contained_in_params.containsKey(var)) {
                    throw new ExpressionException("Die Veränderliche " + var + " muss in den Parametern vorkommen.");
                }
            }

            command_params = new Object[2];
            command_params[0] = expr;
            command_params[1] = vars_contained_in_params;

            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //TAYLORDGL
        /**
         * Struktur: taylordgl(EXPRESSION, var, ord, x_0, y_0, y'(0), ...,
         * y^(ord - 1)(0), k) EXPRESSION: Rechte Seite der DGL y^{(ord)} =
         * EXPRESSION. Anzahl der parameter ist also = ord + 5 var = Variable in
         * der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         * fest k = Ordnung des Taylorpolynoms (an der Stelle x_0)
         */
        if (command.equals("taylordgl")) {
            if (params.length < 6) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'taylordgl'");
            }

            //Ermittelt die Ordnung der DGL
            try {
                Integer.parseInt(params[2]);
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'taylordgl' muss eine positive ganze Zahl sein.");
            }

            int ord = Integer.parseInt(params[2]);

            if (ord < 1) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'taylordgl' muss eine positive ganze Zahl sein.");
            }

            /**
             * Prüft, ob es sich um eine korrekte DGL handelt: Beispielsweise
             * darf in einer DGL der ordnung 3 nicht y''', y'''' etc. auf der
             * rechten Seite auftreten.
             */
            HashSet vars = new HashSet();
            try {
                Expression.build(params[0], vars);
            } catch (ExpressionException e) {
                throw new ExpressionException("Der erste Parameter im Befehl 'taylordgl' muss ein gültiger Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
            }
            Expression expr = Expression.build(params[0], vars);

            HashSet vars_without_primes = new HashSet();
            Iterator iter = vars.iterator();
            String var_without_primes;
            for (int i = 0; i < vars.size(); i++) {
                var_without_primes = (String) iter.next();
                if (!var_without_primes.replaceAll("'", "").equals(params[1])) {
                    if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord) {
                        throw new ExpressionException("Die Differentialgleichung besitzt die Ordnung " + ord + ". "
                                + "Es dürfen daher nur Ableitungen höchstens " + (ord - 1) + ". Ordnung auftreten.");
                    }
                    var_without_primes = var_without_primes.replaceAll("'", "");
                }
                vars_without_primes.add(var_without_primes);
            }

            if (vars_without_primes.size() > 2) {
                throw new ExpressionException("In der Differentialgleichung dürfen höchstens zwei Veränderliche auftreten.");
            }

            if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
                if (vars_without_primes.size() == 2) {
                    if (!vars.contains(params[1])) {
                        throw new ExpressionException("Die Veränderliche " + params[1] + " muss in der Differentialgleichung vorkommen.");
                    }
                }
            } else {
                throw new ExpressionException("Der zweite Parameter im Befehl 'taylordgl' muss eine gültige Veränderliche sein.");
            }

            if (params.length < ord + 5) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'taylordgl'");
            }

            if (params.length > ord + 5) {
                throw new ExpressionException("Zu viele Parameter im Befehl 'taylordgl'");
            }

            //Prüft, ob die AWP-Daten korrekt sind
            HashSet vars_in_limits = new HashSet();
            for (int i = 3; i < ord + 4; i++) {
                try {
                    Expression limit = Expression.build(params[i], vars_in_limits).simplify();
                    if (!vars_in_limits.isEmpty()) {
                        throw new ExpressionException("Der " + String.valueOf(i + 1) + ". Parameter im Befehl 'taylordgl' muss eine reelle Zahl sein.");
                    }
                } catch (ExpressionException | EvaluationException e) {
                    throw new ExpressionException("Der " + String.valueOf(i + 1) + ". Parameter im Befehl 'taylordgl' muss eine reelle Zahl sein.");
                }
            }

            try {
                Integer.parseInt(params[ord + 4]);
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der letzte Parameter im Befehl 'taylordgl' muss eine nichtnegative ganze Zahl sein.");
            }

            command_params = new Object[ord + 5];
            command_params[0] = expr;
            command_params[1] = params[1];
            command_params[2] = ord;
            for (int i = 3; i < ord + 4; i++) {
                command_params[i] = Expression.build(params[i], vars);
            }
            command_params[ord + 4] = Integer.parseInt(params[ord + 4]);

            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //UNDEFINE
        /**
         * Struktur: undef(var_1, ..., var_k) var_i: Variablenname
         */
        if (command.equals("undef")) {
            /**
             * Prüft, ob alle Parameter gültige Variablen sind.
             */
            for (int i = 0; i < params.length; i++) {
                if (!Expression.isValidVariable(params[i]) && !Expression.isPI(params[i])) {
                    throw new ExpressionException("Der " + (i + 1) + ". Parameter im Befehl 'undef' ist keine gültige Veränderliche.");
                }
            }

            command_params = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                command_params[i] = Variable.create(params[i]);
            }

            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //UNDEFINEALL
        /**
         * Struktur: undefall()
         */
        if (command.equals("undefall")) {
            /**
             * Prüft, ob der Befehl keine Parameter besitzt.
             */
            if (params.length > 0) {
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
            GraphicMethods3D graphicMethods3D, GraphicMethodsCurves2D graphicMethodsCurves2D, GraphicMethodsCurves3D graphicMethodsCurves3D,
            GraphicMethodsPolar2D graphicMethodsPolar2D, HashMap<String, Expression> definedVars, HashSet definedVarsSet,
            HashMap<String, Expression> definedFunctions) throws ExpressionException, EvaluationException {

        output = new String[0];
        int n = commandLine.length();

        //Leerzeichen beseitigen und alles zu Kleinbuchstaben machen
        commandLine = commandLine.replaceAll(" ", "");

        //Falls Großbuchstaben auftreten -> zu Kleinbuchstaben machen
        char part;
        for (int i = 0; i < n; i++) {
            part = commandLine.charAt(i);
            if (((int) part >= 65) && ((int) part <= 90)) {
                part = (char) ((int) part + 32);  //Macht Großbuchstaben zu Kleinbuchstaben
                commandLine = commandLine.substring(0, i) + part + commandLine.substring(i + 1, n);
            }
        }

        String[] command_and_params = Expression.getOperatorAndArguments(commandLine);
        String command = command_and_params[0];
        String[] params = Expression.getArguments(command_and_params[1]);

        Command c = getCommand(command, params);

        if (c.getName().equals("approx")) {
            executeApprox(c, area, definedVars);
        } else if (c.getName().equals("clear")) {
            executeClear(c, area);
        } else if ((c.getName().equals("def")) && c.getParams().length >= 1) {
            executeDefine(c, area, definedVars, definedVarsSet, definedFunctions);
        } else if (c.getName().equals("deffuncs")) {
            executeDefFuncs(c, area, definedFunctions);
        } else if (c.getName().equals("defvars")) {
            executeDefVars(c, area, definedVars);
        } else if (c.getName().equals("euler")) {
            executeEuler(c, area);
        } else if (c.getName().equals("latex")) {
            executeLatex(c, area);
        } else if (c.getName().equals("pi")) {
            executePi(c, area);
        } else if (c.getName().equals("plot2d")) {
            if (params[0].contains("=")) {
                executeImplicitPlot2D(c, graphicMethods2D);
            } else {
                executePlot2D(c, graphicMethods2D);
            }
        } else if (c.getName().equals("plot3d")) {
            executePlot3D(c, graphicMethods3D);
        } else if (c.getName().equals("plotcurve") && c.getParams().length == 4) {
            executePlotCurve2D(c, graphicMethodsCurves2D);
        } else if (c.getName().equals("plotcurve") && c.getParams().length == 5) {
            executePlotCurve3D(c, graphicMethodsCurves3D);
        } else if (c.getName().equals("plotpolar")) {
            executePlotPolar2D(c, graphicMethodsPolar2D);
        } else if (c.getName().equals("solve")) {
            executeSolve(c, area, graphicMethods2D);
        } else if (c.getName().equals("solveexact")) {
            executeSolveExact(c, area);
        } else if (c.getName().equals("solvedgl")) {
            executeSolveDGL(c, area, graphicMethods2D);
        } else if (c.getName().equals("tangent")) {
            executeTangent(c, area, graphicMethods2D);
        } else if (c.getName().equals("taylordgl")) {
            executeTaylorDGL(c, area);
        } else if (c.getName().equals("undef")) {
            executeUndefine(c, area, definedVars, definedVarsSet);
        } else if (c.getName().equals("undefall")) {
            executeUndefineAll(c, area, definedVars, definedVarsSet);
        } else {
            throw new ExpressionException("Ungültiger Befehl.");
        }

        /**
         * Zusätzliche Hinweise/Meldungen ausgeben.
         */
        if (output.length > 0) {
            for (int i = 0; i < output.length; i++) {
                area.append(output[i]);
            }
        }

    }

    /**
     * Die folgenden Prozeduren führen einzelne Befehle aus. executePlot2D
     * zeichnet einen 2D-Graphen, executePlot3D zeichnet einen 3D-Graphen, etc.
     */
    private static void executeApprox(Command c, JTextArea area, HashMap<String, Expression> definedVars)
            throws ExpressionException, EvaluationException {

        Expression expr = (Expression) c.getParams()[0];

        /**
         * Falls expr selbstdefinierte Funktionen enthält, dann zunächst expr so
         * darstellen, dass es nur vordefinierte Funktionen beinhaltet.
         */
        expr = expr.replaceOperatorsAndSelfDefinedFunctionsByPredefinedFunctions();
        /**
         * Mit Werten belegte Variablen müssen durch ihren exakten Ausdruck
         * ersetzt werden.
         */
        for (String var : definedVars.keySet()) {
            expr = expr.replaceVariable(var, (Expression) definedVars.get(var));
        }

        expr = expr.turnToIrrationals().simplify();
        area.append(expr.writeFormula(true) + "\n \n");

        /**
         * Dies dient dazu, dass alle Variablen wieder "präzise" sind. Sie
         * werden nur dann approximativ ausgegeben, wenn sie nicht präzise
         * (precise = false) sind.
         */
        Variable.setAllPrecise(true);

    }

    private static void executeClear(Command c, JTextArea area)
            throws ExpressionException {
        area.setText("");
    }

    private static void executeDefine(Command c, JTextArea area, HashMap<String, Expression> definedVars, HashSet definedVarsSet,
            HashMap<String, Expression> definedFunctions) throws ExpressionException, EvaluationException {

        /**
         * Falls ein Variablenwert definiert wird.
         */
        if (c.getParams().length == 2) {
            String var = ((Variable) c.getParams()[0]).getName();
            Expression preciseExpression = ((Expression) c.getParams()[1]).simplify();
            Variable.setPreciseExpression(var, preciseExpression);
            definedVars.put(var, preciseExpression);
            definedVarsSet.add(var);
            output = new String[1];
            if (((Expression) c.getParams()[1]).equals(preciseExpression)) {
                output[0] = "Der Wert der Veränderlichen " + var + " wurde auf " + preciseExpression.writeFormula(true) + " gesetzt. \n \n";
            } else {
                output[0] = "Der Wert der Veränderlichen " + var + " wurde auf " + ((Expression) c.getParams()[1]).writeFormula(true) + " = " + preciseExpression.writeFormula(true) + " gesetzt. \n \n";
            }
        } else {
            /**
             * Falls eine Funktion definiert wird.
             */
            String function_name = (String) (c.getParams()[0]);
            String[] vars = new String[c.getParams().length - 2];
            Expression[] exprs_for_vars = new Expression[c.getParams().length - 2];
            for (int i = 0; i < c.getParams().length - 2; i++) {
                vars[i] = ((Variable) c.getParams()[i + 1]).getName();
                exprs_for_vars[i] = (Variable) c.getParams()[i + 1];
            }
            SelfDefinedFunction.abstractExpressionsForSelfDefinedFunctions.put(function_name, (Expression) c.getParams()[c.getParams().length - 1]);
            SelfDefinedFunction.innerExpressionsForSelfDefinedFunctions.put(function_name, exprs_for_vars);
            SelfDefinedFunction.varsForSelfDefinedFunctions.put(function_name, vars);
            definedFunctions.put(function_name, new SelfDefinedFunction(function_name, vars, (Expression) c.getParams()[c.getParams().length - 1], exprs_for_vars));

            /**
             * Ausgabe an den Benutzer.
             */
            String function = "";
            SelfDefinedFunction f;
            Expression[] vars_for_output;
            Expression f_for_output;

            f = (SelfDefinedFunction) definedFunctions.get(function_name);
            function = f.getName() + "(";
            for (int i = 0; i < f.getArguments().length; i++) {
                function = function + "X_" + (i + 1) + ",";
            }
            function = function.substring(0, function.length() - 1) + ") = ";

            vars_for_output = new Expression[f.getArguments().length];
            for (int i = 0; i < f.getArguments().length; i++) {
                vars_for_output[i] = Variable.create("X_" + (i + 1));
            }
            f_for_output = f.replaceAllVariables(vars_for_output);
            function = function + f_for_output.writeFormula(true);

            output = new String[1];
            output[0] = "Es wurde die folgende Funktion definiert: " + function + "\n \n";

        }

    }

    private static void executeDefFuncs(Command c, JTextArea area, HashMap<String, Expression> definedFunctions)
            throws ExpressionException, EvaluationException {

        String function = "";
        SelfDefinedFunction f;
        Expression[] vars_for_output;
        Expression f_for_output;
        if (!definedFunctions.isEmpty()) {
            for (String function_name : definedFunctions.keySet()) {

                f = (SelfDefinedFunction) definedFunctions.get(function_name);
                function = f.getName() + "(";
                for (int i = 0; i < f.getArguments().length; i++) {
                    function = function + "X_" + (i + 1) + ",";
                }
                function = function.substring(0, function.length() - 1) + ") = ";

                vars_for_output = new Expression[f.getArguments().length];
                for (int i = 0; i < f.getArguments().length; i++) {
                    vars_for_output[i] = Variable.create("X_" + (i + 1));
                }
                f_for_output = f.replaceAllVariables(vars_for_output);
                function = function + f_for_output.writeFormula(true);
                area.append(function + "\n \n");

            }
        }
        output = new String[1];
        output[0] = "Liste aller selbstdefinierten Funktionen: " + function + "\n \n";

    }

    private static void executeDefVars(Command c, JTextArea area, HashMap<String, Expression> definedVars)
            throws ExpressionException, EvaluationException {

        String result = "";
        if (!definedVars.isEmpty()) {
            for (String var : definedVars.keySet()) {
                result += var + " = " + ((Expression) definedVars.get(var)).writeFormula(true) + ", ";
            }
            result = result.substring(0, result.length() - 2);
        }
        output = new String[1];
        output[0] = "Liste aller Veränderlichen mit vordefinierten Werten: " + result + "\n \n";

    }

    private static void executeEuler(Command c, JTextArea area) throws ExpressionException {
        BigDecimal e = AnalysisMethods.e((int) c.getParams()[0]);
        output = new String[1];
        output[0] = "Eulersche Zahl e auf " + (int) c.getParams()[0] + " Stellen gerundet: " + e.toString() + "\n \n";
    }

    private static void executeLatex(Command c, JTextArea area) throws ExpressionException {
        output = new String[1];
        output[0] = "Latex-Code: " + ((Expression) c.getParams()[0]).expressionToLatex(true) + "\n \n";
    }

    private static void executePi(Command c, JTextArea area) throws ExpressionException {
        BigDecimal pi = AnalysisMethods.pi((int) c.getParams()[0]);
        output = new String[1];
        output[0] = "Kreiszahl pi auf " + (int) c.getParams()[0] + " Stellen gerundet: " + pi.toString() + "\n \n";
    }

    private static void executePlot2D(Command c, GraphicMethods2D graphicMethods2D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression[] exprs = new Expression[c.getParams().length - 2];
        for (int i = 0; i < c.getParams().length - 2; i++) {
            exprs[i] = (Expression) c.getParams()[i];
            exprs[i].getContainedVars(vars);
        }

        //Falls der Ausdruck expr konstant ist, soll die Achse die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add("x");
        }

        Expression x_0 = (Expression) c.getParams()[c.getParams().length - 2];
        Expression x_1 = (Expression) c.getParams()[c.getParams().length - 1];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(true);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        for (int i = 0; i < c.getParams().length - 2; i++) {
            graphicMethods2D.addExpression(exprs[i]);
        }
        graphicMethods2D.setVar(var);
        graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate());
        graphicMethods2D.expressionToGraph(var, x_0.evaluate(), x_1.evaluate());
        graphicMethods2D.setDrawSpecialPoints(false);
        graphicMethods2D.drawGraph2D();

    }

    private static void executePlot3D(Command c, GraphicMethods3D graphicMethods3D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr.getContainedVars(vars);

        //Falls der Ausdruck expr konstant ist, sollen die Achsen die Bezeichnungen "x" und "y" tragen.
        if (expr.isConstant()) {
            vars.add("x");
            vars.add("y");
        }

        /**
         * Falls der Ausdruck expr nur von einer Variablen abhängt, sollen die
         * andere Achse eine fest gewählte Bezeichnung tragen. Dies wirdim
         * Folgenden geregelt.
         */
        if (vars.size() == 1) {
            if (vars.contains("y")) {
                vars.add("z");
            } else {
                vars.add("y");
            }
        }

        Expression x_0 = (Expression) c.getParams()[1];
        Expression x_1 = (Expression) c.getParams()[2];
        Expression y_0 = (Expression) c.getParams()[3];
        Expression y_1 = (Expression) c.getParams()[4];

        Iterator iter = vars.iterator();
        String var_1 = (String) iter.next();
        String var_2 = (String) iter.next();

        /**
         * Die Variablen var1 und var2 sind evtl. noch nicht in alphabetischer
         * Reihenfolge. Dies wird hier nachgeholt. GRUND: Der Zeichenbereich
         * wird durch vier Zahlen eingegrenzt, welche den Variablen in
         * ALPHABETISCHER Reihenfolge entsprechen.
         */
        String var1_alphabetical = var_1;
        String var2_alphabetical = var_2;

        if ((int) var_1.charAt(0) > (int) var_2.charAt(0)) {
            var1_alphabetical = var_2;
            var2_alphabetical = var_1;
        }
        if ((int) var_1.charAt(0) == (int) var_2.charAt(0)) {
            if ((var_1.length() > 1) && (var_2.length() == 1)) {
                var1_alphabetical = var_2;
                var2_alphabetical = var_1;
            }
            if ((var_1.length() > 1) && (var_2.length() > 1)) {
                int index_var1 = Integer.parseInt(var_1.substring(2));
                int index_var2 = Integer.parseInt(var_2.substring(2));
                if (index_var1 > index_var2) {
                    var1_alphabetical = var_2;
                    var2_alphabetical = var_1;
                }
            }
        }

        graphicMethods3D.setExpression(expr);
        graphicMethods3D.setParameters(var1_alphabetical, var2_alphabetical, 150, 200, 30, 30);
        graphicMethods3D.expressionToGraph(x_0.evaluate(), x_1.evaluate(), y_0.evaluate(), y_1.evaluate());
        graphicMethods3D.drawGraph3D();

    }

    private static void executeImplicitPlot2D(Command c, GraphicMethods2D graphicMethods2D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression expr = new BinaryOperation((Expression) c.getParams()[0], (Expression) c.getParams()[1],
                TypeBinary.MINUS).simplify();
        expr.getContainedVars(vars);

        //Falls der Ausdruck expr konstant ist, sollen die Achsen die Bezeichnungen "x" und "y" tragen.
        if (expr instanceof Constant) {
            vars.add("x");
            vars.add("y");
        }

        if (vars.size() == 1) {
            if (vars.contains("y")) {
                vars.add("z");
            } else {
                vars.add("y");
            }
        }

        Expression x_0 = (Expression) c.getParams()[2];
        Expression x_1 = (Expression) c.getParams()[3];
        Expression y_0 = (Expression) c.getParams()[4];
        Expression y_1 = (Expression) c.getParams()[5];

        Iterator iter = vars.iterator();
        String var_1 = (String) iter.next();
        String var_2 = (String) iter.next();

        String var1_alphabetical = var_1;
        String var2_alphabetical = var_2;

        if ((int) var_1.charAt(0) > (int) var_2.charAt(0)) {
            var1_alphabetical = var_2;
            var2_alphabetical = var_1;
        }
        if ((int) var_1.charAt(0) == (int) var_2.charAt(0)) {
            if ((var_1.length() > 1) && (var_2.length() == 1)) {
                var1_alphabetical = var_2;
                var2_alphabetical = var_1;
            }
            if ((var_1.length() > 1) && (var_2.length() > 1)) {
                int index_var1 = Integer.parseInt(var_1.substring(2));
                int index_var2 = Integer.parseInt(var_2.substring(2));
                if (index_var1 > index_var2) {
                    var1_alphabetical = var_2;
                    var2_alphabetical = var_1;
                }
            }
        }

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(false);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addExpression(expr);
        graphicMethods2D.setVars(var1_alphabetical, var2_alphabetical);
        graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate(), y_0.evaluate(), y_1.evaluate());
        graphicMethods2D.setDrawSpecialPoints(false);
        HashMap<Integer, double[]> implicit_graph = NumericalMethods.solveImplicitEquation(expr, var1_alphabetical, var2_alphabetical,
                x_0.evaluate(), x_1.evaluate(), y_0.evaluate(), y_1.evaluate());
        graphicMethods2D.setImplicitGraph(implicit_graph);
        graphicMethods2D.drawGraph2D();

    }

    private static void executePlotCurve2D(Command c, GraphicMethodsCurves2D graphicMethodsCurves2D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression[] expr = new Expression[2];
        expr[0] = (Expression) c.getParams()[0];
        expr[0].getContainedVars(vars);
        expr[1] = (Expression) c.getParams()[1];
        expr[1].getContainedVars(vars);

        //Falls der Ausdruck expr konstant ist, soll der Parameter die Bezeichnung "t" tragen.
        if (vars.isEmpty()) {
            vars.add("t");
        }

        Expression t_0 = (Expression) c.getParams()[2];
        Expression t_1 = (Expression) c.getParams()[3];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethodsCurves2D.setIsInitialized(true);
        graphicMethodsCurves2D.setExpression(expr);
        graphicMethodsCurves2D.setVar(var);
        graphicMethodsCurves2D.computeScreenSizes(t_0.evaluate(), t_1.evaluate());
        graphicMethodsCurves2D.expressionToGraph(t_0.evaluate(), t_1.evaluate());
        graphicMethodsCurves2D.drawCurve2D();

    }

    private static void executePlotCurve3D(Command c, GraphicMethodsCurves3D graphicMethodsCurves3D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression[] expr = new Expression[3];
        expr[0] = (Expression) c.getParams()[0];
        expr[0].getContainedVars(vars);
        expr[1] = (Expression) c.getParams()[1];
        expr[1].getContainedVars(vars);
        expr[2] = (Expression) c.getParams()[2];
        expr[2].getContainedVars(vars);

        //Falls der Ausdruck expr konstant ist, soll der Parameter die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add("t");
        }

        Expression t_0 = (Expression) c.getParams()[3];
        Expression t_1 = (Expression) c.getParams()[4];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethodsCurves3D.setIsInitialized(true);
        graphicMethodsCurves3D.setExpression(expr);
        graphicMethodsCurves3D.setVar(var);
        graphicMethodsCurves3D.setParameters(150, 200, 30, 30);
        graphicMethodsCurves3D.computeScreenSizes(t_0.evaluate(), t_1.evaluate());
        graphicMethodsCurves3D.expressionToGraph(t_0.evaluate(), t_1.evaluate());
        graphicMethodsCurves3D.drawCurve3D();

    }

    private static void executePlotPolar2D(Command c, GraphicMethodsPolar2D graphicMethodsPolar2D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression[] exprs = new Expression[c.getParams().length - 2];
        for (int i = 0; i < c.getParams().length - 2; i++) {
            exprs[i] = (Expression) c.getParams()[i];
            exprs[i].getContainedVars(vars);
        }

        //Falls der Ausdruck expr konstant ist, soll die Achse die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add("x");
        }

        Expression phi_0 = (Expression) c.getParams()[c.getParams().length - 2];
        Expression phi_1 = (Expression) c.getParams()[c.getParams().length - 1];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethodsPolar2D.setIsInitialized(true);
        graphicMethodsPolar2D.clearExpressionAndGraph();
        for (int i = 0; i < c.getParams().length - 2; i++) {
            graphicMethodsPolar2D.addExpression(exprs[i]);
        }
        graphicMethodsPolar2D.setVar(var);
        graphicMethodsPolar2D.computeScreenSizes(phi_0.evaluate(), phi_1.evaluate());
        graphicMethodsPolar2D.expressionToGraph(var, phi_0.evaluate(), phi_1.evaluate());
        graphicMethodsPolar2D.drawPolarGraph2D();

    }

    private static void executeSolve(Command c, JTextArea area, GraphicMethods2D graphicMethods2D)
            throws ExpressionException, EvaluationException {

        HashSet vars = new HashSet();
        Expression expr_1 = ((Expression) c.getParams()[0]).simplify();
        Expression expr_2 = ((Expression) c.getParams()[1]).simplify();
        Expression expr = expr_1.sub(expr_2).simplify();
        expr.getContainedVars(vars);
        //Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
        String var = "x";
        if (!vars.isEmpty()) {
            Iterator iter = vars.iterator();
            var = (String) iter.next();
        }

        Expression x_0 = (Expression) c.getParams()[2];
        Expression x_1 = (Expression) c.getParams()[3];
        /**
         * Falls die Anzahl der Unterteilungen nicht angegeben wird, so soll das
         * Intervall in 1000000 Teile unterteilt werden.
         */
        int n = 1000000;

        if (c.getParams().length == 5) {
            n = (int) c.getParams()[4];
        }

        if (expr instanceof Constant) {
            output = new String[2];
            output[0] = "Lösungen der Gleichung " + ((Expression) c.getParams()[0]).writeFormula(true)
                    + " = " + ((Expression) c.getParams()[1]).writeFormula(true) + ": \n \n";
            if (((Constant) expr).getPreciseValue().compareTo(BigDecimal.ZERO) == 0) {
                output[1] = "Alle reellen Zahlen. \n \n";
            } else {
                output[1] = "Die gegebene Gleichung besitzt keine Lösungen. \n \n";
            }

            graphicMethods2D.setIsInitialized(true);
            graphicMethods2D.setGraphIsExplicit(true);
            graphicMethods2D.setGraphIsFixed(false);
            graphicMethods2D.clearExpressionAndGraph();
            graphicMethods2D.addExpression(expr_1);
            graphicMethods2D.addExpression(expr_2);
            graphicMethods2D.setVar(var);
            graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate());
            graphicMethods2D.expressionToGraph(var, x_0.evaluate(), x_1.evaluate());
            graphicMethods2D.setDrawSpecialPoints(false);
            graphicMethods2D.drawGraph2D();
            return;

        }

        HashMap<Integer, Double> zeros = NumericalMethods.solve(expr, var, x_0.evaluate(), x_1.evaluate(), n);

        output = new String[zeros.size() + 1];
        output[0] = "Lösungen der Gleichung " + ((Expression) c.getParams()[0]).writeFormula(true)
                + " = " + ((Expression) c.getParams()[1]).writeFormula(true) + ": \n \n";
        for (int i = 0; i < zeros.size(); i++) {
            output[i + 1] = var + "_" + (i + 1) + " = " + zeros.get(i) + "\n \n";
        }

        /**
         * Nullstellen als Array (zum Markieren).
         */
        double[][] zeros_as_array = new double[zeros.size()][2];
        for (int i = 0; i < zeros_as_array.length; i++) {
            zeros_as_array[i][0] = zeros.get(i);
            Variable.setValue(var, zeros_as_array[i][0]);
            zeros_as_array[i][1] = expr_1.evaluate();
        }

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(true);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addExpression(expr_1);
        graphicMethods2D.addExpression(expr_2);
        graphicMethods2D.setVar(var);
        graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate());
        graphicMethods2D.expressionToGraph(var, x_0.evaluate(), x_1.evaluate());
        graphicMethods2D.setDrawSpecialPoints(true);
        graphicMethods2D.setSpecialPoints(zeros_as_array);
        graphicMethods2D.drawGraph2D();

    }

    private static void executeSolveExact(Command c, JTextArea area)
            throws ExpressionException, EvaluationException {

        HashSet vars = new HashSet();
        Expression expr_1 = ((Expression) c.getParams()[0]).simplify();
        Expression expr_2 = ((Expression) c.getParams()[1]).simplify();
        expr_1.getContainedVars(vars);
        expr_2.getContainedVars(vars);
        //Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
        String var;
        if (c.getParams().length == 3) {
            var = (String) c.getParams()[2];
        } else {
            var = "x";
            if (!vars.isEmpty()) {
                Iterator iter = vars.iterator();
                var = (String) iter.next();
            }
        }

        HashMap<Integer, Expression> zeros = SolveMethods.solveGeneralEquation(expr_1, expr_2, var, area);

        /**
         * Falls Lösungen Parameter K_1, K_2, ... enthalten, dann zusätzlich
         * ausgeben: K_1, K_2, ... sind beliebige ganze Zahlen.
         */
        boolean contains_free_parameter = false;
        String message_about_free_parameters = "";
        for (int i = 0; i < zeros.size(); i++) {
            contains_free_parameter = contains_free_parameter || zeros.get(i).contains("K_1");
        }
        if (contains_free_parameter) {
            boolean contains_free_parameter_of_given_index = true;
            int max_index = 1;
            while (contains_free_parameter_of_given_index) {
                max_index++;
                contains_free_parameter_of_given_index = false;
                for (int i = 0; i < zeros.size(); i++) {
                    contains_free_parameter_of_given_index = contains_free_parameter_of_given_index
                            || zeros.get(i).contains("K_" + max_index);
                }
            }
            max_index--;

            message_about_free_parameters = "K_1, ";
            for (int i = 2; i <= max_index; i++) {
                message_about_free_parameters = message_about_free_parameters + "K_" + i + ", ";
            }
            message_about_free_parameters = message_about_free_parameters.substring(0, message_about_free_parameters.length() - 2);
            if (max_index == 1) {
                message_about_free_parameters = message_about_free_parameters + " ist eine beliebige ganze Zahl. \n \n";
            } else {
                message_about_free_parameters = message_about_free_parameters + " sind beliebige ganze Zahlen. \n \n";
            }

        }

        if (contains_free_parameter) {
            output = new String[zeros.size() + 2];
            output[0] = "Lösungen der Gleichung " + ((Expression) c.getParams()[0]).writeFormula(true)
                    + " = " + ((Expression) c.getParams()[1]).writeFormula(true) + ": \n \n";
            for (int i = 0; i < zeros.size(); i++) {
                output[i + 1] = var + "_" + (i + 1) + " = " + zeros.get(i).writeFormula(true) + "\n \n";
            }
            output[output.length - 1] = message_about_free_parameters;
        } else {
            output = new String[zeros.size() + 1];
            output[0] = "Lösungen der Gleichung " + ((Expression) c.getParams()[0]).writeFormula(true)
                    + " = " + ((Expression) c.getParams()[1]).writeFormula(true) + ": \n \n";
            for (int i = 0; i < zeros.size(); i++) {
                output[i + 1] = var + "_" + (i + 1) + " = " + zeros.get(i).writeFormula(true) + "\n \n";
            }
        }

    }

    private static void executeSolveDGL(Command c, JTextArea area, GraphicMethods2D graphicMethods2D)
            throws ExpressionException, EvaluationException {

        int ord = (int) c.getParams()[2];
        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr.getContainedVars(vars);

        HashSet vars_without_primes = new HashSet();
        Iterator iter = vars.iterator();
        String var_without_primes;
        for (int i = 0; i < vars.size(); i++) {
            var_without_primes = (String) iter.next();
            if (!var_without_primes.replaceAll("'", "").equals(c.getParams()[1])) {
                if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord) {
                    throw new ExpressionException("Die Differentialgleichung besitzt die Ordnung " + ord + ". "
                            + "Es dürfen daher nur Ableitungen höchstens " + (ord - 1) + ". Ordnung auftreten.");
                }
                var_without_primes = var_without_primes.replaceAll("'", "");
            }
            vars_without_primes.add(var_without_primes);
        }

        String var_1 = (String) c.getParams()[1];
        Expression x_0 = (Expression) c.getParams()[3];
        Expression x_1 = (Expression) c.getParams()[4];
        Expression[] y_0 = new Expression[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (Expression) c.getParams()[i + 5];
        }
        double[] y_0_as_double = new double[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0_as_double[i] = y_0[i].evaluate();
        }

        /**
         * zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt
         * werden.
         */
        String var_2 = "";

        if (vars_without_primes.isEmpty()) {
            if (var_1.equals("y")) {
                var_2 = "z";
            } else {
                var_2 = "y";
            }
        } else if (vars_without_primes.size() == 1) {
            if (vars_without_primes.contains(var_1)) {
                if (var_1.equals("y")) {
                    var_2 = "z";
                } else {
                    var_2 = "y";
                }
            } else {
                iter = vars_without_primes.iterator();
                var_2 = (String) iter.next();
            }
        } else {
            iter = vars_without_primes.iterator();
            var_2 = (String) iter.next();
            if (var_2.equals(var_1)) {
                var_2 = (String) iter.next();
            }
        }

        double[][] solution = NumericalMethods.solveDGL(expr.simplify(), var_1, var_2, ord, x_0.evaluate(), x_1.evaluate(), y_0_as_double, 1000);

        /**
         * Falls die Lösung innerhalb des Berechnungsbereichs
         * unendlich/undefiniert ist.
         *
         */
        if (solution.length < 1001) {
            output = new String[2 + solution.length];
            output[output.length - 1] = "Die Lösung der Differentialgleichung ist an der Stelle "
                    + (x_0.evaluate() + (solution.length) * (x_1.evaluate() - x_0.evaluate()) / 1000) + " nicht definiert. \n \n";
        } else {
            output = new String[1 + solution.length];
        }

        /**
         * Formulierung und Ausgabe des AWP.
         */
        output[0] = "Lösung der Differentialgleichung: " + var_2;
        for (int i = 0; i < ord; i++) {
            output[0] = output[0] + "'";
        }

        output[0] = output[0] + "(" + var_1 + ") = " + expr.writeFormula(true);
        for (int i = 0; i < ord; i++) {
            output[0] = output[0] + ", " + var_2;
            for (int j = 0; j < i; j++) {
                output[0] = output[0] + "'";
            }
            output[0] = output[0] + "(" + x_0.writeFormula(true) + ") = ";
            output[0] = output[0] + y_0[i].writeFormula(true);
        }

        output[0] = output[0] + ", " + x_0.writeFormula(true) + " <= " + var_1 + " <= " + x_1.writeFormula(true) + " \n \n";

        /**
         * Lösungen ausgeben.
         */
        for (int i = 0; i < solution.length; i++) {
            output[i + 1] = var_1 + " = " + solution[i][0] + "; " + var_2 + " = " + solution[i][1] + "\n \n";
        }

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(true);
        graphicMethods2D.setGraphIsFixed(true);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addGraph(solution);
        graphicMethods2D.setVars(var_1, var_2);
        graphicMethods2D.computeScreenSizes();
        graphicMethods2D.setDrawSpecialPoints(false);
        graphicMethods2D.drawGraph2D();

    }

    private static void executeTangent(Command c, JTextArea area, GraphicMethods2D graphicMethods2D)
            throws ExpressionException, EvaluationException {

        Expression expr = (Expression) c.getParams()[0];
        HashMap<String, Expression> vars = (HashMap<String, Expression>) c.getParams()[1];

        String tangent_announcement = "Gleichung des Tangentialraumes an den Graphen von Y = " + expr.writeFormula(true) + " im Punkt ";

        for (String var : vars.keySet()) {
            tangent_announcement = tangent_announcement + var + " = " + vars.get(var).writeFormula(true) + ", ";
        }
        tangent_announcement = tangent_announcement.substring(0, tangent_announcement.length() - 2) + ": \n \n";

        Expression tangent = AnalysisMethods.getTangentSpace(expr.simplify(), vars);
        area.append(tangent_announcement);
        area.append("Y=" + tangent.writeFormula(true) + "\n \n");

        if (vars.size() == 1) {

            String var = "";
            for (String unique_var : vars.keySet()) {
                var = unique_var;
            }
            double x_0 = vars.get(var).evaluate() - 1;
            double x_1 = x_0 + 2;

            double[][] tangent_point = new double[1][2];
            tangent_point[0][0] = vars.get(var).evaluate();
            tangent_point[0][1] = expr.replaceVariable(var, vars.get(var)).evaluate();

            graphicMethods2D.setIsInitialized(true);
            graphicMethods2D.setGraphIsExplicit(true);
            graphicMethods2D.setGraphIsFixed(false);
            graphicMethods2D.clearExpressionAndGraph();
            graphicMethods2D.addExpression(expr);
            graphicMethods2D.addExpression(tangent);
            graphicMethods2D.setVar(var);
            graphicMethods2D.computeScreenSizes(x_0, x_1);
            graphicMethods2D.expressionToGraph(var, x_0, x_1);
            graphicMethods2D.setDrawSpecialPoints(true);
            graphicMethods2D.setSpecialPoints(tangent_point);
            graphicMethods2D.drawGraph2D();

        }

    }

    private static void executeTaylorDGL(Command c, JTextArea area) throws ExpressionException, EvaluationException {

        int ord = (int) c.getParams()[2];
        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr = expr.simplify();
        expr.getContainedVars(vars);

        HashSet vars_without_primes = new HashSet();
        Iterator iter = vars.iterator();
        String var_without_primes;
        for (int i = 0; i < vars.size(); i++) {
            var_without_primes = (String) iter.next();
            if (!var_without_primes.replaceAll("'", "").equals(c.getParams()[1])) {
                if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord) {
                    throw new ExpressionException("Die Differentialgleichung besitzt die Ordnung " + ord + ". "
                            + "Es dürfen daher nur Ableitungen höchstens " + (ord - 1) + ". Ordnung auftreten.");
                }
                var_without_primes = var_without_primes.replaceAll("'", "");
            }
            vars_without_primes.add(var_without_primes);
        }

        String var_1 = (String) c.getParams()[1];
        Expression x_0 = (Expression) c.getParams()[3];
        Expression[] y_0 = new Expression[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (Expression) c.getParams()[i + 4];
        }

        int k = (int) c.getParams()[ord + 4];

        /**
         * zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt
         * werden.
         */
        String var_2;

        if (vars_without_primes.isEmpty()) {
            if (var_1.equals("y")) {
                var_2 = "z";
            } else {
                var_2 = "y";
            }
        } else if (vars_without_primes.size() == 1) {
            if (vars_without_primes.contains(var_1)) {
                if (var_1.equals("y")) {
                    var_2 = "z";
                } else {
                    var_2 = "y";
                }
            } else {
                iter = vars_without_primes.iterator();
                var_2 = (String) iter.next();
            }
        } else {
            iter = vars_without_primes.iterator();
            var_2 = (String) iter.next();
            if (var_2.equals(var_1)) {
                var_2 = (String) iter.next();
            }
        }

        Expression result = AnalysisMethods.getTaylorPolynomialFromDGL(expr, var_1, var_2, ord, x_0, y_0, k);
        output = new String[1];
        output[0] = var_2 + "(" + var_1 + ") = " + result.writeFormula(true) + "\n \n";

    }

    private static void executeUndefine(Command c, JTextArea area, HashMap definedVars, HashSet definedVarsSet)
            throws ExpressionException, EvaluationException {

        /**
         * Falls ein Variablenwert freigegeben wird.
         */
        String current_var;
        int vars_count = 0;
        for (int i = 0; i < c.getParams().length; i++) {
            current_var = ((Variable) c.getParams()[i]).getName();
            if (definedVarsSet.contains(current_var)) {
                vars_count++;
            }
        }
        output = new String[vars_count];
        for (int i = 0; i < c.getParams().length; i++) {
            current_var = ((Variable) c.getParams()[i]).getName();
            if (definedVarsSet.contains(current_var)) {
                definedVarsSet.remove(current_var);
                definedVars.remove(current_var);
                output[i] = "Die Veränderliche " + current_var + " ist wieder eine Unbestimmte. \n \n";
            }
        }

    }

    private static void executeUndefineAll(Command c, JTextArea area, HashMap definedVars, HashSet definedVarsSet)
            throws ExpressionException, EvaluationException {

        /**
         * Entleert definedVarsSet und definedVars
         */
        definedVarsSet.clear();
        definedVars.clear();
        output = new String[1];
        output[0] = "Alle Veränderlichen sind wieder Unbestimmte. \n \n";

    }

}
