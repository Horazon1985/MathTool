package mathtool;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.expression.classes.Operator;
import abstractexpressions.expression.classes.SelfDefinedFunction;
import abstractexpressions.expression.classes.TypeOperator;
import abstractexpressions.expression.classes.Variable;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.logicalexpression.classes.TypeLogicalBinary;
import abstractexpressions.logicalexpression.classes.TypeLogicalUnary;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import abstractexpressions.matrixexpression.classes.MatrixOperator;
import abstractexpressions.matrixexpression.classes.TypeMatrixOperator;
import command.Command;
import command.TypeCommand;
import enums.TypeGraphic;
import enums.TypeLanguage;
import enums.TypeSimplify;
import exceptions.ExpressionException;
import graphic.common.GraphicArea;
import graphic.swing.GraphicPanel3D;
import graphic.swing.GraphicPanelCurves3D;
import graphic.swing.GraphicPanelImplicit3D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import mathtool.component.components.ComputingDialogGUI;
import mathtool.component.components.ErrorDialogGUI;
import mathtool.config.ConfigLoader;
import mathtool.config.MathToolPropertiesHandler;
import mathtool.config.classes.MathToolConfig;
import mathtool.enums.TypeMode;
import mathtool.lang.translator.Translator;
import mathtool.mathcommandcompiler.MathCommandCompiler;
import mathtool.mathcommandcompiler.MathCommandExecuter;
import mathtool.session.SessionLoader;
import mathtool.session.classes.DefinedFunction;
import mathtool.session.classes.DefinedFunctions;
import mathtool.session.classes.DefinedVar;
import mathtool.session.classes.DefinedVars;
import mathtool.session.classes.MathToolSession;
import mathtool.utilities.MathToolLogger;
import notations.NotationLoader;
import operationparser.ParseResultPattern;
import util.OperationDataTO;
import util.OperationParsingUtils;

public class MathToolController {

    private static final String GUI_OPERATOR = "GUI_OPERATOR";
    private static final String GUI_COMMAND = "GUI_COMMAND";
    private static final String GUI_ROTATE_GRAPH = "GUI_ROTATE_GRAPH";

    private static final String GUI_TIMEOUT = "GUI_TIMEOUT";
    
    private static final String NEXT_LINE = System.lineSeparator();
    
    private final static ImageIcon computingOwlEyesOpen = new ImageIcon(MathToolController.class.getResource("component/components/icons/LogoOwlEyesOpen.png"));
    private final static ImageIcon computingOwlEyesHalfOpen = new ImageIcon(MathToolController.class.getResource("component/components/icons/LogoOwlEyesHalfOpen.png"));
    private final static ImageIcon computingOwlEyesClosed = new ImageIcon(MathToolController.class.getResource("component/components/icons/LogoOwlEyesClosed.png"));

    private static MathToolGUI gui;

    private static JTextArea mathToolTextArea;

    private static GraphicArea mathToolGraphicArea;
    
    private static MathToolLogger log;
    
    public static void setGui(MathToolGUI mathToolGui) {
        gui = mathToolGui;
    }
    
    public static void setMathToolTextArea(JTextArea area) {
        mathToolTextArea = area;
    }
    
    public static void setMathToolGraphicArea(GraphicArea area) {
        mathToolGraphicArea = area;
    }
    
    public static MathToolLogger getLogger() {
        return log;
    }
    
    public static void setLogger(MathToolLogger logger) {
        log = logger;
    }
   
    public static MathToolConfig loadConfig() {
        return ConfigLoader.loadConfig();
    }
    
    /**
     * Prüfung, ob alle benötigten Ressources im ExpressionBuilder vorhanden
     * sind. Falls Ressourcen fehlen, wird ein entsprechender Dialog angezeigt.
     */
    public static void checkExpressionBuilderResources() {
        Collection<String> resourcesExpressionBuilder = algorithmexecuter.lang.translator.Translator.getResources();
        checkResources(resourcesExpressionBuilder);
    }

