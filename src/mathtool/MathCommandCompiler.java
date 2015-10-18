package mathtool;

import command.Command;
import command.TypeCommand;
import computation.AnalysisMethods;
import computation.NumericalMethods;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import expressionbuilder.Constant;
import expressionbuilder.Expression;
import expressionbuilder.SelfDefinedFunction;
import expressionbuilder.TypeFunction;
import expressionbuilder.TypeOperator;
import expressionbuilder.TypeSimplify;
import expressionbuilder.Variable;
import expressionsimplifymethods.ExpressionCollection;
import graphic.GraphicArea;
import graphic.GraphicPanel2D;
import graphic.GraphicPanel3D;
import graphic.GraphicPanelCurves2D;
import graphic.GraphicPanelCurves3D;
import graphic.GraphicPanelPolar2D;
import graphic.TypeBracket;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextArea;
import linearalgebraalgorithms.EigenvaluesEigenvectorsAlgorithms;
import linearalgebraalgorithms.GaussAlgorithm;
import logicalexpressionbuilder.LogicalExpression;
import logicalexpressionbuilder.LogicalVariable;
import matrixexpressionbuilder.Matrix;
import matrixexpressionbuilder.MatrixExpression;
import matrixsimplifymethods.MatrixExpressionCollection;
import solveequationmethods.SolveMethods;
import translator.Translator;

public class MathCommandCompiler {

    /**
     * Hier werden Berechnungsergebnisse/zusätzliche
     * Hinweise/Meldungen/Warnungen etc. gespeichert, die dem Benutzer nach
     * Beenden der Befehlsausführung mitgeteilt werden.
     */
    private static final ArrayList<String> output = new ArrayList<>();

    /**
     * Diese Funktion wird zum Prüfen für die Vergabe neuer Funktionsnamen
     * benötigt. Sie prüft nach, ob name keine bereits definierte Funktion,
     * Operator oder Befehl ist. Ferner dürfen die Zeichen + - * / ^ nicht
     * enthalten sein.
     */
    private static boolean isForbiddenName(String name) {

        // Prüfen, ob nicht geschützte Funktionen (wie z.B. sin, tan etc.) überschrieben werden.
        for (TypeFunction protectedFunction : TypeFunction.values()) {
            if (protectedFunction.toString().equals(name)) {
                return false;
            }
        }
        // Prüfen, ob nicht geschützte Operatoren (wie z.B. taylor, int etc.) überschrieben werden.
        String operatorName;
        for (TypeOperator protectedOperator : TypeOperator.values()) {
            if (protectedOperator.equals(TypeOperator.integral)) {
                operatorName = "int";
            } else {
                operatorName = protectedOperator.toString();
            }
            if (operatorName.equals(name)) {
                return false;
            }
        }
        // Prüfen, ob nicht geschützte Befehle (wie z.B. approx etc.) überschrieben werden.
        for (TypeCommand protectedCommand : TypeCommand.values()) {
            if (protectedCommand.toString().equals(name)) {
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
        for (int i = 0; i < name.length(); i++) {
            if (!((int) name.charAt(i) >= 48 && (int) name.charAt(i) < 57) && !((int) name.charAt(i) >= 97 && (int) name.charAt(i) < 122)) {
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
     *
     * @throws ExpressionException, EvaluationException
     */
    public static Command getCommand(String command, String[] params) throws ExpressionException {

        switch (command) {
            case "approx":
                return getCommandApprox(params);
            case "ccnf":
                return getCommandCCNF(params);
            case "cdnf":
                return getCommandCDNF(params);
            case "clear":
                return getCommandClear(params);
            case "def":
                return getCommandDef(params);
            case "deffuncs":
                return getCommandDefFuncs(params);
            case "defvars":
                return getCommandDefVars(params);
            case "eigenvalues":
                return getCommandEigenvalues(params);
            case "eigenvectors":
                return getCommandEigenvectors(params);
            case "euler":
                return getCommandEuler(params);
            case "expand":
                return getCommandExpand(params);
            case "ker":
                return getCommandKer(params);
            case "latex":
                return getCommandLatex(params);
            case "pi":
                return getCommandPi(params);
            case "plot2d":
                return getCommandPlot2D(params);
            case "plot3d":
                return getCommandPlot3D(params);
            case "plotcurve":
                return getCommandPlotCurve(params);
            case "plotpolar":
                return getCommandPlotPolar(params);
            case "solve":
                return getCommandSolve(params);
            case "solvedeq":
                return getCommandSolveDEQ(params);
            case "table":
                return getCommandTable(params);
            case "tangent":
                return getCommandTangent(params);
            case "taylordeq":
                return getCommandTaylorDEQ(params);
            case "undef":
                return getCommandUndef(params);
            case "undefall":
                return getCommandUndelAll(params);
            // Sollte theoretisch nie vorkommen.
            default:
                return new Command();
        }

    }

    private static Command getCommandApprox(String[] params) throws ExpressionException {

        // Struktur: approx(expr)
        Object[] commandParams = new Object[1];

        // Prüft, ob der Befehl genau einen Parameter besitzt.
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_APPROX"));
        }

        try {
            commandParams[0] = Expression.build(params[0], null);
            return new Command(TypeCommand.approx, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_PARAMETER_IN_APPROX_IS_INVALID") + e.getMessage());
        }

    }

    private static Command getCommandCCNF(String[] params) throws ExpressionException {

        // Struktur: ccnf(LOGICALEXPRESSION). LOGICALEXPRESSION: Gültiger logischer Ausdruck.
        Object[] commandParams = new Object[1];

        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_CCNF"));
        }

        try {
            commandParams[0] = LogicalExpression.build(params[0], null);
            return new Command(TypeCommand.ccnf, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_CCNF"));
        }

    }

    private static Command getCommandCDNF(String[] params) throws ExpressionException {

        // Struktur: cdnf(LOGICALEXPRESSION). LOGICALEXPRESSION: Gültiger logischer Ausdruck.
        Object[] commandParams = new Object[1];

        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_CDNF"));
        }

        try {
            commandParams[0] = LogicalExpression.build(params[0], null);
            return new Command(TypeCommand.cdnf, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_CDNF"));
        }

    }

    private static Command getCommandClear(String[] params) throws ExpressionException {
        // Struktur: clear().
        // Prüft, ob der Befehl keine Parameter besitzt.
        if (params.length > 0) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_CLEAR"));
        }
        return new Command(TypeCommand.clear, new Object[0]);
    }

    private static Command getCommandDef(String[] params) throws ExpressionException {

        // Struktur: def(VAR = VALUE) oder def(FUNCTION(VAR_1, ..., VAR_n) = EXPRESSION(VAR_1, ..., VAR_n))
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEF"));
        }

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_NO_EQUAL_IN_DEF"));
        }

        String functionNameAndArguments = params[0].substring(0, params[0].indexOf("="));
        String functionTerm = params[0].substring(params[0].indexOf("=") + 1, params[0].length());

        /*
         Falls der linke Teil eine Variable ist, dann ist es eine
         Zuweisung, die dieser Variablen einen festen Wert zuweist.
         Beispiel: def(x = 2) liefert: result.name = "def" result.params =
         {"x"} result.left = 2 (als Expression)
         */
        if (Expression.isValidDerivateOfVariable(functionNameAndArguments) && !Expression.isPI(functionNameAndArguments)) {

            Expression preciseExpression;
            HashSet<String> vars = new HashSet<>();
            try {
                preciseExpression = Expression.build(functionTerm, vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_TO_VARIABLE_MUST_BE_ASSIGNED_REAL_VALUE"));
            }
            if (!vars.isEmpty()) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_TO_VARIABLE_MUST_BE_ASSIGNED_CONSTANT_REAL_VALUE"));
            }

            return new Command(TypeCommand.def, new Object[]{functionNameAndArguments, preciseExpression});

        }

        HashSet<String> vars = new HashSet<>();
        Expression expr;
        /*
         Nun wird geprüft, ob es sich um eine Funktionsdeklaration
         handelt. Zunächst wird versucht, den rechten Teilstring vom "="
         in einen Ausdruck umzuwandeln.
         */
        try {
            expr = Expression.build(functionTerm, vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_INVALID_EXPRESSION_ON_RIGHT_SIDE") + e.getMessage());
        }

        /*
         Hier werden noch einmal alle in expr vorkommenden Variablen neu
         ermittelt. GRUND: Falls exp ein Operator mit lokalen variablen
         ist (etwa Summe, Produkt, Integral), dann werden die lokalen
         Variablen in vars mitaufgenommen, und es kann später Exceptions
         geben, weil im Operator Variablen vorkommen, die in den
         Funktionsargumenten nicht vorkommen. beispiel: def(f(x) =
         sum(x^k,k,1,10)). k ist hier keine echte Variable, sondern nur
         eine Indexvariable, welche bei anwendung von getContainedVars()
         übergangen wird.
         */
        vars.clear();
        expr.addContainedVars(vars);

        /*
         WICHTIG! Falls expr bereits vom Benutzer vordefinierte Funktionen
         enthält (der Benutzer kann beispielsweise eine weitere Funktion
         mit Hilfe bereits definierter Funktionen definieren), dann werden
         hier alle neu definierten Funktionen durch vordefinierte
         Funktionen ersetzt.
         */
        expr = expr.replaceSelfDefinedFunctionsByPredefinedFunctions();

        // Funktionsnamen und Variablen auslesen.
        String functionName;
        String[] functionVars;
        try {
            // Funktionsname und Funktionsvariablen werden ermittelt.
            functionName = Expression.getOperatorAndArguments(functionNameAndArguments)[0];
            functionVars = Expression.getArguments(Expression.getOperatorAndArguments(functionNameAndArguments)[1]);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_INVALID_DEF"));
        }

        /*
         Falls functions_vars leer ist -> Fehler ausgeben (es muss
         mindestens eine Variable vorhanden sein).
         */
        if (functionVars.length == 0) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_IS_NO_FUNCTION_VARS_IN_FUNCTION_DECLARATION"));
        }

        // Wird geprüft, ob die einzelnen Parameter in der Funktionsklammer gültige Variablen sind.
        for (int i = 0; i < functionVars.length; i++) {
            if (!Expression.isValidDerivateOfVariable(functionVars[i])) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_IS_NOT_VALID_VARIABLE_1")
                        + functionVars[i]
                        + Translator.translateExceptionMessage("MCC_IS_NOT_VALID_VARIABLE_2"));
            }
        }

        // Wird geprüft, ob die Variablen in function_vars auch alle verschieden sind!
        HashSet<String> functionVarsAsHashset = new HashSet<>();
        for (String functionVar : functionVars) {
            if (functionVarsAsHashset.contains(functionVar)) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_DEF_1")
                        + functionName
                        + Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_DEF_2"));
            }
            functionVarsAsHashset.add(functionVar);
        }

        /*
         Hier wird den Variablen der Index "_ABSTRACT" angehängt. Dies
         dient der Kennzeichnung, dass diese Variablen Platzhalter für
         weitere Ausdrücke und keine echten Variablen sind. Solche
         Variablen können niemals in einem geparsten Ausdruck vorkommen,
         da der Parser Expression.build solche Variablen nicht akzeptiert.
         */
        for (int i = 0; i < functionVars.length; i++) {
            functionVars[i] = functionVars[i] + "_ABSTRACT";
        }

