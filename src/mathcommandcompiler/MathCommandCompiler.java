package mathcommandcompiler;

import command.Command;
import command.TypeCommand;
import abstractexpressions.expression.computation.AnalysisMethods;
import abstractexpressions.expression.computation.NumericalMethods;
import abstractexpressions.expression.computation.StatisticMethods;
import enums.TypeSimplify;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import abstractexpressions.expression.classes.Expression;
import static abstractexpressions.expression.classes.Expression.MINUS_ONE;
import static abstractexpressions.expression.classes.Expression.ONE;
import static abstractexpressions.expression.classes.Expression.TWO;
import static abstractexpressions.expression.classes.Expression.ZERO;
import abstractexpressions.expression.classes.SelfDefinedFunction;
import abstractexpressions.expression.classes.TypeFunction;
import abstractexpressions.expression.classes.TypeOperator;
import abstractexpressions.expression.classes.Variable;
import abstractexpressions.expression.diferentialequation.SolveGeneralDifferentialEquationMethods;
import abstractexpressions.expression.utilities.ExpressionCollection;
import abstractexpressions.expression.utilities.SimplifyPolynomialMethods;
import graphic.GraphicArea;
import graphic.GraphicPanel2D;
import graphic.GraphicPanel3D;
import graphic.GraphicPanelCurves2D;
import graphic.GraphicPanelCurves3D;
import graphic.GraphicPanelImplicit2D;
import graphic.GraphicPanelPolar;
import abstractexpressions.expression.classes.MultiIndexVariable;
import graphic.TypeBracket;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextArea;
import abstractexpressions.matrixexpression.computation.EigenvaluesEigenvectorsAlgorithms;
import abstractexpressions.matrixexpression.computation.GaussAlgorithm;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.logicalexpression.classes.LogicalVariable;
import mathtool.MathToolGUI;
import abstractexpressions.matrixexpression.classes.Matrix;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import abstractexpressions.matrixexpression.utilities.MatrixExpressionCollection;
import operationparser.OperationParser;
import abstractexpressions.expression.equation.SolveGeneralEquationMethods;
import computationbounds.ComputationBounds;
import graphic.GraphicPanelCylindrical;
import graphic.GraphicPanelSpherical;
import graphic.GraphicPanelVectorField2D;
import graphic.GraphicPanelVectorField3D;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import notations.NotationLoader;
import lang.translator.Translator;
import mathtool.annotations.Execute;
import mathtool.annotations.GetCommand;
import mathtool.component.components.LegendGUI;

public abstract class MathCommandCompiler {

    // Patterns für die einzelnen Befehle.
    public static final String PATTERN_APPROX_EXPR = "approx(expr)";
    public static final String PATTERN_APPROX_MATEXPR = "approx(matexpr)";
    @GetCommand(type = TypeCommand.ccnf)
    public static final String PATTERN_CCNF = "ccnf(logexpr)";
    @GetCommand(type = TypeCommand.cdnf)
    public static final String PATTERN_CDNF = "cdnf(logexpr)";
    @GetCommand(type = TypeCommand.clear)
    public static final String PATTERN_CLEAR = "clear()";
    @GetCommand(type = TypeCommand.deffuncs)
    public static final String PATTERN_DEFFUNCS = "deffuncs()";
    @GetCommand(type = TypeCommand.defvars)
    public static final String PATTERN_DEFVARS = "defvars()";
    @GetCommand(type = TypeCommand.eigenvalues)
    public static final String PATTERN_EIGENVALUES = "eigenvalues(matexpr)";
    @GetCommand(type = TypeCommand.eigenvectors)
    public static final String PATTERN_EIGENVECTORS = "eigenvectors(matexpr)";
    @GetCommand(type = TypeCommand.euler)
    public static final String PATTERN_EULER = "euler(integer(0,2147483647))";
    @GetCommand(type = TypeCommand.expand)
    public static final String PATTERN_EXPAND = "expand(expr)";
    public static final String PATTERN_EXTREMA_ONE_VAR = "extrema(expr(0,1))";
    public static final String PATTERN_EXTREMA_WITH_PARAMETER = "extrema(expr,indet)";
    public static final String PATTERN_EXTREMA_APPROX = "extrema(expr(0,1),expr(0,0),expr(0,0))";
    public static final String PATTERN_EXTREMA_APPROX_WITH_NUMBER_OF_INTERVALS = "extrema(expr(0,1),expr(0,0),expr(0,0),integer(0,2147483647))";
    @GetCommand(type = TypeCommand.ker)
    public static final String PATTERN_KER = "ker(matexpr)";
    @GetCommand(type = TypeCommand.pi)
    public static final String PATTERN_PI = "pi(integer(0,2147483647))";
    @GetCommand(type = TypeCommand.regressionline)
    public static final String PATTERN_REGRESSIONLINE = "regressionline(matexpr,matexpr+)";
    public static final String PATTERN_SOLVE_ONE_VAR = "solve(equation(0,1))";
    public static final String PATTERN_SOLVE_WITH_PARAMETER = "solve(equation,indet)";
    public static final String PATTERN_SOLVE_APPROX = "solve(equation(0,1),expr(0,0),expr(0,0))";
    public static final String PATTERN_SOLVE_APPROX_WITH_NUMBER_OF_INTERVALS = "solve(equation(0,1),expr(0,0),expr(0,0),integer(0,2147483647))";
    @GetCommand(type = TypeCommand.solvesystem)
    public static final String PATTERN_SOLVESYSTEM = "solvesystem(equation+,uniqueindet+)";
    @GetCommand(type = TypeCommand.table)
    public static final String PATTERN_TABLE = "table(logexpr)";
    @GetCommand(type = TypeCommand.undefvars)
    public static final String PATTERN_UNDEFVARS = "undefvars(var+)";
    @GetCommand(type = TypeCommand.undefallvars)
    public static final String PATTERN_UNDEFALLVARS = "undefallvars()";
    @GetCommand(type = TypeCommand.undefallfuncs)
    public static final String PATTERN_UNDEFALLFUNCS = "undefallfuncs()";
    @GetCommand(type = TypeCommand.undefall)
    public static final String PATTERN_UNDEFALL = "undefall()";

    private static GraphicPanel2D graphicPanel2D;
    private static GraphicPanel3D graphicPanel3D;
    private static GraphicPanelImplicit2D graphicPanelImplicit2D;
    private static GraphicPanelCurves2D graphicPanelCurves2D;
    private static GraphicPanelCurves3D graphicPanelCurves3D;
    private static GraphicPanelPolar graphicPanelPolar2D;
    private static GraphicPanelCylindrical graphicPanelCylindrical;
    private static GraphicPanelSpherical graphicPanelSpherical;
    private static GraphicPanelVectorField2D graphicPanelVectorField2D;
    private static GraphicPanelVectorField3D graphicPanelVectorField3D;

    private static GraphicArea mathToolGraphicArea;
    private static JTextArea mathToolTextArea;

    private static final HashSet simplifyTypesExpand = getSimplifyTypesExpand();
    private static final HashSet simplifyTypesExpandShort = getSimplifyTypesExpandShort();
    private static final HashSet simplifyTypesPlot = getSimplifyTypesPlot();
    private static final HashSet simplifyTypesSolveSystem = getSimplifyTypesSolveSystem();

