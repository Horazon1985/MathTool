package mathtool.component.components;

import mathtool.component.templates.MathToolInfoComponentTemplate;
import abstractexpressions.expression.classes.Expression;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Hilfemenü. Singletonklasse.
 */
public class HelpDialogGUI extends MathToolInfoComponentTemplate {

    private static final String PATH_LOGO_HELP = "icons/HelpLogo.png";    
    private static final String RESOURCE_PREFIX = "html/MathToolHelp";    
    private static final String RESOURCE_ENDING = ".html";    
    
    private static final String GUI_HelpDialogGUI_HELP = "GUI_HelpDialogGUI_HELP";    
    
    private static HelpDialogGUI instance = null;

    private HelpDialogGUI(int mathToolGuiX, int mathToolGuiY, int mathToolGuiWidth, int mathToolGuiHeight,
            ArrayList<String> menuCaptions, ArrayList<String> fileNames) {

        super(mathToolGuiX, mathToolGuiY, mathToolGuiWidth, mathToolGuiHeight,
                GUI_HelpDialogGUI_HELP, PATH_LOGO_HELP,
                null, null, null, menuCaptions, fileNames);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                instance.dispose();
                instance = null;
            }
        });

    }

    public static HelpDialogGUI getInstance(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            ArrayList<String> menuCaptions, ArrayList<String> fileNames) {
        if (instance == null) {
            instance = new HelpDialogGUI(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight, menuCaptions, fileNames);
        }
        return instance;
    }

    @Override
    public void showFile(String fileName) {
        getInfoEditorPane().setVisible(true);
        getInfoScrollPane().setVisible(true);

        /*
         Der Parameter helpType gibt den (relevanten) Teil des Dateinamens an,
         aus welcher die Hilfe ausgelesen wird.
         */
        URL helpURL = HelpDialogGUI.class.getResource(RESOURCE_PREFIX + fileName + Expression.getLanguage().toString() + RESOURCE_ENDING);

        if (helpURL != null) {
            try {
                getInfoEditorPane().setPage(helpURL);
            } catch (IOException e) {
            }
        }
        repaint();
        validate();
    }

}
