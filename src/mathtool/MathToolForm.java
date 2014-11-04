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
    private final ComputingDialogGUI computingDialog = new ComputingDialogGUI();
    private final JLabel legendLabel = new JLabel("<html><b>Legende</b></<html>");

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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                inputField.requestFocus();
            }
        });

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

                if (!typeGraphic.equals(typeGraphic.NONE)) {
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
        } else if (command_name.equals("solve") || (command_name.equals("tangent") && ((HashMap) c.getParams()[1]).size() == 1)) {
            graphicMethods2D.setVisible(true);
            typeGraphic = TypeGraphic.GRAPH2D;
        } else if (command_name.equals("solvedgl")) {
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
        MathToolMenuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenuItemQuit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MenuItemHelp = new javax.swing.JMenuItem();
        MenuItemAbout = new javax.swing.JMenuItem();

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
        inputField.setText("sgn(((-2)*x)/(5*y))");
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldKeyPressed(evt);
            }
        });
        getContentPane().add(inputField);
        inputField.setBounds(10, 336, 490, 22);

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
        operatorChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Operator", "diff()", "div()", "fac()", "gcd()", "int()", "laplace()", "lcm()", "mod()", "prod()", "sum()", "taylor()" }));
        operatorChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operatorChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(operatorChoice);
        operatorChoice.setBounds(420, 370, 130, 23);

        commandChoice.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        commandChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Befehl", "approx()", "clear()", "def()", "deffuncs()", "defvars()", "euler()", "latex()", "pi()", "plot2d()", "plot3d()", "plotcurve()", "plotpolar()", "solve()", "solvedgl()", "solveexact()", "tangent()", "taylordgl()", "undef()", "undefall()" }));
        commandChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(commandChoice);
        commandChoice.setBounds(560, 370, 115, 23);

        clearButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        getContentPane().add(clearButton);
        clearButton.setBounds(280, 370, 130, 30);

        jMenu1.setText("Datei");

        MenuItemQuit.setText("Quit");
        MenuItemQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemQuitActionPerformed(evt);
            }
        });
        jMenu1.add(MenuItemQuit);

        MathToolMenuBar.add(jMenu1);

        jMenu2.setText("MathTool");

        MenuItemHelp.setText("Hilfe");
        MenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemHelpActionPerformed(evt);
            }
        });
        jMenu2.add(MenuItemHelp);

        MenuItemAbout.setText("Über MathTool");
        MenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemAboutActionPerformed(evt);
            }
        });
        jMenu2.add(MenuItemAbout);

        MathToolMenuBar.add(jMenu2);

        setJMenuBar(MathToolMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void executeCommand() {
        cancelButton.setVisible(true);
        inputButton.setVisible(false);
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

                    /**
                     * Falls es ein Grafikbefehl ist -> entsprechendes Panel
                     * sichtbar machen und alle anderen unsichtbar.
                     */
                    String[] com = Expression.getOperatorAndArguments(command);
                    String[] params = Expression.getArguments(com[1]);

                    for (int i = 0; i < MathCommandCompiler.commands.length; i++) {
                        if (com[0].equals(MathCommandCompiler.commands[i])) {
                            valid_command = true;
                        }
                    }
                    if (valid_command) {
                        mathToolArea.append(command + "\n \n");
                        /**
                         * Wichtig: Wenn command im Namen 'plot' enthält ->
                         * Rotation stoppen, falls diese aktiv ist.
                         */
                        if (startRotate && com[0].contains("plot")) {
                            startRotate = false;
                            threadRotate.interrupt();
                        }

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
                        mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
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
                    } catch (EvaluationException e) {
                        mathToolArea.append(expr.writeFormula(true) + "\n \n");
                        if (e.getMessage().equals("Berechnung abgebrochen.")) {
                            mathToolArea.append(e.getMessage() + "\n \n");
                        } else {
                            mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
                        }
                    }

                } catch (ExpressionException e) {
                    mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
                }
                return null;
            }
        };
        computing = true;
        computingTimer = new Timer();
        computingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                computingDialog.setVisible(true);
            }
        }, 1000);
        computingSwingWorker.execute();
    }

    private void inputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputButtonActionPerformed
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

    private void MenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemHelpActionPerformed
        HelpDialogGUI helpDialogGUI = new HelpDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        helpDialogGUI.setVisible(true);
    }//GEN-LAST:event_MenuItemHelpActionPerformed

    private void MenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemAboutActionPerformed
        DevelopersDialogGUI aboutMathToolGUI = new DevelopersDialogGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        aboutMathToolGUI.setVisible(true);
    }//GEN-LAST:event_MenuItemAboutActionPerformed

    private void approxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approxButtonActionPerformed
        String line = inputField.getText();
        inputField.setText("approx(" + line + ")");
        executeCommand();
    }//GEN-LAST:event_approxButtonActionPerformed

    private void latexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_latexButtonActionPerformed
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

    private void operatorChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operatorChoiceActionPerformed
        if (operatorChoice.getSelectedIndex() > 0) {
            inputField.replaceSelection((String) operatorChoice.getSelectedItem());
            operatorChoice.setSelectedIndex(0);
            inputField.setSelectionStart(inputField.getSelectionStart() - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());
        }
        inputField.requestFocus();
    }//GEN-LAST:event_operatorChoiceActionPerformed

    private void commandChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commandChoiceActionPerformed
        if (commandChoice.getSelectedIndex() > 0) {
            inputField.replaceSelection((String) commandChoice.getSelectedItem());
            commandChoice.setSelectedIndex(0);
            inputField.setSelectionStart(inputField.getSelectionStart() - 1);
            inputField.setSelectionEnd(inputField.getSelectionStart());
        }
        inputField.requestFocus();
    }//GEN-LAST:event_commandChoiceActionPerformed

    private void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:

//                double x = -3.1;
//                int y = -3;
//                System.out.println(x==y);
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
        inputField.setText("clear()");
        executeCommand();
    }//GEN-LAST:event_clearButtonActionPerformed

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == legendLabel) {
            if (typeGraphic.equals(typeGraphic.GRAPH2D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethods2D.getInstructions(), graphicMethods2D.getColors(), graphicMethods2D.getExpressions());
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(typeGraphic.GRAPH3D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethods3D.getInstructions(), graphicMethods3D.getExpression());
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(typeGraphic.CURVE2D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethodsCurves2D.getInstructions(), graphicMethodsCurves2D.getExpressions());
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(typeGraphic.CURVE3D)) {
                LegendGUI legendGUI = new LegendGUI(this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                        GraphicMethodsCurves3D.getInstructions(), graphicMethodsCurves3D.getExpressions());
                legendGUI.setVisible(true);
            } else if (typeGraphic.equals(typeGraphic.POLARGRAPH2D)) {
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
                myMathToolForm.setBounds(20, 50, 1300, 670);
                myMathToolForm.setExtendedState(MAXIMIZED_BOTH);
                myMathToolForm.getContentPane().setBackground(new Color(255, 150, 0));
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar MathToolMenuBar;
    private javax.swing.JMenuItem MenuItemAbout;
    private javax.swing.JMenuItem MenuItemHelp;
    private javax.swing.JMenuItem MenuItemQuit;
    private javax.swing.JButton approxButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JComboBox commandChoice;
    private javax.swing.JButton inputButton;
    private javax.swing.JTextField inputField;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JButton latexButton;
    private javax.swing.JComboBox operatorChoice;
    private javax.swing.JButton rotateButton;
    // End of variables declaration//GEN-END:variables

}
