package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import graphic.GraphicMethods2D;
import graphic.GraphicMethods3D;
import graphic.GraphicMethodsCurves2D;
import graphic.GraphicMethodsCurves3D;
import graphic.GraphicMethodsPolar2D;
import expressionbuilder.TypeGraphic;
import LogicalExpressionBuilder.LogicalExpression;
import Translator.Translator;
import expressionbuilder.TypeLanguage;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class MathToolForm extends javax.swing.JFrame implements MouseListener {

    private Thread threadRotate;
    private boolean startRotate;
    private TypeGraphic typeGraphic;
    private boolean computing = false;
    private SwingWorker<Void, Void> computingSwingWorker;
    private Timer computingTimer;
    private ComputingDialogGUI computingDialog;
    private final JLabel legendLabel = new JLabel("<html><b>Legende</b></html>");

    JTextArea mathToolArea;
    JEditorPane helpArea;
    JScrollPane scrollPane;

    GraphicMethods2D graphicMethods2D;
    GraphicMethods3D graphicMethods3D;
    GraphicMethodsCurves2D graphicMethodsCurves2D;
    GraphicMethodsCurves3D graphicMethodsCurves3D;
    GraphicMethodsPolar2D graphicMethodsPolar2D;

    /**
     * Diese Objekte werden im Laufe des Programms erweitert. Sie enthalten die
     * im Laufe des Programms definierten Variablen und Funktionen.
     */
    static HashMap<String, Expression> definedVars = new HashMap<>();
    static HashSet definedVarsSet = new HashSet();
    static HashMap<String, Expression> definedFunctions = new HashMap<>();
    public HashMap<Integer, String> listOfCommands = new HashMap<>();
    /**
     * Variablen, die eine Rolle beim Loggen der Befehle spielen. command_count
     * = Anzahl der insgesamt geloggten Befehle (gültige UND ungültige!)
     * log_position = Index des aktuellen befehls, den man mittels Pfeiltasten
     * ausgegeben haben möchte.
     */
    public int log_position = 0;

    public MathToolForm() {
        initComponents();
        this.setLayout(null);
        this.startRotate = false;
//        computingDialog = new ComputingDialogGUI(computingSwingWorker, this.getX(), this.getY(), this.getWidth(), this.getHeight());

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
         * Es wird noch keine Grafik angezeigt
         */
        typeGraphic = TypeGraphic.NONE;

        /**
         * Labels ausrichten
         */
        legendLabel.setVisible(false);
        add(legendLabel);
        legendLabel.addMouseListener(this);

        /**
         * Ausgabefeld ausrichten
         */
        mathToolArea = new JTextArea();
        Font mathToolAreaFont = new Font("Verdana", Font.PLAIN, 12);
        mathToolArea.setFont(mathToolAreaFont);
        add(mathToolArea);
        mathToolArea.setEditable(false);
        mathToolArea.setLineWrap(true);
        mathToolArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(mathToolArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mathToolArea.setCaretPosition(mathToolArea.getDocument().getLength());
        add(scrollPane);

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
        repaint();
        graphicMethods2D.setVisible(false);

        graphicMethodsCurves2D = new GraphicMethodsCurves2D();
        add(graphicMethodsCurves2D);
        repaint();
        graphicMethodsCurves2D.setVisible(false);

        graphicMethodsPolar2D = new GraphicMethodsPolar2D();
        add(graphicMethodsPolar2D);
        repaint();
        graphicMethodsPolar2D.setVisible(false);

        /**
         * 3D-Grafikobjekte initialisieren
         */
        graphicMethods3D = new GraphicMethods3D();
        add(graphicMethods3D);
        repaint();
        graphicMethods3D.setVisible(false);

        graphicMethodsCurves3D = new GraphicMethodsCurves3D();
        add(graphicMethodsCurves3D);
        repaint();
        graphicMethodsCurves3D.setVisible(false);

        /**
         * ComponentListener für das Ausrichten von Komponenten
         */
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

                scrollPane.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
                mathToolArea.setBounds(0, 0, scrollPane.getWidth(), scrollPane.getHeight());

                inputField.setBounds(10, scrollPane.getHeight() + 20, scrollPane.getWidth() - 110, 30);
                inputButton.setBounds(mathToolArea.getWidth() - 90, scrollPane.getHeight() + 20, 100, 30);
                cancelButton.setBounds(mathToolArea.getWidth() - 90, scrollPane.getHeight() + 20, 100, 30);
                approxButton.setBounds(10, scrollPane.getHeight() + 60, 130, 30);
                latexButton.setBounds(145, scrollPane.getHeight() + 60, 130, 30);
                clearButton.setBounds(280, scrollPane.getHeight() + 60, 130, 30);
                operatorChoice.setBounds(415, scrollPane.getHeight() + 60, 130, 30);
                commandChoice.setBounds(550, scrollPane.getHeight() + 60, 130, 30);

                graphicMethods2D.setBounds(scrollPane.getWidth() - 490, scrollPane.getHeight() - 490, 500, 500);
                graphicMethods3D.setBounds(scrollPane.getWidth() - 490, scrollPane.getHeight() - 490, 500, 500);
                graphicMethodsCurves2D.setBounds(scrollPane.getWidth() - 490, scrollPane.getHeight() - 490, 500, 500);
                graphicMethodsCurves3D.setBounds(scrollPane.getWidth() - 490, scrollPane.getHeight() - 490, 500, 500);
                graphicMethodsPolar2D.setBounds(scrollPane.getWidth() - 490, scrollPane.getHeight() - 490, 500, 500);

                legendLabel.setBounds(graphicMethods3D.getX(), scrollPane.getHeight() + 25, 100, 25);
                rotateButton.setBounds(graphicMethods3D.getX() + 140, scrollPane.getHeight() + 20, 220, 30);

                if (computingDialog != null) {
                    computingDialog = new ComputingDialogGUI(computingSwingWorker, getX(), getY(), getWidth(), getHeight());
                }

                if (!typeGraphic.equals(TypeGraphic.NONE)) {
                    scrollPane.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
                    mathToolArea.setBounds(0, 0, scrollPane.getWidth(), scrollPane.getHeight());
                    inputField.setBounds(inputField.getX(), inputField.getY(), inputField.getWidth() - 510, 30);
                    inputButton.setBounds(inputButton.getX() - 510, inputButton.getY(), 100, 30);
                    cancelButton.setBounds(cancelButton.getX() - 510, cancelButton.getY(), 100, 30);
                }

                validate();
                repaint();
            }

        });

    }

    private void showLoggedCommand(int i) {
        inputField.setText(listOfCommands.get(i));
    }

    /**
     * Aktualisiert die Oberfläche nach Änderung von Einstellungen
     */
    private void refreshInterface() {

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

    private void activatePanelsForGraphs(String command_name, String[] params) throws ExpressionException, EvaluationException {

        Command c = MathCommandCompiler.getCommand(command_name, params);

        //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
        scrollPane.setBounds(10, 10, getWidth() - 550, getHeight() - 170);
        mathToolArea.setBounds(0, 0, scrollPane.getWidth(), scrollPane.getHeight());
        inputField.setBounds(10, scrollPane.getHeight() + 20, scrollPane.getWidth() - 110, 30);
        inputButton.setBounds(mathToolArea.getWidth() - 90, scrollPane.getHeight() + 20, 100, 30);
        cancelButton.setBounds(mathToolArea.getWidth() - 90, scrollPane.getHeight() + 20, 100, 30);

        //Alle Grafik-Panels zunächst unsichtbar machen, dann, je nach Fall, wieder sichtbar machen.
        graphicMethods2D.setVisible(false);
        graphicMethods3D.setVisible(false);
        graphicMethodsPolar2D.setVisible(false);
        graphicMethodsCurves2D.setVisible(false);
        graphicMethodsCurves3D.setVisible(false);
        rotateButton.setVisible(false);
        legendLabel.setVisible(true);

        graphicMethods2D.setBounds(scrollPane.getWidth() + 20, scrollPane.getHeight() - 490, 500, 500);
        graphicMethods3D.setBounds(scrollPane.getWidth() + 20, scrollPane.getHeight() - 490, 500, 500);
        graphicMethodsPolar2D.setBounds(scrollPane.getWidth() + 20, scrollPane.getHeight() - 490, 500, 500);
        graphicMethodsCurves2D.setBounds(scrollPane.getWidth() + 20, scrollPane.getHeight() - 490, 500, 500);
        graphicMethodsCurves3D.setBounds(scrollPane.getWidth() + 20, scrollPane.getHeight() - 490, 500, 500);

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
            scrollPane.setBounds(10, 10, getWidth() - 40, getHeight() - 170);
            mathToolArea.setBounds(0, 0, scrollPane.getWidth(), scrollPane.getHeight());
            inputField.setBounds(10, scrollPane.getHeight() + 20, scrollPane.getWidth() - 110, 30);
            inputButton.setBounds(mathToolArea.getWidth() - 90, scrollPane.getHeight() + 20, 100, 30);
            cancelButton.setBounds(mathToolArea.getWidth() - 90, scrollPane.getHeight() + 20, 100, 30);
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
        commandChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Befehl", "approx", "clear", "def", "deffuncs", "defvars", "euler", "latex", "pi", "plot2d", "plot3d", "plotcurve", "plotpolar", "solve", "solvedeq", "table", "tangent", "taylordeq", "undef", "undefall" }));
        commandChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(commandChoice);
        commandChoice.setBounds(560, 370, 96, 23);

        clearButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        clearButton.setText("Clear");
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

        menuItemLanguageEnglish.setText("English");
        menuItemLanguageEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageEnglishActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageEnglish);

        menuItemLanguageGerman.setText("German");
        menuItemLanguageGerman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageGermanActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageGerman);

        menuItemLanguageRussian.setText("Russian");
        menuItemLanguageRussian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageRussianActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageRussian);

        menuItemLanguageUkrainian.setText("Ukrainian");
        menuItemLanguageUkrainian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemLanguageUkrainianActionPerformed(evt);
            }
        });
        menuItemLanguageMenu.add(menuItemLanguageUkrainian);

        menuMathTool.add(menuItemLanguageMenu);

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

    private void stopPossibleRotation() {
        if (startRotate) {
            startRotate = false;
            if (typeGraphic.equals(TypeGraphic.GRAPH3D)) {
                graphicMethods3D.setStartRotate(false);
            } else {
                graphicMethodsCurves3D.setStartRotate(false);
            }
            threadRotate.interrupt();
            rotateButton.setText("Graphen rotieren lassen");
        }
    }

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
                scrollPane.getVerticalScrollBar().setValue(
                        scrollPane.getVerticalScrollBar().getMaximum());
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
                listOfCommands.put(listOfCommands.size(), command);
                log_position = listOfCommands.size();

                /**
                 * Zunächst: es wird geprüft, ob die Zeile einen Befehl bildet.
                 * Ja -> Befehl ausführen. Nein -> Prüfen, ob dies ein
                 * mathematischer Ausdruck ist.
                 */
                try {

                    String[] com = Expression.getOperatorAndArguments(command);
                    String[] params = Expression.getArguments(com[1]);

                    for (String c : MathCommandCompiler.commands) {
                        valid_command = valid_command || com[0].equals(c);
                    }

                    if (valid_command) {

                        mathToolArea.append(command + "\n \n");
                        MathCommandCompiler.executeCommand(command, mathToolArea, graphicMethods2D, graphicMethods3D,
                                graphicMethodsCurves2D, graphicMethodsCurves3D, graphicMethodsPolar2D, definedVars,
                                definedVarsSet, definedFunctions);
                        /**
                         * Falls es ein Grafikbefehle war -> Grafik sichtbar
                         * machen.
                         */
                        activatePanelsForGraphs(com[0], params);
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
                        mathToolArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
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
                 * Nun wird geprüft, ob es sich bei s um einen gültigen Ausdruck
                 * handelt. Ja -> vereinfachen und ausgeben.
                 */
                /**
                 * input_is_arithmetic_expression besagt, ob die Eingabe ein
                 * gültiger arithmetischer Ausdruck ist (welcher aber vielleicht
                 * Vereinfachungsprobleme etc. bereitet). Falls nicht, so wird
                 * weiter geprüft, ob die Eingabe ein logischer Ausdruck ist.
                 */
                boolean input_is_arithmetic_expression;
                try {
                    LogicalExpression.build(command, new HashSet());
                    input_is_arithmetic_expression = false;
                } catch (ExpressionException e) {
                    input_is_arithmetic_expression = true;
                }

                /**
                 * Falls der logische Kompiler versagt, aber command DENNOCH
                 * (eindeutige) logische Operatoren enthält -> merken, command
                 * ist, wenn überhaupt, dann ein logischer Ausdruck.
                 */
                input_is_arithmetic_expression = input_is_arithmetic_expression
                        && !command.contains("&") && !command.contains("|")
                        && !command.contains(">") && !command.contains("=");

                try {

                    Expression expr = Expression.build(command, new HashSet());

                    /**
                     * Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
                     * Z.B. 1/0 ist zwar ein gültiger Ausdruck, liefert aber
                     * beim Auswerten einen Fehler.
                     */
                    try {
                        Expression expr_simplified = expr.evaluate(definedVarsSet);
                        expr_simplified = expr_simplified.simplify();
                        mathToolArea.append(expr.writeFormula(true) + " = " + expr_simplified.writeFormula(true) + "\n \n");
                        inputField.setText("");
                        return null;
                    } catch (EvaluationException e) {
                        if (input_is_arithmetic_expression) {
                            mathToolArea.append(expr.writeFormula(true) + "\n \n");
                            if (e.getMessage().equals("Berechnung abgebrochen.")) {
                                mathToolArea.append(e.getMessage() + "\n \n");
                            } else {
                                mathToolArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                            }
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
                        mathToolArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
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
                    mathToolArea.append(log_expr.writeFormula() + Translator.translateExceptionMessage("MTF_EQUIVALENT_TO") + log_expr_simplified.writeFormula() + " \n \n");
                    inputField.setText("");

                } catch (ExpressionException e) {
                    mathToolArea.append(Translator.translateExceptionMessage("MTF_ERROR") + e.getMessage() + "\n \n");
                }

                return null;
            }
        };
        computing = true;
        computingTimer = new Timer();

        final ImageIcon computingOwlEyesOpen = new ImageIcon(getClass().getResource("icons/LogoOwlEyesOpen.png"));
        final ImageIcon computingOwlEyesHalfOpen = new ImageIcon(getClass().getResource("icons/LogoOwlEyesHalfOpen.png"));
        final ImageIcon computingOwlEyesClosed = new ImageIcon(getClass().getResource("icons/LogoOwlEyesClosed.png"));

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
         * Wichtig: Wenn command im Namen 'plot' enthält -> Rotation stoppen,
         * falls diese aktiv ist.
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
         * Wichtig: Wenn command im Namen 'plot' enthält -> Rotation stoppen,
         * falls diese aktiv ist.
         */
        stopPossibleRotation();
        String line = inputField.getText();
        inputField.setText("approx(" + line + ")");
        executeCommand();
    }//GEN-LAST:event_approxButtonActionPerformed

    private void latexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_latexButtonActionPerformed
        /**
         * Wichtig: Wenn command im Namen 'plot' enthält -> Rotation stoppen,
         * falls diese aktiv ist.
         */
        stopPossibleRotation();
        String line = inputField.getText();
        inputField.setText("latex(" + line + ")");
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
        if (s.equals("div")) {
            return 0;
        }
        if (s.equals("fac")) {
            return 0;
        }
        if (s.equals("gcd")) {
            return 1;
        }
        if (s.equals("int")) {
            return 1;
        }
        if (s.equals("laplace")) {
            return 0;
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

        if (s.equals("approx")) {
            return 0;
        }
        if (s.equals("clear")) {
            return 0;
        }
        if (s.equals("def")) {
            return 0;
        }
        if (s.equals("deffuncs")) {
            return 0;
        }
        if (s.equals("defvars")) {
            return 0;
        }
        if (s.equals("euler")) {
            return 0;
        }
        if (s.equals("expand")) {
            return 0;
        }
        if (s.equals("latex")) {
            return 0;
        }
        if (s.equals("pi")) {
            return 0;
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
        if (s.equals("solve")) {
            return 0;
        }
        if (s.equals("solvedeq")) {
            return 5;
        }
        if (s.equals("table")) {
            return 0;
        }
        if (s.equals("tangent")) {
            return 1;
        }
        if (s.equals("taylordeq")) {
            return 5;
        }
        if (s.equals("undef")) {
            return 0;
        }
        if (s.equals("undefall")) {
            return 0;
        }

        /**
         * Default-Case! Sollte nie eintreten.
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
            operatorChoice.setSelectedIndex(0);
            inputField.setSelectionStart(inputField.getSelectionStart() - number_of_commata - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());

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
            commandChoice.setSelectedIndex(0);
            inputField.setSelectionStart(inputField.getSelectionStart() - number_of_commata - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());

        }
        inputField.requestFocus();
    }//GEN-LAST:event_commandChoiceActionPerformed

    private void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                /**
                 * Wichtig: Wenn command im Namen 'plot' enthält -> Rotation
                 * stoppen, falls diese aktiv ist.
                 */
                stopPossibleRotation();
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
         * Wichtig: Wenn command im Namen 'plot' enthält -> Rotation stoppen,
         * falls diese aktiv ist.
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
            legendLabel.setText("<html><b><u>Legende</u></b></<html>");
            validate();
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == legendLabel) {
            legendLabel.setText("<html><b>Legende</b></<html>");
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
    private javax.swing.JMenu menuMathTool;
    private javax.swing.JComboBox operatorChoice;
    private javax.swing.JButton rotateButton;
    // End of variables declaration//GEN-END:variables

}
