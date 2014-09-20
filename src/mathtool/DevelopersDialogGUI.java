package mathtool;

import java.awt.Color;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DevelopersDialogGUI extends JDialog {

    private JEditorPane developersArea;
    private JScrollPane scrollPaneDevelopers;

    public DevelopersDialogGUI() {

        setTitle("About MathTool");
        setLayout(null);
        setResizable(false);
        setModal(true);

        this.setBounds(400, 200, 505, 420);
        this.getContentPane().setBackground(Color.white);

        /**
         * Logo laden
         */
        JPanel panel = new JPanel();
        add(panel);
        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/MathToolLogo.png"))));
        panel.setBounds(0, -5, 500, 100);
        panel.setVisible(true);

        /**
         * About-Datei laden
         */
        developersArea = new JEditorPane();
        developersArea.setContentType("text/html");
        add(developersArea);
        developersArea.setBounds(20, 120, 460, 240);
        developersArea.setEditable(false);
        scrollPaneDevelopers = new JScrollPane(developersArea);
        scrollPaneDevelopers.setBounds(20, 120, 460, 240);
        scrollPaneDevelopers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneDevelopers);

        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolDevelopers.html");
        if (helpURL != null) {
            try {
                developersArea.setPage(helpURL);
            } catch (IOException e) {
                System.err.println("Fehler: " + helpURL);
            }
        } else {
            System.err.println("Datei nicht gefunden.");
        }

        validate();
        repaint();
    }

}
