package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import expressionbuilder.TypeGraphic;
import expressionbuilder.TypeLanguage;
import LogicalExpressionBuilder.LogicalExpression;
import Translator.Translator;
import graphic.GraphicMethods2D;
import graphic.GraphicMethods3D;
import graphic.GraphicMethodsCurves2D;
import graphic.GraphicMethodsCurves3D;
import graphic.GraphicMethodsPolar2D;
import graphic.GraphicArea;
import graphic.GraphicPresentationOfFormula;
import command.Command;
import graphic.TypeGraphicFormula;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MathToolForm extends JFrame implements MouseListener {

    private Thread threadRotate;
    private boolean startRotate;
    private TypeGraphic typeGraphic;
    private static TypeMode typeMode;
    private static int fontSize;
    private boolean computing = false;
    private SwingWorker<Void, Void> computingSwingWorker;
    private Timer computingTimer;
    private ComputingDialogGUI computingDialog;
    private final JLabel legendLabel;

    JTextArea mathToolTextArea;
    JScrollPane scrollPaneText;
    GraphicArea mathToolGraphicArea;
    JScrollPane scrollPaneGraphic;
    JEditorPane helpArea;

    GraphicMethods2D graphicMethods2D;
    GraphicMethods3D graphicMethods3D;
    GraphicMethodsCurves2D graphicMethodsCurves2D;
    GraphicMethodsCurves3D graphicMethodsCurves3D;
    GraphicMethodsPolar2D graphicMethodsPolar2D;
    ArrayList<GraphicPresentationOfFormula> ListOfFormulas;

    /**
     * Diese Objekte werden im Laufe des Programms erweitert. Sie enthalten die
     * im Laufe des Programms definierten Variablen und Funktionen.
     */
    static HashMap<String, Expression> definedVars = new HashMap<>();
    static HashMap<String, Expression> definedFunctions = new HashMap<>();
    public ArrayList<String> listOfCommands = new ArrayList<>();
    public ArrayList<GraphicPresentationOfFormula> listOfFormulas = new ArrayList<>();

    /**
     * log_position = Index des aktuellen befehls, den man mittels Pfeiltasten
     * ausgegeben haben möchte.
     */
    public int log_position = 0;

    /**
     * Konstruktor
     */
    public MathToolForm() {

        initComponents();
        this.setLayout(null);
        this.startRotate = false;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                inputField.requestFocus();
            }
        });

        /**
         * Standardsprache = DE
         */
        Expression.setLanguage(TypeLanguage.DE);

        /**
         * Formelmodus ist am Anfang aktiviert
         */
        typeMode = TypeMode.GRAPHIC;

        /**
         * Schriftgröße festlegen
         */
        fontSize = 20;

        /**
         * Es wird noch keine Grafik angezeigt
         */
        typeGraphic = TypeGraphic.NONE;

        /**
         * Labels ausrichten
         */
        legendLabel = new JLabel("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</b></html>");
        legendLabel.setVisible(false);
        add(legendLabel);
        legendLabel.addMouseListener(this);

        /**
         * Textliches Ausgabefeld ausrichten
         */
        mathToolTextArea = new JTextArea();
        Font mathToolAreaFont = new Font("Arial", Font.BOLD, 12);
        mathToolTextArea.setFont(mathToolAreaFont);
        mathToolTextArea.setEditable(false);
        mathToolTextArea.setLineWrap(true);
        mathToolTextArea.setWrapStyleWord(true);
        scrollPaneText = new JScrollPane(mathToolTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mathToolTextArea.setCaretPosition(mathToolTextArea.getDocument().getLength());
        add(scrollPaneText);
        scrollPaneText.setVisible(false);

        /**
         * Graphisches Ausgabefeld ausrichten
         */
        mathToolGraphicArea = new GraphicArea(10, 10, this.getWidth() - 40, this.getHeight() - 170);
        scrollPaneGraphic = new JScrollPane(mathToolGraphicArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneGraphic);

        /**
         * Buttons ausrichten
         */
        rotateButton.setVisible(false);
        cancelButton.setVisible(false);

        /**
         * 2D-Grafikobjekte initialisieren
         */
        graphicMethods2D = new GraphicMethods2D();
        add(graphicMethods2D);
        graphicMethods2D.setVisible(false);

        graphicMethodsCurves2D = new GraphicMethodsCurves2D();
        add(graphicMethodsCurves2D);
        graphicMethodsCurves2D.setVisible(false);

        graphicMethodsPolar2D = new GraphicMethodsPolar2D();
        add(graphicMethodsPolar2D);
        graphicMethodsPolar2D.setVisible(false);

        /**
         * 3D-Grafikobjekte initialisieren
         */
        graphicMethods3D = new GraphicMethods3D();
        add(graphicMethods3D);
        graphicMethods3D.setVisible(false);

        graphicMethodsCurves3D = new GraphicMethodsCurves3D();
        add(graphicMethodsCurves3D);
        graphicMethodsCurves3D.setVisible(false);

        /**
         *
         */
        ListOfFormulas = new ArrayList<>();

        validate();
        repaint();

        /**
         * ComponentListener für das Ausrichten von Komponenten bei Änderung der
         * Maße von MathTool.
         */
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

                scrollPaneText.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
                mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
                scrollPaneGraphic.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
                mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());

                inputField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
                inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);
                cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, 140, 30);
                approxButton.setBounds(10, scrollPaneText.getHeight() + 60, 130, 30);
                latexButton.setBounds(145, scrollPaneText.getHeight() + 60, 130, 30);
                clearButton.setBounds(280, scrollPaneText.getHeight() + 60, 130, 30);
                operatorChoice.setBounds(415, scrollPaneText.getHeight() + 60, 130, 30);
                commandChoice.setBounds(550, scrollPaneText.getHeight() + 60, 130, 30);

                graphicMethods2D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicMethods3D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicMethodsCurves2D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicMethodsCurves3D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);
                graphicMethodsPolar2D.setBounds(scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);

                legendLabel.setBounds(graphicMethods3D.getX(), scrollPaneText.getHeight() + 25, 100, 25);
                rotateButton.setBounds(graphicMethods3D.getX() + 140, scrollPaneText.getHeight() + 20, 220, 30);

                if (computingDialog != null) {
                    computingDialog = new ComputingDialogGUI(computingSwingWorker, getX(), getY(), getWidth(), getHeight());
                }

                if (!typeGraphic.equals(TypeGraphic.NONE)) {
                    scrollPaneText.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
                    mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
                    mathToolGraphicArea.setBounds(0, 0, scrollPaneGraphic.getWidth(), scrollPaneGraphic.getHeight());
                    inputField.setBounds(inputField.getX(), inputField.getY(), inputField.getWidth() - 550, 30);
                    inputButton.setBounds(inputButton.getX() - 510, inputButton.getY(), inputButton.getWidth(), inputButton.getHeight());
                    cancelButton.setBounds(cancelButton.getX() - 510, cancelButton.getY(), cancelButton.getWidth(), cancelButton.getHeight());
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
     * Gibt den aktuellen Modus zurück.
     */
    public static int getFontSize() {
        return MathToolForm.fontSize;
    }

    /**
     * Gibt den i-ten geloggten Befehl zurück.
     */
    private void showLoggedCommand(int i) {
        inputField.setText(listOfCommands.get(i));
    }

    /**
     * Aktualisiert die Oberfläche nach Änderung von Einstellungen.
     */
    private void refreshInterface() {

        /**
         * Im Sprachmenü die gewählte Sprache fett hervorheben.
         */
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

        /**
         * Im Darstellungsmenü den gewählten Modus fett hervorheben.
         */
        if (typeMode.equals(TypeMode.GRAPHIC)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.BOLD, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.PLAIN, 12));
        } else if (typeMode.equals(TypeMode.TEXT)) {
            menuItemRepresentationFormula.setFont(new Font(menuItemRepresentationFormula.getFont().getFamily(), Font.PLAIN, 12));
            menuItemRepresentationText.setFont(new Font(menuItemRepresentationText.getFont().getFamily(), Font.BOLD, 12));
        }

        /**
         * Menüeinträge aktualisieren
         */
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

        /**
         * Restliche Komponenten aktualisieren.
         */
        approxButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_APPROX"));
        latexButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_LATEX_CODE"));
        clearButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_CLEAR"));
        inputButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_INPUT"));
        cancelButton.setText(Translator.translateExceptionMessage("GUI_MathToolForm_CANCEL"));
        legendLabel.setText("<html><b>" + Translator.translateExceptionMessage("GUI_MathToolForm_LEGEND") + "</b></html>");
        if (startRotate) {
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
        mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
        mathToolGraphicArea.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
        inputField.setBounds(10, scrollPaneText.getHeight() + 20, scrollPaneText.getWidth() - 150, 30);
        inputButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, inputButton.getWidth(), inputButton.getHeight());
        cancelButton.setBounds(mathToolTextArea.getWidth() - 130, scrollPaneText.getHeight() + 20, cancelButton.getWidth(), cancelButton.getHeight());

        //Alle Grafik-Panels zunächst unsichtbar machen, dann, je nach Fall, wieder sichtbar machen.
        graphicMethods2D.setVisible(false);
        graphicMethods3D.setVisible(false);
        graphicMethodsPolar2D.setVisible(false);
        graphicMethodsCurves2D.setVisible(false);
        graphicMethodsCurves3D.setVisible(false);
        rotateButton.setVisible(false);
        legendLabel.setVisible(true);

        graphicMethods2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicMethods3D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicMethodsPolar2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicMethodsCurves2D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);
        graphicMethodsCurves3D.setBounds(scrollPaneText.getWidth() + 20, scrollPaneText.getHeight() - 490, 500, 500);

        if (command_name.equals("plot2d")) {
            graphicMethods2D.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else if (command_name.equals("plot3d")) {
            graphicMethods3D.setVisible(true);
            rotateButton.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH3D;
        } else if (command_name.equals("plotcurve") && c.getParams().length == 4) {
            graphicMethodsCurves2D.setVisible(true);
            typeGraphic = TypeGraphic.CURVE2D;
        } else if (command_name.equals("plotcurve") && c.getParams().length == 5) {
            graphicMethodsCurves3D.setVisible(true);
            rotateButton.setVisible(true);
            typeGraphic = TypeGraphic.CURVE3D;
        } else if (command_name.equals("plotpolar")) {
            graphicMethodsPolar2D.setVisible(true);
            typeGraphic = TypeGraphic.POLARGRAPH2D;
        } else if ((command_name.equals("solve") && c.getParams().length >= 4) || (command_name.equals("tangent") && ((HashMap) c.getParams()[1]).size() == 1)) {
            graphicMethods2D.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else if (command_name.equals("solvedeq")) {
            graphicMethods2D.setVisible(true);
            legendLabel.setVisible(false);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else {
            scrollPaneText.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
            mathToolTextArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
            mathToolGraphicArea.setBounds(0, 0, scrollPaneText.getWidth(), scrollPaneText.getHeight());
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
        setTitle("MathTool - Mathematical Tool for Analysis and Numerical Computation");
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
        inputButton.setBounds(518, 335, 100, 30);

        inputField.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        inputField.setText("tangent(x+y,x=1,y=2)");
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldKeyPressed(evt);
            }
        });
        getContentPane().add(inputField);
        inputField.setBounds(10, 336, 490, 20);

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
        cancelButton.setBounds(520, 300, 100, 30);

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
        commandChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Befehl", "approx", "clear", "def", "deffuncs", "defvars", "dnf", "euler", "latex", "pi", "plot2d", "plot3d", "plotcurve", "plotpolar", "solve", "solvedeq", "table", "tangent", "taylordeq", "undef", "undefall" }));
        commandChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(commandChoice);
        commandChoice.setBounds(560, 370, 96, 23);

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
        if (startRotate) {
            startRotate = false;
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                graphicMethods3D.setStartRotate(false);
            } else {
                graphicMethodsCurves3D.setStartRotate(false);
            }
            threadRotate.interrupt();
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
                /**
                 * mathToolArea nach unten scrollen lassen.
                 */
                scrollPaneText.getVerticalScrollBar().setValue(scrollPaneText.getVerticalScrollBar().getMaximum());
            }

            @Override
            protected Void doInBackground() throws Exception {

                computingDialog = new ComputingDialogGUI(computingSwingWorker, mtf.getX(), mtf.getY(), mtf.getWidth(), mtf.getHeight());

                boolean valid_command = false;

                /**
                 * Leerzeichen werden im Vorfeld beseitigt.
                 */
                String command = inputField.getText().replaceAll(" ", "");

                /**
                 * Alles zu Kleinbuchstaben machen.
                 */
                char part;
                int n = command.length();
                for (int i = 0; i < n; i++) {
                    part = command.charAt(i);
                    //Falls es ein Großbuchstabe ist -> zu Kleinbuchstaben machen
                    if (((int) part >= 65) && ((int) part <= 90)) {
                        part = (char) ((int) part + 32);  //Macht Großbuchstaben zu Kleinbuchstaben
                        command = command.substring(0, i) + part + command.substring(i + 1, n);
                    }
                }

                /**
                 * Befehl loggen!
                 */
                listOfCommands.add(command);
                log_position = listOfCommands.size();

                /**
                 * 1. Versuch: Es wird geprüft, ob die Zeile einen Befehl
                 * bildet. Ja -> Befehl ausführen. Nein -> Weitere Möglichkeiten
                 * prüfen.
                 */
                try {

                    String[] command_name = Expression.getOperatorAndArguments(command);
                    String[] params = Expression.getArguments(command_name[1]);

                    for (String c : MathCommandCompiler.commands) {
                        valid_command = valid_command || command_name[0].equals(c);
                    }

                    if (valid_command) {

                        /**
                         * Hinzufügen zum textlichen Ausgabefeld.
                         */
                        mathToolTextArea.append(command + "\n \n");
                        /**
                         * Hinzufügen zum graphischen Ausgabefeld.
                         */

                        GraphicPresentationOfFormula f = new GraphicPresentationOfFormula();
                        mathToolGraphicArea.add(f);
                        f.setCommand(MathCommandCompiler.getCommand(command_name[0], params));
                        f.setTypeGraphicFormula(TypeGraphicFormula.COMMAND);
                        f.initialize(getFontSize());
                        ListOfFormulas.add(f);
                        f.drawFormula();
                        mathToolGraphicArea.setPosition(f);
                        validate();
                        repaint();

                        /**
                         * Befehl verarbeiten.
                         */
                        MathCommandCompiler.executeCommand(command, mathToolGraphicArea, mathToolTextArea,
                                graphicMethods2D, graphicMethods3D,
                                graphicMethodsCurves2D, graphicMethodsCurves3D, graphicMethodsPolar2D,
                                definedVars, definedFunctions);
                        /**
                         * Falls es ein Grafikbefehle war -> Grafik sichtbar
                         * machen.
                         */
                        activatePanelsForGraphs(command_name[0], params);
                        inputField.setText("");

                    }

                } catch (ExpressionException | EvaluationException e) {
                    /**
                     * Falls es ein gültiger Befehl war (unabhängig davon, ob
                     * dieser Fehler enthielt oder nicht) -> abbrechen und NICHT
                     * weiter prüfen, ob es sich um einen Ausdruck handeln
                     * könnte.
                     */
                    if (valid_command) {
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                        return null;
                    }
                }

                /**
                 * Falls es ein gültiger Befehl war (unabhängig davon, ob dieser
                 * Fehler enthield oder nicht) -> abbrechen und NICHT weiter
                 * prüfen, ob es sich um einen Ausdruck handeln könnte.
                 */
                if (valid_command) {
                    return null;
                }

                /**
                 * 2. Versuch: Es wird geprüft, ob die Zeile einen
                 * mathematischen Ausdruck bildet. Ja -> Vereinfachen und
                 * ausgeben. Nein -> Weitere Möglichkeiten prüfen.
                 */
                /**
                 * input_is_arithmetic_expression besagt, dass Eingabe, wenn
                 * überhaupt, nur ein gültiger arithmetischer (und kein
                 * logischer) Ausdruck sein kann.
                 */
                boolean input_is_arithmetic_expression = !command.contains("&") && !command.contains("|")
                        && !command.contains(">") && !command.contains("=");

                try {

                    Expression expr = Expression.build(command, new HashSet());

                    /**
                     * Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
                     * Z.B. 1/0 ist zwar ein gültiger Ausdruck, liefert aber
                     * beim Auswerten einen Fehler.
                     */
                    try {
                        Expression expr_simplified = expr.evaluate(new HashSet(definedVars.keySet()));
                        expr_simplified = expr_simplified.simplify();
                        mathToolTextArea.append(expr.writeFormula(true) + " = " + expr_simplified.writeFormula(true) + "\n \n");
                        inputField.setText("");
                        return null;
                    } catch (EvaluationException e) {
                        if (input_is_arithmetic_expression) {
                            mathToolTextArea.append(expr.writeFormula(true) + "\n \n");
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            return null;
                        }
                    }

                } catch (ExpressionException e) {
                    if (input_is_arithmetic_expression) {
                        /**
                         * Dann ist der Ausdruck zumindest kein logischer
                         * Ausdruck -> Fehler ausgeben, welcher soeben bei
                         * arithmetischen Ausdrücken geworfen wurde.
                         */
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                        return null;
                    }
                }

                /**
                 * Nun wird geprüft, ob es sich bei s um einen gültigen
                 * logischen Ausdruck handelt. Ja -> vereinfachen und ausgeben.
                 */
                try {
                    LogicalExpression log_expr = LogicalExpression.build(command, new HashSet());
                    LogicalExpression log_expr_simplified = log_expr.simplify();
                    mathToolTextArea.append(log_expr.writeFormula() + Translator.translateExceptionMessage("MTF_EQUIVALENT_TO") + log_expr_simplified.writeFormula() + " \n \n");
                    inputField.setText("");

                } catch (ExpressionException e) {
                    mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                }

                return null;

            }

        };

        computing = true;
        computingTimer = new Timer();

        final ImageIcon computingOwlEyesOpen = new ImageIcon(getClass().getResource("icons/LogoOwlEyesOpen.png"));
        final ImageIcon computingOwlEyesHalfOpen = new ImageIcon(getClass().getResource("icons/LogoOwlEyesHalfOpen.png"));
        final ImageIcon computingOwlEyesClosed = new ImageIcon(getClass().getResource("icons/LogoOwlEyesClosed.png"));

        /**
         * Es folgen die TimerTasks, welche die Eule veranlassen, mit den Augen
         * zu zwinkern.
         */
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
        /**
         * Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese
         * aktiv ist.
         */
        stopPossibleRotation();
        executeCommand();
    }//GEN-LAST:event_inputButtonActionPerformed


    private void rotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateButtonActionPerformed
        if (!startRotate) {
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                this.threadRotate = new Thread(graphicMethods3D, "rotateGraph");
                startRotate = true;
                graphicMethods3D.setStartRotate(true);
            } else {
                this.threadRotate = new Thread(graphicMethodsCurves3D, "rotateGraph");
                startRotate = true;
                graphicMethodsCurves3D.setStartRotate(true);
            }
            threadRotate.start();
            rotateButton.setText("Rotation stoppen");
        } else {
            startRotate = false;
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                graphicMethods3D.setStartRotate(false);
            } else {
                graphicMethodsCurves3D.setStartRotate(false);
            }
            threadRotate.interrupt();
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
        /**
         * Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese
         * aktiv ist.
         */
        stopPossibleRotation();
        inputField.setText("approx(" + inputField.getText() + ")");
        executeCommand();
    }//GEN-LAST:event_approxButtonActionPerformed

    private void latexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_latexButtonActionPerformed
        /**
         * Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese
         * aktiv ist.
         */
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
    private int getNumberOfComma(String s) {

        if (s.equals("diff")) {
            return 1;
        }
        if (s.equals("gcd")) {
            return 1;
        }
        if (s.equals("int")) {
            return 1;
        }
        if (s.equals("lcm")) {
            return 1;
        }
        if (s.equals("mod")) {
            return 1;
        }
        if (s.equals("prod")) {
            return 3;
        }
        if (s.equals("sum")) {
            return 3;
        }
        if (s.equals("taylor")) {
            return 3;
        }

        if (s.equals("plot2d")) {
            return 2;
        }
        if (s.equals("plot3d")) {
            return 4;
        }
        if (s.equals("plotcurve")) {
            return 2;
        }
        if (s.equals("plotpolar")) {
            return 2;
        }
        if (s.equals("solvedeq")) {
            return 5;
        }
        if (s.equals("tangent")) {
            return 1;
        }
        if (s.equals("taylordeq")) {
            return 5;
        }

        /**
         * Default-Case! Alle Operatoren/Befehle, welche (mindestens) genau ein
         * Argument benötigen.
         */
        return 0;

    }

    private void operatorChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operatorChoiceActionPerformed
        if (operatorChoice.getSelectedIndex() > 0) {

            String inserted_operator = (String) operatorChoice.getSelectedItem() + "(";
            int number_of_commata = getNumberOfComma((String) operatorChoice.getSelectedItem());
            for (int i = 0; i < number_of_commata; i++) {
                inserted_operator = inserted_operator + ",";
            }
            inserted_operator = inserted_operator + ")";

            inputField.replaceSelection(inserted_operator);
            inputField.setSelectionStart(inputField.getSelectionStart() - number_of_commata - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());
            operatorChoice.setSelectedIndex(0);

        }
        inputField.requestFocus();
    }//GEN-LAST:event_operatorChoiceActionPerformed

    private void commandChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commandChoiceActionPerformed
        if (commandChoice.getSelectedIndex() > 0) {

            String inserted_command = (String) commandChoice.getSelectedItem() + "(";
            int number_of_commata = getNumberOfComma((String) commandChoice.getSelectedItem());
            for (int i = 0; i < number_of_commata; i++) {
                inserted_command = inserted_command + ",";
            }
            inserted_command = inserted_command + ")";

            inputField.setText(inserted_command);
            inputField.setSelectionStart(inputField.getSelectionStart() - number_of_commata - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());
            commandChoice.setSelectedIndex(0);

        }
        inputField.requestFocus();
    }//GEN-LAST:event_commandChoiceActionPerformed

    private void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                /**
                 * Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls
                 * diese aktiv ist.
                 */
                stopPossibleRotation();

                /**
                 * try {                  *
                 * GraphicPresentationOfFormula f = new
                 * GraphicPresentationOfFormula(); mathToolGraphicArea.add(f);
                 * String[] command =
                 * Expression.getOperatorAndArguments(inputField.getText());
                 * String[] params = Expression.getArguments(command[1]);
                 * f.setCommand(MathCommandCompiler.getCommand(command[0],
                 * params));
                 * f.setTypeGraphicFormula(TypeGraphicFormula.COMMAND);
                 * f.initialize(getFontSize()); ListOfFormulas.add(f);
                 * f.drawFormula(); mathToolGraphicArea.setPosition(f);
                 * validate(); repaint();
                 *
                 * } catch (ExpressionException | EvaluationException e) {
                 * System.out.println("Fehler"); }
                 */
                executeCommand();
                break;

            case KeyEvent.VK_UP:
                if (log_position > 0) {
                    log_position--;
                }
                if (log_position == listOfCommands.size()) {
                    log_position--;
                }
                showLoggedCommand(log_position);
                break;

            case KeyEvent.VK_DOWN:
                if (log_position < listOfCommands.size() - 1) {
                    log_position++;
                }
                showLoggedCommand(log_position);
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
        /**
         * Wichtig: Neuer Befehl/Neue Formel -> Rotation stoppen, falls diese
         * aktiv ist.
         */
        stopPossibleRotation();
        inputField.setText("clear()");
        executeCommand();
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
        if (e.getSource() == legendLabel) {
            if (typeGraphic.equals(TypeGraphic.GRAPH2D)) {
                LegendGUI legendGUI;
                if (graphicMethods2D.getGraphIsExplicit()) {
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            graphicMethods2D.getInstructions(), graphicMethods2D.getColors(), graphicMethods2D.getExpressions());
                } else {
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            graphicMethods2D.getInstructions(), graphicMethods2D.getColors().get(0), graphicMethods2D.getExpressions().get(0));
                }
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethods3D.getInstructions(), graphicMethods3D.getExpression());
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(TypeGraphic.CURVE2D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethodsCurves2D.getInstructions(), graphicMethodsCurves2D.getExpressions());
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(TypeGraphic.CURVE3D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethodsCurves3D.getInstructions(), graphicMethodsCurves3D.getExpressions());
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(TypeGraphic.POLARGRAPH2D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethodsPolar2D.getInstructions(), graphicMethodsPolar2D.getColors(), graphicMethodsPolar2D.getExpressions());
                legendGUI.setVisible(true);
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
                MathToolForm myMathToolForm = new MathToolForm();
                myMathToolForm.setVisible(true);
                myMathToolForm.setBounds(50, 50, 1300, 650);
                myMathToolForm.setExtendedState(MAXIMIZED_BOTH);
                myMathToolForm.getContentPane().setBackground(new Color(255, 150, 0));
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
