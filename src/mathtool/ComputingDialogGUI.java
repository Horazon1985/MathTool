package mathtool;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComputingDialogGUI extends JDialog {

    private ImageIcon computingIcon;
    
    public ComputingDialogGUI() {

        setTitle("Info");
        setLayout(null);
        setResizable(false);

        this.setBounds(400, 300, 400, 100);
        this.getContentPane().setBackground(Color.white);

        JLabel computingLabel = new JLabel("Berechnung wird ausgeführt...");
        computingLabel.setBounds(70, 25, 300, 25);
        add(computingLabel);
        
        JPanel owlPanel = new JPanel();
        add(owlPanel);
        this.computingIcon = new ImageIcon(getClass().getResource("icons/LogoOwlEyesOpen.png"));
        owlPanel.add(new JLabel(computingIcon));
        owlPanel.setBounds(10, -5, 50, 70);
        owlPanel.setVisible(true);
        
        validate();
        repaint();
    }
    
    public void changeIcon(ImageIcon icon){
        this.computingIcon = icon;
        validate();
        repaint();
    }
    
}
