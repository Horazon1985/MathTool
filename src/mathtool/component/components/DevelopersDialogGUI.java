package mathtool.component.components;

import abstractexpressions.expression.classes.Expression;
import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mathtool.lang.translator.Translator;

public class DevelopersDialogGUI extends JDialog {

    private static final String PATH_LOGO_MATHTOOL = "icons/MathToolLogo.png";    
    private static final String RESOURCE_PREFIX = "html/MathToolDevelopers";    
    private static final String RESOURCE_ENDING = ".html";    

    private static final String GUI_DevelopersDialogGUI_ABOUT = "GUI_DevelopersDialogGUI_ABOUT";    
    
    private final JEditorPane developersArea;
    private final JScrollPane scrollPaneDevelopers;

    private static DevelopersDialogGUI instance = null;
    
    private DevelopersDialogGUI(int mathToolGuiX, int mathToolGuiY, int mathToolGuiWidth, int mathToolGuiHeight) {

        setTitle(Translator.translateOutputMessage(GUI_DevelopersDialogGUI_ABOUT));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);

        this.setBounds((mathToolGuiWidth - 505)/2 + mathToolGuiX, (mathToolGuiHeight - 530)/2 + mathToolGuiY, 505, 530);
        this.getContentPane().setBackground(Color.white);

        // Logo laden
        JPanel panel = new JPanel();
        add(panel);
        panel.add(new JLabel(new ImageIcon(getClass().getResource(PATH_LOGO_MATHTOOL))));
        panel.setBounds(0, -5, 500, 150);
        panel.setVisible(true);

        // About-Datei laden
        developersArea = new JEditorPane();
        developersArea.setContentType("text/html");
        add(developersArea);
        developersArea.setBounds(20, 170, 460, 270);
        developersArea.setEditable(false);
        scrollPaneDevelopers = new JScrollPane(developersArea);
        scrollPaneDevelopers.setBounds(20, 170, 460, 310);
        scrollPaneDevelopers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPaneDevelopers);

        URL helpURL = HelpDialogGUI.class.getResource(RESOURCE_PREFIX + Expression.getLanguage().toString() + RESOURCE_ENDING);
                
        if (helpURL != null) {
            try {
                developersArea.setPage(helpURL);
            } catch (IOException e) {
            }
        } 

        validate();
        repaint();
    }
    
    public static DevelopersDialogGUI getInstance(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiWidth, int mathtoolGuiHeight) {
        if (instance == null) {
            instance = new DevelopersDialogGUI(mathtoolGuiX, mathtoolGuiY, mathtoolGuiWidth, mathtoolGuiHeight);
        }
        return instance;
    }

}
