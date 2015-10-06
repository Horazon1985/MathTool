package mathtool;

import components.MathToolInfoComponentTemplate;
import expressionbuilder.Expression;
import java.io.IOException;
import java.util.ArrayList;

public class HelpDialogGUI extends MathToolInfoComponentTemplate {

    public HelpDialogGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            ArrayList<String> menuCaptions, ArrayList<String> fileNames) {

        super(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight,
                "GUI_HelpDialogGUI_HELP", "icons/HelpLogo.png",
                null, null, null, menuCaptions, fileNames);

    }

    @Override
    public void showFile(String fileName) {
        getInfoEditorPane().setVisible(true);
        getInfoScrollPane().setVisible(true);

        /*
         Der Parameter helpType gibt den (relevanten) Teil des Dateinamens an,
         aus welcher die Hilfe ausgelesen wird.
         */
        java.net.URL helpURL = HelpDialogGUI.class.getResource("help/MathToolHelp" + fileName + Expression.getLanguage().toString() + ".html");

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
