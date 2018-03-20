package mathtool.mathcommandcompiler;

import abstractexpressions.expression.basic.ExpressionCollection;
import abstractexpressions.expression.basic.SimplifyPolynomialUtils;
import abstractexpressions.expression.classes.Expression;
import static abstractexpressions.expression.classes.Expression.MINUS_ONE;
import static abstractexpressions.expression.classes.Expression.ONE;
import static abstractexpressions.expression.classes.Expression.TWO;
import static abstractexpressions.expression.classes.Expression.ZERO;
import abstractexpressions.expression.classes.MultiIndexVariable;
import abstractexpressions.expression.classes.SelfDefinedFunction;
import abstractexpressions.expression.classes.Variable;
import abstractexpressions.expression.computation.AnalysisUtils;
import abstractexpressions.expression.computation.NumericalUtils;
import abstractexpressions.expression.computation.StatisticUtils;
import abstractexpressions.expression.differentialequation.SolveGeneralDifferentialEquationUtils;
import abstractexpressions.expression.equation.SolveGeneralEquationUtils;
import abstractexpressions.expression.equation.SolveGeneralSystemOfEquationsUtils;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.logicalexpression.classes.LogicalVariable;
import abstractexpressions.matrixexpression.basic.MatrixExpressionCollection;
import abstractexpressions.matrixexpression.classes.Matrix;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import abstractexpressions.matrixexpression.computation.EigenvaluesEigenvectorsUtils;
import abstractexpressions.matrixexpression.computation.GaussAlgorithmUtils;
import abstractexpressions.output.EditableAbstractExpression;
import abstractexpressions.output.EditableString;
import command.Command;
import command.TypeCommand;
import computationbounds.ComputationBounds;
import enums.TypeSimplify;
import exceptions.CancellationException;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import graphic.common.GraphicArea;
import graphic.swing.GraphicPanel2D;
import graphic.swing.GraphicPanel3D;
import graphic.swing.GraphicPanelCurves2D;
import graphic.swing.GraphicPanelCurves3D;
import graphic.swing.GraphicPanelCylindrical;
import graphic.swing.GraphicPanelImplicit2D;
import graphic.swing.GraphicPanelImplicit3D;
import graphic.swing.GraphicPanelPolar;
import graphic.swing.GraphicPanelSpherical;
import graphic.swing.GraphicPanelSurface;
import graphic.swing.GraphicPanelVectorField2D;
import graphic.swing.GraphicPanelVectorFieldPolar;
import graphic.util.MarchingCube;
import graphic.util.MarchingSquare;
import graphic.util.TypeBracket;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import mathtool.MathToolGUI;
import mathtool.annotations.Execute;
import mathtool.component.components.LegendGUI;
import mathtool.lang.translator.Translator;
import static mathtool.mathcommandcompiler.MathCommandCompiler.getCommand;
import mathtool.utilities.MathToolUtilities;
import notations.NotationLoader;
import util.OperationDataTO;
import util.OperationParsingUtils;

public abstract class MathCommandExecuter {

