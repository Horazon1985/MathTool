package mathtool;

import expressionbuilder.AnalysisMethods;
import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;
import expressionbuilder.GraphicPresentationOfFormula;
import expressionbuilder.NumericalMethods;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Hashtable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class MathToolForm extends javax.swing.JFrame implements KeyListener{
    private Thread threadRotate;
    private boolean bStart;
    private boolean bThreadStarted;
    JTextArea mathToolArea;
    
    GraphicMethods2D graphicMethods2D;
    GraphicMethods3D graphicMethods3D;
    GraphicPresentationOfFormula graphicPresentationOfFormula;
    NumericalMethods numericalMethods;
    AnalysisMethods analysisMethods;

    /** Diese Objekte werden im Laufe des Programms erweitert.
     * Sie enthalten die im Laufe des Programms definierten Variablen und Funktionen.
     */
    static Hashtable<String, Double> definedVars = new Hashtable<String, Double>();
    static HashSet definedVarsSet = new HashSet();
    static Hashtable<String, Expression> definedFunctions = new Hashtable<String, Expression>();
    public Hashtable<Integer, String> listOfCommands = new Hashtable<Integer, String>();
    public int command_count = 0;
    
    public MathToolForm() {
        initComponents();
        this.setLayout(null);
        this.bStart = false;
        this.bThreadStarted=false;
        InputField.addKeyListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e){
                InputField.requestFocus();
            }
        });
        /**Objekte ausrichten
         */
        
        /**Eingabefelder ausrichten
         */
        InputField.setBounds(10, 530, 650, 30);
        
        /**Ausgabefeld ausrichten
         */
        mathToolArea = new JTextArea();
        add(mathToolArea);
        mathToolArea.setBounds(10, 20, 750, 500);
        mathToolArea.setEditable(false);
        mathToolArea.setLineWrap(true);
        mathToolArea.setWrapStyleWord(true);        
        JScrollPane scrollPane = new JScrollPane(mathToolArea, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10,20,750,500);
        add(scrollPane);

        /**Buttons ausrichten
         */
        InputButton.setBounds(660, 530, 100, 30);
        RotateButton.setBounds(910, 530, 200, 30);
        RotateButton.setVisible(false);
        
        /**2D-Grafikobjekte initialisieren
         */
        graphicMethods2D = new GraphicMethods2D();
        add(graphicMethods2D);
        graphicMethods2D.setBounds(770, 20, 500, 500);
        repaint();
        graphicMethods2D.setVisible(false);
        
        /**3D-Grafikobjekte initialisieren
         */
        graphicMethods3D = new GraphicMethods3D();
        
        
        add(graphicMethods3D);
        graphicMethods3D.setBounds(770, 20, 500, 500);
        repaint();
        graphicMethods3D.setVisible(false);

        /**Sonstige Grafikobjekte initialisieren
         */
        graphicPresentationOfFormula = new GraphicPresentationOfFormula();
        add(graphicPresentationOfFormula);
        graphicPresentationOfFormula.setBounds(770, 20, 500, 500);
        repaint();
        graphicPresentationOfFormula.setVisible(false);
        
        /**Numerische Objekte initialisieren
         */
        numericalMethods = new NumericalMethods();
        
        /**Analytische Objekte initialisieren
         */
        analysisMethods = new AnalysisMethods();
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        InputButton = new javax.swing.JButton();
        InputField = new javax.swing.JTextField();
        RotateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        InputButton.setText("Eingabe");
        InputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InputButtonActionPerformed(evt);
            }
        });

        InputField.setText("plot(x+y,-1,1,-1,1)");

        RotateButton.setText("3D-Graphen rotieren lassen");
        RotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RotateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(InputField, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(InputButton)
                        .addGap(76, 76, 76))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(RotateButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(335, 335, 335)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InputButton)
                    .addComponent(InputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(RotateButton)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void execute(){
    /*       GraphicPresentationOfFormula gr = new GraphicPresentationOfFormula();
        try{
            Expression ex = Expression.build("1258", new HashSet());
            Graphics g = graphicPresentationOfFormula.getGraphics();
            System.out.println(gr.getHeightOfFormula(g, ex));
        } catch (Exception e){
        }
    */        
        MathCommandCompiler mcc = new MathCommandCompiler();
        Command c = new Command();
        boolean valid_command = false;
        
        String s = InputField.getText();

        /** Befehl loggen!
         */
        listOfCommands.put(command_count, s);
        command_count++;
        
        /**
        Zunächst: es wird geschaut, ob die Zeile einen Befehl bildet.
        Ja -> Befehl ausführen.
        Nein -> Prüfen, ob dies ein mathematischer ausdruck ist.
         */
        try{
     
            /** Falls es ein Grafikbefehl ist -> entsprechendes Panel sichtbar machen und das andere unsichtbar.
             */
            String[] com = Expression.getOperatorAndArguments(s);
            String[] params = Expression.getArguments(com[1]);
            if ((com[0].equals("plot")) && (params.length == 3)){
                graphicMethods2D.setVisible(true);
                graphicMethods3D.setVisible(false);
                RotateButton.setVisible(false);
                repaint();
            } else
            if ((com[0].equals("plot")) && (params.length == 5)){
                graphicMethods2D.setVisible(false);
                graphicMethods3D.setVisible(true);
                RotateButton.setVisible(true);
                repaint();
            } else {
                RotateButton.setVisible(false);
            }

            for (int i = 0; i < MathCommandCompiler.commands.length; i++){
                if (com[0].equals(MathCommandCompiler.commands[i])){
                    valid_command = true;
                }
            }
            if (valid_command){
                mathToolArea.append(s + "\n");
                mcc.executeCommand(s, mathToolArea, numericalMethods, graphicMethods2D, graphicMethods3D, 
                        definedVars, definedVarsSet);
            }
            
        } catch(ExpressionException e){
            /** Falls es ein gültiger Befehl war (unabhängig davon, ob dieser Fehler enthield oder nicht)
             * -> abbrechen und NICHT weiter prüfen, ob es sich um einen Ausdruck handeln könnte.
             */
            if(valid_command){
                mathToolArea.append("FEHLER: " + e.getMessage() + "\n");
                return;
            }
            /** Analog wie oben.
             */
        } catch (EvaluationException e){
            if(valid_command){
                mathToolArea.append("FEHLER: " + e.getMessage() + "\n");
                return;
            }
        }

        /** Falls es ein gültiger Befehl war (unabhängig davon, ob dieser Fehler enthield oder nicht)
         * -> abbrechen und NICHT weiter prüfen, ob es sich um einen Ausdruck handeln könnte.
         */
        if(valid_command){
            return;
        }

        try{
        
            Expression expr = Expression.build(s, new HashSet());
            //Falls man den Ausdruck noch vereinfachen kann -> vereinfachen und auch ausgeben.

            /**Falls es bei Vereinfachungen zu Auswertungsfehlern kommt.
             * Z.B. 1/0 ist zwar ein gültiger Ausdruck, liefert aber beim Auswerten einen Fehler.
             */
            try{
                Expression expr_simplified = expr.evaluate(definedVarsSet);
                expr_simplified = expr_simplified.simplify();
                if (expr.equals(expr_simplified)){
                    mathToolArea.append(expr.writeFormula() + "\n");
                //Falls man den Ausdruck nicht vereinfachen kann -> Ausdruck ausgeben.
                } else {
                    mathToolArea.append(expr.writeFormula() + " = " + expr_simplified.writeFormula() + "\n");
                }
                return;
            } catch (EvaluationException e){
                mathToolArea.append(expr.writeFormula() + "\n");
                mathToolArea.append("FEHLER: " + e.getMessage() + "\n");
                return;
            } 
        
        } catch (ExpressionException e){
            mathToolArea.append("FEHLER: " + e.getMessage() + "\n");
        }
        
    }
    private void InputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputButtonActionPerformed
        execute();
    }//GEN-LAST:event_InputButtonActionPerformed

    private void RotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RotateButtonActionPerformed
        if(!bStart){
            this.threadRotate = new Thread(graphicMethods3D, "rotateGraph");
            bStart = true;
            graphicMethods3D.setBStart(true);
            threadRotate.start();
            RotateButton.setText("Rotation stoppen");
        } else {
            bStart = false;
            graphicMethods3D.setBStart(false);
            threadRotate.interrupt();
            RotateButton.setText("3D-Graphen rotieren lassen");
        }
    }//GEN-LAST:event_RotateButtonActionPerformed

    
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
                MyMathToolForm.setTitle("MathTool for Analysis and Numerical Computation");
                MyMathToolForm.setBounds(20,50,1300,650);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton InputButton;
    private javax.swing.JTextField InputField;
    private javax.swing.JButton RotateButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
      
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
        if(KeyEvent.VK_ENTER==e.getKeyCode()){
            execute();
            
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
       
    }
}
