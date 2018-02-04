package mathtool.component.templates;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import mathtool.lang.translator.Translator;

/**
 * Dialog-Template für diverse Befehlgenerierungen.
 */
public class AlgorithmCommandComponentTemplate extends JDialog {

    private static final String ICON_PATH = "/mathtool/icons/MathToolIcon.png";

    protected static final String SIGN_TAB = "\t";
    protected static final String SIGN_NEXT_LINE = "\n";

    protected static JTextArea algorithmEditor;

    private final String[] choiceLabelStringIds;

    private final String[] textFieldlabelStringIds;

    protected JLabel[] choicelabels;

    protected JLabel[] textFieldlabels;

    protected JComboBox[] comboBoxes;

    protected JTextField[] textFields;

    protected JButton generateButton;

    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_BUTTON";

    private final String TITLE;
    
    protected AlgorithmCommandComponentTemplate(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            String[] choiceLabelStringIds, String[] textFieldlabelStringIds, Object[][] comboboxCoices, String titleId) {
        TITLE = titleId;
        // Icon setzen.
        setIconImage(new ImageIcon(getClass().getResource(ICON_PATH)).getImage());
        setTitle(Translator.translateOutputMessage(TITLE));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        this.choiceLabelStringIds = choiceLabelStringIds;
        this.textFieldlabelStringIds = textFieldlabelStringIds;
        init(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh, comboboxCoices);
    }

    private void init(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            Object[][] comboboxCoices) {

        int dialogWidth = 450;
        int dialogHeight = 125 + 50 * (choiceLabelStringIds.length + textFieldlabelStringIds.length);

        setBounds((algorithmGuiWidth - dialogWidth) / 2 + algorithmGuiX,
                (algorithmGuiHeigh - dialogHeight) / 2 + algorithmGuiY,
                dialogWidth, dialogHeight);

        this.choicelabels = new JLabel[choiceLabelStringIds.length];
        this.comboBoxes = new JComboBox[choiceLabelStringIds.length];
        for (int i = 0; i < choiceLabelStringIds.length; i++) {
            this.choicelabels[i] = new JLabel(Translator.translateOutputMessage(choiceLabelStringIds[i]));
            this.add(this.choicelabels[i]);
            this.choicelabels[i].setBounds(25, 25 + 50 * i, 125, 25);
            this.comboBoxes[i] = new JComboBox<>(comboboxCoices[i]);
            this.add(this.comboBoxes[i]);
            this.comboBoxes[i].setBounds(150, 25 + 50 * i, 250, 25);
        }

        this.textFieldlabels = new JLabel[textFieldlabelStringIds.length];
        this.textFields = new JTextField[textFieldlabelStringIds.length];
        for (int i = 0; i < textFieldlabelStringIds.length; i++) {
            this.textFieldlabels[i] = new JLabel(Translator.translateOutputMessage(textFieldlabelStringIds[i]));
            this.add(this.textFieldlabels[i]);
            this.textFieldlabels[i].setBounds(25, 25 + 50 * (choiceLabelStringIds.length + i), 125, 25);
            this.textFields[i] = new JTextField();
            this.add(this.textFields[i]);
            this.textFields[i].setBounds(150, 25 + 50 * (choiceLabelStringIds.length + i), 250, 25);
        }

        this.generateButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_BUTTON));
        this.add(this.generateButton);
        this.generateButton.setBounds(25, 25 + 50 * (choiceLabelStringIds.length + textFieldlabelStringIds.length), 200, 30);
        this.generateButton.setEnabled(textFields.length == 0);
    }

    protected void resetAllFields() {
        for (JTextField tf : this.textFields) {
            tf.setText("");
        }
    }

    protected void updateTemplateGui() {
        setTitle(Translator.translateOutputMessage(TITLE));
        this.generateButton.setText(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_BUTTON));
        for (int i = 0; i < choiceLabelStringIds.length; i++) {
            this.choicelabels[i].setText(Translator.translateOutputMessage(choiceLabelStringIds[i]));
        }
        for (int i = 0; i < textFieldlabelStringIds.length; i++) {
            this.textFieldlabels[i].setText(Translator.translateOutputMessage(textFieldlabelStringIds[i]));
        }
    }

}
