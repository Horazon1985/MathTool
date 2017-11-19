package mathtool.component.components;

import algorithmexecuter.enums.IdentifierType;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.lang.translator.Translator;

/**
 * Dialog für das Generieren einer Bezeichnerdefinition.
 */
public class SubroutineDefinitionDialogGUI extends JDialog {

    private static final String MATHTOOL_ICON_PATH = "/mathtool/icons/MathToolIcon.png";
    private static final String CROSS_ICON_PATH = "/mathtool/icons/CrossIcon.png";

    private static final String SIGN_TAB = "\t";
    private static final String SIGN_NEXT_LINE = "\n";

    private static final String TITLE_ID = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_NAME = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_NAME";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_RETURN_TYPE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_RETURN_TYPE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER_TYPE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER_TYPE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER_NAME = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER_NAME";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_GENERAL_PARAMETER = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_GENERAL_PARAMETER";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_ADD_PARAMETER_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_ADD_PARAMETER_BUTTON";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_BUTTON";

    private static JTextArea algorithmEditor;

    private JLabel routineNameLabel;

    private JTextField routineNameField;

    private JLabel routineReturnTypeLabel;

    private JComboBox routineReturnTypeCombobox;

    private JLabel parameterLabel;

    private JLabel typeLabel;

    private JLabel nameLabel;

    private final List<JLabel> parameterLabels = new ArrayList<>();

    private final List<JComboBox<IdentifierType>> parameterTypeComboBoxes = new ArrayList<>();

    private final List<JTextField> parameterNameFields = new ArrayList<>();

    private final List<JButton> removeButtons = new ArrayList<>();

    private JButton addParameterButton;

    private JButton generateButton;

    int dialogWidth = 500;
    int dialogHeight = 275;

    private static SubroutineDefinitionDialogGUI instance = null;

    private SubroutineDefinitionDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        // Icon setzen.
        setIconImage(new ImageIcon(getClass().getResource(MATHTOOL_ICON_PATH)).getImage());
        setTitle(Translator.translateOutputMessage(TITLE_ID));
        setLayout(null);
//        setResizable(false);
        setAlwaysOnTop(true);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        init(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
    }

