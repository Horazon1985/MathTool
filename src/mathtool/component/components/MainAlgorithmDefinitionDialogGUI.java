package mathtool.component.components;

import algorithmexecuter.enums.IdentifierType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.component.templates.AlgorithmCommandComponentTemplate;
import mathtool.lang.translator.Translator;

/**
 * Dialog für das Generieren des Main-Algorithmus.
 */
public class MainAlgorithmDefinitionDialogGUI extends AlgorithmCommandComponentTemplate {

    private static final String TITLE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM_RETURN_TYPE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM_RETURN_TYPE";

    private static MainAlgorithmDefinitionDialogGUI instance = null;

    private MainAlgorithmDefinitionDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        super(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh, new String[]{GUI_MathToolAlgorithmsGUI_CODE_GENERATE_MAIN_ALGORITHM_RETURN_TYPE},
                new String[]{}, new Object[][]{IdentifierType.values()}, TITLE);
        init();
    }

    private void init() {
        // Korrektur einer Standarddefinition einer Typ-Combobox: der leere Rückgabetyp muss ebenfalls (als erstes Element) mitaufgenommen werden.
        List<IdentifierType> returnTypes = new ArrayList<>();
        returnTypes.add(null);
        for (IdentifierType type : IdentifierType.values()) {
            returnTypes.add(type);
        }
        this.comboBoxes[0].removeAllItems();
        for (IdentifierType type : returnTypes) {
            this.comboBoxes[0].addItem(type);
        }

        this.generateButton.addActionListener((ActionEvent e) -> {
            String mainAlgorithmCode = MathToolAlgorithmsController.generateMainAlgorithm((IdentifierType) comboBoxes[0].getSelectedItem());

            if (algorithmEditor.getText().replaceAll(" ", "").replaceAll(SIGN_NEXT_LINE, "").replaceAll(SIGN_TAB, "").isEmpty()) {
                algorithmEditor.setText(mainAlgorithmCode);
            } else {
                algorithmEditor.append(SIGN_NEXT_LINE + SIGN_NEXT_LINE + mainAlgorithmCode);
            }
            algorithmEditor.setCaretPosition(algorithmEditor.getText().length() - 2);
            dispose();
        });
    }

    public static MainAlgorithmDefinitionDialogGUI createMainAlgorithmDefinitionDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new MainAlgorithmDefinitionDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.updateTemplateGui();
        instance.updateGui();
        instance.resetAllFields();
        instance.resetAllChoices();
        instance.setVisible(true);

        instance.revalidate();
        instance.repaint();
        return instance;
    }

    private void resetAllChoices() {
        this.comboBoxes[0].setSelectedIndex(0);
    }

    /**
     * Aktualisiert sämtliche Texte gemäß der aktuellen Sprache, welche in der
     * entsprechenden Template-Aktualisierungsmethode noch nicht aktualisiert
     * wurden.
     */
    private void updateGui() {
        setTitle(Translator.translateOutputMessage(TITLE));
    }

}
