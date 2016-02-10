package mathtool.component.components;

import lang.translator.Translator;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import javax.swing.SwingWorker;

public class ComputingDialogGUI extends JDialog {

    private final JLabel owlLabel;
    private final SwingWorker swingWorker;

    public ComputingDialogGUI(SwingWorker swingWorker, int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight) {

        setTitle(Translator.translateExceptionMessage("GUI_ComputingDialogGUI_INFO"));
        setLayout(null);
        setResizable(false);

        this.setBounds((mathtoolformWidth - 550) / 2 + mathtoolformX, (mathtoolformHeight - 100) / 2 + mathtoolformY, 550, 100);
        this.getContentPane().setBackground(Color.white);

        JLabel computingLabel = new JLabel(Translator.translateExceptionMessage("GUI_ComputingDialogGUI_COMPUTING"));
        computingLabel.setBounds(70, 25, 450, 25);
        add(computingLabel);

        JPanel owlPanel = new JPanel();
        add(owlPanel);
        ImageIcon icon;
        try {
            icon = new ImageIcon(getClass().getResource("LogoOwlEyesOpen.png"));
        } catch (Exception e) {
            icon = null;
        }
        
        if (icon != null){
            owlLabel = new JLabel(icon);
        } else {
            owlLabel = new JLabel();
        }
        owlPanel.add(owlLabel);
        owlPanel.setBounds(10, -5, 50, 70);
        owlPanel.setVisible(true);

        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
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

    private void computingKeyPressed(java.awt.event.KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                swingWorker.cancel(true);
                break;
        }
    }

}