    // Konstanten für Meldungen / Fehler (Ausführung)
    private static final String MCC_INVALID_COMMAND = "MCC_INVALID_COMMAND";
    private static final String MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF = "MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF";
    private static final String MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF = "MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF";
    private static final String MCC_VALUE_ASSIGNED_TO_VARIABLE_1 = "MCC_VALUE_ASSIGNED_TO_VARIABLE_1";
    private static final String MCC_VALUE_ASSIGNED_TO_VARIABLE_2 = "MCC_VALUE_ASSIGNED_TO_VARIABLE_2";
    private static final String MCC_VALUE_ASSIGNED_TO_VARIABLE_3 = "MCC_VALUE_ASSIGNED_TO_VARIABLE_3";
    private static final String MCC_FUNCTION_WAS_DEFINED = "MCC_FUNCTION_WAS_DEFINED";
    private static final String MCC_LIST_OF_DEFINED_FUNCTIONS = "MCC_LIST_OF_DEFINED_FUNCTIONS";
    private static final String MCC_NO_DEFINED_FUNCTIONS = "MCC_NO_DEFINED_FUNCTIONS";
    private static final String MCC_LIST_OF_VARIABLES = "MCC_LIST_OF_VARIABLES";
    private static final String MCC_NO_DEFINED_VARIABLES = "MCC_NO_DEFINED_VARIABLES";
    private static final String MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVALUES_NOT_QUADRATIC = "MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVALUES_NOT_QUADRATIC";
    private static final String MCC_NO_EIGENVALUES_1 = "MCC_NO_EIGENVALUES_1";
    private static final String MCC_NO_EIGENVALUES_2 = "MCC_NO_EIGENVALUES_2";
    private static final String MCC_EIGENVALUES_OF_MATRIX_1 = "MCC_EIGENVALUES_OF_MATRIX_1";
    private static final String MCC_EIGENVALUES_OF_MATRIX_2 = "MCC_EIGENVALUES_OF_MATRIX_2";
    private static final String MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVECTORS_NOT_QUADRATIC = "MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVECTORS_NOT_QUADRATIC";
    private static final String MCC_EIGENVECTORS_FOR_EIGENVALUE_1 = "MCC_EIGENVECTORS_FOR_EIGENVALUE_1";
    private static final String MCC_EIGENVECTORS_FOR_EIGENVALUE_2 = "MCC_EIGENVECTORS_FOR_EIGENVALUE_2";
    private static final String MCC_EIGENVECTORS_NO_EXPLICIT_EIGENVECTORS = "MCC_EIGENVECTORS_NO_EXPLICIT_EIGENVECTORS";
    private static final String MCC_EIGENVECTORS_NO_EIGENVECTORS = "MCC_EIGENVECTORS_NO_EIGENVECTORS";
    private static final String MCC_DIGITS_OF_E = "MCC_DIGITS_OF_E";
    private static final String MCC_NO_EXTREMA_FOUND = "MCC_NO_EXTREMA_FOUND";
    private static final String MCC_EXTREMA = "MCC_EXTREMA";
    private static final String MCC_LOCAL_MINIMUM_IN = "MCC_LOCAL_MINIMUM_IN";
    private static final String MCC_LOCAL_EXTREMA_FUNCTION_VALUE = "MCC_LOCAL_EXTREMA_FUNCTION_VALUE";
    private static final String MCC_LOCAL_MAXIMUM_IN = "MCC_LOCAL_MAXIMUM_IN";
    private static final String MCC_IS_ARBITRARY_INTEGER = "MCC_IS_ARBITRARY_INTEGER";
    private static final String MCC_ARE_ARBITRARY_INTEGERS = "MCC_ARE_ARBITRARY_INTEGERS";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_EXTREMA = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_EXTREMA";
    private static final String MCC_KER_COULD_NOT_BE_COMPUTED_1 = "MCC_KER_COULD_NOT_BE_COMPUTED_1";
    private static final String MCC_KER_COULD_NOT_BE_COMPUTED_2 = "MCC_KER_COULD_NOT_BE_COMPUTED_2";
    private static final String MCC_TRIVIAL_KER_1 = "MCC_TRIVIAL_KER_1";
    private static final String MCC_TRIVIAL_KER_2 = "MCC_TRIVIAL_KER_2";
    private static final String MCC_TRIVIAL_KER_3 = "MCC_TRIVIAL_KER_3";
    private static final String MCC_BASIS_OF_KER_1 = "MCC_BASIS_OF_KER_1";
    private static final String MCC_BASIS_OF_KER_2 = "MCC_BASIS_OF_KER_2";
    private static final String MCC_LATEX_CODE = "MCC_LATEX_CODE";
    private static final String MCC_PARAMETRIZATION_OF_NORMAL_SPACE_1 = "MCC_PARAMETRIZATION_OF_NORMAL_SPACE_1";
    private static final String MCC_PARAMETRIZATION_OF_NORMAL_SPACE_2 = "MCC_PARAMETRIZATION_OF_NORMAL_SPACE_2";
    private static final String MCC_IS_FREE_VARIABLE_IN_NORMAL = "MCC_IS_FREE_VARIABLE_IN_NORMAL";
    private static final String MCC_DIGITS_OF_PI = "MCC_DIGITS_OF_PI";
    private static final String MCC_OPERATOR_CANNOT_BE_EVALUATED_1 = "MCC_OPERATOR_CANNOT_BE_EVALUATED_1";
    private static final String MCC_OPERATOR_CANNOT_BE_EVALUATED_2 = "MCC_OPERATOR_CANNOT_BE_EVALUATED_2";
    private static final String MCC_GRAPHS_CANNOT_BE_PLOTTED = "MCC_GRAPHS_CANNOT_BE_PLOTTED";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT2D = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT2D";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D";
    private static final String MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR = "MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR";
    private static final String MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_3_DIM_VECTOR = "MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_3_DIM_VECTOR";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTPOLAR = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTPOLAR";
    private static final String MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTPOLAR = "MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTPOLAR";
    private static final String MCC_MIN_RADIUS_MUST_BE_NONNEGATIVE_IN_PLOTCYLINDRICAL = "MCC_MIN_RADIUS_MUST_BE_NONNEGATIVE_IN_PLOTCYLINDRICAL";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTCYLINDRICAL = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTCYLINDRICAL";
    private static final String MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTCYLINDRICAL = "MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTCYLINDRICAL";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTSPHERICAL = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTSPHERICAL";
    private static final String MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTSPHERICAL = "MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTSPHERICAL";
    private static final String MCC_PLOTSURFACE_1_PARAMETER_MUST_BE_3_DIM_VECTOR = "MCC_PLOTSURFACE_1_PARAMETER_MUST_BE_3_DIM_VECTOR";
    private static final String MCC_PLOTVECTORFIELD2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR = "MCC_PLOTVECTORFIELD2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR";
    private static final String MCC_PLOTVECTORFIELDPOLAR_1_PARAMETER_MUST_BE_2_DIM_VECTOR = "MCC_PLOTVECTORFIELDPOLAR_1_PARAMETER_MUST_BE_2_DIM_VECTOR";
    private static final String MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED = "MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED";
    private static final String MCC_MIN_RADIUS_MUST_BE_NONNEGATIVE_IN_PLOTVECTORFIELDPOLAR = "MCC_MIN_RADIUS_MUST_BE_NONNEGATIVE_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTVECTORFIELDPOLAR = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_2_PI_IN_PLOTVECTORFIELDPOLAR = "MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_2_PI_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_REGRESSIONLINE_CANNOT_BE_COMPUTED = "MCC_REGRESSIONLINE_CANNOT_BE_COMPUTED";
    private static final String MCC_REGRESSIONLINE_PARAMETERS_ARE_NOT_POINTS = "MCC_REGRESSIONLINE_PARAMETERS_ARE_NOT_POINTS";
    private static final String MCC_REGRESSIONLINE_MESSAGE = "MCC_REGRESSIONLINE_MESSAGE";
    private static final String MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE = "MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE";
    private static final String MCC_EQUATIONS_HAS_NO_SOLUTIONS = "MCC_EQUATIONS_HAS_NO_SOLUTIONS";
    private static final String MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND = "MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND";
    private static final String MCC_SOLUTIONS_OF_EQUATION = "MCC_SOLUTIONS_OF_EQUATION";
    private static final String MCC_ALL_REALS = "MCC_ALL_REALS";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVE = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVE";
    private static final String MCC_NO_SOLUTIONS_OF_EQUATION_FOUND = "MCC_NO_SOLUTIONS_OF_EQUATION_FOUND";
    private static final String MCC_ALL_FUNCTIONS = "MCC_ALL_FUNCTIONS";
    private static final String MCC_NO_SOLUTIONS = "MCC_NO_SOLUTIONS";
    private static final String MCC_NO_EXACT_SOLUTIONS_OF_DIFFERENTIAL_EQUATION_FOUND = "MCC_NO_EXACT_SOLUTIONS_OF_DIFFERENTIAL_EQUATION_FOUND";
    private static final String MCC_ALGEBRAIC_SOLUTION_OF_DIFFEQ = "MCC_ALGEBRAIC_SOLUTION_OF_DIFFEQ";
    private static final String MCC_IMPLICIT_OF_DIFFEQ = "MCC_IMPLICIT_OF_DIFFEQ";
    private static final String MCC_EXPLICIT_OF_DIFFEQ = "MCC_EXPLICIT_OF_DIFFEQ";
    private static final String MCC_DIFFEQ_IS_FREE_CONSTANT = "MCC_DIFFEQ_IS_FREE_CONSTANT";
    private static final String MCC_DIFFEQ_ARE_FREE_CONSTANTS = "MCC_DIFFEQ_ARE_FREE_CONSTANTS";
    private static final String MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVEDIFFEQ = "MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVEDIFFEQ";
    private static final String MCC_SOLUTION_OF_DIFFEQ_CANNOT_BE_PLOTTED = "MCC_SOLUTION_OF_DIFFEQ_CANNOT_BE_PLOTTED";
    private static final String MCC_SOLUTION_OF_DIFFEQ = "MCC_SOLUTION_OF_DIFFEQ";
    private static final String MCC_SOLUTION_OF_DIFFEQ_NOT_DEFINED_IN_POINT = "MCC_SOLUTION_OF_DIFFEQ_NOT_DEFINED_IN_POINT";
    private static final String MCC_EQUATION_SYSTEM_HAS_ALL_REAL_TUPLES_AS_SOLUTIONS = "MCC_EQUATION_SYSTEM_HAS_ALL_REAL_TUPLES_AS_SOLUTIONS";
    private static final String MCC_EQUATION_SYSTEM_HAS_NO_SOLUTIONS = "MCC_EQUATION_SYSTEM_HAS_NO_SOLUTIONS";
    private static final String MCC_EQUATION_SYSTEM_NO_SOLUTIONS_FOUND = "MCC_EQUATION_SYSTEM_NO_SOLUTIONS_FOUND";
    private static final String MCC_IS_FREE_VARIABLE_IN_SOLVESYSTEM = "MCC_IS_FREE_VARIABLE_IN_SOLVESYSTEM";
    private static final String MCC_ARE_FREE_VARIABLES_IN_SOLVESYSTEM = "MCC_ARE_FREE_VARIABLES_IN_SOLVESYSTEM";
    private static final String MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES = "MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES";
    private static final String MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION = "MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION";
    private static final String MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1 = "MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1";
    private static final String MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2 = "MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2";
    private static final String MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3 = "MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3";
    private static final String MCC_ORDER_OF_VARIABLES_IN_TABLE = "MCC_ORDER_OF_VARIABLES_IN_TABLE";
    private static final String MCC_EQUATION_OF_TANGENT_SPACE_1 = "MCC_EQUATION_OF_TANGENT_SPACE_1";
    private static final String MCC_EQUATION_OF_TANGENT_SPACE_2 = "MCC_EQUATION_OF_TANGENT_SPACE_2";
    private static final String MCC_GRAPH_NOT_POSSIBLE_TO_DRAW = "MCC_GRAPH_NOT_POSSIBLE_TO_DRAW";
    private static final String MCC_TAYLORPOLYNOMIAL_FOR_SOLUTION_OF_DIFFEQ = "MCC_TAYLORPOLYNOMIAL_FOR_SOLUTION_OF_DIFFEQ";
    private static final String MCC_FUNCTION_IS_REMOVED = "MCC_FUNCTION_IS_REMOVED";
    private static final String MCC_VARIABLE_IS_INDETERMINATE_AGAIN = "MCC_VARIABLE_IS_INDETERMINATE_AGAIN";
    private static final String MCC_ALL_FUNCTIONS_ARE_REMOVED = "MCC_ALL_FUNCTIONS_ARE_REMOVED";
    private static final String MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN = "MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN";

