package mathtool.component.components;

import graphic.AbstractGraphicPanel2D;
import graphic.AbstractGraphicPanel3D;
import graphic.GraphicPanel3D;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import javax.swing.JComboBox;
import mathtool.lang.translator.Translator;
import mathtool.component.templates.MathToolOptionComponentTemplate;

public final class GraphicOptionsDialogGUI extends MathToolOptionComponentTemplate {

    private static final String PATH_LOGO_GRAPHIC_OPTIONS = "icons/OutputOptionsLogo.png";    
    
    private static final String GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_TITLE = "GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_TITLE";
    private static final String GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT = "GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT";
    private static final String GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK = "GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK";
    private static final String GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH = "GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH";
    private static final String GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY = "GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY";
    private static final String GUI_GraphicOptionsDialogGUI_SHOW_CURSOR_ON_GRAPH = "GUI_GraphicOptionsDialogGUI_SHOW_CURSOR_ON_GRAPH";
    private static final String GUI_GraphicOptionsDialogGUI_DO_NOT_SHOW_CURSOR_ON_GRAPH = "GUI_GraphicOptionsDialogGUI_DO_NOT_SHOW_CURSOR_ON_GRAPH";

    private static GraphicOptionsDialogGUI instance = null;

    private GraphicOptionsDialogGUI(int mathToolGuiX, int mathToolGuiY, int mathToolGuiWidth, int mathToolGuiHeight,
            int numberOfColumns, String optionGroupName, ArrayList<String> options, ArrayList<String[]> dropDownOptions,
            String saveButtonLabel, String cancelButtonLabel) {
        super(mathToolGuiX, mathToolGuiY, mathToolGuiWidth, mathToolGuiHeight, GUI_GraphicOptionsDialogGUI_GRAPHIC_OPTIONS_TITLE,
                PATH_LOGO_GRAPHIC_OPTIONS, numberOfColumns, optionGroupName, options, dropDownOptions, saveButtonLabel, cancelButtonLabel);
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
        JComboBox<String> comboBoxMouseCursor = comboBoxes.get(2);

        for (int i = 0; i < comboBoxBackgroundColor.getModel().getSize(); i++) {
            if (comboBoxBackgroundColor.getModel().getElementAt(i).equals(convertBackgroundColorModeToOptionName(AbstractGraphicPanel3D.getBackgroundColorMode()))) {
                comboBoxBackgroundColor.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < comboBoxPresentationMode.getModel().getSize(); i++) {
            if (comboBoxPresentationMode.getModel().getElementAt(i).equals(convertPresentationModeToOptionName(AbstractGraphicPanel3D.getPresentationMode()))) {
                comboBoxPresentationMode.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < comboBoxMouseCursor.getModel().getSize(); i++) {
            if (comboBoxMouseCursor.getModel().getElementAt(i).equals(convertMouseCursorOptionToOptionName(AbstractGraphicPanel2D.getPointsAreShowable()))) {
                comboBoxMouseCursor.setSelectedIndex(i);
            }
        }

    }

    private void setGraphicOptions() {

        ArrayList<JComboBox<String>> comboBoxes = getOptionDropDowns();
        JComboBox<String> comboBoxBackgroundColor = comboBoxes.get(0);
        JComboBox<String> comboBoxPresentationMode = comboBoxes.get(1);
        JComboBox<String> comboBoxMouseCursor = comboBoxes.get(2);

        // Hintergrundfarbe wählen.
        if (comboBoxBackgroundColor.getItemAt(comboBoxBackgroundColor.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_BRIGHT))) {
            AbstractGraphicPanel3D.setBackgroundColorMode(AbstractGraphicPanel3D.BackgroundColorMode.BRIGHT);
        } else if (comboBoxBackgroundColor.getItemAt(comboBoxBackgroundColor.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_BACKGROUNDCOLOR_OPTION_DARK))) {
            AbstractGraphicPanel3D.setBackgroundColorMode(AbstractGraphicPanel3D.BackgroundColorMode.DARK);
        }

        // Rastermodus wählen.
        if (comboBoxPresentationMode.getItemAt(comboBoxPresentationMode.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_WHOLE_GRAPH))) {
            AbstractGraphicPanel3D.setPresentationMode(AbstractGraphicPanel3D.PresentationMode.WHOLE_GRAPH);
        } else if (comboBoxPresentationMode.getItemAt(comboBoxPresentationMode.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_PRESENTATION_OPTION_GRID_ONLY))) {
            AbstractGraphicPanel3D.setPresentationMode(AbstractGraphicPanel3D.PresentationMode.GRID_ONLY);
        }
        
        // Mousecursoranzeige auf 2D-Graphen wählen.
        if (comboBoxMouseCursor.getItemAt(comboBoxMouseCursor.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_SHOW_CURSOR_ON_GRAPH))) {
            AbstractGraphicPanel2D.setPointsAreShowable(true);
        } else if (comboBoxMouseCursor.getItemAt(comboBoxMouseCursor.getSelectedIndex()).equals(Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_DO_NOT_SHOW_CURSOR_ON_GRAPH))) {
            AbstractGraphicPanel2D.setPointsAreShowable(false);
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

    private String convertMouseCursorOptionToOptionName(boolean showMouseCursor) {
        if (showMouseCursor) {
            return Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_SHOW_CURSOR_ON_GRAPH);
        }
        return Translator.translateOutputMessage(GUI_GraphicOptionsDialogGUI_DO_NOT_SHOW_CURSOR_ON_GRAPH);
    }

}
