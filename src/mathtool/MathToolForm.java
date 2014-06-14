package mathtool;

import expressionbuilder.Expression;
import java.awt.Graphics; 
import expressionbuilder.ExpressionException;
import expressionbuilder.EvaluationException;
import expressionbuilder.AnalysisMethods;
import expressionbuilder.NumericalMethods;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;

import java.util.HashSet;
import java.util.Hashtable;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.KeyListener; 


public class MathToolForm extends javax.swing.JFrame {

    JTextArea mathToolArea;
    
    GraphicMethods2D graphicMethods2D;
    GraphicMethods3D graphicMethods3D;
    NumericalMethods numericalMethods;
    AnalysisMethods analysisMethods;

    /** Diese Objekte werden im Laufe des Programms erweitert.
     * Sie enthalten die im Laufe des Programms definierten Variablen und Funktionen.
     */
    static Hashtable<String, Double> definedVars = new Hashtable<String, Double>();
    static HashSet definedVarsSet = new HashSet();
    static Hashtable<String, Expression> definedFunctions = new Hashtable<String, Expression>();
    
    public MathToolForm() {
        initComponents();
        this.setLayout(null);

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        InputButton.setText("Eingabe");
        InputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InputButtonActionPerformed(evt);
            }
        });

        InputField.setText("define(x = 5)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(InputField, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(InputButton)
                .addGap(76, 76, 76))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(335, 335, 335)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InputButton)
                    .addComponent(InputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(107, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void InputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputButtonActionPerformed
        
        MathCommandCompiler mcc = new MathCommandCompiler();
        Command c = new Command();
        
        String s = InputField.getText();

        /**
        Zunächst: es wird geschaut, ob der Befehl ein mathematischer Ausdruck ist.
        Ja -> Ausdruck (und evtl. vereinfachten Ausdruck) ausgeben.
        Nein -> Weiter.
         */
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
        }
        
        try{
     
            mathToolArea.append(s + "\n");
            
            String[] com = Expression.getOperatorAndArguments(s);
            String[] params = Expression.getArguments(com[1]);
            if ((com[0].equals("plot")) && (params.length == 3)){
                graphicMethods2D.setVisible(true);
                graphicMethods3D.setVisible(false);
                repaint();
            }
            if ((com[0].equals("plot")) && (params.length == 5)){
                graphicMethods2D.setVisible(false);
                graphicMethods3D.setVisible(true);
                repaint();
            }
/**            
            definedVars.put("x", (double) 4);
            definedVars.put("y", (double) 7);
            definedVarsSet.add("x");
            definedVarsSet.add("y");
*/            
            mcc.executeCommand(s, mathToolArea, numericalMethods, graphicMethods2D, graphicMethods3D, 
                    definedVars, definedVarsSet);
            
        } catch(ExpressionException e){
            mathToolArea.append("FEHLER: " + e.getMessage() + "\n");
        } catch (EvaluationException e){
            mathToolArea.append("FEHLER: " + e.getMessage() + "\n");
        }
        
    }//GEN-LAST:event_InputButtonActionPerformed

    
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
    // End of variables declaration//GEN-END:variables
}
