package mathtool.component.components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Collections;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Dialog, welcher angezeigt wird, falls irgendeine Ressource nicht geladen
 * werden kann.
 */
public class ErrorDialogGUI extends JDialog {

    private static final int RESOURCE_NOT_FOUND = 0;
    private static final int CANNOT_OPEN_RESOURCE = 1;
    private static final int CANNOT_CLOSE_RESOURCE = 2;

    private static final String TITLE = "Error";
    private static final String ERROR_TEXT_RESOURCE_NOT_FOUND = "Following resources could not be found:";
    private static final String ERROR_TEXT_CANNOT_OPEN_RESOURCE = "Following resources could not be loaded:";
    private static final String ERROR_TEXT_CANNOT_CLOSE_RESOURCE = "Following resources could not be closed:";

    private JLabel[] infoLabels;

    private ErrorDialogGUI() {
        setTitle(TITLE);
        setLayout(null);
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
    }

    public ErrorDialogGUI(String resourcePath, int errorCode) {
        this();
        init(Collections.singleton(resourcePath), errorCode);
    }

    public ErrorDialogGUI(Set<String> resourcePaths, int errorCode) {
        this();
        init(resourcePaths, errorCode);
    }

    private void init(Set<String> resourcePaths, int errorCode) {
        Dimension dim = getToolkit().getScreenSize();
        setBounds(200, dim.height / 2 - 50, dim.width - 400, 100 + 30 * resourcePaths.size());
        infoLabels = new JLabel[resourcePaths.size()];
        infoLabels[0] = new JLabel();
        add(infoLabels[0]);
        infoLabels[0].setBounds(25, 25, getWidth() - 50, 25);

        String errorMessage = "";
        switch (errorCode) {
            case RESOURCE_NOT_FOUND:
                errorMessage = ERROR_TEXT_RESOURCE_NOT_FOUND;
                break;
            case CANNOT_OPEN_RESOURCE:
                errorMessage = ERROR_TEXT_CANNOT_OPEN_RESOURCE;
                break;
            case CANNOT_CLOSE_RESOURCE:
                errorMessage = ERROR_TEXT_CANNOT_CLOSE_RESOURCE;
                break;
        }
        infoLabels[0].setText(errorMessage);

        int i = 1;
        for (String res : resourcePaths) {
            infoLabels[i] = new JLabel();
            add(infoLabels[i]);
            infoLabels[i].setBounds(25, 25 + 30 * i, getWidth() - 50, 25);
            infoLabels[i].setText(res);
            i++;
        }
        validate();
        repaint();
    }

    public static ErrorDialogGUI createResourceNotFoundDialog(String resourcePath) {
        return new ErrorDialogGUI(resourcePath, RESOURCE_NOT_FOUND);
    }

    public static ErrorDialogGUI createResourceNotFoundDialog(Set<String> resourcePaths) {
        return new ErrorDialogGUI(resourcePaths, RESOURCE_NOT_FOUND);
    }

    public static ErrorDialogGUI createCannotOpenResourceDialog(String resourcePath) {
        return new ErrorDialogGUI(resourcePath, CANNOT_OPEN_RESOURCE);
    }

    public static ErrorDialogGUI createCannotOpenResourceDialog(Set<String> resourcePaths) {
        return new ErrorDialogGUI(resourcePaths, CANNOT_OPEN_RESOURCE);
    }

    public static ErrorDialogGUI createCannotCloseResourceDialog(String resourcePath) {
        return new ErrorDialogGUI(resourcePath, CANNOT_CLOSE_RESOURCE);
    }

    public static ErrorDialogGUI createCannotCloseResourceDialog(Set<String> resourcePaths) {
        return new ErrorDialogGUI(resourcePaths, CANNOT_CLOSE_RESOURCE);
    }

}
