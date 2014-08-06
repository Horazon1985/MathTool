package mathtool;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DevelopersDialogGUI extends JDialog {

    public DevelopersDialogGUI() {
        
        setTitle("About MathTool");
        setLayout(null);
        setResizable(false);
        JLabel nameLabel = new JLabel("Programm: MathTool.");
        JLabel systemLabel = new JLabel("System: Jedes System, welches mindestens Java 7 unterstützt.");
        JLabel developerLabel = new JLabel("Entwickler: Sergei Kovalenko.");
        JLabel thanksLabel = new JLabel("Besonderer Dank gilt Dimitri Krilov und Sven Willner.");
        JLabel copyrightLabel = new JLabel("Copyright 2014 by Sergei Kovalenko.");

        nameLabel.setVisible(true);
        systemLabel.setVisible(true);
        developerLabel.setVisible(true);
        thanksLabel.setVisible(true);
        copyrightLabel.setVisible(true);

        nameLabel.setBounds(10,100,350,25);
        systemLabel.setBounds(10,130,350,25);
        developerLabel.setBounds(10,160,350,25);
        thanksLabel.setBounds(10,200,350,25);
        copyrightLabel.setBounds(10,250,350,25);

        add(nameLabel);
        add(systemLabel);
        add(developerLabel);
        add(thanksLabel);
        add(copyrightLabel);

        this.setBounds(400,200,410,310);
        this.getContentPane().setBackground(Color.white);
        
     
        File imageFile = new File("MathToollogo.png");
        JPanel panel = new JPanel();
        add(panel);
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageFile);
        }
        catch(java.io.IOException e) {
        }
        panel.add(new JLabel(new ImageIcon(image)));        
        panel.setBounds(0, -5, 400, 100);
        panel.setVisible(true);
        
        validate();
        repaint();
    }
    
    
}