    /**
     * Prüfung, ob alle benötigten Ressources im ExpressionBuilder vorhanden
     * sind. Falls Ressourcen fehlen, wird ein entsprechender Dialog angezeigt.
     */
    public static void checkMathToolResources() {
        Collection<String> resourcesMathTool = Translator.getResources();
        checkResources(resourcesMathTool);
    }

    private static void checkResources(Collection<String> resources) {
        Set<String> missingResources = new HashSet<>();
        URL langFile = null;
        for (String res : resources) {
            langFile = ClassLoader.getSystemResource(res);
            if (langFile == null) {
                missingResources.add(res);
            }
        }
        if (!missingResources.isEmpty()) {
            ErrorDialogGUI errorDialog = ErrorDialogGUI.createResourceNotFoundDialog(missingResources);
            errorDialog.setVisible(true);
        }
    }

    public static void initSimplifyTypes() {
        Set<TypeSimplify> simplifyTypes = MathToolGUI.getSimplifyTypes();
        simplifyTypes.add(TypeSimplify.order_difference_and_division);
        simplifyTypes.add(TypeSimplify.order_sums_and_products);
        simplifyTypes.add(TypeSimplify.simplify_basic);
        simplifyTypes.add(TypeSimplify.simplify_by_inserting_defined_vars);
        simplifyTypes.add(TypeSimplify.simplify_pull_apart_powers);
        simplifyTypes.add(TypeSimplify.simplify_collect_products);
        simplifyTypes.add(TypeSimplify.simplify_expand_rational_factors);
        simplifyTypes.add(TypeSimplify.simplify_factorize);
        simplifyTypes.add(TypeSimplify.simplify_bring_expression_to_common_denominator);
        simplifyTypes.add(TypeSimplify.simplify_reduce_quotients);
        simplifyTypes.add(TypeSimplify.simplify_reduce_differences_and_quotients_advanced);
        simplifyTypes.add(TypeSimplify.simplify_algebraic_expressions);
        simplifyTypes.add(TypeSimplify.simplify_expand_and_collect_equivalents_if_shorter);
        simplifyTypes.add(TypeSimplify.simplify_functional_relations);
        simplifyTypes.add(TypeSimplify.simplify_collect_logarithms);
        // Für Matrizen
        simplifyTypes.add(TypeSimplify.simplify_matrix_entries);
        simplifyTypes.add(TypeSimplify.simplify_compute_matrix_operations);
    }

    /**
     * Setzt die Einträge in den Operator-Dropdown.
     */
    public static void fillOperatorChoice(JComboBox operatorChoice) {
        List<String> operators = new ArrayList<>();
        // Operatoren
        for (TypeOperator value : TypeOperator.values()) {
            operators.add(value.getOperatorName());
        }
        // Matrizenoperatoren
        for (TypeMatrixOperator value : TypeMatrixOperator.values()) {
            if (!operators.contains(value.getOperatorName())) {
                operators.add(value.getOperatorName());
            }
        }
        Collections.sort(operators);
        operatorChoice.removeAllItems();
        operatorChoice.addItem(Translator.translateOutputMessage(GUI_OPERATOR));
        for (String op : operators) {
            operatorChoice.addItem(op);
        }
    }

    /**
     * Setzt die Einträge in den Befehl-Dropdown.
     */
    public static void fillCommandChoice(JComboBox commandChoice) {
        List<String> commands = new ArrayList<>();
        for (TypeCommand value : TypeCommand.values()) {
            commands.add(value.toString());
        }
        commandChoice.removeAllItems();
        commandChoice.addItem(Translator.translateOutputMessage(GUI_COMMAND));
        for (String c : commands) {
            commandChoice.addItem(c);
        }
    }

