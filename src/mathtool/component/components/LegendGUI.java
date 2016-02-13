package mathtool.component.components;

import mathtool.component.templates.MathToolInfoComponentTemplate;
import java.awt.Color;
import java.util.ArrayList;

public class LegendGUI extends MathToolInfoComponentTemplate {

    public LegendGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            ArrayList<String> instructions, ArrayList<Color> colors, ArrayList<String> exprs) {
        
        super(mathtoolformX, mathtoolformY,
                mathtoolformWidth, mathtoolformHeight,
                "GUI_LegendGUI_LEGEND", "LegendLogo.png",
                instructions, exprs, colors, null, null);        
                        
    }

    @Override
    public void showFile(String fileName){
    }
    
}
