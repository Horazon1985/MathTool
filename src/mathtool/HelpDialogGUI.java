package mathtool;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;

public class HelpDialogGUI extends JDialog {
 
        public HelpDialogGUI() {
        
        setTitle("Hilfe");
        setLayout(null);
        setResizable(false);
        JLabel menue = new JLabel("Menü");
        menue.setFont(menue.getFont().deriveFont(Font.BOLD));
        JLabel generalities = new JLabel("Allgemeines");
        JLabel mathFormulas = new JLabel("Mathematische Formeln");
        JLabel operators = new JLabel("Operatoren");
        JLabel commands = new JLabel("Befehle");

        menue.setVisible(true);
        generalities.setVisible(true);
        mathFormulas.setVisible(true);
        operators.setVisible(true);
        commands.setVisible(true);

        menue.setBounds(30,120,350,25);
        generalities.setBounds(30,150,350,25);
        LabelMouseListener lml_generalities = new LabelMouseListener();
        generalities.addMouseListener(lml_generalities);
        mathFormulas.setBounds(30,180,350,25);
        mathFormulas.addMouseListener(new LabelMouseListener());
        operators.setBounds(30,210,350,25);
        operators.addMouseListener(new LabelMouseListener());
        commands.setBounds(30,240,350,25);
        commands.addMouseListener(new LabelMouseListener());

        add(menue);
        add(generalities);
        add(mathFormulas);
        add(operators);
        add(commands);

        this.setBounds(400,200,505,310);
        this.getContentPane().setBackground(Color.white);
        
     
        File imageFile = new File("Helplogo.png");
        JPanel panel = new JPanel();
        add(panel);
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageFile);
        }
        catch(java.io.IOException e) {
        }
        panel.add(new JLabel(new ImageIcon(image)));        
        panel.setBounds(0, -5, 500, 100);
        panel.setVisible(true);
        
        validate();
        repaint();
    }

    private void generalitiesActionPerformed(java.awt.event.MouseEvent evt) {                                            
        System.out.println("d");
    }
    
    
}
