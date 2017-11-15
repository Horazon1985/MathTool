package mathtool.component.components;

import algorithmexecuter.enums.IdentifierType;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
import javax.swing.text.BadLocationException;
import mathtool.component.controller.MathToolAlgorithmsController;
import mathtool.lang.translator.Translator;

/**
 * Dialog für das Generieren einer Bezeichnerdefinition.
 */
public class SubroutineDefinitionDialogGUI extends JDialog {

    private static final String ICON_PATH = "/mathtool/icons/MathToolIcon.png";

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

    private JComboBox<IdentifierType> routineReturnTypeCombobox;
    
    private JLabel parameterLabel;

    private JLabel typeLabel;

    private JLabel nameLabel;

    private final ArrayList<JLabel> parameterLabels = new ArrayList<>();

    private final ArrayList<JComboBox> comboBoxes = new ArrayList<>();

    private final ArrayList<JTextField> textFields = new ArrayList<>();

    private final ArrayList<JButton> removeButtons = new ArrayList<>();

    private JButton addParameterButton;

    private JButton generateButton;

    int dialogWidth = 500;
    int dialogHeight = 275;

    private static SubroutineDefinitionDialogGUI instance = null;

    private SubroutineDefinitionDialogGUI(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh) {
        // Icon setzen.
        setIconImage(new ImageIcon(getClass().getResource(ICON_PATH)).getImage());
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

        this.routineReturnTypeCombobox = new JComboBox<>(IdentifierType.values());
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
        this.generateButton.addActionListener((ActionEvent e) -> {
            
        });
    }

    private void removeParameter(int i) {
        // TO DO.
        
        relocateButtonsAfterNewParameter();
        updateBounds();
        revalidate();
        repaint();
    }

    private void addNewParameter() {
        this.parameterLabels.add(
                new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_GENERAL_PARAMETER, this.parameterLabels.size() + 1)));
        this.add(this.parameterLabels.get(this.parameterLabels.size() - 1));
        this.parameterLabels.get(this.parameterLabels.size() - 1).setBounds(25, 125 + 50 * this.parameterLabels.size(), 100, 25);

        this.comboBoxes.add(new JComboBox(IdentifierType.values()));
        this.add(this.comboBoxes.get(this.comboBoxes.size() - 1));
        this.comboBoxes.get(this.comboBoxes.size() - 1).setBounds(125, 125 + 50 * this.parameterLabels.size(), 100, 25);
        
        this.textFields.add(new JTextField());
        this.add(this.textFields.get(this.textFields.size() - 1));
        this.textFields.get(this.textFields.size() - 1).setBounds(250, 125 + 50 * this.parameterLabels.size(), 150, 25);

        this.removeButtons.add(new JButton("X"));
        this.add(this.removeButtons.get(this.removeButtons.size() - 1));
        this.removeButtons.get(this.removeButtons.size() - 1).setBounds(415, 125 + 50 * this.parameterLabels.size(), 35, 25);
        this.removeButtons.get(this.removeButtons.size() - 1).addActionListener((ActionEvent e) -> {
            
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
        instance.resetAllFields();
        instance.resetAllChoices();
        instance.setVisible(true);

        instance.revalidate();
        instance.repaint();
        return instance;
    }

    private void resetAllChoices() {
        for (JComboBox<IdentifierType> cb : this.comboBoxes) {
            cb.setSelectedItem(IdentifierType.EXPRESSION);
        }
    }

    private void resetAllFields() {
        for (JTextField tf : this.textFields) {
            tf.setText("");
        }
    }

}
