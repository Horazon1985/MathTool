package mathtool;

import components.MathToolOptionComponentTemplate;
import java.util.ArrayList;

public class OutputOptionsDialogGUI extends MathToolOptionComponentTemplate {

    public OutputOptionsDialogGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight, 
            int numberOfColumns, String optionGroupName, ArrayList<String> options, ArrayList<String[]> dropDownOptions, 
            String saveButtonLabel, String cancelButtonLabel) {
        super(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight, "GUI_OutputOptionsDialogGUI_OUTPUT_OPTIONS_TITLE", 
                "icons/OutputOptionsLogo.png", numberOfColumns, optionGroupName, options, dropDownOptions, saveButtonLabel, cancelButtonLabel);
    }

    @Override
    public void loadOptions() {

    }

    @Override
    public void saveOptions() {

    }
    
}
