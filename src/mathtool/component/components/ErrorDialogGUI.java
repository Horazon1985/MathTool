package mathtool.component.components;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Dialog, welcher angezeigt wird, falls irgendeine Ressource nicht geladen
 * werden kann.
 */
public class ErrorDialogGUI extends JDialog {

    private static final String TITLE = "Configuration error";
    private static final String ERROR_TEXT = "Following resource could not be loaded: ";
    
    private JLabel infoLabel;

    private ErrorDialogGUI() {
        setTitle(TITLE);
        setLayout(null);
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        infoLabel = new JLabel();
        add(infoLabel);
    }
    
    public ErrorDialogGUI(String resourcePath) {
        this();
        Dimension dim = getToolkit().getScreenSize();
        setBounds(200, dim.height / 2 - 50, dim.width - 400, 100);
        infoLabel.setBounds(25, 25, getWidth() - 50, 25);
        infoLabel.setText(ERROR_TEXT + resourcePath);
        validate();
        repaint();
    }

    public ErrorDialogGUI(int mathToolGuiX, int mathToolGuiY, int mathToolGuiWidth, int mathToolGuiHeight, String resourcePath) {
        this();
        setBounds((mathToolGuiWidth - 500) / 2 + mathToolGuiX, (mathToolGuiHeight - 100) / 2 + mathToolGuiY, 500, 100);
        infoLabel.setBounds(25, 25, 1050, 25);
        infoLabel.setText(ERROR_TEXT + resourcePath);
        validate();
        repaint();
    }

}
