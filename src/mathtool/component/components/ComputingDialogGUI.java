package mathtool.component.components;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import javax.swing.SwingWorker;
import mathtool.lang.translator.Translator;

public class ComputingDialogGUI extends JDialog {

    private static final String PATH_LOGO_OWL = "icons/LogoOwlEyesOpen.png";    
    
    private static final String GUI_ComputingDialogGUI_INFO = "GUI_ComputingDialogGUI_INFO";    
    private static final String GUI_ComputingDialogGUI_COMPUTING = "GUI_ComputingDialogGUI_COMPUTING";    
    
    private final JLabel owlLabel;
    private final SwingWorker swingWorker;

    public ComputingDialogGUI(SwingWorker swingWorker, int mathToolGuiX, int mathToolGuiY, int mathToolGuiWidth, int mathToolGuiHeight) {

        setTitle(Translator.translateOutputMessage(GUI_ComputingDialogGUI_INFO));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);

        this.setBounds((mathToolGuiWidth - 550) / 2 + mathToolGuiX, (mathToolGuiHeight - 100) / 2 + mathToolGuiY, 550, 100);
        this.getContentPane().setBackground(Color.white);

        JLabel computingLabel = new JLabel(Translator.translateOutputMessage(GUI_ComputingDialogGUI_COMPUTING));
        computingLabel.setBounds(70, 25, 450, 25);
        add(computingLabel);

        JPanel owlPanel = new JPanel();
        add(owlPanel);
        ImageIcon icon;
        try {
            icon = new ImageIcon(getClass().getResource(PATH_LOGO_OWL));
        } catch (Exception e) {
            icon = null;
        }

        if (icon != null) {
            owlLabel = new JLabel(icon);
        } else {
            owlLabel = new JLabel();
        }
        owlPanel.add(owlLabel);
        owlPanel.setBounds(10, -5, 50, 70);
        owlPanel.setVisible(true);

        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                computingKeyPressed(evt);
            }
        });

        this.swingWorker = swingWorker;

        validate();
        repaint();
    }

    public void changeIcon(ImageIcon icon) {
        owlLabel.setIcon(icon);
        owlLabel.validate();
        owlLabel.repaint();
    }

    private void computingKeyPressed(KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                swingWorker.cancel(true);
                break;
        }
    }

}
