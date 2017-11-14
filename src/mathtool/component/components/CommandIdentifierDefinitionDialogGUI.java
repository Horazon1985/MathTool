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
public class CommandIdentifierDefinitionDialogGUI extends JDialog {

    private static final String TITLE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_TYPE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_TYPE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_VALUE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_VALUE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_BUTTON";

    private static JTextArea algorithmEditor;

    private JLabel typeLabel;
    private JLabel nameLabel;
    private JLabel valueLabel;

    private JComboBox<IdentifierType> typeBox;
    private JTextField nameField;
    private JTextField valueField;

    private JButton generateButton;

    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 275;

    private static CommandIdentifierDefinitionDialogGUI instance = null;

    private CommandIdentifierDefinitionDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
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

        this.typeLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_TYPE));
        this.add(this.typeLabel);
        this.typeLabel.setVisible(true);
        this.typeLabel.setBounds(25, 25, 50, 25);
        this.nameLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_NAME));
        this.add(this.nameLabel);
        this.nameLabel.setBounds(25, 75, 50, 25);
        this.valueLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_VALUE));
        this.add(this.valueLabel);
        this.valueLabel.setBounds(25, 125, 50, 25);

        this.typeBox = new JComboBox<>(IdentifierType.values());
        this.add(this.typeBox);
        this.typeBox.setBounds(100, 25, 250, 25);

        this.nameField = new JTextField();
        this.add(this.nameField);
        this.nameField.setBounds(100, 75, 250, 25);

        this.valueField = new JTextField();
        this.add(this.valueField);
        this.valueField.setVisible(true);
        this.valueField.setBounds(100, 125, 250, 25);

        this.generateButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_DEFINE_BUTTON));
        this.add(this.generateButton);
        this.generateButton.setBounds(25, 175, 200, 30);
        this.generateButton.setEnabled(false);
        this.generateButton.addActionListener((ActionEvent e) -> {
            try {
                algorithmEditor.getDocument().insertString(algorithmEditor.getCaretPosition(),
                        MathToolAlgorithmsController.generateCommandDefine((IdentifierType) typeBox.getSelectedItem(), nameField.getText(), valueField.getText()), null);
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

    public static CommandIdentifierDefinitionDialogGUI createCommandIdentifierDefinitionDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new CommandIdentifierDefinitionDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.resetAllFields();
        instance.setVisible(true);
        instance.revalidate();
        instance.repaint();
        return instance;
    }

    private void resetAllFields() {
        this.typeBox.setSelectedItem(IdentifierType.EXPRESSION);
        this.nameField.setText("");
        this.valueField.setText("");
    }
    
}