    public static void loadSettingsFromXML() {
        MathToolConfig config = MathToolController.loadConfig();
        MathToolGUI.setFontSizeGraphic(config.getFontSizeGraphic());
        MathToolGUI.setFontSizeText(config.getFontSizeText());
        MathToolGUI.setLanguage(config.getLanguage());
        MathToolGUI.setMode(config.getMode());
        MathToolGUI.setMinimumDimension(new Dimension(config.getScreenWidth(), config.getScreenHeight()));
        MathToolGUI.setTimeoutComputation(config.getTimeoutComputation());
        MathToolGUI.setTimeoutAlgorithm(config.getTimeoutAlgorithm());

        // Setzen von: Algebraische Relationen
        if (config.getOptionAlgebraicRelations()) {
            MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_algebraic_expressions);
        } else {
            MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_algebraic_expressions);
        }
        // Setzen von: Funktionale Relationen
        if (config.getOptionFunctionalRelations()) {
            MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_functional_relations);
        } else {
            MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_functional_relations);
        }
        // Setzen von: Länge verkürzen
        if (config.getOptionExpandAndCollectIfShorter()) {
            MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_expand_and_collect_equivalents_if_shorter);
        } else {
            MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_and_collect_equivalents_if_shorter);
        }
        // Setzen von: FaktorisierungsDropDown
        switch (config.getOptionFactorizeDropDown()) {
            case factorize:
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_factorize);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_powerful);
                break;
            case expand:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_factorize);
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_expand_powerful);
                break;
            case no_options:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_factorize);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_powerful);
                break;
            default:
                break;
        }
        // Setzen von: LogarithmenDropDown
        switch (config.getOptionLogarithmsDropDown()) {
            case collect:
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_collect_logarithms);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_logarithms);
                break;
            case expand:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_collect_logarithms);
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_expand_logarithms);
                break;
            case no_options:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_collect_logarithms);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_logarithms);
                break;
            default:
                break;
        }
        
    }
    
    public static void loadSettingsFromProperties() {

//        MathToolPropertiesHandler.readMathToolProperties();
        // Konfigurationen setzen.
        MathToolGUI.setFontSizeGraphic(MathToolPropertiesHandler.getFontSizeGraphic());
        MathToolGUI.setFontSizeText(MathToolPropertiesHandler.getFontSizeText());
        MathToolGUI.setLanguage(MathToolPropertiesHandler.getLanguage());
        MathToolGUI.setMode(MathToolPropertiesHandler.getMode());
        MathToolGUI.setMinimumDimension(new Dimension(MathToolPropertiesHandler.getScreenWidth(), MathToolPropertiesHandler.getScreenHeight()));

        // Setzen von: Algebraische Relationen
        if (MathToolPropertiesHandler.getAlgebraicRelations()) {
            MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_algebraic_expressions);
        } else {
            MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_algebraic_expressions);
        }
        // Setzen von: Funktionale Relationen
        if (MathToolPropertiesHandler.getFunctionalRelations()) {
            MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_functional_relations);
        } else {
            MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_functional_relations);
        }
        // Setzen von: Länge verkürzen
        if (MathToolPropertiesHandler.getExpandAndCollectIfShorter()) {
            MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_expand_and_collect_equivalents_if_shorter);
        } else {
            MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_and_collect_equivalents_if_shorter);
        }
        // Setzen von: FaktorisierungsDropDown
        switch (MathToolPropertiesHandler.getFactorizeDropDown()) {
            case factorize:
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_factorize);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_powerful);
                break;
            case expand:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_factorize);
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_expand_powerful);
                break;
            case no_options:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_factorize);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_powerful);
                break;
            default:
                break;
        }
        // Setzen von: LogarithmenDropDown
        switch (MathToolPropertiesHandler.getLogarithmsDropDown()) {
            case collect:
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_collect_logarithms);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_logarithms);
                break;
            case expand:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_collect_logarithms);
                MathToolGUI.getSimplifyTypes().add(TypeSimplify.simplify_expand_logarithms);
                break;
            case no_options:
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_collect_logarithms);
                MathToolGUI.getSimplifyTypes().remove(TypeSimplify.simplify_expand_logarithms);
                break;
            default:
                break;
        }

    }

    public static MathToolLogger initLogger() {
        return MathToolLogger.initLogger();
    }
    
    public static void loadSession(String path) {
        try {
            MathToolSession session = SessionLoader.loadSession(path);
            DefinedVars definedVars = session.getDefinedVars();
            DefinedFunctions definedFunctions = session.getDefinedFunctions();

            if (definedVars.getDefinedVarList() != null) {
                // Alte Variablen löschen.
                for (String var : Variable.getVariablesWithPredefinedValues()) {
                    Variable.setPreciseExpression(var, null);
                }
                // Variablen laden
                for (DefinedVar var : definedVars.getDefinedVarList()) {
                    try {
                        Variable.create(var.getVarname(), Expression.build(var.getValue()));
                    } catch (Exception e) {
                        // Nichts tun, weitere Variablen einlesen.
                    }
                }
            }
            if (definedFunctions.getDefinedFunctionList() != null) {
                // Alte Funktionen löschen.
                for (String f : SelfDefinedFunction.getAbstractExpressionsForSelfDefinedFunctions().keySet()) {
                    SelfDefinedFunction.removeSelfDefinedFunction(f);
                }
                // Variablen laden
                SelfDefinedFunction f;
                List<String> arguments;
                Expression abstractExpression;
                Set<String> vars;
                for (DefinedFunction function : definedFunctions.getDefinedFunctionList()) {
                    try {
                        arguments = function.getArguments().getArguments();
                        abstractExpression = Expression.build(function.getFunctionterm());
                        vars = abstractExpression.getContainedIndeterminates();
                        /*
                         Wenn f als Argumente die Variablen U_1, U_2, ... enthält,
                         dann enthält f.abstractExpression die entsprechenden Variablen
                         u_1, u_2, ... . Für eine korrekte Zuordnung müssen diese 
                         wieder zu Großbuchstaben gemacht werden.
                         */
                        for (String var : vars) {
                            if (var.substring(0, 1).equals(NotationLoader.SELFDEFINEDFUNCTION_VAR.toLowerCase())) {
                                abstractExpression = abstractExpression.replaceVariable(var, Variable.create(var.toUpperCase()));
                            }
                        }
                        f = new SelfDefinedFunction(function.getFunctionname(),
                                arguments.toArray(new String[arguments.size()]),
                                abstractExpression,
                                null);
                        SelfDefinedFunction.createSelfDefinedFunction(f);
                    } catch (Exception e) {
                        // Nichts tun, weitere Funktionen einlesen.
                    }
                }
            }
            // Schließlich: geladene Inhalte (Variablen, Funktionen) ausgeben.
            MathCommandExecuter.executeDefFuncs(new Command(TypeCommand.deffuncs, new Object[]{}));
            MathCommandExecuter.executeDefVars(new Command(TypeCommand.defvars, new Object[]{}));
        } catch (Exception e) {
            // Es wird nichts geladen.
        }

    }

    /**
     * Gibt den i-ten geloggten Befehl zurück.
     */
    public static void showLoggedCommand(JTextField inputField, int i) {
        if (!MathToolGUI.getCommandList().isEmpty() && MathToolGUI.getCommandList().get(i) != null) {
            inputField.setText(MathToolGUI.getCommandList().get(i));
        }
    }

    /**
     * Prüft, ob die Eingabe gültig ist. Bei gültiger (kompilierbarer) Eingabe
     * wird der Text schwary gefärbt, bei ungültiger Eingabe rot.
     */
    public static void checkInputValidity(JTextField inputField) {

        // ToolTipText im Vorfeld entfernen (falls die Validierung doch korrekt ist).
        inputField.setToolTipText("");
        String s = inputField.getText().replaceAll(" ", "").toLowerCase();

        if (inputField.getText().equals("")) {
            inputField.setForeground(Color.black);
            return;
        }

        boolean validCommand = false;
        try {
            OperationDataTO commandData = OperationParsingUtils.getOperationData(s);
            String commandName = commandData.getOperationName();
            for (TypeCommand commandType : TypeCommand.values()) {
                validCommand = validCommand || commandName.equals(commandType.toString());
                if (validCommand) {
                    break;
                }
            }
            String[] params = commandData.getOperationArguments();
            MathCommandCompiler.getCommand(commandName, params);
        } catch (ExpressionException eCommand) {

            // Wenn der Befehlname gültig ist, aber der Befehl ungültig ist: entsprechende Fehlermeldung anzeigen.
            if (validCommand) {
                inputField.setForeground(Color.red);
                inputField.setToolTipText(eCommand.getMessage());
                return;
            }

            try {
                Expression.build(s);
            } catch (ExpressionException eExpr) {
                try {
                    LogicalExpression.build(s);
                } catch (ExpressionException eLogExpr) {
                    try {
                        MatrixExpression.build(s);
                    } catch (ExpressionException eMatExpr) {
                        inputField.setForeground(Color.red);
                        inputField.setToolTipText(eMatExpr.getMessage());
                        return;
                    }
                }
            }
        }

        inputField.setForeground(Color.black);

    }

    /**
     * Berechnet für Operatoren die Mindestanzahl der benötigten Kommata bei
     * einer gültigen Eingabe.
     */
    public static int getNumberOfCommasForOperators(String operatorName) {

        Field[] fields = Operator.class.getDeclaredFields();
        String value;
        int numberOfCommas = -1;

        // Operatoren
        for (Field field : fields) {

            if (!field.getType().equals(String.class) || !Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                value = (String) field.get(null);
                if (value.indexOf(operatorName) == 0) {
                    ParseResultPattern pattern = operationparser.OperationParser.getResultPattern(value);
                    if (numberOfCommas < 0) {
                        numberOfCommas = pattern.size() - 1;
                    } else {
                        numberOfCommas = Math.min(numberOfCommas, pattern.size() - 1);
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | ExpressionException e) {
            }

        }

        // Matrizenoperatoren
        fields = MatrixOperator.class.getDeclaredFields();
        for (Field field : fields) {

            if (!field.getType().equals(String.class) || !Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                value = (String) field.get(null);
                if (value.indexOf(operatorName) == 0) {
                    ParseResultPattern pattern = operationparser.OperationParser.getResultPattern(value);
                    if (numberOfCommas < 0) {
                        numberOfCommas = pattern.size() - 1;
                    } else {
                        numberOfCommas = Math.min(numberOfCommas, pattern.size() - 1);
                    }
                }
            } catch (Exception e) {
            }

        }

        return Math.max(numberOfCommas, 0);

    }

    /**
     * Berechnet für Befehle die Mindestanzahl der benötigten Kommata bei einer
     * gültigen Eingabe.
     */
    public static int getNumberOfCommasForCommands(String commandName) {

        // Sonderfälle
        if (commandName.equals(TypeCommand.normal.name())) {
            return 1;
        }
        if (commandName.equals(TypeCommand.plot2d.name())) {
            return 2;
        }
        if (commandName.equals(TypeCommand.plot3d.name())) {
            return 4;
        }
        if (commandName.equals(TypeCommand.plotimplicit2d.name())) {
            return 6;
        }
        if (commandName.equals(TypeCommand.plotimplicit3d.name())) {
            return 9;
        }
        if (commandName.equals(TypeCommand.plotcurve2d.name()) || commandName.equals(TypeCommand.plotcurve3d.name())) {
            return 2;
        }
        if (commandName.equals(TypeCommand.plotpolar.name())) {
            return 2;
        }
        if (commandName.equals(TypeCommand.plotcylindrical.name()) || commandName.equals(TypeCommand.plotspherical.name())) {
            return 6;
        }
        if (commandName.equals(TypeCommand.plotsurface.name())) {
            return 6;
        }
        if (commandName.equals(TypeCommand.plotvectorfield2d.name())) {
            return 6;
        }
        if (commandName.equals(TypeCommand.solvediffeq.name())) {
            return 2;
        }
        if (commandName.equals(TypeCommand.tangent.name())) {
            return 1;
        }
        if (commandName.equals(TypeCommand.taylordiffeq.name())) {
            return 6;
        }

        Field[] fields = MathCommandCompiler.class.getDeclaredFields();
        String value;
        int numberOfCommas = -1;

        // Operatoren
        for (Field field : fields) {

            if (!field.getType().equals(String.class) || !Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                value = (String) field.get(null);
                if (value.indexOf(commandName) == 0) {
                    ParseResultPattern pattern = operationparser.OperationParser.getResultPattern(value);
                    if (numberOfCommas < 0) {
                        numberOfCommas = pattern.size() - 1;
                    } else {
                        numberOfCommas = Math.min(numberOfCommas, pattern.size() - 1);
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException | ExpressionException e) {
            }

        }

        return Math.max(numberOfCommas, 0);

    }

    /**
     * Richtet alle Grafikpanels an der Stelle (x, y) aus und setzt ihre Breite
     * / Höhe gemäß width / height.
     */
    public static void locateGraphicPanels(JPanel[] panels, int x, int y, int width, int height) {
        for (JPanel panel : panels) {
            panel.setBounds(x, y, width, height);
        }
    }

    /**
     * Ermittelt den Typ des GraphicPanels, welcher zum Befehl c gehört.
     */
    public static TypeGraphic getTypeGraphicFromCommand(Command c) {
        if (c.getTypeCommand().equals(TypeCommand.extrema) && c.getParams().length >= 3) {
            return TypeGraphic.GRAPH2D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plot2d)) {
            return TypeGraphic.GRAPH2D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotimplicit2d)) {
            return TypeGraphic.GRAPHIMPLICIT2D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotimplicit3d)) {
            return TypeGraphic.GRAPHIMPLICIT3D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plot3d) || c.getTypeCommand().equals(TypeCommand.tangent) && ((HashMap) c.getParams()[1]).size() == 2) {
            return TypeGraphic.GRAPH3D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotcurve2d)) {
            return TypeGraphic.GRAPHCURVE2D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotcurve3d)) {
            return TypeGraphic.GRAPHCURVE3D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotpolar)) {
            return TypeGraphic.GRAPHPOLAR;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotcylindrical)) {
            return TypeGraphic.GRAPHCYLINDRCAL;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotspherical)) {
            return TypeGraphic.GRAPHSPHERICAL;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotsurface)) {
            return TypeGraphic.GRAPHSURFACE;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotvectorfield2d)) {
            return TypeGraphic.VECTORFIELD2D;
        }
        if (c.getTypeCommand().equals(TypeCommand.plotvectorfieldpolar)) {
            return TypeGraphic.VECTORFIELDPOLAR;
        }
        if (c.getTypeCommand().equals(TypeCommand.regressionline) && c.getParams().length >= 2) {
            return TypeGraphic.GRAPH2D;
        }
        if (c.getTypeCommand().equals(TypeCommand.solve) && c.getParams().length >= 3 || c.getTypeCommand().equals(TypeCommand.tangent) && ((HashMap) c.getParams()[1]).size() == 1) {
            return TypeGraphic.GRAPH2D;
        }
        if (c.getTypeCommand().equals(TypeCommand.solvediffeq) && c.getParams().length >= 6) {
            return TypeGraphic.GRAPH2D;
        }
        return TypeGraphic.NONE;
    }

    /**
     * Setzt alle Grafikpanels auf sichtbar / unsichtbar (gemäß visible).
     */
    public static void setGraphicPanelsVisible(JPanel[] panels, boolean visible) {
        for (JPanel panel : panels) {
            panel.setVisible(visible);
        }
    }

    /**
     * Setzt die Maße der beiden JScrollPanes auf (x, y, width, height) und die
     * Maße der restlichen Komponenten werden dementsprechend angepasst.
     */
    public static void resizeConsole(JScrollPane scrollPaneText, JScrollPane scrollPaneGraphic, int x, int y, int width, int height,
            JTextArea mathToolTextArea, GraphicArea mathToolGraphicArea, JTextField inputField,
            JButton inputButton) {

        // Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
        scrollPaneText.setBounds(x, y, width, height);
        scrollPaneGraphic.setBounds(x, y, width, height);
        mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
        mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
        gui.setMathToolGraphicAreaX(10);
        gui.setMathToolGraphicAreaY(10);
        gui.setMathToolGraphicAreaWidth(scrollPaneGraphic.getWidth());
        gui.setMathToolGraphicAreaHeight(scrollPaneGraphic.getHeight());
        inputField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
        inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, inputButton.getWidth(), inputButton.getHeight());

    }

    /**
     * Richtet alle Komponenten in components an der Stelle (x, y) aus und
     * verschieben die nachfolgenden Komponenten (äquidistant) um jeweils delta.
     */
    public static void locateButtonsAndDropDowns(JComponent[] components, int x, int y, int width, int height, int delta) {
        for (int i = 0; i < components.length; i++) {
            components[i].setBounds(x + i * (width + delta), y, width, height);
        }
    }

    public static void initTimer(Timer computingTimer, final ComputingDialogGUI computingDialog) {

        // Es folgen die TimerTasks, welche die Eule veranlassen, mit den Augen zu zwinkern.
        TimerTask start = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.setVisible(true);
                }
            }
        };
        TimerTask openEyes = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesOpen);
                }
            }
        };
        TimerTask halfOpenEyes = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask closedEyes = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesClosed);
                }
            }
        };
        TimerTask halfOpenEyesAgain = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask openEyesAgain = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesOpen);
                }
            }
        };
        TimerTask halfOpenEyes2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask closedEyes2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesClosed);
                }
            }
        };
        TimerTask halfOpenEyesAgain2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesHalfOpen);
                }
            }
        };
        TimerTask openEyesAgain2 = new TimerTask() {
            @Override
            public void run() {
                if (computingDialog != null) {
                    computingDialog.changeIcon(computingOwlEyesOpen);
                }
            }
        };

        computingTimer.schedule(start, 1000);
        computingTimer.schedule(openEyes, 0, 2000);
        computingTimer.schedule(halfOpenEyes, 100, 2000);
        computingTimer.schedule(closedEyes, 200, 2000);
        computingTimer.schedule(halfOpenEyesAgain, 300, 2000);
        computingTimer.schedule(openEyesAgain, 400, 2000);
        computingTimer.schedule(halfOpenEyes2, 500, 2000);
        computingTimer.schedule(closedEyes2, 600, 2000);
        computingTimer.schedule(halfOpenEyesAgain2, 700, 2000);
        computingTimer.schedule(openEyesAgain2, 800, 2000);

    }

    /**
     * Rotation bei 3D-Graphen stoppen.
     */
    public static void stopRotationOfGraph(GraphicPanel3D graphicPanel3D, GraphicPanelImplicit3D graphicPanelImplicit3D, GraphicPanelCurves3D graphicPanelCurves3D,
            Thread rotateThread, JLabel rotateLabel) {

        switch (MathToolGUI.getTypeGraphic()) {
            case GRAPH3D:
                graphicPanel3D.setIsRotating(false);
                break;
            case GRAPHCURVE3D:
                graphicPanelCurves3D.setIsRotating(false);
                break;
            case GRAPHIMPLICIT3D:
                graphicPanelImplicit3D.setIsRotating(false);
                break;
            default:
                break;
        }
        rotateThread.interrupt();
        rotateLabel.setText(bold(Translator.translateOutputMessage(GUI_ROTATE_GRAPH)));

    }

    /**
     * Setzt je nach Sprachmodus den entsprechenden Menüeintrag auf fett.
     */
    public static void setFontForLanguages(JMenuItem menuItemLanguageEnglish, JMenuItem menuItemLanguageGerman,
            JMenuItem menuItemLanguageRussian, JMenuItem menuItemLanguageUkrainian) {

        // Im Sprachmenü die gewählte Sprache fett hervorheben.
        if (Expression.getLanguage().equals(TypeLanguage.EN)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
        } else if (Expression.getLanguage().equals(TypeLanguage.DE)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
        } else if (Expression.getLanguage().equals(TypeLanguage.RU)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
        } else if (Expression.getLanguage().equals(TypeLanguage.UA)) {
            menuItemLanguageEnglish.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageGerman.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageRussian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.PLAIN, 12));
            menuItemLanguageUkrainian.setFont(new Font(menuItemLanguageEnglish.getFont().getFamily(), Font.BOLD, 12));
        }

    }

    /**
     * Setzt je nach Darstellungsmodus den entsprechenden Menüeintrag auf fett.
     */
    public static void setFontForMode(JMenuItem menuItemRepresentationFormula, JMenuItem menuItemRepresentationText) {
        // Im Darstellungsmenü den gewählten Modus fett hervorheben.
        if (MathToolGUI.getMode().equals(TypeMode.GRAPHIC)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.BOLD, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.PLAIN, 12));
        } else if (MathToolGUI.getMode().equals(TypeMode.TEXT)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.PLAIN, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.BOLD, 12));
        }
    }

    /**
     * Die Captions aller in componentCaptions befindlichen Komponenten werden
     * entsprechend der Sprache und der ID (im zugehörigen value) gesetzt.
     */
    public static void updateAllCaptions(Map<JComponent, String> componentCaptions) {
        for (JComponent component : componentCaptions.keySet()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setText(bold(Translator.translateOutputMessage(componentCaptions.get(component))));
            } else if (component instanceof JButton) {
                ((JButton) component).setText(Translator.translateOutputMessage(componentCaptions.get(component)));
            } else if (component instanceof JMenuItem) {
                ((JMenuItem) component).setText(Translator.translateOutputMessage(componentCaptions.get(component)));
            }
        }
    }

    /**
     * Gibt zurück, ob input, wenn überhaupt, nur ein gültiger arithmetischer
     * Ausdruck (und kein logischer und kein Matrizenausdruck) sein kann.
     */
    public static boolean isInputProbablyAlgebraicExpression(String input) {
        return input.length() > 0 
                && !input.contains(TypeLogicalBinary.AND.getValue()) 
                && !input.contains(TypeLogicalBinary.OR.getValue())
                && !input.contains(TypeLogicalBinary.IMPLICATION.getValue()) 
                && !input.contains(TypeLogicalBinary.EQUIVALENCE.getValue()) 
                && !input.contains(TypeLogicalUnary.NEGATION.getValue()) 
                && !input.contains("[")
                && !input.contains("]")
                && !input.contains(TypeMatrixOperator.grad.getOperatorName());
    }

    /**
     * Gibt zurück, ob input, wenn überhaupt, ein gültiger Matrizenausdruck sein
     * kann. Diese Prüfung muss stattfinden, nachdem mittels
     * isInputAlgebraicExpression(input) bereits geprüft und false
     * zurückgeliefert wurde.
     */
    public static boolean isInputProbablyMatrixExpression(String input) {
        return input.length() > 0 
                && !input.contains(TypeLogicalBinary.AND.getValue()) 
                && !input.contains(TypeLogicalBinary.OR.getValue())
                && !input.contains(TypeLogicalBinary.IMPLICATION.getValue()) 
                && !input.contains(TypeLogicalBinary.EQUIVALENCE.getValue());
    }

    public static String bold(String s) {
        return "<html><b>" + s + "</b></html>";
    }

    public static String boldAndUnderlined(String s) {
        return "<html><b><u>" + s + "</u></b></html>";
    }
    
    // Ausführung der Eingabe.
    public static void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
        runCallableWithTimeout(() -> {
            runnable.run();
            return null;
        }, timeout, timeUnit);
    }

    private static <T> T runCallableWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<T> future = executor.submit(callable);
        executor.shutdown();
        try {
            return future.get(timeout, timeUnit);
        } catch (TimeoutException e) {
            future.cancel(true);
            doPrintOutput(Translator.translateOutputMessage(GUI_TIMEOUT));
            log.logTimeoutException();
            throw e;
        } catch (ExecutionException e) {
            log.logException(e);
            Throwable t = e.getCause();
            if (t instanceof Error) {
                throw (Error) t;
            } else if (t instanceof Exception) {
                throw (Exception) t;
            }
            throw new IllegalStateException(t);
        }
    }
    
    private static void doPrintOutput(String out) {
        mathToolTextArea.append(out + NEXT_LINE + NEXT_LINE);
        mathToolGraphicArea.addComponent(out);
    }

}
