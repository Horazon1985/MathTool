package mathtool.component.components;

import mathtool.component.templates.MathToolInfoComponentTemplate;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Legende. Singletonklasse.
 */
public class LegendGUI extends MathToolInfoComponentTemplate {

    private static final String PATH_LOGO_LEGEND = "icons/LegendLogo.png";    
    
    private static final String GUI_LegendGUI_LEGEND = "GUI_LegendGUI_LEGEND";    
    
    private static LegendGUI instance = null;

    private LegendGUI(int mathToolGuiX, int mathToolGuiY, int mathToolGuiWidth, int mathToolGuiHeight,
            ArrayList<String> instructions, ArrayList<Color> colors, ArrayList<String> exprs) {

        super(mathToolGuiX, mathToolGuiY,
                mathToolGuiWidth, mathToolGuiHeight,
                GUI_LegendGUI_LEGEND, PATH_LOGO_LEGEND,
                instructions, exprs, colors, null, null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                instance.dispose();
                instance = null;
            }
        });

    }

    public static LegendGUI getInstance(int mathtoolGUIX, int mathtoolGUIY, int mathtoolGUIWidth, int mathtoolGUIHeight,
            ArrayList<String> instructions, ArrayList<Color> colors, ArrayList<String> exprs) {
        if (instance == null) {
            instance = new LegendGUI(mathtoolGUIX, mathtoolGUIY, mathtoolGUIWidth, mathtoolGUIHeight, instructions, colors, exprs);
        }
        return instance;
    }

    public static void close() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    @Override
    public void showFile(String fileName) {
    }

}
