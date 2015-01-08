package mathtool;

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

    public ComputingDialogGUI(SwingWorker swingWorker, int x_mathtoolform, int y_mathtoolform, int with_mathtoolform, int heigth_mathtoolform) {

        setTitle("Info");
        setLayout(null);
        setResizable(false);

        this.setBounds((with_mathtoolform - 400) / 2 + x_mathtoolform, (heigth_mathtoolform - 100) / 2 + y_mathtoolform, 400, 100);
        this.getContentPane().setBackground(Color.white);

        JLabel computingLabel = new JLabel("Berechnung wird ausgeführt...");
        computingLabel.setBounds(70, 25, 300, 25);
        add(computingLabel);

        JPanel owlPanel = new JPanel();
        add(owlPanel);
        owlLabel = new JLabel(new ImageIcon(getClass().getResource("icons/LogoOwlEyesOpen.png")));
        owlPanel.add(owlLabel);
        owlPanel.setBounds(10, -5, 50, 70);
        owlPanel.setVisible(true);

        this.addKeyListener(new java.awt.event.KeyAdapter() {
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
