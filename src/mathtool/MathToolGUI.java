package mathtool;

import command.Command;
import command.TypeCommand;
import components.MathToolSaveGraphicDialog;
import enumerations.TypeGraphic;
import enumerations.TypeLanguage;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import expressionbuilder.Expression;
import graphic.GraphicArea;
import graphic.GraphicPanel2D;
import graphic.GraphicPanel3D;
import graphic.GraphicPanelCurves2D;
import graphic.GraphicPanelCurves3D;
import graphic.GraphicPanelPolar2D;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import logicalexpressionbuilder.LogicalExpression;
import matrixexpressionbuilder.MatrixExpression;
import translator.Translator;

public class MathToolGUI extends JFrame implements MouseListener {

    public static final Color backgroundColor = new Color(255, 150, 0);

    private final JLabel legendLabel;
    private final JLabel saveLabel;
    private final JLabel rotateLabel;
    private final JTextArea mathToolTextArea;
    private final JScrollPane scrollPaneText;
    private final GraphicArea mathToolGraphicArea;
    private final JScrollPane scrollPaneGraphic;
    private ComputingDialogGUI computingDialog;
    private final MathToolTextField mathToolTextField;

    private final GraphicPanel2D graphicPanel2D;
    private final GraphicPanel3D graphicPanel3D;
    private final GraphicPanelCurves2D graphicPanelCurves2D;
    private final GraphicPanelCurves3D graphicPanelCurves3D;
    private final GraphicPanelPolar2D graphicPanelPolar2D;

    // Zeitabhängige Komponenten
    private Thread rotateThread;
    private SwingWorker<Void, Void> computingSwingWorker;
    private Timer computingTimer;

    /*
     Diese Objekte werden im Laufe des Programms erweitert. Sie enthalten die
     im Laufe des Programms definierten Variablen und Funktionen.
     */
    static HashMap<String, Expression> definedVars = new HashMap<>();
    static HashMap<String, Expression> definedFunctions = new HashMap<>();
    private static final ArrayList<String> listOfCommands = new ArrayList<>();

    // Laufzeitvariablen.
    private static TypeGraphic typeGraphic;
    private static TypeMode typeMode;
    private static boolean isRotating;
    private static boolean computing = false;

    // Koordinaten und Maße für die graphische Ausgabeoberfläche.
    public static int mathToolGraphicAreaX;
    public static int mathToolGraphicAreaY;
    public static int mathToolGraphicAreaWidth;
    public static int mathToolGraphicAreaHeight;

    // logPosition = Index des aktuellen Befehls, den man mittels Pfeiltasten ausgegeben haben möchte.
    public static int logPosition = 0;

    public MathToolGUI() {

        initComponents();
        this.setLayout(null);
        isRotating = false;

        // Standardsprache = DE
        Expression.setLanguage(TypeLanguage.DE);

        // Formelmodus ist am Anfang aktiviert
        typeMode = TypeMode.GRAPHIC;

        // Es wird noch keine Grafik angezeigt
        typeGraphic = TypeGraphic.NONE;

        // Mindestfenstergröße festlegen
        setMinimumSize(new Dimension(1200, 670));

        // Labels ausrichten
        legendLabel = new JLabel("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</b></html>");
        legendLabel.setVisible(false);
        add(legendLabel);
        legendLabel.addMouseListener(this);

        saveLabel = new JLabel("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_SAVE") + "</b></html>");
        saveLabel.setVisible(false);
        add(saveLabel);
        saveLabel.addMouseListener(this);