    private static HashSet<TypeSimplify> getSimplifyTypesExpand() {
        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypes.add(TypeSimplify.simplify_expand_powerful);
        simplifyTypes.add(TypeSimplify.simplify_collect_products);
        simplifyTypes.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypes.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypes.add(TypeSimplify.simplify_reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypes.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        return simplifyTypes;
    }

    private static HashSet<TypeSimplify> getSimplifyTypesExpandShort() {
        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypes.add(TypeSimplify.simplify_expand_short);
        simplifyTypes.add(TypeSimplify.simplify_collect_products);
        simplifyTypes.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypes.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypes.add(TypeSimplify.simplify_reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypes.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        return simplifyTypes;
    }

    private static HashSet<TypeSimplify> getSimplifyTypesPlot() {
        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypes.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypes.add(TypeSimplify.simplify_collect_products);
        simplifyTypes.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypes.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypes.add(TypeSimplify.simplify_reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        return simplifyTypes;
    }

    private static HashSet<TypeSimplify> getSimplifyTypesSolveSystem() {
        HashSet<TypeSimplify> simplifyTypes = new HashSet<>();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_trivial);
        simplifyTypes.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypes.add(TypeSimplify.simplify_expand_powerful);
        simplifyTypes.add(TypeSimplify.simplify_collect_products);
        simplifyTypes.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypes.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypes.add(TypeSimplify.simplify_reduce_leadings_coefficients);
        simplifyTypes.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypes.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        return simplifyTypes;
    }

    public static void setGraphicPanel2D(GraphicPanel2D gP2D) {
        graphicPanel2D = gP2D;
    }

    public static void setGraphicPanel3D(GraphicPanel3D gP3D) {
        graphicPanel3D = gP3D;
    }

    public static void setGraphicPanelImplicit2D(GraphicPanelImplicit2D gPImplicit2D) {
        graphicPanelImplicit2D = gPImplicit2D;
    }

    public static void setGraphicPanelCurves2D(GraphicPanelCurves2D gPCurves2D) {
        graphicPanelCurves2D = gPCurves2D;
    }

    public static void setGraphicPanelCurves3D(GraphicPanelCurves3D gPCurves3D) {
        graphicPanelCurves3D = gPCurves3D;
    }

    public static void setGraphicPanelPolar2D(GraphicPanelPolar gPPolar2D) {
        graphicPanelPolar2D = gPPolar2D;
    }

    public static void setGraphicPanelCylindrical(GraphicPanelCylindrical gPCylindrical) {
        graphicPanelCylindrical = gPCylindrical;
    }

    public static void setGraphicPanelSpherical(GraphicPanelSpherical gPSpherical) {
        graphicPanelSpherical = gPSpherical;
    }

    public static void setGraphicPanelVectorField2D(GraphicPanelVectorField2D gPVectorField2D) {
        graphicPanelVectorField2D = gPVectorField2D;
    }

    public static void setGraphicPanelVectorField3D(GraphicPanelVectorField3D gPVectorField3D) {
        graphicPanelVectorField3D = gPVectorField3D;
    }

    public static void setMathToolGraphicArea(GraphicArea mTGraphicArea) {
        mathToolGraphicArea = mTGraphicArea;
    }

    public static void setMathToolTextArea(JTextArea mTTextArea) {
        mathToolTextArea = mTTextArea;
    }

    /**
     * Leitet die Ausgabe an die textliche und die grafische Ausgabeoberfläche
     * weiter.
     */
    public static void doPrintOutput(Object... out) {

        // Textliche Ausgabe.
        String lineToPrint = "";
        for (Object o : out) {
            lineToPrint += o.toString();
        }
        mathToolTextArea.append(lineToPrint + " \n \n");

        // Grafische Ausgabe.
        mathToolGraphicArea.addComponent(out);

    }

    /**
     * Leitet die Ausgabe an die textliche und die grafische Ausgabeoberfläche
     * weiter.
     */
    private static void doPrintOutput(ArrayList out) {

        // Textliche Ausgabe.
        String lineToPrint = "";
        boolean nextExpressionIsSurroundedByBrackets = false;
        for (Object o : out) {
            if (o instanceof TypeBracket) {
                nextExpressionIsSurroundedByBrackets = true;
            } else if (nextExpressionIsSurroundedByBrackets) {
                lineToPrint += "(" + o.toString() + ")";
                nextExpressionIsSurroundedByBrackets = false;
            } else {
                lineToPrint += o.toString();
            }
        }
        mathToolTextArea.append(lineToPrint + " \n \n");

        // Grafische Ausgabe.
        mathToolGraphicArea.addComponent(out);

    }

    /**
     * Diese Funktion wird zum Prüfen für die Vergabe neuer Funktionsnamen
     * benötigt. Sie prüft nach, ob name keine bereits definierte Funktion,
     * Operator oder Befehl ist. Ferner dürfen die Zeichen + - * / ^ nicht
     * enthalten sein.
     */
    private static boolean isNotForbiddenName(String name) {

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
    private static boolean containsNoSpecialCharacters(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!((int) name.charAt(i) >= 48 && (int) name.charAt(i) < 57) && !((int) name.charAt(i) >= 97 && (int) name.charAt(i) < 122)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Diese Funktion wird zum Prüfen für die Vergabe neuer Funktionsnamen
     * benötigt. Sie prüft nach, ob der Funktionsname nur aus Ziffern besteht.
     */
    private static boolean isNameIntegerNumber(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!((int) name.charAt(i) >= 48 && (int) name.charAt(i) < 57)) {
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
    public static Command getCommand(String commandName, String[] params) throws ExpressionException {

        // Sonderfälle_ überladene Befehle.
        switch (commandName) {
            case "approx":
                try {
                    return OperationParser.parseDefaultCommand(commandName, params, PATTERN_APPROX_EXPR);
                } catch (ExpressionException e) {
                    try {
                        return OperationParser.parseDefaultCommand(commandName, params, PATTERN_APPROX_MATEXPR);
                    } catch (ExpressionException ex) {
                        throw new ExpressionException(Translator.translateOutputMessage("MCC_PARAMETER_IN_APPROX_IS_INVALID"));
                    }
                }
            case "extrema":
                if (params.length <= 1) {
                    return OperationParser.parseDefaultCommand(commandName, params, PATTERN_EXTREMA_ONE_VAR);
                }
                if (params.length == 2) {
                    return OperationParser.parseDefaultCommand(commandName, params, PATTERN_EXTREMA_WITH_PARAMETER);
                }
                if (params.length == 3) {
                    return OperationParser.parseDefaultCommand(commandName, params, PATTERN_EXTREMA_APPROX);
                }
                return OperationParser.parseDefaultCommand(commandName, params, PATTERN_EXTREMA_APPROX_WITH_NUMBER_OF_INTERVALS);
            case "solve":
                if (params.length <= 1) {
                    return OperationParser.parseDefaultCommand(commandName, params, PATTERN_SOLVE_ONE_VAR);
                }
                if (params.length == 2) {
                    return OperationParser.parseDefaultCommand(commandName, params, PATTERN_SOLVE_WITH_PARAMETER);
                }
                if (params.length == 3) {
                    return OperationParser.parseDefaultCommand(commandName, params, PATTERN_SOLVE_APPROX);
                }
                return OperationParser.parseDefaultCommand(commandName, params, PATTERN_SOLVE_APPROX_WITH_NUMBER_OF_INTERVALS);
        }

        // Befehle, die man mittels parseDefaultCommand() kompilieren kann.
        // Mittels Reflection das passende Pattern suchen.
        Field[] fields = MathCommandCompiler.class.getDeclaredFields();
        GetCommand annotation;
        for (Field field : fields) {
            annotation = field.getAnnotation(GetCommand.class);
            try {
                if (annotation != null && annotation.type().name().equals(commandName)) {
                    return OperationParser.parseDefaultCommand(commandName, params, (String) field.get(null));
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
            }
        }

        // Befehle, die für das Kompilieren individuelle Methoden benötigen (diese sind entsprechend annotiert).
        Method[] methods = MathCommandCompiler.class.getDeclaredMethods();
        for (Method method : methods) {
            annotation = method.getAnnotation(GetCommand.class);
            if (annotation != null && annotation.type().name().equals(commandName)) {
                try {
                    return (Command) method.invoke(null, new Object[]{params});
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    if (e.getCause() instanceof ExpressionException) {
                        // Methoden können nur EvaluationExceptions werfen.
                        throw (ExpressionException) e.getCause();
                    }
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_INVALID_COMMAND"));
                }
            }
        }

        // Sollte theoretisch nie vorkommen.
        throw new ExpressionException(Translator.translateOutputMessage("MCC_INVALID_COMMAND"));

    }

    @GetCommand(type = TypeCommand.def)
    private static Command getCommandDef(String[] params) throws ExpressionException {

        // Struktur: def(VAR = VALUE) oder def(FUNCTION(VAR_1, ..., VAR_n) = EXPRESSION(VAR_1, ..., VAR_n))
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEF"));
        }

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NO_EQUAL_IN_DEF"));
        }

        String functionNameAndArguments = params[0].substring(0, params[0].indexOf("="));
        String functionTerm = params[0].substring(params[0].indexOf("=") + 1, params[0].length());

        /*
         Falls der linke Teil eine Variable ist, dann ist es eine
         Zuweisung, die dieser Variablen einen festen Wert zuweist.
         Beispiel: def(x = 2) liefert: result.name = "def" result.params =
         {"x"} result.left = 2 (als Expression)
         */
        if (Expression.isValidDerivativeOfVariable(functionNameAndArguments) && !Expression.isPI(functionNameAndArguments)) {

            Expression preciseExpression;
            HashSet<String> vars;
            try {
                preciseExpression = Expression.build(functionTerm, null);
                vars = preciseExpression.getContainedIndeterminates();
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_TO_VARIABLE_MUST_BE_ASSIGNED_REAL_VALUE"));
            }
            if (!vars.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_TO_VARIABLE_MUST_BE_ASSIGNED_CONSTANT_REAL_VALUE"));
            }

            return new Command(TypeCommand.def, new Object[]{functionNameAndArguments, preciseExpression});

        }

        Expression expr;
        /*
         Nun wird geprüft, ob es sich um eine Funktionsdeklaration
         handelt. Zunächst wird versucht, den rechten Teilstring vom "="
         in einen Ausdruck umzuwandeln.
         */
        try {
            expr = Expression.build(functionTerm, null);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INVALID_EXPRESSION_ON_RIGHT_SIDE") + e.getMessage());
        }

        HashSet<String> vars = expr.getContainedIndeterminates();

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
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INVALID_DEF"));
        }

        // Prüfen, ob nicht geschützte Funktionen (wie z.B. sin, tan etc.) überschrieben werden.
        if (!isNotForbiddenName(functionName)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_PROTECTED_FUNC_NAME", functionName));
        }

        // Prüfen, ob keine Sonderzeichen vorkommen.
        if (!containsNoSpecialCharacters(functionName)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS", functionName));
        }

        // Prüfen, ob der Funktionsname nicht nur aus Ziffern besteht.
        if (isNameIntegerNumber(functionName)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_FUNC_NAME_CONTAINS_ONLY_DIGITS", functionName));
        }

        /*
         Falls functionsVars leer ist -> Fehler ausgeben (es muss
         mindestens eine Variable vorhanden sein).
         */
        if (functionVars.length == 0) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_IS_NO_FUNCTION_VARS_IN_FUNCTION_DECLARATION"));
        }

        // Wird geprüft, ob die einzelnen Parameter in der Funktionsklammer gültige Variablen sind.
        for (String functionVar : functionVars) {
            if (!Expression.isValidDerivativeOfVariable(functionVar) || Variable.getVariablesWithPredefinedValues().contains(functionVar)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_IS_NOT_VALID_VARIABLE_1") + functionVar + Translator.translateOutputMessage("MCC_IS_NOT_VALID_VARIABLE_2"));
            }
        }

        // Wird geprüft, ob die Variablen in function_vars auch alle verschieden sind!
        List<String> functionVarsAsList = new ArrayList<>();
        for (String functionVar : functionVars) {
            if (functionVarsAsList.contains(functionVar)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLES_OCCUR_TWICE_IN_DEF", functionName));
            }
            functionVarsAsList.add(functionVar);
        }

        /*
         Hier wird den Variablen der Index "_ABSTRACT" angehängt. Dies
         dient der Kennzeichnung, dass diese Variablen Platzhalter für
         weitere Ausdrücke und keine echten Variablen sind. Solche
         Variablen können niemals in einem geparsten Ausdruck vorkommen,
         da der Parser Expression.build solche Variablen nicht akzeptiert.
         */
        for (int i = 0; i < functionVars.length; i++) {
            functionVars[i] = NotationLoader.SELFDEFINEDFUNCTION_VAR + "_" + (i + 1);
        }

        /*
         Prüfen, ob alle Variablen, die in expr auftreten, auch als
         Funktionsparameter vorhanden sind. Sonst -> Fehler ausgeben.
         Zugleich: Im Ausdruck expr werden alle Variablen der Form var
         durch Variablen der Form var_ABSTRACT ersetzt und alle Variablen
         im HashSet vars ebenfalls.
         */
        Iterator<String> iter = vars.iterator();
        String var;
        for (int i = 0; i < vars.size(); i++) {
            var = iter.next();
            expr = expr.replaceVariable(var, Variable.create(NotationLoader.SELFDEFINEDFUNCTION_VAR + "_" + (functionVarsAsList.indexOf(var) + 1)));
            if (!functionVarsAsList.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_RIGHT_SIDE_OF_DEF_CONTAINS_WRONG_VAR"));
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
         result.type = TypeCommand.def result.params = {"f", "U_1", "U_2"} 
         result.left = U_1^2+U_2 (als Expression).
         */
        return new Command(TypeCommand.def, commandParams);

    }

    @GetCommand(type = TypeCommand.latex)
    private static Command getCommandLatex(String[] params) throws ExpressionException {

        /*
         Struktur: latex(EXPRESSION) EXPRESSION: Ausdruck, welcher in einen
         Latex-Code umgewandelt werden soll.
         */
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_LATEX"));
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
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_LATEX") + e.getMessage());
        }

    }

    @GetCommand(type = TypeCommand.plot2d)
    private static Command getCommandPlot2D(String[] params) throws ExpressionException {

        /*
         Struktur: plot2d(f_1(var), ..., f_n(var), var, x_0, x_1), f_i(var): 
         Ausdruck in einer Variablen. x_0 < x_1: Grenzen des Zeichenbereichs.
         */
        if (params.length < 4) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT2D"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 3; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT2D", i + 1));
            }
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 3])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT2D", params.length - 2));
        }

        commandParams[params.length - 3] = params[params.length - 3];
        vars.remove(params[params.length - 3]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT2D", params[params.length - 3]));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        try {
            commandParams[params.length - 2] = Expression.build(params[params.length - 2], null);
            ((Expression) commandParams[params.length - 2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length - 1));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length - 1));
        }

        try {
            commandParams[params.length - 1] = Expression.build(params[params.length - 1], null);
            ((Expression) commandParams[params.length - 1]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length));
        }

        return new Command(TypeCommand.plot2d, commandParams);

    }

    @GetCommand(type = TypeCommand.plotimplicit)
    private static Command getCommandPlotImplicit2D(String[] params) throws ExpressionException {

        /*
         Struktur: plotimplicit(F(x, y) = G(x, y), x, y, x_0, x_1, y_0, y_1). 
         F, G: Ausdrücke in höchstens zwei Variablen x, y. x_0 < x_1,
         y_0 < y_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT2D"));
        }

        Object[] commandParams = new Object[7];
        HashSet<String> vars = new HashSet<>();

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT2D_MUST_BE_A_VALID_EQUATION"));
        }

        Expression left, right;
        try {
            left = Expression.build(params[0].substring(0, params[0].indexOf("=")), null);
            right = Expression.build(params[0].substring(params[0].indexOf("=") + 1), null);
            left.addContainedIndeterminates(vars);
            right.addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT2D_MUST_BE_A_VALID_EQUATION"));
        }

        commandParams[0] = new Expression[]{left, right};

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT2D", 1));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT2D", 2));
        }
        if (params[1].equals(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT2D"));
        }

        commandParams[1] = params[1];
        commandParams[2] = params[2];
        vars.remove(params[1]);
        vars.remove(params[2]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT2D", params[1], params[2]));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < 7; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D", i + 1));
            }
        }

        return new Command(TypeCommand.plotimplicit, commandParams);

    }

    @GetCommand(type = TypeCommand.plot3d)
    private static Command getCommandPlot3D(String[] params) throws ExpressionException {

        /*
         Struktur: plot3d(F_1(var1, var2), ..., F_n(var1, var2), value_1, value_2, value_3,
         value_4) F_i: Ausdruck in höchstens zwei Variablen. value_1 <
         value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         Variablen werden dabei alphabetisch geordnet.
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT3D"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT3D", i + 1, e.getMessage()));
            }
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 6])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT3D", params.length - 6));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 5])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT3D", params.length - 5));
        }
        if (params[params.length - 6].equals(params[params.length - 5])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOT3D"));
        }

        commandParams[params.length - 6] = params[params.length - 6];
        commandParams[params.length - 5] = params[params.length - 5];
        vars.remove(params[params.length - 6]);
        vars.remove(params[params.length - 5]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT3D", params[params.length - 6], params[params.length - 5]));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D", i + 1));
            }
        }

        return new Command(TypeCommand.plot3d, commandParams);

    }

    @GetCommand(type = TypeCommand.plotcurve2d)
    private static Command getCommandPlotCurve2D(String[] params) throws ExpressionException {

        /*
         Struktur: plotcurve2d(matexpr(var), var, t_0, t_1), matexpr(var): 
         Matrizenusdruck in einer Variablen. t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 4) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE2D"));
        }

        Object[] commandParams = new Object[4];
        HashSet<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0], null);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE2D", e.getMessage()));
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE2D"));
        }

        commandParams[1] = params[1];
        vars.remove(params[1]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE2D", params[1]));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        try {
            commandParams[2] = Expression.build(params[2], null);
            ((Expression) commandParams[2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D", 3));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D", 3));
        }

        try {
            commandParams[3] = Expression.build(params[3], null);
            ((Expression) commandParams[3]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D", 4));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D", 4));
        }

        return new Command(TypeCommand.plotcurve2d, commandParams);

    }

    @GetCommand(type = TypeCommand.plotcurve3d)
    private static Command getCommandPlotCurve3D(String[] params) throws ExpressionException {

        /*
         Struktur: plotcurve2d(matexpr(var), var, t_0, t_1), matexpr(var): 
         Matrizenusdruck in einer Variablen. t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 4) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE3D"));
        }

        Object[] commandParams = new Object[4];
        HashSet<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0], null);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE3D", e.getMessage()));
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE3D"));
        }

        commandParams[1] = params[1];
        vars.remove(params[1]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE3D", params[1]));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        try {
            commandParams[2] = Expression.build(params[2], null);
            ((Expression) commandParams[2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D", 3));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D", 3));
        }

        try {
            commandParams[3] = Expression.build(params[3], null);
            ((Expression) commandParams[3]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D", 4));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D", 4));
        }

        return new Command(TypeCommand.plotcurve3d, commandParams);

    }

    @GetCommand(type = TypeCommand.plotcylindrical)
    private static Command getCommandPlotCylindrical(String[] params) throws ExpressionException {

        /*
         Struktur: plotcylindrical(F_1(var_1, var_2), ..., F_n(var_1, var_2), var_1, var_2, value_1, value_2, value_3,
         value_4) F_i: Ausdruck in höchstens zwei Variablen. value_1 <
         value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         Variablen werden dabei alphabetisch geordnet.
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTCYLINDRICAL"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTCYLINDRICAL", i + 1, e.getMessage()));
            }
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTCYLINDRICAL", String.valueOf(vars.size())));
        }

        HashSet<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!Expression.isValidDerivativeOfVariable(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTCYLINDRICAL", i + 1));
            }
            if (varsInParams.contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTCYLINDRICAL"));
            }
            varsInParams.add(params[i]);
            commandParams[i] = params[i];
        }

        // Prüfen, ob Veränderliche, die in vars auftreten, auch in varsInParams auftreten.
        for (String var : vars) {
            if (!varsInParams.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTCYLINDRICAL", var));
            }
        }

        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCYLINDRICAL", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCYLINDRICAL", i + 1));
            }
        }

        return new Command(TypeCommand.plotcylindrical, commandParams);

    }

    @GetCommand(type = TypeCommand.plotspherical)
    private static Command getCommandPlotSpherical(String[] params) throws ExpressionException {

        /*
         Struktur: plotspherical(F_1(var_1, var_2), ..., F_n(var_1, var_2), var_1, var_2, value_1, value_2, value_3,
         value_4) F_i: Ausdruck in höchstens zwei Variablen. value_1 <
         value_2, value_3 < value_4: Grenzen des Zeichenbereichs. Die beiden
         Variablen werden dabei alphabetisch geordnet.
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTSPHERICAL"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTSPHERICAL", i + 1, e.getMessage()));
            }
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSPHERICAL", String.valueOf(vars.size())));
        }

        HashSet<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!Expression.isValidDerivativeOfVariable(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSPHERICAL", i + 1));
            }
            if (varsInParams.contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSPHERICAL"));
            }
            varsInParams.add(params[i]);
            commandParams[i] = params[i];
        }

        // Prüfen, ob Veränderliche, die in vars auftreten, auch in varsInParams auftreten.
        for (String var : vars) {
            if (!varsInParams.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSPHERICAL", var));
            }
        }

        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSPHERICAL", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSPHERICAL", i + 1));
            }
        }

        return new Command(TypeCommand.plotspherical, commandParams);

    }

    @GetCommand(type = TypeCommand.plotvectorfield2d)
    private static Command getCommandPlotVectorField2D(String[] params) throws ExpressionException {

        /*
         Struktur: plotvectorfield2d(matexpr(x, y), x, y, x_0, x_1, y_0, y_1), matexpr(x, y): 
         Matrizenusdruck in zwei Variablen. x_0 < x_1, y_0 < y_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELD2D"));
        }

        Object[] commandParams = new Object[7];
        HashSet<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0], null);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELD2D", e.getMessage()));
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELD2D", 2));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELD2D", 3));
        }
        if (params[1].equals(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELD2D"));
        }

        commandParams[1] = params[1];
        commandParams[2] = params[2];
        vars.remove(params[1]);
        vars.remove(params[2]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELD2D", params[1], params[2]));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < 7; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD2D", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD2D", i + 1));
            }
        }

        return new Command(TypeCommand.plotvectorfield2d, commandParams);

    }

//    @GetCommand(type = TypeCommand.plotvectorfield3d)
//    private static Command getCommandPlotVectorField3D(String[] params) throws ExpressionException {
//
//        /*
//         Struktur: plotvectorfield3d(matexpr(x, y, z), x, y, z, x_0, x_1, y_0, y_1, z_0, z_1), matexpr(x, y): 
//         Matrizenusdruck in zwei Variablen. x_0 < x_1, y_0 < y_1: Grenzen des Zeichenbereichs.
//         */
//        if (params.length != 10) {
//            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELD3D"));
//        }
//
//        Object[] commandParams = new Object[10];
//        HashSet<String> vars = new HashSet<>();
//
//        try {
//            commandParams[0] = MatrixExpression.build(params[0], null);
//            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
//        } catch (ExpressionException e) {
//            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELD3D", e.getMessage()));
//        }
//
//        for (int i = 1; i < 4; i++) {
//            if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
//                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELD3D", i + 1));
//            }
//        }
//        for (int i = 1; i < 4; i++) {
//            for (int j = i + 1; j < 4; j++) {
//                if (params[1].equals(params[2])) {
//                    throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELD3D", i + 1, j + 1));
//                }
//            }
//        }
//
//        for (int i = 1; i < 4; i++) {
//            commandParams[i] = params[i];
//            vars.remove(params[i]);
//        }
//
//        if (!vars.isEmpty()) {
//            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELD2D", params[1], params[2]));
//        }
//
//        HashSet<String> varsInLimits = new HashSet<>();
//        for (int i = 4; i < 10; i++) {
//            try {
//                commandParams[i] = Expression.build(params[i], null);
//                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
//                if (!varsInLimits.isEmpty()) {
//                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD3D", i + 1));
//                }
//            } catch (ExpressionException e) {
//                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD3D", i + 1));
//            }
//        }
//
//        return new Command(TypeCommand.plotvectorfield3d, commandParams);
//
//    }

    @GetCommand(type = TypeCommand.plotpolar)
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
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTPOLAR"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 2; i++) {
            try {
                commandParams[i] = Expression.build(params[i], null);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTPOLAR", i + 1));
            }
        }

        if (vars.size() > 1) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTPOLAR", vars.size()));
        }

        HashSet<String> varsInLimits = new HashSet<>();
        try {
            commandParams[params.length - 2] = Expression.build(params[params.length - 2], null);
            ((Expression) commandParams[params.length - 2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR", params.length - 1));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR", params.length - 1));
        }

        try {
            commandParams[params.length - 1] = Expression.build(params[params.length - 1], null);
            ((Expression) commandParams[params.length - 1]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR", params.length));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR", params.length));
        }

        return new Command(TypeCommand.plotpolar, commandParams);

    }

    @GetCommand(type = TypeCommand.solvediffeq)
    private static Command getCommandSolveDiffEq(String[] params) throws ExpressionException {

        if (params.length <= 3) {
            return getCommandSolveDiffEquationAlgebraic(params);
        }
        return getCommandSolveDiffEquationNumeric(params);

    }

    private static Command getCommandSolveDiffEquationAlgebraic(String[] params) throws ExpressionException {

        /*
         Struktur: solvediffeq(Equation, varAbsc, varOrd), Equation: Die Differentialgleichung.
         */
        if (params.length != 3) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_SOLVEDIFFEQ"));
        }

        String varAbsc = params[1], varOrd = params[2];
        // Beide Variablen dürfen KEINE Apostrophs enthalten. Diese sind hier für Ableitungen resereviert.
        if (!Expression.isValidVariable(varAbsc)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_2_PARAMETER_IN_SOLVEDIFFEQ"));
        }
        if (!Expression.isValidVariable(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_SOLVEDIFFEQ"));
        }

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_1_PARAMETER_IN_SOLVEDIFFEQ_MUST_CONTAIN_EQUALITY_SIGN"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt.
         */
        HashSet<String> vars = new HashSet<>();
        Expression diffEquationLeft, diffEquationRight;
        try {
            diffEquationLeft = Expression.build(params[0].substring(0, params[0].indexOf("=")), null);
            diffEquationRight = Expression.build(params[0].substring(params[0].indexOf("=") + 1), null);
            diffEquationLeft.addContainedIndeterminates(vars);
            diffEquationRight.addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDIFFEQ") + e.getMessage());
        }

        int ord = 0;
        for (String var : vars) {
            if (var.startsWith(varOrd) && var.contains("'")) {
                ord = var.length() - var.replaceAll("'", "").length();
            }
        }

        if (ord == 0) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NO_DERIVATIVE_IN_1_PARAMETER_IN_SOLVEDIFFEQ"));
        }

        Object[] commandParams = new Object[3];
        commandParams[0] = new Expression[]{diffEquationLeft, diffEquationRight};
        commandParams[1] = params[1];
        commandParams[2] = params[2];

        return new Command(TypeCommand.solvediffeq, commandParams);

    }

    private static Command getCommandSolveDiffEquationNumeric(String[] params) throws ExpressionException {

        /*
         Struktur: solvediffeq(Equation, var, ord, x_0, x_1, y_0, y'(0), ...,
         y^(ord - 1)(0)) Equation: Rechte Seite der DGL y^{(ord)} =
         Equation. Anzahl der parameter ist also = ord + 5 var = Variable in
         der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         fest x_1 = Obere x-Schranke für die numerische Berechnung
         */
        if (params.length < 6) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_SOLVEDIFFEQ"));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[2]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_SOLVEDIFFEQ"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_SOLVEDIFFEQ"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt:
         Beispielsweise darf in einer DGL der ordnung 3 nicht y''',
         y'''' etc. auf der rechten Seite auftreten.
         */
        HashSet<String> vars;
        Expression diffEquation;
        try {
            diffEquation = Expression.build(params[0], null);
            vars = diffEquation.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDIFFEQ") + e.getMessage());
        }

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        Iterator iter = vars.iterator();
        String varWithoutPrimes;
        for (int i = 0; i < vars.size(); i++) {
            varWithoutPrimes = (String) iter.next();
            if (!varWithoutPrimes.replaceAll("'", "").equals(params[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ", ord, ord - 1));
                }
                varWithoutPrimes = varWithoutPrimes.replaceAll("'", "");
            }
            varsWithoutPrimes.add(varWithoutPrimes);
        }

        if (varsWithoutPrimes.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDIFFEQ"));
        }

        if (Expression.isValidVariable(params[1])) {
            if (varsWithoutPrimes.size() == 2) {
                if (!vars.contains(params[1])) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLE_MUST_OCCUR_IN_SOLVEDIFFEQ", params[1]));
                }
            }
        } else {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_2_PARAMETER_IN_SOLVEDIFFEQ"));
        }

        if (params.length < ord + 5) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDIFFEQ"));
        }
        if (params.length > ord + 5) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_TOO_MANY_PARAMETERS_IN_SOLVEDIFFEQ"));
        }

        // Prüft, ob die AWP-Daten korrekt sind.
        for (int i = 3; i < ord + 5; i++) {
            try {
                Expression limit = Expression.build(params[i], null);
                if (!limit.isConstant()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDIFFEQ", i + 1));
                }
                /*
                 Prüfen, ob die Grenzen ausgewerten werden können.
                 Dies ist notwendig, da es sich hier um eine
                 numerische Berechnung handelt (und nicht um eine
                 algebraische).
                 */
                limit.evaluate();
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDIFFEQ", i + 1));
            }
        }

        Object[] commandParams = new Object[ord + 5];
        commandParams[0] = diffEquation;
        commandParams[1] = params[1];
        commandParams[2] = ord;
        for (int i = 3; i < ord + 5; i++) {
            commandParams[i] = Expression.build(params[i], vars);
        }

        return new Command(TypeCommand.solvediffeq, commandParams);

    }

    @GetCommand(type = TypeCommand.tangent)
    private static Command getCommandTangent(String[] params) throws ExpressionException {

        /*
         Struktur: tangent(EXPRESSION, var_1 = value_1, ..., var_n = value_n)
         EXPRESSION: Ausdruck, welcher eine Funktion repräsentiert. var_i =
         Variable value_i = reelle Zahl. Es müssen alle Variablen unter den
         var_i vorkommen, welche auch in expr vorkommen.
         */
        if (params.length < 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TANGENT"));
        }

        HashSet<String> vars;
        Expression expr;
        try {
            expr = Expression.build(params[0], null);
            vars = expr.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_TANGENT") + e.getMessage());
        }

        /*
         Ermittelt die Anzahl der Variablen, von denen die Funktion
         abhängt, von der der Tangentialraum berechnet werden soll.
         */
        for (int i = 1; i < params.length; i++) {
            if (!params[i].contains("=")) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT", i + 1));
            }
            if (!Expression.isValidDerivativeOfVariable(params[i].substring(0, params[i].indexOf("=")))) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_A_VALID_VARIABLE_IN_TANGENT", params[i].substring(0, params[i].indexOf("="))));
            }
            try {
                Expression point = Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet<String>());
                if (!point.isConstant()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_TANGENT", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_TANGENT", i + 1));
            }
        }

        // Es wird geprüft, ob keine Veränderlichen doppelt auftreten.
        for (int i = 1; i < params.length; i++) {
            for (int j = i + 1; j < params.length; j++) {
                if (params[i].substring(0, params[i].indexOf("=")).equals(params[j].substring(0, params[j].indexOf("=")))) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT", params[i].substring(0, params[i].indexOf("="))));
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
        for (String var : vars) {
            if (!varsContainedInParams.containsKey(var)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLE_MUST_OCCUR_IN_TANGENT", var));
            }
        }

        return new Command(TypeCommand.tangent, new Object[]{expr, varsContainedInParams});

    }

    @GetCommand(type = TypeCommand.taylordiffeq)
    private static Command getCommandTaylorDiffEq(String[] params) throws ExpressionException {

        /*
         Struktur: taylordiffeq(Equation, var, ord, x_0, y_0, y'(0), ...,
         y^(ord - 1)(0), k) Equation: Rechte Seite der DGL y^{(ord)} =
         Equation. Anzahl der parameter ist also = ord + 5 var = Variable in
         der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         fest k = Ordnung des Taylorpolynoms (an der Stelle x_0)
         */
        if (params.length < 6) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ"));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[2]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_TAYLORDIFFEQ"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_3_PARAMETER_IN_TAYLORDIFFEQ"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt: Beispielsweise
         darf in einer DGL der ordnung 3 nicht y''', y'''' etc. auf der
         rechten Seite auftreten.
         */
        HashSet<String> vars;
        Expression diffEquation;
        try {
            diffEquation = Expression.build(params[0], null);
            vars = diffEquation.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_TAYLORDIFFEQ") + e.getMessage());
        }

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        String varWithoutPrimes;
        for (String var : vars) {
            varWithoutPrimes = var;
            if (!varWithoutPrimes.replaceAll("'", "").equals(params[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ", ord, ord - 1));
                }
                varWithoutPrimes = varWithoutPrimes.replaceAll("'", "");
            }
            varsWithoutPrimes.add(varWithoutPrimes);
        }

        if (varsWithoutPrimes.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDIFFEQ"));
        }

        if (Expression.isValidVariable(params[1]) && !Expression.isPI(params[1])) {
            if (varsWithoutPrimes.size() == 2) {
                if (!vars.contains(params[1])) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLE_MUST_OCCUR_IN_TAYLORDIFFEQ", params[1]));
                }
            }
        } else {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_2_PARAMETER_IN_TAYLORDIFFEQ"));
        }

        if (params.length < ord + 5) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ"));
        }

        if (params.length > ord + 5) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_TOO_MANY_PARAMETERS_IN_TAYLORDIFFEQ"));
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
        HashSet<String> varsInLimits;
        Expression limit;
        for (int i = 3; i < ord + 4; i++) {
            try {
                limit = Expression.build(params[i], null).simplify();
                varsInLimits = limit.getContainedIndeterminates();
                /*
                 Im Folgenden wird geprüft, ob in den Anfangsbedingungen
                 die Variablen aus der eigentlichen DGL nicht auftreten
                 (diese beiden Variablen sind im HashSet varsWithoutPrimes
                 gespeichert).
                 */
                for (String var : varsWithoutPrimes) {
                    if (varsInLimits.contains(var)) {
                        throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ", i + 1));
                    }
                }
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ", i + 1));
            }
        }

        try {
            Integer.parseInt(params[ord + 4]);
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_TAYLORDIFFEQ"));
        }

        Object[] commandParams = new Object[ord + 5];
        commandParams[0] = diffEquation;
        commandParams[1] = params[1];
        commandParams[2] = ord;
        for (int i = 3; i < ord + 4; i++) {
            commandParams[i] = Expression.build(params[i], vars);
        }
        commandParams[ord + 4] = Integer.parseInt(params[ord + 4]);

        return new Command(TypeCommand.taylordiffeq, commandParams);

    }

    @GetCommand(type = TypeCommand.undeffuncs)
    private static Command getCommandUndeffuncs(String[] params) throws ExpressionException {

        if (params.length < 1) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_UNDEFFUNCS"));
        }

        Object[] commandParams = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            if (!SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEFFUNCS", i + 1));
            }
            commandParams[i] = params[i];
        }

        return new Command(TypeCommand.undeffuncs, commandParams);

    }

    /**
     * Hauptmethode zum Ausführen des Befehls.
     *
     * @throws ExpressionException
     * @throws EvaluationException
     */
    public static void executeCommand(String input) throws ExpressionException, EvaluationException {

        input = input.replaceAll(" ", "").toLowerCase();

        String[] commandNameAndParams = Expression.getOperatorAndArguments(input);
        String commandName = commandNameAndParams[0];
        String[] params = Expression.getArguments(commandNameAndParams[1]);

        //Befehl ermitteln
        Command command = getCommand(commandName, params);

        // Mittels Reflection die passende Ausführmethode ermittln (durch Vergleich der Annotation).
        Method[] methods = MathCommandCompiler.class.getDeclaredMethods();
        Execute annotation;
        for (Method method : methods) {
            annotation = method.getAnnotation(Execute.class);
            if (annotation != null && annotation.type().equals(command.getTypeCommand())) {
                try {
                    method.invoke(null, command);
                    break;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    if (e.getCause() instanceof EvaluationException) {
                        // Methoden können nur EvaluationExceptions werfen.
                        throw (EvaluationException) e.getCause();
                    }
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_INVALID_COMMAND"));
                }
            }
        }

    }

    /**
     * Die folgenden Prozeduren führen einzelne Befehle aus. executePlot2D
     * zeichnet einen 2D-Graphen, executePlot3D zeichnet einen 3D-Graphen, etc.
     */
    @Execute(type = TypeCommand.approx)
    private static void executeApprox(Command command)
            throws EvaluationException {

        if (command.getParams()[0] instanceof Expression) {

            Expression expr = (Expression) command.getParams()[0];

            /*
             Falls expr selbstdefinierte Funktionen enthält, dann zunächst expr so
             darstellen, dass es nur vordefinierte Funktionen beinhaltet.
             */
            expr = expr.replaceSelfDefinedFunctionsByPredefinedFunctions();
            // Zunächst wird, soweit es geht, EXAKT vereinfacht, danach approximativ ausgewertet.
            expr = expr.simplify();
            expr = expr.turnToApproximate().simplify();
            /*
             Dies dient dazu, dass alle Variablen wieder "präzise" sind. Sie
             werden nur dann approximativ ausgegeben, wenn sie nicht präzise
             (precise = false) sind.
             */
            Variable.setAllPrecise(true);
            doPrintOutput(expr);

        } else if (command.getParams()[0] instanceof MatrixExpression) {

            MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
            // Mit Werten belegte Variablen müssen durch ihren exakten Ausdruck ersetzt werden.
            matExpr = matExpr.simplifyByInsertingDefinedVars();
            // Zunächst wird, soweit es geht, EXAKT vereinfacht, danach approximativ ausgewertet.
            matExpr = matExpr.simplify();
            matExpr = matExpr.turnToApproximate().simplify();
            /*
             Dies dient dazu, dass alle Variablen wieder "präzise" sind. Sie
             werden nur dann approximativ ausgegeben, wenn sie nicht präzise
             (precise = false) sind.
             */
            Variable.setAllPrecise(true);
            doPrintOutput(matExpr);

        }

    }

    @Execute(type = TypeCommand.ccnf)
    private static void executeCCNF(Command command) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        HashSet<String> vars = new HashSet<>();
        logExpr.addContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF", logExpr));
        }
        LogicalExpression logExprInCCNF = logExpr.toCCNF();
        doPrintOutput(logExprInCCNF);

    }

    @Execute(type = TypeCommand.cdnf)
    private static void executeCDNF(Command command) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        HashSet<String> vars = new HashSet<>();
        logExpr.addContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF", logExpr));
        }
        LogicalExpression logExprInCDNF = logExpr.toCDNF();
        doPrintOutput(logExprInCDNF);

    }

    @Execute(type = TypeCommand.clear)
    private static void executeClear(Command command) {
        mathToolTextArea.setText("");
        mathToolGraphicArea.initializeBounds(MathToolGUI.mathToolGraphicAreaX, MathToolGUI.mathToolGraphicAreaY,
                MathToolGUI.mathToolGraphicAreaWidth, MathToolGUI.mathToolGraphicAreaHeight);
        mathToolGraphicArea.clearArea();
    }

    @Execute(type = TypeCommand.def)
    private static void executeDef(Command command) throws EvaluationException {

        // Falls ein Variablenwert definiert wird.
        if (command.getParams().length == 2) {
            String var = (String) command.getParams()[0];
            Expression preciseExpression = ((Expression) command.getParams()[1]).simplifyByInsertingDefinedVars().simplify();
            Variable.setPreciseExpression(var, preciseExpression);
            if (((Expression) command.getParams()[1]).equals(preciseExpression)) {
                doPrintOutput(Translator.translateOutputMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1"),
                        var,
                        Translator.translateOutputMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2"),
                        preciseExpression,
                        Translator.translateOutputMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3"));
            } else {
                doPrintOutput(Translator.translateOutputMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_1"),
                        var,
                        Translator.translateOutputMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_2"),
                        (Expression) command.getParams()[1],
                        " = ",
                        preciseExpression,
                        Translator.translateOutputMessage("MCC_VALUE_ASSIGNED_TO_VARIABLE_3"));
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
            SelfDefinedFunction f = new SelfDefinedFunction(functionName, vars, (Expression) command.getParams()[command.getParams().length - 1], exprsForVars);
            SelfDefinedFunction.createSelfDefinedFunction(f);

            doPrintOutput(Translator.translateOutputMessage("MCC_FUNCTION_WAS_DEFINED"),
                    f, " = ", f.getAbstractExpression());

        }

    }

    @Execute(type = TypeCommand.deffuncs)
    public static void executeDefFuncs(Command command)
            throws EvaluationException {

        String[] arguments;
        Expression abstractExpression;

        // Alle selbstdefinierten Funktionen nacheinander ausgeben.
        if (!SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().isEmpty()) {

            doPrintOutput(Translator.translateOutputMessage("MCC_LIST_OF_DEFINED_FUNCTIONS"));
            for (String functionName : SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet()) {

                arguments = SelfDefinedFunction.getArgumentsForSelfDefinedFunctions().get(functionName);
                abstractExpression = SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().get(functionName);

                Expression[] exprsForVars = new Expression[arguments.length];
                for (int i = 0; i < exprsForVars.length; i++) {
                    exprsForVars[i] = Variable.create(NotationLoader.SELFDEFINEDFUNCTION_VAR + "_" + (i + 1));
                }
                SelfDefinedFunction f = new SelfDefinedFunction(functionName, arguments, abstractExpression, exprsForVars);
                doPrintOutput(f, " = ", f.getAbstractExpression());

            }

        } else {
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_DEFINED_FUNCTIONS"));
        }

    }

    @Execute(type = TypeCommand.defvars)
    public static void executeDefVars(Command command) {
        HashSet<String> vars = Variable.getVariablesWithPredefinedValues();
        if (!vars.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_LIST_OF_VARIABLES"));
            for (String var : vars) {
                doPrintOutput(var, " = ", Variable.create(var).getPreciseExpression());
            }
        } else {
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_DEFINED_VARS"));
        }
    }

    @Execute(type = TypeCommand.eigenvalues)
    private static void executeEigenvalues(Command command) throws EvaluationException {

        Dimension dim = ((MatrixExpression) command.getParams()[0]).getDimension();
        if (dim.height != dim.width) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVALUES_NOT_QUADRATIC"));
        }

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsAlgorithms.getEigenvalues((MatrixExpression) command.getParams()[0]);

        if (eigenvalues.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EIGENVALUES_1"),
                    (MatrixExpression) command.getParams()[0],
                    Translator.translateOutputMessage("MCC_NO_EIGENVALUES_2"));
            return;
        }

        // Textliche Ausgabe
        doPrintOutput(Translator.translateOutputMessage("MCC_EIGENVALUES_OF_MATRIX_1"),
                (MatrixExpression) command.getParams()[0],
                Translator.translateOutputMessage("MCC_EIGENVALUES_OF_MATRIX_2"));

        ArrayList eigenvaluesAsArrayList = new ArrayList();

        for (int i = 0; i < eigenvalues.getBound(); i++) {
            eigenvaluesAsArrayList.add(eigenvalues.get(i));
            if (i < eigenvalues.getBound() - 1) {
                eigenvaluesAsArrayList.add(", ");
            }
        }

        doPrintOutput(eigenvaluesAsArrayList);

    }

    @Execute(type = TypeCommand.eigenvectors)
    private static void executeEigenvectors(Command command) throws EvaluationException {

        Dimension dim = ((MatrixExpression) command.getParams()[0]).getDimension();
        if (dim.height != dim.width) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVECTORS_NOT_QUADRATIC"));
        }

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsAlgorithms.getEigenvalues((MatrixExpression) command.getParams()[0]);

        MatrixExpressionCollection eigenvectors;
        MatrixExpression matrix = (MatrixExpression) command.getParams()[0];

        ArrayList<Object> eigenvectorsAsArrayList;

        for (int i = 0; i < eigenvalues.getBound(); i++) {

            // Sicherheitshalber! Sollte eigentlich nie passieren.
            if (eigenvalues.get(i) == null) {
                continue;
            }
            // Eigenvektoren berechnen.
            eigenvectors = EigenvaluesEigenvectorsAlgorithms.getEigenvectorsForEigenvalue(matrix, eigenvalues.get(i));

            eigenvectorsAsArrayList = new ArrayList<>();
            eigenvectorsAsArrayList.add(Translator.translateOutputMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_1"));
            eigenvectorsAsArrayList.add(eigenvalues.get(i));
            eigenvectorsAsArrayList.add(Translator.translateOutputMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_2"));
            if (eigenvectors.isEmpty()) {
                eigenvectorsAsArrayList.add(Translator.translateOutputMessage("MCC_EIGENVECTORS_NO_EXPLICIT_EIGENVECTORS"));
            } else {
                for (int j = 0; j < eigenvectors.getBound(); j++) {
                    eigenvectorsAsArrayList.add(eigenvectors.get(j));
                    if (j < eigenvectors.getBound() - 1) {
                        eigenvectorsAsArrayList.add(", ");
                    }
                }
            }

            doPrintOutput(eigenvectorsAsArrayList);

        }

        if (eigenvalues.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_EIGENVECTORS_NO_EIGENVECTORS"));
        }

    }

    @Execute(type = TypeCommand.euler)
    private static void executeEuler(Command command) throws EvaluationException {
        BigDecimal e = AnalysisMethods.getDigitsOfE((int) command.getParams()[0]);
        doPrintOutput(Translator.translateOutputMessage("MCC_DIGITS_OF_E", (int) command.getParams()[0], e.toString()));
    }

    @Execute(type = TypeCommand.expand)
    private static void executeExpand(Command command) throws EvaluationException {
        Expression expr = (Expression) command.getParams()[0];
        expr = expr.simplify(simplifyTypesExpand);
        doPrintOutput(expr);
    }

    @Execute(type = TypeCommand.extrema)
    private static void executeExtrema(Command command)
            throws EvaluationException {

        if (command.getParams().length <= 2) {
            executeExtremaAlgebraic(command);
        } else {
            executeExtremaNumeric(command);
        }

    }

    private static void executeExtremaAlgebraic(Command command) throws EvaluationException {

        Expression expr = (Expression) command.getParams()[0];
        String var = "x";

        if (command.getParams().length == 2) {
            var = (String) command.getParams()[1];
        } else {
            for (String v : expr.getContainedIndeterminates()) {
                var = v;
            }
        }

        // Fall: expr ist bzgl. var konstant.
        if (expr.getContainedIndeterminates().isEmpty()) {
            // Keinen Kandidaten für Extrema gefunden.
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EXTREMA_FOUND"));
            return;
        }

        Expression derivative = expr.diff(var);
        Expression secondDerivateive = derivative.diff(var);
        Expression secondDerAtZero;

        ExpressionCollection zeros = SolveGeneralEquationMethods.solveEquation(derivative, ZERO, var);
        /*
         Wenn ganzzahlige Variablen auftauchen, so müssen sicherheitshalber Klammern ausgelöst werden.
         */
        for (int i = 0; i < zeros.getBound(); i++) {
            if (expressionContainsIntegerVariables(zeros.get(i))) {
                zeros.put(i, zeros.get(i).simplify(simplifyTypesExpandShort));
            }
        }

        ExpressionCollection extremaPoints = new ExpressionCollection();
        ExpressionCollection extremaValues = new ExpressionCollection();
        ExpressionCollection valuesOfSecondDerivative = new ExpressionCollection();

        for (Expression zero : zeros) {
            try {
                secondDerAtZero = secondDerivateive.replaceVariable(var, zero).simplify(simplifyTypesExpandShort);
                if (secondDerAtZero.isAlwaysPositive() || secondDerAtZero.isAlwaysNegative()) {
                    extremaPoints.add(zero);
                    extremaValues.add(expr.replaceVariable(var, zero).simplify());
                    valuesOfSecondDerivative.add(secondDerAtZero);
                }
            } catch (EvaluationException e) {
                // Einfach weiter probieren.
            }
        }

        if (extremaPoints.isEmpty()) {
            // Keinen Kandidaten für Extrema gefunden.
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EXTREMA_FOUND"));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_EXTREMA"), (Expression) command.getParams()[0], ":");

        MultiIndexVariable multiVar;
        ArrayList<BigInteger> multiIndex;
        for (int i = 0; i < extremaPoints.getBound(); i++) {
            multiVar = new MultiIndexVariable(Variable.create(var));
            multiIndex = multiVar.getIndices();
            multiIndex.add(BigInteger.valueOf(i + 1));
            if (valuesOfSecondDerivative.get(i).isAlwaysPositive()) {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOCAL_MINIMUM_IN"),
                        multiVar, " = ", extremaPoints.get(i),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        extremaValues.get(i));
            } else {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOCAL_MAXIMUM_IN"),
                        multiVar, " = ", extremaPoints.get(i),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        extremaValues.get(i));
            }
        }

        /*
         Falls Lösungen Parameter K_1, K_2, ... enthalten, dann zusätzlich
         ausgeben: K_1, K_2, ... sind beliebige ganze Zahlen.
         */
        boolean solutionContainsFreeParameter = false;

        for (int i = 0; i < extremaPoints.getBound(); i++) {
            solutionContainsFreeParameter = solutionContainsFreeParameter || extremaPoints.get(i).contains(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_1");
        }

        if (solutionContainsFreeParameter) {

            boolean solutionContainsFreeParameterOfGivenIndex = true;
            int maxIndex = 1;
            while (solutionContainsFreeParameterOfGivenIndex) {
                maxIndex++;
                solutionContainsFreeParameterOfGivenIndex = false;
                for (int i = 0; i < extremaPoints.getBound(); i++) {
                    solutionContainsFreeParameterOfGivenIndex = solutionContainsFreeParameterOfGivenIndex
                            || extremaPoints.get(i).contains(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_" + maxIndex);
                }
            }
            maxIndex--;

            ArrayList freeParametersInfoArray = new ArrayList();

            for (int i = 1; i <= maxIndex; i++) {
                freeParametersInfoArray.add(new MultiIndexVariable(NotationLoader.FREE_INTEGER_PARAMETER_VAR, BigInteger.valueOf(i)));
                if (i < maxIndex - 1) {
                    freeParametersInfoArray.add(", ");
                }
            }
            if (maxIndex == 1) {
                freeParametersInfoArray.add(Translator.translateOutputMessage("MCC_IS_ARBITRARY_INTEGER"));
            } else {
                freeParametersInfoArray.add(Translator.translateOutputMessage("MCC_ARE_ARBITRARY_INTEGERS"));
            }

            doPrintOutput(freeParametersInfoArray);

        }

    }

    /**
     * Hilfsmethode. Gibt zurück, ob der Ausdruck expr eine Variable mit
     * ganzzahligen Werten (also eine Variable der Form K_1, K_2, ...) enthält.
     */
    private static boolean expressionContainsIntegerVariables(Expression expr) {
        HashSet<String> vars = expr.getContainedIndeterminates();
        for (String var : vars) {
            if (var.startsWith(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_") && !var.contains("'")) {
                return true;
            }
        }
        return false;
    }

    private static void executeExtremaNumeric(Command command) throws EvaluationException {

        Expression expr = (Expression) command.getParams()[0];
        String var = "x";

        // expr kann in diesem Fall höchstens eine Veränderliche enthalten.
        for (String v : expr.getContainedIndeterminates()) {
            var = v;
        }

        // Fall: expr ist bzgl. var konstant.
        if (expr.getContainedIndeterminates().isEmpty()) {
            // Keinen Kandidaten für Extrema gefunden.
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EXTREMA_FOUND"));
            return;
        }

        Expression x_0 = (Expression) command.getParams()[1];
        Expression x_1 = (Expression) command.getParams()[2];

        // Validierung der Zeichenbereichsgrenzen
        double xStart = x_0.evaluate();
        double xEnd = x_1.evaluate();

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_EXTREMA", 2, 3));
        }

        /*
         Falls die Anzahl der Unterteilungen nicht angegeben wird, so soll
         das Intervall in 10000 Teile unterteilt werden.
         */
        int n = ComputationBounds.BOUND_NUMERIC_DEFAULT_NUMBER_OF_INTERVALS;

        if (command.getParams().length == 4) {
            n = (int) command.getParams()[3];
        }

        Expression derivative = expr.diff(var);
        Expression secondDerivateive = derivative.diff(var);
        double secondDerAtZero;

        ArrayList<Double> zeros = NumericalMethods.solveEquation(derivative, var, x_0.evaluate(), x_1.evaluate(), n);
        ArrayList<Double> extremaPoints = new ArrayList<>();
        ArrayList<Double> valuesOfSecondDerivative = new ArrayList<>();
        ArrayList<Double> extremaValues = new ArrayList<>();

        for (Double zero : zeros) {
            try {
                Variable.setValue(var, zero);
                secondDerAtZero = secondDerivateive.evaluate();
                if (secondDerAtZero != 0) {
                    extremaPoints.add(zero);
                    valuesOfSecondDerivative.add(secondDerAtZero);
                    Variable.setValue(var, zero);
                    extremaValues.add(expr.evaluate());
                }
            } catch (EvaluationException e) {
                // Einfach weiter probieren.
            }
        }

        if (extremaPoints.isEmpty()) {
            // Keinen Kandidaten für Extrema gefunden.
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EXTREMA_FOUND"));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_EXTREMA"), (Expression) command.getParams()[0], ":");

        MultiIndexVariable multiVar;
        ArrayList<BigInteger> multiIndex;
        for (int i = 0; i < extremaPoints.size(); i++) {

            multiVar = new MultiIndexVariable(Variable.create(var));
            multiIndex = multiVar.getIndices();
            multiIndex.add(BigInteger.valueOf(i + 1));
            if (valuesOfSecondDerivative.get(i) > 0) {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOCAL_MINIMUM_IN"),
                        multiVar, " = ", extremaPoints.get(i).toString(),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        extremaValues.get(i).toString());
            } else {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOCAL_MAXIMUM_IN"),
                        multiVar, " = ", extremaPoints.get(i).toString(),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        extremaValues.get(i).toString());
            }

        }

        // Nullstellen als Array (zum Markieren).
        double[][] extremaAsArray = new double[extremaPoints.size()][2];
        for (int i = 0; i < extremaAsArray.length; i++) {
            extremaAsArray[i][0] = extremaPoints.get(i);
            Variable.setValue(var, extremaAsArray[i][0]);
            extremaAsArray[i][1] = expr.evaluate();
        }

        // Graphen der Funktion zeichnen, inkl. der Extrema (als rot markierte Punkte).
        ArrayList<Expression> exprs = new ArrayList<>();
        exprs.add(expr);
        graphicPanel2D.setVarAbsc(var);
        graphicPanel2D.setSpecialPoints(extremaAsArray);
        graphicPanel2D.drawGraphs2D(x_0, x_1, exprs);

    }

    @Execute(type = TypeCommand.ker)
    private static void executeKer(Command command) throws EvaluationException {

        MatrixExpression matExpr = ((MatrixExpression) command.getParams()[0]).simplify();
        if (!(matExpr instanceof Matrix)) {
            doPrintOutput(Translator.translateOutputMessage("MCC_KER_COULD_NOT_BE_COMPUTED_1"),
                    (MatrixExpression) command.getParams()[0],
                    Translator.translateOutputMessage("MCC_KER_COULD_NOT_BE_COMPUTED_2"));
            return;
        }

        MatrixExpressionCollection basisOfKer = GaussAlgorithm.computeKernelOfMatrix((Matrix) matExpr);

        if (basisOfKer.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_TRIVIAL_KER_1"),
                    (MatrixExpression) command.getParams()[0],
                    Translator.translateOutputMessage("MCC_TRIVIAL_KER_2"),
                    MatrixExpression.getZeroMatrix(((Matrix) matExpr).getRowNumber(), 1),
                    Translator.translateOutputMessage("MCC_TRIVIAL_KER_3"));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_BASIS_OF_KER_1"), matExpr, Translator.translateOutputMessage("MCC_BASIS_OF_KER_2"));

        ArrayList<Object> basisAsArray = new ArrayList<>();
        for (int i = 0; i < basisOfKer.getBound(); i++) {
            // Für graphische Ausgabe
            basisAsArray.add(basisOfKer.get(i));
            if (i < basisOfKer.getBound() - 1) {
                basisAsArray.add(", ");
            }
        }

        // Graphische Ausgabe
        doPrintOutput(basisAsArray);

    }

    @Execute(type = TypeCommand.latex)
    private static void executeLatex(Command command) {

        String latexCode = Translator.translateOutputMessage("MCC_LATEX_CODE");
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

        doPrintOutput(latexCode);

    }

    @Execute(type = TypeCommand.pi)
    private static void executePi(Command command) throws EvaluationException {
        BigDecimal pi = AnalysisMethods.getDigitsOfPi((int) command.getParams()[0]);
        doPrintOutput(Translator.translateOutputMessage("MCC_DIGITS_OF_PI", (int) command.getParams()[0], pi.toString()));
    }

    @Execute(type = TypeCommand.plot2d)
    private static void executePlot2D(Command command) throws EvaluationException {

        if (graphicPanel2D == null || mathToolGraphicArea == null) {
            return;
        }

        ArrayList<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 3; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);

            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        expr, Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        Expression x_0 = ((Expression) command.getParams()[command.getParams().length - 2]).simplify(simplifyTypesPlot);
        Expression x_1 = ((Expression) command.getParams()[command.getParams().length - 1]).simplify(simplifyTypesPlot);

        String var = (String) command.getParams()[command.getParams().length - 3];

        // Validierung der Zeichenbereichsgrenzen.
        double xStart, xEnd;

        try {
            xStart = x_0.evaluate();
            xEnd = x_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT2D", exprs.size() + 1, exprs.size() + 2));
        }

        // Graphen zeichnen.
        graphicPanel2D.setVarAbsc(var);
        graphicPanel2D.setSpecialPoints(false);
        graphicPanel2D.drawGraphs2D(x_0, x_1, exprs);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotimplicit)
    private static void executePlotImplicit2D(Command command) throws EvaluationException {

        if (graphicPanelImplicit2D == null || mathToolGraphicArea == null) {
            return;
        }

        Expression expr = ((Expression[]) command.getParams()[0])[0].sub(((Expression[]) command.getParams()[0])[1]).simplify(simplifyTypesPlot);

        // Falls eines der Graphen nicht gezeichnet werden kann.
        if (expr.containsOperator()) {
            Expression difference = ((Expression[]) command.getParams()[0])[0].sub(((Expression[]) command.getParams()[0])[1]);
            doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                    difference, Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
            // Schließlich noch Fehler werfen.
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        String varAbsc = (String) command.getParams()[1];
        String varOrd = (String) command.getParams()[2];

        Expression x_0 = ((Expression) command.getParams()[3]).simplify(simplifyTypesPlot);
        Expression x_1 = ((Expression) command.getParams()[4]).simplify(simplifyTypesPlot);
        Expression y_0 = ((Expression) command.getParams()[5]).simplify(simplifyTypesPlot);
        Expression y_1 = ((Expression) command.getParams()[6]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen
        double xStart, xEnd, yStart, yEnd;

        try {
            xStart = x_0.evaluate();
            xEnd = x_1.evaluate();
            yStart = y_0.evaluate();
            yEnd = y_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D", 2, 3));
        }
        if (yStart >= yEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D", 4, 5));
        }

        ArrayList<double[]> implicitGraph = NumericalMethods.solveImplicitEquation2D(expr, varAbsc, varOrd,
                x_0.evaluate(), x_1.evaluate(), y_0.evaluate(), y_1.evaluate());

        // Graphen zeichnen.
        graphicPanelImplicit2D.setExpressions(((Expression[]) command.getParams()[0])[0], ((Expression[]) command.getParams()[0])[1]);
        graphicPanelImplicit2D.setVars(varAbsc, varOrd);
        graphicPanelImplicit2D.drawGraph2D(implicitGraph, x_0, x_1, y_0, y_1);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plot3d)
    private static void executePlot3D(Command command) throws EvaluationException {

        if (graphicPanel3D == null || mathToolGraphicArea == null) {
            return;
        }

        ArrayList<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 6; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        exprs.get(i), Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        Expression x_0 = ((Expression) command.getParams()[command.getParams().length - 4]).simplify(simplifyTypesPlot);
        Expression x_1 = ((Expression) command.getParams()[command.getParams().length - 3]).simplify(simplifyTypesPlot);
        Expression y_0 = ((Expression) command.getParams()[command.getParams().length - 2]).simplify(simplifyTypesPlot);
        Expression y_1 = ((Expression) command.getParams()[command.getParams().length - 1]).simplify(simplifyTypesPlot);

        String varAbsc = (String) command.getParams()[command.getParams().length - 6];
        String varOrd = (String) command.getParams()[command.getParams().length - 5];

        // Validierung der Zeichenbereichsgrenzen
        double xStart, xEnd, yStart, yEnd;

        try {
            xStart = x_0.evaluate();
            xEnd = x_1.evaluate();
            yStart = y_0.evaluate();
            yEnd = y_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D", exprs.size() + 1, exprs.size() + 2));
        }
        if (yStart >= yEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D", exprs.size() + 3, exprs.size() + 4));
        }

        // Graphen zeichnen.
        graphicPanel3D.setParameters(varAbsc, varOrd, 150, 200, 30, 30);
        graphicPanel3D.drawGraphs3D(x_0, x_1, y_0, y_1, exprs);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotcurve2d)
    private static void executePlotCurve2D(Command command) throws EvaluationException {

        if (graphicPanelCurves2D == null || mathToolGraphicArea == null) {
            return;
        }

        MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
        try {
            matExpr = matExpr.simplify(simplifyTypesPlot);
            Dimension dim = matExpr.getDimension();
            if (!(matExpr instanceof Matrix) || dim.width != 1 || dim.height != 2) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR"));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR"));
        }

        Expression[] components = new Expression[2];
        for (int i = 0; i < 2; i++) {
            components[i] = ((Matrix) matExpr).getEntry(i, 0);
        }

        Expression exprSimplified;
        for (int i = 0; i < 2; i++) {

            exprSimplified = components[i].simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        components[i], Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));

            }
            components[i] = exprSimplified;

        }

        String var = (String) command.getParams()[1];
        Expression t_0 = ((Expression) command.getParams()[2]).simplify(simplifyTypesPlot);
        Expression t_1 = ((Expression) command.getParams()[3]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen.
        try {
            t_0.evaluate();
            t_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        // Kurve zeichnen.
        graphicPanelCurves2D.setVar(var);
        graphicPanelCurves2D.drawCurve2D(t_0, t_1, components);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotcurve3d)
    private static void executePlotCurve3D(Command command) throws EvaluationException {

        if (graphicPanelCurves3D == null || mathToolGraphicArea == null) {
            return;
        }

        MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
        try {
            matExpr = matExpr.simplify(simplifyTypesPlot);
            Dimension dim = matExpr.getDimension();
            if (!(matExpr instanceof Matrix) || dim.width != 1 || dim.height != 3) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_3_DIM_VECTOR"));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_3_DIM_VECTOR"));
        }

        Expression[] components = new Expression[3];
        for (int i = 0; i < 3; i++) {
            components[i] = ((Matrix) matExpr).getEntry(i, 0);
        }

        Expression exprSimplified;
        for (int i = 0; i < 3; i++) {

            exprSimplified = components[i].simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        components[i], Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));

            }
            components[i] = exprSimplified;

        }

        String var = (String) command.getParams()[1];
        Expression t_0 = ((Expression) command.getParams()[2]).simplify(simplifyTypesPlot);
        Expression t_1 = ((Expression) command.getParams()[3]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen.
        try {
            t_0.evaluate();
            t_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        // Kurve zeichnen.
        graphicPanelCurves3D.setParameters(var, 150, 200, 30, 30);
        graphicPanelCurves3D.drawCurve3D(t_0, t_1, components);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotpolar)
    private static void executePlotPolar(Command command) throws EvaluationException {

        if (graphicPanelPolar2D == null || mathToolGraphicArea == null) {
            return;
        }

        HashSet<String> vars = new HashSet<>();
        ArrayList<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 2; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        expr, Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
            } else {
                exprs.add(exprSimplified);
                exprSimplified.addContainedIndeterminates(vars);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        Expression phi_0 = ((Expression) command.getParams()[command.getParams().length - 2]).simplify(simplifyTypesPlot);
        Expression phi_1 = ((Expression) command.getParams()[command.getParams().length - 1]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen
        double phiStart, phiEnd;

        try {
            phiStart = phi_0.evaluate();
            phiEnd = phi_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        if (phiStart >= phiEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTPOLAR", exprs.size() + 1, exprs.size() + 2));
        }

        // Falls der Ausdruck expr konstant ist, soll die Achse die Bezeichnung "x" tragen.
        if (vars.isEmpty()) {
            vars.add(NotationLoader.FREE_REAL_PARAMETER_VAR);
        }

        Iterator iter = vars.iterator();
        String var = (String) iter.next();

        // Graphen zeichnen.
        graphicPanelPolar2D.setVar(var);
        graphicPanelPolar2D.drawGraphPolar(phi_0, phi_1, exprs);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotcylindrical)
    private static void executePlotCylindrical(Command command) throws EvaluationException {

        if (graphicPanelCylindrical == null || mathToolGraphicArea == null) {
            return;
        }

        ArrayList<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 6; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        expr, Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        Expression r_0 = ((Expression) command.getParams()[command.getParams().length - 4]).simplify(simplifyTypesPlot);
        Expression r_1 = ((Expression) command.getParams()[command.getParams().length - 3]).simplify(simplifyTypesPlot);
        Expression phi_0 = ((Expression) command.getParams()[command.getParams().length - 2]).simplify(simplifyTypesPlot);
        Expression phi_1 = ((Expression) command.getParams()[command.getParams().length - 1]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen.
        double minR, maxR, minPhi, maxPhi;

        try {
            minR = r_0.evaluate();
            maxR = r_1.evaluate();
            minPhi = phi_0.evaluate();
            maxPhi = phi_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        if (minR < 0) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_MIN_RADIUS_MUST_BE_NONNEGATIVE_IN_PLOTCYLINDRICAL", exprs.size() + 3));
        }
        if (minR >= maxR) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTCYLINDRICAL", exprs.size() + 3, exprs.size() + 4));
        }
        if (minPhi >= maxPhi) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTCYLINDRICAL", exprs.size() + 5, exprs.size() + 6));
        }
        if (maxPhi - minPhi > 20 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTCYLINDRICAL", exprs.size() + 5, exprs.size() + 6));
        }

        // Graphen zeichnen.
        graphicPanelCylindrical.setParameters((String) command.getParams()[command.getParams().length - 6], (String) command.getParams()[command.getParams().length - 5], 150, 200, 30, 30);
        graphicPanelCylindrical.drawCylindricalGraphs3D(r_0, r_1, phi_0, phi_1, exprs);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotspherical)
    private static void executePlotSpherical(Command command) throws EvaluationException {

        if (graphicPanelSpherical == null || mathToolGraphicArea == null) {
            return;
        }

        ArrayList<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 6; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        expr, Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        Expression phi_0 = ((Expression) command.getParams()[command.getParams().length - 4]).simplify(simplifyTypesPlot);
        Expression phi_1 = ((Expression) command.getParams()[command.getParams().length - 3]).simplify(simplifyTypesPlot);
        Expression tau_0 = ((Expression) command.getParams()[command.getParams().length - 2]).simplify(simplifyTypesPlot);
        Expression tau_1 = ((Expression) command.getParams()[command.getParams().length - 1]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen.
        double minPhi, maxPhi, minTau, maxTau;

        try {
            minPhi = phi_0.evaluate();
            maxPhi = phi_1.evaluate();
            minTau = tau_0.evaluate();
            maxTau = tau_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        if (minPhi >= maxPhi) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTSPHERICAL", exprs.size() + 3, exprs.size() + 4));
        }
        if (minTau >= maxTau) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTSPHERICAL", exprs.size() + 5, exprs.size() + 6));
        }
        if (maxPhi - minPhi > 20 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTSPHERICAL", exprs.size() + 3, exprs.size() + 4));
        }
        if (maxTau - minTau > 20 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTSPHERICAL", exprs.size() + 5, exprs.size() + 6));
        }

        // Graphen zeichnen.
        graphicPanelSpherical.setParameters((String) command.getParams()[command.getParams().length - 6], (String) command.getParams()[command.getParams().length - 5], 150, 200, 30, 30);
        graphicPanelSpherical.drawCylindricalGraphs3D(phi_0, phi_1, tau_0, tau_1, exprs);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotvectorfield2d)
    private static void executePlotVectorField2D(Command command) throws EvaluationException {

        if (graphicPanelVectorField2D == null || mathToolGraphicArea == null) {
            return;
        }

        MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
        try {
            matExpr = matExpr.simplify(simplifyTypesPlot);
            Dimension dim = matExpr.getDimension();
            if (!(matExpr instanceof Matrix) || dim.width != 1 || dim.height != 2) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR"));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR"));
        }

        Expression[] components = new Expression[2];
        for (int i = 0; i < 2; i++) {
            components[i] = ((Matrix) matExpr).getEntry(i, 0);
        }

        Expression exprSimplified;
        for (int i = 0; i < 2; i++) {

            exprSimplified = components[i].simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
                        components[i], Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED"));

            }
            components[i] = exprSimplified;

        }

        String varAbsc = (String) command.getParams()[1];
        String varOrd = (String) command.getParams()[2];
        Expression x_0 = ((Expression) command.getParams()[3]).simplify(simplifyTypesPlot);
        Expression x_1 = ((Expression) command.getParams()[4]).simplify(simplifyTypesPlot);
        Expression y_0 = ((Expression) command.getParams()[5]).simplify(simplifyTypesPlot);
        Expression y_1 = ((Expression) command.getParams()[6]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen.
        try {
            x_0.evaluate();
            x_1.evaluate();
            y_0.evaluate();
            y_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED"));
        }

        // Vektorfeld zeichnen.
        graphicPanelVectorField2D.setVars(varAbsc, varOrd);
        graphicPanelVectorField2D.drawVectorField2D(x_0, x_1, y_0, y_1, components);
        // Alte Legende schließen
        LegendGUI.close();

    }

