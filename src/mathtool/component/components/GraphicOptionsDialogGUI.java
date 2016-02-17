package mathtool.component.components;

import java.util.ArrayList;
import mathtool.component.templates.MathToolOptionComponentTemplate;

public final class GraphicOptionsDialogGUI extends MathToolOptionComponentTemplate {

    public GraphicOptionsDialogGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            int numberOfColumns, String optionGroupName, ArrayList<String> options, ArrayList<String[]> dropDownOptions,
            String saveButtonLabel, String cancelButtonLabel) {
        super(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight, "GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_TITLE",
                "OutputOptionsLogo.png", numberOfColumns, optionGroupName, options, dropDownOptions, saveButtonLabel, cancelButtonLabel);
    }

    @Override
    public void loadOptions() {

    }

    @Override
    public void saveOptions() {

    }

    
}
