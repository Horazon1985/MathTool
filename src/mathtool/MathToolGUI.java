package mathtool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import command.Command;
import command.TypeCommand;
import enums.TypeGraphic;
import enums.TypeLanguage;
import enums.TypeSimplify;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import abstractexpressions.expression.classes.Expression;
import graphic.GraphicArea;
import graphic.GraphicPanel2D;
import graphic.GraphicPanel3D;
import graphic.GraphicPanelCurves2D;
import graphic.GraphicPanelCurves3D;
import graphic.GraphicPanelImplicit2D;
import graphic.GraphicPanelPolar;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import exceptions.CancellationException;
import graphic.GraphicPanelCylindrical;
import graphic.GraphicPanelFormula;
import graphic.GraphicPanelImplicit3D;
import graphic.GraphicPanelSpherical;
import graphic.GraphicPanelSurface;
import graphic.GraphicPanelVectorField2D;
import mathtool.annotations.GraphicPanel;
import mathtool.enums.TypeMode;
import mathtool.component.dialogs.MathToolSaveGraphicDialog;
import mathtool.component.dialogs.MathToolSaveSessionDialog;
import mathtool.component.components.ComputingDialogGUI;
import mathtool.component.components.DevelopersDialogGUI;
import mathtool.component.components.HelpDialogGUI;
import mathtool.component.components.LegendGUI;
import mathtool.component.components.MathToolTextField;
import mathtool.component.components.OutputOptionsDialogGUI;
import mathtool.component.components.GraphicOptionsDialogGUI;
import mathtool.component.components.OutputDetailsGUI;
import mathtool.utilities.MathToolUtilities;
import mathcommandcompiler.MathCommandCompiler;
import static mathtool.MathToolController.bold;
import static mathtool.MathToolController.boldAndUnderlined;
import mathtool.component.components.MathToolAlgorithmsGUI;
import mathtool.lang.translator.Translator;
import util.OperationDataTO;
import util.OperationParsingUtils;

public class MathToolGUI extends JFrame implements MouseListener {

    private static final String GUI_LEGEND = "GUI_LEGEND";
    private static final String GUI_SAVE = "GUI_SAVE";
    private static final String GUI_ROTATE_GRAPH = "GUI_ROTATE_GRAPH";
    private static final String GUI_MENU_FILE = "GUI_MENU_FILE";
    private static final String GUI_MENU_OPEN = "GUI_MENU_OPEN";
    private static final String GUI_MENU_SAVE = "GUI_MENU_SAVE";
    private static final String GUI_MENU_QUIT = "GUI_MENU_QUIT";
    private static final String GUI_MENU_MATHTOOL = "GUI_MENU_MATHTOOL";
    private static final String GUI_MENU_HELP = "GUI_MENU_HELP";
    private static final String GUI_MENU_LANGUAGES = "GUI_MENU_LANGUAGES";
    private static final String GUI_MENU_ENGLISH = "GUI_MENU_ENGLISH";
    private static final String GUI_MENU_GERMAN = "GUI_MENU_GERMAN";
    private static final String GUI_MENU_RUSSIAN = "GUI_MENU_RUSSIAN";
    private static final String GUI_MENU_UKRAINIAN = "GUI_MENU_UKRAINIAN";
    private static final String GUI_MENU_REPRESENTATION_MODE = "GUI_MENU_REPRESENTATION_MODE";
    private static final String GUI_FORMULA_MODE = "GUI_FORMULA_MODE";
    private static final String GUI_MENU_TEXT_MODE = "GUI_MENU_TEXT_MODE";
    private static final String GUI_MENU_ABOUT = "GUI_MENU_ABOUT";
    private static final String GUI_MENU_OPTIONS = "GUI_MENU_OPTIONS";
    private static final String GUI_MENU_OUTPUT_OPTIONS = "GUI_MENU_OUTPUT_OPTIONS";
    private static final String GUI_MENU_GRAPHIC_OPTIONS = "GUI_MENU_GRAPHIC_OPTIONS";
    private static final String GUI_TOOLS = "GUI_TOOLS";
    private static final String GUI_ALGORITHMS = "GUI_ALGORITHMS";
    private static final String GUI_APPROX = "GUI_APPROX";
    private static final String GUI_LATEX_CODE = "GUI_LATEX_CODE";
    private static final String GUI_CLEAR = "GUI_CLEAR";
    private static final String GUI_INPUT = "GUI_INPUT";
    private static final String GUI_CANCEL = "GUI_CANCEL";
    private static final String GUI_STOP_ROTATION = "GUI_STOP_ROTATION";
    private static final String GUI_ERROR = "GUI_ERROR";
    private static final String GUI_UNEXPECTED_EXCEPTION = "GUI_UNEXPECTED_EXCEPTION";
    private static final String GUI_EQUIVALENT_TO = "GUI_EQUIVALENT_TO";
    private static final String GUI_HelpDialogGUI_GENERALITIES = "GUI_HelpDialogGUI_GENERALITIES";
    private static final String GUI_HelpDialogGUI_MATH_FORMULAS = "GUI_HelpDialogGUI_MATH_FORMULAS";
    private static final String GUI_HelpDialogGUI_LOGICAL_EXPRESSION = "GUI_HelpDialogGUI_LOGICAL_EXPRESSION";
    private static final String GUI_HelpDialogGUI_OPERATORS = "GUI_HelpDialogGUI_OPERATORS";
    private static final String GUI_HelpDialogGUI_COMMANDS = "GUI_HelpDialogGUI_COMMANDS";
    private static final String GUI_HelpDialogGUI_BUG_REPORT = "GUI_HelpDialogGUI_BUG_REPORT";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTIONS_GROUP_NAME = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTIONS_GROUP_NAME";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_ALGEBRAIC_EXPRESSIONS = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_ALGEBRAIC_EXPRESSIONS";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_FUNCTIONAL_RELATIONS = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_FUNCTIONAL_RELATIONS";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_AND_COLLECT_IF_SHORTER = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_AND_COLLECT_IF_SHORTER";
    private static final String GUI_OutputOptionsDialogGUI_SAVE_BUTTON = "GUI_OutputOptionsDialogGUI_SAVE_BUTTON";
    private static final String GUI_OutputOptionsDialogGUI_CANCEL_BUTTON = "GUI_OutputOptionsDialogGUI_CANCEL_BUTTON";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_LOGARITHM_OPTION = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_LOGARITHM_OPTION";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_COLLECT_LOGARITHMS = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_COLLECT_LOGARITHMS";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_LOGARITHMS = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_LOGARITHMS";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_FACTPRIZATION_OPTION = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_FACTPRIZATION_OPTION";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_FACTORIZE = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_FACTORIZE";
    private static final String GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND = "GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND";
    private static final String GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_GROUP_NAME = "GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_GROUP_NAME";
    private static final String GUI_GraphicOptionsDialogGUI_SAVE_BUTTON = "GUI_GraphicOptionsDialogGUI_SAVE_BUTTON";
    private static final String GUI_GraphicOptionsDialogGUI_CANCEL_BUTTON = "GUI_GraphicOptionsDialogGUI_CANCEL_BUTTON";
    private static final String GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT = "GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT";
    private static final String GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK = "GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK";
    private static final String GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH = "GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH";
    private static final String GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY = "GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY";
    private static final String GUI_GraphicOptionsDialogGUI_SHOW_CURSOR_ON_GRAPH = "GUI_GraphicOptionsDialogGUI_SHOW_CURSOR_ON_GRAPH";
    private static final String GUI_GraphicOptionsDialogGUI_DO_NOT_SHOW_CURSOR_ON_GRAPH = "GUI_GraphicOptionsDialogGUI_DO_NOT_SHOW_CURSOR_ON_GRAPH";
    private static final String GUI_LegendGUI_CONTROLS = "GUI_LegendGUI_CONTROLS";
    private static final String GUI_LegendGUI_GRAPH = "GUI_LegendGUI_GRAPH";
    private static final String GUI_LegendGUI_PARAMETRIZED_SURFACE = "GUI_LegendGUI_PARAMETRIZED_SURFACE";
    private static final String GUI_LegendGUI_EQUATION_OF_IMPLICIT_FUNCTION = "GUI_LegendGUI_EQUATION_OF_IMPLICIT_FUNCTION";
    private static final String GUI_LegendGUI_PARAMETERIZED_CURVE = "GUI_LegendGUI_PARAMETERIZED_CURVE";
    private static final String GUI_LegendGUI_VECTORFIELD = "GUI_LegendGUI_VECTORFIELD";

