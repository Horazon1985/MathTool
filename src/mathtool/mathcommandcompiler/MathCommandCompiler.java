package mathtool.mathcommandcompiler;

import command.Command;
import command.TypeCommand;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import abstractexpressions.expression.classes.Expression;
import abstractexpressions.expression.classes.SelfDefinedFunction;
import abstractexpressions.expression.classes.TypeFunction;
import abstractexpressions.expression.classes.TypeOperator;
import abstractexpressions.expression.classes.Variable;
import static abstractexpressions.expression.classes.Expression.VALIDATOR;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import operationparser.OperationParser;
import exceptions.CancellationException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import notations.NotationLoader;
import mathtool.annotations.GetCommand;
import mathtool.lang.translator.Translator;
import util.OperationDataTO;
import util.OperationParsingUtils;

public abstract class MathCommandCompiler {

    // Konstanten für Meldungen / Fehler (Kompilierung)
    private static final String MCC_PARAMETER_IN_APPROX_IS_INVALID = "MCC_PARAMETER_IN_APPROX_IS_INVALID";
    private static final String MCC_INVALID_COMMAND = "MCC_INVALID_COMMAND";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEF = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEF";
    private static final String MCC_NO_EQUAL_IN_DEF = "MCC_NO_EQUAL_IN_DEF";
    private static final String MCC_TO_VARIABLE_MUST_BE_ASSIGNED_REAL_VALUE = "MCC_TO_VARIABLE_MUST_BE_ASSIGNED_REAL_VALUE";
    private static final String MCC_TO_VARIABLE_MUST_BE_ASSIGNED_CONSTANT_REAL_VALUE = "MCC_TO_VARIABLE_MUST_BE_ASSIGNED_CONSTANT_REAL_VALUE";
    private static final String MCC_INVALID_EXPRESSION_ON_RIGHT_SIDE = "MCC_INVALID_EXPRESSION_ON_RIGHT_SIDE";
    private static final String MCC_INVALID_DEF = "MCC_INVALID_DEF";
    private static final String MCC_PROTECTED_FUNC_NAME = "MCC_PROTECTED_FUNC_NAME";
    private static final String MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS = "MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS";
    private static final String MCC_FUNC_NAME_CONTAINS_ONLY_DIGITS = "MCC_FUNC_NAME_CONTAINS_ONLY_DIGITS";
    private static final String MCC_IS_NO_FUNCTION_VARS_IN_FUNCTION_DECLARATION = "MCC_IS_NO_FUNCTION_VARS_IN_FUNCTION_DECLARATION";
    private static final String MCC_IS_NOT_VALID_VARIABLE = "MCC_IS_NOT_VALID_VARIABLE";
    private static final String MCC_VARIABLES_OCCUR_TWICE_IN_DEF = "MCC_VARIABLES_OCCUR_TWICE_IN_DEF";
    private static final String MCC_RIGHT_SIDE_OF_DEF_CONTAINS_WRONG_VAR = "MCC_RIGHT_SIDE_OF_DEF_CONTAINS_WRONG_VAR";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_LATEX = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_LATEX";
    private static final String MCC_GENERAL_PARAMETER_IN_LATEX_EMPTY = "MCC_GENERAL_PARAMETER_IN_LATEX_EMPTY";
    private static final String MCC_WRONG_FORM_OF_PARAMETER_IN_LATEX = "MCC_WRONG_FORM_OF_PARAMETER_IN_LATEX";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_NORMAL = "MCC_NOT_ENOUGH_PARAMETERS_IN_NORMAL";
    private static final String MCC_WRONG_FORM_OF_1_PARAMETER_IN_NORMAL = "MCC_WRONG_FORM_OF_1_PARAMETER_IN_NORMAL";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_NORMAL = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_NORMAL";
    private static final String MCC_NOT_A_VALID_VARIABLE_IN_NORMAL = "MCC_NOT_A_VALID_VARIABLE_IN_NORMAL";
    private static final String MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_NORMAL = "MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_NORMAL";
    private static final String MCC_VARIABLES_OCCUR_TWICE_IN_NORMAL = "MCC_VARIABLES_OCCUR_TWICE_IN_NORMAL";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT2D = "MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT2D";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT2D = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT2D";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT2D = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT2D";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT2D = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT2D";
    private static final String MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_PLOT2D = "MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_PLOT2D";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT2D = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT2D";
    private static final String MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT2D_MUST_BE_A_VALID_EQUATION = "MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT2D_MUST_BE_A_VALID_EQUATION";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT2D = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT2D";
    private static final String MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT2D = "MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT2D";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT2D = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT2D";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT3D = "MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT3D";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT3D = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT3D";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT3D = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT3D";
    private static final String MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOT3D = "MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOT3D";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT3D = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT3D";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT3D = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT3D";
    private static final String MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT3D_MUST_BE_A_VALID_EQUATION = "MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT3D_MUST_BE_A_VALID_EQUATION";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D";
    private static final String MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT3D = "MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT3D";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT3D = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT3D";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT3D = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT3D";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE2D = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE2D";
    private static final String MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE2D = "MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE2D";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE2D = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE2D";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE2D = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE2D";
    private static final String MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D = "MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE3D = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE3D";
    private static final String MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE3D = "MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE3D";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE3D = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE3D";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE3D = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE3D";
    private static final String MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D = "MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTCYLINDRICAL = "MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTCYLINDRICAL";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTCYLINDRICAL = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTCYLINDRICAL";
    private static final String MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTCYLINDRICAL = "MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTCYLINDRICAL";
    private static final String MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTCYLINDRICAL = "MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTCYLINDRICAL";
    private static final String MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTCYLINDRICAL = "MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTCYLINDRICAL";
    private static final String MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTCYLINDRICAL = "MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTCYLINDRICAL";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCYLINDRICAL = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCYLINDRICAL";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTSPHERICAL = "MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTSPHERICAL";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTSPHERICAL = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTSPHERICAL";
    private static final String MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSPHERICAL = "MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSPHERICAL";
    private static final String MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSPHERICAL = "MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSPHERICAL";
    private static final String MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSPHERICAL = "MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSPHERICAL";
    private static final String MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSPHERICAL = "MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSPHERICAL";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSPHERICAL = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSPHERICAL";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTSURFACE = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTSURFACE";
    private static final String MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOTSURFACE = "MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOTSURFACE";
    private static final String MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSURFACE = "MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSURFACE";
    private static final String MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSURFACE = "MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSURFACE";
    private static final String MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSURFACE = "MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSURFACE";
    private static final String MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSURFACE = "MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSURFACE";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSURFACE = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSURFACE";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELD2D = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELD2D";
    private static final String MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELD2D = "MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELD2D";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELD2D = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELD2D";
    private static final String MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELD2D = "MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELD2D";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELD2D = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELD2D";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD2D = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD2D";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELDPOLAR = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELDPOLAR = "MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELDPOLAR = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELDPOLAR = "MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELDPOLAR = "MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELDPOLAR = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELDPOLAR";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTPOLAR = "MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTPOLAR";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTPOLAR = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTPOLAR";
    private static final String MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTPOLAR = "MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTPOLAR";
    private static final String MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTPOLAR = "MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTPOLAR";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR";
    private static final String MCC_WRONG_NUMBER_OF_PARAMETERS_IN_SOLVEDIFFEQ = "MCC_WRONG_NUMBER_OF_PARAMETERS_IN_SOLVEDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ = "MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ";
    private static final String MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_SOLVEDIFFEQ = "MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_SOLVEDIFFEQ";
    private static final String MCC_1_PARAMETER_IN_SOLVEDIFFEQ_MUST_CONTAIN_EQUALITY_SIGN = "MCC_1_PARAMETER_IN_SOLVEDIFFEQ_MUST_CONTAIN_EQUALITY_SIGN";
    private static final String MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDIFFEQ = "MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDIFFEQ";
    private static final String MCC_NO_DERIVATIVE_IN_1_PARAMETER_IN_SOLVEDIFFEQ = "MCC_NO_DERIVATIVE_IN_1_PARAMETER_IN_SOLVEDIFFEQ";
    private static final String MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ = "MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVEDIFFEQ = "MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVEDIFFEQ";
    private static final String MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDIFFEQ = "MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDIFFEQ";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDIFFEQ = "MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDIFFEQ";
    private static final String MCC_TOO_MANY_PARAMETERS_IN_SOLVEDIFFEQ = "MCC_TOO_MANY_PARAMETERS_IN_SOLVEDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDIFFEQ = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDIFFEQ";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_TANGENT = "MCC_NOT_ENOUGH_PARAMETERS_IN_TANGENT";
    private static final String MCC_WRONG_FORM_OF_1_PARAMETER_IN_TANGENT = "MCC_WRONG_FORM_OF_1_PARAMETER_IN_TANGENT";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT";
    private static final String MCC_NOT_A_VALID_VARIABLE_IN_TANGENT = "MCC_NOT_A_VALID_VARIABLE_IN_TANGENT";
    private static final String MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_TANGENT = "MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_TANGENT";
    private static final String MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT = "MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ = "MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_4_PARAMETER_IN_TAYLORDIFFEQ = "MCC_WRONG_FORM_OF_4_PARAMETER_IN_TAYLORDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_TAYLORDIFFEQ = "MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_TAYLORDIFFEQ";
    private static final String MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_TAYLORDIFFEQ = "MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_TAYLORDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_1_PARAMETER_IN_TAYLORDIFFEQ = "MCC_WRONG_FORM_OF_1_PARAMETER_IN_TAYLORDIFFEQ";
    private static final String MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ = "MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ";
    private static final String MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDIFFEQ = "MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDIFFEQ";
    private static final String MCC_TOO_MANY_PARAMETERS_IN_TAYLORDIFFEQ = "MCC_TOO_MANY_PARAMETERS_IN_TAYLORDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ = "MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ";
    private static final String MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_TAYLORDIFFEQ = "MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_TAYLORDIFFEQ";
    private static final String MCC_NOT_ENOUGH_PARAMETERS_IN_UNDEFFUNCS = "MCC_NOT_ENOUGH_PARAMETERS_IN_UNDEFFUNCS";
    private static final String MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEFFUNCS = "MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEFFUNCS";
    
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
                        throw new ExpressionException(Translator.translateOutputMessage(MCC_PARAMETER_IN_APPROX_IS_INVALID));
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
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_INVALID_COMMAND));
                }
            }
        }

        // Sollte theoretisch nie vorkommen.
        throw new ExpressionException(Translator.translateOutputMessage(MCC_INVALID_COMMAND));

    }

    @GetCommand(type = TypeCommand.def)
    private static Command getCommandDef(String[] params) throws ExpressionException {

        // Struktur: def(VAR = VALUE) oder def(FUNCTION(VAR_1, ..., VAR_n) = EXPRESSION(VAR_1, ..., VAR_n))
        if (params.length != 1) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_DEF));
        }

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NO_EQUAL_IN_DEF));
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
                throw new ExpressionException(Translator.translateOutputMessage(MCC_TO_VARIABLE_MUST_BE_ASSIGNED_REAL_VALUE));
            }
            if (!vars.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_TO_VARIABLE_MUST_BE_ASSIGNED_CONSTANT_REAL_VALUE));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INVALID_EXPRESSION_ON_RIGHT_SIDE) + e.getMessage());
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INVALID_DEF));
        }

        // Prüfen, ob nicht geschützte Funktionen (wie z.B. sin, tan etc.) überschrieben werden.
        if (!isNotForbiddenName(functionName)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_PROTECTED_FUNC_NAME, functionName));
        }

        // Prüfen, ob keine Sonderzeichen vorkommen.
        if (!containsNoSpecialCharacters(functionName)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_FUNC_NAME_CONTAINS_SPECIAL_CHARS, functionName));
        }

        // Prüfen, ob der Funktionsname nicht nur aus Ziffern besteht.
        if (isNameIntegerNumber(functionName)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_FUNC_NAME_CONTAINS_ONLY_DIGITS, functionName));
        }

        /*
         Falls functionsVars leer ist -> Fehler ausgeben (es muss
         mindestens eine Variable vorhanden sein).
         */
        if (functionVars.length == 0) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_IS_NO_FUNCTION_VARS_IN_FUNCTION_DECLARATION));
        }

        // Wird geprüft, ob die einzelnen Parameter in der Funktionsklammer gültige Variablen sind.
        for (String functionVar : functionVars) {
            if (!VALIDATOR.isValidIdentifier(functionVar) || Variable.getVariablesWithPredefinedValues().contains(functionVar)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_IS_NOT_VALID_VARIABLE, functionVar));
            }
        }

        // Wird geprüft, ob die Variablen in function_vars auch alle verschieden sind!
        List<String> functionVarsAsList = new ArrayList<>();
        for (String functionVar : functionVars) {
            if (functionVarsAsList.contains(functionVar)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLES_OCCUR_TWICE_IN_DEF, functionName));
            }
            functionVarsAsList.add(functionVar);
        }

        /*
         Hier wird den Variablen ein bestimmtes Format gegeben. Dies
         dient der Kennzeichnung, dass diese Variablen Platzhalter für
         weitere Ausdrücke und keine echten Variablen sind. Solche
         Variablen können niemals in einem geparsten Ausdruck vorkommen,
         da der Standardvalidator in der Methode Expression.build() solche 
         Variablen nicht akzeptiert.
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
        for (String var : vars) {
            expr = expr.replaceVariable(var, Variable.create(NotationLoader.SELFDEFINEDFUNCTION_VAR + "_" + (functionVarsAsList.indexOf(var) + 1)));
            if (!functionVarsAsList.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_RIGHT_SIDE_OF_DEF_CONTAINS_WRONG_VAR));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_LATEX));
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
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_GENERAL_PARAMETER_IN_LATEX_EMPTY));
                }
                exprs[i] = Expression.build(expressions.substring(0, expressions.indexOf("=")));
                expressions = expressions.substring(expressions.indexOf("=") + 1, expressions.length());
            }
            if (expressions.length() == 0) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_GENERAL_PARAMETER_IN_LATEX_EMPTY));
            }
            exprs[n] = Expression.build(expressions);
            return new Command(TypeCommand.latex, exprs);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_PARAMETER_IN_LATEX) + e.getMessage());
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_NORMAL));
        }

        Set<String> vars;
        Expression expr;
        try {
            expr = Expression.build(params[0]);
            vars = expr.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_1_PARAMETER_IN_NORMAL) + e.getMessage());
        }

        /*
         Ermittelt die Anzahl der Variablen, von denen die Funktion
         abhängt, von der der Tangentialraum berechnet werden soll.
         */
        for (int i = 1; i < params.length; i++) {
            if (!params[i].contains("=")) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_NORMAL, i + 1));
            }
            if (!VALIDATOR.isValidIdentifier(params[i].substring(0, params[i].indexOf("=")))) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_A_VALID_VARIABLE_IN_NORMAL, params[i].substring(0, params[i].indexOf("="))));
            }
            try {
                Expression point = Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet<>());
                if (!point.isConstant()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_NORMAL, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_NORMAL, i + 1));
            }
        }

        // Es wird geprüft, ob keine Veränderlichen doppelt auftreten.
        for (int i = 1; i < params.length; i++) {
            for (int j = i + 1; j < params.length; j++) {
                if (params[i].substring(0, params[i].indexOf("=")).equals(params[j].substring(0, params[j].indexOf("=")))) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLES_OCCUR_TWICE_IN_NORMAL, params[i].substring(0, params[i].indexOf("="))));
                }
            }
        }

        /*
         Einzelne Punktkoordinaten werden in der Map
         varsContainedInParams gespeichert.
         */
        Map<String, Expression> varsContainedInParams = new HashMap<>();
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT2D));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 3; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT2D, i + 1));
            }
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 3])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT2D, params.length - 2));
        }

        commandParams[params.length - 3] = params[params.length - 3];
        vars.remove(params[params.length - 3]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT2D, params[params.length - 3]));
        }

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[params.length - 2] = Expression.build(params[params.length - 2]);
            ((Expression) commandParams[params.length - 2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_PLOT2D, params.length - 1));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_PLOT2D, params.length - 1));
        }

        try {
            commandParams[params.length - 1] = Expression.build(params[params.length - 1]);
            ((Expression) commandParams[params.length - 1]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_PLOT2D, params.length));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_PLOT2D, params.length));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT2D));
        }

        Object[] commandParams = new Object[7];
        Set<String> vars = new HashSet<>();

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT2D_MUST_BE_A_VALID_EQUATION));
        }

        Expression left, right;
        try {
            left = Expression.build(params[0].substring(0, params[0].indexOf("=")));
            right = Expression.build(params[0].substring(params[0].indexOf("=") + 1));
            left.addContainedIndeterminates(vars);
            right.addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT2D_MUST_BE_A_VALID_EQUATION));
        }

        commandParams[0] = new Expression[]{left, right};

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT2D, 1));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT2D, 2));
        }
        if (params[1].equals(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT2D));
        }

        commandParams[1] = params[1];
        commandParams[2] = params[2];
        vars.remove(params[1]);
        vars.remove(params[2]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT2D, params[1], params[2]));
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < 7; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT2D, i + 1));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_PLOT3D));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOT3D, i + 1, e.getMessage()));
            }
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 6])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT3D, params.length - 6));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 5])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOT3D, params.length - 5));
        }
        if (params[params.length - 6].equals(params[params.length - 5])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOT3D));
        }

        commandParams[params.length - 6] = params[params.length - 6];
        commandParams[params.length - 5] = params[params.length - 5];
        vars.remove(params[params.length - 6]);
        vars.remove(params[params.length - 5]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOT3D, params[params.length - 6], params[params.length - 5]));
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOT3D, i + 1));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTIMPLICIT3D));
        }

        Object[] commandParams = new Object[10];
        Set<String> vars = new HashSet<>();

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT3D_MUST_BE_A_VALID_EQUATION));
        }

        Expression left, right;
        try {
            left = Expression.build(params[0].substring(0, params[0].indexOf("=")));
            right = Expression.build(params[0].substring(params[0].indexOf("=") + 1));
            left.addContainedIndeterminates(vars);
            right.addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_FIRST_PARAMETER_IN_PLOTIMPLICIT3D_MUST_BE_A_VALID_EQUATION));
        }

        commandParams[0] = new Expression[]{left, right};

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D, 1));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D, 2));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[3])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTIMPLICIT3D, 3));
        }
        if (params[1].equals(params[2]) || params[1].equals(params[3]) || params[2].equals(params[3])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTIMPLICIT3D));
        }

        commandParams[1] = params[1];
        commandParams[2] = params[2];
        commandParams[3] = params[3];
        vars.remove(params[1]);
        vars.remove(params[2]);
        vars.remove(params[3]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTIMPLICIT3D, params[1], params[2], params[3]));
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = 4; i < 10; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT3D, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTIMPLICIT3D, i + 1));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE2D));
        }

        Object[] commandParams = new Object[4];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE2D, e.getMessage()));
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE2D));
        }

        commandParams[1] = params[1];
        vars.remove(params[1]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE2D, params[1]));
        }

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[2] = Expression.build(params[2]);
            ((Expression) commandParams[2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D, 3));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D, 3));
        }

        try {
            commandParams[3] = Expression.build(params[3]);
            ((Expression) commandParams[3]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D, 4));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE2D, 4));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTCURVE3D));
        }

        Object[] commandParams = new Object[4];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTCURVE3D, e.getMessage()));
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTCURVE3D));
        }

        commandParams[1] = params[1];
        vars.remove(params[1]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTCURVE3D, params[1]));
        }

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[2] = Expression.build(params[2]);
            ((Expression) commandParams[2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D, 3));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D, 3));
        }

        try {
            commandParams[3] = Expression.build(params[3]);
            ((Expression) commandParams[3]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D, 4));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETERS_IN_PLOTCURVE3D, 4));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTCYLINDRICAL));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTCYLINDRICAL, i + 1, e.getMessage()));
            }
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTCYLINDRICAL, vars.size()));
        }

        Set<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!VALIDATOR.isValidIdentifier(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTCYLINDRICAL, i + 1));
            }
            if (varsInParams.contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTCYLINDRICAL));
            }
            varsInParams.add(params[i]);
            commandParams[i] = params[i];
        }

        // Prüfen, ob Veränderliche, die in vars auftreten, auch in varsInParams auftreten.
        for (String var : vars) {
            if (!varsInParams.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTCYLINDRICAL, var));
            }
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCYLINDRICAL, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTCYLINDRICAL, i + 1));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTSPHERICAL));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 6; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTSPHERICAL, i + 1, e.getMessage()));
            }
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSPHERICAL, vars.size()));
        }

        Set<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!VALIDATOR.isValidIdentifier(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSPHERICAL, i + 1));
            }
            if (varsInParams.contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSPHERICAL));
            }
            varsInParams.add(params[i]);
            commandParams[i] = params[i];
        }

        // Prüfen, ob Veränderliche, die in vars auftreten, auch in varsInParams auftreten.
        for (String var : vars) {
            if (!varsInParams.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSPHERICAL, var));
            }
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSPHERICAL, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSPHERICAL, i + 1));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTSURFACE));
        }

        Object[] commandParams = new Object[7];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_1_PARAMETER_IN_PLOTSURFACE, 1, e.getMessage()));
        }

        if (vars.size() > 2) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTSURFACE, vars.size()));
        }

        Set<String> varsInParams = new HashSet<>();
        for (int i = params.length - 6; i < params.length - 4; i++) {
            if (!VALIDATOR.isValidIdentifier(params[i]) || Variable.getVariablesWithPredefinedValues().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VARIABLE_PARAMETER_IN_PLOTSURFACE, i + 1));
            }
            if (varsInParams.contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTSURFACE));
            }
            varsInParams.add(params[i]);
            commandParams[i] = params[i];
        }

        // Prüfen, ob Veränderliche, die in vars auftreten, auch in varsInParams auftreten.
        for (String var : vars) {
            if (!varsInParams.contains(var)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLE_NOT_ALLOWED_TO_OCCUR_IN_FUNCTION_IN_PLOTSURFACE, var));
            }
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = params.length - 4; i < params.length; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSURFACE, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTSURFACE, i + 1));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELD2D));
        }

        Object[] commandParams = new Object[7];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELD2D, e.getMessage()));
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELD2D, 2));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELD2D, 3));
        }
        if (params[1].equals(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELD2D));
        }

        commandParams[1] = params[1];
        commandParams[2] = params[2];
        vars.remove(params[1]);
        vars.remove(params[2]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELD2D, params[1], params[2]));
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < 7; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD2D, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELD2D, i + 1));
            }
        }

        return new Command(TypeCommand.plotvectorfield2d, commandParams);

    }

    @GetCommand(type = TypeCommand.plotvectorfieldpolar)
    private static Command getCommandPlotVectorFieldPolar(String[] params) throws ExpressionException {

        /*
         Struktur: plotvectorfieldpolar(f(r, t), r, t, r_0, r_1, t_0, t_1), f(r, t): 
         Matrizenusdruck in zwei Variablen. r_0 < r_1, t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length != 7) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_PLOTVECTORFIELDPOLAR));
        }

        Object[] commandParams = new Object[7];
        Set<String> vars = new HashSet<>();

        try {
            commandParams[0] = MatrixExpression.build(params[0]);
            ((MatrixExpression) commandParams[0]).addContainedIndeterminates(vars);
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_FIRST_PARAMETER_IN_PLOTVECTORFIELDPOLAR, e.getMessage()));
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELDPOLAR, 2));
        }
        if (!Expression.isValidDerivativeOfIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTVECTORFIELDPOLAR, 3));
        }
        if (params[1].equals(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_PLOTVECTORFIELDPOLAR));
        }

        commandParams[1] = params[1];
        commandParams[2] = params[2];
        vars.remove(params[1]);
        vars.remove(params[2]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_INDETERMINATES_IN_PLOTVECTORFIELDPOLAR, params[1], params[2]));
        }

        Set<String> varsInLimits = new HashSet<>();
        for (int i = 3; i < 7; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(varsInLimits);
                if (!varsInLimits.isEmpty()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELDPOLAR, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTVECTORFIELDPOLAR, i + 1));
            }
        }

        return new Command(TypeCommand.plotvectorfieldpolar, commandParams);

    }

    @GetCommand(type = TypeCommand.plotpolar)
    private static Command getCommandPlotPolar(String[] params) throws ExpressionException {

        /*
         Struktur: plotpolar(f_1(t), ..., f_n(t), t, t_0, t_1). f_i(t): Ausdruck in 
         einer Variablen t. t_0 < t_1: Grenzen des Zeichenbereichs.
         */
        if (params.length < 4) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_PLOTPOLAR));
        }

        Object[] commandParams = new Object[params.length];
        Set<String> vars = new HashSet<>();

        for (int i = 0; i < params.length - 3; i++) {
            try {
                commandParams[i] = Expression.build(params[i]);
                ((Expression) commandParams[i]).addContainedIndeterminates(vars);
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_PLOTPOLAR, i + 1));
            }
        }

        if (!Expression.isValidDerivativeOfIndeterminate(params[params.length - 3])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_INDETERMINATE_PARAMETER_IN_PLOTPOLAR, params.length - 2));
        }

        commandParams[params.length - 3] = params[params.length - 3];
        vars.remove(params[params.length - 3]);

        if (!vars.isEmpty()) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_VARIABLES_IN_PLOTPOLAR, vars.size()));
        }

        Set<String> varsInLimits = new HashSet<>();
        try {
            commandParams[params.length - 2] = Expression.build(params[params.length - 2]);
            ((Expression) commandParams[params.length - 2]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR, params.length - 1));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR, params.length - 1));
        }

        try {
            commandParams[params.length - 1] = Expression.build(params[params.length - 1]);
            ((Expression) commandParams[params.length - 1]).addContainedIndeterminates(varsInLimits);
            if (!varsInLimits.isEmpty()) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR, params.length));
            }
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_PLOTPOLAR, params.length));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_SOLVEDIFFEQ));
        }

        String varAbsc = params[1], varOrd = params[2];
        // Beide Variablen dürfen KEINE Apostrophs enthalten. Diese sind hier für Ableitungen resereviert.
        if (!Expression.isValidIndeterminate(varAbsc)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ, 2));
        }
        if (!Expression.isValidIndeterminate(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ, 3));
        }
        if (varAbsc.equals(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_SOLVEDIFFEQ));
        }

        if (!params[0].contains("=")) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_1_PARAMETER_IN_SOLVEDIFFEQ_MUST_CONTAIN_EQUALITY_SIGN));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDIFFEQ) + e.getMessage());
        }

        int ord = 0;
        for (String var : vars) {
            if (var.startsWith(varOrd) && var.contains("'")) {
                ord = var.length() - var.replaceAll("'", "").length();
            }
        }

        if (ord == 0) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NO_DERIVATIVE_IN_1_PARAMETER_IN_SOLVEDIFFEQ));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_NUMBER_OF_PARAMETERS_IN_SOLVEDIFFEQ));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[3]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVEDIFFEQ));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_4_PARAMETER_IN_SOLVEDIFFEQ));
        }

        if (!Expression.isValidIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ, 2));
        }
        if (!Expression.isValidIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_SOLVEDIFFEQ, 3));
        }

        String varAbsc = params[1];
        String varOrd = params[2];
        if (varAbsc.equals(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_SOLVEDIFFEQ));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_1_PARAMETER_IN_SOLVEDIFFEQ) + e.getMessage());
        }

        String varWithoutPrimes;
        for (String var : vars) {
            varWithoutPrimes = var.replaceAll("'", "");
            if (varWithoutPrimes.equals(varOrd)) {
                if (var.length() - varWithoutPrimes.length() >= ord) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_SOLVEDIFFEQ, ord, ord - 1));
                }
            }
            if (!varWithoutPrimes.equals(var) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDIFFEQ));
            }
            if (!varWithoutPrimes.equals(varAbsc) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_TWO_VARIABLES_ARE_ALLOWED_IN_SOLVEDIFFEQ));
            }
        }

        if (params.length < ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_SOLVEDIFFEQ));
        }
        if (params.length > ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_TOO_MANY_PARAMETERS_IN_SOLVEDIFFEQ));
        }

        // Prüft, ob die AWP-Daten korrekt sind.
        for (int i = 4; i < ord + 6; i++) {
            try {
                Expression limit = Expression.build(params[i]);
                if (!limit.isConstant()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDIFFEQ, i + 1));
                }
                /*
                 Prüfen, ob die Grenzen ausgewerten werden können.
                 Dies ist notwendig, da es sich hier um eine
                 numerische Berechnung handelt (und nicht um eine
                 algebraische).
                 */
                limit.evaluate();
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_SOLVEDIFFEQ, i + 1));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_TANGENT));
        }

        Set<String> vars;
        Expression expr;
        try {
            expr = Expression.build(params[0]);
            vars = expr.getContainedIndeterminates();
        } catch (ExpressionException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_1_PARAMETER_IN_TANGENT) + e.getMessage());
        }

        /*
         Ermittelt die Anzahl der Variablen, von denen die Funktion
         abhängt, von der der Tangentialraum berechnet werden soll.
         */
        for (int i = 1; i < params.length; i++) {
            if (!params[i].contains("=")) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_TANGENT, i + 1));
            }
            if (!VALIDATOR.isValidIdentifier(params[i].substring(0, params[i].indexOf("=")))) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_A_VALID_VARIABLE_IN_TANGENT, params[i].substring(0, params[i].indexOf("="))));
            }
            try {
                Expression point = Expression.build(params[i].substring(params[i].indexOf("=") + 1, params[i].length()), new HashSet<String>());
                if (!point.isConstant()) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_TANGENT, i + 1));
                }
            } catch (ExpressionException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_POINT_PARAMETER_IN_TANGENT, i + 1));
            }
        }

        // Es wird geprüft, ob keine Veränderlichen doppelt auftreten.
        for (int i = 1; i < params.length; i++) {
            for (int j = i + 1; j < params.length; j++) {
                if (params[i].substring(0, params[i].indexOf("=")).equals(params[j].substring(0, params[j].indexOf("=")))) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_VARIABLES_OCCUR_TWICE_IN_TANGENT, params[i].substring(0, params[i].indexOf("="))));
                }
            }
        }

        /*
         Einzelne Punktkoordinaten werden in der Map
         varsContainedInParams gespeichert.
         */
        Map<String, Expression> varsContainedInParams = new HashMap<>();
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ));
        }

        // Ordnung der DGL ermitteln.
        int ord;
        try {
            ord = Integer.parseInt(params[3]);
            if (ord < 1) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_4_PARAMETER_IN_TAYLORDIFFEQ));
            }
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_4_PARAMETER_IN_TAYLORDIFFEQ));
        }

        if (!Expression.isValidIndeterminate(params[1])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_TAYLORDIFFEQ, 2));
        }
        if (!Expression.isValidIndeterminate(params[2])) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_VAR_PARAMETER_IN_TAYLORDIFFEQ, 3));
        }

        String varAbsc = params[1];
        String varOrd = params[2];
        if (varAbsc.equals(varOrd)) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_INDETERMINATES_MUST_BE_PAIRWISE_DIFFERENT_IN_TAYLORDIFFEQ));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_1_PARAMETER_IN_TAYLORDIFFEQ) + e.getMessage());
        }

        String varWithoutPrimes;
        for (String var : vars) {
            varWithoutPrimes = var.replaceAll("'", "");
            if (varWithoutPrimes.equals(varOrd)) {
                if (var.length() - varWithoutPrimes.length() >= ord) {
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_DERIVATIVE_ORDER_OCCUR_IN_TAYLORDIFFEQ, ord, ord - 1));
                }
            }
            if (!varWithoutPrimes.equals(var) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDIFFEQ));
            }
            if (!varWithoutPrimes.equals(varAbsc) && !varWithoutPrimes.equals(varOrd)) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_TWO_VARIABLES_ARE_ALLOWED_IN_TAYLORDIFFEQ));
            }
        }

        if (params.length < ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_TAYLORDIFFEQ));
        }

        if (params.length > ord + 6) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_TOO_MANY_PARAMETERS_IN_TAYLORDIFFEQ));
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
                    throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ, i + 1));
                }
            } catch (ExpressionException | EvaluationException e) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LIMIT_PARAMETER_IN_TAYLORDIFFEQ, i + 1));
            }
        }

        int ordOfTaylorPolynomial;
        try {
            ordOfTaylorPolynomial = Integer.parseInt(params[ord + 5]);
        } catch (NumberFormatException e) {
            throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_LAST_PARAMETER_IN_TAYLORDIFFEQ));
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
            throw new ExpressionException(Translator.translateOutputMessage(MCC_NOT_ENOUGH_PARAMETERS_IN_UNDEFFUNCS));
        }

        Object[] commandParams = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            if (!SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet().contains(params[i])) {
                throw new ExpressionException(Translator.translateOutputMessage(MCC_WRONG_FORM_OF_GENERAL_PARAMETER_IN_UNDEFFUNCS, i + 1));
            }
            commandParams[i] = params[i];
        }

        return new Command(TypeCommand.undeffuncs, commandParams);

    }

}
