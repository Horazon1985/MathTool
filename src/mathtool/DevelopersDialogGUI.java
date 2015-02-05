package mathtool;

import Translator.Translator;
import expressionbuilder.Expression;
import expressionbuilder.TypeLanguage;
import java.awt.Color;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DevelopersDialogGUI extends JDialog {

    private final JEditorPane developersArea;
    private final JScrollPane scrollPaneDevelopers;

    public DevelopersDialogGUI(int x_mathtoolform, int y_mathtoolform, int with_mathtoolform, int heigth_mathtoolform) {

        setTitle(Translator.translateExceptionMessage("GUI_DevelopersDialogGUI_ABOUT"));
        setLayout(null);
        setResizable(false);
        setModal(true);

        this.setBounds((with_mathtoolform - 505)/2 + x_mathtoolform, (heigth_mathtoolform - 530)/2 + y_mathtoolform, 505, 530);
        this.getContentPane().setBackground(Color.white);

        /**
         * Logo laden
         */
        JPanel panel = new JPanel();
        add(panel);
        panel.add(new JLabel(new ImageIcon(getClass().getResource("icons/MathToolLogo.png"))));
        panel.setBounds(0, -5, 500, 150);
        panel.setVisible(true);

        /**
         * About-Datei laden
         */
        developersArea = new JEditorPane();
        developersArea.setContentType("text/html");
        add(developersArea);
        developersArea.setBounds(20, 170, 460, 270);
        developersArea.setEditable(false);
        scrollPaneDevelopers = new JScrollPane(developersArea);
        scrollPaneDevelopers.setBounds(20, 170, 460, 310);
        scrollPaneDevelopers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneDevelopers);

        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolDevelopers" + Expression.getLanguage().toString() + ".html");
                
        if (helpURL != null) {
            try {
                developersArea.setPage(helpURL);
            } catch (IOException e) {
            }
        } 

        validate();
        repaint();
    }

}