        rotateLabel = new JLabel("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH") + "</b></html>");
        rotateLabel.setVisible(false);
        add(rotateLabel);
        rotateLabel.addMouseListener(this);

        // Textliches Ausgabefeld ausrichten
        mathToolTextArea = new JTextArea();
        Font mathToolAreaFont = new Font("Arial", Font.BOLD, 14);
        mathToolTextArea.setFont(mathToolAreaFont);
        mathToolTextArea.setEditable(false);
        mathToolTextArea.setLineWrap(true);
        mathToolTextArea.setWrapStyleWord(true);
        scrollPaneText = new JScrollPane(mathToolTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mathToolTextArea.setCaretPosition(mathToolTextArea.getDocument().getLength());
        add(scrollPaneText);
        scrollPaneText.setVisible(false);

        mathToolTextField = new MathToolTextField();
        mathToolTextField.setFont(new java.awt.Font("Verdana", 0, 12));
        mathToolTextField.addKeyListener(new java.awt.event.KeyAdapter() {
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
                mathToolGraphicAreaWidth, mathToolGraphicAreaHeight);
        mathToolGraphicArea.setFontSize(18);
        scrollPaneGraphic = new JScrollPane(mathToolGraphicArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneGraphic);

        // Buttons ausrichten
        cancelButton.setVisible(false);

        // 2D-Grafikobjekte initialisieren
        graphicPanel2D = new GraphicPanel2D();
        add(graphicPanel2D);
        graphicPanel2D.setVisible(false);

        graphicPanelCurves2D = new GraphicPanelCurves2D();
        add(graphicPanelCurves2D);
        graphicPanelCurves2D.setVisible(false);

        graphicPanelPolar2D = new GraphicPanelPolar2D();
        add(graphicPanelPolar2D);
        graphicPanelPolar2D.setVisible(false);

        // 3D-Grafikobjekte initialisieren
        graphicPanel3D = new GraphicPanel3D();
        add(graphicPanel3D);
        graphicPanel3D.setVisible(false);

        graphicPanelCurves3D = new GraphicPanelCurves3D();
        add(graphicPanelCurves3D);
        graphicPanelCurves3D.setVisible(false);

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

                scrollPaneText.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
                scrollPaneGraphic.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
                mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
                mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());

                mathToolGraphicAreaX = 0;
                mathToolGraphicAreaY = 0;
                mathToolGraphicAreaWidth = scrollPaneGraphic.getWidth();
                mathToolGraphicAreaHeight = scrollPaneGraphic.getHeight();

                mathToolTextField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
                inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);
                cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);
                approxButton.setBounds(10, scrollPaneText.getHeight() + 60, 130, 30);
                latexButton.setBounds(145, scrollPaneText.getHeight() + 60, 130, 30);
                clearButton.setBounds(280, scrollPaneText.getHeight() + 60, 130, 30);
                operatorChoice.setBounds(415, scrollPaneText.getHeight() + 60, 130, 30);
                commandChoice.setBounds(550, scrollPaneText.getHeight() + 60, 130, 30);

                graphicPanel2D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicPanel3D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicPanelCurves2D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicPanelCurves3D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicPanelPolar2D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);

                legendLabel.setBounds(graphicPanel3D.getX(), scrollPaneText.getHeight() + 25, 100, 25);
                saveLabel.setBounds(graphicPanel3D.getX() + 150, scrollPaneText.getHeight() + 25, 150, 25);
                rotateLabel.setBounds(graphicPanel3D.getX() + 300, scrollPaneText.getHeight() + 25, 150, 25);

                if (computingDialog != null) {
                    computingDialog = new ComputingDialogGUI(computingSwingWorker, getX(), getY(), getWidth(), getHeight());
                }

                if (!typeGraphic.equals(TypeGraphic.NONE)) {
                    scrollPaneText.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
                    scrollPaneGraphic.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
                    mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
                    mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
                    mathToolTextField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
                    inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);
                    cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);

                    mathToolGraphicAreaX = 0;
                    mathToolGraphicAreaY = 0;
                    mathToolGraphicAreaWidth = scrollPaneGraphic.getWidth();
                    mathToolGraphicAreaHeight = scrollPaneGraphic.getHeight();
                }

                mathToolGraphicArea.updateSize();

                validate();
                repaint();
            }

        });

    }

    /**
     * Getter für Mode.
     */
    public static TypeMode getMode() {
        return typeMode;
    }

    /**
     * Getter für listOfCommands.
     */
    public static ArrayList<String> getListOfCommands() {
        return listOfCommands;
    }

    /**
     * Aktualisiert die Oberfläche nach Änderung von Einstellungen.
     */
    private void refreshAPI() {

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

        // Im Darstellungsmenü den gewählten Modus fett hervorheben.
        if (typeMode.equals(TypeMode.GRAPHIC)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.BOLD, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.PLAIN, 12));
        } else if (typeMode.equals(TypeMode.TEXT)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.PLAIN, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.BOLD, 12));
        }

        // Menüeinträge aktualisieren
        menuFile.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_FILE"));
        MenuItemQuit.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_QUIT"));
        menuMathTool.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_MATHTOOL"));
        menuItemHelp.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_HELP"));
        menuItemLanguageMenu.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_LANGUAGES"));
        menuItemAbout.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_ABOUT"));
        menuItemLanguageEnglish.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_ENGLISH"));
        menuItemLanguageGerman.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_GERMAN"));
        menuItemLanguageRussian.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_RUSSIAN"));
        menuItemLanguageUkrainian.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_UKRAINIAN"));
        menuItemRepresentationMenu.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_REPRESENTATION_MODE"));
        menuItemRepresentationText.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_TEXT_MODE"));
        menuItemRepresentationFormula.setText(Translator.translateExceptionMessage("GUI_MathToolForm_FORMULA_MODE"));
        menuItemOptionsMenu.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_OPTIONS"));
        menuItemOutputOptions.setText(Translator.translateExceptionMessage("GUI_MathToolForm_MENU_OUTPUT_OPTIONS"));

        // Restliche Komponenten aktualisieren.
        // Operatorbox aktualisieren.
        ArrayList<String> newEntries = new ArrayList<>();
        newEntries.add(Translator.translateExceptionMessage("GUI_MathToolForm_OPERATOR"));
        for (int i = 1; i < operatorChoice.getModel().getSize(); i++) {
            newEntries.add((String) operatorChoice.getItemAt(i));
        }
        operatorChoice.removeAllItems();
        for (String op : newEntries) {
            operatorChoice.addItem(op);
        }
        //Befehlbox aktualisieren.
        newEntries.clear();
        newEntries.add(Translator.translateExceptionMessage("GUI_MathToolForm_COMMAND"));
        for (int i = 1; i < commandChoice.getModel().getSize(); i++) {
            newEntries.add((String) commandChoice.getItemAt(i));
        }
        commandChoice.removeAllItems();
        for (String c : newEntries) {
            commandChoice.addItem(c);
        }
        //Buttons und Labels aktualisieren
        approxButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_APPROX"));
        latexButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_LATEX_CODE"));
        clearButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_CLEAR"));
        inputButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_INPUT"));
        cancelButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_CANCEL"));
        legendLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</b></html>");
        saveLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_SAVE") + "</b></html>");
        if (isRotating) {
            rotateLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_STOP_ROTATION") + "</b></html>");
        } else {
            rotateLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH") + "</b></html>");
        }

    }

    /**
     * Falls commandName der Name eines Befehls ist, welcher Grafiken anzuzeigen
     * veranlasst, dann wird das dazu passende Panel (auf dem die Grafik
     * gezeichnet wird) angezeigt und die Ausgabefläche wird um die
     * entsprechenden Maße nach links zusammengedrückt.
     *
     * @throws ExpressionException
     * @throws EvaluationException
     */
    private void activatePanelsForGraphs(String commandName, String[] params) throws ExpressionException, EvaluationException {

        Command c = MathCommandCompiler.getCommand(commandName, params);

        //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
        scrollPaneText.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
        scrollPaneGraphic.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
        mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
        mathToolGraphicAreaX = 0;
        mathToolGraphicAreaY = 0;
        mathToolGraphicAreaWidth = scrollPaneGraphic.getWidth();
        mathToolGraphicAreaHeight = scrollPaneGraphic.getHeight();
        mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
        mathToolTextField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
        inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, inputButton.getWidth(), inputButton.getHeight());
        cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, cancelButton.getWidth(), cancelButton.getHeight());

        //Alle Grafik-Panels zunächst unsichtbar machen, dann, je nach Fall, wieder sichtbar machen.
        graphicPanel2D.setVisible(false);
        graphicPanel3D.setVisible(false);
        graphicPanelPolar2D.setVisible(false);
        graphicPanelCurves2D.setVisible(false);
        graphicPanelCurves3D.setVisible(false);
        rotateLabel.setVisible(false);
        legendLabel.setVisible(false);
        saveLabel.setVisible(false);

        graphicPanel2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanel3D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanelPolar2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanelCurves2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanelCurves3D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);

        if (commandName.equals("plot2d")) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else if (commandName.equals("plotimplicit")) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            typeGraphic = TypeGraphic.GRAPHIMPLICIT;
        } else if (commandName.equals("plot3d") || commandName.equals("tangent") && ((HashMap) c.getParams()[1]).size() == 2) {
            graphicPanel3D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH3D;
        } else if (commandName.equals("plotcurve") && c.getParams().length == 4) {
            graphicPanelCurves2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            typeGraphic = TypeGraphic.CURVE2D;
        } else if (commandName.equals("plotcurve") && c.getParams().length == 5) {
            graphicPanelCurves3D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            rotateLabel.setVisible(true);
            typeGraphic = TypeGraphic.CURVE3D;
        } else if (commandName.equals("plotpolar")) {
            graphicPanelPolar2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            typeGraphic = TypeGraphic.POLARGRAPH2D;
        } else if (commandName.equals("solve") && c.getParams().length >= 4 || commandName.equals("tangent") && ((HashMap) c.getParams()[1]).size() == 1) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else if (commandName.equals("solvedeq")) {
            graphicPanel2D.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else {
            scrollPaneText.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
            scrollPaneGraphic.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
            mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
            mathToolGraphicAreaX = 0;
            mathToolGraphicAreaY = 0;
            mathToolGraphicAreaWidth = scrollPaneGraphic.getWidth();
            mathToolGraphicAreaHeight = scrollPaneGraphic.getHeight();
            mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
            mathToolTextField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
            inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, inputButton.getWidth(), inputButton.getHeight());
            cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, cancelButton.getWidth(), cancelButton.getHeight());
            typeGraphic = TypeGraphic.NONE;
        }

        validate();
        repaint();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inputButton = new javax.swing.JButton();
        latexButton = new javax.swing.JButton();
        approxButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        operatorChoice = new javax.swing.JComboBox();
        commandChoice = new javax.swing.JComboBox();
        clearButton = new javax.swing.JButton();
        mathToolMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        MenuItemQuit = new javax.swing.JMenuItem();
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
        menuItemAbout = new javax.swing.JMenuItem();

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
        inputButton.setBounds(560, 330, 100, 30);

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

        cancelButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        cancelButton.setText("Abbruch");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelButton);
        cancelButton.setBounds(560, 300, 100, 30);

        operatorChoice.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        operatorChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Operator", "diff", "div", "fac", "gcd", "grad", "int", "laplace", "lcm", "mod", "prod", "rot", "sum", "taylor" }));
        operatorChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operatorChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(operatorChoice);
        operatorChoice.setBounds(420, 370, 130, 23);

        commandChoice.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        commandChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Befehl", "approx", "ccnf", "cdnf", "clear", "def", "deffuncs", "defvars", "eigenvalues", "eigenvectors", "euler", "expand", "ker", "latex", "pi", "plot2d", "plotimplicit", "plot3d", "plotcurve", "plotpolar", "solve", "solvedeq", "solvesystem", "table", "tangent", "taylordeq", "undef", "undefall" }));
        commandChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(commandChoice);
        commandChoice.setBounds(560, 370, 122, 23);

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

        MenuItemQuit.setText("Verlassen");
        MenuItemQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemQuitActionPerformed(evt);
            }
        });
        menuFile.add(MenuItemQuit);

        mathToolMenuBar.add(menuFile);

        menuMathTool.setText("MathTool");

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

        menuMathTool.add(menuItemOptionsMenu);

        menuItemAbout.setText("Über MathTool");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuMathTool.add(menuItemAbout);

        mathToolMenuBar.add(menuMathTool);

        setJMenuBar(mathToolMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Dient dazu, dass falls ein neuer Befehl eingegeben wird, während sich
     * eine 3D-Grafik dreht, dass diese Rotation zunächst gestoppt wird. GRUND:
     * Es kann zu Anzeigefehlern bei der 3D-Grafik kommen.
     */
    private void stopPossibleRotation() {
        if (isRotating) {
            isRotating = false;
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                graphicPanel3D.setIsRotating(false);
            } else {
                graphicPanelCurves3D.setIsRotating(false);
            }
            rotateThread.interrupt();
            rotateLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH") + "</b></html>");
        }
    }

    /**
     * Hauptmethode zum Ausführen eines Befehls.
     */
    private void executeCommand() {

        cancelButton.setVisible(true);
        inputButton.setVisible(false);
        final MathToolGUI mtf = this;

        computingSwingWorker = new SwingWorker<Void, Void>() {

            @Override
            protected void done() {
                computingTimer.cancel();
                computingDialog.setVisible(false);
                inputButton.setVisible(true);
                cancelButton.setVisible(false);
                computing = false;
                // mathToolArea nach unten scrollen lassen.
                scrollPaneText.getVerticalScrollBar().setValue(scrollPaneText.getVerticalScrollBar().getMaximum());
            }

            @Override
            protected Void doInBackground() throws Exception {

                computingDialog = new ComputingDialogGUI(computingSwingWorker, mtf.getX(), mtf.getY(), mtf.getWidth(), mtf.getHeight());

                boolean validCommand = false;

                // Leerzeichen werden im Vorfeld beseitigt.
                String input = mathToolTextField.getText().replaceAll(" ", "").toLowerCase();

                // Befehl loggen!
                listOfCommands.add(input);
                logPosition = listOfCommands.size();

                /*
                 1. Versuch: Es wird geprüft, ob die Zeile einen Befehl
                 bildet. Ja -> Befehl ausführen. Nein -> Weitere Möglichkeiten
                 prüfen.
                 */
                try {

                    String[] commandName = Expression.getOperatorAndArguments(input);
                    String[] params = Expression.getArguments(commandName[1]);

                    for (TypeCommand commandType : TypeCommand.values()) {
                        validCommand = validCommand || commandName[0].equals(commandType.toString());
                        if (validCommand) {
                            break;
                        }
                    }

                    if (validCommand) {

                        // Hinzufügen zum textlichen Ausgabefeld.
                        mathToolTextArea.append(input + "\n \n");
                        // Hinzufügen zum graphischen Ausgabefeld.
                        mathToolGraphicArea.addComponent(MathCommandCompiler.getCommand(commandName[0], params));
                        // Befehl verarbeiten.
                        MathCommandCompiler.executeCommand(input, mathToolGraphicArea, mathToolTextArea,
                                graphicPanel2D, graphicPanel3D,
                                graphicPanelCurves2D, graphicPanelCurves3D, graphicPanelPolar2D,
                                definedVars, definedFunctions);
                        // Falls es ein Grafikbefehle war -> Grafik sichtbar machen.
                        activatePanelsForGraphs(commandName[0], params);
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
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                        return null;
                    }
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (validCommand) {
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
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
                /*
                 inputIsAlgebraicExpression besagt, dass Eingabe, wenn
                 überhaupt, nur ein gültiger arithmetischer Ausdruck (und kein
                 logischer und kein Matrizenausdruck) sein kann.
                 */
                boolean inputIsAlgebraicExpression = !input.contains("&") && !input.contains("|")
                        && !input.contains(">") && !input.contains("=") && !input.contains("[")
                        && !input.contains("]") && input.length() > 0 && input.charAt(0) != '!'
                        && !input.contains("grad");

                try {

                    Expression expr = Expression.build(input, null);

                    /*
                     Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
                     Beispielsweise ist 1/0 zwar ein gültiger Ausdruck, liefert aber
                     beim Auswerten einen Fehler.
                     */
                    try {

                        Expression exprSimplified = expr.evaluate(new HashSet(definedVars.keySet()));
                        exprSimplified = exprSimplified.simplify();
                        // Hinzufügen zum textlichen Ausgabefeld.
                        mathToolTextArea.append(expr.writeExpression() + " = " + exprSimplified.writeExpression() + "\n \n");
                        // Hinzufügen zum graphischen Ausgabefeld.
                        mathToolGraphicArea.addComponent(expr, "  =  ", exprSimplified);
                        mathToolTextField.setText("");
                        return null;

                    } catch (EvaluationException e) {
                        if (inputIsAlgebraicExpression) {
                            mathToolTextArea.append(expr.writeExpression() + "\n \n");
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(expr);
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                            mathToolTextField.setText("");
                            return null;
                        }
                    } catch (Exception exception) {
                        // Falls ein unerwarteter Fehler auftritt.
                        if (inputIsAlgebraicExpression) {
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
                            return null;
                        }
                    }

                } catch (ExpressionException e) {
                    if (inputIsAlgebraicExpression) {
                        /*
                         Dann ist der Ausdruck zumindest kein logischer
                         Ausdruck -> Fehler ausgeben, welcher soeben bei
                         arithmetischen Ausdrücken geworfen wurde.
                         */
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                        return null;
                    }
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (inputIsAlgebraicExpression) {
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
                        return null;
                    }
                }

                /*
                 3. Versuch: Es wird geprüft, ob die Zeile einen gültigen
                 Matrizenausdruck bildet. Ja -> vereinfachen und ausgeben.
                 Nein -> Weitere Möglichkeiten prüfen.
                 */
                boolean inputIsMatrixExpression = !input.contains("&") && !input.contains("|")
                        && !input.contains(">") && !input.contains("=");

                try {

                    MatrixExpression matExpr = MatrixExpression.build(input, null);

                    /*
                     Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
                     Beispielsweise ist 1/0 zwar ein gültiger Ausdruck, liefert aber
                     beim Auswerten einen Fehler.
                     */
                    try {

                        MatrixExpression matExprSimplified = matExpr.simplify();
                        // Hinzufügen zum textlichen Ausgabefeld.
                        if (matExprSimplified.convertOneTimesOneMatrixToExpression() instanceof Expression) {
                            mathToolTextArea.append(matExpr.writeMatrixExpression() + " = " + ((Expression) matExprSimplified.convertOneTimesOneMatrixToExpression()).writeExpression() + "\n \n");
                        } else {
                            mathToolTextArea.append(matExpr.writeMatrixExpression() + " = " + matExprSimplified.writeMatrixExpression() + "\n \n");
                        }
                        // Hinzufügen zum graphischen Ausgabefeld.
                        mathToolGraphicArea.addComponent(matExpr, "  =  ", matExprSimplified.convertOneTimesOneMatrixToExpression());
                        mathToolTextField.setText("");
                        return null;

                    } catch (EvaluationException e) {
                        if (inputIsMatrixExpression) {
                            mathToolTextArea.append(matExpr.writeMatrixExpression() + "\n \n");
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(matExpr);
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                            mathToolTextField.setText("");
                            return null;
                        }
                    } catch (Exception exception) {
                        // Falls ein unerwarteter Fehler auftritt.
                        if (inputIsMatrixExpression) {
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
                            return null;
                        }
                    }

                } catch (ExpressionException e) {
                    if (inputIsMatrixExpression) {
                        /*
                         Dann ist der Ausdruck zumindest kein logischer
                         Ausdruck -> Fehler ausgeben, welcher soeben bei
                         arithmetischen Ausdrücken geworfen wurde.
                         */
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                        return null;
                    }
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (inputIsMatrixExpression) {
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
                        return null;
                    }
                }

                /*
                 4. Versuch: Es wird geprüft, ob die Zeile einen gültigen
                 logischen Ausdruck bildet. Ja -> vereinfachen und ausgeben.
                 */
                try {

                    LogicalExpression logExpr = LogicalExpression.build(input, null);
                    LogicalExpression logExprSimplified = logExpr.simplify();
                    // Hinzufügen zum textlichen Ausgabefeld.
                    mathToolTextArea.append(logExpr.writeLogicalExpression() + Translator.translateExceptionMessage("MTF_EQUIVALENT_TO") + logExprSimplified.writeLogicalExpression() + " \n \n");
                    // Hinzufügen zum graphischen Ausgabefeld.
                    mathToolGraphicArea.addComponent(logExpr, Translator.translateExceptionMessage("MTF_EQUIVALENT_TO"), logExprSimplified);
                    mathToolTextField.setText("");

                } catch (ExpressionException e) {
                    mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                    mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                } catch (Exception exception) {
                    mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                    mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
                }

                return null;

            }

        };

        computing = true;
        computingTimer = new Timer();

        final ImageIcon computingOwlEyesOpen = new ImageIcon(getClass().getResource("icons/LogoOwlEyesOpen.png"));
        final ImageIcon computingOwlEyesHalfOpen = new ImageIcon(getClass().getResource("icons/LogoOwlEyesHalfOpen.png"));
        final ImageIcon computingOwlEyesClosed = new ImageIcon(getClass().getResource("icons/LogoOwlEyesClosed.png"));

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
        computingSwingWorker.execute();

    }

    private void inputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputButtonActionPerformed
        // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
        stopPossibleRotation();
        executeCommand();
    }//GEN-LAST:event_inputButtonActionPerformed

    private void rotateLabelClick() {
        if (!isRotating) {
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                rotateThread = new Thread(graphicPanel3D, "rotateGraph");
                isRotating = true;
                graphicPanel3D.setIsRotating(true);
            } else {
                rotateThread = new Thread(graphicPanelCurves3D, "rotateGraph");
                isRotating = true;
                graphicPanelCurves3D.setIsRotating(true);
            }
            rotateThread.start();
            rotateLabel.setText("<html><b><u>" + Translator.translateExceptionMessage("GUI_MathToolForm_STOP_ROTATION") + "</u></b></html>");
        } else {
            isRotating = false;
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                graphicPanel3D.setIsRotating(false);
            } else {
                graphicPanelCurves3D.setIsRotating(false);
            }
            rotateThread.interrupt();
            rotateLabel.setText("<html><b><u>" + Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH") + "</u></b></html>");
        }
    }

    private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
        ArrayList<String> menuCaptions = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();
        menuCaptions.add(Translator.translateExceptionMessage("GUI_HelpDialogGUI_GENERALITIES"));
        menuCaptions.add(Translator.translateExceptionMessage("GUI_HelpDialogGUI_MATH_FORMULAS"));
        menuCaptions.add(Translator.translateExceptionMessage("GUI_HelpDialogGUI_LOGICAL_EXPRESSION"));
        menuCaptions.add(Translator.translateExceptionMessage("GUI_HelpDialogGUI_OPERATORS"));
        menuCaptions.add(Translator.translateExceptionMessage("GUI_HelpDialogGUI_COMMANDS"));
        menuCaptions.add(Translator.translateExceptionMessage("GUI_HelpDialogGUI_BUG_REPORT"));
        fileNames.add("Generalities");
        fileNames.add("Formulas");
        fileNames.add("LogicalExpressions");
        fileNames.add("Operators");
        fileNames.add("Commands");
        fileNames.add("Contact");
        HelpDialogGUI helpDialogGUI = new HelpDialogGUI(this.getX(), this.getY(),
                this.getWidth(), this.getHeight(), menuCaptions, fileNames
        );
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

    private void MenuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_MenuItemQuitActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        computingSwingWorker.cancel(true);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void operatorChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operatorChoiceActionPerformed
        if (operatorChoice.getSelectedIndex() > 0) {

            String insertedOperator = (String) operatorChoice.getSelectedItem() + "(";
            int numberOfCommata = MathToolController.getNumberOfComma((String) operatorChoice.getSelectedItem());
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
            int numberOfCommata = MathToolController.getNumberOfComma((String) commandChoice.getSelectedItem());
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
            MathCommandCompiler.executeCommand("clear()", mathToolGraphicArea, mathToolTextArea,
                    graphicPanel2D, graphicPanel3D,
                    graphicPanelCurves2D, graphicPanelCurves3D, graphicPanelPolar2D,
                    definedVars, definedFunctions);
        } catch (Exception e) {
            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + e.getMessage() + "\n \n");
            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + e.getMessage());
        }
    }//GEN-LAST:event_clearButtonActionPerformed

    private void menuItemLanguageEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageEnglishActionPerformed
        Expression.setLanguage(TypeLanguage.EN);
        refreshAPI();
    }//GEN-LAST:event_menuItemLanguageEnglishActionPerformed

    private void menuItemLanguageGermanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageGermanActionPerformed
        Expression.setLanguage(TypeLanguage.DE);
        refreshAPI();
    }//GEN-LAST:event_menuItemLanguageGermanActionPerformed

    private void menuItemLanguageRussianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageRussianActionPerformed
        Expression.setLanguage(TypeLanguage.RU);
        refreshAPI();
    }//GEN-LAST:event_menuItemLanguageRussianActionPerformed

    private void menuItemLanguageUkrainianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageUkrainianActionPerformed
        Expression.setLanguage(TypeLanguage.UA);
        refreshAPI();
    }//GEN-LAST:event_menuItemLanguageUkrainianActionPerformed

    private void menuItemRepresentationTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationTextActionPerformed
        typeMode = TypeMode.TEXT;
        scrollPaneGraphic.setVisible(false);
        scrollPaneText.setVisible(true);
        refreshAPI();
    }//GEN-LAST:event_menuItemRepresentationTextActionPerformed

    private void menuItemRepresentationFormulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationFormulaActionPerformed
        typeMode = TypeMode.GRAPHIC;
        scrollPaneGraphic.setVisible(true);
        scrollPaneText.setVisible(false);
        refreshAPI();
    }//GEN-LAST:event_menuItemRepresentationFormulaActionPerformed

    private void menuItemOutputOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOutputOptionsActionPerformed

        String simplifyOptionsTitle = Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTIONS_GROUP_NAME");
        ArrayList<String> simplifyOptions = new ArrayList<>();
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_AND_COLLECT_IF_SHORTER");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_FACTORIZE");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_ALGEBRAIC_EXPRESSIONS");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_FUNCTIONAL_RELATIONS");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_COLLECT_LOGARITHMS");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_LOGARITHMS");
        String saveButtonLabel = Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SAVE_BUTTON");
        String cancelButtonLabel = Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_CANCEL_BUTTON");

        OutputOptionsDialogGUI outputOptionsDialogGUI = new OutputOptionsDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                2, simplifyOptionsTitle, simplifyOptions, saveButtonLabel, cancelButtonLabel);
        outputOptionsDialogGUI.setVisible(true);

    }//GEN-LAST:event_menuItemOutputOptionsActionPerformed

    private void mathToolTextFieldKeyPressed(java.awt.event.KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
                stopPossibleRotation();
                executeCommand();
                break;
            case KeyEvent.VK_UP:
                if (logPosition > 0) {
                    logPosition--;
                }
                MathToolController.showLoggedCommand(mathToolTextField, logPosition);
                break;
            case KeyEvent.VK_DOWN:
                if (logPosition < listOfCommands.size() - 1) {
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
        instructions.add("<html><b><u>" + Translator.translateExceptionMessage("GUI_LegendGUI_CONTROLS") + "</u></b></html>");

        if (e.getSource() == legendLabel) {
            if (typeGraphic.equals(TypeGraphic.GRAPH2D)) {
                instructions.addAll(graphicPanel2D.getInstructions());
                if (graphicPanel2D.getIsExplicit()) {
                    for (int i = 0; i < graphicPanel2D.getExpressions().size(); i++) {
                        exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + (i + 1) + ": " + graphicPanel2D.getExpressions().get(i).writeExpression());
                    }
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanel2D.getColors(), exprs);
                } else {
                    exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_EQUATION_OF_IMPLICIT_FUNCTION")
                            + graphicPanel2D.getExpressions().get(0).writeExpression() + " = 0");
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanel2D.getColors(), exprs);
                }
            } else if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                instructions.addAll(GraphicPanel3D.getInstructions());
                exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + ": " + graphicPanel3D.getExpression().get(0).writeExpression());
                ArrayList<Color> colors = new ArrayList<>();
                colors.add(Color.blue);
                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        instructions, colors, exprs);
            } else if (typeGraphic.equals(TypeGraphic.CURVE2D)) {
                instructions.addAll(GraphicPanelCurves2D.getInstructions());
                ArrayList<Color> colors = new ArrayList<>();
                colors.add(Color.blue);
                exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_PARAMETERIZED_CURVE")
                        + "(" + graphicPanelCurves2D.getExpressions()[0]
                        + ", " + graphicPanelCurves2D.getExpressions()[1] + ")"
                );
                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        instructions, colors, exprs);
            } else if (typeGraphic.equals(TypeGraphic.CURVE3D)) {
                instructions.addAll(GraphicPanelCurves3D.getInstructions());
                ArrayList<Color> colors = new ArrayList<>();
                colors.add(Color.blue);
                exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_PARAMETERIZED_CURVE")
                        + "(" + graphicPanelCurves3D.getExpressions()[0]
                        + ", " + graphicPanelCurves3D.getExpressions()[1]
                        + ", " + graphicPanelCurves3D.getExpressions()[2] + ")"
                );
                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        instructions, colors, exprs);
            } else if (typeGraphic.equals(TypeGraphic.POLARGRAPH2D)) {
                instructions.addAll(GraphicPanelPolar2D.getInstructions());
                for (int i = 0; i < graphicPanelPolar2D.getExpressions().size(); i++) {
                    exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + (i + 1) + ": " + graphicPanelPolar2D.getExpressions().get(i).writeExpression());
                }
                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        instructions, graphicPanelPolar2D.getColors(), exprs);
            }
        } else if (e.getSource() == saveLabel) {

            if (typeGraphic.equals(TypeGraphic.GRAPH2D)) {
                saveDialog = new MathToolSaveGraphicDialog(graphicPanel2D);
            } else if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                saveDialog = new MathToolSaveGraphicDialog(graphicPanel3D);
            } else if (typeGraphic.equals(TypeGraphic.GRAPHIMPLICIT)) {
                saveDialog = new MathToolSaveGraphicDialog(graphicPanel2D);
            } else if (typeGraphic.equals(TypeGraphic.CURVE2D)) {
                saveDialog = new MathToolSaveGraphicDialog(graphicPanelCurves2D);
            } else if (typeGraphic.equals(TypeGraphic.CURVE3D)) {
                saveDialog = new MathToolSaveGraphicDialog(graphicPanelCurves3D);
            } else if (typeGraphic.equals(TypeGraphic.POLARGRAPH2D)) {
                saveDialog = new MathToolSaveGraphicDialog(graphicPanelPolar2D);
            }

        } else if (e.getSource() == rotateLabel) {
            if (rotateLabel.isVisible()){
                rotateLabelClick();
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
            legendLabel.setText("<html><b><u>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</u></b></html>");
            validate();
            repaint();
        } else if (e.getSource() == saveLabel) {
            saveLabel.setText("<html><b><u>" + Translator.translateExceptionMessage("GUI_MathToolForm_SAVE") + "</u></b></html>");
            validate();
            repaint();
        } else if (e.getSource() == rotateLabel) {
            if (isRotating) {
                rotateLabel.setText("<html><b><u>" + Translator.translateExceptionMessage("GUI_MathToolForm_STOP_ROTATION") + "</u></b></html>");
            } else {
                rotateLabel.setText("<html><b><u>" + Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH") + "</u></b></html>");
            }
            validate();
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == legendLabel) {
            legendLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</b></html>");
            validate();
            repaint();
        } else if (e.getSource() == saveLabel) {
            saveLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_SAVE") + "</b></html>");
            validate();
            repaint();
        } else if (e.getSource() == rotateLabel) {
            if (isRotating) {
                rotateLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_STOP_ROTATION") + "</b></html>");
            } else {
                rotateLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH") + "</b></html>");
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
                MathToolGUI mathToolForm = new MathToolGUI();
                mathToolForm.setVisible(true);
                mathToolForm.setBounds(50, 50, 1300, 670);
                mathToolForm.setExtendedState(MAXIMIZED_BOTH);
                mathToolForm.getContentPane().setBackground(backgroundColor);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem MenuItemQuit;
    private javax.swing.JButton approxButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JComboBox commandChoice;
    private javax.swing.JButton inputButton;
    private javax.swing.JButton latexButton;
    private javax.swing.JMenuBar mathToolMenuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemLanguageEnglish;
    private javax.swing.JMenuItem menuItemLanguageGerman;
    private javax.swing.JMenu menuItemLanguageMenu;
    private javax.swing.JMenuItem menuItemLanguageRussian;
    private javax.swing.JMenuItem menuItemLanguageUkrainian;
    private javax.swing.JMenu menuItemOptionsMenu;
    private javax.swing.JMenuItem menuItemOutputOptions;
    private javax.swing.JMenuItem menuItemRepresentationFormula;
    private javax.swing.JMenu menuItemRepresentationMenu;
    private javax.swing.JMenuItem menuItemRepresentationText;
    private javax.swing.JMenu menuMathTool;
    private javax.swing.JComboBox operatorChoice;
    // End of variables declaration//GEN-END:variables

}
