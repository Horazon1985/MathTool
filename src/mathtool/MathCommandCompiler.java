package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.Constant;
import expressionbuilder.ExpressionException;
import expressionbuilder.SelfDefinedFunction;
import expressionbuilder.Variable;
import expressionbuilder.TypeBinary;
import expressionbuilder.TypeSimplify;
import expressionbuilder.BinaryOperation;
import graphic.GraphicMethods2D;
import graphic.GraphicMethodsCurves2D;
import graphic.GraphicMethods3D;
import graphic.GraphicMethodsCurves3D;
import graphic.GraphicMethodsPolar2D;
import graphic.GraphicArea;
import graphic.TypeBracket;
import computationclasses.AnalysisMethods;
import computationclasses.NumericalMethods;
import solveequationmethods.SolveMethods;
import logicalexpressionbuilder.LogicalExpression;
import logicalexpressionbuilder.LogicalVariable;
import translator.Translator;
import command.Command;
import command.TypeCommand;
import expressionbuilder.TypeFunction;
import expressionbuilder.TypeOperator;

import java.math.BigInteger;
import java.math.BigDecimal;

import javax.swing.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class MathCommandCompiler {

    /**
     * Hier werden Berechnungsergebnisse/zusätzliche
     * Hinweise/Meldungen/Warnungen etc. gespeichert, die dem Benutzer nach
     * Beenden der Befehlsausführung mitgeteilt werden.
     */
    private static ArrayList<String> output = new ArrayList<>();

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
        for (TypeFunction protected_function : TypeFunction.values()) {
            if (protected_function.toString().equals(name)) {
                return false;
            }
        }
        /**
         * Prüfen, ob nicht geschützte Operatoren (wie z.B. taylor, int etc.)
         * überschrieben werden.
         */
        String op_name;
        for (TypeOperator protected_operator : TypeOperator.values()) {
            if (protected_operator.equals(TypeOperator.integral)) {
                op_name = "int";
            } else {
                op_name = protected_operator.toString();
            }
            if (op_name.equals(name)) {
                return false;
            }
        }
        /**
         * Prüfen, ob nicht geschützte Befehle (wie z.B. approx etc.)
         * überschrieben werden.
         */
        for (TypeCommand protected_command : TypeCommand.values()) {
            if (protected_command.toString().equals(name)) {
                return false;
            }
        }

        return true;

    }

    /**
     * Diese Funktion wird zum Prüfen für die Vergabe neuer Funktionsnamen
     * benötigt. Sie prüft nach, ob der Funktionsname Sonderzeichen enthält
     * (also Zeichen außer Buchstaben und Ziffern).
     */
    private static boolean checkForSpecialCharacters(String name) {

        int ascii_value_of_letter;

        for (int i = 0; i < name.length(); i++) {

            ascii_value_of_letter = (int) name.charAt(i);
            if (!(ascii_value_of_letter >= 48 && ascii_value_of_letter < 57) && !(ascii_value_of_letter >= 97 && ascii_value_of_letter < 122)) {
                return false;
            }

        }

        return true;

    }

    /**
     * Gibt eine Instanz der Klasse Command zurück, welche zum Namen command und
     * zu den Parametern params gehört. Ansonsten wird eine entsprechende
     * ExpressionException geworfen. WICHTIG: Der String command und die
     * Parameter params enthalten keine Leerzeichen mehr. Diese wurden bereits
     * im Vorfeld beseitigt.
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_APPROX"));
            }

            try {
                Expression.build(params[0], new HashSet());
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_PARAMETER_IN_APPROX_IS_INVALID") + e.getMessage());
            }

            command_params = new Object[1];
            command_params[0] = Expression.build(params[0], new HashSet());
            result.setType(TypeCommand.approx);
            result.setParams(command_params);
            return result;

        }

        //CCNF
        /**
         * Struktur: ccnf(LOGICALEXPRESSION). LOGICALEXPRESSION: Gültiger
         * logischer Ausdruck.
         */
        if (command.equals("ccnf")) {

            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_CCNF"));
            }

            try {
                command_params = new Object[1];
                command_params[0] = LogicalExpression.build(params[0], new HashSet());
                result.setType(TypeCommand.ccnf);
                result.setParams(command_params);
                return result;
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_CCNF"));
            }

        }

        //CDNF
        /**
         * Struktur: cdnf(LOGICALEXPRESSION). LOGICALEXPRESSION: Gültiger
         * logischer Ausdruck.
         */
        if (command.equals("cdnf")) {

            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_CDNF"));
            }

            try {
                command_params = new Object[1];
                command_params[0] = LogicalExpression.build(params[0], new HashSet());
                result.setType(TypeCommand.cdnf);
                result.setParams(command_params);
                return result;
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_CDNF"));
            }

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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_CLEAR"));
            }

            command_params = new Object[0];
            result.setType(TypeCommand.clear);
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

            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEF"));
            }

            if (!params[0].contains("=")) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NO_EQUAL_IN_DEF"));
            }

            String function_name_and_params = params[0].substring(0, params[0].indexOf("="));
            String function_term = params[0].substring(params[0].indexOf("=") + 1, params[0].length());

            /**
             * Falls der linke Teil eine Variable ist, dann ist es eine
             * Zuweisung, die dieser Variablen einen festen Wert zuweist.
             * Beispiel: def(x = 2) liefert: result.name = "def" result.params =
             * {"x"} result.left = 2 (als Expression)
             */
            if (Expression.isValidDerivateOfVariable(function_name_and_params) && !Expression.isPI(function_name_and_params)) {

                Expression preciseExpression;
                HashSet vars = new HashSet();
                try {
                    preciseExpression = Expression.build(function_term, vars);
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_TO_VARIABLE_MUST_BE_ASSIGNED_REAL_VALUE"));
                }
                if (!vars.isEmpty()) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_TO_VARIABLE_MUST_BE_ASSIGNED_CONSTANT_REAL_VALUE"));
                }
                command_params = new Object[2];
                command_params[0] = function_name_and_params;
                command_params[1] = preciseExpression;
                result.setType(TypeCommand.def);
                result.setParams(command_params);
                return result;

            }

            HashSet vars = new HashSet();
            Expression expr;
            /**
             * Nun wird geprüft, ob es sich um eine Funktionsdeklaration
             * handelt. Zunächst wird versucht, den rechten Teilstring vom "="
             * in einen Ausdruck umzuwandeln.
             */
            try {
                expr = Expression.build(function_term, vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_INVALID_EXPRESSION_ON_RIGHT_SIDE") + e.getMessage());
            }

            /**
             * Falls man hier ankommt, muss das obige try funktioniert haben.
             */
            /**
             * WICHTIG! Falls expr bereits vom Benutzer vordefinierte Funktionen
             * enthält (der Benutzer kann beispielsweise eine weitere Funktion
             * mit Hilfe bereits definierter Funktionen definieren), dann werden
             * hier alle neu definierten Funktionen durch vordefinierte
             * Funktionen ersetzt.
             */
            expr = expr.replaceSelfDefinedFunctionsByPredefinedFunctions();

            try {
                /**
                 * Funktionsname und Funktionsvariablen werden ermittelt.
                 */
                Expression.getOperatorAndArguments(function_name_and_params);
                Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_INVALID_DEF"));
            }

            /**
             * Funktionsnamen und Variablen auslesen.
             */
            String function_name = Expression.getOperatorAndArguments(function_name_and_params)[0];
            String[] function_vars = Expression.getArguments(Expression.getOperatorAndArguments(function_name_and_params)[1]);

            /**
             * Falls functions_vars leer ist -> Fehler ausgeben (es muss
             * mindestens eine Variable vorhanden sein).
             */
            if (function_vars.length == 0) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_IS_NO_FUNCTION_VARS_IN_FUNCTION_DECLARATION"));
            }

            /**
             * Nun wird geprüft, ob die einzelnen Parameter in der
             * Funktionsklammer gültige Variablen sind
             */
            for (int i = 0; i < function_vars.length; i++) {
                if (!Expression.isValidDerivateOfVariable(function_vars[i])) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_IS_NOT_VALID_VARIABLE_1")
                            + function_vars[i]
                            + Translator.translateExceptionMessage("MCC_IS_NOT_VALID_VARIABLE_2"));
                }
            }

            /**
             * Nun wird geprüft, ob die Variablen in function_vars auch alle
             * verschieden sind!
             */
            HashSet function_vars_as_hashset = new HashSet();
            for (int i = 0; i < function_vars.length; i++) {
                if (function_vars_as_hashset.contains(function_vars[i])) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_DEF_1")
                            + function_name
                            + Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_DEF_2"));
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_PROTECTED_FUNC_NAME_1")
                        + function_name
                        + Translator.translateExceptionMessage("MCC_PROTECTED_FUNC_NAME_2"));
            }

            /**
             * Prüfen, ob keine Sonderzeichen vorkommen.
             */
            if (!checkForSpecialCharacters(function_name)) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS_1")
                        + function_name
                        + Translator.translateExceptionMessage("MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS_2"));
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
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_RIGHT_SIDE_OF_DEF_CONTAINS_WRONG_VAR"));
                }
            }

            /**
             * result.params werden gesetzt.
             */
            command_params = new Object[2 + function_vars.length];
            command_params[0] = function_name;
            for (int i = 1; i <= function_vars.length; i++) {
                command_params[i] = function_vars[i - 1];
            }
            command_params[1 + function_vars.length] = expr;

            /**
             * Für das obige Beispiel def(f(x, y) = x^2+y) gilt dann:
             * result.type = TypeCommand.DEF result.params = {"f", "x_ABSTRACT",
             * "y_ABSTRACT"} result.left = x_ABSTRACT^2+y_ABSTRACT (als
             * Expression).
             */
            result.setType(TypeCommand.def);
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEFFUNCS"));
            }

            command_params = new Object[0];
            result.setType(TypeCommand.deffuncs);
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEFVARS"));
            }

            command_params = new Object[0];
            result.setType(TypeCommand.defvars);
            result.setParams(command_params);
            return result;

        }

        //EULER
        /**
         * Struktur: euler(int). int: nichtnegative ganze Zahl; bestimmt die
         * Anzahl der Stellen, die von e ausgegeben werden sollen.
         */
        if (command.equals("euler")) {

            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_EULER"));
            }

            /**
             * Zunächst prüfen, ob es sich um eine (evtl. viel zu große) ganze
             * Zahl handelt.
             */
            try {
                BigInteger number_of_digits = new BigInteger(params[0]);
                if (number_of_digits.compareTo(BigInteger.ZERO) < 0) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EULER"));
                }
            } catch (NumberFormatException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EULER"));
            }

            try {
                command_params = new Object[1];
                command_params[0] = Integer.parseInt(params[0]);
                if ((int) command_params[0] < 0) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EULER"));
                }
                result.setType(TypeCommand.euler);
                result.setParams(command_params);
                return result;
            } catch (NumberFormatException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_ENTER_SMALLER_NUMBER_IN_EULER"));
            }

        }

        //EXPAND
        /**
         * Struktur: expand(EXPRESSION). EXPRESSION: Gültiger Ausdruck.
         */
        if (command.equals("expand")) {

            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_EXPAND"));
            }

            try {
                command_params = new Object[1];
                command_params[0] = Expression.build(params[0], new HashSet());
                result.setType(TypeCommand.expand);
                result.setParams(command_params);
                return result;
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EXPAND"));
            }

        }

        //LATEX
        /**
         * Struktur: latex(EXPRESSION) EXPRESSION: Ausdruck, welcher in einen
         * Latex-Code umgewandelt werden soll.
         */
        if (command.equals("latex")) {

            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_LATEX"));
            }

            try {
                int n = 0;
                String expressions = params[0];
                while (expressions.contains("=")) {
                    expressions = expressions.substring(expressions.indexOf("=") + 1, expressions.length());
                    n++;
                }
                expressions = params[0];
                Expression[] exprs = new Expression[n + 1];
                HashSet vars = new HashSet();
                for (int i = 0; i < n; i++) {
                    if (expressions.indexOf("=") == 0) {
                        exprs[i] = null;
                        expressions = expressions.substring(1, expressions.length());
                    } else {
                        exprs[i] = Expression.build(expressions.substring(0, expressions.indexOf("=")), vars);
                        expressions = expressions.substring(expressions.indexOf("=") + 1, expressions.length());
                    }
                }
                if (expressions.length() == 0) {
                    exprs[n] = null;
                } else {
                    exprs[n] = Expression.build(expressions, vars);
                }
                result.setType(TypeCommand.latex);
                result.setParams(exprs);
                return result;
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_LATEX") + e.getMessage());
            }

        }

        //PI
        /**
         * Struktur: pi(int). int: nichtnegative ganze Zahl; bestimmt die Anzahl
         * der Stellen, die von pi ausgegeben werden sollen.
         */
        if (command.equals("pi")) {

            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PI"));
            }

            /**
             * Zunächst prüfen, ob es sich um eine (evtl. viel zu große) ganze
             * Zahl handelt.
             */
            try {
                BigInteger number_of_digits = new BigInteger(params[0]);
                if (number_of_digits.compareTo(BigInteger.ZERO) < 0) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_PI"));
                }
            } catch (NumberFormatException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_PI"));
            }

            try {
                command_params = new Object[1];
                command_params[0] = Integer.parseInt(params[0]);
                if ((int) command_params[0] < 0) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_PI"));
                }
                result.setType(TypeCommand.pi);
                result.setParams(command_params);
                return result;
            } catch (NumberFormatException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_ENTER_SMALLER_NUMBER_IN_PI"));
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT2D"));
            }

            HashSet vars = new HashSet();

            if (params.length != 5 || !params[0].contains("=")) {

                for (int i = 0; i < params.length - 2; i++) {
                    try {
                        Expression.build(params[i], new HashSet()).getContainedVars(vars);
                    } catch (ExpressionException e) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT2D_1")
                                + (i + 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT2D_2"));
                    }
                }

                if (vars.size() > 1) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOT2D_1")
                            + vars.size()
                            + Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOT2D_2"));
                }

                HashSet vars_in_limits = new HashSet();
                try {
                    Expression.build(params[params.length - 2], new HashSet()).getContainedVars(vars_in_limits);
                    if (!vars_in_limits.isEmpty()) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_1")
                                + (params.length - 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_2"));
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_1")
                            + (params.length - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_2"));
                }

                try {
                    Expression.build(params[params.length - 1], new HashSet()).getContainedVars(vars_in_limits);
                    if (!vars_in_limits.isEmpty()) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_1")
                                + params.length
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_2"));
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_1")
                            + params.length
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_2"));
                }

                Expression x_0 = Expression.build(params[params.length - 2], vars_in_limits);
                Expression x_1 = Expression.build(params[params.length - 1], vars_in_limits);
                if (x_0.evaluate() >= x_1.evaluate()) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT2D_1")
                            + (params.length - 1)
                            + Translator.translateExceptionMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT2D_2")
                            + params.length
                            + Translator.translateExceptionMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT2D_3"));
                }

                command_params = new Object[params.length];
                for (int i = 0; i < params.length - 2; i++) {
                    command_params[i] = Expression.build(params[i], vars);
                }
                command_params[params.length - 2] = x_0;
                command_params[params.length - 1] = x_1;
                result.setType(TypeCommand.plot2d);
                result.setParams(command_params);
                return result;

            } else {

                if (params[0].contains("=")) {

                    if (params.length != 5) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_IMPLICIT_PLOT2D"));
                    }

                    try {
                        Expression.build(params[0].substring(0, params[0].indexOf("=")), new HashSet()).getContainedVars(vars);
                        Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), new HashSet()).getContainedVars(vars);
                    } catch (ExpressionException e) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_IMPLICIT_PLOT2D") + e.getMessage());
                    }

                    if (vars.size() > 2) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_IMPLICIT_PLOT2D_1")
                                + String.valueOf(vars.size())
                                + Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_IMPLICIT_PLOT2D_2"));
                    }

                    HashSet vars_in_limits = new HashSet();
                    for (int i = 1; i <= 4; i++) {
                        try {
                            Expression.build(params[i], new HashSet()).getContainedVars(vars_in_limits);
                            if (!vars_in_limits.isEmpty()) {
                                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_IMPLICIT_PLOT2D_1")
                                        + (i + 1)
                                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_IMPLICIT_PLOT2D_2"));
                            }
                        } catch (ExpressionException e) {
                            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_IMPLICIT_PLOT2D_1")
                                    + (i + 1)
                                    + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_IMPLICIT_PLOT2D_2"));
                        }
                    }

                    Expression expr_left = Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
                    Expression expr_right = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
                    Expression x_0 = Expression.build(params[1], vars);
                    Expression x_1 = Expression.build(params[2], vars);
                    Expression y_0 = Expression.build(params[3], vars);
                    Expression y_1 = Expression.build(params[4], vars);
                    if (x_0.evaluate() >= x_1.evaluate()) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_FIRST_LIMITS_MUST_BE_WELL_ORDERED_IN_IMPLICIT_PLOT2D"));
                    }
                    if (y_0.evaluate() >= y_1.evaluate()) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_SECOND_LIMITS_MUST_BE_WELL_ORDERED_IN_IMPLICIT_PLOT2D"));
                    }

                    command_params = new Object[6];
                    command_params[0] = expr_left;
                    command_params[1] = expr_right;
                    command_params[2] = x_0;
                    command_params[3] = x_1;
                    command_params[4] = y_0;
                    command_params[5] = y_1;
                    result.setType(TypeCommand.plot2d);
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
            if (params.length != 5) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOT3D"));
            }

            HashSet vars = new HashSet();

            try {
                Expression expr = Expression.build(params[0], new HashSet());
                expr.getContainedVars(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOT3D") + e.getMessage());
            }

            if (vars.size() > 2) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOT3D_1")
                        + String.valueOf(vars.size())
                        + Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOT3D_2"));
            }

            HashSet vars_in_limits = new HashSet();
            for (int i = 1; i <= 4; i++) {
                try {
                    Expression.build(params[i], new HashSet()).getContainedVars(vars_in_limits);
                    if (!vars_in_limits.isEmpty()) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D_1")
                                + (i + 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D_2"));
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D_1")
                            + (i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D_2"));
                }
            }

            Expression expr = Expression.build(params[0], vars);
            Expression x_0 = Expression.build(params[1], vars);
            Expression x_1 = Expression.build(params[2], vars);
            Expression y_0 = Expression.build(params[3], vars);
            Expression y_1 = Expression.build(params[4], vars);
            if (x_0.evaluate() >= x_1.evaluate()) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_FIRST_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D"));
            }
            if (y_0.evaluate() >= y_1.evaluate()) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_SECOND_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D"));
            }
            command_params = new Object[5];
            command_params[0] = expr;
            command_params[1] = x_0;
            command_params[2] = x_1;
            command_params[3] = y_0;
            command_params[4] = y_1;
            result.setType(TypeCommand.plot3d);
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE"));
            }

            HashSet vars = new HashSet();

            /**
             * Es wird nun geprüft, ob der erste Parameter die Form "(expr_1,
             * expr_2)" oder "(expr_1, expr_2, expr_3)" besitzt.
             */
            if (!params[0].substring(0, 1).equals("(") || !params[0].substring(params[0].length() - 1, params[0].length()).equals(")")) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOTCURVE"));
            }

            String[] curve_components = Expression.getArguments(params[0].substring(1, params[0].length() - 1));
            if (curve_components.length != 2 && curve_components.length != 3) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_CURVE_COMPONENTS_IN_PLOTCURVE"));
            }

            for (int i = 0; i < curve_components.length; i++) {
                try {
                    Expression.build(curve_components[i], vars);
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_CURVE_COMPONENTS_IN_PLOTCURVE_1")
                            + (i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_CURVE_COMPONENTS_IN_PLOTCURVE_2")
                            + e.getMessage());
                }
            }

            if (vars.size() > 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_CURVE_COMPONENTS_IN_PLOTCURVE"));
            }

            HashSet vars_in_limits = new HashSet();
            for (int i = 1; i <= 2; i++) {
                try {
                    Expression.build(params[i], new HashSet()).getContainedVars(vars_in_limits);
                    if (!vars_in_limits.isEmpty()) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_1")
                                + (i + 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_2"));
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_1")
                            + (i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_2"));
                }
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

            result.setType(TypeCommand.plotcurve);
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTPOLAR"));
            }

            HashSet vars = new HashSet();

            for (int i = 0; i < params.length - 2; i++) {
                try {
                    Expression.build(params[i], new HashSet()).getContainedVars(vars);
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTPOLAR_1")
                            + (i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTPOLAR_2"));
                }
            }

            if (vars.size() > 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTPOLAR_1")
                        + vars.size()
                        + Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTPOLAR_2"));
            }

            HashSet vars_in_limits = new HashSet();
            try {
                Expression.build(params[params.length - 2], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_1")
                            + (params.length - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_2"));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_1")
                        + (params.length - 1)
                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_2"));
            }

            try {
                Expression.build(params[params.length - 1], new HashSet()).getContainedVars(vars_in_limits);
                if (!vars_in_limits.isEmpty()) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_1")
                            + params.length
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_2"));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_1")
                        + params.length
                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_2"));
            }

            Expression x_0 = Expression.build(params[params.length - 2], vars_in_limits);
            Expression x_1 = Expression.build(params[params.length - 1], vars_in_limits);
            if (x_0.evaluate() >= x_1.evaluate()) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTPOLAR_1")
                        + (params.length - 1)
                        + Translator.translateExceptionMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTPOLAR_2")
                        + (params.length)
                        + Translator.translateExceptionMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTPOLAR_3"));
            }

            command_params = new Object[params.length];
            for (int i = 0; i < params.length - 2; i++) {
                command_params[i] = Expression.build(params[i], vars);
            }
            command_params[params.length - 2] = x_0;
            command_params[params.length - 1] = x_1;
            result.setType(TypeCommand.plotpolar);
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
            if (params.length < 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVE"));
            }
            if (params.length > 4) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_TOO_MANY_PARAMETERS_IN_SOLVE"));
            }
            if (!params[0].contains("=")) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVE"));
            }

            HashSet vars = new HashSet();
            try {
                Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
                Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVE_WITH_REPORTED_ERROR") + e.getMessage());
            }

            Expression expr_1 = Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
            Expression expr_2 = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);

            if (params.length == 1 || params.length == 2) {

                if (vars.size() > 1 && params.length == 1) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_MORE_THAN_ONE_VARIABLE_IN_SOLVE"));
                }

                if (params.length == 2) {
                    if (!Expression.isValidDerivateOfVariable(params[1])) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_2_PARAMETER_IN_SOLVE"));
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

                result.setType(TypeCommand.solve);
                result.setParams(command_params);
                return result;

            } else {

                if (vars.size() > 1) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_SOLVE"));
                }

                HashSet vars_in_limits = new HashSet();
                for (int i = 1; i <= 2; i++) {
                    try {
                        Expression.build(params[i], new HashSet()).getContainedVars(vars_in_limits);
                        if (!vars_in_limits.isEmpty()) {
                            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVE_1")
                                    + (i + 1)
                                    + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVE_2"));
                        }
                    } catch (ExpressionException e) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVE_1")
                                + (i + 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVE_2"));
                    }
                }

                if (params.length == 4) {
                    try {
                        Integer.parseInt(params[3]);
                    } catch (NumberFormatException e) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVE"));
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
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVE"));
                    }
                    command_params = new Object[5];
                    command_params[0] = expr_1;
                    command_params[1] = expr_2;
                    command_params[2] = x_1;
                    command_params[3] = x_2;
                    command_params[4] = n;
                }

                result.setType(TypeCommand.solve);
                result.setParams(command_params);
                return result;

            }
        }

        //SOLVEDEQ
        /**
         * Struktur: solvedeq(EXPRESSION, var, ord, x_0, x_1, y_0, y'(0), ...,
         * y^(ord - 1)(0)) EXPRESSION: Rechte Seite der DGL y^{(ord)} =
         * EXPRESSION. Anzahl der parameter ist also = ord + 5 var = Variable in
         * der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         * fest x_1 = Obere x-Schranke für die numerische Berechnung
         */
        if (command.equals("solvedeq")) {
            if (params.length < 6) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDEQ"));
            }

            if (params.length >= 6) {

                //Ermittelt die Ordnung der DGL
                int ord;
                try {
                    ord = Integer.parseInt(params[2]);
                    if (ord < 1) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_SOLVEDEQ"));
                    }
                } catch (NumberFormatException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_SOLVEDEQ"));
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
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDEQ") + e.getMessage());
                }
                Expression expr = Expression.build(params[0], vars);

                HashSet vars_without_primes = new HashSet();
                Iterator iter = vars.iterator();
                String var_without_primes;
                for (int i = 0; i < vars.size(); i++) {
                    var_without_primes = (String) iter.next();
                    if (!var_without_primes.replaceAll("'", "").equals(params[1])) {
                        if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord) {
                            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDEQ_1")
                                    + ord
                                    + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDEQ_2")
                                    + (ord - 1)
                                    + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDEQ_3"));
                        }
                        var_without_primes = var_without_primes.replaceAll("'", "");
                    }
                    vars_without_primes.add(var_without_primes);
                }

                if (vars_without_primes.size() > 2) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDEQ"));
                }

                if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
                    if (vars_without_primes.size() == 2) {
                        if (!vars.contains(params[1])) {
                            throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_SOLVEDEQ_1")
                                    + params[1]
                                    + Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_SOLVEDEQ_2"));
                        }
                    }
                } else {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_2_PARAMETER_IN_SOLVEDEQ"));
                }

                if (params.length < ord + 5) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDEQ"));
                }
                if (params.length > ord + 5) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_TOO_MANY_PARAMETERS_IN_SOLVEDEQ"));
                }

                //Prüft, ob die AWP-Daten korrekt sind
                HashSet vars_in_limits = new HashSet();
                for (int i = 3; i < ord + 5; i++) {
                    try {
                        Expression limit = Expression.build(params[i], vars_in_limits);
                        if (!vars_in_limits.isEmpty()) {
                            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_1")
                                    + String.valueOf(i + 1)
                                    + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_2"));
                        }
                        limit.evaluate();
                    } catch (ExpressionException | EvaluationException e) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_1")
                                + String.valueOf(i + 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_2"));
                    }
                }

                command_params = new Object[ord + 5];
                command_params[0] = expr;
                command_params[1] = params[1];
                command_params[2] = ord;
                for (int i = 3; i < ord + 5; i++) {
                    command_params[i] = Expression.build(params[i], vars);
                }

                result.setType(TypeCommand.solvedeq);
                result.setParams(command_params);
                return result;
            }
        }

        //TABLE
        /**
         * Struktur: table(LOGICALEXPRESSION) LOGICALEXPRESSION: Logischer
         * Ausdruck.
         */
        if (command.equals("table")) {
            if (params.length != 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_TABLE"));
            }

            HashSet vars = new HashSet();
            LogicalExpression log_expr;

            try {
                log_expr = LogicalExpression.build(params[0], vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_TABLE") + e.getMessage());
            }

            command_params = new Object[1];
            command_params[0] = log_expr;

            result.setType(TypeCommand.table);
            result.setParams(command_params);
            return result;
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TANGENT"));
            }

            HashSet vars = new HashSet();
            Expression expr;
            try {
                expr = Expression.build(params[0], vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_TANGENT") + e.getMessage());
            }

            /**
             * Ermittelt die Anzahl der einzugebenen Parameter.
             */
            HashMap<String, Expression> vars_contained_in_params = new HashMap<>();
            for (int i = 1; i < params.length; i++) {
                if (!params[i].contains("=")) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT_1")
                            + (i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT_2"));
                }
                if (!Expression.isValidDerivateOfVariable(params[i].substring(0, params[i].indexOf("=")))) {
                    throw new ExpressionException(params[i].substring(0, params[i].indexOf("="))
                            + Translator.translateExceptionMessage("MCC_NOT_A_VALID_VARIABLE_IN_TANGENT"));
                }
                try {
                    Expression point = Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet());
                    if (!point.isConstant()) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT_1")
                                + (i + 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT_2"));
                    }
                } catch (ExpressionException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT_1")
                            + (i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT_2"));
                }
            }

            /**
             * Es wird geprüft, ob keine Veränderlichen doppelt auftreten.
             */
            for (int i = 1; i < params.length; i++) {
                for (int j = i + 1; j < params.length; j++) {
                    if (params[i].substring(0, params[i].indexOf("=")).equals(params[j].substring(0, params[j].indexOf("=")))) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT_1")
                                + params[i].substring(0, params[i].indexOf("="))
                                + Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT_2"));
                    }
                }
            }

            /**
             * Einzelne Punktkoordinaten werden in der HashMap
             * vars_contained_in_params gespeichert.
             */
            for (int i = 1; i < params.length; i++) {
                vars_contained_in_params.put(params[i].substring(0, params[i].indexOf("=")),
                        Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet()));
            }

            /**
             * Es wird geprüft, ob allen Variablen, welche in der
             * Funktionsvorschrift auftauchen, auch eine Koordinate zugewirsen
             * wurde.
             */
            Iterator iter = vars.iterator();
            String var;
            for (int i = 0; i < vars.size(); i++) {
                var = (String) iter.next();
                if (!vars_contained_in_params.containsKey(var)) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_TANGENT_1")
                            + var
                            + Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_TANGENT_2"));
                }
            }

            command_params = new Object[2];
            command_params[0] = expr;
            command_params[1] = vars_contained_in_params;

            result.setType(TypeCommand.tangent);
            result.setParams(command_params);
            return result;
        }

        //TAYLORDEQ
        /**
         * Struktur: taylordeq(EXPRESSION, var, ord, x_0, y_0, y'(0), ...,
         * y^(ord - 1)(0), k) EXPRESSION: Rechte Seite der DGL y^{(ord)} =
         * EXPRESSION. Anzahl der parameter ist also = ord + 5 var = Variable in
         * der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         * fest k = Ordnung des Taylorpolynoms (an der Stelle x_0)
         */
        if (command.equals("taylordeq")) {
            if (params.length < 6) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDEQ"));
            }

            /**
             * Ermittelt die Ordnung der DGL
             */
            int ord;
            try {
                ord = Integer.parseInt(params[2]);
                if (ord < 1) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_TAYLORDEQ"));
                }
            } catch (NumberFormatException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_TAYLORDEQ"));
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_TAYLORDEQ") + e.getMessage());
            }
            Expression expr = Expression.build(params[0], vars);

            HashSet vars_without_primes = new HashSet();
            Iterator iter = vars.iterator();
            String var_without_primes;
            for (int i = 0; i < vars.size(); i++) {
                var_without_primes = (String) iter.next();
                if (!var_without_primes.replaceAll("'", "").equals(params[1])) {
                    if (var_without_primes.length() - var_without_primes.replaceAll("'", "").length() >= ord) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDEQ_1")
                                + ord
                                + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDEQ_2")
                                + (ord - 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDEQ_3"));
                    }
                    var_without_primes = var_without_primes.replaceAll("'", "");
                }
                vars_without_primes.add(var_without_primes);
            }

            if (vars_without_primes.size() > 2) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDEQ"));
            }

            if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
                if (vars_without_primes.size() == 2) {
                    if (!vars.contains(params[1])) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_TAYLORDEQ_1")
                                + params[1]
                                + Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_TAYLORDEQ_2"));
                    }
                }
            } else {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_2_PARAMETER_IN_TAYLORDEQ"));
            }

            if (params.length < ord + 5) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDEQ"));
            }

            if (params.length > ord + 5) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_TOO_MANY_PARAMETERS_IN_TAYLORDEQ"));
            }

            /**
             * Nun wird vars_without_primes, falls nötig, soweit ergänzt, dass
             * es alle in der DGL auftretenden Variablen enthält (max. 2). Dies
             * wird später wichtig sein, wenn es darum geht, zu prüfen, ob die
             * SWP-Daten korrekt sind.
             */
            if (vars_without_primes.isEmpty()) {
                vars_without_primes.add(params[1]);
                if (params[1].equals("y")) {
                    vars_without_primes.add("z");
                } else {
                    vars_without_primes.add("y");
                }
            } else if (vars_without_primes.size() == 1) {

                if (vars_without_primes.contains(params[1])) {
                    if (params[1].equals("y")) {
                        vars_without_primes.add("z");
                    } else {
                        vars_without_primes.add("y");
                    }
                } else {
                    vars_without_primes.add(params[1]);
                }

            }

            /**
             * Prüft, ob die AWP-Daten korrekt sind.
             */
            HashSet vars_in_limits = new HashSet();
            for (int i = 3; i < ord + 4; i++) {
                try {
                    Expression.build(params[i], vars_in_limits).simplify();
                    iter = vars_without_primes.iterator();
                    /**
                     * Im Folgenden wird geprüft, ob in den Anfangsbedingungen
                     * die Variablen aus der eigentlichen DGL nicht auftreten
                     * (diese beiden Variablen sind im HashSet
                     * vars_without_primes gespeichert).
                     */
                    if (vars_in_limits.contains(iter.next()) || vars_in_limits.contains(iter.next())) {
                        throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDEQ_1")
                                + String.valueOf(i + 1)
                                + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDEQ_2"));
                    }
                } catch (ExpressionException | EvaluationException e) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDEQ_1")
                            + String.valueOf(i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDEQ_2"));
                }
            }

            try {
                Integer.parseInt(params[ord + 4]);
            } catch (NumberFormatException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_TAYLORDEQ"));
            }

            command_params = new Object[ord + 5];
            command_params[0] = expr;
            command_params[1] = params[1];
            command_params[2] = ord;
            for (int i = 3; i < ord + 4; i++) {
                command_params[i] = Expression.build(params[i], vars);
            }
            command_params[ord + 4] = Integer.parseInt(params[ord + 4]);

            result.setType(TypeCommand.taylordeq);
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
                if (!Expression.isValidDerivateOfVariable(params[i]) && !Expression.isPI(params[i])) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEF_1")
                            + (i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEF_2"));
                }
            }

            command_params = new Object[params.length];
            System.arraycopy(params, 0, command_params, 0, params.length);

            result.setType(TypeCommand.undef);
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
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_UNDEFALL"));
            }

            command_params = new Object[0];
            result.setType(TypeCommand.undefall);
            result.setParams(command_params);
            return result;
        }

        return result;

    }

    /**
     * Hauptmethode zum Ausführen des Befehls.
     *
     * @throws ExpressionException
     * @throws EvaluationException
     */
    public static void executeCommand(String commandLine, GraphicArea graphicArea,
            JTextArea textArea, GraphicMethods2D graphicMethods2D, GraphicMethods3D graphicMethods3D,
            GraphicMethodsCurves2D graphicMethodsCurves2D, GraphicMethodsCurves3D graphicMethodsCurves3D,
            GraphicMethodsPolar2D graphicMethodsPolar2D, HashMap<String, Expression> definedVars,
            HashMap<String, Expression> definedFunctions) throws ExpressionException, EvaluationException {

        output.clear();
        int n = commandLine.length();

        //Leerzeichen beseitigen und alles zu Kleinbuchstaben machen
        commandLine = commandLine.replaceAll(" ", "");

        //Falls Großbuchstaben auftreten -> zu Kleinbuchstaben machen
        char part;
        for (int i = 0; i < n; i++) {
            part = commandLine.charAt(i);
            if (((int) part >= 65) && ((int) part <= 90)) {
                //Macht Großbuchstaben zu Kleinbuchstaben
                part = (char) ((int) part + 32);
                commandLine = commandLine.substring(0, i) + part + commandLine.substring(i + 1, n);
            }
        }

        String[] command_and_params = Expression.getOperatorAndArguments(commandLine);
        String command = command_and_params[0];
        String[] params = Expression.getArguments(command_and_params[1]);

        /**
         * Zunächst muss der entsprechende Befehl ermittelt und in ein Objekt
         * der Klasse Command umgewandelt werdeb.
         */
        Command c = getCommand(command, params);

        /**
         * Abhängig vom Typ von c wird der Befehl ausgeführt.
         */
        if (c.getTypeCommand().equals(TypeCommand.approx)) {
            executeApprox(c, definedVars, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.ccnf)) {
            executeCCNF(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.cdnf)) {
            executeCDNF(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.clear)) {
            executeClear(c, textArea, graphicArea);
        } else if ((c.getTypeCommand().equals(TypeCommand.def)) && c.getParams().length >= 1) {
            executeDefine(c, definedVars, definedFunctions, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.deffuncs)) {
            executeDefFuncs(definedFunctions, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.defvars)) {
            executeDefVars(definedVars, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.euler)) {
            executeEuler(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.expand)) {
            executeExpand(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.latex)) {
            executeLatex(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.pi)) {
            executePi(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.plot2d)) {
            if (params[0].contains("=")) {
                executeImplicitPlot2D(c, graphicMethods2D);
            } else {
                executePlot2D(c, graphicMethods2D);
            }
        } else if (c.getTypeCommand().equals(TypeCommand.plot3d)) {
            executePlot3D(c, graphicMethods3D);
        } else if (c.getTypeCommand().equals(TypeCommand.plotcurve) && c.getParams().length == 4) {
            executePlotCurve2D(c, graphicMethodsCurves2D);
        } else if (c.getTypeCommand().equals(TypeCommand.plotcurve) && c.getParams().length == 5) {
            executePlotCurve3D(c, graphicMethodsCurves3D);
        } else if (c.getTypeCommand().equals(TypeCommand.plotpolar)) {
            executePlotPolar2D(c, graphicMethodsPolar2D);
        } else if (c.getTypeCommand().equals(TypeCommand.solve)) {
            executeSolve(c, graphicMethods2D, textArea, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.solvedeq)) {
            executeSolveDEQ(c, graphicMethods2D, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.table)) {
            executeTable(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.tangent)) {
            executeTangent(c, graphicMethods2D, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.taylordeq)) {
            executeTaylorDEQ(c, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.undef)) {
            executeUndefine(c, definedVars, graphicArea);
        } else if (c.getTypeCommand().equals(TypeCommand.undefall)) {
            executeUndefineAll(definedVars, graphicArea);
        } else {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_INVALID_COMMAND"));
        }

        for (String out : output) {
            textArea.append(out);
        }

    }

    /**
     * Die folgenden Prozeduren führen einzelne Befehle aus. executePlot2D
     * zeichnet einen 2D-Graphen, executePlot3D zeichnet einen 3D-Graphen, etc.
     */
    private static void executeApprox(Command c, HashMap<String, Expression> definedVars, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        Expression expr = (Expression) c.getParams()[0];

        /**
         * Falls expr selbstdefinierte Funktionen enthält, dann zunächst expr so
         * darstellen, dass es nur vordefinierte Funktionen beinhaltet.
         */
        expr = expr.replaceSelfDefinedFunctionsByPredefinedFunctions();
        /**
         * Mit Werten belegte Variablen müssen durch ihren exakten Ausdruck
         * ersetzt werden.
         */
        for (String var : definedVars.keySet()) {
            expr = expr.replaceVariable(var, (Expression) definedVars.get(var));
        }

        expr = expr.turnToApproximate().simplify();
        /**
         * Dies dient dazu, dass alle Variablen wieder "präzise" sind. Sie
         * werden nur dann approximativ ausgegeben, wenn sie nicht präzise
         * (precise = false) sind.
         */
        Variable.setAllPrecise(true);

        /**
         * Textliche Ausgabe
         */
        output.add(expr.writeFormula(true) + " \n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(expr);

    }

    private static void executeClear(Command c, JTextArea area, GraphicArea graphicArea)
            throws ExpressionException {
        area.setText("");
        graphicArea.clearArea();
    }

    private static void executeDefine(Command c, HashMap<String, Expression> definedVars,
            HashMap<String, Expression> definedFunctions, GraphicArea graphicArea) throws ExpressionException, EvaluationException {

        /**
         * Falls ein Variablenwert definiert wird.
         */
        if (c.getParams().length == 2) {
            String var = (String) c.getParams()[0];
            Expression preciseExpression = ((Expression) c.getParams()[1]).simplify();
            Variable.setPreciseExpression(var, preciseExpression);
            definedVars.put(var, preciseExpression);
            if (((Expression) c.getParams()[1]).equals(preciseExpression)) {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2")
                        + preciseExpression.writeFormula(true)
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3")
                        + " \n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2"),
                        preciseExpression, Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3"));
            } else {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2")
                        + ((Expression) c.getParams()[1]).writeFormula(true) + " = " + preciseExpression.writeFormula(true)
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3")
                        + " \n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2"),
                        (Expression) c.getParams()[1], " = ", preciseExpression,
                        Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3"));
            }
        } else {
            /**
             * Falls eine Funktion definiert wird.
             */
            Object[] params = c.getParams();
            String function_name = (String) params[0];
            String[] vars = new String[params.length - 2];
            Expression[] exprs_for_vars = new Expression[params.length - 2];
            for (int i = 0; i < params.length - 2; i++) {
                vars[i] = (String) params[i + 1];
                exprs_for_vars[i] = Variable.create((String) params[i + 1]);
            }
            SelfDefinedFunction.abstractExpressionsForSelfDefinedFunctions.put(function_name, (Expression) c.getParams()[c.getParams().length - 1]);
            SelfDefinedFunction.innerExpressionsForSelfDefinedFunctions.put(function_name, exprs_for_vars);
            SelfDefinedFunction.varsForSelfDefinedFunctions.put(function_name, vars);
            definedFunctions.put(function_name, new SelfDefinedFunction(function_name, vars, (Expression) c.getParams()[c.getParams().length - 1], exprs_for_vars));

            /**
             * Ausgabe an den Benutzer.
             */
            String function;
            SelfDefinedFunction f = (SelfDefinedFunction) definedFunctions.get(function_name);
            String[] f_arguments = f.getArguments();
            Expression[] vars_for_output = new Expression[f.getArguments().length];
            Expression f_for_output;

            function = f.getName() + "(";
            for (int i = 0; i < f_arguments.length; i++) {
                /**
                 * Die Argumente in f haben alle "_ABSTRACT" als Anhängsel.
                 * Dieses wird nun beseitigt, um die Originalnamen
                 * wiederzubekommen. Die Variablen mit den Originalnamen werden
                 * im Array vars_for_output abgespechert.
                 */
                vars_for_output[i] = Variable.create(f_arguments[i].substring(0, f_arguments[i].indexOf("_ABSTRACT")));
                function = function + ((Variable) vars_for_output[i]).getName() + ",";
            }
            function = function.substring(0, function.length() - 1) + ")";
            f_for_output = f.replaceAllVariables(vars_for_output);

            /**
             * Textliche Ausgabe
             */
            output.add(Translator.translateExceptionMessage("MCC_FUNCTION_WAS_DEFINED") + function + " = "
                    + f_for_output.writeFormula(true) + "\n \n");
            /**
             * Grafische Ausgabe
             */
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_FUNCTION_WAS_DEFINED"),
                    function, " = ", f_for_output);

        }

    }

    private static void executeDefFuncs(HashMap<String, Expression> definedFunctions, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        String function;
        SelfDefinedFunction f;
        Expression[] vars_for_output;
        Expression f_for_output;

        /**
         * Textliche Ausgabe
         */
        output.add(Translator.translateExceptionMessage("MCC_LIST_OF_DEFINED_FUNCTIONS") + "\n \n");
        /**
         * Grafische Ausgabe
         */
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_LIST_OF_DEFINED_FUNCTIONS"));

        /**
         * Alle selbstdefinierten Funktionen nacheinander ausgeben.
         */
        if (!definedFunctions.isEmpty()) {
            for (String function_name : definedFunctions.keySet()) {

                function = "";
                f = (SelfDefinedFunction) definedFunctions.get(function_name);
                function = function + f.getName() + "(";
                for (int i = 0; i < f.getArguments().length; i++) {
                    function = function + "X_" + (i + 1) + ",";
                }
                function = function.substring(0, function.length() - 1) + ")";

                vars_for_output = new Expression[f.getArguments().length];
                for (int i = 0; i < f.getArguments().length; i++) {
                    vars_for_output[i] = Variable.create("X_" + (i + 1));
                }
                f_for_output = f.replaceAllVariables(vars_for_output);

                /**
                 * Textliche Ausgabe
                 */
                output.add(function + " = " + f_for_output.writeFormula(true) + "\n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(function, " = ", f_for_output);

            }
        }

    }

    private static void executeDefVars(HashMap<String, Expression> definedVars, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        if (!definedVars.isEmpty()) {
            for (String var : definedVars.keySet()) {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_LIST_OF_VARIABLES")
                        + var + " = " + ((Expression) definedVars.get(var)).writeFormula(true) + "\n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(var, " = ", (Expression) definedVars.get(var));
            }
        }

    }

    private static void executeCCNF(Command c, GraphicArea graphicArea) throws ExpressionException, EvaluationException {

        LogicalExpression log_expr = (LogicalExpression) c.getParams()[0];
        HashSet vars = new HashSet();
        log_expr.getContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF_1")
                    + log_expr.writeFormula()
                    + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF_2"));
        }

        LogicalExpression log_expr_in_ccnf = log_expr.toCCNF();
        /**
         * Textliche Ausgabe
         */
        output.add(log_expr_in_ccnf.writeFormula() + " \n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(log_expr_in_ccnf);

    }

    private static void executeCDNF(Command c, GraphicArea graphicArea) throws ExpressionException, EvaluationException {

        LogicalExpression log_expr = (LogicalExpression) c.getParams()[0];
        HashSet vars = new HashSet();
        log_expr.getContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF_1")
                    + log_expr.writeFormula()
                    + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF_2"));
        }

        LogicalExpression log_expr_in_cdnf = log_expr.toCDNF();
        /**
         * Textliche Ausgabe
         */
        output.add(log_expr_in_cdnf.writeFormula() + " \n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(log_expr_in_cdnf);

    }

    private static void executeEuler(Command c, GraphicArea graphicArea) throws ExpressionException {

        BigDecimal e = AnalysisMethods.e((int) c.getParams()[0]);
        /**
         * Textliche Ausgabe
         */
        output.add(Translator.translateExceptionMessage("MCC_DIGITS_OF_E_1")
                + (int) c.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_E_2")
                + e.toString() + "\n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_DIGITS_OF_E_1")
                + (int) c.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_E_2")
                + e.toString());

    }

    private static void executeExpand(Command c, GraphicArea graphicArea) throws ExpressionException, EvaluationException {
        /**
         * Es wird definiert, welche Arten von Vereinfachungen durchgeführt
         * werden müssen.
         */
        HashSet simplify = new HashSet();
        simplify.add(TypeSimplify.simplify_trivial);
        simplify.add(TypeSimplify.sort_difference_and_division);
        simplify.add(TypeSimplify.expand);
        simplify.add(TypeSimplify.collect_products);
        simplify.add(TypeSimplify.factorize_rationals_in_sums);
        simplify.add(TypeSimplify.factorize_rationals_in_differences);
        simplify.add(TypeSimplify.reduce_quotients);
        simplify.add(TypeSimplify.reduce_leadings_coefficients);
        simplify.add(TypeSimplify.simplify_algebraic_expressions);
        simplify.add(TypeSimplify.simplify_powers);
        simplify.add(TypeSimplify.simplify_functional_relations);
        simplify.add(TypeSimplify.order_sums_and_products);

        Expression expr = (Expression) c.getParams()[0];
        expr = expr.simplify(simplify);
        /**
         * Textliche Ausgabe
         */
        output.add(expr.writeFormula(true) + "\n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(expr);
    }

    private static void executeLatex(Command c, GraphicArea graphicArea) throws ExpressionException {

        String latex_code = Translator.translateExceptionMessage("MCC_LATEX_CODE");
        for (int i = 0; i < c.getParams().length - 1; i++) {
            if (c.getParams()[i] == null) {
                latex_code = latex_code + " = ";
            } else {
                latex_code = latex_code + ((Expression) c.getParams()[i]).expressionToLatex(true) + " = ";
            }
        }
        if (c.getParams()[c.getParams().length - 1] == null) {
            latex_code = latex_code + "\n \n";
        } else {
            latex_code = latex_code + ((Expression) c.getParams()[c.getParams().length - 1]).expressionToLatex(true) + "\n \n";
        }

        /**
         * Texttliche Ausgabe
         */
        output.add(latex_code);
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(latex_code);

    }

    private static void executePi(Command c, GraphicArea graphicArea) throws ExpressionException {

        BigDecimal pi = AnalysisMethods.pi((int) c.getParams()[0]);
        /**
         * Textliche Ausgabe
         */
        output.add(Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_1")
                + (int) c.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_2")
                + pi.toString() + "\n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_1")
                + (int) c.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_2")
                + pi.toString());

    }

    private static void executePlot2D(Command c, GraphicMethods2D graphicMethods2D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression[] exprs = new Expression[c.getParams().length - 2];
        for (int i = 0; i < c.getParams().length - 2; i++) {
            exprs[i] = (Expression) c.getParams()[i];
            exprs[i] = exprs[i].simplify();
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
        graphicMethods2D.setIsExplicit(true);
        graphicMethods2D.setIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        for (int i = 0; i < c.getParams().length - 2; i++) {
            graphicMethods2D.addExpression(exprs[i]);
        }
        graphicMethods2D.setVar(var);
        graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate());
        graphicMethods2D.expressionToGraph(var, x_0.evaluate(), x_1.evaluate());
        graphicMethods2D.setSpecialPointsOccur(false);
        graphicMethods2D.drawGraph2D();

    }

    private static void executePlot3D(Command c, GraphicMethods3D graphicMethods3D) throws ExpressionException,
            EvaluationException {

        HashSet vars = new HashSet();
        Expression expr = (Expression) c.getParams()[0];
        expr = expr.simplify();
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
        graphicMethods2D.setIsExplicit(false);
        graphicMethods2D.setIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addExpression(expr);
        graphicMethods2D.setVars(var1_alphabetical, var2_alphabetical);
        graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate(), y_0.evaluate(), y_1.evaluate());
        graphicMethods2D.setSpecialPointsOccur(false);
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
        expr[0] = expr[0].simplify();
        expr[0].getContainedVars(vars);
        expr[1] = (Expression) c.getParams()[1];
        expr[1].getContainedVars(vars);
        expr[1] = expr[1].simplify();

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
        expr[0] = expr[0].simplify();
        expr[1] = (Expression) c.getParams()[1];
        expr[1].getContainedVars(vars);
        expr[1] = expr[1].simplify();
        expr[2] = (Expression) c.getParams()[2];
        expr[2].getContainedVars(vars);
        expr[2] = expr[2].simplify();

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
            exprs[i] = exprs[i].simplify();
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

    private static void executeSolve(Command c, GraphicMethods2D graphicMethods2D, JTextArea area, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        HashSet vars = new HashSet();
        Expression expr_1 = (Expression) c.getParams()[0];
        Expression expr_2 = (Expression) c.getParams()[1];

        if (c.getParams().length <= 3) {

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

            HashMap<Integer, Expression> zeros = SolveMethods.solveGeneralEquation(expr_1, expr_2, var);

            /**
             * Falls keine Lösungen ermittelt werden konnten, User informieren.
             */
            if (zeros.isEmpty()) {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND") + " \n \n");
                /**
                 * Graphische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND"));

                return;
            }

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
                    message_about_free_parameters = message_about_free_parameters
                            + Translator.translateExceptionMessage("MCC_IS_ARBITRARY_INTEGER") + " \n \n";
                } else {
                    message_about_free_parameters = message_about_free_parameters
                            + Translator.translateExceptionMessage("MCC_ARE_ARBITRARY_INTEGERS") + " \n \n";
                }

            }

            /**
             * Textliche Ausgabe
             */
            output.add(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION")
                    + ((Expression) c.getParams()[0]).writeFormula(true)
                    + " = "
                    + ((Expression) c.getParams()[1]).writeFormula(true) + ": \n \n");
            for (int i = 0; i < zeros.size(); i++) {
                output.add(var + "_" + (i + 1) + " = " + zeros.get(i).writeFormula(true) + "\n \n");
            }
            /**
             * Grafische Ausgabe
             */
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION"), (Expression) c.getParams()[0],
                    " = ", (Expression) c.getParams()[1], " :");
            for (int i = 0; i < zeros.size(); i++) {
                graphicArea.addComponent(var + "_" + (i + 1) + " = ", zeros.get(i));
            }

            if (contains_free_parameter) {

                /**
                 * Textliche Ausgabe
                 */
                output.add(message_about_free_parameters);
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(message_about_free_parameters);

            }

        } else {

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
             * Falls die Anzahl der Unterteilungen nicht angegeben wird, so soll
             * das Intervall in 10000 Teile unterteilt werden.
             */
            int n = 10000;

            if (c.getParams().length == 5) {
                n = (int) c.getParams()[4];
            }

            if (expr instanceof Constant) {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION")
                        + ((Expression) c.getParams()[0]).writeFormula(true)
                        + " = "
                        + ((Expression) c.getParams()[1]).writeFormula(true) + ": \n \n");
                if (expr.equals(Expression.ZERO)) {
                    output.add(Translator.translateExceptionMessage("MCC_ALL_REALS") + " \n \n");
                } else {
                    output.add(Translator.translateExceptionMessage("MCC_EQUATIONS_HAS_NO_SOLUTIONS") + " \n \n");
                }
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION"), (Expression) c.getParams()[0],
                        " = ", (Expression) c.getParams()[1], " :");
                if (expr.equals(Expression.ZERO)) {
                    graphicArea.addComponent(Translator.translateExceptionMessage("MCC_ALL_REALS"));
                } else {
                    graphicArea.addComponent(Translator.translateExceptionMessage("MCC_EQUATIONS_HAS_NO_SOLUTIONS"));
                }

                /**
                 * Graphen der linken und der rechten Seite zeichnen.
                 */
                graphicMethods2D.setIsInitialized(true);
                graphicMethods2D.setIsExplicit(true);
                graphicMethods2D.setIsFixed(false);
                graphicMethods2D.clearExpressionAndGraph();
                graphicMethods2D.addExpression(expr_1);
                graphicMethods2D.addExpression(expr_2);
                graphicMethods2D.setVar(var);
                graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate());
                graphicMethods2D.expressionToGraph(var, x_0.evaluate(), x_1.evaluate());
                graphicMethods2D.setSpecialPointsOccur(false);
                graphicMethods2D.drawGraph2D();
                return;

            }

            HashMap<Integer, Double> zeros = NumericalMethods.solveEquation(expr, var, x_0.evaluate(), x_1.evaluate(), n);

            /**
             * Textliche Ausgabe
             */
            output.add(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION")
                    + ((Expression) c.getParams()[0]).writeFormula(true)
                    + " = "
                    + ((Expression) c.getParams()[1]).writeFormula(true) + ": \n \n");
            /**
             * Grafische Ausgabe
             */
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION"), (Expression) c.getParams()[0],
                    " = ", (Expression) c.getParams()[1], " :");

            for (int i = 0; i < zeros.size(); i++) {
                /**
                 * Textliche Ausgabe
                 */
                output.add(var + "_" + (i + 1) + " = " + zeros.get(i) + "\n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(var + "_" + (i + 1) + " = " + zeros.get(i));
            }

            if (zeros.isEmpty()) {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_NO_SOLUTIONS_OF_EQUATION_FOUND") + " \n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_NO_SOLUTIONS_OF_EQUATION_FOUND"));
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

            /**
             * Graphen der linken und der rechten Seite zeichnen, inkl. der
             * Lösungen (als rot markierte Punkte).
             */
            graphicMethods2D.setIsInitialized(true);
            graphicMethods2D.setIsExplicit(true);
            graphicMethods2D.setIsFixed(false);
            graphicMethods2D.clearExpressionAndGraph();
            graphicMethods2D.addExpression(expr_1);
            graphicMethods2D.addExpression(expr_2);
            graphicMethods2D.setVar(var);
            graphicMethods2D.computeScreenSizes(x_0.evaluate(), x_1.evaluate());
            graphicMethods2D.expressionToGraph(var, x_0.evaluate(), x_1.evaluate());
            graphicMethods2D.setSpecialPointsOccur(true);
            graphicMethods2D.setSpecialPoints(zeros_as_array);
            graphicMethods2D.drawGraph2D();

        }

    }

    private static void executeSolveDEQ(Command c, GraphicMethods2D graphicMethods2D, GraphicArea graphicArea)
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
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_1")
                            + ord
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_2")
                            + (ord - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_3"));
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
         * Zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt
         * werden. Falls dieser nicht eindeutig ist, wird er kanonisch als "y"
         * vergeben.
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

        double[][] solution_of_deq = NumericalMethods.solveDifferentialEquation(expr.simplify(), var_1, var_2, ord, x_0.evaluate(), x_1.evaluate(), y_0_as_double, 1000);

        /**
         * Formulierung und Ausgabe des AWP.
         */
        String formulation_of_AWP = Translator.translateExceptionMessage("MCC_SOLUTION_OF_DEQ") + var_2;
        ArrayList formulation_of_AWP_for_graphicArea = new ArrayList();

        for (int i = 0; i < ord; i++) {
            formulation_of_AWP = formulation_of_AWP + "'";
        }

        formulation_of_AWP_for_graphicArea.add(formulation_of_AWP + "(" + var_1 + ") = ");
        formulation_of_AWP_for_graphicArea.add(expr);
        formulation_of_AWP = formulation_of_AWP + "(" + var_1 + ") = " + expr.writeFormula(true);

        String var_2_with_primes;
        for (int i = 0; i < ord; i++) {
            formulation_of_AWP = formulation_of_AWP + ", " + var_2;
            formulation_of_AWP_for_graphicArea.add(", ");
            var_2_with_primes = var_2;
            for (int j = 0; j < i; j++) {
                formulation_of_AWP = formulation_of_AWP + "'";
                var_2_with_primes = var_2_with_primes + "'";
            }

            formulation_of_AWP = formulation_of_AWP + "(" + x_0.writeFormula(true) + ") = ";
            formulation_of_AWP = formulation_of_AWP + y_0[i].writeFormula(true);
            formulation_of_AWP_for_graphicArea.add(var_2_with_primes);
            formulation_of_AWP_for_graphicArea.add(TypeBracket.BRACKET_SURROUNDING_EXPRESSION);
            formulation_of_AWP_for_graphicArea.add(x_0);
            formulation_of_AWP_for_graphicArea.add(" = ");
            formulation_of_AWP_for_graphicArea.add(y_0[i]);
        }

        formulation_of_AWP = formulation_of_AWP + ", " + x_0.writeFormula(true) + " \u2264 " + var_1 + " \u2264 " + x_1.writeFormula(true) + ": \n \n";
        formulation_of_AWP_for_graphicArea.add(", ");
        formulation_of_AWP_for_graphicArea.add(x_0);
        formulation_of_AWP_for_graphicArea.add(" \u2264 ");
        formulation_of_AWP_for_graphicArea.add(var_1);
        formulation_of_AWP_for_graphicArea.add(" \u2264 ");
        formulation_of_AWP_for_graphicArea.add(x_1);
        formulation_of_AWP_for_graphicArea.add(":");

        /**
         * Textliche Ausgabe
         */
        output.add(formulation_of_AWP);
        /**
         * Grafische Ausgabe
         */
        graphicArea.addComponent(formulation_of_AWP_for_graphicArea);

        /**
         * Lösungen ausgeben.
         */
        for (double[] solution : solution_of_deq) {
            /**
             * Textliche Ausgabe
             */
            output.add(var_1 + " = " + solution[0] + "; " + var_2 + " = " + solution[1] + "\n \n");
            /**
             * Grafische Ausgabe
             */
            graphicArea.addComponent(var_1 + " = " + solution[0] + "; " + var_2 + " = " + solution[1]);
        }
        if (solution_of_deq.length < 1001) {
            /**
             * Falls die Lösung innerhalb des Berechnungsbereichs
             * unendlich/undefiniert ist.
             */
            output.add(Translator.translateExceptionMessage("MCC_SOLUTION_OF_DEQ_NOT_DEFINED_IN_POINT")
                    + (x_0.evaluate() + (solution_of_deq.length) * (x_1.evaluate() - x_0.evaluate()) / 1000)
                    + ". \n \n");
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTION_OF_DEQ_NOT_DEFINED_IN_POINT")
                    + (x_0.evaluate() + (solution_of_deq.length) * (x_1.evaluate() - x_0.evaluate()) / 1000)
                    + ".");
        }

        /**
         * Lösungsgraphen zeichnen.
         */
        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setIsExplicit(true);
        graphicMethods2D.setIsFixed(true);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addGraph(solution_of_deq);
        graphicMethods2D.setVars(var_1, var_2);
        graphicMethods2D.computeScreenSizes();
        graphicMethods2D.setSpecialPointsOccur(false);
        graphicMethods2D.drawGraph2D();

    }

    private static void executeTable(Command c, GraphicArea graphicArea) throws EvaluationException {

        LogicalExpression log_expr = (LogicalExpression) c.getParams()[0];
        HashSet vars = new HashSet();
        log_expr.getContainedVars(vars);
        int n = vars.size();
        if (n > 20) {
            throw new EvaluationException(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES_1")
                    + log_expr.writeFormula()
                    + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES_2"));
        }

        /**
         * Textliche Ausgabe
         */
        output.add(Translator.translateExceptionMessage("MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION") + log_expr.writeFormula() + ": \n \n");
        /**
         * Grafische Ausgabe
         */
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION"), log_expr);

        /**
         * Falls es sich um einen konstanten Ausdruck handelt.
         */
        if (n == 0) {
            boolean value = log_expr.evaluate();
            if (value) {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + log_expr.writeFormula()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2") + " \n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + log_expr.writeFormula()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2"));
            } else {
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + log_expr.writeFormula()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3") + " \n \n");
                /**
                 * Grafische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + log_expr.writeFormula()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3"));
            }
            return;
        }

        /**
         * Für die Geschwindigkeit der Tabellenberechnung: log_expr
         * vereinfachen.
         */
        log_expr = log_expr.simplify();

        /**
         * Nummerierung der logischen Variablen.
         */
        HashMap<Integer, String> vars_enumerated = new HashMap<>();

        Iterator iter = vars.iterator();
        for (int i = 0; i < vars.size(); i++) {
            vars_enumerated.put(vars_enumerated.size(), (String) iter.next());
        }

        int table_length = BigInteger.valueOf(2).pow(n).intValue();

        /**
         * Textliche Ausgabe
         */
        output.add(Translator.translateExceptionMessage("MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION") + log_expr.writeFormula() + ": \n \n");
        /**
         * Grafische Ausgabe
         */
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION"), log_expr);

        String vars_ordered = Translator.translateExceptionMessage("MCC_ORDER_OF_VARIABLES_IN_TABLE");
        for (int i = 0; i < vars_enumerated.size(); i++) {
            vars_ordered = vars_ordered + vars_enumerated.get(i) + ", ";
        }
        vars_ordered = vars_ordered.substring(0, vars_ordered.length() - 2) + " \n \n";

        /**
         * Textliche Ausgabe
         */
        output.add(vars_ordered);
        /**
         * Grafische Ausgabe
         */
        graphicArea.addComponent(vars_ordered);

        /**
         * Erstellung eines Binärcounters zum Durchlaufen aller möglichen
         * Belegungen der Variablen in vars.
         */
        boolean[] vars_values = new boolean[vars.size()];
        boolean current_value;

        String binary_counter;
        for (int i = 0; i < table_length; i++) {

            binary_counter = "(";
            for (int j = 0; j < vars.size(); j++) {
                if (vars_values[j]) {
                    binary_counter = binary_counter + "1, ";
                } else {
                    binary_counter = binary_counter + "0, ";
                }
                LogicalVariable.setValue(vars_enumerated.get(j), vars_values[j]);
            }
            binary_counter = binary_counter.substring(0, binary_counter.length() - 2) + "): ";

            current_value = log_expr.evaluate();
            if (current_value) {
                binary_counter = binary_counter + "1 \n";
            } else {
                binary_counter = binary_counter + "0 \n";
            }

            /**
             * Textliche Ausgabe
             */
            output.add(binary_counter);
            /**
             * Grafische Ausgabe
             */
            graphicArea.addComponent(binary_counter);

            vars_values = LogicalExpression.binaryCounter(vars_values);

            /**
             * Am Ende der Tabelle: Leerzeile lassen.
             */
            if (i == table_length - 1) {
                output.add("\n");
            }

        }

    }

    private static void executeTangent(Command c, GraphicMethods2D graphicMethods2D, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        Expression expr = (Expression) c.getParams()[0];
        HashMap<String, Expression> vars = (HashMap<String, Expression>) c.getParams()[1];

        String tangent_announcement = Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_1")
                + expr.writeFormula(true)
                + Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_2");
        ArrayList tangent_announcement_for_graphicArea = new ArrayList();
        tangent_announcement_for_graphicArea.add(Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_1"));
        tangent_announcement_for_graphicArea.add(expr);
        tangent_announcement_for_graphicArea.add(Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_2"));

        for (String var : vars.keySet()) {
            tangent_announcement = tangent_announcement + var + " = " + vars.get(var).writeFormula(true) + ", ";
            tangent_announcement_for_graphicArea.add(var + " = ");
            tangent_announcement_for_graphicArea.add(vars.get(var));
            tangent_announcement_for_graphicArea.add(", ");
        }
        /**
         * In der textlichen und in der grafischen Ausgabe das letzte
         * (überflüssige) Komma entfernen.
         */
        tangent_announcement = tangent_announcement.substring(0, tangent_announcement.length() - 2) + ": \n \n";
        tangent_announcement_for_graphicArea.remove(tangent_announcement_for_graphicArea.size() - 1);
        tangent_announcement_for_graphicArea.add(":");

        Expression tangent = AnalysisMethods.getTangentSpace(expr.simplify(), vars);

        /**
         * Textliche Ausgabe
         */
        output.add(tangent_announcement);
        output.add("Y = " + tangent.writeFormula(true) + "\n \n");
        /**
         * Grafische Ausgabe
         */
        graphicArea.addComponent(tangent_announcement_for_graphicArea);
        graphicArea.addComponent("Y = ", tangent);

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

            /**
             * Im Falle einer Veränderlichen: den Graphen der Funktion und die
             * Tangente zeichnen.
             */
            graphicMethods2D.setIsInitialized(true);
            graphicMethods2D.setIsExplicit(true);
            graphicMethods2D.setIsFixed(false);
            graphicMethods2D.clearExpressionAndGraph();
            graphicMethods2D.addExpression(expr);
            graphicMethods2D.addExpression(tangent);
            graphicMethods2D.setVar(var);
            graphicMethods2D.computeScreenSizes(x_0, x_1);
            graphicMethods2D.expressionToGraph(var, x_0, x_1);
            graphicMethods2D.setSpecialPointsOccur(true);
            graphicMethods2D.setSpecialPoints(tangent_point);
            graphicMethods2D.drawGraph2D();

        }

    }

    private static void executeTaylorDEQ(Command c, GraphicArea graphicArea) throws ExpressionException, EvaluationException {

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
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_1")
                            + ord
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_2")
                            + (ord - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_3"));
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

        Expression result = AnalysisMethods.getTaylorPolynomialFromDifferentialEquation(expr, var_1, var_2, ord, x_0, y_0, k);
        /**
         * Textliche Ausgabe
         */
        output.add(var_2 + "(" + var_1 + ") = " + result.writeFormula(true) + "\n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(var_2 + "(" + var_1 + ") = ", result);

    }

    private static void executeUndefine(Command c, HashMap definedVars, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        Object[] vars = c.getParams();
        for (int i = 0; i < vars.length; i++) {
            if (definedVars.containsKey((String) vars[i])) {
                definedVars.remove((String) vars[i]);
                /**
                 * Textliche Ausgabe
                 */
                output.add(Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_1")
                        + (String) vars[i]
                        + Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_2") + " \n \n");
                /**
                 * Graphische Ausgabe
                 */
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_1")
                        + (String) vars[i]
                        + Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_2"));
            }
        }

    }

    private static void executeUndefineAll(HashMap definedVars, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        definedVars.clear();
        /**
         * Textliche Ausgabe
         */
        output.add(Translator.translateExceptionMessage("MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN") + " \n \n");
        /**
         * Graphische Ausgabe
         */
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN"));

    }

}
