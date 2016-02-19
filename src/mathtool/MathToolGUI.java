package mathtool;

import mathtool.enums.TypeMode;
import command.Command;
import command.TypeCommand;
import mathtool.component.dialogs.MathToolSaveGraphicDialog;
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
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import mathcommandcompiler.MathCommandCompiler;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import graphic.GraphicPanelCylindrical;
import mathtool.component.dialogs.MathToolSaveSessionDialog;
import mathtool.component.components.ComputingDialogGUI;
import mathtool.component.components.DevelopersDialogGUI;
import mathtool.component.components.HelpDialogGUI;
import mathtool.component.components.LegendGUI;
import mathtool.component.components.MathToolTextField;
import mathtool.component.components.OutputOptionsDialogGUI;
import lang.translator.Translator;
import mathtool.component.components.GraphicOptionsDialogGUI;

public class MathToolGUI extends JFrame implements MouseListener {

    public static final Color BACKGROUND_COLOR = new Color(255, 150, 0);

    private final JLabel legendLabel;
    private final JLabel saveLabel;
    private final JLabel rotateLabel;
    private final JTextArea mathToolTextArea;
    private final JScrollPane scrollPaneText;
    private final GraphicArea mathToolGraphicArea;
    private final JScrollPane scrollPaneGraphic;
    private ComputingDialogGUI computingDialog;
    private final MathToolTextField mathToolTextField;

    private static GraphicPanel2D graphicPanel2D;
    private static GraphicPanel3D graphicPanel3D;
    private static GraphicPanelCurves2D graphicPanelCurves2D;
    private static GraphicPanelCurves3D graphicPanelCurves3D;
    private static GraphicPanelImplicit2D graphicPanelImplicit2D;
    private static GraphicPanelPolar graphicPanelPolar;
    private static GraphicPanelCylindrical graphicPanelCylindrical;

    private final JPanel[] graphicPanels;
    private final JComponent[] buttonsAndDropDowns;

    private HashMap<JComponent, String> componentCaptions;

    // Zeitabhängige Komponenten
    private Thread rotateThread;
    private SwingWorker<Void, Void> computingSwingWorker;
    private Timer computingTimer;

    // MathTool-Log
    private static final ArrayList<String> commandList = new ArrayList<>();

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

        // Vereinfachungsoptionen initialisieren.
        MathToolController.initSimplifyTypes();

        // Konfigurationen aus XML auslesen.
        MathToolController.loadSettings();

        // Mindestfenstergröße festlegen
        setMinimumSize(minimumDimension);

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
        Font mathToolAreaFont = new Font("Arial", Font.BOLD, fontSizeText);
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
        mathToolGraphicArea.setFontSize(fontSizeGraphic);
        scrollPaneGraphic = new JScrollPane(mathToolGraphicArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneGraphic);

        // Buttons ausrichten
        cancelButton.setVisible(false);

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

        graphicPanelCurves3D = new GraphicPanelCurves3D();
        add(graphicPanelCurves3D);

        graphicPanelCylindrical = new GraphicPanelCylindrical();
        add(graphicPanelCylindrical);

