package mathcommandcompiler;

import command.Command;
import command.TypeCommand;
import abstractexpressions.expression.computation.AnalysisUtils;
import abstractexpressions.expression.computation.NumericalUtils;
import abstractexpressions.expression.computation.StatisticUtils;
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
import abstractexpressions.expression.differentialequation.SolveGeneralDifferentialEquationUtils;
import abstractexpressions.expression.basic.ExpressionCollection;
import abstractexpressions.expression.basic.SimplifyPolynomialUtils;
import static abstractexpressions.expression.classes.Expression.VALIDATOR;
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
import abstractexpressions.matrixexpression.computation.EigenvaluesEigenvectorsUtils;
import abstractexpressions.matrixexpression.computation.GaussAlgorithmUtils;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.logicalexpression.classes.LogicalVariable;
import mathtool.MathToolGUI;
import abstractexpressions.matrixexpression.classes.Matrix;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import abstractexpressions.matrixexpression.basic.MatrixExpressionCollection;
import operationparser.OperationParser;
import abstractexpressions.expression.equation.SolveGeneralEquationUtils;
import abstractexpressions.expression.equation.SolveGeneralSystemOfEquationsUtils;
import abstractexpressions.output.EditableAbstractExpression;
import abstractexpressions.output.EditableString;
import computationbounds.ComputationBounds;
import exceptions.CancellationException;
import graphic.GraphicPanelCylindrical;
import graphic.GraphicPanelImplicit2D.MarchingSquare;
import graphic.GraphicPanelImplicit3D;
import graphic.GraphicPanelImplicit3D.MarchingCube;
import graphic.GraphicPanelSpherical;
import graphic.GraphicPanelSurface;
import graphic.GraphicPanelVectorField2D;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import notations.NotationLoader;
import mathtool.annotations.Execute;
import mathtool.annotations.GetCommand;
import mathtool.component.components.LegendGUI;
import mathtool.lang.translator.Translator;
import mathtool.utilities.MathToolUtilities;
import util.OperationDataTO;
import util.OperationParsingUtils;

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
    private static GraphicPanelImplicit3D graphicPanelImplicit3D;
    private static GraphicPanelCurves2D graphicPanelCurves2D;
    private static GraphicPanelCurves3D graphicPanelCurves3D;
    private static GraphicPanelPolar graphicPanelPolar2D;
    private static GraphicPanelCylindrical graphicPanelCylindrical;
    private static GraphicPanelSpherical graphicPanelSpherical;
    private static GraphicPanelSurface graphicPanelSurface;
    private static GraphicPanelVectorField2D graphicPanelVectorField2D;

    private static GraphicArea mathToolGraphicArea;
    private static JTextArea mathToolTextArea;

    private static final HashSet<TypeSimplify> simplifyTypesExpand = new HashSet<>();
    private static final HashSet<TypeSimplify> simplifyTypesExpandShort = new HashSet<>();
    private static final HashSet<TypeSimplify> simplifyTypesPlot = new HashSet<>();
    private static final HashSet<TypeSimplify> simplifyTypesSolveSystem = new HashSet<>();

    static {
        simplifyTypesExpand.add(TypeSimplify.order_difference_and_division);
        simplifyTypesExpand.add(TypeSimplify.order_sums_and_products);
        simplifyTypesExpand.add(TypeSimplify.simplify_basic);
        simplifyTypesExpand.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypesExpand.add(TypeSimplify.simplify_expand_powerful);
        simplifyTypesExpand.add(TypeSimplify.simplify_collect_products);
        simplifyTypesExpand.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypesExpand.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypesExpand.add(TypeSimplify.simplify_reduce_differences_and_quotients_advanced);
        simplifyTypesExpand.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypesExpand.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypesExpand.add(TypeSimplify.simplify_functional_relations);

        simplifyTypesExpandShort.add(TypeSimplify.order_difference_and_division);
        simplifyTypesExpandShort.add(TypeSimplify.order_sums_and_products);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_basic);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_expand_short);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_collect_products);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_reduce_differences_and_quotients_advanced);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypesExpandShort.add(TypeSimplify.simplify_functional_relations);

        simplifyTypesPlot.add(TypeSimplify.order_difference_and_division);
        simplifyTypesPlot.add(TypeSimplify.order_sums_and_products);
        simplifyTypesPlot.add(TypeSimplify.simplify_basic);
        simplifyTypesPlot.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypesPlot.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypesPlot.add(TypeSimplify.simplify_collect_products);
        simplifyTypesPlot.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypesPlot.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypesPlot.add(TypeSimplify.simplify_reduce_differences_and_quotients_advanced);
        simplifyTypesPlot.add(TypeSimplify.simplify_functional_relations);

        simplifyTypesSolveSystem.add(TypeSimplify.order_difference_and_division);
        simplifyTypesSolveSystem.add(TypeSimplify.order_sums_and_products);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_basic);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_expand_powerful);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_collect_products);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_factorize_all_but_rationals);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_reduce_differences_and_quotients_advanced);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypesSolveSystem.add(TypeSimplify.simplify_functional_relations);
        simplifyTypesSolveSystem.add(TypeSimplify.order_sums_and_products);
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

    public static void setGraphicPanelImplicit3D(GraphicPanelImplicit3D gPImplicit3D) {
        graphicPanelImplicit3D = gPImplicit3D;
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

    public static void setGraphicPanelSurface(GraphicPanelSurface gPSurface) {
        graphicPanelSurface = gPSurface;
    }

    public static void setGraphicPanelVectorField2D(GraphicPanelVectorField2D gPVectorField2D) {
        graphicPanelVectorField2D = gPVectorField2D;
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
        boolean nextExpressionIsSurroundedByBrackets = false;
        for (Object o : out) {
            if (o instanceof EditableAbstractExpression) {
                lineToPrint += ((EditableAbstractExpression) o).getAbstractExpression().toString();
            } else if (o instanceof EditableString) {
                lineToPrint += ((EditableString) o).getText();
            } else if (o instanceof TypeBracket) {
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
     * Leitet die Ausgabe an die textliche und die grafische Ausgabeoberfläche
     * weiter.
     */
    private static void doPrintOutput(ArrayList out) {

        // Textliche Ausgabe.
        String lineToPrint = "";
        boolean nextExpressionIsSurroundedByBrackets = false;
        for (Object o : out) {
            if (o instanceof EditableAbstractExpression) {
                lineToPrint += ((EditableAbstractExpression) o).getAbstractExpression().toString();
            } else if (o instanceof EditableString) {
                lineToPrint += ((EditableString) o).getText();
            } else if (o instanceof TypeBracket) {
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
     * @throws ExpressionException
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
                        throw (ExpressionException) e.getCause();
                    }
                    if (e.getCause() instanceof CancellationException) {
                        throw (CancellationException) e.getCause();
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
        if (VALIDATOR.isValidIdentifier(functionNameAndArguments) && !Expression.isPI(functionNameAndArguments)) {

            Expression preciseExpression;
            Set<String> vars;
            try {
                preciseExpression = Expression.build(functionTerm);
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
            expr = Expression.build(functionTerm);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INVALID_EXPRESSION_ON_RIGHT_SIDE") + e.getMessage());
        }

        Set<String> vars = expr.getContainedIndeterminates();

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
            OperationDataTO functionData = OperationParsingUtils.getOperationData(functionNameAndArguments);
            functionName = functionData.getOperationName();
            functionVars = functionData.getOperationArguments();
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
            if (!VALIDATOR.isValidIdentifier(functionVar) || Variable.getVariablesWithPredefinedValues().contains(functionVar)) {
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
            for (int i = 0; i < n; i++) {
                if (expressions.indexOf("=") == 0) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_GENERAL_PARAMETER_IN_LATEX_EMPTY"));
                }
                exprs[i] = Expression.build(expressions.substring(0, expressions.indexOf("=")));
                expressions = expressions.substring(expressions.indexOf("=") + 1, expressions.length());
            }
            if (expressions.length() == 0) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_GENERAL_PARAMETER_IN_LATEX_EMPTY"));
            }
            exprs[n] = Expression.build(expressions);
            return new Command(TypeCommand.latex, exprs);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_PARAMETER_IN_LATEX") + e.getMessage());
        }

    }

    @GetCommand(type = TypeCommand.normal)
    private static Command getCommandNormal(String[] params) throws ExpressionException {

        /*
         Struktur: normal(f, var_1 = value_1, ..., var_n = value_n)
         f = Ausdruck, welcher eine Funktion repräsentiert. var_i =
         Variable value_i = reelle Zahl. Es müssen alle Variablen unter den
         var_i vorkommen, welche auch in f vorkommen.
         */
        if (params.length < 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_NORMAL"));
        }

        Set<String> vars;
        Expression expr;
        try {
            expr = Expression.build(params[0]);
            vars = expr.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_NORMAL") + e.getMessage());
        }

        /*
         Ermittelt die Anzahl der Variablen, von denen die Funktion
         abhängt, von der der Tangentialraum berechnet werden soll.
         */
        for (int i = 1; i < params.length; i++) {
            if (!params[i].contains("=")) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_NORMAL", i + 1));
            }
            if (!VALIDATOR.isValidIdentifier(params[i].substring(0, params[i].indexOf("=")))) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_A_VALID_VARIABLE_IN_NORMAL", params[i].substring(0, params[i].indexOf("="))));
            }
            try {
                Expression point = Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet<String>());
                if (!point.isConstant()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_NORMAL", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_NORMAL", i + 1));
            }
        }

        // Es wird geprüft, ob keine Veränderlichen doppelt auftreten.
        for (int i = 1; i < params.length; i++) {
            for (int j = i + 1; j < params.length; j++) {
                if (params[i].substring(0, params[i].indexOf("=")).equals(params[j].substring(0, params[j].indexOf("=")))) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLES_OCCUR_TWICE_IN_NORMAL", params[i].substring(0, params[i].indexOf("="))));
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
                    Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length())));
        }

        return new Command(TypeCommand.normal, new Object[]{expr, varsContainedInParams});

    }

    @GetCommand(type = TypeCommand.plot2d)
    private static Command getCommandPlot2D(String[] params) throws ExpressionException {

        /*
         Struktur: plot2d(f_1(x), ..., f_n(x), x, x_0, x_1). f_i: Ausdruck in einer 
         Variablen x. x_0 < x_1: Grenzen des Zeichenbereichs.
         */
        if (params.length < 4) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT2D"));
        }

        Object[] commandParams = new Object[params.length];
        HashSet<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 3; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
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

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[params.length - 2] = Expression.build(params[params.length - 2]);
            ((Expression) commandParams[params.length - 2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length - 1));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length - 1));
        }

        try {
            commandParams[params.length - 1] = Expression.build(params[params.length - 1]);
            ((Expression) commandParams[params.length - 1]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOT2D", params.length));
        }

        return new Command(TypeCommand.plot2d, commandParams);

    }

    @GetCommand(type = TypeCommand.plotimplicit2d)
    private static Command getCommandPlotImplicit2D(String[] params) throws ExpressionException {

        /*
         Struktur: plotimplicit2d(F(x, y) = G(x, y), x, y, x_0, x_1, y_0, y_1). 
         F, G: Ausdrücke in höchstens drei Variablen x, y, z. x_0 < x_1,
         y_0 < y_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT2D"));
        }

        Object[] commandParams = new Object[7];
        Set<String> vars = new HashSet<>();

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT2D_MUST_BE_A_VALID_EQUATION"));
        }

        Expression left, right;
        try {
            left = Expression.build(params[0].substring(0, params[0].indexOf("=")));
            right = Expression.build(params[0].substring(params[0].indexOf("=") + 1));
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

        Set<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < 7; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D", i + 1));
            }
        }

        return new Command(TypeCommand.plotimplicit2d, commandParams);

    }

    @GetCommand(type = TypeCommand.plot3d)
    private static Command getCommandPlot3D(String[] params) throws ExpressionException {

        /*
         Struktur: plot3d(f_1(x, y), ..., f_n(x, y), x, y, x_0, x_1, y_0, y_1) f_i: Ausdruck 
         in höchstens zwei Variablen x und y. x_0 < x_1, y_0 < y_1: Grenzen des Zeichenbereichs.
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT3D"));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
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

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
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

    @GetCommand(type = TypeCommand.plotimplicit3d)
    private static Command getCommandPlotImplicit3D(String[] params) throws ExpressionException {

        /*
         Struktur: plotimplicit3d(F(x, y, z) = G(x, y, z), x, y, z, x_0, x_1, y_0, y_1, z_0, z_1). 
         F, G: Ausdrücke in höchstens drei Variablen x, y, z. x_0 < x_1,
         y_0 < y_1, z_0 < z_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 10) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT3D"));
        }

        Object[] commandParams = new Object[10];
        Set<String> vars = new HashSet<>();

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT3D_MUST_BE_A_VALID_EQUATION"));
        }

        Expression left, right;
        try {
            left = Expression.build(params[0].substring(0, params[0].indexOf("=")));
            right = Expression.build(params[0].substring(params[0].indexOf("=") + 1));
            left.addContainedIndeterminates(vars);
            right.addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT3D_MUST_BE_A_VALID_EQUATION"));
        }

        commandParams[0] = new Expression[]{left, right};

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D", 1));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D", 2));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[3])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D", 3));
        }
        if (params[1].equals(params[2]) || params[1].equals(params[3]) || params[2].equals(params[3])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT3D"));
        }

        commandParams[1] = params[1];
        commandParams[2] = params[2];
        commandParams[3] = params[3];
        vars.remove(params[1]);
        vars.remove(params[2]);
        vars.remove(params[3]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT3D", params[1], params[2], params[3]));
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = 4; i < 10; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT3D", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT3D", i + 1));
            }
        }

        return new Command(TypeCommand.plotimplicit3d, commandParams);

    }

    @GetCommand(type = TypeCommand.plotcurve2d)
    private static Command getCommandPlotCurve2D(String[] params) throws ExpressionException {

        /*
         Struktur: plotcurve2d(f(t), t, t_0, t_1), f(t): 
         Matrizenusdruck in einer Variablen t. t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 4) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE2D"));
        }

        Object[] commandParams = new Object[4];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
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

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[2] = Expression.build(params[2]);
            ((Expression) commandParams[2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D", 3));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D", 3));
        }

        try {
            commandParams[3] = Expression.build(params[3]);
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
         Struktur: plotcurve3d(f(t), t, t_0, t_1), f(t): 
         Matrizenusdruck in einer Variablen t. t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 4) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE3D"));
        }

        Object[] commandParams = new Object[4];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
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

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[2] = Expression.build(params[2]);
            ((Expression) commandParams[2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D", 3));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D", 3));
        }

        try {
            commandParams[3] = Expression.build(params[3]);
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
         Struktur: plotcylindrical(F_1(s, t), ..., F_n(s, t), s, t, s_0, s_1, t_0, t_1). 
         F_i: Ausdruck in höchstens zwei Variablen s und t. s_0 < s_1, t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTCYLINDRICAL"));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTCYLINDRICAL", i + 1, e.getMessage()));
            }
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTCYLINDRICAL", vars.size()));
        }

        Set<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!VALIDATOR.isValidIdentifier(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
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

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
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
         Struktur: plotspherical(F_1(s, t), ..., F_n(s, t), s, t, s_0, s_1, t_0, t_1). 
         F_i: Ausdruck in höchstens zwei Variablen s und t. s_0 < s_1, t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTSPHERICAL"));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTSPHERICAL", i + 1, e.getMessage()));
            }
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSPHERICAL", vars.size()));
        }

        Set<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!VALIDATOR.isValidIdentifier(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
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

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
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

    @GetCommand(type = TypeCommand.plotsurface)
    private static Command getCommandPlotSurface(String[] params) throws ExpressionException {

        /*
         Struktur: plotsurface(F_1(s, t), s, t, s_0, s_1, t_0, t_1). 
         F_i: Ausdruck in höchstens zwei Variablen s und t. s_0 < s_1, t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTSURFACE"));
        }

        Object[] commandParams = new Object[7];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOTSURFACE", 1, e.getMessage()));
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSURFACE", vars.size()));
        }

        Set<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!VALIDATOR.isValidIdentifier(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSURFACE", i + 1));
            }
            if (varsInParams.contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSURFACE"));
            }
            varsInParams.add(params[i]);
            commandParams[i] = params[i];
        }

        // Prüfen, ob Veränderliche, die in vars auftreten, auch in varsInParams auftreten.
        for (String var : vars) {
            if (!varsInParams.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSURFACE", var));
            }
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSURFACE", i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSURFACE", i + 1));
            }
        }

        return new Command(TypeCommand.plotsurface, commandParams);

    }

    @GetCommand(type = TypeCommand.plotvectorfield2d)
    private static Command getCommandPlotVectorField2D(String[] params) throws ExpressionException {

        /*
         Struktur: plotvectorfield2d(f(x, y), x, y, x_0, x_1, y_0, y_1), f(x, y): 
         Matrizenusdruck in zwei Variablen. x_0 < x_1, y_0 < y_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELD2D"));
        }

        Object[] commandParams = new Object[7];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
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

        Set<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < 7; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
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

    @GetCommand(type = TypeCommand.plotpolar)
    private static Command getCommandPlotPolar(String[] params) throws ExpressionException {

        /*
         Struktur: plotpolar(f_1(t), ..., f_n(t), t, t_0, t_1). f_i(t): Ausdruck in 
         einer Variablen t. t_0 < t_1: Grenzen des Zeichenbereichs..
         */
        if (params.length < 4) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTPOLAR"));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 3; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTPOLAR", i + 1));
            }
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 3])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTPOLAR", params.length - 2));
        }

        commandParams[params.length - 3] = params[params.length - 3];
        vars.remove(params[params.length - 3]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTPOLAR", vars.size()));
        }

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[params.length - 2] = Expression.build(params[params.length - 2]);
            ((Expression) commandParams[params.length - 2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR", params.length - 1));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR", params.length - 1));
        }

        try {
            commandParams[params.length - 1] = Expression.build(params[params.length - 1]);
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
        if (!Expression.isValidIndeterminate(varAbsc)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ", 2));
        }
        if (!Expression.isValidIndeterminate(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ", 3));
        }
        if (varAbsc.equals(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_SOLVEDIFFEQ"));
        }

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_1_PARAMETER_IN_SOLVEDIFFEQ_MUST_CONTAIN_EQUALITY_SIGN"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt.
         */
        Set<String> vars = new HashSet<>();
        Expression diffEquationLeft, diffEquationRight;
        try {
            diffEquationLeft = Expression.build(params[0].substring(0, params[0].indexOf("=")));
            diffEquationRight = Expression.build(params[0].substring(params[0].indexOf("=") + 1));
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
         Struktur: solvediffeq(Equation, varAbsc, varOrd, ord, x_0, x_1, y_0, y'(0), ...,
         y^(ord - 1)(0)) Equation: Rechte Seite der DGL y^{(ord)} =
         Equation. Anzahl der parameter ist also = ord + 5 var = Variable in
         der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         fest x_1 = Obere x-Schranke für die numerische Berechnung
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_NUMBER_OF_PARAMETERS_IN_SOLVEDIFFEQ"));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[3]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVEDIFFEQ"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVEDIFFEQ"));
        }

        if (!Expression.isValidIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ", 2));
        }
        if (!Expression.isValidIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ", 3));
        }

        String varAbsc = params[1];
        String varOrd = params[2];
        if (varAbsc.equals(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_SOLVEDIFFEQ"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt: Beispielsweise
         darf in einer DGL der Ordnung 3 nicht y''', y'''' etc. auf der
         rechten Seite auftreten.
         */
        Set<String> vars;
        Expression diffEquation;
        try {
            diffEquation = Expression.build(params[0]);
            vars = diffEquation.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDIFFEQ") + e.getMessage());
        }

        String varWithoutPrimes;
        for (String var : vars) {
            varWithoutPrimes = var.replaceAll("'", "");
            if (varWithoutPrimes.equals(varOrd)) {
                if (var.length() - varWithoutPrimes.length() >= ord) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ", ord, ord - 1));
                }
            }
            if (!varWithoutPrimes.equals(var) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDIFFEQ"));
            }
            if (!varWithoutPrimes.equals(varAbsc) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDIFFEQ"));
            }
        }

        if (params.length < ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDIFFEQ"));
        }
        if (params.length > ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_TOO_MANY_PARAMETERS_IN_SOLVEDIFFEQ"));
        }

        // Prüft, ob die AWP-Daten korrekt sind.
        for (int i = 4; i < ord + 6; i++) {
            try {
                Expression limit = Expression.build(params[i]);
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

        Object[] commandParams = new Object[ord + 6];
        commandParams[0] = diffEquation;
        commandParams[1] = params[1];
        commandParams[2] = params[2];
        commandParams[3] = ord;
        for (int i = 4; i < ord + 6; i++) {
            commandParams[i] = Expression.build(params[i], vars);
        }

        return new Command(TypeCommand.solvediffeq, commandParams);

    }

    @GetCommand(type = TypeCommand.tangent)
    private static Command getCommandTangent(String[] params) throws ExpressionException {

        /*
         Struktur: tangent(f, var_1 = value_1, ..., var_n = value_n)
         f = Ausdruck, welcher eine Funktion repräsentiert. var_i =
         Variable value_i = reelle Zahl. Es müssen alle Variablen unter den
         var_i vorkommen, welche auch in f vorkommen.
         */
        if (params.length < 2) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TANGENT"));
        }

        Set<String> vars;
        Expression expr;
        try {
            expr = Expression.build(params[0]);
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
            if (!VALIDATOR.isValidIdentifier(params[i].substring(0, params[i].indexOf("=")))) {
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
                    Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length())));
        }

        return new Command(TypeCommand.tangent, new Object[]{expr, varsContainedInParams});

    }

    @GetCommand(type = TypeCommand.taylordiffeq)
    private static Command getCommandTaylorDiffEq(String[] params) throws ExpressionException {

        /*
         Struktur: taylordiffeq(Equation, varAbsc, varOrd, ord, x_0, y_0, y'(0), ...,
         y^(ord - 1)(0), k) Equation: Rechte Seite der DGL y^{(ord)} =
         Equation. Anzahl der parameter ist also = ord + 6 var = Variable in
         der DGL ord = Ordnung der DGL. x_0, y_0, y'(0), ... legen das AWP
         fest k = Ordnung des Taylorpolynoms (an der Stelle x_0)
         */
        if (params.length < 7) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ"));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[3]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_4_PARAMETER_IN_TAYLORDIFFEQ"));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_4_PARAMETER_IN_TAYLORDIFFEQ"));
        }

        if (!Expression.isValidIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_TAYLORDIFFEQ", 2));
        }
        if (!Expression.isValidIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_TAYLORDIFFEQ", 3));
        }

        String varAbsc = params[1];
        String varOrd = params[2];
        if (varAbsc.equals(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_TAYLORDIFFEQ"));
        }

        /*
         Prüft, ob es sich um eine korrekte DGL handelt: Beispielsweise
         darf in einer DGL der Ordnung 3 nicht y''', y'''' etc. auf der
         rechten Seite auftreten.
         */
        Set<String> vars;
        Expression diffEquation;
        try {
            diffEquation = Expression.build(params[0]);
            vars = diffEquation.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_1_PARAMETER_IN_TAYLORDIFFEQ") + e.getMessage());
        }

        String varWithoutPrimes;
        for (String var : vars) {
            varWithoutPrimes = var.replaceAll("'", "");
            if (varWithoutPrimes.equals(varOrd)) {
                if (var.length() - varWithoutPrimes.length() >= ord) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ", ord, ord - 1));
                }
            }
            if (!varWithoutPrimes.equals(var) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDIFFEQ"));
            }
            if (!varWithoutPrimes.equals(varAbsc) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDIFFEQ"));
            }
        }

        if (params.length < ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ"));
        }

        if (params.length > ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_TOO_MANY_PARAMETERS_IN_TAYLORDIFFEQ"));
        }

        // Prüft, ob die AWP-Daten korrekt sind.
        Set<String> varsInLimits;
        Expression limit;
        for (int i = 4; i < ord + 5; i++) {
            try {
                limit = Expression.build(params[i]).simplify();
                varsInLimits = limit.getContainedIndeterminates();
                /*
                 Im Folgenden wird geprüft, ob in den Anfangsbedingungen
                 die Variablen aus der eigentlichen DGL nicht auftreten.
                 */
                if (varsInLimits.contains(varAbsc) || varsInLimits.contains(varOrd)) {
                    throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ", i + 1));
                }
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ", i + 1));
            }
        }

        int ordOfTaylorPolynomial;
        try {
            ordOfTaylorPolynomial = Integer.parseInt(params[ord + 5]);
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage("MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_TAYLORDIFFEQ"));
        }

        Object[] commandParams = new Object[ord + 6];
        commandParams[0] = diffEquation;
        commandParams[1] = params[1];
        commandParams[2] = params[2];
        commandParams[3] = ord;
        for (int i = 4; i < ord + 5; i++) {
            commandParams[i] = Expression.build(params[i], vars);
        }
        commandParams[ord + 5] = ordOfTaylorPolynomial;

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

        OperationDataTO commandData = OperationParsingUtils.getOperationData(input);
        String commandName = commandData.getOperationName();
        String[] params = commandData.getOperationArguments();

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
                        throw (EvaluationException) e.getCause();
                    }
                    if (e.getCause() instanceof CancellationException) {
                        throw (CancellationException) e.getCause();
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
            doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(expr));

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
            doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(matExpr));

        }

    }

    @Execute(type = TypeCommand.ccnf)
    private static void executeCCNF(Command command) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        Set<String> vars = new HashSet<>();
        logExpr.addContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF", logExpr));
        }
        LogicalExpression logExprInCCNF = logExpr.toCCNF();
        doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(logExprInCCNF));

    }

    @Execute(type = TypeCommand.cdnf)
    private static void executeCDNF(Command command) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        Set<String> vars = new HashSet<>();
        logExpr.addContainedVars(vars);
        if (vars.size() > 20) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF", logExpr));
        }
        LogicalExpression logExprInCDNF = logExpr.toCDNF();
        doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(logExprInCDNF));

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
        Set<String> vars = Variable.getVariablesWithPredefinedValues();
        if (!vars.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_LIST_OF_VARIABLES"));
            for (String var : vars) {
                doPrintOutput(var, " = ", MathToolUtilities.convertToEditableAbstractExpression(Variable.create(var).getPreciseExpression()));
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

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsUtils.getEigenvalues((MatrixExpression) command.getParams()[0]);

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
            eigenvaluesAsArrayList.add(MathToolUtilities.convertToEditableAbstractExpression(eigenvalues.get(i)));
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

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsUtils.getEigenvalues((MatrixExpression) command.getParams()[0]);

        MatrixExpressionCollection eigenvectors;
        MatrixExpression matrix = (MatrixExpression) command.getParams()[0];

        ArrayList eigenvectorsAsArrayList;

        for (int i = 0; i < eigenvalues.getBound(); i++) {

            // Sicherheitshalber! Sollte eigentlich nie passieren.
            if (eigenvalues.get(i) == null) {
                continue;
            }
            // Eigenvektoren berechnen.
            eigenvectors = EigenvaluesEigenvectorsUtils.getEigenvectorsForEigenvalue(matrix, eigenvalues.get(i));

            eigenvectorsAsArrayList = new ArrayList();
            eigenvectorsAsArrayList.add(Translator.translateOutputMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_1"));
            eigenvectorsAsArrayList.add(eigenvalues.get(i));
            eigenvectorsAsArrayList.add(Translator.translateOutputMessage("MCC_EIGENVECTORS_FOR_EIGENVALUE_2"));
            if (eigenvectors.isEmpty()) {
                eigenvectorsAsArrayList.add(Translator.translateOutputMessage("MCC_EIGENVECTORS_NO_EXPLICIT_EIGENVECTORS"));
            } else {
                for (int j = 0; j < eigenvectors.getBound(); j++) {
                    eigenvectorsAsArrayList.add(MathToolUtilities.convertToEditableAbstractExpression(eigenvectors.get(j)));
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
        BigDecimal e = AnalysisUtils.getDigitsOfE((int) command.getParams()[0]);
        doPrintOutput(Translator.translateOutputMessage("MCC_DIGITS_OF_E", (int) command.getParams()[0]), MathToolUtilities.convertToEditableString(e));
    }

    @Execute(type = TypeCommand.expand)
    private static void executeExpand(Command command) throws EvaluationException {

        Expression expr = (Expression) command.getParams()[0];

        // Voreingestellte Vereinfachungstypen müssen eingebunden werden, welche expand() nicht widersprechen.
        HashSet<TypeSimplify> simplifyTypesMathTool = MathToolGUI.getSimplifyTypes();
        if (simplifyTypesMathTool.contains(TypeSimplify.simplify_algebraic_expressions)) {
            simplifyTypesExpand.add(TypeSimplify.simplify_algebraic_expressions);
        } else {
            simplifyTypesExpand.remove(TypeSimplify.simplify_algebraic_expressions);
        }
        if (simplifyTypesMathTool.contains(TypeSimplify.simplify_functional_relations)) {
            simplifyTypesExpand.add(TypeSimplify.simplify_functional_relations);
        } else {
            simplifyTypesExpand.remove(TypeSimplify.simplify_functional_relations);
        }
        if (simplifyTypesMathTool.contains(TypeSimplify.simplify_expand_logarithms)) {
            simplifyTypesExpand.add(TypeSimplify.simplify_expand_logarithms);
        } else {
            simplifyTypesExpand.remove(TypeSimplify.simplify_expand_logarithms);
        }
        if (simplifyTypesMathTool.contains(TypeSimplify.simplify_collect_logarithms)) {
            simplifyTypesExpand.add(TypeSimplify.simplify_collect_logarithms);
        } else {
            simplifyTypesExpand.remove(TypeSimplify.simplify_collect_logarithms);
        }

        expr = expr.simplify(simplifyTypesExpand);

        // Voreingestellte Vereinfachungstypen müssen aus simplifyTypesExpand wieder entfernt werden.
        simplifyTypesExpand.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypesExpand.add(TypeSimplify.simplify_functional_relations);
        simplifyTypesExpand.remove(TypeSimplify.simplify_expand_logarithms);
        simplifyTypesExpand.remove(TypeSimplify.simplify_collect_logarithms);

        doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(expr));

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

        ExpressionCollection zeros = SolveGeneralEquationUtils.solveEquation(derivative, ZERO, var);
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
                        multiVar, " = ", MathToolUtilities.convertToEditableAbstractExpression(extremaPoints.get(i)),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        MathToolUtilities.convertToEditableAbstractExpression(extremaValues.get(i)));
            } else {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOCAL_MAXIMUM_IN"),
                        multiVar, " = ", MathToolUtilities.convertToEditableAbstractExpression(extremaPoints.get(i)),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        MathToolUtilities.convertToEditableAbstractExpression(extremaValues.get(i)));
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
        Set<String> vars = expr.getContainedIndeterminates();
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

        ArrayList<Double> zeros = NumericalUtils.solveEquation(derivative, var, x_0.evaluate(), x_1.evaluate(), n);
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
                        multiVar, " = ", MathToolUtilities.convertToEditableString(extremaPoints.get(i)),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        MathToolUtilities.convertToEditableString(extremaValues.get(i)));
            } else {
                doPrintOutput(Translator.translateOutputMessage("MCC_LOCAL_MAXIMUM_IN"),
                        multiVar, " = ", MathToolUtilities.convertToEditableString(extremaPoints.get(i)),
                        Translator.translateOutputMessage("MCC_LOCAL_EXTREMA_FUNCTION_VALUE"),
                        MathToolUtilities.convertToEditableString(extremaValues.get(i)));
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

        MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
        MatrixExpression matExprSimplified = matExpr.simplify();
        if (!(matExprSimplified instanceof Matrix)) {
            doPrintOutput(Translator.translateOutputMessage("MCC_KER_COULD_NOT_BE_COMPUTED_1"),
                    (MatrixExpression) command.getParams()[0],
                    Translator.translateOutputMessage("MCC_KER_COULD_NOT_BE_COMPUTED_2"));
            return;
        }

        MatrixExpressionCollection basisOfKer = GaussAlgorithmUtils.computeKernelOfMatrix((Matrix) matExprSimplified);

        if (basisOfKer.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_TRIVIAL_KER_1"),
                    matExpr,
                    Translator.translateOutputMessage("MCC_TRIVIAL_KER_2"),
                    MatrixExpression.getZeroMatrix(((Matrix) matExprSimplified).getRowNumber(), 1),
                    Translator.translateOutputMessage("MCC_TRIVIAL_KER_3"));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_BASIS_OF_KER_1"), matExpr, Translator.translateOutputMessage("MCC_BASIS_OF_KER_2"));

        ArrayList basisAsArray = new ArrayList();
        for (int i = 0; i < basisOfKer.getBound(); i++) {
            // Für graphische Ausgabe
            basisAsArray.add(MathToolUtilities.convertToEditableAbstractExpression(basisOfKer.get(i)));
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

        doPrintOutput(MathToolUtilities.convertToEditableString(latexCode));

    }

    @Execute(type = TypeCommand.normal)
    private static void executeNormal(Command command)
            throws EvaluationException {

        Expression f = (Expression) command.getParams()[0];
        HashMap<String, Expression> vars = (HashMap<String, Expression>) command.getParams()[1];

        ArrayList normalInfoForGraphicArea = new ArrayList();
        normalInfoForGraphicArea.add(Translator.translateOutputMessage("MCC_PARAMETRIZATION_OF_NORMAL_SPACE_1"));
        normalInfoForGraphicArea.add(f);
        normalInfoForGraphicArea.add(Translator.translateOutputMessage("MCC_PARAMETRIZATION_OF_NORMAL_SPACE_2"));

        for (String var : vars.keySet()) {
            normalInfoForGraphicArea.add(var + " = ");
            normalInfoForGraphicArea.add(vars.get(var));
            normalInfoForGraphicArea.add(", ");
        }
        // In der textlichen und in der grafischen Ausgabe das letzte (überflüssige) Komma entfernen.
        normalInfoForGraphicArea.remove(normalInfoForGraphicArea.size() - 1);
        normalInfoForGraphicArea.add(":");

        HashMap<String, Expression> normalLineParametrization = AnalysisUtils.getNormalLineParametrization(f.simplify(), vars);

        doPrintOutput(normalInfoForGraphicArea);

        for (String var : normalLineParametrization.keySet()) {
            doPrintOutput(var + " = ", MathToolUtilities.convertToEditableAbstractExpression(normalLineParametrization.get(var)));
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_IS_FREE_VARIABLE_IN_NORMAL", NotationLoader.FREE_REAL_PARAMETER_VAR));

//        if (vars.size() == 1) {
//
//            String var = "";
//            // vars enthält in diesem Fall nur eine Variable.
//            for (String uniqueVar : vars.keySet()) {
//                var = uniqueVar;
//            }
//
//            // Im Falle einer oder zweier Veränderlichen: den Graphen der Funktion und den Tangentialraum zeichnen.
//            try {
//                double[][] tangentPoint = new double[1][2];
//                tangentPoint[0][0] = vars.get(var).evaluate();
//                tangentPoint[0][1] = f.replaceVariable(var, vars.get(var)).evaluate();
//
//                ArrayList<Expression> exprs = new ArrayList<>();
//                exprs.add(f);
//                exprs.add(tangent);
//                graphicPanel2D.setVarAbsc(var);
//                graphicPanel2D.setSpecialPoints(tangentPoint);
//                graphicPanel2D.drawGraphs2D(Variable.create(var).sub(1), Variable.create(var).add(1), exprs);
//
//            } catch (EvaluationException e) {
//                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPH_NOT_POSSIBLE_TO_DRAW"));
//            }
//
//        } else if (vars.size() == 2) {
//
//            Iterator iter = vars.keySet().iterator();
//            String varOne = (String) iter.next();
//            String varTwo = (String) iter.next();
//
//            /*
//             Die Variablen varOne und varTwo sind evtl. noch nicht in
//             alphabetischer Reihenfolge. Dies wird hier nachgeholt. GRUND: Der
//             Zeichenbereich wird durch vier Zahlen eingegrenzt, welche den
//             Variablen in ALPHABETISCHER Reihenfolge entsprechen. Die ersten
//             beiden bilden die Grenzen für die Abszisse, die anderen beiden für
//             die Ordinate.
//             */
//            String varAbsc = varOne;
//            String varOrd = varTwo;
//
//            if (varAbsc.compareTo(varOrd) > 0) {
//                varAbsc = varTwo;
//                varOrd = varOne;
//            }
//
//            try {
//                Expression x_0 = TWO.mult(vars.get(varAbsc).abs()).simplify(simplifyTypesPlot);
//                Expression y_0 = TWO.mult(vars.get(varOrd).abs()).simplify(simplifyTypesPlot);
//                if (x_0.equals(ZERO)) {
//                    x_0 = ONE;
//                }
//                if (y_0.equals(ZERO)) {
//                    y_0 = ONE;
//                }
//
//                graphicPanel3D.setParameters(varAbsc, varOrd, 150, 200, 30, 30);
//                graphicPanel3D.drawGraphs3D(MINUS_ONE.mult(x_0), x_0, MINUS_ONE.mult(y_0), y_0, f, tangent);
//            } catch (EvaluationException e) {
//                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPH_NOT_POSSIBLE_TO_DRAW"));
//            }
//
//        }
    }

    @Execute(type = TypeCommand.pi)
    private static void executePi(Command command) throws EvaluationException {
        BigDecimal pi = AnalysisUtils.getDigitsOfPi((int) command.getParams()[0]);
        doPrintOutput(Translator.translateOutputMessage("MCC_DIGITS_OF_PI", (int) command.getParams()[0]), MathToolUtilities.convertToEditableString(pi));
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
        graphicPanel2D.setSpecialPoints((double[][]) null);
        graphicPanel2D.drawGraphs2D(x_0, x_1, exprs);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotimplicit2d)
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
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D", 4, 5));
        }
        if (yStart >= yEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D", 6, 7));
        }

        MarchingSquare[][] implicitGraph2D = NumericalUtils.solveImplicitEquation2D(expr, varAbsc, varOrd,
                x_0.evaluate(), x_1.evaluate(), y_0.evaluate(), y_1.evaluate());

        // Graphen zeichnen.
        graphicPanelImplicit2D.setExpressions(((Expression[]) command.getParams()[0])[0], ((Expression[]) command.getParams()[0])[1]);
        graphicPanelImplicit2D.setVars(varAbsc, varOrd);
        graphicPanelImplicit2D.drawImplicitGraph2D(implicitGraph2D, x_0, x_1, y_0, y_1);
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

    @Execute(type = TypeCommand.plotimplicit3d)
    private static void executePlotImplicit3D(Command command) throws EvaluationException {

        if (graphicPanelImplicit3D == null || mathToolGraphicArea == null) {
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
        String varAppl = (String) command.getParams()[3];

        Expression x_0 = ((Expression) command.getParams()[4]).simplify(simplifyTypesPlot);
        Expression x_1 = ((Expression) command.getParams()[5]).simplify(simplifyTypesPlot);
        Expression y_0 = ((Expression) command.getParams()[6]).simplify(simplifyTypesPlot);
        Expression y_1 = ((Expression) command.getParams()[7]).simplify(simplifyTypesPlot);
        Expression z_0 = ((Expression) command.getParams()[8]).simplify(simplifyTypesPlot);
        Expression z_1 = ((Expression) command.getParams()[9]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen
        double xStart, xEnd, yStart, yEnd, zStart, zEnd;

        try {
            xStart = x_0.evaluate();
            xEnd = x_1.evaluate();
            yStart = y_0.evaluate();
            yEnd = y_1.evaluate();
            zStart = z_0.evaluate();
            zEnd = z_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D", 5, 6));
        }
        if (yStart >= yEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D", 7, 8));
        }
        if (zStart >= zEnd) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D", 9, 10));
        }

        MarchingCube[][][] implicitGraph = NumericalUtils.solveImplicitEquation3D(expr, varAbsc, varOrd, varAppl, xStart, xEnd, yStart, yEnd, zStart, zEnd);

        // Graphen zeichnen.
        graphicPanelImplicit3D.setParameters(varAbsc, varOrd, varAppl, 150, 200, 30, 30);
        graphicPanelImplicit3D.setExpressions(((Expression[]) command.getParams()[0])[0], ((Expression[]) command.getParams()[0])[1]);
        graphicPanelImplicit3D.drawImplicitGraph3D(implicitGraph, x_0, x_1, y_0, y_1, z_0, z_1);
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
            if (matExpr.isNotMatrix() || dim.width != 1 || dim.height != 2) {
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
            if (matExpr.isNotMatrix() || dim.width != 1 || dim.height != 3) {
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

        String var = (String) command.getParams()[command.getParams().length - 3];

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
        graphicPanelSpherical.drawSphericalGraphs3D(phi_0, phi_1, tau_0, tau_1, exprs);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotsurface)
    private static void executePlotSurface(Command command) throws EvaluationException {

        if (graphicPanelSurface == null || mathToolGraphicArea == null) {
            return;
        }

        MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
        try {
            matExpr = matExpr.simplify(simplifyTypesPlot);
            Dimension dim = matExpr.getDimension();
            if (matExpr.isNotMatrix() || dim.width != 1 || dim.height != 3) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTSURFACE_1_PARAMETER_MUST_BE_3_DIM_VECTOR"));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_PLOTSURFACE_1_PARAMETER_MUST_BE_3_DIM_VECTOR"));
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

        String varS = (String) command.getParams()[1];
        String varT = (String) command.getParams()[2];
        Expression s_0 = ((Expression) command.getParams()[3]).simplify(simplifyTypesPlot);
        Expression s_1 = ((Expression) command.getParams()[4]).simplify(simplifyTypesPlot);
        Expression t_0 = ((Expression) command.getParams()[5]).simplify(simplifyTypesPlot);
        Expression t_1 = ((Expression) command.getParams()[6]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen.
        try {
            t_0.evaluate();
            t_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPHS_CANNOT_BE_PLOTTED"));
        }

        // Kurve zeichnen.
        graphicPanelSurface.setParameters(varS, varT, 150, 200, 30, 30);
        graphicPanelSurface.drawSurface(s_0, s_1, t_0, t_1, components);
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

            ExpressionCollection regressionLineCoefficients = StatisticUtils.getRegressionLineCoefficients(pts);
            Expression regressionLine = SimplifyPolynomialUtils.getPolynomialFromCoefficients(regressionLineCoefficients, "X").simplify();

            doPrintOutput(Translator.translateOutputMessage("MCC_REGRESSIONLINE_MESSAGE"),
                    "Y = ", MathToolUtilities.convertToEditableAbstractExpression(regressionLine));

            /* 
             Graphen der Regressionsgeraden zeichnen, inkl. der Stichproben (als rot markierte Punkte),
             falls keines der Stichproben Parameter enthält.
             */
            for (Matrix point : pts) {
                if (!point.isConstant()) {
                    throw new EvaluationException(Translator.translateOutputMessage("MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE"));
                }
            }

            Expression x_0 = StatisticUtils.getMinimum(pts, 0);
            Expression x_1 = StatisticUtils.getMaximum(pts, 0);
            Expression y_0 = StatisticUtils.getMinimum(pts, 1);
            Expression y_1 = StatisticUtils.getMaximum(pts, 1);

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

        Set<String> vars = new HashSet<>();
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
                Iterator<String> iter = vars.iterator();
                var = iter.next();
            }
        }

        ExpressionCollection zeros = SolveGeneralEquationUtils.solveEquation(f, g, var);

        // Falls die Gleichung keine Lösungen besitzt, User informieren.
        if (zeros == SolveGeneralEquationUtils.NO_SOLUTIONS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_EQUATIONS_HAS_NO_SOLUTIONS"));
            return;
        }

        // Falls keine Lösungen ermittelt werden konnten, User informieren.
        if (zeros.isEmpty() && zeros != SolveGeneralEquationUtils.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND"));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage("MCC_SOLUTIONS_OF_EQUATION"), ((Expression[]) command.getParams()[0])[0],
                " = ", ((Expression[]) command.getParams()[0])[1], " :");
        if (zeros == SolveGeneralEquationUtils.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_ALL_REALS"));
        } else {
            MultiIndexVariable multiVar;
            ArrayList<BigInteger> multiIndex;
            for (int i = 0; i < zeros.getBound(); i++) {
                multiVar = new MultiIndexVariable(Variable.create(var));
                multiIndex = multiVar.getIndices();
                multiIndex.add(BigInteger.valueOf(i + 1));
                doPrintOutput(multiVar, " = ", MathToolUtilities.convertToEditableAbstractExpression(zeros.get(i)));
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
                infoAboutFreeParameters = Translator.translateOutputMessage("MCC_IS_ARBITRARY_INTEGER");
            } else {
                infoAboutFreeParameters = Translator.translateOutputMessage("MCC_ARE_ARBITRARY_INTEGERS");
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

        Set<String> vars = new HashSet<>();
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
                    " = ", ((Expression[]) command.getParams()[0])[1], " :");
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

        ArrayList<Double> zeros = NumericalUtils.solveEquation(equation, var, x_0.evaluate(), x_1.evaluate(), n);

        doPrintOutput(Translator.translateOutputMessage("MCC_SOLUTIONS_OF_EQUATION"), ((Expression[]) command.getParams()[0])[0],
                " = ", ((Expression[]) command.getParams()[0])[1], ":");

        MultiIndexVariable multiVar;
        ArrayList<BigInteger> multiIndex;
        for (int i = 0; i < zeros.size(); i++) {
            // Grafische Ausgabe
            multiVar = new MultiIndexVariable(Variable.create(var));
            multiIndex = multiVar.getIndices();
            multiIndex.add(BigInteger.valueOf(i + 1));
            doPrintOutput(multiVar, " = ", MathToolUtilities.convertToEditableString(zeros.get(i)));
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
            solutions = SolveGeneralDifferentialEquationUtils.solveDifferentialEquation(exprLeft, exprRight, varAbsc, varOrd);
        } finally {
            Variable.setDependingOnVariable(varOrd, null);
        }

        // Falls keine Lösungen ermittelt werden konnten, User informieren.
        if (solutions.isEmpty()) {
            if (solutions == SolveGeneralDifferentialEquationUtils.ALL_FUNCTIONS) {
                doPrintOutput(Translator.translateOutputMessage("MCC_ALL_FUNCTIONS"));
                return;
            } else if (solutions == SolveGeneralDifferentialEquationUtils.NO_SOLUTIONS) {
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
                doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(solutionsImplicit.get(i)), " = ", ZERO);
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
                doPrintOutput(multiVar, " = ", MathToolUtilities.convertToEditableAbstractExpression(solutionsExplicit.get(i)));
            }
        }

        ArrayList<Variable> integrationConstants = SolveGeneralDifferentialEquationUtils.getListOfFreeIntegrationConstants(solutions);
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

        Expression expr = (Expression) command.getParams()[0];
        String varAbsc = (String) command.getParams()[1];
        String varOrd = (String) command.getParams()[2];
        int ord = (int) command.getParams()[3];
        Expression x_0 = (Expression) command.getParams()[4];
        Expression x_1 = (Expression) command.getParams()[5];
        Expression[] y_0 = new Expression[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (Expression) command.getParams()[i + 6];
        }

        // Prüfen, ob x_0 < x_1 ist.
        try {
            if (x_0.evaluate() >= x_1.evaluate()) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVEDIFFEQ", 5, 6));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage("MCC_SOLUTION_OF_DIFFEQ_CANNOT_BE_PLOTTED"));
        }

        double[] startValues = new double[ord];
        for (int i = 0; i < y_0.length; i++) {
            startValues[i] = y_0[i].evaluate();
        }

        try {
            Variable.setDependingOnVariable(varOrd, varAbsc);
            expr = expr.simplify();

            if (doesExpressionContainDerivativesOfTooHighOrder(expr, varOrd, ord - 1)) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ", ord, ord - 1));
            }

            double[][] solutionOfDifferentialEquation = NumericalUtils.solveDifferentialEquationByRungeKutta(expr, varAbsc, varOrd, ord, x_0.evaluate(), x_1.evaluate(), startValues, 1000);

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
                doPrintOutput(varAbsc + " = ", MathToolUtilities.convertToEditableString(solution[0]), "; " + varOrd + " = ", MathToolUtilities.convertToEditableString(solution[1]));
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
    private static void executeSolveSystem(Command command) throws EvaluationException {

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

        // Gleichungssystem bilden.
        Expression[] equations = new Expression[numberOfEquations];
        for (int i = 0; i < numberOfEquations; i++) {
            equations[i] = ((Expression[]) params[i])[0].sub(((Expression[]) params[i])[1]).simplify(simplifyTypesSolveSystem);
        }

        ArrayList<Expression[]> solutions = SolveGeneralSystemOfEquationsUtils.solveSystemOfEquations(equations, solutionVars);

        // Sonderfälle: keine Lösungen, alle reellen Zahlentupel.
        if (solutions == SolveGeneralSystemOfEquationsUtils.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_EQUATION_SYSTEM_HAS_ALL_REAL_TUPLES_AS_SOLUTIONS"));
            return;
        } else if (solutions == SolveGeneralSystemOfEquationsUtils.NO_SOLUTIONS) {
            doPrintOutput(Translator.translateOutputMessage("MCC_EQUATION_SYSTEM_HAS_NO_SOLUTIONS"));
            return;
        } else if (solutions.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage("MCC_EQUATION_SYSTEM_NO_SOLUTIONS_FOUND"));
            return;
        }

        Object[] solutionLine;
        for (Expression[] solution : solutions) {
            solutionLine = new Object[4 * solutionVars.size() - 1];
            for (int i = 0; i < solution.length; i++) {
                solutionLine[4 * i] = solutionVars.get(i);
                solutionLine[4 * i + 1] = " = ";
                solutionLine[4 * i + 2] = MathToolUtilities.convertToEditableAbstractExpression(solution[i]);
                if (i < solution.length - 1) {
                    solutionLine[4 * i + 3] = ", ";
                }
            }
            doPrintOutput(solutionLine);
        }

        /*
         (1) Falls Lösungen Parameter T_0, T_0, ... enthalten, dann zusätzlich
         ausgeben: T_0, T_1, ... sind beliebige freie Veränderliche.
         (2) Falls Lösungen Parameter K_1, K_2, ... enthalten, dann zusätzlich
         ausgeben: K_1, K_2, ... sind beliebige ganzzahlige Veränderliche.
         */
        boolean solutionContainsFreeParameter = false, solutionContainsIntegerParameter = false;
        String infoAboutFreeParameters, infoAboutIntegerParameters;

        for (Expression[] solution : solutions) {
            for (Expression solutionEntry : solution) {
                solutionContainsFreeParameter = solutionContainsFreeParameter || solutionEntry.contains(NotationLoader.FREE_REAL_PARAMETER_VAR + "_0");
                solutionContainsIntegerParameter = solutionContainsIntegerParameter || solutionEntry.contains(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_1");
            }
        }

        if (solutionContainsFreeParameter) {
            boolean solutionContainsFreeParameterOfGivenIndex = true;
            int maxIndex = 0;
            while (solutionContainsFreeParameterOfGivenIndex) {
                maxIndex++;
                solutionContainsFreeParameterOfGivenIndex = false;
                for (Expression[] solution : solutions) {
                    for (Expression solutionEntry : solution) {
                        solutionContainsFreeParameterOfGivenIndex = solutionContainsFreeParameterOfGivenIndex || solutionEntry.contains(NotationLoader.FREE_REAL_PARAMETER_VAR + "_" + maxIndex);
                    }
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

        if (solutionContainsIntegerParameter) {
            boolean solutionContainsIntegerParameterOfGivenIndex = true;
            int maxIndex = 1;
            while (solutionContainsIntegerParameterOfGivenIndex) {
                maxIndex++;
                solutionContainsIntegerParameterOfGivenIndex = false;
                for (Expression[] solution : solutions) {
                    for (Expression solutionEntry : solution) {
                        solutionContainsIntegerParameterOfGivenIndex = solutionContainsIntegerParameterOfGivenIndex || solutionEntry.contains(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_" + maxIndex);
                    }
                }
            }
            maxIndex--;

            ArrayList<MultiIndexVariable> freeParameterVars = new ArrayList<>();
            for (int i = 1; i <= maxIndex; i++) {
                freeParameterVars.add(new MultiIndexVariable(NotationLoader.FREE_INTEGER_PARAMETER_VAR, BigInteger.valueOf(i)));
            }
            if (maxIndex == 1) {
                infoAboutIntegerParameters = Translator.translateOutputMessage("MCC_IS_ARBITRARY_INTEGER");
            } else {
                infoAboutIntegerParameters = Translator.translateOutputMessage("MCC_ARE_ARBITRARY_INTEGERS");
            }

            ArrayList infoAboutIntegerParametersForGraphicArea = new ArrayList();
            for (int i = 0; i < freeParameterVars.size(); i++) {
                infoAboutIntegerParametersForGraphicArea.add(freeParameterVars.get(i));
                if (i < freeParameterVars.size() - 1) {
                    infoAboutIntegerParametersForGraphicArea.add(", ");
                }
            }
            infoAboutIntegerParametersForGraphicArea.add(infoAboutIntegerParameters);
            doPrintOutput(infoAboutIntegerParametersForGraphicArea);

        }

    }

    @Execute(type = TypeCommand.table)
    private static void executeTable(Command command) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        Set<String> vars = new HashSet<>();
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
        String result;
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
                result = "1";
            } else {
                result = "0";
            }

            doPrintOutput(binaryCounter, MathToolUtilities.convertToEditableString(result));

            varsValues = LogicalExpression.binaryCounter(varsValues);

        }

    }

    @Execute(type = TypeCommand.tangent)
    private static void executeTangent(Command command)
            throws EvaluationException {

        Expression f = (Expression) command.getParams()[0];
        HashMap<String, Expression> vars = (HashMap<String, Expression>) command.getParams()[1];

        ArrayList tangentInfoForGraphicArea = new ArrayList();
        tangentInfoForGraphicArea.add(Translator.translateOutputMessage("MCC_EQUATION_OF_TANGENT_SPACE_1"));
        tangentInfoForGraphicArea.add(f);
        tangentInfoForGraphicArea.add(Translator.translateOutputMessage("MCC_EQUATION_OF_TANGENT_SPACE_2"));

        for (String var : vars.keySet()) {
            tangentInfoForGraphicArea.add(var + " = ");
            tangentInfoForGraphicArea.add(vars.get(var));
            tangentInfoForGraphicArea.add(", ");
        }
        // In der textlichen und in der grafischen Ausgabe das letzte (überflüssige) Komma entfernen.
        tangentInfoForGraphicArea.remove(tangentInfoForGraphicArea.size() - 1);
        tangentInfoForGraphicArea.add(":");

        Expression tangent = AnalysisUtils.getTangentSpace(f.simplify(), vars);

        doPrintOutput(tangentInfoForGraphicArea);
        doPrintOutput(NotationLoader.AXIS_VAR + " = ", MathToolUtilities.convertToEditableAbstractExpression(tangent));

        /*
        Prüfung, ob alle Veränderliche, die in der Funktionsvorschrift auftauchen,
        auch in den Punktkoordinaten vorkommen. Wenn nicht, werden keine Graphen
        gezeichnet (da freie Parameter vorkommen).
         */
        Set<String> varsInFunction = f.getContainedIndeterminates();
        for (String var : varsInFunction) {
            if (!vars.keySet().contains(var)) {
                return;
            }
        }

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
                tangentPoint[0][1] = f.replaceVariable(var, vars.get(var)).evaluate();

                ArrayList<Expression> exprs = new ArrayList<>();
                exprs.add(f);
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
                graphicPanel3D.drawGraphs3D(MINUS_ONE.mult(x_0), x_0, MINUS_ONE.mult(y_0), y_0, f, tangent);
            } catch (EvaluationException e) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_GRAPH_NOT_POSSIBLE_TO_DRAW"));
            }

        }

    }

    @Execute(type = TypeCommand.taylordiffeq)
    private static void executeTaylorDiffEq(Command command) throws EvaluationException {

        Expression expr = (Expression) command.getParams()[0];
        String varAbsc = (String) command.getParams()[1];
        String varOrd = (String) command.getParams()[2];
        int ord = (int) command.getParams()[3];
        Expression x_0 = (Expression) command.getParams()[4];

        Expression[] y_0 = new Expression[ord];
        for (int i = 0; i < y_0.length; i++) {
            y_0[i] = (Expression) command.getParams()[i + 5];
        }

        int k = (int) command.getParams()[ord + 5];

        try {
            Variable.setDependingOnVariable(varOrd, varAbsc);
            expr = expr.simplify();

            if (doesExpressionContainDerivativesOfTooHighOrder(expr, varOrd, ord - 1)) {
                throw new EvaluationException(Translator.translateOutputMessage("MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ", ord, ord - 1));
            }

            Expression result = AnalysisUtils.getTaylorPolynomialFromDifferentialEquation(expr, varAbsc, varOrd, ord, x_0, y_0, k);

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
            doPrintOutput(varOrd + "(" + varAbsc + ") = ", MathToolUtilities.convertToEditableAbstractExpression(result));

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
        Set<String> functionNames = new HashSet<>(SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet());

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

    ///////////////////////// Diverse Hilfsmethoden ///////////////////////////////////
    /**
     * Gibt zurück, ob expr Veränderliche enthält, deren Namen mit varOrd
     * beginnen und die mehr als ord Apostrophs enthalten.<br>
     * VORAUSSETZUNG: varOrd darf selbst keine Apostrophs enthalten.
     */
    private static boolean doesExpressionContainDerivativesOfTooHighOrder(Expression expr, String varOrd, int ord) {

        Set<String> vars = expr.getContainedIndeterminates();
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
