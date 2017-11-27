package mathtool.component.components;

import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.component.templates.AlgorithmCommandComponentTemplate;

/**
 * Dialog für das Generieren einer If-Else-Kontrollstruktur.
 */
public class CommandControlStructureForDialogGUI extends AlgorithmCommandComponentTemplate {

    private static final String TITLE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_IF";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_INITIALIZATION = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_INITIALIZATION";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_END_CONDITION = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_END_CONDITION";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_INCREMENT = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_INCREMENT";

    private static CommandControlStructureForDialogGUI instance = null;

    private CommandControlStructureForDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        super(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh, new String[]{},
                new String[]{GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_INITIALIZATION,
                    GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_END_CONDITION,
                    GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_FOR_INCREMENT},
                new Object[][]{}, TITLE);
        init();
    }

    private void init() {

        this.generateButton.addActionListener((ActionEvent e) -> {
            try {
                algorithmEditor.getDocument().insertString(algorithmEditor.getCaretPosition(),
                        MathToolAlgorithmsController.generateControlStructureFor(textFields[0].getText(), textFields[1].getText(), textFields[2].getText()), null);
                dispose();
            } catch (BadLocationException ex) {
            }
        });
        for (JTextField tf : this.textFields) {
            tf.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    generateButton.setEnabled(isGenerateButtonEnabled());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    generateButton.setEnabled(isGenerateButtonEnabled());
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    generateButton.setEnabled(isGenerateButtonEnabled());
                }
            });
        }

    }

    private boolean isGenerateButtonEnabled() {
        for (JTextField tf : this.textFields) {
            if (tf.getText().replaceAll(" ", "").isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static CommandControlStructureForDialogGUI createCommandControlStructureForDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new CommandControlStructureForDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.updateTemplateGui();
        instance.resetAllFields();
        instance.setVisible(true);

        instance.revalidate();
        instance.repaint();
        return instance;
    }

}
