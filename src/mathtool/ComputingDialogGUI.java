package mathtool;

import java.awt.Color;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ComputingDialogGUI extends JDialog {

    public ComputingDialogGUI() {

        setTitle("Info");
        setLayout(null);
        setResizable(false);
//        setModal(true);

        this.setBounds(400, 300, 300, 100);
        this.getContentPane().setBackground(Color.white);

        JLabel computingLabel = new JLabel("Berechnung wird ausgeführt...");
        computingLabel.setBounds(20, 25, 200, 25);
        add(computingLabel);
        validate();
        repaint();
    }
    
    
    
}
