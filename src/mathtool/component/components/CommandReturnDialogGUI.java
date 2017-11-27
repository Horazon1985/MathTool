package mathtool.component.components;

import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.component.templates.AlgorithmCommandComponentTemplate;

/**
 * Dialog für das Generieren eines Rückgabebefehls.
 */
public class CommandReturnDialogGUI extends AlgorithmCommandComponentTemplate {

    private static final String TITLE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_RETURN";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME";

    private static CommandReturnDialogGUI instance = null;

    private CommandReturnDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        super(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh, new String[]{},
                new String[]{GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME}, new Object[][]{}, TITLE);
        init();
    }

    private void init() {
        this.generateButton.setEnabled(false);
        this.generateButton.addActionListener((ActionEvent e) -> {
            try {
                algorithmEditor.getDocument().insertString(algorithmEditor.getCaretPosition(),
                        MathToolAlgorithmsController.generateCommandReturn(textFields[0].getText()), null);
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

    public static CommandReturnDialogGUI createCommandReturnDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new CommandReturnDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.updateTemplateGui();
        instance.resetAllFields();
        instance.setVisible(true);
        instance.revalidate();
        instance.repaint();
        return instance;
    }

}