    private void init(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        initBounds(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);

        this.routineNameLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_NAME));
        this.add(this.routineNameLabel);
        this.routineNameLabel.setBounds(25, 25, 100, 25);

        this.routineNameField = new JTextField();
        this.add(this.routineNameField);
        this.routineNameField.setBounds(150, 25, 200, 25);

        this.routineReturnTypeLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_RETURN_TYPE));
        this.add(this.routineReturnTypeLabel);
        this.routineReturnTypeLabel.setBounds(25, 75, 100, 25);

        initAndFillReturnTypeValues();
        
        this.add(this.routineReturnTypeCombobox);
        this.routineReturnTypeCombobox.setBounds(150, 75, 200, 25);

        this.parameterLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER));
        this.add(this.parameterLabel);
        this.parameterLabel.setBounds(25, 125, 75, 25);

        this.typeLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER_TYPE));
        this.add(this.typeLabel);
        this.typeLabel.setBounds(125, 125, 75, 25);

        this.nameLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER_NAME));
        this.add(this.nameLabel);
        this.nameLabel.setBounds(250, 125, 75, 25);

        this.addParameterButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_ADD_PARAMETER_BUTTON));
        this.add(this.addParameterButton);
        this.addParameterButton.setBounds(25, 175, 200, 25);
        this.addParameterButton.addActionListener((ActionEvent e) -> {
            addNewParameter();
        });

        this.generateButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_BUTTON));
        this.add(this.generateButton);
        this.generateButton.setBounds(250, 175, 200, 25);
        this.generateButton.setEnabled(false);
        this.generateButton.addActionListener((ActionEvent e) -> {
            IdentifierType[] parameterTypes = new IdentifierType[parameterTypeComboBoxes.size()];
            String[] parameterNames = new String[parameterTypeComboBoxes.size()];
            for (int i = 0; i < parameterTypeComboBoxes.size(); i++) {
                parameterTypes[i] = (IdentifierType) parameterTypeComboBoxes.get(i).getSelectedItem();
                parameterNames[i] = parameterNameFields.get(i).getText();
            }

            String subroutineCode = MathToolAlgorithmsController.generateSubroutine((IdentifierType) routineReturnTypeCombobox.getSelectedItem(), routineNameField.getText(), parameterTypes, parameterNames);

            if (algorithmEditor.getText().replaceAll(" ", "").replaceAll(SIGN_NEXT_LINE, "").replaceAll(SIGN_TAB, "").isEmpty()) {
                algorithmEditor.setText(subroutineCode);
            } else {
                algorithmEditor.append(SIGN_NEXT_LINE + SIGN_NEXT_LINE + subroutineCode);
            }
            algorithmEditor.setCaretPosition(algorithmEditor.getText().length() - 2);

            dispose();
        });

        // Changelistener für das Aktivieren des Generierenbuttons.
        this.routineNameField.getDocument().addDocumentListener(new DocumentListener() {
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
    
    private void initAndFillReturnTypeValues() {
        ArrayList returnTypes = new ArrayList();
        returnTypes.add(null);
        for (IdentifierType type : IdentifierType.values()) {
            returnTypes.add(type);
        }
        this.routineReturnTypeCombobox = new JComboBox(returnTypes.toArray());
    }

    private boolean isGenerateButtonEnabled() {
        if (this.routineNameField.getText().replaceAll(" ", "").isEmpty()) {
            return false;
        }
        for (JTextField tf : this.parameterNameFields) {
            if (tf.getText().replaceAll(" ", "").isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void removeParameter(int i) {

        remove(this.parameterLabels.get(i));
        remove(this.parameterTypeComboBoxes.get(i));
        remove(this.parameterNameFields.get(i));
        remove(this.removeButtons.get(i));

        this.parameterLabels.remove(i);
        this.parameterTypeComboBoxes.remove(i);
        this.parameterNameFields.remove(i);
        this.removeButtons.remove(i);

        for (int j = i; j < this.removeButtons.size(); j++) {
            this.parameterLabels.get(j).setBounds(this.parameterLabels.get(j).getX(), this.parameterLabels.get(j).getY() - 50,
                    this.parameterLabels.get(j).getWidth(), this.parameterLabels.get(j).getHeight());
            this.parameterTypeComboBoxes.get(j).setBounds(this.parameterTypeComboBoxes.get(j).getX(), this.parameterTypeComboBoxes.get(j).getY() - 50,
                    this.parameterTypeComboBoxes.get(j).getWidth(), this.parameterTypeComboBoxes.get(j).getHeight());
            this.parameterNameFields.get(j).setBounds(this.parameterNameFields.get(j).getX(), this.parameterNameFields.get(j).getY() - 50,
                    this.parameterNameFields.get(j).getWidth(), this.parameterNameFields.get(j).getHeight());
            this.removeButtons.get(j).setBounds(this.removeButtons.get(j).getX(), this.removeButtons.get(j).getY() - 50,
                    this.removeButtons.get(j).getWidth(), this.removeButtons.get(j).getHeight());
        }

        relocateButtonsAfterNewParameter();
        updateBounds();
        this.generateButton.setEnabled(isGenerateButtonEnabled());
        revalidate();
        repaint();
    }

    private void addNewParameter() {
        this.parameterLabels.add(
                new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_GENERAL_PARAMETER, this.parameterLabels.size() + 1)));
        this.add(this.parameterLabels.get(this.parameterLabels.size() - 1));
        this.parameterLabels.get(this.parameterLabels.size() - 1).setBounds(25, 125 + 50 * this.parameterLabels.size(), 100, 25);

        this.parameterTypeComboBoxes.add(new JComboBox(IdentifierType.values()));
        this.add(this.parameterTypeComboBoxes.get(this.parameterTypeComboBoxes.size() - 1));
        this.parameterTypeComboBoxes.get(this.parameterTypeComboBoxes.size() - 1).setBounds(125, 125 + 50 * this.parameterLabels.size(), 100, 25);

        this.parameterNameFields.add(new JTextField());
        this.add(this.parameterNameFields.get(this.parameterNameFields.size() - 1));
        this.parameterNameFields.get(this.parameterNameFields.size() - 1).setBounds(250, 125 + 50 * this.parameterLabels.size(), 150, 25);

        ImageIcon crossIcon = null;
        try {
            crossIcon = new ImageIcon(ImageIO.read(getClass().getResource(CROSS_ICON_PATH)));
        } catch (IOException e) {
        }

        this.removeButtons.add(new JButton(crossIcon));
        this.add(this.removeButtons.get(this.removeButtons.size() - 1));
        this.removeButtons.get(this.removeButtons.size() - 1).setBounds(420, 125 + 50 * this.parameterLabels.size(), 30, 25);
        this.removeButtons.get(this.removeButtons.size() - 1).addActionListener((ActionEvent e) -> {
            removeParameter(removeButtons.indexOf(e.getSource()));
        });

        // Changelistener für das Aktivieren des Generierenbuttons.
        this.generateButton.setEnabled(false);
        this.parameterNameFields.get(this.parameterNameFields.size() - 1).getDocument().addDocumentListener(new DocumentListener() {
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

        relocateButtonsAfterNewParameter();
        updateBounds();
        revalidate();
        repaint();
    }

    private void initBounds(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        setBounds((algorithmGuiWidth - this.dialogWidth) / 2 + algorithmGuiX,
                (algorithmGuiHeigh - this.dialogHeight) / 2 + algorithmGuiY,
                this.dialogWidth, this.dialogHeight);
    }

    private void updateBounds() {
        this.dialogHeight = 275 + 50 * this.parameterLabels.size();
        setBounds(getX(), getY(), getWidth(), this.dialogHeight);
    }

    private void relocateButtonsAfterNewParameter() {
        this.addParameterButton.setBounds(this.addParameterButton.getX(), 175 + 50 * this.parameterLabels.size(),
                this.addParameterButton.getWidth(), this.addParameterButton.getHeight());
        this.generateButton.setBounds(this.generateButton.getX(), 175 + 50 * this.parameterLabels.size(),
                this.generateButton.getWidth(), this.generateButton.getHeight());
    }

    public static SubroutineDefinitionDialogGUI createSubroutineDefinitionDialog(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            JTextArea algEditor) {
        algorithmEditor = algEditor;
        if (instance == null) {
            instance = new SubroutineDefinitionDialogGUI(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh);
        }
        instance.reset();
        instance.setVisible(true);

        instance.revalidate();
        instance.repaint();
        return instance;
    }

    private void reset() {
        
        for (JLabel l : this.parameterLabels) {
            remove(l);
        }
        for (JComboBox cb : this.parameterTypeComboBoxes) {
            remove(cb);
        }
        for (JTextField tf : this.parameterNameFields) {
            remove(tf);
        }
        for (JButton b : this.removeButtons) {
            remove(b);
        }
        
        this.routineNameField.setText("");
        this.routineReturnTypeCombobox.setSelectedIndex(0);
        this.parameterLabels.clear();
        this.parameterTypeComboBoxes.clear();
        this.parameterNameFields.clear();
        this.removeButtons.clear();
    
        relocateButtonsAfterNewParameter();
        updateBounds();
    }
    
}
