package mathtool;

import command.Command;
import command.TypeCommand;
import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import expressionbuilder.TypeGraphic;
import expressionbuilder.TypeLanguage;
import graphic.GraphicArea;
import graphic.GraphicPanel2D;
import graphic.GraphicPanel3D;
import graphic.GraphicPanelCurves2D;
import graphic.GraphicPanelCurves3D;
import graphic.GraphicPanelFormula;
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
import logicalexpressionbuilder.LogicalExpression;
import matrixexpressionbuilder.MatrixExpression;
import translator.Translator;

public class MathToolForm extends JFrame implements MouseListener {

    public static final Color backgroundColor = new Color(255, 150, 0);

    private final JLabel legendLabel;
    private final JTextArea mathToolTextArea;
    private final JScrollPane scrollPaneText;
    private final GraphicArea mathToolGraphicArea;
    private final JScrollPane scrollPaneGraphic;
    private ComputingDialogGUI computingDialog;

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
    public ArrayList<String> listOfCommands = new ArrayList<>();
    public ArrayList<GraphicPanelFormula> listOfFormulas = new ArrayList<>();

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

    // logPosition = Index des aktuellen befehls, den man mittels Pfeiltasten ausgegeben haben möchte.
    public static int logPosition = 0;

    public MathToolForm() {

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
        rotateButton.setVisible(false);
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
                inputField.requestFocus();
            }
        });

        // ComponentListener für das Ausrichten von Komponenten bei Änderung der Maße von MathTool.
        this.addComponentListener(new ComponentAdapter() {

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

                inputField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
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
                rotateButton.setBounds(graphicPanel3D.getX() + 140, scrollPaneText.getHeight() + 20, 220, 30);

                if (computingDialog != null) {
                    computingDialog = new ComputingDialogGUI(computingSwingWorker, getX(), getY(), getWidth(), getHeight());
                }

                if (!typeGraphic.equals(TypeGraphic.NONE)) {
                    scrollPaneText.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
                    scrollPaneGraphic.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
                    mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
                    mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
                    inputField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
                    inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);
                    cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);

                    mathToolGraphicAreaX = 0;
                    mathToolGraphicAreaY = 0;
                    mathToolGraphicAreaWidth = scrollPaneGraphic.getWidth();
                    mathToolGraphicAreaHeight = scrollPaneGraphic.getHeight();
                }

                validate();
                repaint();
            }

        });

    }

    /**
     * Gibt den aktuellen Modus zurück.
     */
    public static TypeMode getMode() {
        return MathToolForm.typeMode;
    }

    /**
     * Gibt den i-ten geloggten Befehl zurück.
     */
    private void showLoggedCommand(int i) {
        if (!listOfCommands.isEmpty() && listOfCommands.get(i) != null) {
            inputField.setText(listOfCommands.get(i));
        }
    }

    /**
     * Aktualisiert die Oberfläche nach Änderung von Einstellungen.
     */
    private void refreshInterface() {

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

        // Restliche Komponenten aktualisieren.
        //Operatorbox aktualisieren.
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
        //Buttons aktualisieren
        approxButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_APPROX"));
        latexButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_LATEX_CODE"));
        clearButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_CLEAR"));
        inputButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_INPUT"));
        cancelButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_CANCEL"));
        legendLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</b></html>");
        if (isRotating) {
            rotateButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_STOP_ROTATION"));
        } else {
            rotateButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH"));
        }

    }

    /**
     * Falls command_name der Name eines Befehls ist, welcher Grafiken
     * anzuzeigen veranlasst, dann wird das dazu passende Panel (auf dem die
     * Grafik gezeichnet wird) angezeigt und die Ausgabefläche wird um die
     * entsprechenden Maße nach links zusammengedrückt.
     *
     * @throws ExpressionException
     * @throws EvaluationException
     */
    private void activatePanelsForGraphs(String command_name, String[] params) throws ExpressionException, EvaluationException {

        Command c = MathCommandCompiler.getCommand(command_name, params);

        //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
        scrollPaneText.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
        scrollPaneGraphic.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
        mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
        mathToolGraphicAreaX = 0;
        mathToolGraphicAreaY = 0;
        mathToolGraphicAreaWidth = scrollPaneGraphic.getWidth();
        mathToolGraphicAreaHeight = scrollPaneGraphic.getHeight();
        mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
        inputField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
        inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, inputButton.getWidth(), inputButton.getHeight());
        cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, cancelButton.getWidth(), cancelButton.getHeight());

        //Alle Grafik-Panels zunächst unsichtbar machen, dann, je nach Fall, wieder sichtbar machen.
        graphicPanel2D.setVisible(false);
        graphicPanel3D.setVisible(false);
        graphicPanelPolar2D.setVisible(false);
        graphicPanelCurves2D.setVisible(false);
        graphicPanelCurves3D.setVisible(false);
        rotateButton.setVisible(false);
        legendLabel.setVisible(true);

        graphicPanel2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanel3D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanelPolar2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanelCurves2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicPanelCurves3D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);

        if (command_name.equals("plot2d")) {
            graphicPanel2D.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else if (command_name.equals("plot3d")) {
            graphicPanel3D.setVisible(true);
            rotateButton.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH3D;
        } else if (command_name.equals("plotcurve") && c.getParams().length == 4) {
            graphicPanelCurves2D.setVisible(true);
            typeGraphic = TypeGraphic.CURVE2D;
        } else if (command_name.equals("plotcurve") && c.getParams().length == 5) {
            graphicPanelCurves3D.setVisible(true);
            rotateButton.setVisible(true);
            typeGraphic = TypeGraphic.CURVE3D;
        } else if (command_name.equals("plotpolar")) {
            graphicPanelPolar2D.setVisible(true);
            typeGraphic = TypeGraphic.POLARGRAPH2D;
        } else if ((command_name.equals("solve") && c.getParams().length >= 4) || (command_name.equals("tangent") && ((HashMap) c.getParams()[1]).size() == 1)) {
            graphicPanel2D.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else if (command_name.equals("solvedeq")) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(false);
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
            inputField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
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
        inputField = new javax.swing.JTextField();
        rotateButton = new javax.swing.JButton();
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

        inputField.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        inputField.setText("plot2d(x^2+y^2=1,-2,2,-2,2)");
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldKeyPressed(evt);
            }
        });
        getContentPane().add(inputField);
        inputField.setBounds(10, 336, 540, 20);

        rotateButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        rotateButton.setText("Graphen rotieren lassen");
        rotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(rotateButton);
        rotateButton.setBounds(10, 410, 205, 30);

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
        operatorChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Operator", "diff", "div", "fac", "gcd", "int", "laplace", "lcm", "mod", "prod", "sum", "taylor" }));
        operatorChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operatorChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(operatorChoice);
        operatorChoice.setBounds(420, 370, 130, 23);

        commandChoice.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        commandChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Befehl", "approx", "ccnf", "cdnf", "clear", "def", "deffuncs", "defvars", "eigenvalues", "eigenvectors", "euler", "expand", "ker", "latex", "pi", "plot2d", "plot3d", "plotcurve", "plotpolar", "solve", "solvedeq", "table", "tangent", "taylordeq", "undef", "undefall" }));
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
            rotateButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_ROTATE_GRAPH"));
        }
    }

    /**
     * Hauptmethode zum Ausführen eines Befehls.
     */
    private void executeCommand() {

        cancelButton.setVisible(true);
        inputButton.setVisible(false);
        final MathToolForm mtf = this;

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
                String input = inputField.getText().replaceAll(" ", "");

                // Alles zu Kleinbuchstaben machen.
                char currentChar;
                int lengthInput = input.length();
                for (int i = 0; i < lengthInput; i++) {
                    currentChar = input.charAt(i);
                    //Falls es ein Großbuchstabe ist -> zu Kleinbuchstaben machen
                    if (((int) currentChar >= 65) && ((int) currentChar <= 90)) {
                        currentChar = (char) ((int) currentChar + 32);  //Macht Großbuchstaben zu Kleinbuchstaben
                        input = input.substring(0, i) + currentChar + input.substring(i + 1, lengthInput);
                    }
                }

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
                        inputField.setText("");

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
                        && !input.contains("]") && input.length() > 0 && input.charAt(0) != '!';

                try {

                    Expression expr = Expression.build(input, new HashSet());

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
                        inputField.setText("");
                        return null;

                    } catch (EvaluationException e) {
                        if (inputIsAlgebraicExpression) {
                            mathToolTextArea.append(expr.writeExpression() + "\n \n");
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(expr);
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                            inputField.setText("");
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

                    MatrixExpression matExpr = MatrixExpression.build(input, new HashSet());

                    /*
                     Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
                     Beispielsweise ist 1/0 zwar ein gültiger Ausdruck, liefert aber
                     beim Auswerten einen Fehler.
                     */
                    try {

                        MatrixExpression matExprSimplified = matExpr.simplify();
                        // Hinzufügen zum textlichen Ausgabefeld.
                        mathToolTextArea.append(matExpr.writeMatrixExpression() + " = " + matExprSimplified.writeMatrixExpression() + "\n \n");
                        // Hinzufügen zum graphischen Ausgabefeld.
                        mathToolGraphicArea.addComponent(matExpr, "  =  ", matExprSimplified.convertOneTimesOneMatrixToExpression());
                        inputField.setText("");
                        return null;

                    } catch (EvaluationException e) {
                        if (inputIsMatrixExpression) {
                            mathToolTextArea.append(matExpr.writeMatrixExpression() + "\n \n");
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(matExpr);
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                            inputField.setText("");
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

                    LogicalExpression logExpr = LogicalExpression.build(input, new HashSet());
                    LogicalExpression logExprSimplified = logExpr.simplify();
                    // Hinzufügen zum textlichen Ausgabefeld.
                    mathToolTextArea.append(logExpr.writeLogicalExpression() + Translator.translateExceptionMessage("MTF_EQUIVALENT_TO") + logExprSimplified.writeLogicalExpression() + " \n \n");
                    // Hinzufügen zum graphischen Ausgabefeld.
                    mathToolGraphicArea.addComponent(logExpr, Translator.translateExceptionMessage("MTF_EQUIVALENT_TO"), logExprSimplified);
                    inputField.setText("");

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


    private void rotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateButtonActionPerformed
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
            rotateButton.setText("Rotation stoppen");
        } else {
            isRotating = false;
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                graphicPanel3D.setIsRotating(false);
            } else {
                graphicPanelCurves3D.setIsRotating(false);
            }
            rotateThread.interrupt();
            rotateButton.setText("Graphen rotieren lassen");
        }
    }//GEN-LAST:event_rotateButtonActionPerformed

    private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
        HelpDialogGUI helpDialogGUI = new HelpDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        helpDialogGUI.setVisible(true);
    }//GEN-LAST:event_menuItemHelpActionPerformed

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        DevelopersDialogGUI aboutMathToolGUI = new DevelopersDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        aboutMathToolGUI.setVisible(true);
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void approxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approxButtonActionPerformed
        // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
        stopPossibleRotation();
        inputField.setText("approx(" + inputField.getText() + ")");
        executeCommand();
    }//GEN-LAST:event_approxButtonActionPerformed

    private void latexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_latexButtonActionPerformed
        // Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese aktiv ist.
        stopPossibleRotation();
        inputField.setText("latex(" + inputField.getText() + ")");
        executeCommand();
    }//GEN-LAST:event_latexButtonActionPerformed

    private void MenuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_MenuItemQuitActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        computingSwingWorker.cancel(true);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Berechnet für Operatoren und Befehle die Mindestanzahl der benötigten
     * Kommata bei einer gültigen Eingabe.
     */
    private int getNumberOfComma(String operatorOrCommandName) {

        if (operatorOrCommandName.equals("diff")) {
            return 1;
        }
        if (operatorOrCommandName.equals("gcd")) {
            return 1;
        }
        if (operatorOrCommandName.equals("int")) {
            return 1;
        }
        if (operatorOrCommandName.equals("lcm")) {
            return 1;
        }
        if (operatorOrCommandName.equals("mod")) {
            return 1;
        }
        if (operatorOrCommandName.equals("prod")) {
            return 3;
        }
        if (operatorOrCommandName.equals("sum")) {
            return 3;
        }
        if (operatorOrCommandName.equals("taylor")) {
            return 3;
        }

        if (operatorOrCommandName.equals("plot2d")) {
            return 2;
        }
        if (operatorOrCommandName.equals("plot3d")) {
            return 4;
        }
        if (operatorOrCommandName.equals("plotcurve")) {
            return 2;
        }
        if (operatorOrCommandName.equals("plotpolar")) {
            return 2;
        }
        if (operatorOrCommandName.equals("solvedeq")) {
            return 5;
        }
        if (operatorOrCommandName.equals("tangent")) {
            return 1;
        }
        if (operatorOrCommandName.equals("taylordeq")) {
            return 5;
        }

        // Default-Case! Alle Operatoren/Befehle, welche höchstens ein Argument benötigen.
        return 0;

    }

    private void operatorChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operatorChoiceActionPerformed
        if (operatorChoice.getSelectedIndex() > 0) {

            String insertedOperator = (String) operatorChoice.getSelectedItem() + "(";
            int numberOfCommata = getNumberOfComma((String) operatorChoice.getSelectedItem());
            for (int i = 0; i < numberOfCommata; i++) {
                insertedOperator = insertedOperator + ",";
            }
            insertedOperator = insertedOperator + ")";

            inputField.replaceSelection(insertedOperator);
            inputField.setSelectionStart(inputField.getSelectionStart() - numberOfCommata - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());
            operatorChoice.setSelectedIndex(0);

        }
        inputField.requestFocus();
    }//GEN-LAST:event_operatorChoiceActionPerformed

    private void commandChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commandChoiceActionPerformed
        if (commandChoice.getSelectedIndex() > 0) {

            String insertedCommand = (String) commandChoice.getSelectedItem() + "(";
            int numberOfCommata = getNumberOfComma((String) commandChoice.getSelectedItem());
            for (int i = 0; i < numberOfCommata; i++) {
                insertedCommand = insertedCommand + ",";
            }
            insertedCommand = insertedCommand + ")";

            inputField.setText(insertedCommand);
            inputField.setSelectionStart(inputField.getSelectionStart() - numberOfCommata - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());
            commandChoice.setSelectedIndex(0);

        }
        inputField.requestFocus();
    }//GEN-LAST:event_commandChoiceActionPerformed

    private void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyPressed
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
                if (logPosition == listOfCommands.size()) {
                    logPosition--;
                }
                showLoggedCommand(logPosition);
                break;

            case KeyEvent.VK_DOWN:
                if (logPosition < listOfCommands.size() - 1) {
                    logPosition++;
                }
                showLoggedCommand(logPosition);
                break;

            case KeyEvent.VK_ESCAPE:
                if (computing) {
                    computingSwingWorker.cancel(true);
                } else {
                    inputField.setText("");
                }
                break;
        }
    }//GEN-LAST:event_inputFieldKeyPressed

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
        refreshInterface();
    }//GEN-LAST:event_menuItemLanguageEnglishActionPerformed

    private void menuItemLanguageGermanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageGermanActionPerformed
        Expression.setLanguage(TypeLanguage.DE);
        refreshInterface();
    }//GEN-LAST:event_menuItemLanguageGermanActionPerformed

    private void menuItemLanguageRussianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageRussianActionPerformed
        Expression.setLanguage(TypeLanguage.RU);
        refreshInterface();
    }//GEN-LAST:event_menuItemLanguageRussianActionPerformed

    private void menuItemLanguageUkrainianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageUkrainianActionPerformed
        Expression.setLanguage(TypeLanguage.UA);
        refreshInterface();
    }//GEN-LAST:event_menuItemLanguageUkrainianActionPerformed

    private void menuItemRepresentationTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationTextActionPerformed
        typeMode = TypeMode.TEXT;
        scrollPaneGraphic.setVisible(false);
        scrollPaneText.setVisible(true);
        refreshInterface();
    }//GEN-LAST:event_menuItemRepresentationTextActionPerformed

    private void menuItemRepresentationFormulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationFormulaActionPerformed
        typeMode = TypeMode.GRAPHIC;
        scrollPaneGraphic.setVisible(true);
        scrollPaneText.setVisible(false);
        refreshInterface();
    }//GEN-LAST:event_menuItemRepresentationFormulaActionPerformed

    @Override
    public void mouseClicked(MouseEvent e) {

        LegendGUI legendGUI = null;

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
                exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + ": " + graphicPanel3D.getExpression().writeExpression());
                ArrayList<Color> colors = new ArrayList<>();
                colors.add(Color.blue);
                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        instructions, colors, exprs);
            } else if (typeGraphic.equals(TypeGraphic.CURVE2D)) {
//                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
//                        GraphicPanelCurves2D.getInstructions(), graphicMethodsCurves2D.getExpressions());
            } else if (typeGraphic.equals(TypeGraphic.CURVE3D)) {
//                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
//                        GraphicPanelCurves3D.getInstructions(), graphicPanelCurves3D.getExpressions());
            } else if (typeGraphic.equals(TypeGraphic.POLARGRAPH2D)) {
//                legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
//                        GraphicPanelPolar2D.getInstructions(), graphicMethodsPolar2D.getColors(), graphicMethodsPolar2D.getExpressions());
            }
        }

        if (legendGUI != null) {
            legendGUI.setVisible(true);
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
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == legendLabel) {
            legendLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</b></html>");
            validate();
            repaint();
        }
    }

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MathToolForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MathToolForm mathToolForm = new MathToolForm();
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
    private javax.swing.JTextField inputField;
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
    private javax.swing.JMenuItem menuItemRepresentationFormula;
    private javax.swing.JMenu menuItemRepresentationMenu;
    private javax.swing.JMenuItem menuItemRepresentationText;
    private javax.swing.JMenu menuMathTool;
    private javax.swing.JComboBox operatorChoice;
    private javax.swing.JButton rotateButton;
    // End of variables declaration//GEN-END:variables

}
