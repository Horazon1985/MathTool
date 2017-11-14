package mathtool.component.components;

import algorithmexecuter.enums.IdentifierType;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.lang.translator.Translator;

/**
 * Dialog für Bezeichnerdefinition.
 */
public class CommandReturnDialogGUI extends JDialog {

    private static final String TITLE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_RETURN";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_BUTTON";

    private static JTextArea algorithmEditor;

    private JLabel nameLabel;

    private JTextField nameField;

    private JButton generateButton;

    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 175;

    private static CommandReturnDialogGUI instance = null;

    private CommandReturnDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        setTitle(Translator.translateOutputMessage(TITLE));
        setLayout(null);
//        setResizable(false);
        setAlwaysOnTop(true);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        init(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
    }

    private void init(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        setBounds((algorithmGuiWidth - DIALOG_WIDTH) / 2 + algorithmGuiX,
                (algorithmGuiHeigh - DIALOG_HEIGHT) / 2 + algorithmGuiY,
                DIALOG_WIDTH, DIALOG_HEIGHT);

        this.nameLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME));
        this.add(this.nameLabel);
        this.nameLabel.setBounds(25, 25, 50, 25);

        this.nameField = new JTextField();
        this.add(this.nameField);
        this.nameField.setBounds(100, 25, 250, 25);

        this.generateButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_BUTTON));
        this.add(this.generateButton);
        this.generateButton.setBounds(25, 75, 200, 30);
        this.generateButton.setEnabled(false);
        this.generateButton.addActionListener((ActionEvent e) -> {
            try {
                algorithmEditor.getDocument().insertString(algorithmEditor.getCaretPosition(),
                        MathToolAlgorithmsController.generateCommandReturn(nameField.getText()), null);
                dispose();
            } catch (BadLocationException ex) {
            }
        });
        this.nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                generateButton.setEnabled(!nameField.getText().replaceAll(" ", "").isEmpty());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                generateButton.setEnabled(!nameField.getText().replaceAll(" ", "").isEmpty());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                generateButton.setEnabled(!nameField.getText().replaceAll(" ", "").isEmpty());
            }
        });

    }

    public static CommandReturnDialogGUI createCommandReturnDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new CommandReturnDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.resetAllFields();
        instance.setVisible(true);
        instance.revalidate();
        instance.repaint();
        return instance;
    }

    private void resetAllFields() {
        this.nameField.setText("");
    }

}