//    @Execute(type = TypeCommand.plotvectorfield3d)
//    private static void executePlotVectorField3D(Command command) throws EvaluationException {
//
//        if (graphicPanelCurves3D == null || mathToolGraphicArea == null) {
//            return;
//        }
//
//        MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
//        try {
//            matExpr = matExpr.simplify(simplifyTypesPlot);
//            Dimension dim = matExpr.getDimension();
//            if (!(matExpr instanceof Matrix) || dim.width != 1 || dim.height != 3) {
//                throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD3D_1_PARAMETER_MUST_BE_2_DIM_VECTOR"));
//            }
//        } catch (EvaluationException e) {
//            throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD3D_1_PARAMETER_MUST_BE_2_DIM_VECTOR"));
//        }
//
//        Expression[] components = new Expression[3];
//        for (int i = 0; i < 3; i++) {
//            components[i] = ((Matrix) matExpr).getEntry(i, 0);
//        }
//
//        Expression exprSimplified;
//        for (int i = 0; i < 3; i++) {
//
//            exprSimplified = components[i].simplify(simplifyTypesPlot);
//            // Falls eines der Graphen nicht gezeichnet werden kann.
//            if (exprSimplified.containsOperator()) {
//                doPrintOutput(Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_1"),
//                        components[i], Translator.translateOutputMessage("EB_Operator_OPERATOR_CANNOT_BE_EVALUATED_2"));
//                // Schließlich noch Fehler werfen.
//                throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED"));
//
//            }
//            components[i] = exprSimplified;
//
//        }
//
//        String varAbsc = (String) command.getParams()[1];
//        String varOrd = (String) command.getParams()[2];
//        String varAppl = (String) command.getParams()[3];
//        Expression x_0 = ((Expression) command.getParams()[4]).simplify(simplifyTypesPlot);
//        Expression x_1 = ((Expression) command.getParams()[5]).simplify(simplifyTypesPlot);
//        Expression y_0 = ((Expression) command.getParams()[6]).simplify(simplifyTypesPlot);
//        Expression y_1 = ((Expression) command.getParams()[7]).simplify(simplifyTypesPlot);
//        Expression z_0 = ((Expression) command.getParams()[8]).simplify(simplifyTypesPlot);
//        Expression z_1 = ((Expression) command.getParams()[9]).simplify(simplifyTypesPlot);
//
//        // Validierung der Zeichenbereichsgrenzen.
//        try {
//            x_0.evaluate();
//            x_1.evaluate();
//            y_0.evaluate();
//            y_1.evaluate();
//            z_0.evaluate();
//            z_1.evaluate();
//        } catch (EvaluationException e) {
//            throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED"));
//        }
//
//        // Vektorfeld zeichnen.
//        graphicPanelVectorField3D.setParameters(varAbsc, varOrd, varAppl, 150, 200, 30, 30);
////        graphicPanelVectorField3D.drawVectorField3D(x_0, x_1, y_0, y_1, z_0, z_1, components);
//        // Alte Legende schließen
//        LegendGUI.close();
//
//    }

    @Execute(type = TypeCommand.regressionline)
    private static void executeRegressionLine(Command command)
            throws EvaluationException {

        if (graphicPanel2D == null || mathToolGraphicArea == null) {
            return;
        }

        MatrixExpression[] points = new MatrixExpression[command.getParams().length];
        Dimension dim;

        for (int i = 0; i < points.length; i++) {
            try {
                points[i] = ((MatrixExpression) command.getParams()[i]).simplify();
                dim = points[i].getDimension();
            } catch (EvaluationException e) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_REGRESSIONLINE_CANNOT_BE_COMPUTED"));
            }
            if (!points[i].isMatrix() || dim.width != 1 || dim.height != 2) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_REGRESSIONLINE_PARAMETERS_ARE_NOT_POINTS"));
            }
        }

        // Koeffizienten für die Regressionsgerade berechnen.
        Matrix[] pts = new Matrix[points.length];
        for (int i = 0; i < points.length; i++) {
            pts[i] = (Matrix) points[i];
        }

        try {

            ExpressionCollection regressionLineCoefficients = StatisticMethods.getRegressionLineCoefficients(pts);
            Expression regressionLine = SimplifyPolynomialMethods.getPolynomialFromCoefficients(regressionLineCoefficients, "X").simplify();

            doPrintOutput(Translator.translateOutputMessage("MCC_REGRESSIONLINE_MESSAGE"),
                    "Y = ", regressionLine);

            /* 
             Graphen der Regressionsgeraden zeichnen, inkl. der Stichproben (als rot markierte Punkte),
             falls keines der Stichproben Parameter enthält.
             */
            for (Matrix point : pts) {
                if (!point.isConstant()) {
                    throw new EvaluationException(Translator.translateOutputMessage("MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE"));
                }
            }

            Expression x_0 = StatisticMethods.getMinimum(pts, 0);
            Expression x_1 = StatisticMethods.getMaximum(pts, 0);
            Expression y_0 = StatisticMethods.getMinimum(pts, 1);
            Expression y_1 = StatisticMethods.getMaximum(pts, 1);

            // Validierung der Zeichenbereichsgrenzen.
            double varAbscStart = x_0.evaluate();
            double varAbscEnd = x_1.evaluate();
            double varOrdStart = y_0.evaluate();
            double varOrdEnd = y_1.evaluate();

            if (varAbscStart >= varAbscEnd) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE"));
            }
            if (varOrdStart == varOrdEnd) {
                y_0 = y_0.sub(1);
                y_1 = y_1.add(1);
            }

            ArrayList<Expression> exprs = new ArrayList<>();
            exprs.add(regressionLine);
            graphicPanel2D.setVars("X", "Y");
            graphicPanel2D.setSpecialPoints(pts);
            graphicPanel2D.drawGraphs2D(x_0, x_1, y_0, y_1, exprs);

        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE"));
        }

    }

    @Execute(type = TypeCommand.solve)
    private static void executeSolve(Command command)
            throws EvaluationException {

        if (command.getParams().length <= 2) {
            executeSolveAlgebraic(command);
        } else {
            executeSolveNumeric(command);
        }

    }

    private static void executeSolveAlgebraic(Command command)
            throws EvaluationException {

        HashSet<String> vars = new HashSet<>();
        Expression f = ((Expression[]) command.getParams()[0])[0];
        Expression g = ((Expression[]) command.getParams()[0])[1];

        f.addContainedIndeterminates(vars);
        g.addContainedIndeterminates(vars);

        // Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
        String var;
        if (command.getParams().length == 2) {
            var = (String) command.getParams()[1];
        } else {
            var = "x";
            if (!vars.isEmpty()) {
                Iterator iter = vars.iterator();
                var = (String) iter.next();
            }
        }

        ExpressionCollection zeros = SolveGeneralEquationMethods.solveEquation(f, g, var);

        // Falls die Gleichung keine Lösungen besitzt, User informieren.
        if (zeros == SolveGeneralEquationMethods.NO_SOLUTIONS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_EQUATIONS_HAS_NO_SOLUTIONS"));
            return;
        }

        // Falls keine Lösungen ermittelt werden konnten, User informieren.
        if (zeros.isEmpty() && zeros != SolveGeneralEquationMethods.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND"));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_SOLUTIONS_OF_EQUATION"), ((Expression[]) command.getParams()[0])[0],
                " = ", ((Expression[]) command.getParams()[0])[1], ":");
        if (zeros == SolveGeneralEquationMethods.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_ALL_REALS"));
        } else {
            MultiIndexVariable multiVar;
            ArrayList<BigInteger> multiIndex;
            for (int i = 0; i < zeros.getBound(); i++) {
                multiVar = new MultiIndexVariable(Variable.create(var));
                multiIndex = multiVar.getIndices();
                multiIndex.add(BigInteger.valueOf(i + 1));
                doPrintOutput(multiVar, " = ", zeros.get(i));
            }
        }

        /*
         Falls Lösungen Parameter K_1, K_2, ... enthalten, dann zusätzlich
         ausgeben: K_1, K_2, ... sind beliebige ganze Zahlen.
         */
        boolean solutionContainsFreeParameter = false;
        String infoAboutFreeParameters;

        for (int i = 0; i < zeros.getBound(); i++) {
            solutionContainsFreeParameter = solutionContainsFreeParameter || zeros.get(i).contains(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_1");
        }

        if (solutionContainsFreeParameter) {
            boolean solutionContainsFreeParameterOfGivenIndex = true;
            int maxIndex = 1;
            while (solutionContainsFreeParameterOfGivenIndex) {
                maxIndex++;
                solutionContainsFreeParameterOfGivenIndex = false;
                for (int i = 0; i < zeros.getBound(); i++) {
                    solutionContainsFreeParameterOfGivenIndex = solutionContainsFreeParameterOfGivenIndex
                            || zeros.get(i).contains(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_" + maxIndex);
                }
            }
            maxIndex--;

            ArrayList<MultiIndexVariable> freeParameterVars = new ArrayList<>();
            for (int i = 1; i <= maxIndex; i++) {
                freeParameterVars.add(new MultiIndexVariable(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_" + i));
            }
            if (maxIndex == 1) {
                infoAboutFreeParameters = Translator.translateOutputMessage("MCC_IS_ARBITRARY_INTEGER") + " \n \n";
            } else {
                infoAboutFreeParameters = Translator.translateOutputMessage("MCC_ARE_ARBITRARY_INTEGERS") + " \n \n";
            }

            ArrayList infoAboutFreeParametersForGraphicArea = new ArrayList();
            for (int i = 0; i < freeParameterVars.size(); i++) {
                infoAboutFreeParametersForGraphicArea.add(freeParameterVars.get(i));
                if (i < freeParameterVars.size() - 1) {
                    infoAboutFreeParametersForGraphicArea.add(", ");
                }
            }
            infoAboutFreeParametersForGraphicArea.add(infoAboutFreeParameters);
            doPrintOutput(infoAboutFreeParametersForGraphicArea);

        }

    }

    private static void executeSolveNumeric(Command command)
            throws EvaluationException {

        HashSet<String> vars = new HashSet<>();
        Expression f = ((Expression[]) command.getParams()[0])[0];
        Expression g = ((Expression[]) command.getParams()[0])[1];

        Expression equation = f.sub(g).simplify();
        equation.addContainedIndeterminates(vars);
        // Variablenname in der Gleichung wird ermittelt (die Gleichung enthält höchstens Veränderliche)
        String var = "x";
        for (String v : vars) {
            var = v;
        }

        Expression x_0 = (Expression) command.getParams()[1];
        Expression x_1 = (Expression) command.getParams()[2];

        // Validierung der Zeichenbereichsgrenzen.
        double xStart = x_0.evaluate();
        double xEnd = x_1.evaluate();

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVE", 2, 3));
        }

        /*
         Falls die Anzahl der Unterteilungen nicht angegeben wird, so soll
         das Intervall in 10000 Teile unterteilt werden.
         */
        int n = ComputationBounds.BOUND_NUMERIC_DEFAULT_NUMBER_OF_INTERVALS;

        if (command.getParams().length == 4) {
            n = (int) command.getParams()[3];
        }

        if (equation.isConstant()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_SOLUTIONS_OF_EQUATION"), ((Expression[]) command.getParams()[0])[0],
                    " = ", ((Expression[]) command.getParams()[0])[1], ":");
            if (equation.equals(Expression.ZERO)) {
                doPrintOutput(Translator.translateOutputMessage("MCC_ALL_REALS"));
            } else {
                doPrintOutput(Translator.translateOutputMessage("MCC_EQUATIONS_HAS_NO_SOLUTIONS"));
            }

            // Graphen der linken und der rechten Seite zeichnen.
            ArrayList<Expression> exprs = new ArrayList<>();
            exprs.add(f);
            exprs.add(g);
            graphicPanel2D.setVarAbsc(var);
            graphicPanel2D.drawGraphs2D(x_0, x_1, exprs);
            return;

        }

        ArrayList<Double> zeros = NumericalMethods.solveEquation(equation, var, x_0.evaluate(), x_1.evaluate(), n);

        doPrintOutput(Translator.translateOutputMessage("MCC_SOLUTIONS_OF_EQUATION"), ((Expression[]) command.getParams()[0])[0],
                " = ", ((Expression[]) command.getParams()[0])[1], ":");

        MultiIndexVariable multiVar;
        ArrayList<BigInteger> multiIndex;
        for (int i = 0; i < zeros.size(); i++) {
            // Grafische Ausgabe
            multiVar = new MultiIndexVariable(Variable.create(var));
            multiIndex = multiVar.getIndices();
            multiIndex.add(BigInteger.valueOf(i + 1));
            doPrintOutput(multiVar, " = " + String.valueOf(zeros.get(i)));
        }

        if (zeros.isEmpty()) {
            // Grafische Ausgabe
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_SOLUTIONS_OF_EQUATION_FOUND"));
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
        ArrayList<Expression> exprs = new ArrayList<>();
        exprs.add(f);
        exprs.add(g);
        graphicPanel2D.setVarAbsc(var);
        graphicPanel2D.setSpecialPoints(zerosAsArray);
        graphicPanel2D.drawGraphs2D(x_0, x_1, exprs);

    }

    @Execute(type = TypeCommand.solvediffeq)
    private static void executeSolveDiffEq(Command command)
            throws EvaluationException {

        if (command.getParams().length <= 3) {
            executeSolveDiffEquationAlgebraic(command);
        } else {
            executeSolveDiffEquationNumeric(command);
        }

    }

    private static void executeSolveDiffEquationAlgebraic(Command command)
            throws EvaluationException {

        Expression exprLeft = ((Expression[]) command.getParams()[0])[0];
        Expression exprRight = ((Expression[]) command.getParams()[0])[1];
        String varAbsc = (String) command.getParams()[1];
        String varOrd = (String) command.getParams()[2];

        ExpressionCollection solutions;
        try {
            Variable.setDependingOnVariable(varOrd, varAbsc);
            solutions = SolveGeneralDifferentialEquationMethods.solveDifferentialEquation(exprLeft, exprRight, varAbsc, varOrd);
        } finally {
            Variable.setDependingOnVariable(varOrd, null);
        }

        // Falls keine Lösungen ermittelt werden konnten, User informieren.
        if (solutions.isEmpty()) {
            if (solutions == SolveGeneralDifferentialEquationMethods.ALL_FUNCTIONS) {
                doPrintOutput(Translator.translateOutputMessage("MCC_ALL_FUNCTIONS"));
                return;
            } else if (solutions == SolveGeneralDifferentialEquationMethods.NO_SOLUTIONS) {
                doPrintOutput(Translator.translateOutputMessage("MCC_NO_SOLUTIONS"));
                return;
            }
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EXACT_SOLUTIONS_OF_DIFFERENTIAL_EQUATION_FOUND"));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_ALGEBRAIC_SOLUTION_OF_DIFFEQ"), exprLeft, " = ", exprRight, ":");

        // Zwischen impliziten und expliziten Lösungen unterscheiden.
        ExpressionCollection solutionsImplicit = new ExpressionCollection();
        ExpressionCollection solutionsExplicit = new ExpressionCollection();
        for (Expression solution : solutions) {
            if (solution.contains(varOrd)) {
                solutionsImplicit.add(solution);
            } else {
                solutionsExplicit.add(solution);
            }
        }

        if (!solutionsImplicit.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_IMPLICIT_OF_DIFFEQ"));
            for (int i = 0; i < solutionsImplicit.getBound(); i++) {
                doPrintOutput(solutionsImplicit.get(i), " = ", ZERO);
            }
        }
        if (!solutionsExplicit.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_EXPLICIT_OF_DIFFEQ"));
            MultiIndexVariable multiVar;
            ArrayList<BigInteger> multiIndex;
            for (int i = 0; i < solutionsExplicit.getBound(); i++) {
                multiVar = new MultiIndexVariable(Variable.create(varOrd));
                multiIndex = multiVar.getIndices();
                multiIndex.add(BigInteger.valueOf(i + 1));
                doPrintOutput(multiVar, " = ", solutionsExplicit.get(i));
            }
        }

        ArrayList<Variable> integrationConstants = SolveGeneralDifferentialEquationMethods.getListOfFreeIntegrationConstants(solutions);
        ArrayList integrationConstantsInfoMessage = new ArrayList();

        for (int i = 0; i < integrationConstants.size(); i++) {
            integrationConstantsInfoMessage.add(integrationConstants.get(i));
            if (i < integrationConstants.size() - 1) {
                integrationConstantsInfoMessage.add(", ");
            }
        }

        if (integrationConstants.size() == 1) {
            integrationConstantsInfoMessage.add(Translator.translateOutputMessage("MCC_DIFFEQ_IS_FREE_CONSTANT"));
        } else if (integrationConstants.size() > 1) {
            integrationConstantsInfoMessage.add(Translator.translateOutputMessage("MCC_DIFFEQ_ARE_FREE_CONSTANTS"));
        }

        if (!integrationConstants.isEmpty()) {
            doPrintOutput(integrationConstantsInfoMessage);
        }

    }

    private static void executeSolveDiffEquationNumeric(Command command)
            throws EvaluationException {

        int ord = (int) command.getParams()[2];
        HashSet<String> vars = new HashSet<>();
        Expression expr = (Expression) command.getParams()[0];
        expr.addContainedIndeterminates(vars);

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        String varWithoutPrimes;
        for (String var : vars) {
            varWithoutPrimes = var;
            if (!varWithoutPrimes.replaceAll("'", "").equals(command.getParams()[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DIFFEQ", ord, ord - 1));
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
                Iterator<String> iter = varsWithoutPrimes.iterator();
                varOrd = iter.next();
            }
        } else {
            Iterator<String> iter = varsWithoutPrimes.iterator();
            varOrd = iter.next();
            if (varOrd.equals(varAbsc)) {
                varOrd = iter.next();
            }
        }

        try {
            Variable.setDependingOnVariable(varOrd, varAbsc);
            expr = expr.simplify();

            if (doesExpressionContainDerivativesOfTooHighOrder(expr, varOrd, ord - 1)) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ", ord, ord - 1));
            }

            double[][] solutionOfDifferentialEquation = NumericalMethods.solveDifferentialEquationByRungeKutta(expr, varAbsc, varOrd, ord, x_0.evaluate(), x_1.evaluate(), startValues, 1000);

            // Formulierung und Ausgabe des AWP.
            String formulationOfAWP = Translator.translateOutputMessage("MCC_SOLUTION_OF_DIFFEQ") + varOrd;
            ArrayList formulationOfAWPForGraphicArea = new ArrayList();

            for (int i = 0; i < ord; i++) {
                formulationOfAWP = formulationOfAWP + "'";
            }
            formulationOfAWP += "(" + varAbsc + ") = ";

            formulationOfAWPForGraphicArea.add(formulationOfAWP);
            formulationOfAWPForGraphicArea.add(expr);

            String varOrdWithPrimes;
            for (int i = 0; i < ord; i++) {
                formulationOfAWPForGraphicArea.add(", ");
                varOrdWithPrimes = varOrd;
                for (int j = 0; j < i; j++) {
                    varOrdWithPrimes = varOrdWithPrimes + "'";
                }

                formulationOfAWPForGraphicArea.add(varOrdWithPrimes);
                formulationOfAWPForGraphicArea.add(TypeBracket.BRACKET_SURROUNDING_EXPRESSION);
                formulationOfAWPForGraphicArea.add(x_0);
                formulationOfAWPForGraphicArea.add(" = ");
                formulationOfAWPForGraphicArea.add(y_0[i]);
            }

            formulationOfAWPForGraphicArea.add(", ");
            formulationOfAWPForGraphicArea.add(x_0);
            formulationOfAWPForGraphicArea.add(" \u2264 ");
            formulationOfAWPForGraphicArea.add(varAbsc);
            formulationOfAWPForGraphicArea.add(" \u2264 ");
            formulationOfAWPForGraphicArea.add(x_1);
            formulationOfAWPForGraphicArea.add(":");

            doPrintOutput(formulationOfAWPForGraphicArea);

            // Lösungen ausgeben.
            for (double[] solution : solutionOfDifferentialEquation) {
                doPrintOutput(varAbsc + " = " + solution[0] + "; " + varOrd + " = " + solution[1]);
            }
            if (solutionOfDifferentialEquation.length < 1001) {
                // Falls die Lösung innerhalb des Berechnungsbereichs unendlich/undefiniert ist.
                doPrintOutput(Translator.translateOutputMessage("MCC_SOLUTION_OF_DIFFEQ_NOT_DEFINED_IN_POINT", x_0.evaluate() + (solutionOfDifferentialEquation.length) * (x_1.evaluate() - x_0.evaluate()) / 1000)
                        + ".");
            }

            // Lösungsgraphen zeichnen.
            graphicPanel2D.setVars(varAbsc, varOrd);
            graphicPanel2D.drawGraphs2D(solutionOfDifferentialEquation);

        } finally {
            Variable.setDependingOnVariable(varOrd, null);
        }

    }

    @Execute(type = TypeCommand.solvesystem)
    private static void executeSolveSystem(Command command)
            throws EvaluationException {

        Object[] params = command.getParams();

        // Die Anzahl der Parameter, welche Instanzen von Expression sind, ist gerade und beträgt mindestens 2.
        int numberOfEquations = 0;
        for (Object param : params) {
            if (param instanceof Expression[]) {
                numberOfEquations++;
            } else {
                break;
            }
        }

        // Die Anzahl der Parameter, welche Instanzen von String sind, beträgt mindestens 1.
        ArrayList<String> solutionVars = new ArrayList<>();
        for (int i = numberOfEquations; i < params.length; i++) {
            solutionVars.add((String) params[i]);
        }

        // Lineares Gleichungssystem bilden.
        Expression[] equations = new Expression[numberOfEquations];
        for (int i = 0; i < numberOfEquations; i++) {
            equations[i] = ((Expression[]) params[i])[0].sub(((Expression[]) params[i])[1]).simplify(simplifyTypesSolveSystem);
        }

        // Prüfung, ob alle Gleichungen linear in den angegebenen Variablen sind.
        BigInteger degInVar;
        for (int i = 0; i < numberOfEquations; i++) {
            for (String solutionVar : solutionVars) {
                degInVar = SimplifyPolynomialMethods.getDegreeOfPolynomial(equations[i], solutionVar);
                if (degInVar.compareTo(BigInteger.ONE) > 0) {
                    throw new EvaluationException(Translator.translateOutputMessage("MCC_GENERAL_EQUATION_NOT_LINEAR_IN_SOLVESYSTEM", i + 1, solutionVar));
                }
            }
        }

        Expression[][] matrixEntries = new Expression[numberOfEquations][solutionVars.size()];
        Expression[] vectorEntries = new Expression[numberOfEquations];

        for (int i = 0; i < numberOfEquations; i++) {
            for (int j = 0; j < solutionVars.size(); j++) {
                matrixEntries[i][j] = equations[i].diff(solutionVars.get(j)).simplify();
            }
        }

        // Alle Variablen durch 0 ersetzen.
        for (int i = 0; i < numberOfEquations; i++) {
            vectorEntries[i] = equations[i];
            for (String solutionVar : solutionVars) {
                vectorEntries[i] = vectorEntries[i].replaceVariable(solutionVar, ZERO);
            }
            vectorEntries[i] = MINUS_ONE.mult(vectorEntries[i]).simplify();
        }

        Matrix m = new Matrix(matrixEntries);
        Matrix b = new Matrix(vectorEntries);

        try {

            Expression[] solutions = GaussAlgorithm.solveLinearSystemOfEquations(m, b);
            for (int i = 0; i < solutions.length; i++) {
                doPrintOutput(solutionVars.get(i), " = ", solutions[i]);
            }

            /*
             Falls Lösungen Parameter T_0, T_0, ... enthalten, dann zusätzlich
             ausgeben: T_0, T_1, ... sind beliebige freie Veränderliche.
             */
            boolean solutionContainsFreeParameter = false;
            String infoAboutFreeParameters;

            for (Expression solution : solutions) {
                solutionContainsFreeParameter = solutionContainsFreeParameter || solution.contains(NotationLoader.FREE_REAL_PARAMETER_VAR + "_0");
            }

            if (solutionContainsFreeParameter) {
                boolean solutionContainsFreeParameterOfGivenIndex = true;
                int maxIndex = 0;
                while (solutionContainsFreeParameterOfGivenIndex) {
                    maxIndex++;
                    solutionContainsFreeParameterOfGivenIndex = false;
                    for (Expression solution : solutions) {
                        solutionContainsFreeParameterOfGivenIndex = solutionContainsFreeParameterOfGivenIndex || solution.contains(NotationLoader.FREE_REAL_PARAMETER_VAR + "_" + maxIndex);
                    }
                }
                maxIndex--;

                ArrayList<MultiIndexVariable> freeParameterVars = new ArrayList<>();
                for (int i = 0; i <= maxIndex; i++) {
                    freeParameterVars.add(new MultiIndexVariable(NotationLoader.FREE_REAL_PARAMETER_VAR, BigInteger.valueOf(i)));
                }
                if (maxIndex == 0) {
                    infoAboutFreeParameters = Translator.translateOutputMessage("MCC_IS_FREE_VARIABLE_IN_SOLVESYSTEM");
                } else {
                    infoAboutFreeParameters = Translator.translateOutputMessage("MCC_ARE_FREE_VARIABLES_IN_SOLVESYSTEM");
                }

                ArrayList infoAboutFreeParametersForGraphicArea = new ArrayList();
                for (int i = 0; i < freeParameterVars.size(); i++) {
                    infoAboutFreeParametersForGraphicArea.add(freeParameterVars.get(i));
                    if (i < freeParameterVars.size() - 1) {
                        infoAboutFreeParametersForGraphicArea.add(", ");
                    }
                }
                infoAboutFreeParametersForGraphicArea.add(infoAboutFreeParameters);
                doPrintOutput(infoAboutFreeParametersForGraphicArea);

            }

        } catch (EvaluationException e) {
            doPrintOutput(Translator.translateOutputMessage("MCC_SYSTEM_NOT_SOLVABLE"));
        }

    }

    @Execute(type = TypeCommand.table)
    private static void executeTable(Command command) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        HashSet<String> vars = new HashSet<>();
        logExpr.addContainedIndeterminates(vars);
        int numberOfVars = vars.size();
        if (numberOfVars > 20) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES", logExpr));
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION"), logExpr, ":");

        // Falls es sich um einen konstanten Ausdruck handelt.
        if (numberOfVars == 0) {
            boolean value = logExpr.evaluate();
            if (value) {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1"),
                        logExpr,
                        Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2"));
            } else {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1"),
                        logExpr,
                        Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3"));
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

        String varsInOrder = Translator.translateOutputMessage("MCC_ORDER_OF_VARIABLES_IN_TABLE");
        for (int i = 0; i < varsEnumerated.size(); i++) {
            varsInOrder = varsInOrder + varsEnumerated.get(i) + ", ";
        }
        varsInOrder = varsInOrder.substring(0, varsInOrder.length() - 2);

        doPrintOutput(varsInOrder);

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
                binaryCounter = binaryCounter + "1";
            } else {
                binaryCounter = binaryCounter + "0";
            }

            doPrintOutput(binaryCounter);

            varsValues = LogicalExpression.binaryCounter(varsValues);

        }

    }

    @Execute(type = TypeCommand.tangent)
    private static void executeTangent(Command command)
            throws EvaluationException {

        Expression expr = (Expression) command.getParams()[0];
        HashMap<String, Expression> vars = (HashMap<String, Expression>) command.getParams()[1];

        ArrayList tangentInfoForGraphicArea = new ArrayList();
        tangentInfoForGraphicArea.add(Translator.translateOutputMessage("MCC_EQUATION_OF_TANGENT_SPACE_1"));
        tangentInfoForGraphicArea.add(expr);
        tangentInfoForGraphicArea.add(Translator.translateOutputMessage("MCC_EQUATION_OF_TANGENT_SPACE_2"));

        for (String var : vars.keySet()) {
            tangentInfoForGraphicArea.add(var + " = ");
            tangentInfoForGraphicArea.add(vars.get(var));
            tangentInfoForGraphicArea.add(", ");
        }
        // In der textlichen und in der grafischen Ausgabe das letzte (überflüssige) Komma entfernen.
        tangentInfoForGraphicArea.remove(tangentInfoForGraphicArea.size() - 1);
        tangentInfoForGraphicArea.add(":");

        Expression tangent = AnalysisMethods.getTangentSpace(expr.simplify(), vars);

        doPrintOutput(tangentInfoForGraphicArea);
        doPrintOutput("Y = ", tangent);

        if (vars.size() == 1) {

            String var = "";
            // vars enthält in diesem Fall nur eine Variable.
            for (String uniqueVar : vars.keySet()) {
                var = uniqueVar;
            }

            // Im Falle einer oder zweier Veränderlichen: den Graphen der Funktion und den Tangentialraum zeichnen.
            try {
                double[][] tangentPoint = new double[1][2];
                tangentPoint[0][0] = vars.get(var).evaluate();
                tangentPoint[0][1] = expr.replaceVariable(var, vars.get(var)).evaluate();

                ArrayList<Expression> exprs = new ArrayList<>();
                exprs.add(expr);
                exprs.add(tangent);
                graphicPanel2D.setVarAbsc(var);
                graphicPanel2D.setSpecialPoints(tangentPoint);
                graphicPanel2D.drawGraphs2D(Variable.create(var).sub(1), Variable.create(var).add(1), exprs);

            } catch (EvaluationException e) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPH_NOT_POSSIBLE_TO_DRAW"));
            }

        } else if (vars.size() == 2) {

            Iterator iter = vars.keySet().iterator();
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

            try {
                Expression x_0 = TWO.mult(vars.get(varAbsc).abs()).simplify(simplifyTypesPlot);
                Expression y_0 = TWO.mult(vars.get(varOrd).abs()).simplify(simplifyTypesPlot);
                if (x_0.equals(ZERO)) {
                    x_0 = ONE;
                }
                if (y_0.equals(ZERO)) {
                    y_0 = ONE;
                }

                graphicPanel3D.setParameters(varAbsc, varOrd, 150, 200, 30, 30);
                graphicPanel3D.drawGraphs3D(MINUS_ONE.mult(x_0), x_0, MINUS_ONE.mult(y_0), y_0, expr, tangent);
            } catch (EvaluationException e) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPH_NOT_POSSIBLE_TO_DRAW"));
            }

        }

    }

    @Execute(type = TypeCommand.taylordiffeq)
    private static void executeTaylorDiffEq(Command command) throws EvaluationException {

        int ord = (int) command.getParams()[2];
        HashSet<String> vars = new HashSet<>();
        Expression expr = (Expression) command.getParams()[0];
        expr.addContainedIndeterminates(vars);

        HashSet<String> varsWithoutPrimes = new HashSet<>();
        String varWithoutPrimes;
        for (String var : vars) {
            varWithoutPrimes = var;
            if (!varWithoutPrimes.replaceAll("'", "").equals(command.getParams()[1])) {
                if (varWithoutPrimes.length() - varWithoutPrimes.replaceAll("'", "").length() >= ord) {
                    throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_DIFFEQ", ord, ord - 1));
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
                Iterator<String> iter = varsWithoutPrimes.iterator();
                varOrd = iter.next();
            }
        } else {
            Iterator<String> iter = varsWithoutPrimes.iterator();
            varOrd = iter.next();
            if (varOrd.equals(varAbsc)) {
                varOrd = iter.next();
            }
        }

        try {
            Variable.setDependingOnVariable(varOrd, varAbsc);
            expr = expr.simplify();

            if (doesExpressionContainDerivativesOfTooHighOrder(expr, varOrd, ord - 1)) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ", ord, ord - 1));
            }

            Expression result = AnalysisMethods.getTaylorPolynomialFromDifferentialEquation(expr, varAbsc, varOrd, ord, x_0, y_0, k);

            // Formulierung und Ausgabe des AWP.
            String formulationOfAWP = Translator.translateOutputMessage("MCC_TAYLORPOLYNOMIAL_FOR_SOLUTION_OF_DIFFEQ", k) + varOrd;
            ArrayList formulationOfAWPForGraphicArea = new ArrayList();

            for (int i = 0; i < ord; i++) {
                formulationOfAWP = formulationOfAWP + "'";
            }
            formulationOfAWP += "(" + varAbsc + ") = ";

            formulationOfAWPForGraphicArea.add(formulationOfAWP);
            formulationOfAWPForGraphicArea.add(expr);

            String varOrdWithPrimes;
            for (int i = 0; i < ord; i++) {
                formulationOfAWPForGraphicArea.add(", ");
                varOrdWithPrimes = varOrd;
                for (int j = 0; j < i; j++) {
                    varOrdWithPrimes = varOrdWithPrimes + "'";
                }

                formulationOfAWPForGraphicArea.add(varOrdWithPrimes);
                formulationOfAWPForGraphicArea.add(TypeBracket.BRACKET_SURROUNDING_EXPRESSION);
                formulationOfAWPForGraphicArea.add(x_0);
                formulationOfAWPForGraphicArea.add(" = ");
                formulationOfAWPForGraphicArea.add(y_0[i]);
            }

            formulationOfAWPForGraphicArea.add(":");

            doPrintOutput(formulationOfAWPForGraphicArea);

            // Ausgabe des Taylorpolynoms        
            doPrintOutput(varOrd + "(" + varAbsc + ") = ", result);

        } finally {
            Variable.setDependingOnVariable(varOrd, null);
        }

    }

    @Execute(type = TypeCommand.undeffuncs)
    private static void executeUndefFuncs(Command command)
            throws EvaluationException {

        Object[] functions = command.getParams();
        for (Object function : functions) {
            if (SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet().contains((String) function)) {
                SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().remove((String) function);
                SelfDefinedFunction.getArgumentsForSelfDefinedFunctions().remove((String) function);
                SelfDefinedFunction.getInnerExpressionsForSelfDefinedFunctions().remove((String) function);
                doPrintOutput(Translator.translateOutputMessage("MCC_FUNCTION_IS_REMOVED", function));
            }
        }

    }

    @Execute(type = TypeCommand.undefvars)
    private static void executeUndefVars(Command command)
            throws EvaluationException {

        Object[] vars = command.getParams();
        for (Object var : vars) {
            if (Variable.getVariablesWithPredefinedValues().contains((String) var)) {
                Variable.setPreciseExpression((String) var, null);
                doPrintOutput(Translator.translateOutputMessage("MCC_VARIABLE_IS_INDETERMINATE_AGAIN", var));
            }
        }

    }

    @Execute(type = TypeCommand.undefallfuncs)
    private static void executeUndefAllFuncs(Command command) {

        HashMap<String, Expression> abstractExpressions = SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions();
        HashMap<String, Expression[]> innerExpressions = SelfDefinedFunction.getInnerExpressionsForSelfDefinedFunctions();
        HashMap<String, String[]> arguments = SelfDefinedFunction.getArgumentsForSelfDefinedFunctions();

        /*
         Einfach nur keySet() zu benutzen würde beim Iterieren zu NullPointerExceptions 
         führen. Daher besser ein neues HashSet mit den Funktionsnamen definieren und
         erst DANN zu iterieren.
         */
        HashSet<String> functionNames = new HashSet<>(SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet());

        for (String f : functionNames) {
            abstractExpressions.remove(f);
            innerExpressions.remove(f);
            arguments.remove(f);
        }
        doPrintOutput(Translator.translateOutputMessage("MCC_ALL_FUNCTIONS_ARE_REMOVED"));

    }

    @Execute(type = TypeCommand.undefallvars)
    private static void executeUndefAllVars(Command command) {

        for (String var : Variable.getVariablesWithPredefinedValues()) {
            Variable.setPreciseExpression(var, null);
        }
        doPrintOutput(Translator.translateOutputMessage("MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN"));

    }

    @Execute(type = TypeCommand.undefall)
    private static void executeUndefAll(Command command) {
        executeUndefAllFuncs(command);
        executeUndefAllVars(command);
    }

    /*
    Diverse Hilfsmethoden.
     */
    /**
     * Gibt zurück, ob expr Veränderliche enthält, deren Namen mit varOrd
     * beginnen und die mehr als ord Apostrophs enthalten.<br>
     * VORAUSSETZUNG: varOrd darf selbst keine Apostrophs enthalten.
     */
    private static boolean doesExpressionContainDerivativesOfTooHighOrder(Expression expr, String varOrd, int ord) {

        HashSet<String> vars = expr.getContainedIndeterminates();
        int numberOfPrimesInVar;
        for (String var : vars) {
            numberOfPrimesInVar = 0;
            while (var.startsWith(varOrd) && var.substring(var.length() - 1).equals("'")) {
                numberOfPrimesInVar++;
                var = var.substring(0, var.length() - 1);
            }
            if (numberOfPrimesInVar > ord) {
                return true;
            }
        }

        return false;

    }

}
