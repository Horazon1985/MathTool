package mathtool.component.components;

import algorithmexecuter.enums.IdentifierType;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.component.templates.AlgorithmCommandComponentTemplate;

/**
 * Dialog für das Generieren einer Bezeichnerdefinition.
 */
public class CommandIdentifierDefinitionDialogGUI extends AlgorithmCommandComponentTemplate {

    private static final String TITLE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_TYPE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_TYPE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_VALUE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_VALUE";
    
    private static CommandIdentifierDefinitionDialogGUI instance = null;

    private CommandIdentifierDefinitionDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        super(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh, new String[]{GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_TYPE},
                new String[]{GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME, GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_VALUE}, 
                new Object[][]{IdentifierType.values()}, TITLE);
        init();
    }

    private void init() {

        this.generateButton.addActionListener((ActionEvent e) -> {
            try {
                algorithmEditor.getDocument().insertString(algorithmEditor.getCaretPosition(),
                        MathToolAlgorithmsController.generateCommandDefine((IdentifierType) comboBoxes[0].getSelectedItem(), 
                                textFields[0].getText(), textFields[1].getText()), null);
                dispose();
            } catch (BadLocationException ex) {
            }
        });
        this.textFields[0].getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                generateButton.setEnabled(!textFields[0].getText().replaceAll(" ", "").isEmpty());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                generateButton.setEnabled(!textFields[0].getText().replaceAll(" ", "").isEmpty());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                generateButton.setEnabled(!textFields[0].getText().replaceAll(" ", "").isEmpty());
            }
        });

    }

    public static CommandIdentifierDefinitionDialogGUI createCommandIdentifierDefinitionDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new CommandIdentifierDefinitionDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.updateTemplateGui();
        instance.resetAllFields();
        instance.resetAllChoices();
        instance.setVisible(true);
        
        instance.revalidate();
        instance.repaint();
        return instance;
    }

    private void resetAllChoices() {
        this.comboBoxes[0].setSelectedItem(IdentifierType.EXPRESSION);
    }
    
}
