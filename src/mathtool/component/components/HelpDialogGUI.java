package mathtool.component.components;

import mathtool.component.templates.MathToolInfoComponentTemplate;
import abstractexpressions.expression.classes.Expression;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Hilfemenü. Singletonklasse.
 */
public class HelpDialogGUI extends MathToolInfoComponentTemplate {

    private static final String GUI_HelpDialogGUI_HELP = "GUI_HelpDialogGUI_HELP";    
    
    private static HelpDialogGUI instance = null;

    private HelpDialogGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            ArrayList<String> menuCaptions, ArrayList<String> fileNames) {

        super(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight,
                GUI_HelpDialogGUI_HELP, "icons/HelpLogo.png",
                null, null, null, menuCaptions, fileNames);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
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
        java.net.URL helpURL = HelpDialogGUI.class.getResource("html/MathToolHelp" + fileName + Expression.getLanguage().toString() + ".html");

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
