package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.Constant;
import expressionbuilder.ExpressionException;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethodsCurves2D;
import expressionbuilder.GraphicMethods3D;
import expressionbuilder.GraphicMethodsCurves3D;
import expressionbuilder.SelfDefinedFunction;
import expressionbuilder.Variable;
import expressionbuilder.AnalysisMethods;
import expressionbuilder.BinaryOperation;
import expressionbuilder.NumericalMethods;
import expressionbuilder.TypeBinary;
import java.math.BigDecimal;

import javax.swing.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;

public class MathCommandCompiler {

    /**
     * Liste aller gültiger Befehle. Dies benötigt das Hauptprogramm
     * MathToolForm, um zu prüfen, ob es sich um einen gültigen Befehl handelt.
     */
    public static final String[] commands = {"approx", "clear", "def", "defvars", "euler", "latex",
        "pi", "plot", "plotcurve", "solve", "solvedgl", "tangent", "taylordgl", "undef", "undefall"};

    /**
     * Wichtig: Der String command und die Parameter params entahlten keine
     * Leerzeichen mehr. Diese wurden bereits im Vorfeld beseitigt.
     */
    public static Command getCommand(String command, String[] params) throws ExpressionException {

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

            String eq = "=";

            if (!params[0].contains(eq)) {
                throw new ExpressionException("Im Befehl 'def' muss ein Gleichheitszeichen als Zuweisungsoperator vorhanden sein.");
            }

            String function_name_and_params = params[0].substring(0, params[0].indexOf(eq));
            String function_term = params[0].substring(params[0].indexOf(eq) + 1, params[0].length());

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
            Expression expr = Expression.build(function_term, vars);

            try {
                /**
                 * function_name = Funktionsname function_vars =
                 * Funktionsvariablen Beispiel: def(f(x, y) = x^2+y) Dann:
                 * function_name = "f" function_vars = {"x_ABSTRACT",
                 * "y_ABSTRACT"}
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

        //PLOT
        /**
         * Struktur: PLOT(EXPRESSION(var), value_1, value_2) EXPRESSION:
         * Ausdruck in einer Variablen. value_1 < value_2: Grenzen des
         * Zeichenbereichs. 
         * ODER: 
         * PLOT(EXPRESSION_1(var), ..., EXPRESSION_n(var),
         * value_1, value_2) EXPRESSION_i(var): Ausdruck in einer Variablen.
         * value_1 < value_2: Grenzen des Zeichenbereichs 
         * ODER:
         * PLOT(EXPRESSION(var1, var2), value_1, value_2, value_3, value_4)
         * EXPRESSION: Ausdruck in höchstens zwei Variablen. value_1 < value_2,
         * value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden Variablen
         * werden dabei alphabetisch geordnet. 
         * ODER: PLOT(EXPRESSION_1(var1,
         * var2) = EXPRESSION_2(var1, var2), value_1, value_2, value_3, value_4)
         * (Plot der Lösungsmenge {EXPRESSION_1 = EXPRESSION_2}) EXPRESSION_1,
         * EXPRESSION_2: Ausdrücke in höchstens zwei Variablen. value_1 <
         * value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         * Variablen werden dabei alphabetisch geordnet.
         */
        if (command.equals("plot")) {
            if (params.length < 3) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'plot'");
            }

            HashSet vars = new HashSet();

            if (params.length == 3) {

                try {
                    Expression expr = Expression.build(params[0], new HashSet());
                    expr.getContainedVars(vars);
                } catch (ExpressionException e) {
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
                }

                if (vars.size() > 1) {
                    throw new ExpressionException("Der Ausdruck im Befehl 'plot' darf höchstens eine Veränderliche enthalten. Dieser enthält jedoch "
                            + String.valueOf(vars.size()) + " Veränderliche.");
                }

                try {
                    Double.parseDouble(params[1]);
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try {
                    Double.parseDouble(params[2]);
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                Expression expr = Expression.build(params[0], vars);
                double x_0 = Double.parseDouble(params[1]);
                double x_1 = Double.parseDouble(params[2]);
                if (x_0 >= x_1) {
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss größer sein als der zweite Parameter.");
                }
                command_params = new Object[3];
                command_params[0] = expr;
                command_params[1] = x_0;
                command_params[2] = x_1;
                result.setName(command);
                result.setParams(command_params);
                return result;

            } else if (params.length != 5) {

                for (int i = 0; i < params.length - 2; i++) {
                    try {
                        Expression.build(params[i], new HashSet()).getContainedVars(vars);
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der " + (i + 1) + ". Parameter im Befehl 'plot' muss ein gültiger Ausdruck in einer Veränderlichen sein.");
                    }
                }

                for (int i = 0; i < params.length - 2; i++) {
                    Expression.build(params[i], new HashSet()).getContainedVars(vars);
                }

                if (vars.size() > 1) {
                    throw new ExpressionException("Die Ausdrücke im Befehl 'plot' dürfen höchstens eine Veränderliche enthalten. "
                            + "Diese enthalten jedoch " + vars.size() + " Veränderliche.");
                }

                try {
                    Double.parseDouble(params[params.length - 2]);
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try {
                    Double.parseDouble(params[params.length - 1]);
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Der " + params.length + ". Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                double x_0 = Double.parseDouble(params[params.length - 2]);
                double x_1 = Double.parseDouble(params[params.length - 1]);
                if (x_0 >= x_1) {
                    throw new ExpressionException("Der " + (params.length - 1) + ". Parameter im Befehl 'plot' muss größer sein als der "
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

                /**
                 * Zunächst wird geprüft, ob es sich um einen Plot von drei
                 * 2D-Graphen handeln kann. Falls nicht -> keine Exception
                 * werfen und weitere Möglichkeiten überprüfen. Für die
                 * Eindeutigkeit reicht es zu testen, on der zweite Parameter
                 * ein Ausdruck (in einer Veränderlichen) ist.
                 */
                boolean is_not_plot_of_three_graphs = params[0].contains("=");
                try {
                    HashSet vars_in_first_expr = new HashSet();
                    Expression.build(params[0], vars_in_first_expr);
                    Double.parseDouble(params[1]);
                    Double.parseDouble(params[2]);
                    Double.parseDouble(params[3]);
                    Double.parseDouble(params[4]);

                    /**
                     * Falls man hier angekommen ist, so kann es sich nicht um
                     * einen Plot von drei 2D-Graphen handeln.
                     */
                    is_not_plot_of_three_graphs = true;
                } catch (ExpressionException | NumberFormatException e) {
                }

                if (!is_not_plot_of_three_graphs) {
                    Expression.build(params[0], new HashSet()).getContainedVars(vars);
                    Expression.build(params[1], new HashSet()).getContainedVars(vars);
                    Expression.build(params[2], new HashSet()).getContainedVars(vars);
                    if (vars.size() > 1) {
                        throw new ExpressionException("Die Ausdrücke im Befehl 'plot' dürfen höchstens eine Veränderliche enthalten. "
                                + "Diese enthalten jedoch " + vars.size() + " Veränderliche.");
                    }

                    try {
                        Double.parseDouble(params[3]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der vierte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    try {
                        Double.parseDouble(params[4]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der fünfte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    double x_0 = Double.parseDouble(params[3]);
                    double x_1 = Double.parseDouble(params[4]);
                    if (x_0 >= x_1) {
                        throw new ExpressionException("Der vierte Parameter im Befehl 'plot' muss größer sein als der dritte Parameter.");
                    }

                    command_params = new Object[5];
                    command_params[0] = Expression.build(params[0], vars);
                    command_params[1] = Expression.build(params[1], vars);
                    command_params[2] = Expression.build(params[2], vars);
                    command_params[3] = x_0;
                    command_params[4] = x_1;
                    result.setName(command);
                    result.setParams(command_params);
                    return result;
                }

                if (params[0].contains("=")) {

                    try {
                        Expression.build(params[0].substring(0, params[0].indexOf("=")), new HashSet()).getContainedVars(vars);
                        Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), new HashSet()).getContainedVars(vars);
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss aus zwei gültigen Ausdrücken bestehen,"
                                + " welche durch ein '=' verbunden sind. Gemeldeter Fehler: " + e.getMessage());
                    }

                    if (vars.size() > 2) {
                        throw new ExpressionException("Die beiden Ausdrücke im Befehl 'plot' dürfen höchstens zwei Veränderliche enthalten. Diese enthalten jedoch "
                                + String.valueOf(vars.size()) + " Veränderliche.");
                    }

                    try {
                        Double.parseDouble(params[1]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    try {
                        Double.parseDouble(params[2]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    try {
                        Double.parseDouble(params[3]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    try {
                        Double.parseDouble(params[4]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der vierte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    Expression expr_left = Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
                    Expression expr_right = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
                    double x_0 = Double.parseDouble(params[1]);
                    double x_1 = Double.parseDouble(params[2]);
                    double y_0 = Double.parseDouble(params[3]);
                    double y_1 = Double.parseDouble(params[4]);
                    if (x_0 >= x_1) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss größer sein als der zweite Parameter.");
                    }
                    if (y_0 >= y_1) {
                        throw new ExpressionException("Der fünfte Parameter im Befehl 'plot' muss größer sein als der vierte Parameter.");
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

                } else {

                    try {
                        Expression expr = Expression.build(params[0], vars);
                        expr.getContainedVars(vars);
                    } catch (ExpressionException e) {
                        throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
                    }

                    if (vars.size() > 2) {
                        throw new ExpressionException("Der Ausdruck im Befehl 'plot' darf höchstens zwei Veränderliche enthalten. Dieser enthält jedoch "
                                + String.valueOf(vars.size()) + " Veränderliche.");
                    }

                    try {
                        Double.parseDouble(params[1]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    try {
                        Double.parseDouble(params[2]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    try {
                        Double.parseDouble(params[3]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    try {
                        Double.parseDouble(params[4]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der vierte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                    }

                    Expression expr = Expression.build(params[0], vars);
                    double x_0 = Double.parseDouble(params[1]);
                    double x_1 = Double.parseDouble(params[2]);
                    double y_0 = Double.parseDouble(params[3]);
                    double y_1 = Double.parseDouble(params[4]);
                    if (x_0 >= x_1) {
                        throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss größer sein als der zweite Parameter.");
                    }
                    if (y_0 >= y_1) {
                        throw new ExpressionException("Der fünfte Parameter im Befehl 'plot' muss größer sein als der vierte Parameter.");
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

            }
        }

        //PLOTCURVE
        /**
         * Struktur: PLOTCURVE([FUNCTION_1(var), FUNCTION_2(var)], value_1, value_2).
         * FUNCTION_i(var) = Funktion in einer Variablen. value_1 < value_2: Parametergrenzen. 
         * ODER:
         * Struktur: PLOTCURVE([FUNCTION_1(var), FUNCTION_2(var), FUNCTION_3(var)], value_1, value_2).
         * FUNCTION_i(var) = Funktion in einer Variablen. value_1 < value_2: Parametergrenzen. 
         */
        if (command.equals("plotcurve")) {
            if (params.length != 3) {
                throw new ExpressionException("Im Befehl 'plotcurve' müssen gnau drei Parameter stehen.");
            }

            HashSet vars = new HashSet();

            /** Es wird nun geprüft, ob der erste Parameter die Form "[expr_1, expr_2]" oder 
             * "[expr_1, expr_2, expr_3]" besitzt.
             */

            if (!params[0].substring(0, 1).equals("(") || !params[0].substring(params[0].length() - 1, params[0].length()).equals(")")){
                throw new ExpressionException("Der erste Parameter im Befehl 'plotcurve' muss eine parametrisierte Kurve sein.");
            }

            String[] curve_components = Expression.getArguments(params[0].substring(1, params[0].length() - 1));
            if (curve_components.length != 2 && curve_components.length != 3){
                throw new ExpressionException("Die parametrisierte Kurve im Befehl 'plotcurve' muss entweder aus zwei oder aus drei Komponenten bestehen.");
            }
            
            for (int i = 0; i < curve_components.length; i++){
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

            try {
                Double.parseDouble(params[1]);
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der zweite Parameter im Befehl 'plotcurve' muss eine reelle Zahl sein.");
            }

            try {
                Double.parseDouble(params[2]);
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'plotcurve' muss eine reelle Zahl sein.");
            }

            double t_0 = Double.parseDouble(params[1]);
            double t_1 = Double.parseDouble(params[2]);

            if (curve_components.length == 2){
                command_params = new Object[4];
                command_params[0] = Expression.build(curve_components[0], vars);
                command_params[1] = Expression.build(curve_components[1], vars);
                command_params[2] = t_0;
                command_params[3] = t_1;
            } else {
                command_params = new Object[5];
                command_params[0] = Expression.build(curve_components[0], vars);
                command_params[1] = Expression.build(curve_components[1], vars);
                command_params[2] = Expression.build(curve_components[2], vars);
                command_params[3] = t_0;
                command_params[4] = t_1;
            }
            
            result.setName(command);
            result.setParams(command_params);
            return result;
        }

        //SOLVE
        /**
         * Struktur: solve(expr_1 = expr_2, x_1, x_2) ODER solve(expr_1 =
         * expr_2, x_1, x_2, n) var = Variable in der DGL x_0, x_1 legen den
         * Lösungsbereich fest y_0 = Funktionswert an der Stelle x_0
         */
        if (command.equals("solve")) {
            if (params.length < 3) {
                throw new ExpressionException("Zu wenig Parameter im Befehl 'solve'");
            }
            if (params.length > 4) {
                throw new ExpressionException("Zu viele Parameter im Befehl 'solve'");
            }

            if (!params[0].contains("=")) {
                throw new ExpressionException("Der erste Parameter im Befehl 'solve' muss von der Form 'Ausdruck_1 = Ausdruck_2' sein.");
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

            try {
                Double.parseDouble(params[1]);
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der zweite Parameter im Befehl 'solve' muss eine reelle Zahl sein.");
            }

            try {
                Double.parseDouble(params[2]);
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der dritte Parameter im Befehl 'solve' muss eine reelle Zahl sein.");
            }

            if (params.length == 4) {
                try {
                    Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    throw new ExpressionException("Der vierte Parameter im Befehl 'solve' muss positive ganze Zahl sein.");
                }
            }

            double x_1 = Double.parseDouble(params[1]);
            double x_2 = Double.parseDouble(params[2]);

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
                for (int i = 3; i < ord + 5; i++) {
                    try {
                        Double.parseDouble(params[i]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException("Der " + String.valueOf(i + 1) + ". Parameter im Befehl 'solvedgl' muss eine reelle Zahl sein.");
                    }
                }

                command_params = new Object[ord + 5];
                command_params[0] = expr;
                command_params[1] = Variable.create(params[1]);
                command_params[2] = ord;
                for (int i = 3; i < ord + 5; i++) {
                    command_params[i] = Double.parseDouble(params[i]);
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
            } catch (NumberFormatException e) {
                throw new ExpressionException("Der erste Parameter im Befehl 'tangent' muss ein gültiger Ausdruck sein. Gemeldeter Fehler: " + e.getMessage());
            }

            HashSet vars = new HashSet();
            Expression expr = Expression.build(params[0], vars);

            /**
             * Ermittelt die Anzahl der einzugebenen Parameter.
             */
            HashMap<String, Expression> vars_contained_in_params = new HashMap<String, Expression>();
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
            for (int i = 3; i < ord + 4; i++) {
                try {
                    new BigDecimal(params[i]);
                } catch (NumberFormatException e) {
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
            command_params[1] = Variable.create(params[1]);
            command_params[2] = ord;
            for (int i = 3; i < ord + 4; i++) {
                command_params[i] = new BigDecimal(params[i]);
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
            HashMap<String, Expression> definedVars, HashSet definedVarsSet) throws ExpressionException, EvaluationException {

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
            executeDefine(c, area, definedVars, definedVarsSet);
        } else if (c.getName().equals("defvars")) {
            executeDefVars(c, area, definedVars, definedVarsSet);
        } else if (c.getName().equals("euler")) {
            executeEuler(c, area);
        } else if (c.getName().equals("latex")) {
            executeLatex(c, area);
        } else if (c.getName().equals("pi")) {
            executePi(c, area);
        } else if (c.getName().equals("plot") && c.getParams().length != 5 && c.getParams().length != 6) {
            executePlot2D(c, graphicMethods2D);
        } else if (c.getName().equals("plot") && c.getParams().length == 5) {
            if (c.getParams()[1] instanceof Expression) {
                executePlot2D(c, graphicMethods2D);
            } else {
                executePlot3D(c, graphicMethods3D);
            }
        } else if (c.getName().equals("plot") && c.getParams().length == 6) {
            if (c.getParams()[2] instanceof Expression) {
                executePlot2D(c, graphicMethods2D);
            } else {
                executeImplicitPlot2D(c, graphicMethods2D);
            }
        } else if (c.getName().equals("plotcurve") && c.getParams().length == 4) {
            executePlotCurve2D(c, graphicMethodsCurves2D);
        } else if (c.getName().equals("plotcurve") && c.getParams().length == 5) {
            executePlotCurve3D(c, graphicMethodsCurves3D);
        } else if (c.getName().equals("solve")) {
            executeSolve(c, area, graphicMethods2D);
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

    }

    /**
     * Die folgenden Prozeduren führen einzelne Befehle aus. executePlot2D
     * zeichnet einen 2D-Graphen, executePlot3D zeichnet einen 3D-Graphen, etc.
     */
    private static void executeApprox(Command c, JTextArea area, HashMap<String, Expression> definedVars)
            throws ExpressionException, EvaluationException {

        Expression expr = (Expression) c.getParams()[0];

        /**
         * Mit Werten belegte Variablen müssen durch ihren exakten Ausdruck
         * ersetzt werden.
         */
        for (String var : definedVars.keySet()) {
            expr = expr.replaceVariable(var, (Expression) definedVars.get(var));
        }
/**        
        Enumeration keys = definedVars.keys();
        for (int i = 0; i < definedVars.size(); i++) {
            var = (String) keys.nextElement();
            expr = expr.replaceVariable(var, (Expression) definedVars.get(var));
        }
*/
        expr = expr.simplify();
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

    private static void executeDefine(Command c, JTextArea area, HashMap<String, Expression> definedVars, HashSet definedVarsSet)
            throws ExpressionException, EvaluationException {

        /**
         * Falls ein Variablenwert definiert wird.
         */
        if (c.getParams().length == 2) {
            String var = ((Variable) c.getParams()[0]).getName();
            Expression preciseExpression = (Expression) c.getParams()[1];
            Variable.setPreciseExpression(var, preciseExpression);
            definedVars.put(var, preciseExpression);
            definedVarsSet.add(var);
            area.append("Der Wert der Veränderlichen " + var + " wurde auf " + preciseExpression.writeFormula(true) + " gesetzt. \n \n");
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
        }

    }

    private static void executeDefVars(Command c, JTextArea area, HashMap<String, Expression> definedVars, HashSet definedVarsSet)
            throws ExpressionException, EvaluationException {
        String result = "";
        for (String var : definedVars.keySet()) {
            result += var + " = " + ((Expression) definedVars.get(var)).writeFormula(true) + ", ";
        }
        result = result.substring(0, result.length() - 2);
        area.append("Liste aller Veränderlichen mit vordefinierten Werten: " + result + "\n \n");
    }

    private static void executeEuler(Command c, JTextArea area) throws ExpressionException {
        BigDecimal e = AnalysisMethods.e((int) c.getParams()[0]);
        area.append("Eulersche Zahl e auf " + (int) c.getParams()[0] + " Stellen gerundet: " + e.toString() + "\n \n");
    }

    private static void executeLatex(Command c, JTextArea area) throws ExpressionException {
        area.append("Latex-Code: " + ((Expression) c.getParams()[0]).expressionToLatex(true) + "\n \n");
    }

    private static void executePi(Command c, JTextArea area) throws ExpressionException {
        BigDecimal pi = AnalysisMethods.pi((int) c.getParams()[0]);
        area.append("Kreiszahl pi auf " + (int) c.getParams()[0] + " Stellen gerundet: " + pi.toString() + "\n \n");
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

        double x_1 = (double) c.getParams()[c.getParams().length - 2];
        double x_2 = (double) c.getParams()[c.getParams().length - 1];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(true);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        for (int i = 0; i < c.getParams().length - 2; i++) {
            graphicMethods2D.addExpression(exprs[i].simplify());
        }
        graphicMethods2D.expressionToGraph(var, x_1, x_2);
        graphicMethods2D.computeMaxXMaxY();
        graphicMethods2D.setParameters(var, graphicMethods2D.getAxeCenterX(), graphicMethods2D.getAxeCenterY());
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

        double x_0 = (double) c.getParams()[1];
        double x_1 = (double) c.getParams()[2];
        double y_0 = (double) c.getParams()[3];
        double y_1 = (double) c.getParams()[4];

        Iterator iter = vars.iterator();
        String var1 = (String) iter.next();
        String var2 = (String) iter.next();

        /**
         * Die Variablen var1 und var2 sind evtl. noch nicht in alphabetischer
         * Reihenfolge. Dies wird hier nachgeholt. GRUND: Der Zeichenbereich
         * wird durch vier Zahlen eingegrenzt, welche den Variablen in
         * ALPHABETISCHER Reihenfolge entsprechen.
         */
        String var1_alphabetical = var1;
        String var2_alphabetical = var2;

        if ((int) var1.charAt(0) > (int) var2.charAt(0)) {
            var1_alphabetical = var2;
            var2_alphabetical = var1;
        }
        if ((int) var1.charAt(0) == (int) var2.charAt(0)) {
            if ((var1.length() > 1) && (var2.length() == 1)) {
                var1_alphabetical = var2;
                var2_alphabetical = var1;
            }
            if ((var1.length() > 1) && (var2.length() > 1)) {
                int index_var1 = Integer.parseInt(var1.substring(2));
                int index_var2 = Integer.parseInt(var2.substring(2));
                if (index_var1 > index_var2) {
                    var1_alphabetical = var2;
                    var2_alphabetical = var1;
                }
            }
        }

        graphicMethods3D.setParameters(var1_alphabetical, var2_alphabetical, 150, 200, 30, 30);
        graphicMethods3D.expressionToGraph(expr.simplify(), x_0, x_1, y_0, y_1);
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

        double x_1 = (double) c.getParams()[2];
        double x_2 = (double) c.getParams()[3];
        double y_1 = (double) c.getParams()[4];
        double y_2 = (double) c.getParams()[5];

        Iterator iter = vars.iterator();
        String var1 = (String) iter.next();
        String var2 = (String) iter.next();

        String var1_alphabetical = var1;
        String var2_alphabetical = var2;

        if ((int) var1.charAt(0) > (int) var2.charAt(0)) {
            var1_alphabetical = var2;
            var2_alphabetical = var1;
        }
        if ((int) var1.charAt(0) == (int) var2.charAt(0)) {
            if ((var1.length() > 1) && (var2.length() == 1)) {
                var1_alphabetical = var2;
                var2_alphabetical = var1;
            }
            if ((var1.length() > 1) && (var2.length() > 1)) {
                int index_var1 = Integer.parseInt(var1.substring(2));
                int index_var2 = Integer.parseInt(var2.substring(2));
                if (index_var1 > index_var2) {
                    var1_alphabetical = var2;
                    var2_alphabetical = var1;
                }
            }
        }

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(false);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addExpression(expr.simplify());
        graphicMethods2D.setParameters(var1_alphabetical, var2_alphabetical, (x_1 + x_2) / 2, (y_1 + y_2) / 2, (x_2 - x_1) / 2, (y_2 - y_1) / 2);
        graphicMethods2D.setDrawSpecialPoints(false);
        Hashtable<Integer, double[]> implicit_graph = NumericalMethods.solveImplicitEquation(expr, var1_alphabetical, var2_alphabetical,
                x_1, x_2, y_1, y_2);
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
        expr[0] = expr[0].simplify();
        expr[1] = expr[1].simplify();
        
        //Falls der Ausdruck expr konstant ist, soll der Parameter die Bezeichnung "t" tragen.
        if (vars.isEmpty()) {
            vars.add("t");
        }

        double t_0 = (double) c.getParams()[2];
        double t_1 = (double) c.getParams()[3];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethodsCurves2D.setIsInitialized(true);
        graphicMethodsCurves2D.setExpression(expr);
        graphicMethodsCurves2D.setVar(var);
        graphicMethodsCurves2D.computeScreenSizes(t_0, t_1);
        graphicMethodsCurves2D.expressionToGraph(t_0, t_1);
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
        expr[0] = expr[0].simplify();
        expr[1] = expr[1].simplify();
        expr[2] = expr[2].simplify();
        
        //Falls der Ausdruck expr konstant ist, soll der Parameter die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add("t");
        }

        double t_0 = (double) c.getParams()[3];
        double t_1 = (double) c.getParams()[4];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethodsCurves3D.setIsInitialized(true);
        graphicMethodsCurves3D.setExpression(expr);
        graphicMethodsCurves3D.setVar(var);
        graphicMethodsCurves3D.setParameters(150, 200, 30, 30);
        graphicMethodsCurves3D.computeScreenSizes(t_0, t_1);
        graphicMethodsCurves3D.expressionToGraph(t_0, t_1);
        graphicMethodsCurves3D.drawCurve3D();

    }

    private static void executeSolve(Command c, JTextArea area, GraphicMethods2D graphicMethods2D)
            throws ExpressionException, EvaluationException {

        HashSet vars = new HashSet();
        Expression expr_1 = ((Expression) c.getParams()[0]).simplify();
        Expression expr_2 = ((Expression) c.getParams()[1]).simplify();
        Expression expr = new BinaryOperation(expr_1, expr_2, TypeBinary.MINUS).simplify();
        expr.getContainedVars(vars);
        //Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
        String var = "x";
        if (!vars.isEmpty()) {
            Iterator iter = vars.iterator();
            var = (String) iter.next();
        }

        double x_1 = (double) c.getParams()[2];
        double x_2 = (double) c.getParams()[3];
        /**
         * Falls die Anzahl der Unterteilungen nicht angegeben wird, so soll das
         * Intervall in 1000000 Teile unterteilt werden.
         *
         */
        int n = 1000000;

        if (c.getParams().length == 5) {
            n = (int) c.getParams()[4];
        }

        Hashtable<Integer, Double> result = NumericalMethods.solve(expr, x_1, x_2, n);

        area.append("Lösungen der Gleichung: " + expr.writeFormula(true) + " = 0 \n \n");
        for (int i = 0; i < result.size(); i++) {
            area.append(var + "_" + (i + 1) + " = " + result.get(i + 1) + "\n \n");
        }

        /**
         * Nullstellen als Array (zum Markieren).
         */
        double[][] zeros = new double[result.size()][2];
        for (int i = 0; i < zeros.length; i++) {
            zeros[i][0] = result.get(i + 1);
            Variable.setValue(var, zeros[i][0]);
            zeros[i][1] = expr_1.evaluate();
        }

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(true);
        graphicMethods2D.setGraphIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addExpression(expr_1);
        graphicMethods2D.addExpression(expr_2);
        graphicMethods2D.expressionToGraph(var, x_1, x_2);
        graphicMethods2D.computeMaxXMaxY();
        graphicMethods2D.setParameters(var, graphicMethods2D.getAxeCenterX(), graphicMethods2D.getAxeCenterY());
        graphicMethods2D.setDrawSpecialPoints(true);
        graphicMethods2D.setSpecialPoints(zeros);
        graphicMethods2D.drawGraph2D();

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

        String var1 = ((Variable) c.getParams()[1]).getName();
        double x_0 = (double) c.getParams()[3];
        double x_1 = (double) c.getParams()[4];
        double[] y_0 = new double[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (double) c.getParams()[i + 5];
        }

        /**
         * zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt
         * werden.
         */
        String var2 = new String();

        if (vars_without_primes.isEmpty()) {
            if (var1.equals("y")) {
                var2 = "z";
            } else {
                var2 = "y";
            }
        } else if (vars_without_primes.size() == 1) {
            if (vars_without_primes.contains(var1)) {
                if (var1.equals("y")) {
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
            if (var2.equals(var1)) {
                var2 = (String) iter.next();
            }
        }

        double[][] solution = NumericalMethods.solveDGL(expr, var1, var2, ord, x_0, x_1, y_0, 1000);

        /**
         * Formulierung und Ausgabe des AWP.
         */
        String awp = "Lösung der Differentialgleichung: " + var2;
        for (int i = 0; i < ord; i++) {
            awp = awp + "'";
        }

        /**
         * Falls Anfangsdaten ganze Zahlen sind, so sollen diese ohne
         * Nachkommastellen ausgegeben werden.
         */
        awp = awp + "(" + var1 + ") = " + expr.writeFormula(true);
        for (int i = 0; i < ord; i++) {
            awp = awp + ", " + var2;
            for (int j = 0; j < i; j++) {
                awp = awp + "'";
            }
            if (x_0 == Math.round(x_0)) {
                awp = awp + "(" + String.valueOf((long) x_0) + ") = ";
            } else {
                awp = awp + "(" + String.valueOf(x_0) + ") = ";
            }
            if (y_0[i] == Math.round(y_0[i])) {
                awp = awp + String.valueOf((long) y_0[i]);
            } else {
                awp = awp + String.valueOf(y_0[i]);
            }
        }

        if (x_0 == Math.round(x_0)) {
            awp = awp + ", " + String.valueOf((long) x_0) + " <= " + var1 + " <= ";
        } else {
            awp = awp + ", " + String.valueOf(x_0) + " <= " + var1 + " <= ";
        }
        if (x_1 == Math.round(x_1)) {
            awp = awp + String.valueOf((long) x_1) + " \n \n";
        } else {
            awp = awp + String.valueOf(x_1) + " \n \n";
        }

        area.append(awp);
        for (int i = 0; i < solution.length; i++) {
            area.append(var1 + " = " + solution[i][0] + "; " + var2 + " = " + solution[i][1] + "\n \n");
        }

        /**
         * Falls die Lösung innerhalb des Berechnungsbereichs
         * unendlich/undefiniert ist.
         *
         */
        double pos_of_critical_line = 0;
        if (solution.length < 1001) {
            pos_of_critical_line = x_0 + (solution.length) * (x_1 - x_0) / 1000;
            area.append("Die Lösung der Differentialgleichung ist an der Stelle " + pos_of_critical_line + " nicht definiert. \n \n");
        }

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setGraphIsExplicit(true);
        graphicMethods2D.setGraphIsFixed(true);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addGraph(solution);
        graphicMethods2D.computeMaxXMaxY();
        graphicMethods2D.setParameters(var1, graphicMethods2D.getAxeCenterX(), graphicMethods2D.getAxeCenterY());
        graphicMethods2D.setDrawSpecialPoints(false);
        graphicMethods2D.drawGraph2D();

    }

    private static void executeTangent(Command c, JTextArea area, GraphicMethods2D graphicMethods2D)
            throws ExpressionException, EvaluationException {

        Expression expr = (Expression) c.getParams()[0];
        HashMap<String, Expression> vars = (HashMap<String, Expression>) c.getParams()[1];

        String tangent_announcement = "Gleichung des Tangentialraumes an den Graphen von Y = " + expr.writeFormula(true) + " im Punkt ";

        for(String var : vars.keySet()){
            tangent_announcement = tangent_announcement + var + " = " + vars.get(var).writeFormula(true) + ", ";
        }                
        tangent_announcement = tangent_announcement.substring(0, tangent_announcement.length() - 2) + ": \n \n";

        Expression tangent = AnalysisMethods.getTangentSpace(expr, vars);
        area.append(tangent_announcement);
        area.append("Y=" + tangent.writeFormula(true) + "\n \n");

        if (vars.size() == 1) {

            String var = "";
            for(String unique_var : vars.keySet()){
                var = unique_var;
            }                
            double x_1 = vars.get(var).evaluate() - 1;
            double x_2 = x_1 + 2;

            double[][] tangent_point = new double[1][2];
            tangent_point[0][0] = vars.get(var).evaluate();
            tangent_point[0][1] = expr.replaceVariable(var, vars.get(var)).evaluate();

            graphicMethods2D.setIsInitialized(true);
            graphicMethods2D.setGraphIsExplicit(true);
            graphicMethods2D.setGraphIsFixed(false);
            graphicMethods2D.clearExpressionAndGraph();
            graphicMethods2D.addExpression(expr);
            graphicMethods2D.addExpression(tangent);
            graphicMethods2D.expressionToGraph(var, x_1, x_2);
            graphicMethods2D.computeMaxXMaxY();
            graphicMethods2D.setParameters(var, graphicMethods2D.getAxeCenterX(), graphicMethods2D.getAxeCenterY());
            graphicMethods2D.setDrawSpecialPoints(true);
            graphicMethods2D.setSpecialPoints(tangent_point);
            graphicMethods2D.drawGraph2D();

        }

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

        String var1 = ((Variable) c.getParams()[1]).getName();
        BigDecimal x_0 = (BigDecimal) c.getParams()[3];
        BigDecimal[] y_0 = new BigDecimal[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (BigDecimal) c.getParams()[i + 4];
        }

        int k = (int) c.getParams()[ord + 4];

        /**
         * zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt
         * werden.
         */
        String var2 = new String();

        if (vars_without_primes.isEmpty()) {
            if (var1.equals("y")) {
                var2 = "z";
            } else {
                var2 = "y";
            }
        } else if (vars_without_primes.size() == 1) {
            if (vars_without_primes.contains(var1)) {
                if (var1.equals("y")) {
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
            if (var2.equals(var1)) {
                var2 = (String) iter.next();
            }
        }

        Expression result = AnalysisMethods.getTaylorPolynomialFromDGL(expr, var1, ord, x_0, y_0, k);
        area.append(result.writeFormula(true) + "\n \n");

    }

    private static void executeUndefine(Command c, JTextArea area, HashMap definedVars, HashSet definedVarsSet)
            throws ExpressionException, EvaluationException {

        /**
         * Falls ein Variablenwert freigegeben wird.
         */
        String current_var;
        for (int i = 0; i < c.getParams().length; i++) {
            current_var = ((Variable) c.getParams()[i]).getName();
            if (definedVarsSet.contains(current_var)) {
                definedVarsSet.remove(current_var);
                definedVars.remove(current_var);
                area.append("Die Veränderliche " + current_var + " ist wieder eine Unbestimmte. \n \n");
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
        area.append("Alle Veränderlichen sind wieder Unbestimmte. \n \n");

    }

}
