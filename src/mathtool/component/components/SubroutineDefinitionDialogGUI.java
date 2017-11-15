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
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_TYPE = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_TYPE";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_NAME = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_NAME";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_GENERAL_PARAMETER = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_GENERAL_PARAMETER";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_ADD_PARAMETER_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_ADD_PARAMETER_BUTTON";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_REMOVE_PARAMETER_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_REMOVE_PARAMETER_BUTTON";
    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_BUTTON";

    private static JTextArea algorithmEditor;

    private JLabel routineNameLabel;

    private JLabel routineReturnTypeLabel;
    
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
    int dialogHeight = 200;

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

        this.parameterLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_PARAMETER));
        this.add(this.parameterLabel);
        this.parameterLabel.setBounds(25, 25, 75, 25);

        this.typeLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_TYPE));
        this.add(this.typeLabel);
        this.typeLabel.setBounds(125, 25, 75, 25);

        this.nameLabel = new JLabel(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_NAME));
        this.add(this.nameLabel);
        this.nameLabel.setBounds(225, 25, 75, 25);

        this.addParameterButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_ADD_PARAMETER_BUTTON));
        this.add(this.addParameterButton);
        this.addParameterButton.setBounds(25, 75, 200, 25);
        this.addParameterButton.addActionListener((ActionEvent e) -> {
            addNewParameter();
            
        });

        this.generateButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_SUBROUTINE_BUTTON));
        this.add(this.generateButton);
        this.generateButton.setBounds(250, 75, 200, 25);
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
        this.parameterLabels.get(this.parameterLabels.size() - 1).setBounds(25, 25 + 50 * this.parameterLabels.size(), 50, 25);

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
        this.dialogHeight = 100 + 50 * this.parameterLabels.size();
        setBounds(getX(), getY(), getWidth(), this.dialogHeight);
    }

    private void relocateButtonsAfterNewParameter() {
        this.addParameterButton.setBounds(this.addParameterButton.getX(), 75 + 50 * this.parameterLabels.size(), 
                this.addParameterButton.getWidth(), this.addParameterButton.getHeight());
        this.generateButton.setBounds(this.generateButton.getX(), 75 + 50 * this.parameterLabels.size(), 
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