    public static final Color BACKGROUND_COLOR = new Color(255, 150, 0);

    private final JLabel legendLabel;
    private final JLabel saveLabel;
    private final JLabel rotateLabel;
    private final JTextArea mathToolTextArea;
    private final JScrollPane scrollPaneText;
    private final GraphicArea mathToolGraphicArea;
    private final JScrollPane scrollPaneGraphic;
    private ComputingDialogGUI computingDialogGUI;
    private final MathToolTextField mathToolTextField;

    @GraphicPanel
    private static GraphicPanel2D graphicPanel2D;
    @GraphicPanel
    private static GraphicPanel3D graphicPanel3D;
    @GraphicPanel
    private static GraphicPanelCurves2D graphicPanelCurves2D;
    @GraphicPanel
    private static GraphicPanelCurves3D graphicPanelCurves3D;
    @GraphicPanel
    private static GraphicPanelImplicit2D graphicPanelImplicit2D;
    @GraphicPanel
    private static GraphicPanelImplicit3D graphicPanelImplicit3D;
    @GraphicPanel
    private static GraphicPanelPolar graphicPanelPolar;
    @GraphicPanel
    private static GraphicPanelCylindrical graphicPanelCylindrical;
    @GraphicPanel
    private static GraphicPanelSpherical graphicPanelSpherical;
    @GraphicPanel
    private static GraphicPanelSurface graphicPanelSurface;
    @GraphicPanel
    private static GraphicPanelVectorField2D graphicPanelVectorField2D;

    private static JPanel[] graphicPanels = new JPanel[0];
    private final JComponent[] buttonsAndDropDowns;

    private HashMap<JComponent, String> componentCaptions;

    // Zeitabhängige Komponenten
    private Thread rotateThread;
    private SwingWorker<Void, Void> computingSwingWorker;
    private Timer computingTimer;

    /**
     * MathTool-Log mit allen bisher ausgeführten Befehlen.
     */
    private static final ArrayList<String> COMMANDS = new ArrayList<>();

    // Laufzeitvariablen.
    private static TypeGraphic typeGraphic = TypeGraphic.NONE;
    private static TypeMode typeMode;
    private static int fontSizeGraphic;
    private static int fontSizeText;
    private static Dimension minimumDimension;
    private static boolean isRotating = false;
    private static boolean computing = false;

    // Koordinaten und Maße für die graphische Ausgabeoberfläche.
    public static int mathToolGraphicAreaX;
    public static int mathToolGraphicAreaY;
    public static int mathToolGraphicAreaWidth;
    public static int mathToolGraphicAreaHeight;

    // Optionen
    private static HashSet<TypeSimplify> simplifyTypes = new HashSet<>();

    // logPosition = Index des aktuellen Befehls, den man mittels Pfeiltasten ausgegeben haben möchte.
    public static int logPosition = 0;

