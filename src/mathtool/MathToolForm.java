package mathtool;

import expressionbuilder.Expression;
import java.awt.*; 
import expressionbuilder.ExpressionException;
import expressionbuilder.AnalysisMethods;
import expressionbuilder.NumericalMethods;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;

import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class MathToolForm extends javax.swing.JFrame {

    JTextArea mathToolArea;
    
    GraphicMethods2D graphicMethods2D;
    GraphicMethods3D graphicMethods3D;
    NumericalMethods numericalMethods;
    AnalysisMethods analysisMethods;

    public MathToolForm() {
        initComponents();
        this.setLayout(null);

        //Objekte ausrichten
        
        //Eingabefelder ausrichten
        InputField.setBounds(10, 530, 1150, 25);
        
        //Ausgabefeld ausrichten
        mathToolArea = new JTextArea();
        add(mathToolArea);
        mathToolArea.setBounds(10, 20, 1250, 500);
        mathToolArea.setEditable(false);
        
        //Labels ausrichten
        
        //Buttons ausrichten
        InputButton.setBounds(1160, 530, 100, 25);
        
        //Grafik-Objekte initialisieren
        graphicMethods2D = new GraphicMethods2D();
        add(graphicMethods2D);
        repaint();
        
        //Numerische Objekte initialisieren
        numericalMethods = new NumericalMethods();
        
        //Analytische Objekte initialisieren
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

        InputField.setText("diff(diff(diff(x^7,x),x,2), x,2)");

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
//        solutionArea.setBounds(10, 20, 750, 500);
//        InputField.setBounds(10, 530, 650, 25);
//        InputButton.setBounds(660, 530, 100, 25);

//        graphicMethods2D.setBounds(770, 20, 500, 500);
        
        MathCommandCompiler mcc = new MathCommandCompiler();
        Commands c = new Commands();
        
        String s = InputField.getText();

        /**
        Zunächst: es wird geschaut, ob der Befehl ein mathematischer Ausdruck ist.
        Ja -> Ausdruck (und evtl. vereinfachten Ausdruck) ausgeben.
        Nein -> Weiter.
         */
        try{
        
            Expression expr = Expression.build(s, new HashSet());
            //Falls man den Ausdruck noch vereinfachen kann -> vereinfachen und auch ausgeben.
            if (expr.equals(expr.simplify())){
                mathToolArea.append(expr.writeFormula() + "\n");
            //Falls man den Ausdruck nicht vereinfachen kann -> Ausdruck ausgeben.
            } else {
                mathToolArea.append(expr.writeFormula() + " = " + expr.simplify().writeFormula() + "\n");
            }    
            return;
        
        } catch (ExpressionException e){
        }
        
        try{
     
            mathToolArea.append(s + "\n");
            c = mcc.executeCommand(s);
            c.output(mathToolArea, graphicMethods2D, graphicMethods3D);
            
        } catch(Exception e){
//            JOptionPane.showMessageDialog(null, "Fehler! " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
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
                MyMathToolForm.setTitle("MathTool");
                MyMathToolForm.setBounds(20,50,1300,650);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton InputButton;
    private javax.swing.JTextField InputField;
    // End of variables declaration//GEN-END:variables
}
