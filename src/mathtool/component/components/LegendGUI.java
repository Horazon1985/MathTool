package mathtool.component.components;

import mathtool.component.templates.MathToolInfoComponentTemplate;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;

/**
 * Legende. Singletonklasse.
 */
public class LegendGUI extends MathToolInfoComponentTemplate {

    private static LegendGUI instance = null;

    private LegendGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            ArrayList<String> instructions, ArrayList<Color> colors, ArrayList<String> exprs) {

        super(mathtoolformX, mathtoolformY,
                mathtoolformWidth, mathtoolformHeight,
                "GUI_LegendGUI_LEGEND", "icons/LegendLogo.png",
                instructions, exprs, colors, null, null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                instance.dispose();
                instance = null;
            }
        });

    }

    public static LegendGUI getInstance(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            ArrayList<String> instructions, ArrayList<Color> colors, ArrayList<String> exprs) {
        if (instance == null) {
            instance = new LegendGUI(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight, instructions, colors, exprs);
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