        // Prüfen, ob nicht geschützte Funktionen (wie z.B. sin, tan etc.) überschrieben werden.
        if (!isForbiddenName(functionName)) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_PROTECTED_FUNC_NAME_1")
                    + functionName
                    + Translator.translateExceptionMessage("MCC_PROTECTED_FUNC_NAME_2"));
        }

        // Prüfen, ob keine Sonderzeichen vorkommen.
        if (!checkForSpecialCharacters(functionName)) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS_1")
                    + functionName
                    + Translator.translateExceptionMessage("MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS_2"));
        }

        /*
         Prüfen, ob alle Variablen, die in expr auftreten, auch als
         Funktionsparameter vorhanden sind. Sonst -> Fehler ausgeben.
         Zugleich: Im Ausdruck expr werden alle Variablen der Form var
         durch Variablen der Form var_ABSTRACT ersetzt und alle Variablen
         im HashSet vars ebenfalls.
         */
        List<String> functionVarsAsList = Arrays.asList(functionVars);
        Iterator iter = vars.iterator();
        String var;
        for (int i = 0; i < vars.size(); i++) {
            var = (String) iter.next();
            expr = expr.replaceVariable(var, Variable.create(var + "_ABSTRACT"));
            var = var + "_ABSTRACT";
            if (!functionVarsAsList.contains(var)) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_RIGHT_SIDE_OF_DEF_CONTAINS_WRONG_VAR"));
            }
        }

        Object[] commandParams = new Object[2 + functionVars.length];
        commandParams[0] = functionName;
        for (int i = 1; i <= functionVars.length; i++) {
            commandParams[i] = functionVars[i - 1];
        }
        commandParams[1 + functionVars.length] = expr;

        /*
         Für das obige Beispiel def(f(x, y) = x^2+y) das Ergebnis result gilt dann:
         result.type = TypeCommand.def result.params = {"f", "x_ABSTRACT",
         "y_ABSTRACT"} result.left = x_ABSTRACT^2+y_ABSTRACT (als Expression).
         */
        return new Command(TypeCommand.def, commandParams);

    }

    private static Command getCommandDefFuncs(String[] params) throws ExpressionException {
        // Struktur: deffuncs().
        // Prüft, ob der Befehl keine Parameter besitzt.
        if (params.length > 0) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEFFUNCS"));
        }
        return new Command(TypeCommand.deffuncs, new Object[0]);
    }

    private static Command getCommandDefVars(String[] params) throws ExpressionException {
        // Struktur: defvars().
        // Prüft, ob der Befehl keine Parameter besitzt.
        if (params.length > 0) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEFVARS"));
        }
        return new Command(TypeCommand.defvars, new Object[0]);
    }

    private static Command getCommandEigenvalues(String[] params) throws ExpressionException {

        /* 
         Struktur: eigenvalues(MATRIXEXPRESSION), MATRIXEXPRESSION: gültiger
         Matrizenausdruck, der eine quadratische Matrix darstellt.
         */
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_EIGENVALUES"));
        }

        try {
            Object[] commandParams = new Object[1];
            commandParams[0] = MatrixExpression.build(params[0], null);
            // Testen, ob dieser Matrizenausdruck wohldefiniert und quadratisch ist.
            Dimension dim = ((MatrixExpression) commandParams[0]).getDimension();
            if (dim.height != dim.width) {
                throw new EvaluationException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVALUES"));
            }
            return new Command(TypeCommand.eigenvalues, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVALUES"));
        } catch (EvaluationException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVALUES_NOT_QUADRATIC"));
        }

    }

    private static Command getCommandEigenvectors(String[] params) throws ExpressionException {

        /* 
         Struktur: eigenvectors(MATRIXEXPRESSION), MATRIXEXPRESSION: gültiger
         Matrizenausdruck, der eine quadratische Matrix darstellt.
         */
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_EIGENVECTORS"));
        }

        try {
            Object[] commandParams = new Object[1];
            commandParams[0] = MatrixExpression.build(params[0], null);
            // Testen, ob dieser Matrizenausdruck wohldefiniert und quadratisch ist.
            Dimension dim = ((MatrixExpression) commandParams[0]).getDimension();
            if (dim.height != dim.width) {
                throw new EvaluationException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVECTORS"));
            }
            return new Command(TypeCommand.eigenvectors, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVECTORS"));
        } catch (EvaluationException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVECTORS_NOT_QUADRATIC"));
        }

    }

    private static Command getCommandEuler(String[] params) throws ExpressionException {

        /*
         Struktur: euler(int). int: nichtnegative ganze Zahl; bestimmt die
         Anzahl der Stellen, die von e ausgegeben werden sollen.
         */
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_EULER"));
        }

        // Zunächst prüfen, ob es sich um eine (evtl. viel zu große) ganze Zahl handelt.
        try {
            BigInteger numberOfDigits = new BigInteger(params[0]);
            if (numberOfDigits.compareTo(BigInteger.ZERO) < 0) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EULER"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EULER"));
        }

        try {
            Object[] commandParams = new Object[1];
            commandParams[0] = Integer.parseInt(params[0]);
            if ((int) commandParams[0] < 0) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EULER"));
            }
            return new Command(TypeCommand.euler, commandParams);
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_ENTER_SMALLER_NUMBER_IN_EULER"));
        }

    }

    private static Command getCommandExpand(String[] params) throws ExpressionException {

        // Struktur: expand(EXPRESSION). EXPRESSION: Gültiger Ausdruck.
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_EXPAND"));
        }

        try {
            Object[] commandParams = new Object[1];
            commandParams[0] = Expression.build(params[0], null);
            return new Command(TypeCommand.expand, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EXPAND"));
        }

    }

    private static Command getCommandKer(String[] params) throws ExpressionException {

        // Struktur: ker(MATRIXEXPRESSION). MATRIXEXPRESSION: Gültiger Matrizenausdruck.
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_KER"));
        }

        try {
            Object[] commandParams = new Object[1];
            commandParams[0] = MatrixExpression.build(params[0], null);
            return new Command(TypeCommand.ker, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_KER"));
        }

    }

    private static Command getCommandLatex(String[] params) throws ExpressionException {

        /*
         Struktur: latex(EXPRESSION) EXPRESSION: Ausdruck, welcher in einen
         Latex-Code umgewandelt werden soll.
         */
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
            HashSet<String> vars = new HashSet<>();
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
            return new Command(TypeCommand.latex, exprs);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_LATEX") + e.getMessage());
        }

    }

    private static Command getCommandPi(String[] params) throws ExpressionException {

        /*
         Struktur: pi(int). int: nichtnegative ganze Zahl; bestimmt die Anzahl
         der Stellen, die von pi ausgegeben werden sollen.
         */
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PI"));
        }

        // Zunächst prüfen, ob es sich um eine (evtl. viel zu große) ganze Zahl handelt.
        try {
            BigInteger numberOfDigits = new BigInteger(params[0]);
            if (numberOfDigits.compareTo(BigInteger.ZERO) < 0) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_PI"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_PI"));
        }

        try {
            Object[] commandParams = new Object[1];
            commandParams[0] = Integer.parseInt(params[0]);
            if ((int) commandParams[0] < 0) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_PI"));
            }
            return new Command(TypeCommand.pi, commandParams);
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_ENTER_SMALLER_NUMBER_IN_PI"));
        }

    }

    private static Command getCommandPlot2D(String[] params) throws ExpressionException {

        /*
         Struktur: plot2d(EXPRESSION_1(var), ..., EXPRESSION_n(var), value_1,
         value_2) EXPRESSION_i(var): Ausdruck in einer Variablen. value_1 <
         value_2: Grenzen des Zeichenbereichs ODER: plot2d(EXPRESSION_1(var1,
         var2) = EXPRESSION_2(var1, var2), value_1, value_2, value_3, value_4)
         (Plot der Lösungsmenge {EXPRESSION_1 = EXPRESSION_2}) EXPRESSION_1,
         EXPRESSION_2: Ausdrücke in höchstens zwei Variablen. value_1 <
         value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         Variablen werden dabei alphabetisch geordnet.
         */
        if (params.length < 3) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT2D"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        if (!params[0].contains("=")) {

            for (int i = 0; i < params.length - 2; i++) {
                try {
                    commandParams[i] = Expression.build(params[i], null);
                    ((Expression) commandParams[i]).addContainedVars(vars);
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

            HashSet<String> varsInLimits = new HashSet<>();
            try {
                commandParams[params.length - 2] = Expression.build(params[params.length - 2], null);
                ((Expression) commandParams[params.length - 2]).addContainedVars(varsInLimits);
                if (!varsInLimits.isEmpty()) {
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
                commandParams[params.length - 1] = Expression.build(params[params.length - 1], null);
                ((Expression) commandParams[params.length - 1]).addContainedVars(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_1")
                            + params.length
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_2"));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_1")
                        + params.length
                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D_2"));
            }

            return new Command(TypeCommand.plot2d, commandParams);

        } else {

            if (params.length != 5) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_IMPLICIT_PLOT2D"));
            }

            commandParams = new Object[6];

            try {
                commandParams[0] = Expression.build(params[0].substring(0, params[0].indexOf("=")), null);
                commandParams[1] = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), null);
                ((Expression) commandParams[0]).addContainedVars(vars);
                ((Expression) commandParams[1]).addContainedVars(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_IMPLICIT_PLOT2D") + e.getMessage());
            }

            if (vars.size() > 2) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_IMPLICIT_PLOT2D_1")
                        + String.valueOf(vars.size())
                        + Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_IMPLICIT_PLOT2D_2"));
            }

            HashSet<String> varsInLimits = new HashSet<>();
            for (int i = 1; i <= 4; i++) {
                try {
                    commandParams[i + 1] = Expression.build(params[i], null);
                    ((Expression) commandParams[i + 1]).addContainedVars(varsInLimits);
                    if (!varsInLimits.isEmpty()) {
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

            return new Command(TypeCommand.plot2d, commandParams);

        }

    }

    private static Command getCommandPlot3D(String[] params) throws ExpressionException {

        /*
         Struktur: plot3d(EXPRESSION(var1, var2), value_1, value_2, value_3,
         value_4) EXPRESSION: Ausdruck in höchstens zwei Variablen. value_1 <
         value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         Variablen werden dabei alphabetisch geordnet.
         */
        if (params.length != 5) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOT3D"));
        }

        Object[] commandParams = new Object[5];
        HashSet<String> vars = new HashSet<>();

        try {
            commandParams[0] = Expression.build(params[0], null);
            ((Expression) commandParams[0]).addContainedVars(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOT3D") + e.getMessage());
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOT3D_1")
                    + String.valueOf(vars.size())
                    + Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOT3D_2"));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = 1; i <= 4; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedVars(varsInLimits);
                if (!varsInLimits.isEmpty()) {
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

        return new Command(TypeCommand.plot3d, commandParams);

    }

    private static Command getCommandPlotCurve(String[] params) throws ExpressionException {

        /*
         Struktur: plotcurve([FUNCTION_1(var), FUNCTION_2(var)], value_1,
         value_2). FUNCTION_i(var) = Funktion in einer Variablen. value_1 <
         value_2: Parametergrenzen. ODER: plotcurve([FUNCTION_1(var),
         FUNCTION_2(var), FUNCTION_3(var)], value_1, value_2). FUNCTION_i(var)
         = Funktion in einer Variablen. value_1 < value_2: Parametergrenzen.
         */
        if (params.length != 3) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE"));
        }

        HashSet<String> vars = new HashSet<>();

        /*
         Es wird nun geprüft, ob der erste Parameter die Form "(expr_1,
         expr_2)" oder "(expr_1, expr_2, expr_3)" besitzt.
         */
        if (!params[0].substring(0, 1).equals("(") || !params[0].substring(params[0].length() - 1, params[0].length()).equals(")")) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOTCURVE"));
        }

        String[] curveComponents = Expression.getArguments(params[0].substring(1, params[0].length() - 1));
        if (curveComponents.length != 2 && curveComponents.length != 3) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_CURVE_COMPONENTS_IN_PLOTCURVE"));
        }

        Object[] commandParams;
        if (curveComponents.length == 2) {
            commandParams = new Object[4];
        } else {
            commandParams = new Object[5];
        }

        for (int i = 0; i < curveComponents.length; i++) {
            try {
                commandParams[i] = Expression.build(curveComponents[i], null);
                ((Expression) commandParams[i]).addContainedVars(vars);
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

        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = 0; i <= 1; i++) {
            try {
                commandParams[curveComponents.length + i] = Expression.build(params[i + 1], null);
                ((Expression) commandParams[curveComponents.length + i]).addContainedVars(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_1")
                            + (i + 2)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_2"));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_1")
                        + (i + 2)
                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCURVE_2"));
            }
        }

        return new Command(TypeCommand.plotcurve, commandParams);

    }

    private static Command getCommandPlotPolar(String[] params) throws ExpressionException {

        /*
         Struktur: plotpolar(EXPRESSION_1(var), ..., EXPRESSION_n(var), value_1,
         value_2) EXPRESSION_i(var): Ausdruck in einer Variablen. value_1 <
         value_2: Grenzen des Zeichenbereichs ODER: PLOT(EXPRESSION_1(var1,
         var2) = EXPRESSION_2(var1, var2), value_1, value_2, value_3, value_4)
         (Plot der Lösungsmenge {EXPRESSION_1 = EXPRESSION_2}) EXPRESSION_1,
         EXPRESSION_2: Ausdrücke in höchstens zwei Variablen. value_1 <
         value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         Variablen werden dabei alphabetisch geordnet.
         */
        if (params.length < 3) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTPOLAR"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 2; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedVars(vars);
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

        HashSet<String> varsInLimits = new HashSet<>();
        try {
            commandParams[params.length - 2] = Expression.build(params[params.length - 2], null);
            ((Expression) commandParams[params.length - 2]).addContainedVars(varsInLimits);
            if (!varsInLimits.isEmpty()) {
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
            commandParams[params.length - 1] = Expression.build(params[params.length - 1], null);
            ((Expression) commandParams[params.length - 1]).addContainedVars(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_1")
                        + params.length
                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_2"));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_1")
                    + params.length
                    + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR_2"));
        }

        return new Command(TypeCommand.plotpolar, commandParams);

    }

    private static Command getCommandSolve(String[] params) throws ExpressionException {

        /*
         Struktur: solve(expr_1 = expr_2, x_1, x_2) ODER solve(expr_1 =
         expr_2, x_1, x_2, n) var = Variable in der GLeichung, x_1 und x_2
         legen den Lösungsbereich fest; n = Anzahl der Unterteilungen des
         Intervalls [x_1, x_2]
         */
        if (params.length < 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVE"));
        }
        if (params.length > 4) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_TOO_MANY_PARAMETERS_IN_SOLVE"));
        }
        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVE"));
        }

        Object[] commandParams;
        HashSet<String> vars = new HashSet<>();
        try {
            Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
            Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVE_WITH_REPORTED_ERROR") + e.getMessage());
        }

        Expression f = Expression.build(params[0].substring(0, params[0].indexOf("=")), vars);
        Expression g = Expression.build(params[0].substring(params[0].indexOf("=") + 1, params[0].length()), vars);

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
                commandParams = new Object[2];
                commandParams[0] = f;
                commandParams[1] = g;
            } else {
                commandParams = new Object[3];
                commandParams[0] = f;
                commandParams[1] = g;
                commandParams[2] = params[1];
            }

            return new Command(TypeCommand.solve, commandParams);

        } else {

            if (vars.size() > 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_SOLVE"));
            }

            HashSet<String> varsInLimits = new HashSet<>();
            for (int i = 1; i <= 2; i++) {
                try {
                    Expression.build(params[i], new HashSet<String>()).addContainedVars(varsInLimits);
                    if (!varsInLimits.isEmpty()) {
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

            Expression lowerLimit = Expression.build(params[1], vars);
            Expression upperLimit = Expression.build(params[2], vars);

            if (params.length == 3) {
                commandParams = new Object[4];
                commandParams[0] = f;
                commandParams[1] = g;
                commandParams[2] = lowerLimit;
                commandParams[3] = upperLimit;
            } else {
                int n = Integer.parseInt(params[3]);
                if (n < 1) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVE"));
                }
                commandParams = new Object[5];
                commandParams[0] = f;
                commandParams[1] = g;
                commandParams[2] = lowerLimit;
                commandParams[3] = upperLimit;
                commandParams[4] = n;
            }

            return new Command(TypeCommand.solve, commandParams);

        }

    }

    private static Command getCommandSolveDEQ(String[] params) throws ExpressionException {

        /*
         Struktur: solvedeq(EXPRESSION, var, ord, x_0, x_1, y_0, y'(0), ...,
         y^(ord - 1)(0)) EXPRESSION: Rechte Seite der DGL y^{(ord)} =
         EXPRESSION. Anzahl der parameter ist also = ord + 5 var = Variable in
         der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         fest x_1 = Obere x-Schranke für die numerische Berechnung
         */
        if (params.length < 6) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDEQ"));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[2]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_SOLVEDEQ"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_SOLVEDEQ"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt:
         Beispielsweise darf in einer DGL der ordnung 3 nicht y''',
         y'''' etc. auf der rechten Seite auftreten.
         */
        HashSet<String> vars = new HashSet<>();
        try {
            Expression.build(params[0], vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDEQ") + e.getMessage());
        }
        Expression expr = Expression.build(params[0], vars);

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        Iterator iter = vars.iterator();
        String varWithoutPrimes;
        for (int i = 0; i < vars.size(); i++) {
            varWithoutPrimes = (String) iter.next();
            if (!varWithoutPrimes.replaceAll("'", "").equals(params[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDEQ_1")
                            + ord
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDEQ_2")
                            + (ord - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDEQ_3"));
                }
                varWithoutPrimes = varWithoutPrimes.replaceAll("'", "");
            }
            varsWithoutPrimes.add(varWithoutPrimes);
        }

        if (varsWithoutPrimes.size() > 2) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDEQ"));
        }

        if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
            if (varsWithoutPrimes.size() == 2) {
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

        // Prüft, ob die AWP-Daten korrekt sind.
        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < ord + 5; i++) {
            try {
                Expression limit = Expression.build(params[i], varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_1")
                            + String.valueOf(i + 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_2"));
                }
                /*
                 Prüfen, ob die Grenzen ausgewerten werden können.
                 Dies ist notwendig, da es sich hier um eine
                 numerische Berechnung handelt (und nicht um eine
                 algebraische).
                 */
                limit.evaluate();
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_1")
                        + String.valueOf(i + 1)
                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDEQ_2"));
            }
        }

        Object[] commandParams = new Object[ord + 5];
        commandParams[0] = expr;
        commandParams[1] = params[1];
        commandParams[2] = ord;
        for (int i = 3; i < ord + 5; i++) {
            commandParams[i] = Expression.build(params[i], vars);
        }

        return new Command(TypeCommand.solvedeq, commandParams);

    }

    private static Command getCommandTable(String[] params) throws ExpressionException {

        // Struktur: table(LOGICALEXPRESSION) LOGICALEXPRESSION: Logischer Ausdruck.
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_TABLE"));
        }

        try {
            Object[] commandParams = new Object[1];
            commandParams[0] = LogicalExpression.build(params[0], null);
            return new Command(TypeCommand.table, commandParams);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_TABLE") + e.getMessage());
        }

    }

    private static Command getCommandTangent(String[] params) throws ExpressionException {

        /*
         Struktur: tangent(EXPRESSION, var_1 = value_1, ..., var_n = value_n)
         EXPRESSION: Ausdruck, welcher eine Funktion repräsentiert. var_i =
         Variable value_i = reelle Zahl. Es müssen alle Variablen unter den
         var_i vorkommen, welche auch in expr vorkommen.
         */
        if (params.length < 2) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TANGENT"));
        }

        HashSet<String> vars = new HashSet<>();
        Expression expr;
        try {
            expr = Expression.build(params[0], vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_TANGENT") + e.getMessage());
        }

        /*
         Ermittelt die Anzahl der Variablen, von denen die Funktion
         abhängt, von der der Tangentialraum berechnet werden soll.
         */
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
                Expression point = Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet<String>());
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

        // Es wird geprüft, ob keine Veränderlichen doppelt auftreten.
        for (int i = 1; i < params.length; i++) {
            for (int j = i + 1; j < params.length; j++) {
                if (params[i].substring(0, params[i].indexOf("=")).equals(params[j].substring(0, params[j].indexOf("=")))) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT_1")
                            + params[i].substring(0, params[i].indexOf("="))
                            + Translator.translateExceptionMessage("MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT_2"));
                }
            }
        }

        /*
         Einzelne Punktkoordinaten werden in der HashMap
         varsContainedInParams gespeichert.
         */
        HashMap<String, Expression> varsContainedInParams = new HashMap<>();
        for (int i = 1; i < params.length; i++) {
            varsContainedInParams.put(params[i].substring(0, params[i].indexOf("=")),
                    Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet<String>()));
        }

        /*
         Es wird geprüft, ob allen Variablen, welche in der
         Funktionsvorschrift auftauchen, auch eine Koordinate zugewirsen
         wurde.
         */
        Iterator iter = vars.iterator();
        String var;
        for (int i = 0; i < vars.size(); i++) {
            var = (String) iter.next();
            if (!varsContainedInParams.containsKey(var)) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_TANGENT_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VARIABLE_MUST_OCCUR_IN_TANGENT_2"));
            }
        }

        return new Command(TypeCommand.tangent, new Object[]{expr, varsContainedInParams});

    }

    private static Command getCommandTaylorDEQ(String[] params) throws ExpressionException {

        /*
         Struktur: taylordeq(EXPRESSION, var, ord, x_0, y_0, y'(0), ...,
         y^(ord - 1)(0), k) EXPRESSION: Rechte Seite der DGL y^{(ord)} =
         EXPRESSION. Anzahl der parameter ist also = ord + 5 var = Variable in
         der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         fest k = Ordnung des Taylorpolynoms (an der Stelle x_0)
         */
        if (params.length < 6) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDEQ"));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[2]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_TAYLORDEQ"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_TAYLORDEQ"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt: Beispielsweise
         darf in einer DGL der ordnung 3 nicht y''', y'''' etc. auf der
         rechten Seite auftreten.
         */
        HashSet<String> vars = new HashSet<>();
        try {
            Expression.build(params[0], vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_TAYLORDEQ") + e.getMessage());
        }
        Expression expr = Expression.build(params[0], vars);

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        Iterator iter = vars.iterator();
        String varWithoutPrimes;
        for (int i = 0; i < vars.size(); i++) {
            varWithoutPrimes = (String) iter.next();
            if (!varWithoutPrimes.replaceAll("'", "").equals(params[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDEQ_1")
                            + ord
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDEQ_2")
                            + (ord - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDEQ_3"));
                }
                varWithoutPrimes = varWithoutPrimes.replaceAll("'", "");
            }
            varsWithoutPrimes.add(varWithoutPrimes);
        }

        if (varsWithoutPrimes.size() > 2) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDEQ"));
        }

        if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
            if (varsWithoutPrimes.size() == 2) {
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

        /*
         Nun wird varsWithoutPrimes, falls nötig, soweit ergänzt, dass es
         alle in der DGL auftretenden Variablen enthält (max. 2). Dies
         wird später wichtig sein, wenn es darum geht, zu prüfen, ob die
         SWP-Daten korrekt sind.
         */
        if (varsWithoutPrimes.isEmpty()) {
            varsWithoutPrimes.add(params[1]);
            if (params[1].equals("y")) {
                varsWithoutPrimes.add("z");
            } else {
                varsWithoutPrimes.add("y");
            }
        } else if (varsWithoutPrimes.size() == 1) {

            if (varsWithoutPrimes.contains(params[1])) {
                if (params[1].equals("y")) {
                    varsWithoutPrimes.add("z");
                } else {
                    varsWithoutPrimes.add("y");
                }
            } else {
                varsWithoutPrimes.add(params[1]);
            }

        }

        // Prüft, ob die AWP-Daten korrekt sind.
        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < ord + 4; i++) {
            try {
                Expression.build(params[i], varsInLimits).simplify();
                iter = varsWithoutPrimes.iterator();
                /*
                 Im Folgenden wird geprüft, ob in den Anfangsbedingungen
                 die Variablen aus der eigentlichen DGL nicht auftreten
                 (diese beiden Variablen sind im HashSet varsWithoutPrimes
                 gespeichert).
                 */
                if (varsInLimits.contains((String) iter.next())) {
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

        Object[] commandParams = new Object[ord + 5];
        commandParams[0] = expr;
        commandParams[1] = params[1];
        commandParams[2] = ord;
        for (int i = 3; i < ord + 4; i++) {
            commandParams[i] = Expression.build(params[i], vars);
        }
        commandParams[ord + 4] = Integer.parseInt(params[ord + 4]);

        return new Command(TypeCommand.taylordeq, commandParams);

    }

    private static Command getCommandUndef(String[] params) throws ExpressionException {
        // Struktur: undef(var_1, ..., var_k) var_i: Variablenname.
        // Prüft, ob alle Parameter gültige Variablen sind.
        for (int i = 0; i < params.length; i++) {
            if (!Expression.isValidDerivateOfVariable(params[i]) && !Expression.isPI(params[i])) {
                throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEF_1")
                        + (i + 1)
                        + Translator.translateExceptionMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEF_2"));
            }
        }
        return new Command(TypeCommand.undef, params);
    }

    private static Command getCommandUndelAll(String[] params) throws ExpressionException {
        // Struktur: undefall().
        // Prüft, ob der Befehl keine Parameter besitzt.
        if (params.length > 0) {
            throw new ExpressionException(Translator.translateExceptionMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_UNDEFALL"));
        }
        return new Command(TypeCommand.undefall, new Object[0]);
    }

    /**
     * Hauptmethode zum Ausführen des Befehls.
     *
     * @throws ExpressionException
     * @throws EvaluationException
     */
    public static void executeCommand(String input, GraphicArea graphicArea,
            JTextArea textArea, GraphicPanel2D graphicMethods2D, GraphicPanel3D graphicMethods3D,
            GraphicPanelCurves2D graphicMethodsCurves2D, GraphicPanelCurves3D graphicMethodsCurves3D,
            GraphicPanelPolar2D graphicMethodsPolar2D, HashMap<String, Expression> definedVars,
            HashMap<String, Expression> definedFunctions) throws ExpressionException, EvaluationException {

        output.clear();
        input = input.replaceAll(" ", "").toLowerCase();

        String[] commandNameAndParams = Expression.getOperatorAndArguments(input);
        String commandName = commandNameAndParams[0];
        String[] params = Expression.getArguments(commandNameAndParams[1]);

        /*
         Zunächst muss der entsprechende Befehl ermittelt und in eine Instanz
         der Klasse Command umgewandelt werden.
         */
        Command command = getCommand(commandName, params);

        // Abhängig vom Typ von c wird der Befehl ausgeführt.
        if (command.getTypeCommand().equals(TypeCommand.approx)) {
            executeApprox(command, definedVars, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.ccnf)) {
            executeCCNF(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.cdnf)) {
            executeCDNF(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.clear)) {
            executeClear(textArea, graphicArea);
        } else if ((command.getTypeCommand().equals(TypeCommand.def)) && command.getParams().length >= 1) {
            executeDefine(command, definedVars, definedFunctions, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.deffuncs)) {
            executeDefFuncs(definedFunctions, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.defvars)) {
            executeDefVars(definedVars, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.eigenvalues)) {
            executeEigenvalues(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.eigenvectors)) {
            executeEigenvectors(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.euler)) {
            executeEuler(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.expand)) {
            executeExpand(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.ker)) {
            executeKer(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.latex)) {
            executeLatex(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.pi)) {
            executePi(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.plot2d)) {
            if (params[0].contains("=")) {
                executeImplicitPlot2D(command, graphicMethods2D);
            } else {
                executePlot2D(command, graphicMethods2D);
            }
        } else if (command.getTypeCommand().equals(TypeCommand.plot3d)) {
            executePlot3D(command, graphicMethods3D);
        } else if (command.getTypeCommand().equals(TypeCommand.plotcurve) && command.getParams().length == 4) {
            executePlotCurve2D(command, graphicMethodsCurves2D);
        } else if (command.getTypeCommand().equals(TypeCommand.plotcurve) && command.getParams().length == 5) {
            executePlotCurve3D(command, graphicMethodsCurves3D);
        } else if (command.getTypeCommand().equals(TypeCommand.plotpolar)) {
            executePlotPolar2D(command, graphicMethodsPolar2D);
        } else if (command.getTypeCommand().equals(TypeCommand.solve)) {
            executeSolve(command, graphicMethods2D, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.solvedeq)) {
            executeSolveDEQ(command, graphicMethods2D, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.table)) {
            executeTable(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.tangent)) {
            executeTangent(command, graphicMethods2D, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.taylordeq)) {
            executeTaylorDEQ(command, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.undef)) {
            executeUndefine(command, definedVars, graphicArea);
        } else if (command.getTypeCommand().equals(TypeCommand.undefall)) {
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
    private static void executeApprox(Command command, HashMap<String, Expression> definedVars, GraphicArea graphicArea)
            throws ExpressionException, EvaluationException {

        Expression expr = (Expression) command.getParams()[0];

        /*
         Falls expr selbstdefinierte Funktionen enthält, dann zunächst expr so
         darstellen, dass es nur vordefinierte Funktionen beinhaltet.
         */
        expr = expr.replaceSelfDefinedFunctionsByPredefinedFunctions();
        // Mit Werten belegte Variablen müssen durch ihren exakten Ausdruck ersetzt werden.
        for (String var : definedVars.keySet()) {
            expr = expr.replaceVariable(var, (Expression) definedVars.get(var));
        }

        // Zunächst wird, soweit es geht, EXAKT vereinfacht, danach approximativ ausgewertet.
        expr = expr.simplify();
        expr = expr.turnToApproximate().simplify();
        /*
         Dies dient dazu, dass alle Variablen wieder "präzise" sind. Sie
         werden nur dann approximativ ausgegeben, wenn sie nicht präzise
         (precise = false) sind.
         */
        Variable.setAllPrecise(true);

        // Textliche Ausgabe
        output.add(expr.writeExpression() + " \n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(expr);

    }

    private static void executeCCNF(Command command, GraphicArea graphicArea) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        HashSet<String> vars = new HashSet<>();
        logExpr.getContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF_1")
                    + logExpr.writeLogicalExpression()
                    + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF_2"));
        }

        LogicalExpression logExprInCCNF = logExpr.toCCNF();
        // Textliche Ausgabe
        output.add(logExprInCCNF.writeLogicalExpression() + " \n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(logExprInCCNF);

    }

    private static void executeCDNF(Command command, GraphicArea graphicArea) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        HashSet<String> vars = new HashSet<>();
        logExpr.getContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF_1")
                    + logExpr.writeLogicalExpression()
                    + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF_2"));
        }

        LogicalExpression logExprInCDNF = logExpr.toCDNF();
        // Textliche Ausgabe
        output.add(logExprInCDNF.writeLogicalExpression() + " \n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(logExprInCDNF);

    }

    private static void executeClear(JTextArea area, GraphicArea graphicArea) {
        area.setText("");
        graphicArea.initializeBounds(MathToolForm.mathToolGraphicAreaX, MathToolForm.mathToolGraphicAreaY,
                MathToolForm.mathToolGraphicAreaWidth, MathToolForm.mathToolGraphicAreaHeight);
        graphicArea.clearArea();
    }

    private static void executeDefine(Command command, HashMap<String, Expression> definedVars,
            HashMap<String, Expression> definedFunctions, GraphicArea graphicArea) throws EvaluationException {

        // Falls ein Variablenwert definiert wird.
        if (command.getParams().length == 2) {
            String var = (String) command.getParams()[0];
            Expression preciseExpression = ((Expression) command.getParams()[1]).simplify();
            Variable.setPreciseExpression(var, preciseExpression);
            definedVars.put(var, preciseExpression);
            if (((Expression) command.getParams()[1]).equals(preciseExpression)) {
                // Textliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2")
                        + preciseExpression.writeExpression()
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3")
                        + " \n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2"),
                        preciseExpression, Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3"));
            } else {
                // Textliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2")
                        + ((Expression) command.getParams()[1]).writeExpression() + " = " + preciseExpression.writeExpression()
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3")
                        + " \n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1")
                        + var
                        + Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2"),
                        (Expression) command.getParams()[1], " = ", preciseExpression,
                        Translator.translateExceptionMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3"));
            }
        } else {
            // Falls eine Funktion definiert wird.
            Object[] params = command.getParams();
            String functionName = (String) params[0];
            String[] vars = new String[params.length - 2];
            Expression[] exprsForVars = new Expression[params.length - 2];
            for (int i = 0; i < params.length - 2; i++) {
                vars[i] = (String) params[i + 1];
                exprsForVars[i] = Variable.create((String) params[i + 1]);
            }
            SelfDefinedFunction.abstractExpressionsForSelfDefinedFunctions.put(functionName, (Expression) command.getParams()[command.getParams().length - 1]);
            SelfDefinedFunction.innerExpressionsForSelfDefinedFunctions.put(functionName, exprsForVars);
            SelfDefinedFunction.varsForSelfDefinedFunctions.put(functionName, vars);
            definedFunctions.put(functionName, new SelfDefinedFunction(functionName, vars, (Expression) command.getParams()[command.getParams().length - 1], exprsForVars));

            // Ausgabe an den Benutzer.
            String function;
            SelfDefinedFunction f = (SelfDefinedFunction) definedFunctions.get(functionName);
            String[] fArguments = f.getArguments();
            Expression[] varsForOutput = new Expression[f.getArguments().length];
            Expression fForOutput;

            function = f.getName() + "(";
            for (int i = 0; i < fArguments.length; i++) {
                /*
                 Die Argumente in f haben alle "_ABSTRACT" als Anhängsel.
                 Dieses wird nun beseitigt, um die Originalnamen
                 wiederzubekommen. Die Variablen mit den Originalnamen werden
                 im Array vars_for_output abgespechert.
                 */
                varsForOutput[i] = Variable.create(fArguments[i].substring(0, fArguments[i].indexOf("_ABSTRACT")));
                function = function + ((Variable) varsForOutput[i]).getName() + ",";
            }
            function = function.substring(0, function.length() - 1) + ")";
            fForOutput = f.replaceAllVariables(varsForOutput);

            // Textliche Ausgabe
            output.add(Translator.translateExceptionMessage("MCC_FUNCTION_WAS_DEFINED") + function + " = "
                    + fForOutput.writeExpression() + "\n \n");
            // Grafische Ausgabe
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_FUNCTION_WAS_DEFINED"),
                    function, " = ", fForOutput);

        }

    }

    private static void executeDefFuncs(HashMap<String, Expression> definedFunctions, GraphicArea graphicArea)
            throws EvaluationException {

        String function;
        SelfDefinedFunction f;
        Expression[] varsForOutput;
        Expression fForOutput;

        // Textliche Ausgabe
        output.add(Translator.translateExceptionMessage("MCC_LIST_OF_DEFINED_FUNCTIONS") + "\n \n");
        // Grafische Ausgabe
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_LIST_OF_DEFINED_FUNCTIONS"));

        // Alle selbstdefinierten Funktionen nacheinander ausgeben.
        if (!definedFunctions.isEmpty()) {
            for (String functionName : definedFunctions.keySet()) {

                function = "";
                f = (SelfDefinedFunction) definedFunctions.get(functionName);
                function = function + f.getName() + "(";
                for (int i = 0; i < f.getArguments().length; i++) {
                    function = function + "X_" + (i + 1) + ",";
                }
                function = function.substring(0, function.length() - 1) + ")";

                varsForOutput = new Expression[f.getArguments().length];
                for (int i = 0; i < f.getArguments().length; i++) {
                    varsForOutput[i] = Variable.create("X_" + (i + 1));
                }
                fForOutput = f.replaceAllVariables(varsForOutput);

                // Textliche Ausgabe
                output.add(function + " = " + fForOutput.writeExpression() + "\n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(function, " = ", fForOutput);

            }
        }

    }

    private static void executeDefVars(HashMap<String, Expression> definedVars, GraphicArea graphicArea) {

        if (!definedVars.isEmpty()) {
            for (String var : definedVars.keySet()) {
                // Textliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_LIST_OF_VARIABLES")
                        + var + " = " + ((Expression) definedVars.get(var)).writeExpression() + "\n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(var, " = ", (Expression) definedVars.get(var));
            }
        }

    }

    private static void executeEigenvalues(Command command, GraphicArea graphicArea) throws EvaluationException {

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsAlgorithms.getEigenvalues((MatrixExpression) command.getParams()[0]);

        if (eigenvalues.isEmpty()) {
            // Textliche Ausgabe
            output.add(Translator.translateExceptionMessage("MCC_NO_EIGENVALUES_1")
                    + ((MatrixExpression) command.getParams()[0]).writeMatrixExpression()
                    + Translator.translateExceptionMessage("MCC_NO_EIGENVALUES_2") + "\n \n");
            // Graphische Ausgabe
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_NO_EIGENVALUES_1"),
                    (MatrixExpression) command.getParams()[0],
                    Translator.translateExceptionMessage("MCC_NO_EIGENVALUES_2"));
            return;
        }

        // Textliche Ausgabe
        output.add(Translator.translateExceptionMessage("MCC_EIGENVALUES_OF_MATRIX_1")
                + ((MatrixExpression) command.getParams()[0]).writeMatrixExpression()
                + Translator.translateExceptionMessage("MCC_EIGENVALUES_OF_MATRIX_2") + "\n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_EIGENVALUES_OF_MATRIX_1"),
                (MatrixExpression) command.getParams()[0],
                Translator.translateExceptionMessage("MCC_EIGENVALUES_OF_MATRIX_2"));

        String eigenvaluesAsString = "";
        ArrayList<Object> eigenvaluesAsObjectArray = new ArrayList<>();

        for (int i = 0; i < eigenvalues.getBound(); i++) {
            // Für textliche Ausgabe
            eigenvaluesAsString = eigenvaluesAsString + eigenvalues.get(i).writeExpression();
            // Für graphische Ausgabe
            eigenvaluesAsObjectArray.add(eigenvalues.get(i));
            if (i < eigenvalues.getBound() - 1) {
                eigenvaluesAsString = eigenvaluesAsString + ", ";
                eigenvaluesAsObjectArray.add(", ");
            }
        }

        // Textliche Ausgabe
        output.add(eigenvaluesAsString);
        // Graphische Ausgabe
        graphicArea.addComponent(eigenvaluesAsObjectArray);

    }

    private static void executeEigenvectors(Command command, GraphicArea graphicArea) throws EvaluationException {

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsAlgorithms.getEigenvalues((MatrixExpression) command.getParams()[0]);

        MatrixExpressionCollection eigenvectors;
        MatrixExpression matrix = (MatrixExpression) command.getParams()[0];

        String eigenvectorsAsString;
        ArrayList<Object> eigenvectorsAsObjectArray;

        for (int i = 0; i < eigenvalues.getBound(); i++) {

            // Sicherheitshalber! Sollte eigentlich nie passieren.
            if (eigenvalues.get(i) == null) {
                continue;
            }
            // Eigenvektoren berechnen.
            eigenvectors = EigenvaluesEigenvectorsAlgorithms.getEigenvectorsForEigenvalue(matrix, eigenvalues.get(i));

            eigenvectorsAsString = "";
            eigenvectorsAsObjectArray = new ArrayList<>();
            eigenvectorsAsObjectArray.add(Translator.translateExceptionMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_1"));
            eigenvectorsAsObjectArray.add(eigenvalues.get(i));
            eigenvectorsAsObjectArray.add(Translator.translateExceptionMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_2"));
            if (eigenvectors.isEmpty()) {
                // Für textliche Ausgabe
                eigenvectorsAsString = Translator.translateExceptionMessage("MCC_EIGENVECTORS_NO_EXPLICIT_EIGENVECTORS");
                // Für graphische Ausgabe
                eigenvectorsAsObjectArray.add(Translator.translateExceptionMessage("MCC_EIGENVECTORS_NO_EXPLICIT_EIGENVECTORS"));
            } else {
                for (int j = 0; j < eigenvectors.getBound(); j++) {

                    // Für textliche Ausgabe
                    eigenvectorsAsString = eigenvectorsAsString + eigenvectors.get(j).writeMatrixExpression();
                    // Für graphische Ausgabe
                    eigenvectorsAsObjectArray.add(eigenvectors.get(j));
                    if (j < eigenvectors.getBound() - 1) {
                        eigenvectorsAsString = eigenvectorsAsString + ", ";
                        eigenvectorsAsObjectArray.add(", ");
                    }

                }
            }
            // Textliche Ausgabe
            output.add(Translator.translateExceptionMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_1")
                    + eigenvalues.get(i).writeExpression()
                    + Translator.translateExceptionMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_2")
                    + eigenvectorsAsString + "\n \n");
            // Graphische Ausgabe
            graphicArea.addComponent(eigenvectorsAsObjectArray);
        }

        if (eigenvalues.isEmpty()) {
            // Textliche Ausgabe
            output.add(Translator.translateExceptionMessage("MCC_EIGENVECTORS_NO_EIGENVECTORS"));
            // Graphische Ausgabe
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_EIGENVECTORS_NO_EIGENVECTORS"));
        }

    }

    private static void executeEuler(Command command, GraphicArea graphicArea) throws ExpressionException {

        BigDecimal e = AnalysisMethods.getDigitsOfE((int) command.getParams()[0]);
        // Textliche Ausgabe
        output.add(Translator.translateExceptionMessage("MCC_DIGITS_OF_E_1")
                + (int) command.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_E_2")
                + e.toString() + "\n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_DIGITS_OF_E_1")
                + (int) command.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_E_2")
                + e.toString());

    }

    private static void executeExpand(Command command, GraphicArea graphicArea) throws EvaluationException {
        // Es wird definiert, welche Arten von Vereinfachungen durchgeführt werden müssen.
        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.expand_powerful);
        simplifyTypes.add(TypeSimplify.collect_products);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_sums);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_differences);
        simplifyTypes.add(TypeSimplify.reduce_quotients);
        simplifyTypes.add(TypeSimplify.reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypes.add(TypeSimplify.simplify_powers);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);

        Expression expr = (Expression) command.getParams()[0];
        expr = expr.simplify(simplifyTypes);
        // Textliche Ausgabe
        output.add(expr.writeExpression() + "\n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(expr);
    }

    private static void executeKer(Command command, GraphicArea graphicArea) throws EvaluationException {

        MatrixExpression matExpr = ((MatrixExpression) command.getParams()[0]).simplify();
        if (!(matExpr instanceof Matrix)) {
            output.add(Translator.translateExceptionMessage("MCC_KER_COULD_NOT_BE_COMPUTED_1")
                    + ((MatrixExpression) command.getParams()[0]).writeMatrixExpression()
                    + Translator.translateExceptionMessage("MCC_KER_COULD_NOT_BE_COMPUTED_2"));
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_KER_COULD_NOT_BE_COMPUTED_1"),
                    ((MatrixExpression) command.getParams()[0]),
                    Translator.translateExceptionMessage("MCC_KER_COULD_NOT_BE_COMPUTED_2"));
            return;
        }

        MatrixExpressionCollection basisOfKer = GaussAlgorithm.computeKernelOfMatrix((Matrix) matExpr);

        if (basisOfKer.isEmpty()) {
            // Textliche Ausgabe
            output.add(Translator.translateExceptionMessage("MCC_TRIVIAL_KER_1")
                    + ((MatrixExpression) command.getParams()[0]).writeMatrixExpression()
                    + Translator.translateExceptionMessage("MCC_TRIVIAL_KER_2")
                    + MatrixExpression.getZeroMatrix(((Matrix) matExpr).getRowNumber(), 1).writeMatrixExpression());
            // Graphische Ausgabe
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_TRIVIAL_KER_1"),
                    ((MatrixExpression) command.getParams()[0]),
                    Translator.translateExceptionMessage("MCC_TRIVIAL_KER_2"),
                    MatrixExpression.getZeroMatrix(((Matrix) matExpr).getRowNumber(), 1));
            return;
        }

        // Textliche Ausgabe
        output.add(Translator.translateExceptionMessage("MCC_BASIS_OF_KER_1")
                + matExpr.writeMatrixExpression()
                + Translator.translateExceptionMessage("MCC_BASIS_OF_KER_2"));
        // Graphische Ausgabe
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_BASIS_OF_KER_1"),
                matExpr, Translator.translateExceptionMessage("MCC_BASIS_OF_KER_2"));

        String basisAsString = "";
        ArrayList<Object> basisAsObjectArray = new ArrayList<>();
        for (int i = 0; i < basisOfKer.getBound(); i++) {
            // Für textliche Ausgabe
            basisAsString = basisAsString + basisOfKer.get(i).writeMatrixExpression();
            // Für graphische Ausgabe
            basisAsObjectArray.add(basisOfKer.get(i));
            if (i < basisOfKer.getBound() - 1) {
                basisAsString = basisAsString + ", ";
                basisAsObjectArray.add(", ");
            }
        }

        // Textliche Ausgabe
        output.add(basisAsString + "\n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(basisAsObjectArray);

    }

    private static void executeLatex(Command command, GraphicArea graphicArea) throws ExpressionException {

        String latexCode = Translator.translateExceptionMessage("MCC_LATEX_CODE");
        for (int i = 0; i < command.getParams().length - 1; i++) {
            if (command.getParams()[i] == null) {
                latexCode = latexCode + " = ";
            } else {
                latexCode = latexCode + ((Expression) command.getParams()[i]).expressionToLatex() + " = ";
            }
        }
        if (command.getParams()[command.getParams().length - 1] == null) {
            latexCode = latexCode + "\n \n";
        } else {
            latexCode = latexCode + ((Expression) command.getParams()[command.getParams().length - 1]).expressionToLatex() + "\n \n";
        }

        // Texttliche Ausgabe
        output.add(latexCode);
        // Graphische Ausgabe
        graphicArea.addComponent(latexCode);

    }

    private static void executePi(Command command, GraphicArea graphicArea) throws ExpressionException {

        BigDecimal pi = AnalysisMethods.getDigitsOfPi((int) command.getParams()[0]);
        // Texttliche Ausgabe
        output.add(Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_1")
                + (int) command.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_2")
                + pi.toString() + "\n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_1")
                + (int) command.getParams()[0]
                + Translator.translateExceptionMessage("MCC_DIGITS_OF_PI_2")
                + pi.toString());

    }

    private static void executePlot2D(Command command, GraphicPanel2D graphicMethods2D) throws ExpressionException,
            EvaluationException {

        HashSet<String> vars = new HashSet<>();
        Expression[] exprs = new Expression[command.getParams().length - 2];

        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_powers);
        simplifyTypes.add(TypeSimplify.collect_products);
        simplifyTypes.add(TypeSimplify.reduce_quotients);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_sums);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_differences);
        simplifyTypes.add(TypeSimplify.reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        for (int i = 0; i < command.getParams().length - 2; i++) {
            exprs[i] = (Expression) command.getParams()[i];
            exprs[i] = exprs[i].simplify(simplifyTypes);
            exprs[i].addContainedVars(vars);
        }

        // Falls der Ausdruck expr konstant ist, soll die Achse die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add("x");
        }

        Expression x_0 = (Expression) command.getParams()[command.getParams().length - 2];
        Expression x_1 = (Expression) command.getParams()[command.getParams().length - 1];

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setIsExplicit(true);
        graphicMethods2D.setIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        for (int i = 0; i < command.getParams().length - 2; i++) {
            graphicMethods2D.addExpression(exprs[i]);
        }
        graphicMethods2D.setVarAbsc(var);
        graphicMethods2D.computeScreenSizes(x_0, x_1);
        graphicMethods2D.expressionToGraph(var, x_0, x_1);
        graphicMethods2D.setSpecialPointsOccur(false);
        graphicMethods2D.drawGraph2D();

    }

    private static void executePlot3D(Command command, GraphicPanel3D graphicMethods3D) throws EvaluationException {

        HashSet<String> vars = new HashSet<>();

        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_powers);
        simplifyTypes.add(TypeSimplify.collect_products);
        simplifyTypes.add(TypeSimplify.reduce_quotients);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_sums);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_differences);
        simplifyTypes.add(TypeSimplify.reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        
        Expression expr = ((Expression) command.getParams()[0]).simplify(simplifyTypes);
        Expression x_0 = ((Expression) command.getParams()[1]).simplify(simplifyTypes);
        Expression x_1 = ((Expression) command.getParams()[2]).simplify(simplifyTypes);
        Expression y_0 = ((Expression) command.getParams()[3]).simplify(simplifyTypes);
        Expression y_1 = ((Expression) command.getParams()[4]).simplify(simplifyTypes);
        expr.addContainedVars(vars);

        // Falls der Ausdruck expr konstant ist, sollen die Achsen die Bezeichnungen "x" und "y" tragen.
        if (expr.isConstant()) {
            vars.add("x");
            vars.add("y");
        }

        /*
         Falls der Ausdruck expr nur von einer Variablen abhängt, sollen die
         andere Achse eine fest gewählte Bezeichnung tragen. Dies wirdim
         Folgenden geregelt.
         */
        if (vars.size() == 1) {
            if (vars.contains("y")) {
                vars.add("z");
            } else {
                vars.add("y");
            }
        }

        Iterator iter = vars.iterator();
        String varOne = (String) iter.next();
        String varTwo = (String) iter.next();

        /*
         Die Variablen varOne und varTwo sind evtl. noch nicht in
         alphabetischer Reihenfolge. Dies wird hier nachgeholt. GRUND: Der
         Zeichenbereich wird durch vier Zahlen eingegrenzt, welche den
         Variablen in ALPHABETISCHER Reihenfolge entsprechen. Die ersten
         beiden bilden die Grenzen für die Abszisse, die anderen beiden für
         die Ordinate.
         */
        String varAbsc = varOne;
        String varOrd = varTwo;

        if (varAbsc.compareTo(varOrd) > 0) {
            varAbsc = varTwo;
            varOrd = varOne;
        }

        graphicMethods3D.setExpression(expr);
        graphicMethods3D.setParameters(varAbsc, varOrd, 150, 200, 30, 30);
        graphicMethods3D.expressionToGraph(x_0, x_1, y_0, y_1);
        graphicMethods3D.drawGraph3D();

    }

    private static void executeImplicitPlot2D(Command command, GraphicPanel2D graphicMethods2D) throws EvaluationException {

        
        HashSet<String> vars = new HashSet<>();
        
        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_powers);
        simplifyTypes.add(TypeSimplify.collect_products);
        simplifyTypes.add(TypeSimplify.reduce_quotients);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_sums);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_differences);
        simplifyTypes.add(TypeSimplify.reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        
        Expression expr = ((Expression) command.getParams()[0]).sub((Expression) command.getParams()[1]).simplify(simplifyTypes);
        Expression x_0 = ((Expression) command.getParams()[2]).simplify(simplifyTypes);
        Expression x_1 = ((Expression) command.getParams()[3]).simplify(simplifyTypes);
        Expression y_0 = ((Expression) command.getParams()[4]).simplify(simplifyTypes);
        Expression y_1 = ((Expression) command.getParams()[5]).simplify(simplifyTypes);
        expr.addContainedVars(vars);

        // Falls der Ausdruck expr konstant ist, sollen die Achsen die Bezeichnungen "x" und "y" tragen.
        if (vars.isEmpty()) {
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

        Iterator iter = vars.iterator();
        String varOne = (String) iter.next();
        String varTwo = (String) iter.next();

        String varAbsc = varOne;
        String varOrd = varTwo;

        if ((int) varOne.charAt(0) > (int) varTwo.charAt(0)) {
            varAbsc = varTwo;
            varOrd = varOne;
        }
        if ((int) varOne.charAt(0) == (int) varTwo.charAt(0)) {
            if ((varOne.length() > 1) && (varTwo.length() == 1)) {
                varAbsc = varTwo;
                varOrd = varOne;
            }
            if ((varOne.length() > 1) && (varTwo.length() > 1)) {
                int indexOfVarOne = Integer.parseInt(varOne.substring(2));
                int indexOfVarTwo = Integer.parseInt(varTwo.substring(2));
                if (indexOfVarOne > indexOfVarTwo) {
                    varAbsc = varTwo;
                    varOrd = varOne;
                }
            }
        }

        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setIsExplicit(false);
        graphicMethods2D.setIsFixed(false);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addExpression(expr);
        graphicMethods2D.setVars(varAbsc, varOrd);
        graphicMethods2D.computeScreenSizes(x_0, x_1, y_0, y_1);
        graphicMethods2D.setSpecialPointsOccur(false);
        ArrayList<double[]> implicitGraph = NumericalMethods.solveImplicitEquation(expr, varAbsc, varOrd,
                x_0.evaluate(), x_1.evaluate(), y_0.evaluate(), y_1.evaluate());
        graphicMethods2D.setImplicitGraph(implicitGraph);
        graphicMethods2D.drawGraph2D();

    }

    private static void executePlotCurve2D(Command command, GraphicPanelCurves2D graphicMethodsCurves2D) throws EvaluationException {

        HashSet<String> vars = new HashSet<>();
        Expression[] expr = new Expression[2];

        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_powers);
        simplifyTypes.add(TypeSimplify.collect_products);
        simplifyTypes.add(TypeSimplify.reduce_quotients);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_sums);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_differences);
        simplifyTypes.add(TypeSimplify.reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);

        expr[0] = (Expression) command.getParams()[0];
        expr[0].addContainedVars(vars);
        expr[0] = expr[0].simplify(simplifyTypes);
        expr[1] = (Expression) command.getParams()[1];
        expr[1].addContainedVars(vars);
        expr[1] = expr[1].simplify(simplifyTypes);
        Expression t_0 = ((Expression) command.getParams()[2]).simplify(simplifyTypes);
        Expression t_1 = ((Expression) command.getParams()[3]).simplify(simplifyTypes);

        // Falls der Ausdruck expr konstant ist, soll der Parameter die Bezeichnung "t" tragen.
        if (vars.isEmpty()) {
            vars.add("t");
        }

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethodsCurves2D.setIsInitialized(true);
        graphicMethodsCurves2D.setExpression(expr);
        graphicMethodsCurves2D.setVar(var);
        graphicMethodsCurves2D.computeScreenSizes(t_0, t_1);
        graphicMethodsCurves2D.expressionToGraph(t_0, t_1);
        graphicMethodsCurves2D.drawCurve2D();

    }

    private static void executePlotCurve3D(Command command, GraphicPanelCurves3D graphicMethodsCurves3D) throws ExpressionException,
            EvaluationException {

        HashSet<String> vars = new HashSet<>();
        Expression[] expr = new Expression[3];

        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_powers);
        simplifyTypes.add(TypeSimplify.collect_products);
        simplifyTypes.add(TypeSimplify.reduce_quotients);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_sums);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_differences);
        simplifyTypes.add(TypeSimplify.reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);

        expr[0] = (Expression) command.getParams()[0];
        expr[0].addContainedVars(vars);
        expr[0] = expr[0].simplify(simplifyTypes);
        expr[1] = (Expression) command.getParams()[1];
        expr[1].addContainedVars(vars);
        expr[1] = expr[1].simplify(simplifyTypes);
        expr[2] = (Expression) command.getParams()[2];
        expr[2].addContainedVars(vars);
        expr[2] = expr[2].simplify(simplifyTypes);
        Expression t_0 = ((Expression) command.getParams()[3]).simplify(simplifyTypes);
        Expression t_1 = ((Expression) command.getParams()[4]).simplify(simplifyTypes);

        // Falls der Ausdruck expr konstant ist, soll der Parameter die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add("t");
        }

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

    private static void executePlotPolar2D(Command command, GraphicPanelPolar2D graphicMethodsPolar2D) throws EvaluationException {

        HashSet<String> vars = new HashSet<>();
        Expression[] exprs = new Expression[command.getParams().length - 2];

        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_powers);
        simplifyTypes.add(TypeSimplify.collect_products);
        simplifyTypes.add(TypeSimplify.reduce_quotients);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_sums);
        simplifyTypes.add(TypeSimplify.factorize_all_but_rationals_in_differences);
        simplifyTypes.add(TypeSimplify.reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);

        for (int i = 0; i < command.getParams().length - 2; i++) {
            exprs[i] = (Expression) command.getParams()[i];
            exprs[i] = exprs[i].simplify(simplifyTypes);
            exprs[i].addContainedVars(vars);
        }
        Expression phi_0 = ((Expression) command.getParams()[command.getParams().length - 2]).simplify(simplifyTypes);
        Expression phi_1 = ((Expression) command.getParams()[command.getParams().length - 1]).simplify(simplifyTypes);

        // Falls der Ausdruck expr konstant ist, soll die Achse die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add("x");
        }

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        graphicMethodsPolar2D.setIsInitialized(true);
        graphicMethodsPolar2D.clearExpressionAndGraph();
        for (int i = 0; i < command.getParams().length - 2; i++) {
            graphicMethodsPolar2D.addExpression(exprs[i]);
        }
        graphicMethodsPolar2D.setVar(var);
        graphicMethodsPolar2D.computeScreenSizes(phi_0, phi_1);
        graphicMethodsPolar2D.expressionToGraph(var, phi_0.evaluate(), phi_1.evaluate());
        graphicMethodsPolar2D.drawPolarGraph2D();

    }

    private static void executeSolve(Command command, GraphicPanel2D graphicMethods2D, GraphicArea graphicArea)
            throws EvaluationException {

        HashSet<String> vars = new HashSet<>();
        Expression f = (Expression) command.getParams()[0];
        Expression g = (Expression) command.getParams()[1];

        if (command.getParams().length <= 3) {

            f.addContainedVars(vars);
            g.addContainedVars(vars);

            // Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
            String var;
            if (command.getParams().length == 3) {
                var = (String) command.getParams()[2];
            } else {
                var = "x";
                if (!vars.isEmpty()) {
                    Iterator iter = vars.iterator();
                    var = (String) iter.next();
                }
            }

            SolveMethods.setSolveTries(100);
            ExpressionCollection zeros = SolveMethods.solveGeneralEquation(f, g, var);

            // Falls keine Lösungen ermittelt werden konnten, User informieren.
            if (zeros.isEmpty() && zeros != SolveMethods.ALL_REALS) {
                // Textliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND") + " \n \n");
                // Graphische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND"));

                return;
            }

            /*
             Falls Lösungen Parameter K_1, K_2, ... enthalten, dann zusätzlich
             ausgeben: K_1, K_2, ... sind beliebige ganze Zahlen.
             */
            boolean solutionContainsFreeParameter = false;
            String messageAboutFreeParameters = "";
            for (int i = 0; i < zeros.getBound(); i++) {
                solutionContainsFreeParameter = solutionContainsFreeParameter || zeros.get(i).contains("K_1");
            }
            if (solutionContainsFreeParameter) {
                boolean solutionContainsFreeParameterOfGivenIndex = true;
                int maxIndex = 1;
                while (solutionContainsFreeParameterOfGivenIndex) {
                    maxIndex++;
                    solutionContainsFreeParameterOfGivenIndex = false;
                    for (int i = 0; i < zeros.getBound(); i++) {
                        solutionContainsFreeParameterOfGivenIndex = solutionContainsFreeParameterOfGivenIndex
                                || zeros.get(i).contains("K_" + maxIndex);
                    }
                }
                maxIndex--;

                messageAboutFreeParameters = "K_1, ";
                for (int i = 2; i <= maxIndex; i++) {
                    messageAboutFreeParameters = messageAboutFreeParameters + "K_" + i + ", ";
                }
                messageAboutFreeParameters = messageAboutFreeParameters.substring(0, messageAboutFreeParameters.length() - 2);
                if (maxIndex == 1) {
                    messageAboutFreeParameters = messageAboutFreeParameters
                            + Translator.translateExceptionMessage("MCC_IS_ARBITRARY_INTEGER") + " \n \n";
                } else {
                    messageAboutFreeParameters = messageAboutFreeParameters
                            + Translator.translateExceptionMessage("MCC_ARE_ARBITRARY_INTEGERS") + " \n \n";
                }

            }

            if (var.length() > 1) {
                /*
                 Falls var etwa x_1 oder x' ist, so sollen die Lösungen
                 (x_1)_i bzw. (x')_i, i = 1, 2, 3, ... heißen.
                 */
                var = "(" + var + ")";
            }
            // Textliche Ausgabe
            output.add(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION")
                    + ((Expression) command.getParams()[0]).writeExpression()
                    + " = "
                    + ((Expression) command.getParams()[1]).writeExpression() + ": \n \n");
            if (zeros == SolveMethods.ALL_REALS) {
                output.add(Translator.translateExceptionMessage("MCC_ALL_REALS") + " \n \n");
            } else {
                for (int i = 0; i < zeros.getBound(); i++) {
                    output.add(var + "_" + (i + 1) + " = " + zeros.get(i).writeExpression() + "\n \n");
                }
            }
            // Grafische Ausgabe
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION"), (Expression) command.getParams()[0],
                    " = ", (Expression) command.getParams()[1], " :");
            if (zeros == SolveMethods.ALL_REALS) {
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_ALL_REALS") + " \n \n");
            } else {
                for (int i = 0; i < zeros.getBound(); i++) {
                    graphicArea.addComponent(var + "_" + (i + 1) + " = ", zeros.get(i));
                }
            }

            if (solutionContainsFreeParameter) {

                // Textliche Ausgabe
                output.add(messageAboutFreeParameters);
                // Grafische Ausgabe
                graphicArea.addComponent(messageAboutFreeParameters);

            }

        } else {

            Expression equation = f.sub(g).simplify();
            equation.addContainedVars(vars);
            // Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
            String var = "x";
            if (!vars.isEmpty()) {
                Iterator iter = vars.iterator();
                var = (String) iter.next();
            }

            Expression x_0 = (Expression) command.getParams()[2];
            Expression x_1 = (Expression) command.getParams()[3];
            /*
             Falls die Anzahl der Unterteilungen nicht angegeben wird, so soll
             das Intervall in 10000 Teile unterteilt werden.
             */
            int n = 10000;

            if (command.getParams().length == 5) {
                n = (int) command.getParams()[4];
            }

            if (equation.isConstant()) {
                // Textliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION")
                        + ((Expression) command.getParams()[0]).writeExpression()
                        + " = "
                        + ((Expression) command.getParams()[1]).writeExpression() + ": \n \n");
                if (equation.equals(Expression.ZERO)) {
                    output.add(Translator.translateExceptionMessage("MCC_ALL_REALS") + " \n \n");
                } else {
                    output.add(Translator.translateExceptionMessage("MCC_EQUATIONS_HAS_NO_SOLUTIONS") + " \n \n");
                }
                // Grafische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION"), (Expression) command.getParams()[0],
                        " = ", (Expression) command.getParams()[1], " :");
                if (equation.equals(Expression.ZERO)) {
                    graphicArea.addComponent(Translator.translateExceptionMessage("MCC_ALL_REALS"));
                } else {
                    graphicArea.addComponent(Translator.translateExceptionMessage("MCC_EQUATIONS_HAS_NO_SOLUTIONS"));
                }

                // Graphen der linken und der rechten Seite zeichnen.
                graphicMethods2D.setIsInitialized(true);
                graphicMethods2D.setIsExplicit(true);
                graphicMethods2D.setIsFixed(false);
                graphicMethods2D.clearExpressionAndGraph();
                graphicMethods2D.addExpression(f);
                graphicMethods2D.addExpression(g);
                graphicMethods2D.setVarAbsc(var);
                graphicMethods2D.computeScreenSizes(x_0, x_1);
                graphicMethods2D.expressionToGraph(var, x_0, x_1);
                graphicMethods2D.setSpecialPointsOccur(false);
                graphicMethods2D.drawGraph2D();
                return;

            }

            HashMap<Integer, Double> zeros = NumericalMethods.solveEquation(equation, var, x_0.evaluate(), x_1.evaluate(), n);

            if (var.length() > 1) {
                /*
                 Falls var etwa x_1 oder x' ist, so sollen die Lösungen
                 (x_1)_i bzw. (x')_i, i = 1, 2, 3, ... heißen.
                 */
                var = "(" + var + ")";
            }
            // Textliche Ausgabe
            output.add(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION")
                    + ((Expression) command.getParams()[0]).writeExpression()
                    + " = "
                    + ((Expression) command.getParams()[1]).writeExpression() + ": \n \n");
            // Grafische Ausgabe
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTIONS_OF_EQUATION"), (Expression) command.getParams()[0],
                    " = ", (Expression) command.getParams()[1], " :");

            for (int i = 0; i < zeros.size(); i++) {
                // Textliche Ausgabe
                output.add(var + "_" + (i + 1) + " = " + zeros.get(i) + "\n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(var + "_" + (i + 1) + " = " + zeros.get(i));
            }

            if (zeros.isEmpty()) {
                // Textliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_NO_SOLUTIONS_OF_EQUATION_FOUND") + " \n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_NO_SOLUTIONS_OF_EQUATION_FOUND"));
            }

            // Nullstellen als Array (zum Markieren).
            double[][] zerosAsArray = new double[zeros.size()][2];
            for (int i = 0; i < zerosAsArray.length; i++) {
                zerosAsArray[i][0] = zeros.get(i);
                Variable.setValue(var, zerosAsArray[i][0]);
                zerosAsArray[i][1] = f.evaluate();
            }

            /*
             Graphen der linken und der rechten Seite zeichnen, inkl. der
             Lösungen (als rot markierte Punkte).
             */
            graphicMethods2D.setIsInitialized(true);
            graphicMethods2D.setIsExplicit(true);
            graphicMethods2D.setIsFixed(false);
            graphicMethods2D.clearExpressionAndGraph();
            graphicMethods2D.addExpression(f);
            graphicMethods2D.addExpression(g);
            graphicMethods2D.setVarAbsc(var);
            graphicMethods2D.computeScreenSizes(x_0, x_1);
            graphicMethods2D.expressionToGraph(var, x_0, x_1);
            graphicMethods2D.setSpecialPointsOccur(true);
            graphicMethods2D.setSpecialPoints(zerosAsArray);
            graphicMethods2D.drawGraph2D();

        }

    }

    private static void executeSolveDEQ(Command command, GraphicPanel2D graphicMethods2D, GraphicArea graphicArea)
            throws EvaluationException {

        int ord = (int) command.getParams()[2];
        HashSet<String> vars = new HashSet<>();
        Expression expr = ((Expression) command.getParams()[0]).simplify();
        expr.addContainedVars(vars);

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        Iterator iter = vars.iterator();
        String varWithoutPrimes;
        for (int i = 0; i < vars.size(); i++) {
            varWithoutPrimes = (String) iter.next();
            if (!varWithoutPrimes.replaceAll("'", "").equals(command.getParams()[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new EvaluationException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_1")
                            + ord
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_2")
                            + (ord - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_3"));
                }
                varWithoutPrimes = varWithoutPrimes.replaceAll("'", "");
            }
            varsWithoutPrimes.add(varWithoutPrimes);
        }

        String varAbsc = (String) command.getParams()[1];
        Expression x_0 = (Expression) command.getParams()[3];
        Expression x_1 = (Expression) command.getParams()[4];
        Expression[] y_0 = new Expression[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (Expression) command.getParams()[i + 5];
        }
        double[] startValues = new double[ord];
        for (int i = 0; i < y_0.length; i++) {
            startValues[i] = y_0[i].evaluate();
        }

        /*
         Zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt
         werden. Falls dieser nicht eindeutig ist, wird "Y" vorgegeben.
         */
        String varOrd;

        if (varsWithoutPrimes.isEmpty()) {
            if (varAbsc.equals("y")) {
                varOrd = "z";
            } else {
                varOrd = "y";
            }
        } else if (varsWithoutPrimes.size() == 1) {
            if (varsWithoutPrimes.contains(varAbsc)) {
                if (varAbsc.equals("y")) {
                    varOrd = "z";
                } else {
                    varOrd = "y";
                }
            } else {
                iter = varsWithoutPrimes.iterator();
                varOrd = (String) iter.next();
            }
        } else {
            iter = varsWithoutPrimes.iterator();
            varOrd = (String) iter.next();
            if (varOrd.equals(varAbsc)) {
                varOrd = (String) iter.next();
            }
        }

        double[][] solutionOfDifferentialEquation = NumericalMethods.solveDifferentialEquation(expr, varAbsc, varOrd, ord, x_0.evaluate(), x_1.evaluate(), startValues, 1000);

        // Formulierung und Ausgabe des AWP.
        String formulationOfAWPForTextArea = Translator.translateExceptionMessage("MCC_SOLUTION_OF_DEQ") + varOrd;
        ArrayList formulationOfAWPForGraphicArea = new ArrayList();

        for (int i = 0; i < ord; i++) {
            formulationOfAWPForTextArea = formulationOfAWPForTextArea + "'";
        }

        formulationOfAWPForGraphicArea.add(formulationOfAWPForTextArea + "(" + varAbsc + ") = ");
        formulationOfAWPForGraphicArea.add(expr);
        formulationOfAWPForTextArea = formulationOfAWPForTextArea + "(" + varAbsc + ") = " + expr.writeExpression();

        String varOrdWithPrimes;
        for (int i = 0; i < ord; i++) {
            formulationOfAWPForTextArea = formulationOfAWPForTextArea + ", " + varOrd;
            formulationOfAWPForGraphicArea.add(", ");
            varOrdWithPrimes = varOrd;
            for (int j = 0; j < i; j++) {
                formulationOfAWPForTextArea = formulationOfAWPForTextArea + "'";
                varOrdWithPrimes = varOrdWithPrimes + "'";
            }

            formulationOfAWPForTextArea = formulationOfAWPForTextArea + "(" + x_0.writeExpression() + ") = ";
            formulationOfAWPForTextArea = formulationOfAWPForTextArea + y_0[i].writeExpression();
            formulationOfAWPForGraphicArea.add(varOrdWithPrimes);
            formulationOfAWPForGraphicArea.add(TypeBracket.BRACKET_SURROUNDING_EXPRESSION);
            formulationOfAWPForGraphicArea.add(x_0);
            formulationOfAWPForGraphicArea.add(" = ");
            formulationOfAWPForGraphicArea.add(y_0[i]);
        }

        formulationOfAWPForTextArea = formulationOfAWPForTextArea + ", " + x_0.writeExpression() + " \u2264 " + varAbsc + " \u2264 " + x_1.writeExpression() + ": \n \n";
        formulationOfAWPForGraphicArea.add(", ");
        formulationOfAWPForGraphicArea.add(x_0);
        formulationOfAWPForGraphicArea.add(" \u2264 ");
        formulationOfAWPForGraphicArea.add(varAbsc);
        formulationOfAWPForGraphicArea.add(" \u2264 ");
        formulationOfAWPForGraphicArea.add(x_1);
        formulationOfAWPForGraphicArea.add(":");

        // Texttliche Ausgabe
        output.add(formulationOfAWPForTextArea);
        // Grafische Ausgabe
        graphicArea.addComponent(formulationOfAWPForGraphicArea);

        // Lösungen ausgeben.
        for (double[] solution : solutionOfDifferentialEquation) {
            // Texttliche Ausgabe
            output.add(varAbsc + " = " + solution[0] + "; " + varOrd + " = " + solution[1] + "\n \n");
            // Grafische Ausgabe
            graphicArea.addComponent(varAbsc + " = " + solution[0] + "; " + varOrd + " = " + solution[1]);
        }
        if (solutionOfDifferentialEquation.length < 1001) {
            // Falls die Lösung innerhalb des Berechnungsbereichs unendlich/undefiniert ist.
            output.add(Translator.translateExceptionMessage("MCC_SOLUTION_OF_DEQ_NOT_DEFINED_IN_POINT")
                    + (x_0.evaluate() + (solutionOfDifferentialEquation.length) * (x_1.evaluate() - x_0.evaluate()) / 1000)
                    + ". \n \n");
            graphicArea.addComponent(Translator.translateExceptionMessage("MCC_SOLUTION_OF_DEQ_NOT_DEFINED_IN_POINT")
                    + (x_0.evaluate() + (solutionOfDifferentialEquation.length) * (x_1.evaluate() - x_0.evaluate()) / 1000)
                    + ".");
        }

        // Lösungsgraphen zeichnen.
        graphicMethods2D.setIsInitialized(true);
        graphicMethods2D.setIsExplicit(true);
        graphicMethods2D.setIsFixed(true);
        graphicMethods2D.clearExpressionAndGraph();
        graphicMethods2D.addGraph(solutionOfDifferentialEquation);
        graphicMethods2D.setVars(varAbsc, varOrd);
        graphicMethods2D.computeScreenSizes();
        graphicMethods2D.setSpecialPointsOccur(false);
        graphicMethods2D.drawGraph2D();

    }

    private static void executeTable(Command command, GraphicArea graphicArea) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        HashSet<String> vars = new HashSet<>();
        logExpr.getContainedVars(vars);
        int numberOfVars = vars.size();
        if (numberOfVars > 20) {
            throw new EvaluationException(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES_1")
                    + logExpr.writeLogicalExpression()
                    + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES_2"));
        }

        // Texttliche Ausgabe
        output.add(Translator.translateExceptionMessage("MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION") + logExpr.writeLogicalExpression() + ": \n \n");
        // Grafische Ausgabe
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION"), logExpr, ":");

        // Falls es sich um einen konstanten Ausdruck handelt.
        if (numberOfVars == 0) {
            boolean value = logExpr.evaluate();
            if (value) {
                // Texttliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + logExpr.writeLogicalExpression()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2") + " \n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + logExpr.writeLogicalExpression()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2"));
            } else {
                // Texttliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + logExpr.writeLogicalExpression()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3") + " \n \n");
                // Grafische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1")
                        + logExpr.writeLogicalExpression()
                        + Translator.translateExceptionMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3"));
            }
            return;
        }

        // Für die Geschwindigkeit der Tabellenberechnung: logExpr vereinfachen.
        logExpr = logExpr.simplify();

        // Nummerierung der logischen Variablen.
        HashMap<Integer, String> varsEnumerated = new HashMap<>();

        Iterator iter = vars.iterator();
        for (int i = 0; i < vars.size(); i++) {
            varsEnumerated.put(varsEnumerated.size(), (String) iter.next());
        }

        int tableLength = BigInteger.valueOf(2).pow(numberOfVars).intValue();

        String varsInOrder = Translator.translateExceptionMessage("MCC_ORDER_OF_VARIABLES_IN_TABLE");
        for (int i = 0; i < varsEnumerated.size(); i++) {
            varsInOrder = varsInOrder + varsEnumerated.get(i) + ", ";
        }
        varsInOrder = varsInOrder.substring(0, varsInOrder.length() - 2) + " \n \n";

        // Texttliche Ausgabe
        output.add(varsInOrder);
        // Grafische Ausgabe
        graphicArea.addComponent(varsInOrder);

        /*
         Erstellung eines Binärcounters zum Durchlaufen aller möglichen
         Belegungen der Variablen in vars.
         */
        boolean[] varsValues = new boolean[vars.size()];
        boolean currentValue;

        String binaryCounter;
        for (int i = 0; i < tableLength; i++) {

            binaryCounter = "(";
            for (int j = 0; j < vars.size(); j++) {
                if (varsValues[j]) {
                    binaryCounter = binaryCounter + "1, ";
                } else {
                    binaryCounter = binaryCounter + "0, ";
                }
                LogicalVariable.setValue(varsEnumerated.get(j), varsValues[j]);
            }
            binaryCounter = binaryCounter.substring(0, binaryCounter.length() - 2) + "): ";

            currentValue = logExpr.evaluate();
            if (currentValue) {
                binaryCounter = binaryCounter + "1 \n";
            } else {
                binaryCounter = binaryCounter + "0 \n";
            }

            // Texttliche Ausgabe
            output.add(binaryCounter);
            // Grafische Ausgabe
            graphicArea.addComponent(binaryCounter);

            varsValues = LogicalExpression.binaryCounter(varsValues);

            // Am Ende der Tabelle: Leerzeile lassen.
            if (i == tableLength - 1) {
                output.add("\n");
            }

        }

    }

    private static void executeTangent(Command command, GraphicPanel2D graphicMethods2D, GraphicArea graphicArea)
            throws EvaluationException {

        Expression expr = (Expression) command.getParams()[0];
        HashMap<String, Expression> vars = (HashMap<String, Expression>) command.getParams()[1];

        String tangentAnnouncementForTextArea = Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_1")
                + expr.writeExpression()
                + Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_2");
        ArrayList tangentAnnouncementForGraphicArea = new ArrayList();
        tangentAnnouncementForGraphicArea.add(Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_1"));
        tangentAnnouncementForGraphicArea.add(expr);
        tangentAnnouncementForGraphicArea.add(Translator.translateExceptionMessage("MCC_EQUATION_OF_TANGENT_SPACE_2"));

        for (String var : vars.keySet()) {
            tangentAnnouncementForTextArea = tangentAnnouncementForTextArea + var + " = " + vars.get(var).writeExpression() + ", ";
            tangentAnnouncementForGraphicArea.add(var + " = ");
            tangentAnnouncementForGraphicArea.add(vars.get(var));
            tangentAnnouncementForGraphicArea.add(", ");
        }
        // In der textlichen und in der grafischen Ausgabe das letzte (überflüssige) Komma entfernen.
        tangentAnnouncementForTextArea = tangentAnnouncementForTextArea.substring(0, tangentAnnouncementForTextArea.length() - 2) + ": \n \n";
        tangentAnnouncementForGraphicArea.remove(tangentAnnouncementForGraphicArea.size() - 1);
        tangentAnnouncementForGraphicArea.add(":");

        Expression tangent = AnalysisMethods.getTangentSpace(expr.simplify(), vars);

        // Texttliche Ausgabe
        output.add(tangentAnnouncementForTextArea);
        output.add("Y = " + tangent.writeExpression() + "\n \n");
        // Grafische Ausgabe
        graphicArea.addComponent(tangentAnnouncementForGraphicArea);
        graphicArea.addComponent("Y = ", tangent);

        if (vars.size() == 1) {

            String var = "";
            // vars enthält in diesem Fall nur eine Variable.
            for (String uniqueVar : vars.keySet()) {
                var = uniqueVar;
            }
            double x_0 = vars.get(var).evaluate() - 1;
            double x_1 = x_0 + 2;

            double[][] tangentPoint = new double[1][2];
            tangentPoint[0][0] = vars.get(var).evaluate();
            tangentPoint[0][1] = expr.replaceVariable(var, vars.get(var)).evaluate();

            // Im Falle einer Veränderlichen: den Graphen der Funktion und die Tangente zeichnen.
            graphicMethods2D.setIsInitialized(true);
            graphicMethods2D.setIsExplicit(true);
            graphicMethods2D.setIsFixed(false);
            graphicMethods2D.clearExpressionAndGraph();
            graphicMethods2D.addExpression(expr);
            graphicMethods2D.addExpression(tangent);
            graphicMethods2D.setVarAbsc(var);
            graphicMethods2D.computeScreenSizes(new Constant(x_0), new Constant(x_1));
            graphicMethods2D.expressionToGraph(var, new Constant(x_0), new Constant(x_1));
            graphicMethods2D.setSpecialPointsOccur(true);
            graphicMethods2D.setSpecialPoints(tangentPoint);
            graphicMethods2D.drawGraph2D();

        }

    }

    private static void executeTaylorDEQ(Command command, GraphicArea graphicArea) throws EvaluationException {

        int ord = (int) command.getParams()[2];
        HashSet<String> vars = new HashSet<>();
        Expression expr = ((Expression) command.getParams()[0]).simplify();
        expr.addContainedVars(vars);

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        Iterator iter = vars.iterator();
        String varWithoutPrimes;
        for (int i = 0; i < vars.size(); i++) {
            varWithoutPrimes = (String) iter.next();
            if (!varWithoutPrimes.replaceAll("'", "").equals(command.getParams()[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new EvaluationException(Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_1")
                            + ord
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_2")
                            + (ord - 1)
                            + Translator.translateExceptionMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DEQ_3"));
                }
                varWithoutPrimes = varWithoutPrimes.replaceAll("'", "");
            }
            varsWithoutPrimes.add(varWithoutPrimes);
        }

        String varAbsc = (String) command.getParams()[1];
        Expression x_0 = (Expression) command.getParams()[3];
        Expression[] y_0 = new Expression[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (Expression) command.getParams()[i + 4];
        }

        int k = (int) command.getParams()[ord + 4];

        /*
         Zunächst muss der Name der Variablen y in der DGL y' = expr ermittelt
         werden. Falls dieser nicht eindeutig ist, wird "Y" vorgegeben.
         */
        String varOrd;

        if (varsWithoutPrimes.isEmpty()) {
            if (varAbsc.equals("y")) {
                varOrd = "z";
            } else {
                varOrd = "y";
            }
        } else if (varsWithoutPrimes.size() == 1) {
            if (varsWithoutPrimes.contains(varAbsc)) {
                if (varAbsc.equals("y")) {
                    varOrd = "z";
                } else {
                    varOrd = "y";
                }
            } else {
                iter = varsWithoutPrimes.iterator();
                varOrd = (String) iter.next();
            }
        } else {
            iter = varsWithoutPrimes.iterator();
            varOrd = (String) iter.next();
            if (varOrd.equals(varAbsc)) {
                varOrd = (String) iter.next();
            }
        }

        Expression result = AnalysisMethods.getTaylorPolynomialFromDifferentialEquation(expr, varAbsc, varOrd, ord, x_0, y_0, k);
        // Texttliche Ausgabe
        output.add(varOrd + "(" + varAbsc + ") = " + result.writeExpression() + "\n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(varOrd + "(" + varAbsc + ") = ", result);

    }

    private static void executeUndefine(Command comand, HashMap definedVars, GraphicArea graphicArea)
            throws EvaluationException {

        Object[] vars = comand.getParams();
        for (Object var : vars) {
            if (definedVars.containsKey((String) var)) {
                definedVars.remove((String) var);
                // Texttliche Ausgabe
                output.add(Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_1") + (String) var + Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_2") + " \n \n");
                // Graphische Ausgabe
                graphicArea.addComponent(Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_1") + (String) var + Translator.translateExceptionMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN_2"));
            }
        }

    }

    private static void executeUndefineAll(HashMap definedVars, GraphicArea graphicArea) {

        definedVars.clear();
        // Texttliche Ausgabe
        output.add(Translator.translateExceptionMessage("MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN") + " \n \n");
        // Graphische Ausgabe
        graphicArea.addComponent(Translator.translateExceptionMessage("MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN"));

    }

}
