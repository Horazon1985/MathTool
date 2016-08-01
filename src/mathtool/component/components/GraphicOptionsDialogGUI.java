package mathtool.component.components;

import graphic.GraphicPanel3D;
import graphic.GraphicPanelCylindrical;
import graphic.GraphicPanelSpherical;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import javax.swing.JComboBox;
import lang.translator.Translator;
import mathtool.MathToolGUI;
import mathtool.component.templates.MathToolOptionComponentTemplate;

public final class GraphicOptionsDialogGUI extends MathToolOptionComponentTemplate {

    private static final String GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_TITLE = "GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_TITLE";
    private static final String GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT = "GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT";
    private static final String GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK = "GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK";
    private static final String GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH = "GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH";
    private static final String GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY = "GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY";
    
    private static GraphicOptionsDialogGUI instance = null;

    private final GraphicPanel3D graphicPanel3D;
    private final GraphicPanelCylindrical graphicPanelCylindrical;
    private final GraphicPanelSpherical graphicPanelSpherical;
    
    private GraphicOptionsDialogGUI(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            int numberOfColumns, String optionGroupName, ArrayList<String> options, ArrayList<String[]> dropDownOptions,
            String saveButtonLabel, String cancelButtonLabel) {
        super(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight, GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_TITLE,
                "icons/OutputOptionsLogo.png", numberOfColumns, optionGroupName, options, dropDownOptions, saveButtonLabel, cancelButtonLabel);
        this.graphicPanel3D = MathToolGUI.getGraphicPanel3D();
        this.graphicPanelCylindrical = MathToolGUI.getGraphicPanelCylindrical();
        this.graphicPanelSpherical = MathToolGUI.getGraphicPanelSpherical();
        loadOptions();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                instance.dispose();
                instance = null;
            }
        });
        
    }
    
    public static GraphicOptionsDialogGUI getInstance(int mathtoolformX, int mathtoolformY, int mathtoolformWidth, int mathtoolformHeight,
            int numberOfColumns, String optionGroupName, ArrayList<String> options, ArrayList<String[]> dropDownOptions,
            String saveButtonLabel, String cancelButtonLabel) {
        if (instance == null) {
            instance = new GraphicOptionsDialogGUI(mathtoolformX, mathtoolformY, mathtoolformWidth, mathtoolformHeight,
            numberOfColumns, optionGroupName, options, dropDownOptions, saveButtonLabel, cancelButtonLabel);
        }
        return instance;
    }
    
    @Override
    public void loadOptions() {
        getGraphicOptions();
    }
    
    @Override
    public void saveOptions() {
        setGraphicOptions();
    }
    
    private void getGraphicOptions() {
        
        ArrayList<JComboBox<String>> comboBoxes = getOptionDropDowns();
        JComboBox<String> comboBoxBackgroundColor = comboBoxes.get(0);
        JComboBox<String> comboBoxPresentationMode = comboBoxes.get(1);
        
        for (int i = 0; i < comboBoxBackgroundColor.getModel().getSize(); i++) {
            if (comboBoxBackgroundColor.getModel().getElementAt(i).equals(convertBackgroundColorModeToOptionName(graphicPanel3D.getBackgroundColorMode()))) {
                comboBoxBackgroundColor.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < comboBoxPresentationMode.getModel().getSize(); i++) {
            if (comboBoxPresentationMode.getModel().getElementAt(i).equals(convertPresentationModeToOptionName(graphicPanel3D.getPresentationMode()))) {
                comboBoxPresentationMode.setSelectedIndex(i);
            }
        }
        
    }
    
    private void setGraphicOptions() {
        
        ArrayList<JComboBox<String>> comboBoxes = getOptionDropDowns();
        JComboBox<String> comboBoxBackgroundColor = comboBoxes.get(0);
        JComboBox<String> comboBoxPresentationMode = comboBoxes.get(1);

        // Hintergrundfarbe wählen.
        if (comboBoxBackgroundColor.getItemAt(comboBoxBackgroundColor.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT))) {
            this.graphicPanel3D.setBackgroundColorMode(GraphicPanel3D.BackgroundColorMode.BRIGHT);
            this.graphicPanelCylindrical.setBackgroundColorMode(GraphicPanelCylindrical.BackgroundColorMode.BRIGHT);
            this.graphicPanelSpherical.setBackgroundColorMode(GraphicPanelSpherical.BackgroundColorMode.BRIGHT);
        } else if (comboBoxBackgroundColor.getItemAt(comboBoxBackgroundColor.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK))) {
            this.graphicPanel3D.setBackgroundColorMode(GraphicPanel3D.BackgroundColorMode.DARK);
            this.graphicPanelCylindrical.setBackgroundColorMode(GraphicPanelCylindrical.BackgroundColorMode.DARK);
            this.graphicPanelSpherical.setBackgroundColorMode(GraphicPanelSpherical.BackgroundColorMode.DARK);
        }

        // Rastermodus wählen.
        if (comboBoxPresentationMode.getItemAt(comboBoxPresentationMode.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH))) {
            this.graphicPanel3D.setPresentationMode(GraphicPanel3D.PresentationMode.WHOLE_GRAPH);
            this.graphicPanelCylindrical.setPresentationMode(GraphicPanelCylindrical.PresentationMode.WHOLE_GRAPH);
            this.graphicPanelSpherical.setPresentationMode(GraphicPanelSpherical.PresentationMode.WHOLE_GRAPH);
        } else if (comboBoxPresentationMode.getItemAt(comboBoxPresentationMode.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY))) {
            this.graphicPanel3D.setPresentationMode(GraphicPanel3D.PresentationMode.GRID_ONLY);
            this.graphicPanelCylindrical.setPresentationMode(GraphicPanelCylindrical.PresentationMode.GRID_ONLY);
            this.graphicPanelSpherical.setPresentationMode(GraphicPanelSpherical.PresentationMode.GRID_ONLY);
        }
        
    }
    
    private String convertBackgroundColorModeToOptionName(GraphicPanel3D.BackgroundColorMode mode) {
        if (mode.equals(GraphicPanel3D.BackgroundColorMode.BRIGHT)) {
            return Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT);
        }
        if (mode.equals(GraphicPanel3D.BackgroundColorMode.DARK)) {
            return Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK);
        }
        return "";
    }
    
    private String convertPresentationModeToOptionName(GraphicPanel3D.PresentationMode mode) {
        if (mode.equals(GraphicPanel3D.PresentationMode.WHOLE_GRAPH)) {
            return Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH);
        }
        if (mode.equals(GraphicPanel3D.PresentationMode.GRID_ONLY)) {
            return Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY);
        }
        return "";
    }

}
