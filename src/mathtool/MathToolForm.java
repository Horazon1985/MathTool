package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;
import expressionbuilder.GraphicMethodsCurves2D;
import expressionbuilder.GraphicPresentationOfFormula;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class MathToolForm extends javax.swing.JFrame {

    private Thread threadRotate;
    private boolean startRotate;
    private boolean computing = false;
    private SwingWorker<Void, Void> computingSwingWorker;
    private Timer computingTimer;
    private ComputingDialogGUI computingDialog = new ComputingDialogGUI();

    JTextArea mathToolArea;
    JEditorPane helpArea;
    JScrollPane scrollPane;

    GraphicMethods2D graphicMethods2D;
    GraphicMethods3D graphicMethods3D;
    GraphicMethodsCurves2D graphicMethodsCurves2D;
    GraphicPresentationOfFormula graphicPresentationOfFormula;

    /**
     * Diese Objekte werden im Laufe des Programms erweitert. Sie enthalten die
     * im Laufe des Programms definierten Variablen und Funktionen.
     */
    static Hashtable<String, Expression> definedVars = new Hashtable<String, Expression>();
    static HashSet definedVarsSet = new HashSet();
    static Hashtable<String, Expression> definedFunctions = new Hashtable<String, Expression>();
    public Hashtable<Integer, String> listOfCommands = new Hashtable<Integer, String>();
    /**
     * Variablen, die eine Rolle beim Loggen der Befehle spielen. command_count
     * = Anzahl der insgesamt geloggten Befehle (gültige UND ungültige!)
     * log_position = Index des aktuellen befehls, den man mittels Pfeiltasten
     * ausgegeben haben möchte.
     */
    public int command_count = 0;
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
         * Objekte ausrichten
         */

        /**
         * Eingabefelder ausrichten
         */
        inputField.setBounds(10, 530, 1160, 30);

        /**
         * Ausgabefeld ausrichten
         */
        mathToolArea = new JTextArea();
        add(mathToolArea);
        mathToolArea.setBounds(10, 20, 1270, 500);
        mathToolArea.setEditable(false);
        mathToolArea.setLineWrap(true);
        mathToolArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(mathToolArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 20, 1270, 500);
        add(scrollPane);

        /**
         * Buttons ausrichten
         */
        inputButton.setBounds(1180, 530, 100, 30);
        cancelButton.setBounds(1180, 530, 100, 30);
        approxButton.setBounds(10, 570, 105, 30);
        latexButton.setBounds(125, 570, 120, 30);
        rotateButton.setBounds(900, 530, 220, 30);
        rotateButton.setVisible(false);
        cancelButton.setVisible(false);

        /**
         * Auswahlmenüs ausrichten
         */
        operatorChoice.setBounds(250, 570, 120, 30);

        commandChoice.setBounds(375, 570, 120, 30);

        /**
         * 2D-Grafikobjekte initialisieren
         */
        graphicMethods2D = new GraphicMethods2D();
        add(graphicMethods2D);
        graphicMethods2D.setBounds(770, 20, 500, 500);
        repaint();
        graphicMethods2D.setVisible(false);

        /**
         * 3D-Grafikobjekte initialisieren
         */
        graphicMethods3D = new GraphicMethods3D();
        add(graphicMethods3D);
        graphicMethods3D.setBounds(770, 20, 500, 500);
        repaint();
        graphicMethods3D.setVisible(false);

        /**
         * Sonstige Grafikobjekte initialisieren
         */
        graphicPresentationOfFormula = new GraphicPresentationOfFormula();
        add(graphicPresentationOfFormula);
        graphicPresentationOfFormula.setBounds(770, 20, 500, 500);
        repaint();
        graphicPresentationOfFormula.setVisible(false);
    }

    private void showLoggedCommand(int i) {
        inputField.setText(listOfCommands.get(i));
    }

    private void activatePanelsForGraphs(String command_name, String[] params) {

        if ((command_name.equals("plot")) && (params.length > 2) && (params.length != 5)) {
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            inputField.setBounds(10, 530, 640, 30);
            inputButton.setBounds(660, 530, 100, 30);
            cancelButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            rotateButton.setVisible(false);
            repaint();
        } else if ((command_name.equals("plot")) && (params.length == 5) && (params[0].contains("="))) {
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            inputField.setBounds(10, 530, 640, 30);
            inputButton.setBounds(660, 530, 100, 30);
            cancelButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            rotateButton.setVisible(false);
            repaint();
        } else if ((command_name.equals("plot")) && (params.length == 5)) {
            try {
                Double.parseDouble(params[1]);
                Double.parseDouble(params[2]);
                Double.parseDouble(params[3]);
                Double.parseDouble(params[4]);
                if (params[0].contains("=")) {
                    //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
                    mathToolArea.setBounds(10, 20, 750, 500);
                    scrollPane.setBounds(10, 20, 750, 500);
                    inputField.setBounds(10, 530, 640, 30);
                    inputButton.setBounds(660, 530, 100, 30);
                    cancelButton.setBounds(660, 530, 100, 30);
                    //Grafik-Panels sichtbar machen
                    graphicMethods2D.setVisible(true);
                    graphicMethods3D.setVisible(false);
                    rotateButton.setVisible(false);
                } else {
                    //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
                    mathToolArea.setBounds(10, 20, 750, 500);
                    scrollPane.setBounds(10, 20, 750, 500);
                    inputField.setBounds(10, 530, 640, 30);
                    inputButton.setBounds(660, 530, 100, 30);
                    cancelButton.setBounds(660, 530, 100, 30);
                    //Grafik-Panels sichtbar machen
                    graphicMethods2D.setVisible(false);
                    graphicMethods3D.setVisible(true);
                    rotateButton.setVisible(true);
                }
            } catch (NumberFormatException e) {
                //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
                mathToolArea.setBounds(10, 20, 750, 500);
                scrollPane.setBounds(10, 20, 750, 500);
                inputField.setBounds(10, 530, 640, 30);
                inputButton.setBounds(660, 530, 100, 30);
                cancelButton.setBounds(660, 530, 100, 30);
                //Grafik-Panels sichtbar machen
                graphicMethods2D.setVisible(true);
                graphicMethods3D.setVisible(false);
                rotateButton.setVisible(false);
            }
            repaint();
        } else if (command_name.equals("solve")) {
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            inputField.setBounds(10, 530, 640, 30);
            inputButton.setBounds(660, 530, 100, 30);
            cancelButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            rotateButton.setVisible(false);
            repaint();
        } else if (command_name.equals("solvedgl")) {
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            inputField.setBounds(10, 530, 640, 30);
            inputButton.setBounds(660, 530, 100, 30);
            cancelButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            rotateButton.setVisible(false);
            repaint();
        } else if (command_name.equals("tangent")) {
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            inputField.setBounds(10, 530, 640, 30);
            inputButton.setBounds(660, 530, 100, 30);
            cancelButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            rotateButton.setVisible(false);
            repaint();
        } else {
            rotateButton.setVisible(false);
        }

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

        inputButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mathtool/icons/InputButtonImage.png"))); // NOI18N
        inputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputButtonActionPerformed(evt);
            }
        });
        getContentPane().add(inputButton);
        inputButton.setBounds(518, 335, 100, 30);

        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldKeyPressed(evt);
            }
        });
        getContentPane().add(inputField);
        inputField.setBounds(10, 336, 490, 20);

        rotateButton.setText("3D-Graphen rotieren lassen");
        rotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(rotateButton);
        rotateButton.setBounds(10, 410, 165, 23);

        latexButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mathtool/icons/LatexButtonImage.png"))); // NOI18N
        latexButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                latexButtonActionPerformed(evt);
            }
        });
        getContentPane().add(latexButton);
        latexButton.setBounds(180, 370, 150, 30);

        approxButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mathtool/icons/ApproxButtonImage.png"))); // NOI18N
        approxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approxButtonActionPerformed(evt);
            }
        });
        getContentPane().add(approxButton);
        approxButton.setBounds(10, 370, 150, 30);

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mathtool/icons/CancelButtonImage.png"))); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelButton);
        cancelButton.setBounds(520, 410, 100, 30);

        operatorChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Operator", "diff()", "div()", "fac()", "gcd()", "int()", "laplace()", "lcm()", "mod()", "prod()", "sum()", "taylor()" }));
        operatorChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operatorChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(operatorChoice);
        operatorChoice.setBounds(340, 370, 130, 20);

        commandChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Befehl", "approx()", "clear()", "def()", "defvars()", "euler()", "latex()", "pi()", "plot()", "solve()", "solvedgl()", "tangent()", "taylordgl()", "undef()", "undefall()" }));
        commandChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandChoiceActionPerformed(evt);
            }
        });
        getContentPane().add(commandChoice);
        commandChoice.setBounds(480, 370, 75, 20);

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
            }

            @Override
            protected Void doInBackground() throws Exception {
                boolean valid_command = false;

                /**
                 * Leerzeichen werden im Vorfeld beseitigt.
                 */
                String s = inputField.getText().replaceAll(" ", "");

                /**
                 * Befehl loggen!
                 */
                listOfCommands.put(command_count, s);
                command_count++;
                log_position = command_count;

                /**
                 * Zunächst: es wird geprüft, ob die Zeile einen Befehl bildet.
                 * Ja -> Befehl ausführen. Nein -> Prüfen, ob dies ein
                 * mathematischer ausdruck ist.
                 */
                try {

                    /**
                     * Falls es ein Grafikbefehl ist -> entsprechendes Panel
                     * sichtbar machen und das andere unsichtbar.
                     */
                    String[] com = Expression.getOperatorAndArguments(s);
                    String[] params = Expression.getArguments(com[1]);

                    for (int i = 0; i < MathCommandCompiler.commands.length; i++) {
                        if (com[0].equals(MathCommandCompiler.commands[i])) {
                            valid_command = true;
                        }
                    }
                    if (valid_command) {
                        mathToolArea.append(s + "\n \n");
                        MathCommandCompiler.executeCommand(s, mathToolArea, graphicMethods2D, graphicMethods3D,
                                definedVars, definedVarsSet);
                        /**
                         * Falls es graphische befehle waren -> Grafik sichtbar
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
                 * Nun wird geprüft, ob es sich bei s um einen gültigen ausdruck
                 * handelt. Ja -> ggfs. vereinfachen und ausgeben.
                 */
                try {

                    Expression expr = Expression.build(s, new HashSet());

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
                        mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
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
            this.threadRotate = new Thread(graphicMethods3D, "rotateGraph");
            startRotate = true;
            graphicMethods3D.setStartRotate(true);
            threadRotate.start();
            rotateButton.setText("Rotation stoppen");
        } else {
            startRotate = false;
            graphicMethods3D.setStartRotate(false);
            threadRotate.interrupt();
            rotateButton.setText("3D-Graphen rotieren lassen");
        }
    }//GEN-LAST:event_rotateButtonActionPerformed

    private void MenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemHelpActionPerformed
        HelpDialogGUI helpDialogGUI = new HelpDialogGUI();
        helpDialogGUI.setVisible(true);
    }//GEN-LAST:event_MenuItemHelpActionPerformed

    private void MenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemAboutActionPerformed
        DevelopersDialogGUI aboutMathToolGUI = new DevelopersDialogGUI();
        aboutMathToolGUI.setVisible(true);
    }//GEN-LAST:event_MenuItemAboutActionPerformed

    private void approxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approxButtonActionPerformed
        String line = inputField.getText();
        inputField.setText("approx(" + line + ")");
        executeCommand();
        inputField.setText(line);
    }//GEN-LAST:event_approxButtonActionPerformed

    private void latexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_latexButtonActionPerformed
        String line = inputField.getText();
        inputField.setText("latex(" + line + ")");
        executeCommand();
        inputField.setText(line);
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
                executeCommand();
                break;

            case KeyEvent.VK_UP:
                if (log_position > 0) {
                    log_position--;
                }
                if (log_position == command_count - 1) {
                    log_position--;
                }
                showLoggedCommand(log_position);
                break;

            case KeyEvent.VK_DOWN:
                if (log_position < command_count - 1) {
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