    private static final String MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ = "MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ";
    private static final String MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ = "MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ";
   
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
    private static GraphicPanelVectorFieldPolar graphicPanelVectorFieldPolar;

    private static MathToolGUI mathToolGui;
    private static GraphicArea mathToolGraphicArea;
    private static JScrollPane mathToolGraphicScrollPane;
    private static JTextArea mathToolTextArea;

    private static final Set<TypeSimplify> simplifyTypesExpand = new HashSet<>();
    private static final Set<TypeSimplify> simplifyTypesExpandShort = new HashSet<>();
    private static final Set<TypeSimplify> simplifyTypesPlot = new HashSet<>();
    private static final Set<TypeSimplify> simplifyTypesSolveSystem = new HashSet<>();

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

    public static void setGui(MathToolGUI gui) {
        mathToolGui = gui;
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

    public static void setGraphicPanelVectorFieldPolar(GraphicPanelVectorFieldPolar gPVectorFieldPolar) {
        graphicPanelVectorFieldPolar = gPVectorFieldPolar;
    }

    public static void setMathToolGraphicArea(GraphicArea mTGraphicArea, JScrollPane scrollPane) {
        mathToolGraphicArea = mTGraphicArea;
        mathToolGraphicScrollPane = scrollPane;
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
    private static void doPrintOutput(List out) {

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
        mathToolGraphicArea.addComponent(out.toArray());

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
        Method[] methods = MathCommandExecuter.class.getDeclaredMethods();
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
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_INVALID_COMMAND));
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
        if (vars.size() > ComputationBounds.BOUND_COMMAND_MAX_NUMBER_OF_VARS_IN_LOGICAL_EXPRESSION) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CCNF, logExpr, ComputationBounds.BOUND_COMMAND_MAX_NUMBER_OF_VARS_IN_LOGICAL_EXPRESSION));
        }
        LogicalExpression logExprInCCNF = logExpr.toCCNF();
        doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(logExprInCCNF));

    }

    @Execute(type = TypeCommand.cdnf)
    private static void executeCDNF(Command command) throws EvaluationException {

        LogicalExpression logExpr = (LogicalExpression) command.getParams()[0];
        Set<String> vars = new HashSet<>();
        logExpr.addContainedVars(vars);
        if (vars.size() > ComputationBounds.BOUND_COMMAND_MAX_NUMBER_OF_VARS_IN_LOGICAL_EXPRESSION) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LOGICAL_EXPRESSION_CONTAINS_TOO_MANY_VARIABLES_FOR_CDNF, logExpr, ComputationBounds.BOUND_COMMAND_MAX_NUMBER_OF_VARS_IN_LOGICAL_EXPRESSION));
        }
        LogicalExpression logExprInCDNF = logExpr.toCDNF();
        doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(logExprInCDNF));

    }

    @Execute(type = TypeCommand.clear)
    private static void executeClear(Command command) {
        mathToolTextArea.setText("");
        mathToolGraphicArea.initializeBounds(mathToolGui.getMathToolGraphicAreaX(), mathToolGui.getMathToolGraphicAreaY(),
                mathToolGui.getMathToolGraphicAreaWidth(), mathToolGui.getMathToolGraphicAreaHeight());
        mathToolGraphicArea.clearArea();
//        mathToolGui.remove(mathToolGraphicScrollPane);
//        mathToolGui.validate();
//        mathToolGui.repaint();
//
//        mathToolGraphicArea = new GraphicArea(mathToolGui.getMathToolGraphicAreaX(), mathToolGui.getMathToolGraphicAreaY(),
//                mathToolGui.getMathToolGraphicAreaWidth(), mathToolGui.getMathToolGraphicAreaHeight(), mathToolGui);
//
//        mathToolGraphicScrollPane = new JScrollPane(mathToolGraphicArea,
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        mathToolGui.add(mathToolGraphicScrollPane);
//        mathToolGui.validate();
//        mathToolGui.repaint();
    }

    @Execute(type = TypeCommand.def)
    private static void executeDef(Command command) throws EvaluationException {

        // Falls ein Variablenwert definiert wird.
        if (command.getParams().length == 2) {
            String var = (String) command.getParams()[0];
            Expression preciseExpression = ((Expression) command.getParams()[1]).simplifyByInsertingDefinedVars().simplify();
            Variable.setPreciseExpression(var, preciseExpression);
            if (((Expression) command.getParams()[1]).equals(preciseExpression)) {
                doPrintOutput(Translator.translateOutputMessage(MCC_VALUE_ASSIGNED_TO_VARIABLE_1),
                        var,
                        Translator.translateOutputMessage(MCC_VALUE_ASSIGNED_TO_VARIABLE_2),
                        preciseExpression,
                        Translator.translateOutputMessage(MCC_VALUE_ASSIGNED_TO_VARIABLE_3));
            } else {
                doPrintOutput(Translator.translateOutputMessage(MCC_VALUE_ASSIGNED_TO_VARIABLE_1),
                        var,
                        Translator.translateOutputMessage(MCC_VALUE_ASSIGNED_TO_VARIABLE_2),
                        (Expression) command.getParams()[1],
                        " = ",
                        preciseExpression,
                        Translator.translateOutputMessage(MCC_VALUE_ASSIGNED_TO_VARIABLE_3));
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

            doPrintOutput(Translator.translateOutputMessage(MCC_FUNCTION_WAS_DEFINED),
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

            doPrintOutput(Translator.translateOutputMessage(MCC_LIST_OF_DEFINED_FUNCTIONS));
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
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_DEFINED_FUNCTIONS));
        }

    }

    @Execute(type = TypeCommand.defvars)
    public static void executeDefVars(Command command) {
        Set<String> vars = Variable.getVariablesWithPredefinedValues();
        if (!vars.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage(MCC_LIST_OF_VARIABLES));
            for (String var : vars) {
                doPrintOutput(var, " = ", MathToolUtilities.convertToEditableAbstractExpression(Variable.create(var).getPreciseExpression()));
            }
        } else {
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_DEFINED_VARIABLES));
        }
    }

    @Execute(type = TypeCommand.eigenvalues)
    private static void executeEigenvalues(Command command) throws EvaluationException {

        Dimension dim = ((MatrixExpression) command.getParams()[0]).getDimension();
        if (dim.height != dim.width) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVALUES_NOT_QUADRATIC));
        }

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsUtils.getEigenvalues((MatrixExpression) command.getParams()[0]);

        if (eigenvalues.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_EIGENVALUES_1),
                    (MatrixExpression) command.getParams()[0],
                    Translator.translateOutputMessage(MCC_NO_EIGENVALUES_2));
            return;
        }

        // Textliche Ausgabe
        doPrintOutput(Translator.translateOutputMessage(MCC_EIGENVALUES_OF_MATRIX_1),
                (MatrixExpression) command.getParams()[0],
                Translator.translateOutputMessage(MCC_EIGENVALUES_OF_MATRIX_2));

        List eigenvaluesAsList = new ArrayList();

        for (int i = 0; i < eigenvalues.getBound(); i++) {
            eigenvaluesAsList.add(MathToolUtilities.convertToEditableAbstractExpression(eigenvalues.get(i)));
            if (i < eigenvalues.getBound() - 1) {
                eigenvaluesAsList.add(", ");
            }
        }

        doPrintOutput(eigenvaluesAsList);

    }

    @Execute(type = TypeCommand.eigenvectors)
    private static void executeEigenvectors(Command command) throws EvaluationException {

        Dimension dim = ((MatrixExpression) command.getParams()[0]).getDimension();
        if (dim.height != dim.width) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_PARAMETER_IN_EIGENVECTORS_NOT_QUADRATIC));
        }

        ExpressionCollection eigenvalues = EigenvaluesEigenvectorsUtils.getEigenvalues((MatrixExpression) command.getParams()[0]);

        MatrixExpressionCollection eigenvectors;
        MatrixExpression matrix = (MatrixExpression) command.getParams()[0];

        List eigenvectorsAsList;

        for (int i = 0; i < eigenvalues.getBound(); i++) {

            // Sicherheitshalber! Sollte eigentlich nie passieren.
            if (eigenvalues.get(i) == null) {
                continue;
            }
            // Eigenvektoren berechnen.
            eigenvectors = EigenvaluesEigenvectorsUtils.getEigenvectorsForEigenvalue(matrix, eigenvalues.get(i));

            eigenvectorsAsList = new ArrayList();
            eigenvectorsAsList.add(Translator.translateOutputMessage(MCC_EIGENVECTORS_FOR_EIGENVALUE_1));
            eigenvectorsAsList.add(eigenvalues.get(i));
            eigenvectorsAsList.add(Translator.translateOutputMessage(MCC_EIGENVECTORS_FOR_EIGENVALUE_2));
            if (eigenvectors.isEmpty()) {
                eigenvectorsAsList.add(Translator.translateOutputMessage(MCC_EIGENVECTORS_NO_EXPLICIT_EIGENVECTORS));
            } else {
                for (int j = 0; j < eigenvectors.getBound(); j++) {
                    eigenvectorsAsList.add(MathToolUtilities.convertToEditableAbstractExpression(eigenvectors.get(j)));
                    if (j < eigenvectors.getBound() - 1) {
                        eigenvectorsAsList.add(", ");
                    }
                }
            }

            doPrintOutput(eigenvectorsAsList);

        }

        if (eigenvalues.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage(MCC_EIGENVECTORS_NO_EIGENVECTORS));
        }

    }

    @Execute(type = TypeCommand.euler)
    private static void executeEuler(Command command) throws EvaluationException {
        BigDecimal e = AnalysisUtils.getDigitsOfE((int) command.getParams()[0]);
        doPrintOutput(Translator.translateOutputMessage(MCC_DIGITS_OF_E, (int) command.getParams()[0]), MathToolUtilities.convertToEditableString(e));
    }

    @Execute(type = TypeCommand.expand)
    private static void executeExpand(Command command) throws EvaluationException {

        Expression expr = (Expression) command.getParams()[0];

        // Voreingestellte Vereinfachungstypen müssen eingebunden werden, welche expand() nicht widersprechen.
        Set<TypeSimplify> simplifyTypesMathTool = MathToolGUI.getSimplifyTypes();
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
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_EXTREMA_FOUND));
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
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_EXTREMA_FOUND));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage(MCC_EXTREMA), (Expression) command.getParams()[0], ":");

        MultiIndexVariable multiVar;
        List<BigInteger> multiIndex;
        for (int i = 0; i < extremaPoints.getBound(); i++) {
            multiVar = new MultiIndexVariable(Variable.create(var));
            multiIndex = multiVar.getIndices();
            multiIndex.add(BigInteger.valueOf(i + 1));
            if (valuesOfSecondDerivative.get(i).isAlwaysPositive()) {
                doPrintOutput(Translator.translateOutputMessage(MCC_LOCAL_MINIMUM_IN),
                        multiVar, " = ", MathToolUtilities.convertToEditableAbstractExpression(extremaPoints.get(i)),
                        Translator.translateOutputMessage(MCC_LOCAL_EXTREMA_FUNCTION_VALUE),
                        MathToolUtilities.convertToEditableAbstractExpression(extremaValues.get(i)));
            } else {
                doPrintOutput(Translator.translateOutputMessage(MCC_LOCAL_MAXIMUM_IN),
                        multiVar, " = ", MathToolUtilities.convertToEditableAbstractExpression(extremaPoints.get(i)),
                        Translator.translateOutputMessage(MCC_LOCAL_EXTREMA_FUNCTION_VALUE),
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

            List freeParametersInfoArray = new ArrayList();

            for (int i = 1; i <= maxIndex; i++) {
                freeParametersInfoArray.add(new MultiIndexVariable(NotationLoader.FREE_INTEGER_PARAMETER_VAR, BigInteger.valueOf(i)));
                if (i < maxIndex - 1) {
                    freeParametersInfoArray.add(", ");
                }
            }
            if (maxIndex == 1) {
                freeParametersInfoArray.add(Translator.translateOutputMessage(MCC_IS_ARBITRARY_INTEGER));
            } else {
                freeParametersInfoArray.add(Translator.translateOutputMessage(MCC_ARE_ARBITRARY_INTEGERS));
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
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_EXTREMA_FOUND));
            return;
        }

        Expression x_0 = (Expression) command.getParams()[1];
        Expression x_1 = (Expression) command.getParams()[2];

        // Validierung der Zeichenbereichsgrenzen
        double xStart = x_0.evaluate();
        double xEnd = x_1.evaluate();

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_EXTREMA, 2, 3));
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

        List<Double> zeros = NumericalUtils.solveEquation(derivative, var, x_0.evaluate(), x_1.evaluate(), n);
        List<Double> extremaPoints = new ArrayList<>();
        List<Double> valuesOfSecondDerivative = new ArrayList<>();
        List<Double> extremaValues = new ArrayList<>();

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
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_EXTREMA_FOUND));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage(MCC_EXTREMA), (Expression) command.getParams()[0], ":");

        MultiIndexVariable multiVar;
        List<BigInteger> multiIndex;
        for (int i = 0; i < extremaPoints.size(); i++) {

            multiVar = new MultiIndexVariable(Variable.create(var));
            multiIndex = multiVar.getIndices();
            multiIndex.add(BigInteger.valueOf(i + 1));
            if (valuesOfSecondDerivative.get(i) > 0) {
                doPrintOutput(Translator.translateOutputMessage(MCC_LOCAL_MINIMUM_IN),
                        multiVar, " = ", MathToolUtilities.convertToEditableString(extremaPoints.get(i)),
                        Translator.translateOutputMessage(MCC_LOCAL_EXTREMA_FUNCTION_VALUE),
                        MathToolUtilities.convertToEditableString(extremaValues.get(i)));
            } else {
                doPrintOutput(Translator.translateOutputMessage(MCC_LOCAL_MAXIMUM_IN),
                        multiVar, " = ", MathToolUtilities.convertToEditableString(extremaPoints.get(i)),
                        Translator.translateOutputMessage(MCC_LOCAL_EXTREMA_FUNCTION_VALUE),
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
        List<Expression> exprs = new ArrayList<>();
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
            doPrintOutput(Translator.translateOutputMessage(MCC_KER_COULD_NOT_BE_COMPUTED_1),
                    (MatrixExpression) command.getParams()[0],
                    Translator.translateOutputMessage(MCC_KER_COULD_NOT_BE_COMPUTED_2));
            return;
        }

        MatrixExpressionCollection basisOfKer = GaussAlgorithmUtils.computeKernelOfMatrix((Matrix) matExprSimplified);

        if (basisOfKer.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage(MCC_TRIVIAL_KER_1),
                    matExpr,
                    Translator.translateOutputMessage(MCC_TRIVIAL_KER_2),
                    MatrixExpression.getZeroMatrix(((Matrix) matExprSimplified).getRowNumber(), 1),
                    Translator.translateOutputMessage(MCC_TRIVIAL_KER_3));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage(MCC_BASIS_OF_KER_1), matExpr, Translator.translateOutputMessage(MCC_BASIS_OF_KER_2));

        List basisAsArray = new ArrayList();
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

        String latexCode = Translator.translateOutputMessage(MCC_LATEX_CODE);
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
        Map<String, Expression> vars = (Map<String, Expression>) command.getParams()[1];

        List normalInfoForGraphicArea = new ArrayList();
        normalInfoForGraphicArea.add(Translator.translateOutputMessage(MCC_PARAMETRIZATION_OF_NORMAL_SPACE_1));
        normalInfoForGraphicArea.add(f);
        normalInfoForGraphicArea.add(Translator.translateOutputMessage(MCC_PARAMETRIZATION_OF_NORMAL_SPACE_2));

        for (String var : vars.keySet()) {
            normalInfoForGraphicArea.add(var + " = ");
            normalInfoForGraphicArea.add(vars.get(var));
            normalInfoForGraphicArea.add(", ");
        }
        // In der textlichen und in der grafischen Ausgabe das letzte (überflüssige) Komma entfernen.
        normalInfoForGraphicArea.remove(normalInfoForGraphicArea.size() - 1);
        normalInfoForGraphicArea.add(":");

        Map<String, Expression> normalLineParametrization = AnalysisUtils.getNormalLineParametrization(f.simplify(), vars);

        doPrintOutput(normalInfoForGraphicArea);

        for (String var : normalLineParametrization.keySet()) {
            doPrintOutput(var + " = ", MathToolUtilities.convertToEditableAbstractExpression(normalLineParametrization.get(var)));
        }

        doPrintOutput(Translator.translateOutputMessage(MCC_IS_FREE_VARIABLE_IN_NORMAL, NotationLoader.FREE_REAL_PARAMETER_VAR));

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
//        }
    }

    @Execute(type = TypeCommand.pi)
    private static void executePi(Command command) throws EvaluationException {
        BigDecimal pi = AnalysisUtils.getDigitsOfPi((int) command.getParams()[0]);
        doPrintOutput(Translator.translateOutputMessage(MCC_DIGITS_OF_PI, (int) command.getParams()[0]), MathToolUtilities.convertToEditableString(pi));
    }

    @Execute(type = TypeCommand.plot2d)
    private static void executePlot2D(Command command) throws EvaluationException {

        if (graphicPanel2D == null || mathToolGraphicArea == null) {
            return;
        }

        List<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 3; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);

            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        expr, Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT2D, exprs.size() + 1, exprs.size() + 2));
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
            doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                    difference, Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
            // Schließlich noch Fehler werfen.
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D, 4, 5));
        }
        if (yStart >= yEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT2D, 6, 7));
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

        List<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 6; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        exprs.get(i), Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D, exprs.size() + 1, exprs.size() + 2));
        }
        if (yStart >= yEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOT3D, exprs.size() + 3, exprs.size() + 4));
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
            doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                    difference, Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
            // Schließlich noch Fehler werfen.
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        if (xStart >= xEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D, 5, 6));
        }
        if (yStart >= yEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D, 7, 8));
        }
        if (zStart >= zEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTIMPLICIT3D, 9, 10));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR));
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
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        components[i], Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));

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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_3_DIM_VECTOR));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTCURVE2D_1_PARAMETER_MUST_BE_3_DIM_VECTOR));
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
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        components[i], Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));

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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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

        List<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 3; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        expr, Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        Expression phi_0 = ((Expression) command.getParams()[command.getParams().length - 2]).simplify(simplifyTypesPlot);
        Expression phi_1 = ((Expression) command.getParams()[command.getParams().length - 1]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen
        double phiStart, phiEnd;

        try {
            phiStart = phi_0.evaluate();
            phiEnd = phi_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        if (phiStart >= phiEnd) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTPOLAR, exprs.size() + 1, exprs.size() + 2));
        }
        if (phiEnd - phiStart > 20 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTPOLAR, exprs.size() + 1, exprs.size() + 2));
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

        List<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 6; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        expr, Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        if (minR < 0) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_MIN_RADIUS_MUST_BE_NONNEGATIVE_IN_PLOTCYLINDRICAL, exprs.size() + 3));
        }
        if (minR >= maxR) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTCYLINDRICAL, exprs.size() + 3, exprs.size() + 4));
        }
        if (minPhi >= maxPhi) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTCYLINDRICAL, exprs.size() + 5, exprs.size() + 6));
        }
        if (maxPhi - minPhi > 20 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTCYLINDRICAL, exprs.size() + 5, exprs.size() + 6));
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

        List<Expression> exprs = new ArrayList<>();

        Expression expr, exprSimplified;
        for (int i = 0; i < command.getParams().length - 6; i++) {

            expr = (Expression) command.getParams()[i];
            exprSimplified = expr.simplify(simplifyTypesPlot);
            // Falls eines der Graphen nicht gezeichnet werden kann.
            if (exprSimplified.containsOperator()) {
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        expr, Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
            } else {
                exprs.add(exprSimplified);
            }

        }
        if (exprs.isEmpty()) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
        }

        if (minPhi >= maxPhi) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTSPHERICAL, exprs.size() + 3, exprs.size() + 4));
        }
        if (minTau >= maxTau) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTSPHERICAL, exprs.size() + 5, exprs.size() + 6));
        }
        if (maxPhi - minPhi > 20 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTSPHERICAL, exprs.size() + 3, exprs.size() + 4));
        }
        if (maxTau - minTau > 20 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_20_PI_IN_PLOTSPHERICAL, exprs.size() + 5, exprs.size() + 6));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTSURFACE_1_PARAMETER_MUST_BE_3_DIM_VECTOR));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTSURFACE_1_PARAMETER_MUST_BE_3_DIM_VECTOR));
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
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        components[i], Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));

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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPHS_CANNOT_BE_PLOTTED));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELD2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELD2D_1_PARAMETER_MUST_BE_2_DIM_VECTOR));
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
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        components[i], Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED));

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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED));
        }

        // Vektorfeld zeichnen.
        graphicPanelVectorField2D.setVars(varAbsc, varOrd);
        graphicPanelVectorField2D.drawVectorField2D(x_0, x_1, y_0, y_1, components);
        // Alte Legende schließen
        LegendGUI.close();

    }

    @Execute(type = TypeCommand.plotvectorfieldpolar)
    private static void executePlotVectorFieldPolar(Command command) throws EvaluationException {

        if (graphicPanelVectorFieldPolar == null || mathToolGraphicArea == null) {
            return;
        }

        MatrixExpression matExpr = (MatrixExpression) command.getParams()[0];
        try {
            matExpr = matExpr.simplify(simplifyTypesPlot);
            Dimension dim = matExpr.getDimension();
            if (!(matExpr instanceof Matrix) || dim.width != 1 || dim.height != 2) {
                throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELDPOLAR_1_PARAMETER_MUST_BE_2_DIM_VECTOR));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELDPOLAR_1_PARAMETER_MUST_BE_2_DIM_VECTOR));
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
                doPrintOutput(Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_1),
                        components[i], Translator.translateOutputMessage(MCC_OPERATOR_CANNOT_BE_EVALUATED_2));
                // Schließlich noch Fehler werfen.
                throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED));

            }
            components[i] = exprSimplified;

        }

        String varR = (String) command.getParams()[1];
        String varPhi = (String) command.getParams()[2];
        Expression r_0 = ((Expression) command.getParams()[3]).simplify(simplifyTypesPlot);
        Expression r_1 = ((Expression) command.getParams()[4]).simplify(simplifyTypesPlot);
        Expression phi_0 = ((Expression) command.getParams()[5]).simplify(simplifyTypesPlot);
        Expression phi_1 = ((Expression) command.getParams()[6]).simplify(simplifyTypesPlot);

        // Validierung der Zeichenbereichsgrenzen.
        double rMin, rMax, phiMin, phiMax;
        try {
            rMin = r_0.evaluate();
            rMax = r_1.evaluate();
            phiMin = phi_0.evaluate();
            phiMax = phi_1.evaluate();
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_PLOTVECTORFIELD_CANNOT_BE_PLOTTED));
        }
        if (rMin < 0) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_MIN_RADIUS_MUST_BE_NONNEGATIVE_IN_PLOTVECTORFIELDPOLAR, 4));
        }
        if (rMax <= rMin) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_PLOTVECTORFIELDPOLAR, 4, 5));
        }
        if (phiMax - phiMin > 2 * Math.PI) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_DIFFERENCE_OF_ANGLES_MUST_BE_AT_MOST_2_PI_IN_PLOTVECTORFIELDPOLAR, 6, 7));
        }

        // Vektorfeld zeichnen.
        graphicPanelVectorFieldPolar.setVars(varR, varPhi);
        graphicPanelVectorFieldPolar.drawVectorFieldPolar(r_0, r_1, phi_0, phi_1, components);
        // Alte Legende schließen
        LegendGUI.close();

    }
    
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_REGRESSIONLINE_CANNOT_BE_COMPUTED));
            }
            if (!points[i].isMatrix() || dim.width != 1 || dim.height != 2) {
                throw new EvaluationException(Translator.translateOutputMessage(MCC_REGRESSIONLINE_PARAMETERS_ARE_NOT_POINTS));
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

            doPrintOutput(Translator.translateOutputMessage(MCC_REGRESSIONLINE_MESSAGE),
                    "Y = ", MathToolUtilities.convertToEditableAbstractExpression(regressionLine));

            /* 
             Graphen der Regressionsgeraden zeichnen, inkl. der Stichproben (als rot markierte Punkte),
             falls keines der Stichproben Parameter enthält.
             */
            for (Matrix point : pts) {
                if (!point.isConstant()) {
                    throw new EvaluationException(Translator.translateOutputMessage(MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE));
            }
            if (varOrdStart == varOrdEnd) {
                y_0 = y_0.sub(1);
                y_1 = y_1.add(1);
            }

            List<Expression> exprs = new ArrayList<>();
            exprs.add(regressionLine);
            graphicPanel2D.setVars("X", "Y");
            graphicPanel2D.setSpecialPoints(pts);
            graphicPanel2D.drawGraphs2D(x_0, x_1, y_0, y_1, exprs);

        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_REGRESSIONLINE_NOT_POSSIBLE_TO_COMPUTE));
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
            doPrintOutput(Translator.translateOutputMessage(MCC_EQUATIONS_HAS_NO_SOLUTIONS));
            return;
        }

        // Falls keine Lösungen ermittelt werden konnten, User informieren.
        if (zeros.isEmpty() && zeros != SolveGeneralEquationUtils.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_EXACT_SOLUTIONS_OF_EQUATION_FOUND));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage(MCC_SOLUTIONS_OF_EQUATION), ((Expression[]) command.getParams()[0])[0],
                " = ", ((Expression[]) command.getParams()[0])[1], " :");
        if (zeros == SolveGeneralEquationUtils.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage(MCC_ALL_REALS));
        } else {
            MultiIndexVariable multiVar;
            List<BigInteger> multiIndex;
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

            List<MultiIndexVariable> freeParameterVars = new ArrayList<>();
            for (int i = 1; i <= maxIndex; i++) {
                freeParameterVars.add(new MultiIndexVariable(NotationLoader.FREE_INTEGER_PARAMETER_VAR + "_" + i));
            }
            if (maxIndex == 1) {
                infoAboutFreeParameters = Translator.translateOutputMessage(MCC_IS_ARBITRARY_INTEGER);
            } else {
                infoAboutFreeParameters = Translator.translateOutputMessage(MCC_ARE_ARBITRARY_INTEGERS);
            }

            List infoAboutFreeParametersForGraphicArea = new ArrayList();
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVE, 2, 3));
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
            doPrintOutput(Translator.translateOutputMessage(MCC_SOLUTIONS_OF_EQUATION), ((Expression[]) command.getParams()[0])[0],
                    " = ", ((Expression[]) command.getParams()[0])[1], " :");
            if (equation.equals(Expression.ZERO)) {
                doPrintOutput(Translator.translateOutputMessage(MCC_ALL_REALS));
            } else {
                doPrintOutput(Translator.translateOutputMessage(MCC_EQUATIONS_HAS_NO_SOLUTIONS));
            }

            // Graphen der linken und der rechten Seite zeichnen.
            List<Expression> exprs = new ArrayList<>();
            exprs.add(f);
            exprs.add(g);
            graphicPanel2D.setVarAbsc(var);
            graphicPanel2D.drawGraphs2D(x_0, x_1, exprs);
            return;

        }

        List<Double> zeros = NumericalUtils.solveEquation(equation, var, x_0.evaluate(), x_1.evaluate(), n);

        doPrintOutput(Translator.translateOutputMessage(MCC_SOLUTIONS_OF_EQUATION), ((Expression[]) command.getParams()[0])[0],
                " = ", ((Expression[]) command.getParams()[0])[1], ":");

        MultiIndexVariable multiVar;
        List<BigInteger> multiIndex;
        for (int i = 0; i < zeros.size(); i++) {
            // Grafische Ausgabe
            multiVar = new MultiIndexVariable(Variable.create(var));
            multiIndex = multiVar.getIndices();
            multiIndex.add(BigInteger.valueOf(i + 1));
            doPrintOutput(multiVar, " = ", MathToolUtilities.convertToEditableString(zeros.get(i)));
        }

        if (zeros.isEmpty()) {
            // Grafische Ausgabe
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_SOLUTIONS_OF_EQUATION_FOUND));
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
        List<Expression> exprs = new ArrayList<>();
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
                doPrintOutput(Translator.translateOutputMessage(MCC_ALL_FUNCTIONS));
                return;
            } else if (solutions == SolveGeneralDifferentialEquationUtils.NO_SOLUTIONS) {
                doPrintOutput(Translator.translateOutputMessage(MCC_NO_SOLUTIONS));
                return;
            }
            doPrintOutput(Translator.translateOutputMessage(MCC_NO_EXACT_SOLUTIONS_OF_DIFFERENTIAL_EQUATION_FOUND));
            return;
        }

        doPrintOutput(Translator.translateOutputMessage(MCC_ALGEBRAIC_SOLUTION_OF_DIFFEQ), exprLeft, " = ", exprRight, ":");

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
            doPrintOutput(Translator.translateOutputMessage(MCC_IMPLICIT_OF_DIFFEQ));
            for (int i = 0; i < solutionsImplicit.getBound(); i++) {
                doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(solutionsImplicit.get(i)), " = ", ZERO);
            }
        }
        if (!solutionsExplicit.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage(MCC_EXPLICIT_OF_DIFFEQ));
            MultiIndexVariable multiVar;
            List<BigInteger> multiIndex;
            for (int i = 0; i < solutionsExplicit.getBound(); i++) {
                multiVar = new MultiIndexVariable(Variable.create(varOrd));
                multiIndex = multiVar.getIndices();
                multiIndex.add(BigInteger.valueOf(i + 1));
                doPrintOutput(multiVar, " = ", MathToolUtilities.convertToEditableAbstractExpression(solutionsExplicit.get(i)));
            }
        }

        List<Variable> integrationConstants = SolveGeneralDifferentialEquationUtils.getListOfFreeIntegrationConstants(solutions);
        List integrationConstantsInfoMessage = new ArrayList();

        for (int i = 0; i < integrationConstants.size(); i++) {
            integrationConstantsInfoMessage.add(integrationConstants.get(i));
            if (i < integrationConstants.size() - 1) {
                integrationConstantsInfoMessage.add(", ");
            }
        }

        if (integrationConstants.size() == 1) {
            integrationConstantsInfoMessage.add(Translator.translateOutputMessage(MCC_DIFFEQ_IS_FREE_CONSTANT));
        } else if (integrationConstants.size() > 1) {
            integrationConstantsInfoMessage.add(Translator.translateOutputMessage(MCC_DIFFEQ_ARE_FREE_CONSTANTS));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_LIMITS_MUST_BE_WELL_ORDERED_IN_SOLVEDIFFEQ, 5, 6));
            }
        } catch (EvaluationException e) {
            throw new EvaluationException(Translator.translateOutputMessage(MCC_SOLUTION_OF_DIFFEQ_CANNOT_BE_PLOTTED));
        }

        double[] startValues = new double[ord];
        for (int i = 0; i < y_0.length; i++) {
            startValues[i] = y_0[i].evaluate();
        }

        try {
            Variable.setDependingOnVariable(varOrd, varAbsc);
            expr = expr.simplify();

            if (doesExpressionContainDerivativesOfTooHighOrder(expr, varOrd, ord - 1)) {
                throw new EvaluationException(Translator.translateOutputMessage(MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ, ord, ord - 1));
            }

            double[][] solutionOfDifferentialEquation = NumericalUtils.solveDifferentialEquationByRungeKutta(expr, varAbsc, varOrd, ord, x_0.evaluate(), x_1.evaluate(), startValues, 1000);

            // Formulierung und Ausgabe des AWP.
            String formulationOfAWP = Translator.translateOutputMessage(MCC_SOLUTION_OF_DIFFEQ) + varOrd;
            List formulationOfAWPForGraphicArea = new ArrayList();

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
                doPrintOutput(Translator.translateOutputMessage(MCC_SOLUTION_OF_DIFFEQ_NOT_DEFINED_IN_POINT, x_0.evaluate() + (solutionOfDifferentialEquation.length) * (x_1.evaluate() - x_0.evaluate()) / 1000)
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
        List<String> solutionVars = new ArrayList<>();
        for (int i = numberOfEquations; i < params.length; i++) {
            solutionVars.add((String) params[i]);
        }

        // Gleichungssystem bilden.
        Expression[] equations = new Expression[numberOfEquations];
        for (int i = 0; i < numberOfEquations; i++) {
            equations[i] = ((Expression[]) params[i])[0].sub(((Expression[]) params[i])[1]).simplify(simplifyTypesSolveSystem);
        }

        List<Expression[]> solutions = SolveGeneralSystemOfEquationsUtils.solveSystemOfEquations(equations, solutionVars);

        // Sonderfälle: keine Lösungen, alle reellen Zahlentupel.
        if (solutions == SolveGeneralSystemOfEquationsUtils.ALL_REALS) {
            doPrintOutput(Translator.translateOutputMessage(MCC_EQUATION_SYSTEM_HAS_ALL_REAL_TUPLES_AS_SOLUTIONS));
            return;
        } else if (solutions == SolveGeneralSystemOfEquationsUtils.NO_SOLUTIONS) {
            doPrintOutput(Translator.translateOutputMessage(MCC_EQUATION_SYSTEM_HAS_NO_SOLUTIONS));
            return;
        } else if (solutions.isEmpty()) {
            doPrintOutput(Translator.translateOutputMessage(MCC_EQUATION_SYSTEM_NO_SOLUTIONS_FOUND));
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

            List<MultiIndexVariable> freeParameterVars = new ArrayList<>();
            for (int i = 0; i <= maxIndex; i++) {
                freeParameterVars.add(new MultiIndexVariable(NotationLoader.FREE_REAL_PARAMETER_VAR, BigInteger.valueOf(i)));
            }
            if (maxIndex == 0) {
                infoAboutFreeParameters = Translator.translateOutputMessage(MCC_IS_FREE_VARIABLE_IN_SOLVESYSTEM);
            } else {
                infoAboutFreeParameters = Translator.translateOutputMessage(MCC_ARE_FREE_VARIABLES_IN_SOLVESYSTEM);
            }

            List infoAboutFreeParametersForGraphicArea = new ArrayList();
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
                infoAboutIntegerParameters = Translator.translateOutputMessage(MCC_IS_ARBITRARY_INTEGER);
            } else {
                infoAboutIntegerParameters = Translator.translateOutputMessage(MCC_ARE_ARBITRARY_INTEGERS);
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
            throw new EvaluationException(Translator.translateOutputMessage(MCC_LOGICAL_EXPRESSION_CONTAINS_MORE_THAN_20_VARIABLES, logExpr));
        }

        doPrintOutput(Translator.translateOutputMessage(MCC_TABLE_OF_VALUES_FOR_LOGICAL_EXPRESSION), logExpr, ":");

        // Falls es sich um einen konstanten Ausdruck handelt.
        if (numberOfVars == 0) {
            boolean value = logExpr.evaluate();
            if (value) {
                doPrintOutput(Translator.translateOutputMessage(MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1),
                        logExpr,
                        Translator.translateOutputMessage(MCC_LOGICAL_EXPRESSION_IS_CONSTANT_2));
            } else {
                doPrintOutput(Translator.translateOutputMessage(MCC_LOGICAL_EXPRESSION_IS_CONSTANT_1),
                        logExpr,
                        Translator.translateOutputMessage(MCC_LOGICAL_EXPRESSION_IS_CONSTANT_3));
            }
            return;
        }

        // Für die Geschwindigkeit der Tabellenberechnung: logExpr vereinfachen.
        logExpr = logExpr.simplify();

        // Nummerierung der logischen Variablen.
        Map<Integer, String> varsEnumerated = new HashMap<>();

        for (String var : vars) {
            varsEnumerated.put(varsEnumerated.size(), var);
        }

        int tableLength = BigInteger.valueOf(2).pow(numberOfVars).intValue();

        String varsInOrder = Translator.translateOutputMessage(MCC_ORDER_OF_VARIABLES_IN_TABLE);
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
        Map<String, Expression> vars = (Map<String, Expression>) command.getParams()[1];

        ArrayList tangentInfoForGraphicArea = new ArrayList();
        tangentInfoForGraphicArea.add(Translator.translateOutputMessage(MCC_EQUATION_OF_TANGENT_SPACE_1));
        tangentInfoForGraphicArea.add(f);
        tangentInfoForGraphicArea.add(Translator.translateOutputMessage(MCC_EQUATION_OF_TANGENT_SPACE_2));

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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPH_NOT_POSSIBLE_TO_DRAW));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_GRAPH_NOT_POSSIBLE_TO_DRAW));
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
                throw new EvaluationException(Translator.translateOutputMessage(MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ, ord, ord - 1));
            }

            Expression result = AnalysisUtils.getTaylorPolynomialFromDifferentialEquation(expr, varAbsc, varOrd, ord, x_0, y_0, k);

            // Formulierung und Ausgabe des AWP.
            String formulationOfAWP = Translator.translateOutputMessage(MCC_TAYLORPOLYNOMIAL_FOR_SOLUTION_OF_DIFFEQ, k) + varOrd;
            List formulationOfAWPForGraphicArea = new ArrayList();

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
                doPrintOutput(Translator.translateOutputMessage(MCC_FUNCTION_IS_REMOVED, function));
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
                doPrintOutput(Translator.translateOutputMessage(MCC_VARIABLE_IS_INDETERMINATE_AGAIN, var));
            }
        }

    }

    @Execute(type = TypeCommand.undefallfuncs)
    private static void executeUndefAllFuncs(Command command) {

        Map<String, Expression> abstractExpressions = SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions();
        Map<String, Expression[]> innerExpressions = SelfDefinedFunction.getInnerExpressionsForSelfDefinedFunctions();
        Map<String, String[]> arguments = SelfDefinedFunction.getArgumentsForSelfDefinedFunctions();

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
        doPrintOutput(Translator.translateOutputMessage(MCC_ALL_FUNCTIONS_ARE_REMOVED));

    }

    @Execute(type = TypeCommand.undefallvars)
    private static void executeUndefAllVars(Command command) {

        for (String var : Variable.getVariablesWithPredefinedValues()) {
            Variable.setPreciseExpression(var, null);
        }
        doPrintOutput(Translator.translateOutputMessage(MCC_ALL_VARIABLES_ARE_INDETERMINATES_AGAIN));

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