    public MathToolGUI() {

        initComponents();
        initCaptions();
        setLayout(null);

        // Prüfung, ob alle nötigen Resourcen vorhanden sind.
        MathToolController.checkExpressionBuilderResources();
        MathToolController.checkMathToolResources();

        // Vereinfachungsoptionen initialisieren.
        MathToolController.initSimplifyTypes();

        // Konfigurationen aus XML auslesen.
        MathToolController.loadSettings();

        // Mindestfenstergröße festlegen
        setMinimumSize(minimumDimension);

        // Labels ausrichten
        legendLabel = new JLabel(bold(Translator.translateOutputMessage(GUI_LEGEND)));
        legendLabel.setVisible(false);
        add(legendLabel);
        legendLabel.addMouseListener(this);

        saveLabel = new JLabel(bold(Translator.translateOutputMessage(GUI_SAVE)));
        saveLabel.setVisible(false);
        add(saveLabel);
        saveLabel.addMouseListener(this);

        rotateLabel = new JLabel(bold(Translator.translateOutputMessage(GUI_ROTATE_GRAPH)));
        rotateLabel.setVisible(false);
        add(rotateLabel);
        rotateLabel.addMouseListener(this);

        // Textliches Ausgabefeld ausrichten
        mathToolTextArea = new JTextArea();
        mathToolTextArea.setFont(new Font("Times New Roman", Font.BOLD, fontSizeText));
        mathToolTextArea.setEditable(false);
        mathToolTextArea.setLineWrap(true);
        mathToolTextArea.setWrapStyleWord(true);
        scrollPaneText = new JScrollPane(mathToolTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mathToolTextArea.setCaretPosition(mathToolTextArea.getDocument().getLength());
        add(scrollPaneText);
        scrollPaneText.setVisible(false);

        mathToolTextField = new MathToolTextField();
        mathToolTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mathToolTextFieldKeyPressed(evt);
            }
        });
        mathToolTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                MathToolController.checkInputValidity(mathToolTextField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                MathToolController.checkInputValidity(mathToolTextField);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                MathToolController.checkInputValidity(mathToolTextField);
            }
        });

        getContentPane().add(mathToolTextField);
        mathToolTextField.setBounds(10, 336, 540, 20);

        // Graphisches Ausgabefeld ausrichten
        mathToolGraphicAreaX = 10;
        mathToolGraphicAreaY = 10;
        mathToolGraphicAreaWidth = this.getWidth() - 40;
        mathToolGraphicAreaHeight = this.getHeight() - 170;
        mathToolGraphicArea = new GraphicArea(mathToolGraphicAreaX, mathToolGraphicAreaY,
                mathToolGraphicAreaWidth, mathToolGraphicAreaHeight, this);
        mathToolGraphicArea.setFontSize(fontSizeGraphic);
        scrollPaneGraphic = new JScrollPane(mathToolGraphicArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneGraphic);

        // 2D-Grafikobjekte initialisieren
        graphicPanel2D = new GraphicPanel2D();
        add(graphicPanel2D);

        graphicPanelCurves2D = new GraphicPanelCurves2D();
        add(graphicPanelCurves2D);

        graphicPanelImplicit2D = new GraphicPanelImplicit2D();
        add(graphicPanelImplicit2D);

        graphicPanelPolar = new GraphicPanelPolar();
        add(graphicPanelPolar);

        // 3D-Grafikobjekte initialisieren
        graphicPanel3D = new GraphicPanel3D();
        add(graphicPanel3D);

        graphicPanelImplicit3D = new GraphicPanelImplicit3D();
        add(graphicPanelImplicit3D);

        graphicPanelCurves3D = new GraphicPanelCurves3D();
        add(graphicPanelCurves3D);

        graphicPanelCylindrical = new GraphicPanelCylindrical();
        add(graphicPanelCylindrical);

        graphicPanelSpherical = new GraphicPanelSpherical();
        add(graphicPanelSpherical);

        graphicPanelSurface = new GraphicPanelSurface();
        add(graphicPanelSurface);

        graphicPanelVectorField2D = new GraphicPanelVectorField2D();
        add(graphicPanelVectorField2D);

        // Alle Grafikpanels unsichtbar machen.
        graphicPanels = getAllGraphicPanels();
        MathToolController.setGraphicPanelsVisible(graphicPanels, false);

        // Alle Buttons und Dropdowns ausrichten.
        buttonsAndDropDowns = new JComponent[]{approxButton, latexButton, clearButton, operatorChoice, commandChoice};

        // Operatorbox aktualisieren.
        MathToolController.fillOperatorChoice(operatorChoice);
        // Befehlbox aktualisieren.
        MathToolController.fillCommandChoice(commandChoice);

        // An MathCommandCompiler alle Anzeigeobjekte übergeben.
        MathCommandCompiler.setGraphicPanel2D(graphicPanel2D);
        MathCommandCompiler.setGraphicPanel3D(graphicPanel3D);
        MathCommandCompiler.setGraphicPanelCurves2D(graphicPanelCurves2D);
        MathCommandCompiler.setGraphicPanelCurves3D(graphicPanelCurves3D);
        MathCommandCompiler.setGraphicPanelImplicit2D(graphicPanelImplicit2D);
        MathCommandCompiler.setGraphicPanelImplicit3D(graphicPanelImplicit3D);
        MathCommandCompiler.setGraphicPanelPolar2D(graphicPanelPolar);
        MathCommandCompiler.setGraphicPanelCylindrical(graphicPanelCylindrical);
        MathCommandCompiler.setGraphicPanelSpherical(graphicPanelSpherical);
        MathCommandCompiler.setGraphicPanelSurface(graphicPanelSurface);
        MathCommandCompiler.setGraphicPanelVectorField2D(graphicPanelVectorField2D);
        MathCommandCompiler.setMathToolTextArea(mathToolTextArea);
        MathCommandCompiler.setMathToolGraphicArea(mathToolGraphicArea);

        validate();
        repaint();

        // Diverse Listener
        // Der Fokus soll immmer auf der Eingabezeile sien.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                mathToolTextField.requestFocus();
            }
        });

        // ComponentListener für das Ausrichten von Komponenten bei Änderung der Maße von MathTool.
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

                // Konsolenmaße setzen.
                MathToolController.resizeConsole(scrollPaneText, scrollPaneGraphic, 10, 10, getWidth() - 40, getHeight() - 170,
                        mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton);
                // Alle Buttons und Dropdowns korrekt ausrichten.
                MathToolController.locateButtonsAndDropDowns(buttonsAndDropDowns, 10, scrollPaneText.getHeight() + 60, 150, 30, 5);
                // Alle Grafikpanels korrekt ausrichten.
                MathToolController.locateGraphicPanels(graphicPanels, scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);

                legendLabel.setBounds(graphicPanel3D.getX(), scrollPaneText.getHeight() + 25, 100, 25);
                saveLabel.setBounds(graphicPanel3D.getX() + 150, scrollPaneText.getHeight() + 25, 150, 25);
                rotateLabel.setBounds(graphicPanel3D.getX() + 300, scrollPaneText.getHeight() + 25, 200, 25);

                if (computingDialogGUI != null) {
                    computingDialogGUI = new ComputingDialogGUI(computingSwingWorker, getX(), getY(), getWidth(), getHeight());
                }

                if (!typeGraphic.equals(TypeGraphic.NONE)) {
                    // Konsolenmaße neu setzen, falls eine Grafik angezeigt werden muss.
                    MathToolController.resizeConsole(scrollPaneText, scrollPaneGraphic, 10, 10, getWidth() - 550, getHeight() - 170,
                            mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton);
                }

                mathToolGraphicArea.updateSize();

                // Abhängig von der Sprache alle Texte (neu) setzen.
                updateGUI();

                validate();
                repaint();
            }

        });

    }

    /**
     * Getter für typeMode.
     */
    public static TypeMode getMode() {
        return typeMode;
    }

    /**
     * Setter für typeMode.
     */
    public static void setMode(TypeMode mode) {
        typeMode = mode;
    }

    /**
     * Getter für typeGraphic.
     */
    public static TypeGraphic getTypeGraphic() {
        return typeGraphic;
    }

    /**
     * Setter für typeGraphic.
     */
    public static void setTypeGraphic(TypeGraphic graphic) {
        typeGraphic = graphic;
    }

    /**
     * Getter für fontSizeGraphic.
     */
    public static int getFontSizeGraphic() {
        return fontSizeGraphic;
    }

    /**
     * Setter für fontSizeGraphic.
     */
    public static void setFontSizeGraphic(int size) {
        fontSizeGraphic = size;
    }

    /**
     * Getter für fontSizeText.
     */
    public static int getFontSizeText() {
        return fontSizeText;
    }

    /**
     * Setter für fontSizeText.
     */
    public static void setFontSizeText(int size) {
        fontSizeText = size;
    }

    /**
     * Getter für minimumDimension.
     */
    public static Dimension getMinimumDimension() {
        return minimumDimension;
    }

    /**
     * Setter für minimumDimension.
     */
    public static void setMinimumDimension(Dimension dim) {
        minimumDimension = dim;
    }

    /**
     * Setter für language.
     */
    public static void setLanguage(TypeLanguage language) {
        Expression.setLanguage(language);
    }

    /**
     * Getter für commandList.
     */
    public static ArrayList<String> getCommandList() {
        return COMMANDS;
    }

    /**
     * Getter für simplifyTypes.
     */
    public static HashSet<TypeSimplify> getSimplifyTypes() {
        return simplifyTypes;
    }

    /**
     * Setter für simplifyTypes.
     */
    public static void setSimplifyTypes(HashSet<TypeSimplify> simplifyTypes) {
        MathToolGUI.simplifyTypes = simplifyTypes;
    }

    /**
     * Gibt ein Array mit allen GraphicPanels zurück.
     */
    private static JPanel[] getAllGraphicPanels() {
        ArrayList<JPanel> graphicPanelsAsList = new ArrayList<>();
        Field[] fields = MathToolGUI.class.getDeclaredFields();
        GraphicPanel annotation;
        for (Field field : fields) {
            annotation = field.getAnnotation(GraphicPanel.class);
            if (annotation != null) {
                try {
                    graphicPanelsAsList.add((JPanel) field.get(null));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                }
            }
        }
        return graphicPanelsAsList.toArray(graphicPanels);
    }

    /**
     * Aktualisiert die Oberfläche nach Änderung von Einstellungen.
     */
    private void updateGUI() {
        // Captions neu setzen.
        initCaptions();
        // Im Sprachmenü die gewählte Sprache fett hervorheben.
        MathToolController.setFontForLanguages(menuItemLanguageEnglish, menuItemLanguageGerman, menuItemLanguageRussian, menuItemLanguageUkrainian);
        // Im Darstellungsmenü den gewählten Modus fett hervorheben.
        MathToolController.setFontForMode(menuItemRepresentationFormula, menuItemRepresentationText);
        // Menüeinträge aktualisieren
        MathToolController.updateAllCaptions(componentCaptions);
        // Operatorbox aktualisieren.
        MathToolController.fillOperatorChoice(operatorChoice);
        // Befehlbox aktualisieren.
        MathToolController.fillCommandChoice(commandChoice);
    }

    /**
     * Falls commandName der Name eines Befehls ist, welcher Grafiken anzuzeigen
     * veranlasst, dann wird das dazu passende Panel (auf dem die Grafik
     * gezeichnet wird) angezeigt und die Ausgabefläche wird um die
     * entsprechenden Maße nach links zusammengedrückt.
     */
    private void activatePanelsForGraphs(String commandName, String[] params) {

        Command c;
        try {
            c = MathCommandCompiler.getCommand(commandName, params);
        } catch (ExpressionException e) {
            return;
        }

        // Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
        MathToolController.resizeConsole(scrollPaneText, scrollPaneGraphic, 10, 10, getWidth() - 550, getHeight() - 170,
                mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton);

        // Alle Grafik-Panels zunächst unsichtbar machen, dann, je nach Fall, wieder sichtbar machen.
        MathToolController.setGraphicPanelsVisible(graphicPanels, false);
        legendLabel.setVisible(false);
        saveLabel.setVisible(false);
        rotateLabel.setVisible(false);

        // Alle Grafikpanels korrekt ausrichten.
        MathToolController.locateGraphicPanels(graphicPanels, scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        // Grafiktyp neu ermitteln.
        typeGraphic = MathToolController.getTypeGraphicFromCommand(c);

        if (c.getTypeCommand().equals(TypeCommand.extrema) && c.getParams().length >= 3) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plot2d)) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotimplicit2d)) {
            graphicPanelImplicit2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plot3d) || c.getTypeCommand().equals(TypeCommand.tangent)
                && ((Expression) c.getParams()[0]).getContainedIndeterminates().size() <= 2 && ((HashMap) c.getParams()[1]).size() == 2) {
            graphicPanel3D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotimplicit3d)) {
            graphicPanelImplicit3D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotcurve2d)) {
            graphicPanelCurves2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotcurve3d)) {
            graphicPanelCurves3D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotpolar)) {
            graphicPanelPolar.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotcylindrical)) {
            graphicPanelCylindrical.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotspherical)) {
            graphicPanelSpherical.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotsurface)) {
            graphicPanelSurface.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plotvectorfield2d)) {
            graphicPanelVectorField2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.regressionline) && c.getParams().length >= 2) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.solve) && c.getParams().length >= 3 || c.getTypeCommand().equals(TypeCommand.tangent)
                && ((Expression) c.getParams()[0]).getContainedIndeterminates().size() <= 1 && ((HashMap) c.getParams()[1]).size() == 1) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.solvediffeq) && c.getParams().length >= 6) {
            graphicPanel2D.setVisible(true);
            saveLabel.setVisible(true);
        } else {
            MathToolController.resizeConsole(scrollPaneText, scrollPaneGraphic, 10, 10, getWidth() - 40, getHeight() - 170,
                    mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton);
        }

        validate();
        repaint();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu2 = new javax.swing.JMenu();
        inputButton = new javax.swing.JButton();
        latexButton = new javax.swing.JButton();
        approxButton = new javax.swing.JButton();
        operatorChoice = new javax.swing.JComboBox();
        commandChoice = new javax.swing.JComboBox();
        clearButton = new javax.swing.JButton();
        mathToolMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemOpen = new javax.swing.JMenuItem();
        menuItemSave = new javax.swing.JMenuItem();
        menuItemQuit = new javax.swing.JMenuItem();
        menuMathTool = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        menuItemLanguageMenu = new javax.swing.JMenu();
        menuItemLanguageEnglish = new javax.swing.JMenuItem();
        menuItemLanguageGerman = new javax.swing.JMenuItem();
        menuItemLanguageRussian = new javax.swing.JMenuItem();
        menuItemLanguageUkrainian = new javax.swing.JMenuItem();
        menuItemRepresentationMenu = new javax.swing.JMenu();
        menuItemRepresentationFormula = new javax.swing.JMenuItem();
        menuItemRepresentationText = new javax.swing.JMenuItem();
        menuItemOptionsMenu = new javax.swing.JMenu();
        menuItemOutputOptions = new javax.swing.JMenuItem();
        menuItemGraphicOptions = new javax.swing.JMenuItem();
        menuItemAbout = new javax.swing.JMenuItem();
        menuTools = new javax.swing.JMenu();
        menuItemAlgorithms = new javax.swing.JMenuItem();

        jMenu2.setText("jMenu2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MathTool - Mathematical Tool for Analysis, Algebra and Numerical Computation");
        setBackground(new java.awt.Color(250, 150, 0));
        setBounds(new java.awt.Rectangle(20, 50, 1300, 670));
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/mathtool/icons/MathToolIcon.png")).getImage());
        getContentPane().setLayout(null);

        inputButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        inputButton.setText("Eingabe");
        inputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputButtonActionPerformed(evt);
            }
        });
        getContentPane().add(inputButton);
        inputButton.setBounds(700, 370, 130, 30);

        latexButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        latexButton.setText("LaTex-Code");
        latexButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                latexButtonActionPerformed(evt);
            }
        });
        getContentPane().add(latexButton);
        latexButton.setBounds(140, 370, 140, 30);

        approxButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        approxButton.setText("Approx");
        approxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approxButtonActionPerformed(evt);
            }
        });
        getContentPane().add(approxButton);
        approxButton.setBounds(10, 370, 130, 30);

        operatorChoice.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        operatorChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operatorChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(operatorChoice);
        operatorChoice.setBounds(420, 370, 130, 23);

        commandChoice.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        commandChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(commandChoice);
        commandChoice.setBounds(560, 370, 130, 23);

        clearButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        clearButton.setText("Leeren");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        getContentPane().add(clearButton);
        clearButton.setBounds(280, 370, 130, 30);

        menuFile.setText("Datei");

        menuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemOpen.setText("Öffnen");
        menuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenActionPerformed(evt);
            }
        });
        menuFile.add(menuItemOpen);

        menuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSave.setText("Speichern");
        menuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSaveActionPerformed(evt);
            }
        });
        menuFile.add(menuItemSave);

        menuItemQuit.setText("Verlassen");
        menuItemQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemQuitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemQuit);

        mathToolMenuBar.add(menuFile);

        menuMathTool.setText("MathTool");
        menuMathTool.setName(""); // NOI18N

        menuItemHelp.setText("Hilfe");
        menuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemHelpActionPerformed(evt);
            }
        });
        menuMathTool.add(menuItemHelp);

        menuItemLanguageMenu.setText("Sprache");

        menuItemLanguageEnglish.setText("Englisch");
        menuItemLanguageEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageEnglishActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageEnglish);

        menuItemLanguageGerman.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        menuItemLanguageGerman.setText("Deutsch");
        menuItemLanguageGerman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageGermanActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageGerman);

        menuItemLanguageRussian.setText("Russisch");
        menuItemLanguageRussian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageRussianActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageRussian);

        menuItemLanguageUkrainian.setText("Ukrainisch");
        menuItemLanguageUkrainian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageUkrainianActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageUkrainian);

        menuMathTool.add(menuItemLanguageMenu);

        menuItemRepresentationMenu.setText("Darstellungsmodus");

        menuItemRepresentationFormula.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        menuItemRepresentationFormula.setText("Formelmodus");
        menuItemRepresentationFormula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRepresentationFormulaActionPerformed(evt);
            }
        });
        menuItemRepresentationMenu.add(menuItemRepresentationFormula);

        menuItemRepresentationText.setText("Textmodus");
        menuItemRepresentationText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRepresentationTextActionPerformed(evt);
            }
        });
        menuItemRepresentationMenu.add(menuItemRepresentationText);

        menuMathTool.add(menuItemRepresentationMenu);

        menuItemOptionsMenu.setText("Optionen");

        menuItemOutputOptions.setText("Ausgabeoptionen");
        menuItemOutputOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOutputOptionsActionPerformed(evt);
            }
        });
        menuItemOptionsMenu.add(menuItemOutputOptions);

        menuItemGraphicOptions.setText("Grafikoptionen");
        menuItemGraphicOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemGraphicOptionsActionPerformed(evt);
            }
        });
        menuItemOptionsMenu.add(menuItemGraphicOptions);

        menuMathTool.add(menuItemOptionsMenu);

        menuItemAbout.setText("Über MathTool");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuMathTool.add(menuItemAbout);

        mathToolMenuBar.add(menuMathTool);

        menuTools.setText("Werkzeuge");

        menuItemAlgorithms.setText("Algorithmenkompiler");
        menuItemAlgorithms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAlgorithmsActionPerformed(evt);
            }
        });
        menuTools.add(menuItemAlgorithms);

        mathToolMenuBar.add(menuTools);

        setJMenuBar(mathToolMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Fügt dem HashMap componentCaptions alle Ids der Texte hinzu, die an den
     * entsprechenden Komponenten (welche die Keys sind) erscheinen sollen.
     */
    private void initCaptions() {

        componentCaptions = new HashMap<>();

        // Menüeinträge
        componentCaptions.put(menuFile, GUI_MENU_FILE);
        componentCaptions.put(menuItemOpen, GUI_MENU_OPEN);
        componentCaptions.put(menuItemSave, GUI_MENU_SAVE);
        componentCaptions.put(menuItemQuit, GUI_MENU_QUIT);
        componentCaptions.put(menuMathTool, GUI_MENU_MATHTOOL);
        componentCaptions.put(menuItemHelp, GUI_MENU_HELP);
        componentCaptions.put(menuItemLanguageMenu, GUI_MENU_LANGUAGES);
        componentCaptions.put(menuItemLanguageEnglish, GUI_MENU_ENGLISH);
        componentCaptions.put(menuItemLanguageGerman, GUI_MENU_GERMAN);
        componentCaptions.put(menuItemLanguageRussian, GUI_MENU_RUSSIAN);
        componentCaptions.put(menuItemLanguageUkrainian, GUI_MENU_UKRAINIAN);
        componentCaptions.put(menuItemRepresentationMenu, GUI_MENU_REPRESENTATION_MODE);
        componentCaptions.put(menuItemRepresentationFormula, GUI_FORMULA_MODE);
        componentCaptions.put(menuItemRepresentationText, GUI_MENU_TEXT_MODE);
        componentCaptions.put(menuItemAbout, GUI_MENU_ABOUT);
        componentCaptions.put(menuItemOptionsMenu, GUI_MENU_OPTIONS);
        componentCaptions.put(menuItemOutputOptions, GUI_MENU_OUTPUT_OPTIONS);
        componentCaptions.put(menuItemGraphicOptions, GUI_MENU_GRAPHIC_OPTIONS);
        componentCaptions.put(menuTools, GUI_TOOLS);
        componentCaptions.put(menuItemAlgorithms, GUI_ALGORITHMS);
        // Buttons
        componentCaptions.put(approxButton, GUI_APPROX);
        componentCaptions.put(latexButton, GUI_LATEX_CODE);
        componentCaptions.put(clearButton, GUI_CLEAR);
        componentCaptions.put(inputButton, GUI_INPUT);

        // Labels
        componentCaptions.put(legendLabel, GUI_LEGEND);
        componentCaptions.put(saveLabel, GUI_SAVE);
        if (isRotating) {
            componentCaptions.put(rotateLabel, GUI_STOP_ROTATION);
        } else {
            componentCaptions.put(rotateLabel, GUI_ROTATE_GRAPH);
        }

    }

    /**
     * Falls ein neuer Befehl eingegeben wird, während sich eine 3D-Grafik
     * dreht, wird die Rotation dieser 3D-Grafik gestoppt wird.<br>
     * GRUND: Es kann zu Anzeigefehlern bei der 3D-Grafik kommen.
     */
    private void stopPossibleRotation() {
        if (isRotating) {
            isRotating = false;
            MathToolController.stopRotationOfGraph(graphicPanel3D, graphicPanelImplicit3D, graphicPanelCurves3D, rotateThread, rotateLabel);
        }
    }

    /**
     * Hauptmethode zum Ausführen eines Befehls.
     */
    private void executeCommand() {

        final MathToolGUI mathToolGUI = this;

        computingSwingWorker = new SwingWorker<Void, Void>() {

            @Override
            protected void done() {
                computing = false;
                computingTimer.cancel();
                computingDialogGUI.setVisible(false);
                inputButton.setText(Translator.translateOutputMessage(GUI_INPUT));
                // mathToolTextArea und mathToolGraphicArea nach unten scrollen lassen.
                scrollPaneText.getVerticalScrollBar().setValue(scrollPaneText.getVerticalScrollBar().getMaximum());
                scrollPaneGraphic.getVerticalScrollBar().setValue(scrollPaneGraphic.getVerticalScrollBar().getMaximum());
            }

            @Override
            protected Void doInBackground() throws Exception {

                inputButton.setText(Translator.translateOutputMessage(GUI_CANCEL));
                computingDialogGUI = new ComputingDialogGUI(computingSwingWorker, mathToolGUI.getX(), mathToolGUI.getY(), mathToolGUI.getWidth(), mathToolGUI.getHeight());
                MathToolController.initTimer(computingTimer, computingDialogGUI);

                boolean validCommand = false;

                // Leerzeichen werden im Vorfeld beseitigt.
                String input = mathToolTextField.getText().replaceAll(" ", "").toLowerCase();

                if (input.isEmpty()) {
                    return null;
                }

                // Befehl loggen!
                COMMANDS.add(input);
                logPosition = COMMANDS.size();

                /*
                 1. Versuch: Es wird geprüft, ob die Zeile einen Befehl
                 bildet. Ja -> Befehl ausführen. Nein -> Weitere Möglichkeiten
                 prüfen.
                 */
                try {
                    OperationDataTO commandData = OperationParsingUtils.getOperationData(input);

                    String commandName = commandData.getOperationName();
                    String[] params = commandData.getOperationArguments();

                    for (TypeCommand commandType : TypeCommand.values()) {
                        validCommand = validCommand || commandName.equals(commandType.toString());
                        if (validCommand) {
                            break;
                        }
                    }

                    if (validCommand) {
                        MathCommandCompiler.doPrintOutput(MathCommandCompiler.getCommand(commandName, params));
                        // Befehl verarbeiten.
                        MathCommandCompiler.executeCommand(input);
                        // Falls es ein Grafikbefehle war -> Grafik sichtbar machen.
                        activatePanelsForGraphs(commandName, params);
                        mathToolTextField.setText("");
                    }

                } catch (ExpressionException | EvaluationException e) {
                    /*
                     Falls es ein gültiger Befehl war (unabhängig davon, ob
                     dieser Fehler enthielt oder nicht) -> abbrechen und NICHT
                     weiter prüfen, ob es sich um einen Ausdruck handeln
                     könnte.
                     */
                    if (validCommand) {
                        MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_ERROR) + e.getMessage());
                        return null;
                    }
                } catch (CancellationException e) {
                    MathCommandCompiler.doPrintOutput(e.getMessage());
                    return null;
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (validCommand) {
                        MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + exception.getMessage());
                        return null;
                    }
                }

                /*
                 Falls es ein gültiger Befehl war (unabhängig davon, ob dieser
                 Fehler enthield oder nicht) -> abbrechen und NICHT weiter
                 prüfen, ob es sich um einen Ausdruck handeln könnte.
                 */
                if (validCommand) {
                    return null;
                }

                /*
                 2. Versuch: Es wird geprüft, ob die Zeile einen
                 mathematischen Ausdruck bildet. Ja -> Vereinfachen und
                 ausgeben. Nein -> Weitere Möglichkeiten prüfen.
                 */
                try {

                    Expression expr = Expression.build(input);

                    try {
                        Expression exprSimplified = expr.simplify(simplifyTypes);
                        MathCommandCompiler.doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(expr), "  =  ", MathToolUtilities.convertToEditableAbstractExpression(exprSimplified));
                        mathToolTextField.setText("");
                        return null;
                    } catch (EvaluationException e) {
                        if (MathToolController.isInputAlgebraicExpression(input)) {
                            MathCommandCompiler.doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(expr));
                            MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_ERROR) + e.getMessage());
                            mathToolTextField.setText("");
                            return null;
                        }
                    } catch (CancellationException e) {
                        MathCommandCompiler.doPrintOutput(e.getMessage());
                        return null;
                    } catch (Exception exception) {
                        // Falls ein unerwarteter Fehler auftritt.
                        if (MathToolController.isInputAlgebraicExpression(input)) {
                            MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + exception.getMessage());
                            return null;
                        }
                    }

                } catch (ExpressionException e) {
                    if (MathToolController.isInputAlgebraicExpression(input)) {
                        /*
                         Dann ist der Ausdruck zumindest kein logischer
                         Ausdruck -> Fehler ausgeben, welcher soeben bei
                         arithmetischen Ausdrücken geworfen wurde.
                         */
                        MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_ERROR) + e.getMessage());
                        return null;
                    }
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (MathToolController.isInputAlgebraicExpression(input)) {
                        MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + exception.getMessage());
                        return null;
                    }
                }

                /*
                 3. Versuch: Es wird geprüft, ob die Zeile einen gültigen
                 Matrizenausdruck bildet. Ja -> vereinfachen und ausgeben.
                 Nein -> Weitere Möglichkeiten prüfen.
                 */
                try {

                    MatrixExpression matExpr = MatrixExpression.build(input);

                    try {
                        MatrixExpression matExprSimplified = matExpr.simplify(simplifyTypes);
                        // Hinzufügen zum textlichen Ausgabefeld.
                        if (matExprSimplified.convertOneTimesOneMatrixToExpression() instanceof Expression) {
                            MathCommandCompiler.doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(matExpr), "  =  ", MathToolUtilities.convertToEditableAbstractExpression((Expression) matExprSimplified.convertOneTimesOneMatrixToExpression()));
                        } else {
                            MathCommandCompiler.doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(matExpr), "  =  ", MathToolUtilities.convertToEditableAbstractExpression(matExprSimplified));
                        }
                        mathToolTextField.setText("");
                        return null;
                    } catch (EvaluationException e) {
                        if (MathToolController.isInputMatrixExpression(input)) {
                            MathCommandCompiler.doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(matExpr));
                            MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_ERROR) + e.getMessage());
                            mathToolTextField.setText("");
                            return null;
                        }
                    } catch (CancellationException e) {
                        MathCommandCompiler.doPrintOutput(e.getMessage());
                        return null;
                    } catch (Exception exception) {
                        // Falls ein unerwarteter Fehler auftritt.
                        if (MathToolController.isInputMatrixExpression(input)) {
                            MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + exception.getMessage());
                            return null;
                        }
                    }

                } catch (ExpressionException e) {
                    if (MathToolController.isInputMatrixExpression(input)) {
                        /*
                         Dann ist der Ausdruck zumindest kein logischer
                         Ausdruck -> Fehler ausgeben, welcher soeben bei
                         arithmetischen Ausdrücken geworfen wurde.
                         */
                        MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_ERROR) + e.getMessage());
                        return null;
                    }
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (MathToolController.isInputMatrixExpression(input)) {
                        MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + exception.getMessage());
                        return null;
                    }
                }

                /*
                 4. Versuch: Es wird geprüft, ob die Zeile einen gültigen
                 logischen Ausdruck bildet. Ja -> vereinfachen und ausgeben.
                 */
                try {
                    LogicalExpression logExpr = LogicalExpression.build(input);
                    LogicalExpression logExprSimplified = logExpr.simplify();
                    MathCommandCompiler.doPrintOutput(MathToolUtilities.convertToEditableAbstractExpression(logExpr),
                            Translator.translateOutputMessage(GUI_EQUIVALENT_TO),
                            MathToolUtilities.convertToEditableAbstractExpression(logExprSimplified));
                    mathToolTextField.setText("");
                } catch (ExpressionException | EvaluationException e) {
                    MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_ERROR) + e.getMessage());
                    return null;
                } catch (CancellationException e) {
                    MathCommandCompiler.doPrintOutput(e.getMessage());
                    return null;
                } catch (Exception exception) {
                    MathCommandCompiler.doPrintOutput(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + exception.getMessage());
                }

                return null;

            }

        };

        computing = true;
        computingTimer = new Timer();
        computingSwingWorker.execute();

    }

    private void inputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputButtonActionPerformed
        if (!computing) {
            // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
            stopPossibleRotation();
            executeCommand();
        } else {
            computingSwingWorker.cancel(true);
        }
    }//GEN-LAST:event_inputButtonActionPerformed

    /**
     * Führt den Klick auf das Rotationslabel für eine 3D-Grafik aus. Falls die
     * Grafik nicht rotiert, so wird sie in Rotation versetzt. Andernfalls wird
     * die Rotation gestoppt.
     */
    private void rotateLabelClick() {
        if (!isRotating) {
            switch (typeGraphic) {
                case GRAPH3D:
                    rotateThread = new Thread(graphicPanel3D, "rotateGraph3D");
                    isRotating = true;
                    graphicPanel3D.setIsRotating(true);
                    break;
                case GRAPHIMPLICIT3D:
                    rotateThread = new Thread(graphicPanelImplicit3D, "rotateGraphImplicit3D");
                    isRotating = true;
                    graphicPanelImplicit3D.setIsRotating(true);
                    break;
                case GRAPHCURVE3D:
                    rotateThread = new Thread(graphicPanelCurves3D, "rotateGraphCurve3D");
                    isRotating = true;
                    graphicPanelCurves3D.setIsRotating(true);
                    break;
                case GRAPHCYLINDRCAL:
                    rotateThread = new Thread(graphicPanelCylindrical, "rotateGraphCylindrical");
                    isRotating = true;
                    graphicPanelCylindrical.setIsRotating(true);
                    break;
                case GRAPHSPHERICAL:
                    rotateThread = new Thread(graphicPanelSpherical, "rotateGraphSpherical");
                    isRotating = true;
                    graphicPanelSpherical.setIsRotating(true);
                    break;
                case GRAPHSURFACE:
                    rotateThread = new Thread(graphicPanelSurface, "rotateGraphSurface");
                    isRotating = true;
                    graphicPanelSurface.setIsRotating(true);
                    break;
                default:
                    break;
            }
            rotateThread.start();
            rotateLabel.setText(boldAndUnderlined(Translator.translateOutputMessage(GUI_STOP_ROTATION)));
        } else {
            isRotating = false;
            switch (typeGraphic) {
                case GRAPH3D:
                    graphicPanel3D.setIsRotating(false);
                    break;
                case GRAPHIMPLICIT3D:
                    graphicPanelImplicit3D.setIsRotating(false);
                    break;
                case GRAPHCURVE3D:
                    graphicPanelCurves3D.setIsRotating(false);
                    break;
                case GRAPHCYLINDRCAL:
                    graphicPanelCylindrical.setIsRotating(false);
                    break;
                case GRAPHSPHERICAL:
                    graphicPanelSpherical.setIsRotating(false);
                    break;
                case GRAPHSURFACE:
                    graphicPanelSurface.setIsRotating(false);
                    break;
                default:
                    break;
            }
            rotateThread.interrupt();
            rotateLabel.setText(boldAndUnderlined(Translator.translateOutputMessage(GUI_ROTATE_GRAPH)));
        }
    }

    private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
        ArrayList<String> menuCaptions = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();
        menuCaptions.add(Translator.translateOutputMessage(GUI_HelpDialogGUI_GENERALITIES));
        menuCaptions.add(Translator.translateOutputMessage(GUI_HelpDialogGUI_MATH_FORMULAS));
        menuCaptions.add(Translator.translateOutputMessage(GUI_HelpDialogGUI_LOGICAL_EXPRESSION));
        menuCaptions.add(Translator.translateOutputMessage(GUI_HelpDialogGUI_OPERATORS));
        menuCaptions.add(Translator.translateOutputMessage(GUI_HelpDialogGUI_COMMANDS));
        menuCaptions.add(Translator.translateOutputMessage(GUI_HelpDialogGUI_BUG_REPORT));
        fileNames.add("Generalities");
        fileNames.add("Formulas");
        fileNames.add("LogicalExpressions");
        fileNames.add("Operators");
        fileNames.add("Commands");
        fileNames.add("Contact");
        HelpDialogGUI helpDialogGUI = HelpDialogGUI.getInstance(this.getX(), this.getY(),
                this.getWidth(), this.getHeight(), menuCaptions, fileNames);
        helpDialogGUI.setVisible(true);
    }//GEN-LAST:event_menuItemHelpActionPerformed

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        DevelopersDialogGUI aboutMathToolGUI = new DevelopersDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        aboutMathToolGUI.setVisible(true);
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void approxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approxButtonActionPerformed
        // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
        stopPossibleRotation();
        mathToolTextField.setText("approx(" + mathToolTextField.getText() + ")");
        executeCommand();
    }//GEN-LAST:event_approxButtonActionPerformed

    private void latexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_latexButtonActionPerformed
        // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
        stopPossibleRotation();
        mathToolTextField.setText("latex(" + mathToolTextField.getText() + ")");
        executeCommand();
    }//GEN-LAST:event_latexButtonActionPerformed

    private void menuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItemQuitActionPerformed

    private void operatorChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operatorChoiceActionPerformed
        if (operatorChoice.getSelectedIndex() > 0) {

            String insertedOperator = (String) operatorChoice.getSelectedItem() + "(";
            int numberOfCommata = MathToolController.getNumberOfCommasForOperators((String) operatorChoice.getSelectedItem());
            for (int i = 0; i < numberOfCommata; i++) {
                insertedOperator = insertedOperator + ",";
            }
            insertedOperator = insertedOperator + ")";

            mathToolTextField.replaceSelection(insertedOperator);
            mathToolTextField.setSelectionStart(mathToolTextField.getSelectionStart() - numberOfCommata - 1);
            mathToolTextField.setSelectionEnd(mathToolTextField.getSelectionStart());
            operatorChoice.setSelectedIndex(0);

        }
        mathToolTextField.requestFocus();
    }//GEN-LAST:event_operatorChoiceActionPerformed

    private void commandChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commandChoiceActionPerformed
        if (commandChoice.getSelectedIndex() > 0) {

            String insertedCommand = (String) commandChoice.getSelectedItem() + "(";
            int numberOfCommata = MathToolController.getNumberOfCommasForCommands((String) commandChoice.getSelectedItem());
            for (int i = 0; i < numberOfCommata; i++) {
                insertedCommand = insertedCommand + ",";
            }
            insertedCommand = insertedCommand + ")";

            mathToolTextField.setText(insertedCommand);
            mathToolTextField.setSelectionStart(mathToolTextField.getSelectionStart() - numberOfCommata - 1);
            mathToolTextField.setSelectionEnd(mathToolTextField.getSelectionStart());
            commandChoice.setSelectedIndex(0);

        }
        mathToolTextField.requestFocus();
    }//GEN-LAST:event_commandChoiceActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
        stopPossibleRotation();
        try {
            MathCommandCompiler.executeCommand("clear()");
        } catch (ExpressionException | EvaluationException e) {
            mathToolTextArea.append(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + e.getMessage() + "\n \n");
            mathToolGraphicArea.addComponent(Translator.translateOutputMessage(GUI_UNEXPECTED_EXCEPTION) + e.getMessage());
        }
    }//GEN-LAST:event_clearButtonActionPerformed

    private void menuItemLanguageEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageEnglishActionPerformed
        Expression.setLanguage(TypeLanguage.EN);
        updateGUI();
    }//GEN-LAST:event_menuItemLanguageEnglishActionPerformed

    private void menuItemLanguageGermanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageGermanActionPerformed
        Expression.setLanguage(TypeLanguage.DE);
        updateGUI();
    }//GEN-LAST:event_menuItemLanguageGermanActionPerformed

    private void menuItemLanguageRussianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageRussianActionPerformed
        Expression.setLanguage(TypeLanguage.RU);
        updateGUI();
    }//GEN-LAST:event_menuItemLanguageRussianActionPerformed

    private void menuItemLanguageUkrainianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageUkrainianActionPerformed
        Expression.setLanguage(TypeLanguage.UA);
        updateGUI();
    }//GEN-LAST:event_menuItemLanguageUkrainianActionPerformed

    private void menuItemRepresentationTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationTextActionPerformed
        typeMode = TypeMode.TEXT;
        scrollPaneGraphic.setVisible(false);
        scrollPaneText.setVisible(true);
        updateGUI();
    }//GEN-LAST:event_menuItemRepresentationTextActionPerformed

    private void menuItemRepresentationFormulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationFormulaActionPerformed
        typeMode = TypeMode.GRAPHIC;
        scrollPaneGraphic.setVisible(true);
        scrollPaneText.setVisible(false);
        updateGUI();
    }//GEN-LAST:event_menuItemRepresentationFormulaActionPerformed

    private void menuItemOutputOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOutputOptionsActionPerformed

        String simplifyOptionsTitle = Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTIONS_GROUP_NAME);

        // Checkboxen.
        ArrayList<String> simplifyOptions = new ArrayList<>();
        simplifyOptions.add(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_ALGEBRAIC_EXPRESSIONS);
        simplifyOptions.add(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_FUNCTIONAL_RELATIONS);
        simplifyOptions.add(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_AND_COLLECT_IF_SHORTER);
        String saveButtonLabel = Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SAVE_BUTTON);
        String cancelButtonLabel = Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_CANCEL_BUTTON);

        // DropDowns.
        ArrayList<String[]> dropDownOptions = new ArrayList<>();
        dropDownOptions.add(new String[]{Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_LOGARITHM_OPTION),
            Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_COLLECT_LOGARITHMS),
            Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_LOGARITHMS)});
        dropDownOptions.add(new String[]{Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_FACTPRIZATION_OPTION),
            Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_FACTORIZE),
            Translator.translateOutputMessage(GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND)});

        OutputOptionsDialogGUI outputOptionsDialogGUI = OutputOptionsDialogGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                2, simplifyOptionsTitle, simplifyOptions, dropDownOptions, saveButtonLabel, cancelButtonLabel);
        outputOptionsDialogGUI.setVisible(true);

    }//GEN-LAST:event_menuItemOutputOptionsActionPerformed

    private void menuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveActionPerformed
        MathToolSaveSessionDialog saveDialog = new MathToolSaveSessionDialog();
        saveDialog.showSaveDialog(this);
        try {
            String path = saveDialog.getSelectedFile().getPath();
            saveDialog.save(path);
        } catch (Exception e) {
        }

    }//GEN-LAST:event_menuItemSaveActionPerformed

    private void menuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenActionPerformed
        MathToolSaveSessionDialog openDialog = new MathToolSaveSessionDialog();
        openDialog.showOpenDialog(this);
        try {
            String path = openDialog.getSelectedFile().getPath();
            openDialog.open(path);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_menuItemOpenActionPerformed

    private void menuItemGraphicOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemGraphicOptionsActionPerformed

        String simplifyOptionsTitle = Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_GROUP_NAME);

        String saveButtonLabel = Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_SAVE_BUTTON);
        String cancelButtonLabel = Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_CANCEL_BUTTON);

        // DropDowns.
        ArrayList<String[]> dropDownOptions = new ArrayList<>();
        dropDownOptions.add(new String[]{Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT),
            Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK)});
        dropDownOptions.add(new String[]{Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH),
            Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY)});
        dropDownOptions.add(new String[]{Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_SHOW_CURSOR_ON_GRAPH),
            Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_DO_NOT_SHOW_CURSOR_ON_GRAPH)});

        GraphicOptionsDialogGUI graphicOptionsDialogGUI = GraphicOptionsDialogGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                2, simplifyOptionsTitle, null, dropDownOptions, saveButtonLabel, cancelButtonLabel);
        graphicOptionsDialogGUI.setVisible(true);

    }//GEN-LAST:event_menuItemGraphicOptionsActionPerformed

    private void menuItemAlgorithmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAlgorithmsActionPerformed
        String algorithmsTitle = Translator.translateOutputMessage(GUI_ALGORITHMS);

        MathToolAlgorithmsGUI mathToolAlgorithmsGUI = MathToolAlgorithmsGUI.getInstance(this.getX(), this.getY(), this.getHeight(),
                algorithmsTitle);

        mathToolAlgorithmsGUI.setVisible(true);

    }//GEN-LAST:event_menuItemAlgorithmsActionPerformed

    /**
     * Key-Steuerung für die MathToolGUI.
     */
    private void mathToolTextFieldKeyPressed(KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (!computing) {
                    // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
                    stopPossibleRotation();
                    executeCommand();
                }
            case KeyEvent.VK_UP:
                if (logPosition > 0) {
                    logPosition--;
                }
                MathToolController.showLoggedCommand(mathToolTextField, logPosition);
                break;
            case KeyEvent.VK_DOWN:
                if (logPosition < COMMANDS.size() - 1) {
                    logPosition++;
                }
                MathToolController.showLoggedCommand(mathToolTextField, logPosition);
                break;
            case KeyEvent.VK_ESCAPE:
                if (computing) {
                    computingSwingWorker.cancel(true);
                } else {
                    mathToolTextField.setText("");
                }
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        LegendGUI legendGUI = null;
        MathToolSaveGraphicDialog saveDialog = null;

        ArrayList<String> instructions = new ArrayList<>();
        ArrayList<String> exprs = new ArrayList<>();
        instructions.add(boldAndUnderlined(Translator.translateOutputMessage(GUI_LegendGUI_CONTROLS)));

        if (e.getSource() == legendLabel && e.getButton() == MouseEvent.BUTTON1) {
            switch (typeGraphic) {
                case GRAPH2D:
                    instructions.addAll(GraphicPanel2D.getInstructions());
                    for (int i = 0; i < graphicPanel2D.getExpressions().size(); i++) {
                        exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_GRAPH, i + 1, graphicPanel2D.getExpressions().get(i).toString()));
                    }
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanel2D.getColors(), exprs);
                    break;
                case GRAPHIMPLICIT2D: {
                    instructions.addAll(GraphicPanelImplicit2D.getInstructions());
                    exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_EQUATION_OF_IMPLICIT_FUNCTION,
                            graphicPanelImplicit2D.getExpressions().get(0).toString(),
                            graphicPanelImplicit2D.getExpressions().get(1).toString()));
                    ArrayList<Color> colors = new ArrayList<>();
                    colors.add(graphicPanelImplicit2D.getColor());
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, colors, exprs);
                    break;
                }
                case GRAPH3D:
                    instructions.addAll(GraphicPanel3D.getInstructions());
                    for (int i = 0; i < graphicPanel3D.getExpressions().size(); i++) {
                        exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_GRAPH, i + 1, graphicPanel3D.getExpressions().get(i).toString()));
                    }
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanel3D.getColors(), exprs);
                    break;
                case GRAPHIMPLICIT3D: {
                    instructions.addAll(GraphicPanelImplicit3D.getInstructions());
                    exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_EQUATION_OF_IMPLICIT_FUNCTION,
                            graphicPanelImplicit3D.getExpressions().get(0).toString(),
                            graphicPanelImplicit3D.getExpressions().get(1).toString()));
                    ArrayList<Color> colors = new ArrayList<>();
                    colors.add(graphicPanelImplicit3D.getColor());
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, colors, exprs);
                    break;
                }
                case GRAPHCURVE2D: {
                    instructions.addAll(GraphicPanelCurves2D.getInstructions());
                    ArrayList<Color> colors = new ArrayList<>();
                    colors.add(Color.blue);
                    exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_PARAMETERIZED_CURVE,
                            "(" + graphicPanelCurves2D.getExpressions()[0]
                            + ", " + graphicPanelCurves2D.getExpressions()[1] + ")"));
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, colors, exprs);
                    break;
                }
                case GRAPHCURVE3D: {
                    instructions.addAll(GraphicPanelCurves3D.getInstructions());
                    ArrayList<Color> colors = new ArrayList<>();
                    colors.add(Color.blue);
                    exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_PARAMETERIZED_CURVE,
                            "(" + graphicPanelCurves3D.getExpressions()[0]
                            + ", " + graphicPanelCurves3D.getExpressions()[1]
                            + ", " + graphicPanelCurves3D.getExpressions()[2] + ")"));
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, colors, exprs);
                    break;
                }
                case GRAPHPOLAR:
                    instructions.addAll(GraphicPanelPolar.getInstructions());
                    for (int i = 0; i < graphicPanelPolar.getExpressions().size(); i++) {
                        exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_GRAPH, i + 1, graphicPanelPolar.getExpressions().get(i).toString()));
                    }
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanelPolar.getColors(), exprs);
                    break;
                case GRAPHCYLINDRCAL:
                    instructions.addAll(GraphicPanelCylindrical.getInstructions());
                    for (int i = 0; i < graphicPanelCylindrical.getExpressions().size(); i++) {
                        exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_GRAPH, i + 1, graphicPanelCylindrical.getExpressions().get(i).toString()));
                    }
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanelCylindrical.getColors(), exprs);
                    break;
                case GRAPHSPHERICAL:
                    instructions.addAll(GraphicPanelSpherical.getInstructions());
                    for (int i = 0; i < graphicPanelSpherical.getExpressions().size(); i++) {
                        exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_GRAPH, i + 1, graphicPanelSpherical.getExpressions().get(i).toString()));
                    }
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanelSpherical.getColors(), exprs);
                    break;
                case GRAPHSURFACE:
                    instructions.addAll(GraphicPanelSurface.getInstructions());
                    exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_PARAMETRIZED_SURFACE,
                            "(" + graphicPanelSurface.getExpressions()[0]
                            + ", " + graphicPanelSurface.getExpressions()[1]
                            + ", " + graphicPanelSurface.getExpressions()[2] + ")"));
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanelSurface.getColors(), exprs);
                    break;
                case VECTORFIELD2D:
                    instructions.addAll(GraphicPanelVectorField2D.getInstructions());
                    ArrayList<Color> colors = new ArrayList<>();
                    colors.add(graphicPanelVectorField2D.getColor());
                    exprs.add(Translator.translateOutputMessage(GUI_LegendGUI_VECTORFIELD, graphicPanelVectorField2D.getVectorFieldExpression().toString()));
                    legendGUI = LegendGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, colors, exprs);
                    break;
                default:
                    break;
            }
        } else if (e.getSource() == saveLabel && e.getButton() == MouseEvent.BUTTON1) {

            switch (typeGraphic) {
                case GRAPH2D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanel2D);
                    break;
                case GRAPH3D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanel3D);
                    break;
                case GRAPHIMPLICIT2D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelImplicit2D);
                    break;
                case GRAPHIMPLICIT3D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelImplicit3D);
                    break;
                case GRAPHCURVE2D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelCurves2D);
                    break;
                case GRAPHCURVE3D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelCurves3D);
                    break;
                case GRAPHPOLAR:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelPolar);
                    break;
                case GRAPHCYLINDRCAL:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelCylindrical);
                    break;
                case GRAPHSPHERICAL:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelSpherical);
                    break;
                case GRAPHSURFACE:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelSurface);
                    break;
                case VECTORFIELD2D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanelVectorField2D);
                    break;
                default:
                    break;
            }

        } else if (e.getSource() == rotateLabel && e.getButton() == MouseEvent.BUTTON1) {
            if (rotateLabel.isVisible()) {
                rotateLabelClick();
            }
        }

        // Prüfen, ob eine der Ausgabeobjekte mit Rechtsklick angeklickt wurde.
        for (GraphicPanelFormula formula : this.mathToolGraphicArea.getFormulas()) {
            if (e.getSource() == formula && e.getButton() == MouseEvent.BUTTON3
                    && formula.getContainedAbstractExpression().length + formula.getContainedEditableStrings().length > 0) {
                OutputDetailsGUI.getInstance(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 500,
                        formula.getContainedAbstractExpression(), formula.getContainedEditableStrings());
            }
        }

        if (legendGUI != null) {
            legendGUI.setVisible(true);
        }
        if (saveDialog != null) {
            stopPossibleRotation();
            saveDialog.showSaveDialog(this);
            try {
                String path = saveDialog.getSelectedFile().getPath();
                saveDialog.save(path);
            } catch (Exception exception) {
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() == legendLabel) {
            legendLabel.setText(boldAndUnderlined(Translator.translateOutputMessage(GUI_LEGEND)));
            validate();
            repaint();
        } else if (e.getSource() == saveLabel) {
            saveLabel.setText(boldAndUnderlined(Translator.translateOutputMessage(GUI_SAVE)));
            validate();
            repaint();
        } else if (e.getSource() == rotateLabel) {
            if (isRotating) {
                rotateLabel.setText(boldAndUnderlined(Translator.translateOutputMessage(GUI_STOP_ROTATION)));
            } else {
                rotateLabel.setText(boldAndUnderlined(Translator.translateOutputMessage(GUI_ROTATE_GRAPH)));
            }
            validate();
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == legendLabel) {
            legendLabel.setText(bold(Translator.translateOutputMessage(GUI_LEGEND)));
            validate();
            repaint();
        } else if (e.getSource() == saveLabel) {
            saveLabel.setText(bold(Translator.translateOutputMessage(GUI_SAVE)));
            validate();
            repaint();
        } else if (e.getSource() == rotateLabel) {
            if (isRotating) {
                rotateLabel.setText(bold(Translator.translateOutputMessage(GUI_STOP_ROTATION)));
            } else {
                rotateLabel.setText(bold(Translator.translateOutputMessage(GUI_ROTATE_GRAPH)));
            }
            validate();
            repaint();
        }
    }

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MathToolGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MathToolGUI mathToolGUI = new MathToolGUI();
                mathToolGUI.setVisible(true);
                mathToolGUI.setBounds(50, 50, 1300, 670);
                mathToolGUI.setExtendedState(MAXIMIZED_BOTH);
                mathToolGUI.getContentPane().setBackground(BACKGROUND_COLOR);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton approxButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JComboBox commandChoice;
    private javax.swing.JButton inputButton;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JButton latexButton;
    private javax.swing.JMenuBar mathToolMenuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemAlgorithms;
    private javax.swing.JMenuItem menuItemGraphicOptions;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemLanguageEnglish;
    private javax.swing.JMenuItem menuItemLanguageGerman;
    private javax.swing.JMenu menuItemLanguageMenu;
    private javax.swing.JMenuItem menuItemLanguageRussian;
    private javax.swing.JMenuItem menuItemLanguageUkrainian;
    private javax.swing.JMenuItem menuItemOpen;
    private javax.swing.JMenu menuItemOptionsMenu;
    private javax.swing.JMenuItem menuItemOutputOptions;
    private javax.swing.JMenuItem menuItemQuit;
    private javax.swing.JMenuItem menuItemRepresentationFormula;
    private javax.swing.JMenu menuItemRepresentationMenu;
    private javax.swing.JMenuItem menuItemRepresentationText;
    private javax.swing.JMenuItem menuItemSave;
    private javax.swing.JMenu menuMathTool;
    private javax.swing.JMenu menuTools;
    private javax.swing.JComboBox operatorChoice;
    // End of variables declaration//GEN-END:variables

}
