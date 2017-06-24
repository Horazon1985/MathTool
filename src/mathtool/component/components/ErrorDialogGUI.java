package mathtool.component.components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Dialog, welcher angezeigt wird, falls irgendeine Ressource nicht geladen
 * werden kann.
 */
public class ErrorDialogGUI extends JDialog {

    private static final String TITLE = "Configuration error";
    private static final String ERROR_TEXT = "Following resource could not be loaded: ";

    private JLabel[] infoLabels;

    private ErrorDialogGUI() {
        setTitle(TITLE);
        setLayout(null);
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
    }

    public ErrorDialogGUI(String resourcePath) {
        this();
        Dimension dim = getToolkit().getScreenSize();
        setBounds(200, dim.height / 2 - 50, dim.width - 400, 100);
        infoLabels = new JLabel[1];
        infoLabels[0] = new JLabel();
        add(infoLabels[0]);
        infoLabels[0].setBounds(25, 25, getWidth() - 50, 25);
        infoLabels[0].setText(ERROR_TEXT + resourcePath);
        validate();
        repaint();
    }

    public ErrorDialogGUI(Set<String> resourcePaths) {
        this();
        Dimension dim = getToolkit().getScreenSize();
        setBounds(200, dim.height / 2 - 50, dim.width - 400, 100 + 30 * (resourcePaths.size() - 1));
        infoLabels = new JLabel[resourcePaths.size()];
        int i = 0;
        for (String res : resourcePaths) {
            infoLabels[i] = new JLabel();
            add(infoLabels[i]);
            infoLabels[i].setBounds(25, 25 + 30 * i, getWidth() - 50, 25);
            infoLabels[i].setText(ERROR_TEXT + res);
            i++;
        }
        validate();
        repaint();
    }
    
}