        // Alle Grafikpanels unsichtbar machen.
        graphicPanels = new JPanel[]{graphicPanel2D, graphicPanel3D, graphicPanelCurves2D, graphicPanelCurves3D, graphicPanelImplicit2D, 
            graphicPanelPolar, graphicPanelCylindrical};
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
        MathCommandCompiler.setGraphicPanelPolar2D(graphicPanelPolar);
        MathCommandCompiler.setGraphicPanelCylindrical3D(graphicPanelCylindrical);
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
                        mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton, cancelButton);
                // Alle Buttons und Dropdowns korrekt ausrichten.
                MathToolController.locateButtonsAndDropDowns(buttonsAndDropDowns, 10, scrollPaneText.getHeight() + 60, 130, 30, 135);
                // Alle Grafikpanels korrekt ausrichten.
                MathToolController.locateGraphicPanels(graphicPanels, scrollPaneText.getWidth() - 490, scrollPaneText.getHeight() - 490, 500, 500);

                legendLabel.setBounds(graphicPanel3D.getX(), scrollPaneText.getHeight() + 25, 100, 25);
                saveLabel.setBounds(graphicPanel3D.getX() + 150, scrollPaneText.getHeight() + 25, 150, 25);
                rotateLabel.setBounds(graphicPanel3D.getX() + 300, scrollPaneText.getHeight() + 25, 200, 25);

                if (computingDialog != null) {
                    computingDialog = new ComputingDialogGUI(computingSwingWorker, getX(), getY(), getWidth(), getHeight());
                }

                if (!typeGraphic.equals(TypeGraphic.NONE)) {
                    // Konsolenmaße neu setzen, falls eine Grafik angezeigt werden muss.
                    MathToolController.resizeConsole(scrollPaneText, scrollPaneGraphic, 10, 10, getWidth() - 550, getHeight() - 170,
                            mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton, cancelButton);
                }

                mathToolGraphicArea.updateSize();

                // Abhängig von der Sprache alle Texte (neu) setzen.
                updateAPI();

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
     * Setter für language
     */
    public static void setLanguage(TypeLanguage language) {
        Expression.setLanguage(language);
    }

    /**
     * Getter für commandList.
     */
    public static ArrayList<String> getCommandList() {
        return commandList;
    }

    /**
     * Getter für simplifyTypes
     */
    public static HashSet<TypeSimplify> getSimplifyTypes() {
        return simplifyTypes;
    }

    /**
     * Setter für simplifyTypes
     */
    public static void setSimplifyTypes(HashSet<TypeSimplify> simplifyTypes) {
        MathToolGUI.simplifyTypes = simplifyTypes;
    }

    /**
     * Getter für graphicPanel3D
     */
    public static GraphicPanel3D getGraphicPanel3D(){
        return graphicPanel3D;
    }
    
    /**
     * Aktualisiert die Oberfläche nach Änderung von Einstellungen.
     */
    private void updateAPI() {
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
     *
     * @throws ExpressionException
     */
    private void activatePanelsForGraphs(String commandName, String[] params) throws ExpressionException {

        Command c = MathCommandCompiler.getCommand(commandName, params);

        // Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
        MathToolController.resizeConsole(scrollPaneText, scrollPaneGraphic, 10, 10, getWidth() - 550, getHeight() - 170,
                mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton, cancelButton);

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
        } else if (c.getTypeCommand().equals(TypeCommand.plotimplicit)) {
            graphicPanelImplicit2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.plot3d) || c.getTypeCommand().equals(TypeCommand.tangent) && ((HashMap) c.getParams()[1]).size() == 2) {
            graphicPanel3D.setVisible(true);
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
        } else if (c.getTypeCommand().equals(TypeCommand.regressionline) && c.getParams().length >= 2) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.solve) && c.getParams().length >= 3 || c.getTypeCommand().equals(TypeCommand.tangent) && ((HashMap) c.getParams()[1]).size() == 1) {
            graphicPanel2D.setVisible(true);
            legendLabel.setVisible(true);
            saveLabel.setVisible(true);
        } else if (c.getTypeCommand().equals(TypeCommand.solvedeq)) {
            graphicPanel2D.setVisible(true);
            saveLabel.setVisible(true);
        } else {
            MathToolController.resizeConsole(scrollPaneText, scrollPaneGraphic, 10, 10, getWidth() - 40, getHeight() - 170,
                    mathToolTextArea, mathToolGraphicArea, mathToolTextField, inputButton, cancelButton);
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
        inputButton.setBounds(560, 330, 130, 30);

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
        cancelButton.setBounds(560, 300, 130, 30);

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
        commandChoice.setBounds(560, 370, 32, 23);

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

        setJMenuBar(mathToolMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initCaptions() {

        componentCaptions = new HashMap<>();

        // Menüeinträge
        componentCaptions.put(menuFile, "GUI_MathToolForm_MENU_FILE");
        componentCaptions.put(menuItemOpen, "GUI_MathToolForm_MENU_OPEN");
        componentCaptions.put(menuItemSave, "GUI_MathToolForm_MENU_SAVE");
        componentCaptions.put(menuItemQuit, "GUI_MathToolForm_MENU_QUIT");
        componentCaptions.put(menuMathTool, "GUI_MathToolForm_MENU_MATHTOOL");
        componentCaptions.put(menuItemHelp, "GUI_MathToolForm_MENU_HELP");
        componentCaptions.put(menuItemLanguageMenu, "GUI_MathToolForm_MENU_LANGUAGES");
        componentCaptions.put(menuItemLanguageEnglish, "GUI_MathToolForm_MENU_ENGLISH");
        componentCaptions.put(menuItemLanguageGerman, "GUI_MathToolForm_MENU_GERMAN");
        componentCaptions.put(menuItemLanguageRussian, "GUI_MathToolForm_MENU_RUSSIAN");
        componentCaptions.put(menuItemLanguageUkrainian, "GUI_MathToolForm_MENU_UKRAINIAN");
        componentCaptions.put(menuItemRepresentationMenu, "GUI_MathToolForm_MENU_REPRESENTATION_MODE");
        componentCaptions.put(menuItemRepresentationFormula, "GUI_MathToolForm_FORMULA_MODE");
        componentCaptions.put(menuItemRepresentationText, "GUI_MathToolForm_MENU_TEXT_MODE");
        componentCaptions.put(menuItemAbout, "GUI_MathToolForm_MENU_ABOUT");
        componentCaptions.put(menuItemOptionsMenu, "GUI_MathToolForm_MENU_OPTIONS");
        componentCaptions.put(menuItemOutputOptions, "GUI_MathToolForm_MENU_OUTPUT_OPTIONS");
        componentCaptions.put(menuItemGraphicOptions, "GUI_MathToolForm_MENU_GRAPHIC_OPTIONS");

        // Buttons
        componentCaptions.put(approxButton, "GUI_MathToolForm_APPROX");
        componentCaptions.put(latexButton, "GUI_MathToolForm_LATEX_CODE");
        componentCaptions.put(clearButton, "GUI_MathToolForm_CLEAR");
        componentCaptions.put(inputButton, "GUI_MathToolForm_INPUT");
        componentCaptions.put(cancelButton, "GUI_MathToolForm_CANCEL");

        // Labels
        componentCaptions.put(legendLabel, "GUI_MathToolForm_LEGEND");
        componentCaptions.put(saveLabel, "GUI_MathToolForm_SAVE");
        if (isRotating) {
            componentCaptions.put(rotateLabel, "GUI_MathToolForm_STOP_ROTATION");
        } else {
            componentCaptions.put(rotateLabel, "GUI_MathToolForm_ROTATE_GRAPH");
        }

    }

    /**
     * Dient dazu, dass falls ein neuer Befehl eingegeben wird, während sich
     * eine 3D-Grafik dreht, dass diese Rotation zunächst gestoppt wird. GRUND:
     * Es kann zu Anzeigefehlern bei der 3D-Grafik kommen.
     */
    private void stopPossibleRotation() {
        if (isRotating) {
            isRotating = false;
            MathToolController.stopRotationOfGraph(graphicPanel3D, graphicPanelCurves3D, rotateThread, rotateLabel);
        }
    }

    /**
     * Hauptmethode zum Ausführen eines Befehls.
     */
    private void executeCommand() {

        cancelButton.setVisible(true);
        inputButton.setVisible(false);
        final MathToolGUI mathToolGUI = this;

        computingSwingWorker = new SwingWorker<Void, Void>() {

            @Override
            protected void done() {
                computing = false;
                computingTimer.cancel();
                computingDialog.setVisible(false);
                inputButton.setVisible(true);
                cancelButton.setVisible(false);
                // mathToolArea nach unten scrollen lassen.
                scrollPaneText.getVerticalScrollBar().setValue(scrollPaneText.getVerticalScrollBar().getMaximum());
            }

            @Override
            protected Void doInBackground() throws Exception {

                try {
                    computingDialog = new ComputingDialogGUI(computingSwingWorker, mathToolGUI.getX(), mathToolGUI.getY(), mathToolGUI.getWidth(), mathToolGUI.getHeight());
                } catch (Exception e) {
                    mathToolGraphicArea.addComponent("ERROR!!!!!");
                }
                MathToolController.initializeTimer(computingTimer, computingDialog);

                boolean validCommand = false;

                // Leerzeichen werden im Vorfeld beseitigt.
                String input = mathToolTextField.getText().replaceAll(" ", "").toLowerCase();

                // Befehl loggen!
                commandList.add(input);
                logPosition = commandList.size();

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
                        MathCommandCompiler.executeCommand(input);
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
                try {

                    Expression expr = Expression.build(input, null);

                    /*
                     Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
                     Beispielsweise ist 1/0 zwar ein gültiger Ausdruck, liefert aber
                     beim Auswerten einen Fehler.
                     */
                    try {

                        Expression exprSimplified = expr.simplify(simplifyTypes);
                        // Hinzufügen zum textlichen Ausgabefeld.
                        mathToolTextArea.append(expr.writeExpression() + " = " + exprSimplified.writeExpression() + "\n \n");
                        // Hinzufügen zum graphischen Ausgabefeld.
                        mathToolGraphicArea.addComponent(expr, "  =  ", exprSimplified);
                        mathToolTextField.setText("");
                        return null;

                    } catch (EvaluationException e) {
                        if (MathToolController.isInputAlgebraicExpression(input)) {
                            mathToolTextArea.append(expr.writeExpression() + "\n \n");
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(expr);
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                            mathToolTextField.setText("");
                            return null;
                        }
                    } catch (Exception exception) {
                        // Falls ein unerwarteter Fehler auftritt.
                        if (MathToolController.isInputAlgebraicExpression(input)) {
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
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
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                        return null;
                    }
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (MathToolController.isInputAlgebraicExpression(input)) {
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
                try {

                    MatrixExpression matExpr = MatrixExpression.build(input, null);

                    /*
                     Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
                     Beispielsweise ist 1/0 zwar ein gültiger Ausdruck, liefert aber
                     beim Auswerten einen Fehler.
                     */
                    try {

                        MatrixExpression matExprSimplified = matExpr.simplify(simplifyTypes);
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
                        if (MathToolController.isInputMatrixExpression(input)) {
                            mathToolTextArea.append(matExpr.writeMatrixExpression() + "\n \n");
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(matExpr);
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                            mathToolTextField.setText("");
                            return null;
                        }
                    } catch (Exception exception) {
                        // Falls ein unerwarteter Fehler auftritt.
                        if (MathToolController.isInputMatrixExpression(input)) {
                            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage() + "\n \n");
                            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + exception.getMessage());
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
                        mathToolTextArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                        mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage());
                        return null;
                    }
                } catch (Exception exception) {
                    // Falls ein unerwarteter Fehler auftritt.
                    if (MathToolController.isInputMatrixExpression(input)) {
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

    private void rotateLabelClick() {
        if (!isRotating) {
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                rotateThread = new Thread(graphicPanel3D, "rotateGraph");
                isRotating = true;
                graphicPanel3D.setIsRotating(true);
            } else if (typeGraphic.equals(TypeGraphic.GRAPHCURVE3D)) {
                rotateThread = new Thread(graphicPanelCurves3D, "rotateGraph");
                isRotating = true;
                graphicPanelCurves3D.setIsRotating(true);
            } else {
                rotateThread = new Thread(graphicPanelCylindrical, "rotateGraph");
                isRotating = true;
                graphicPanelCylindrical.setIsRotating(true);
            }
            rotateThread.start();
            rotateLabel.setText("<html><b><u>" + Translator.translateExceptionMessage("GUI_MathToolForm_STOP_ROTATION") + "</u></b></html>");
        } else {
            isRotating = false;
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                graphicPanel3D.setIsRotating(false);
            } else if (typeGraphic.equals(TypeGraphic.GRAPHCURVE3D)) {
                graphicPanelCurves3D.setIsRotating(false);
            } else {
                graphicPanelCylindrical.setIsRotating(false);
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

    private void menuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItemQuitActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        computingSwingWorker.cancel(true);
    }//GEN-LAST:event_cancelButtonActionPerformed

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
            mathToolTextArea.append(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + e.getMessage() + "\n \n");
            mathToolGraphicArea.addComponent(Translator.translateExceptionMessage("MTF_UNEXPECTED_EXCEPTION") + e.getMessage());
        }
    }//GEN-LAST:event_clearButtonActionPerformed

    private void menuItemLanguageEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageEnglishActionPerformed
        Expression.setLanguage(TypeLanguage.EN);
        updateAPI();
    }//GEN-LAST:event_menuItemLanguageEnglishActionPerformed

    private void menuItemLanguageGermanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageGermanActionPerformed
        Expression.setLanguage(TypeLanguage.DE);
        updateAPI();
    }//GEN-LAST:event_menuItemLanguageGermanActionPerformed

    private void menuItemLanguageRussianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageRussianActionPerformed
        Expression.setLanguage(TypeLanguage.RU);
        updateAPI();
    }//GEN-LAST:event_menuItemLanguageRussianActionPerformed

    private void menuItemLanguageUkrainianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemLanguageUkrainianActionPerformed
        Expression.setLanguage(TypeLanguage.UA);
        updateAPI();
    }//GEN-LAST:event_menuItemLanguageUkrainianActionPerformed

    private void menuItemRepresentationTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationTextActionPerformed
        typeMode = TypeMode.TEXT;
        scrollPaneGraphic.setVisible(false);
        scrollPaneText.setVisible(true);
        updateAPI();
    }//GEN-LAST:event_menuItemRepresentationTextActionPerformed

    private void menuItemRepresentationFormulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRepresentationFormulaActionPerformed
        typeMode = TypeMode.GRAPHIC;
        scrollPaneGraphic.setVisible(true);
        scrollPaneText.setVisible(false);
        updateAPI();
    }//GEN-LAST:event_menuItemRepresentationFormulaActionPerformed

    private void menuItemOutputOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOutputOptionsActionPerformed

        String simplifyOptionsTitle = Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTIONS_GROUP_NAME");

        // Checkboxen.
        ArrayList<String> simplifyOptions = new ArrayList<>();
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_ALGEBRAIC_EXPRESSIONS");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_SIMPLIFY_FUNCTIONAL_RELATIONS");
        simplifyOptions.add("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_AND_COLLECT_IF_SHORTER");
        String saveButtonLabel = Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SAVE_BUTTON");
        String cancelButtonLabel = Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_CANCEL_BUTTON");

        // DropDowns.
        ArrayList<String[]> dropDownOptions = new ArrayList<>();
        dropDownOptions.add(new String[]{Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_LOGARITHM_OPTION"),
            Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_COLLECT_LOGARITHMS"),
            Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND_LOGARITHMS")});
        dropDownOptions.add(new String[]{Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_NO_FACTPRIZATION_OPTION"),
            Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_FACTORIZE"),
            Translator.translateExceptionMessage("GUI_OutputOptionsDialogGUI_SIMPLIFY_OPTION_EXPAND")});

        OutputOptionsDialogGUI outputOptionsDialogGUI = new OutputOptionsDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
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

        String simplifyOptionsTitle = Translator.translateExceptionMessage("GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_GROUP_NAME");

        String saveButtonLabel = Translator.translateExceptionMessage("GUI_GraphicOptionsDialogGUI_SAVE_BUTTON");
        String cancelButtonLabel = Translator.translateExceptionMessage("GUI_GraphicOptionsDialogGUI_CANCEL_BUTTON");

        // DropDowns.
        ArrayList<String[]> dropDownOptions = new ArrayList<>();
        dropDownOptions.add(new String[]{Translator.translateExceptionMessage("GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT"),
            Translator.translateExceptionMessage("GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK")});
        dropDownOptions.add(new String[]{Translator.translateExceptionMessage("GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH"),
            Translator.translateExceptionMessage("GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY")});

        GraphicOptionsDialogGUI graphicOptionsDialogGUI = new GraphicOptionsDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                2, simplifyOptionsTitle, null, dropDownOptions, saveButtonLabel, cancelButtonLabel);
        graphicOptionsDialogGUI.setVisible(true);

    }//GEN-LAST:event_menuItemGraphicOptionsActionPerformed

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
                if (logPosition < commandList.size() - 1) {
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
            switch (typeGraphic) {
                case GRAPH2D:
                    instructions.addAll(graphicPanel2D.getInstructions());
                    for (int i = 0; i < graphicPanel2D.getExpressions().size(); i++) {
                        exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + (i + 1) + ": " + graphicPanel2D.getExpressions().get(i).writeExpression());
                    }
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanel2D.getColors(), exprs);
                    break;
                case GRAPHIMPLICIT2D: {
                    instructions.addAll(graphicPanelImplicit2D.getInstructions());
                    exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_EQUATION_OF_IMPLICIT_FUNCTION")
                            + graphicPanelImplicit2D.getExpressions().get(0).writeExpression()
                            + " = "
                            + graphicPanelImplicit2D.getExpressions().get(1).writeExpression());
                    ArrayList<Color> colors = new ArrayList<>();
                    colors.add(graphicPanelImplicit2D.getColor());
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, colors, exprs);
                    break;
                }
                case GRAPH3D:
                    instructions.addAll(GraphicPanel3D.getInstructions());
                    for (int i = 0; i < graphicPanel3D.getExpressions().size(); i++) {
                        exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + (i + 1) + ": " + graphicPanel3D.getExpressions().get(i).writeExpression());
                    }
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanel3D.getColors(), exprs);
                    break;
                case GRAPHCURVE2D: {
                    instructions.addAll(GraphicPanelCurves2D.getInstructions());
                    ArrayList<Color> colors = new ArrayList<>();
                    colors.add(Color.blue);
                    exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_PARAMETERIZED_CURVE")
                            + "(" + graphicPanelCurves2D.getExpressions()[0]
                            + ", " + graphicPanelCurves2D.getExpressions()[1] + ")"
                    );
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, colors, exprs);
                    break;
                }
                case GRAPHCURVE3D: {
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
                    break;
                }
                case GRAPHPOLAR:
                    instructions.addAll(GraphicPanelPolar.getInstructions());
                    for (int i = 0; i < graphicPanelPolar.getExpressions().size(); i++) {
                        exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + (i + 1) + ": " + graphicPanelPolar.getExpressions().get(i).writeExpression());
                    }
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanelPolar.getColors(), exprs);
                    break;
                case GRAPHCYLINDRCAL:
                    instructions.addAll(GraphicPanelCylindrical.getInstructions());
                    for (int i = 0; i < graphicPanelCylindrical.getExpressions().size(); i++) {
                        exprs.add(Translator.translateExceptionMessage("GUI_LegendGUI_GRAPH") + (i + 1) + ": " + graphicPanelCylindrical.getExpressions().get(i).writeExpression());
                    }
                    legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                            instructions, graphicPanelCylindrical.getColors(), exprs);
                    break;
                default:
                    break;
            }
        } else if (e.getSource() == saveLabel) {

            switch (typeGraphic) {
                case GRAPH2D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanel2D);
                    break;
                case GRAPH3D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanel3D);
                    break;
                case GRAPHIMPLICIT2D:
                    saveDialog = new MathToolSaveGraphicDialog(graphicPanel2D);
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
                default:
                    break;
            }

        } else if (e.getSource() == rotateLabel) {
            if (rotateLabel.isVisible()) {
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
                mathToolForm.getContentPane().setBackground(BACKGROUND_COLOR);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton approxButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JComboBox commandChoice;
    private javax.swing.JButton inputButton;
    private javax.swing.JButton latexButton;
    private javax.swing.JMenuBar mathToolMenuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemAbout;
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
    private javax.swing.JComboBox operatorChoice;
    // End of variables declaration//GEN-END:variables

}
