package mathtool.component.components;

import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.component.templates.AlgorithmCommandComponentTemplate;

/**
 * Dialog für das Generieren einer If-Else-Kontrollstruktur.
 */
public class CommandControlStructureWhileDialogGUI extends AlgorithmCommandComponentTemplate {

    private static final String TITLE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_WHILE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_IF_ELSE_DO_WHILE_CONDITION = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_IF_ELSE_DO_WHILE_CONDITION";

    private static CommandControlStructureWhileDialogGUI instance = null;

    private CommandControlStructureWhileDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        super(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh, new String[]{},
                new String[]{GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_CONTROLLSTRUCTURE_IF_ELSE_DO_WHILE_CONDITION},
                new Object[][]{}, TITLE);
        init();
    }

    private void init() {

        this.generateButton.addActionListener((ActionEvent e) -> {
            try {
                algorithmEditor.getDocument().insertString(algorithmEditor.getCaretPosition(),
                        MathToolAlgorithmsController.generateControlStructureWhile(textFields[0].getText()), null);
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

    public static CommandControlStructureWhileDialogGUI createCommandControlStructureWhileDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new CommandControlStructureWhileDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.updateTemplateGui();
        instance.resetAllFields();
        instance.setVisible(true);

        instance.revalidate();
        instance.repaint();
        return instance;
    }

}
