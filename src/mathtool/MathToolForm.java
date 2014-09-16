package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;
import expressionbuilder.GraphicPresentationOfFormula;
import expressionbuilder.SimplifyFunctionalEquations;
import expressionbuilder.TypeFunction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;


public class MathToolForm extends javax.swing.JFrame implements KeyListener{
    private Thread threadRotate;
    private boolean startRotate;
    JTextArea mathToolArea;
    JEditorPane helpArea;
    JScrollPane scrollPane;

    GraphicMethods2D graphicMethods2D;
    GraphicMethods3D graphicMethods3D;
    GraphicPresentationOfFormula graphicPresentationOfFormula;

    /** Diese Objekte werden im Laufe des Programms erweitert.
     * Sie enthalten die im Laufe des Programms definierten Variablen und Funktionen.
     */ 
    static Hashtable<String, Expression> definedVars = new Hashtable<String, Expression>();
    static HashSet definedVarsSet = new HashSet();
    static Hashtable<String, Expression> definedFunctions = new Hashtable<String, Expression>();
    public Hashtable<Integer, String> listOfCommands = new Hashtable<Integer, String>();
    /** Variablen, die eine Rolle beim Loggen der Befehle spielen.
     * command_count = Anzahl der insgesamt geloggten Befehle (gültige UND ungültige!)
     * log_position = Index des aktuellen befehls, den man mittels Pfeiltasten ausgegeben haben möchte.
     */
    public int command_count = 0;
    public int log_position = 0;

    
    public MathToolForm() {
        initComponents();
        this.setLayout(null);
        this.startRotate = false;
        InputField.addKeyListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e){
                InputField.requestFocus();
            }
        });
        /** Objekte ausrichten
         */
        
        /** Eingabefelder ausrichten
         */
        InputField.setBounds(10, 530, 1160, 30);
        
        /** Ausgabefeld ausrichten
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

        /** Buttons ausrichten
         */
        InputButton.setBounds(1180, 530, 100, 30);
        ApproxButton.setBounds(10, 570, 105, 30);
        LatexButton.setBounds(125, 570, 120, 30);
        RotateButton.setBounds(900, 530, 220, 30);
        RotateButton.setVisible(false);

        /** Auswahlmenüs ausrichten
         */
        OperatorChoice.setBounds(250, 573, 120, 30);
        OperatorChoice.addItem("Operator");
        OperatorChoice.addItem("diff()");
        OperatorChoice.addItem("div()");
        OperatorChoice.addItem("gcd()");
        OperatorChoice.addItem("int()");
        OperatorChoice.addItem("laplace()");
        OperatorChoice.addItem("lcm()");
        OperatorChoice.addItem("prod()");
        OperatorChoice.addItem("sum()");
        OperatorChoice.addItem("taylor()");

        CommandChoice.setBounds(375, 573, 120, 30);
        CommandChoice.addItem("approx()");
        CommandChoice.addItem("clear()");
        CommandChoice.addItem("def()");
        CommandChoice.addItem("defvars()");
        CommandChoice.addItem("euler()");
        CommandChoice.addItem("latex()");
        CommandChoice.addItem("pi()");
        CommandChoice.addItem("plot()");
        CommandChoice.addItem("solve()");
        CommandChoice.addItem("solvedgl()");
        CommandChoice.addItem("tangent()");
        CommandChoice.addItem("taylordgl()");
        CommandChoice.addItem("undef()");
        CommandChoice.addItem("undefall()");
        
        /** 2D-Grafikobjekte initialisieren
         */
        graphicMethods2D = new GraphicMethods2D();
        add(graphicMethods2D);
        graphicMethods2D.setBounds(770, 20, 500, 500);
        repaint();
        graphicMethods2D.setVisible(false);
        
        /** 3D-Grafikobjekte initialisieren
         */
        graphicMethods3D = new GraphicMethods3D();
        add(graphicMethods3D);
        graphicMethods3D.setBounds(770, 20, 500, 500);
        repaint();
        graphicMethods3D.setVisible(false);

        /** Sonstige Grafikobjekte initialisieren
         */
        graphicPresentationOfFormula = new GraphicPresentationOfFormula();
        add(graphicPresentationOfFormula);
        graphicPresentationOfFormula.setBounds(770, 20, 500, 500);
        repaint();
        graphicPresentationOfFormula.setVisible(false);

        /** Icons auf die Buttons setzen
         */
        Icon icon = new ImageIcon("ApproxButtonImage.png");
        ApproxButton.setIcon(icon);
        icon = new ImageIcon("LatexButtonImage.png");
        LatexButton.setIcon(icon);
        icon = new ImageIcon("InputButtonImage.png");
        InputButton.setIcon(icon);
        
    }

    
    private void showLoggedCommand(int i){
        InputField.setText(listOfCommands.get(i));
    }
    
    
    private void activatePanelsForGraphs(String command_name, String[] params){

        if ((command_name.equals("plot")) && (params.length > 2) && (params.length != 5)){
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            InputField.setBounds(10, 530, 640, 30);
            InputButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            RotateButton.setVisible(false);
            repaint();
        } else
        if ((command_name.equals("plot")) && (params.length == 5) && (params[0].contains("="))){
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            InputField.setBounds(10, 530, 640, 30);
            InputButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            RotateButton.setVisible(false);
            repaint();
        } else
        if ((command_name.equals("plot")) && (params.length == 5)){
            try{
                Double.parseDouble(params[1]);
                Double.parseDouble(params[2]);
                Double.parseDouble(params[3]);
                Double.parseDouble(params[4]);
                if (params[0].contains("=")){
                    //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
                    mathToolArea.setBounds(10, 20, 750, 500);
                    scrollPane.setBounds(10, 20, 750, 500);
                    InputField.setBounds(10, 530, 640, 30);
                    InputButton.setBounds(660, 530, 100, 30);
                    //Grafik-Panels sichtbar machen
                    graphicMethods2D.setVisible(true);
                    graphicMethods3D.setVisible(false);
                    RotateButton.setVisible(false);
                } else {
                    //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
                    mathToolArea.setBounds(10, 20, 750, 500);
                    scrollPane.setBounds(10, 20, 750, 500);
                    InputField.setBounds(10, 530, 640, 30);
                    InputButton.setBounds(660, 530, 100, 30);
                    //Grafik-Panels sichtbar machen
                    graphicMethods2D.setVisible(false);
                    graphicMethods3D.setVisible(true);
                    RotateButton.setVisible(true);
                }
            } catch (NumberFormatException e){
                //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
                mathToolArea.setBounds(10, 20, 750, 500);
                scrollPane.setBounds(10, 20, 750, 500);
                InputField.setBounds(10, 530, 640, 30);
                InputButton.setBounds(660, 530, 100, 30);
                //Grafik-Panels sichtbar machen
                graphicMethods2D.setVisible(true);
                graphicMethods3D.setVisible(false);
                RotateButton.setVisible(false);
            }
            repaint();
        } else
        if (command_name.equals("solve")){
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            InputField.setBounds(10, 530, 640, 30);
            InputButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            RotateButton.setVisible(false);
            repaint();
        } else 
        if (command_name.equals("solvedgl")){
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            InputField.setBounds(10, 530, 640, 30);
            InputButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            RotateButton.setVisible(false);
            repaint();
        } else 
        if (command_name.equals("tangent")){
            //Konsolenmaße abpassen, wenn eine Graphic eingeblendet wird.
            mathToolArea.setBounds(10, 20, 750, 500);
            scrollPane.setBounds(10, 20, 750, 500);
            InputField.setBounds(10, 530, 640, 30);
            InputButton.setBounds(660, 530, 100, 30);
            //Grafik-Panels sichtbar machen
            graphicMethods2D.setVisible(true);
            graphicMethods3D.setVisible(false);
            RotateButton.setVisible(false);
            repaint();
        } else {
            RotateButton.setVisible(false);
        }
    
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        InputButton = new javax.swing.JButton();
        InputField = new javax.swing.JTextField();
        RotateButton = new javax.swing.JButton();
        LatexButton = new javax.swing.JButton();
        ApproxButton = new javax.swing.JButton();
        OperatorChoice = new java.awt.Choice();
        CommandChoice = new java.awt.Choice();
        MathToolMenuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenuItemQuit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MenuItemHelp = new javax.swing.JMenuItem();
        MenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(MathToolForm.class.getResource("MathToolIcon.png")));
        getContentPane().setLayout(null);

        InputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InputButtonActionPerformed(evt);
            }
        });
        getContentPane().add(InputButton);
        InputButton.setBounds(518, 335, 70, 30);
        getContentPane().add(InputField);
        InputField.setBounds(10, 336, 490, 20);

        RotateButton.setText("3D-Graphen rotieren lassen");
        RotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RotateButtonActionPerformed(evt);
            }
        });
        getContentPane().add(RotateButton);
        RotateButton.setBounds(10, 410, 165, 23);

        LatexButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LatexButtonActionPerformed(evt);
            }
        });
        getContentPane().add(LatexButton);
        LatexButton.setBounds(180, 370, 150, 30);

        ApproxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ApproxButtonActionPerformed(evt);
            }
        });
        getContentPane().add(ApproxButton);
        ApproxButton.setBounds(10, 370, 150, 30);
        getContentPane().add(OperatorChoice);
        OperatorChoice.setBounds(340, 380, 130, 20);
        getContentPane().add(CommandChoice);
        CommandChoice.setBounds(480, 380, 130, 20);

        jMenu1.setText("Datei");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

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
    private void execute(){
        
        boolean valid_command = false;
        
        /**Leerzeichen werden im Vorfeld beseitigt.
         */
        String s = InputField.getText().replaceAll(" ", "");

        /** Befehl loggen!
         */
        listOfCommands.put(command_count, s);
        command_count++;
        log_position = command_count;
        
        /**
        Zunächst: es wird geprüft, ob die Zeile einen Befehl bildet.
        Ja -> Befehl ausführen.
        Nein -> Prüfen, ob dies ein mathematischer ausdruck ist.
         */
        try{
     
            /** Falls es ein Grafikbefehl ist -> entsprechendes Panel sichtbar machen und das andere unsichtbar.
             */
            String[] com = Expression.getOperatorAndArguments(s);
            String[] params = Expression.getArguments(com[1]);
            activatePanelsForGraphs(com[0], params);
            
            for (int i = 0; i < MathCommandCompiler.commands.length; i++){
                if (com[0].equals(MathCommandCompiler.commands[i])){
                    valid_command = true;
                }
            }
            if (valid_command){
                mathToolArea.append(s + "\n \n");
                MathCommandCompiler.executeCommand(s, mathToolArea, graphicMethods2D, graphicMethods3D, 
                        definedVars, definedVarsSet);
            }
            
        } catch(ExpressionException e){
            /** Falls es ein gültiger Befehl war (unabhängig davon, ob dieser Fehler enthielt oder nicht)
             * -> abbrechen und NICHT weiter prüfen, ob es sich um einen Ausdruck handeln könnte.
             */
            if(valid_command){
                mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
                return;
            }
        } catch (EvaluationException e){
            /** Analog wie oben.
             */
            if(valid_command){
                mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
                return;
            }
        }

        /** Falls es ein gültiger Befehl war (unabhängig davon, ob dieser Fehler enthield oder nicht)
         * -> abbrechen und NICHT weiter prüfen, ob es sich um einen Ausdruck handeln könnte.
         */
        if(valid_command){
            return;
        }

        /** Nun wird geprüft, ob es sich bei s um einen gültigen ausdruck handelt.
         * Ja -> ggfs. vereinfachen und ausgeben.
         */
        try{
        
            Expression expr = Expression.build(s, new HashSet());

            /**Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
             * Z.B. 1/0 ist zwar ein gültiger Ausdruck, liefert aber beim Auswerten einen Fehler.
             */
            try{
                Expression expr_simplified = expr.evaluate(definedVarsSet);
                expr_simplified = expr_simplified.simplify();
                if (expr.equals(expr_simplified)){
                    /**Falls man den Ausdruck nicht vereinfachen kann -> Ausdruck ausgeben.
                     */
                    mathToolArea.append(expr.writeFormula(true) + "\n \n");
                } else {
                    mathToolArea.append(expr.writeFormula(true) + " = " + expr_simplified.writeFormula(true) + "\n \n");
                }
                return;
            } catch (EvaluationException e){
                mathToolArea.append(expr.writeFormula(true) + "\n \n");
                mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
                return;
            } 
        
        } catch (ExpressionException e){
            mathToolArea.append("FEHLER: " + e.getMessage() + "\n \n");
        }
        
    }

    private void InputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputButtonActionPerformed
        execute();
    }//GEN-LAST:event_InputButtonActionPerformed

    
    private void RotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RotateButtonActionPerformed
        if(!startRotate){
            this.threadRotate = new Thread(graphicMethods3D, "rotateGraph");
            startRotate = true;
            graphicMethods3D.setStartRotate(true);
            threadRotate.start();
            RotateButton.setText("Rotation stoppen");
        } else {
            startRotate = false;
            graphicMethods3D.setStartRotate(false);
            threadRotate.interrupt();
            RotateButton.setText("3D-Graphen rotieren lassen");
        }
    }//GEN-LAST:event_RotateButtonActionPerformed

    private void MenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemHelpActionPerformed
        HelpDialogGUI helpDialogGUI = new HelpDialogGUI();
        helpDialogGUI.setVisible(true);
    }//GEN-LAST:event_MenuItemHelpActionPerformed

    private void MenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemAboutActionPerformed
        DevelopersDialogGUI aboutMathToolGUI = new DevelopersDialogGUI();
        aboutMathToolGUI.setVisible(true);
    }//GEN-LAST:event_MenuItemAboutActionPerformed

    private void ApproxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ApproxButtonActionPerformed
        String line = InputField.getText();
        InputField.setText("approx(" + line + ")");
        execute();
        InputField.setText(line);
    }//GEN-LAST:event_ApproxButtonActionPerformed

    private void LatexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LatexButtonActionPerformed
        String line = InputField.getText();
        InputField.setText("latex(" + line + ")");
        execute();
        InputField.setText(line);
    }//GEN-LAST:event_LatexButtonActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void MenuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_MenuItemQuitActionPerformed

    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MathToolForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MathToolForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MathToolForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MathToolForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MathToolForm MyMathToolForm = new MathToolForm();
                MyMathToolForm.setVisible(true);
                MyMathToolForm.setTitle("MathTool - Mathematical Tool for Analysis and Numerical Computation");
                MyMathToolForm.setBounds(20,50,1300,670);
                MyMathToolForm.getContentPane().setBackground(new Color(255,150,0));
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ApproxButton;
    private java.awt.Choice CommandChoice;
    private javax.swing.JButton InputButton;
    private javax.swing.JTextField InputField;
    private javax.swing.JButton LatexButton;
    private javax.swing.JMenuBar MathToolMenuBar;
    private javax.swing.JMenuItem MenuItemAbout;
    private javax.swing.JMenuItem MenuItemHelp;
    private javax.swing.JMenuItem MenuItemQuit;
    private java.awt.Choice OperatorChoice;
    private javax.swing.JButton RotateButton;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
        if(KeyEvent.VK_ENTER == e.getKeyCode()){
            execute();
/**            
            try{
                Expression expr = Expression.build(InputField.getText(), new HashSet());
                Expression result = SimplifyFunctionalEquations.productOfTwoTrigonometricalFunctions(expr, TypeFunction.cos,
                        TypeFunction.tan, TypeFunction.sin);
                mathToolArea.append(result.writeFormula(true) + "\n \n");
            } catch (ExpressionException|EvaluationException ex){
            }
*/            
        }
        if(KeyEvent.VK_UP == e.getKeyCode()){
            if (log_position > 0){
                log_position--;
            }
            if (log_position == command_count - 1){
                log_position--;
            }
            showLoggedCommand(log_position);
        }
        if(KeyEvent.VK_DOWN == e.getKeyCode()){
            if (log_position < command_count - 1){
                log_position++;
            }
            showLoggedCommand(log_position);
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
