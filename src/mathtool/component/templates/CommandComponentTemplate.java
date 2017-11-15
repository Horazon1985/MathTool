package mathtool.component.templates;

import java.awt.Color;
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
public class CommandComponentTemplate extends JDialog {

    protected static JTextArea algorithmEditor;

    protected JLabel[] choicelabels;

    protected JLabel[] textFieldlabels;

    protected JComboBox[] comboBoxes;

    protected JTextField[] textFields;

    protected JButton generateButton;

    private static final String GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_BUTTON = "GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_BUTTON";

    protected CommandComponentTemplate(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            String[] choiceLabelStringIds, String[] textFieldlabelStringIds, Object[][] comboboxCoices, String titleId) {
        setTitle(Translator.translateOutputMessage(titleId));
        setLayout(null);
//        setResizable(false);
        setAlwaysOnTop(true);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        init(algorithmGuiX, algorithmGuiY, algorithmGuiWidth, algorithmGuiHeigh, choiceLabelStringIds, textFieldlabelStringIds, comboboxCoices);
    }

    private void init(int algorithmGuiX, int algorithmGuiY, int algorithmGuiWidth, int algorithmGuiHeigh,
            String[] choiceLabelStringIds, String[] textFieldlabelStringIds, Object[][] comboboxCoices) {

        int dialogWidth = 400;
        int dialogHeight = 125 + 50 * (choiceLabelStringIds.length + textFieldlabelStringIds.length);

        setBounds((algorithmGuiWidth - dialogWidth) / 2 + algorithmGuiX,
                (algorithmGuiHeigh - dialogHeight) / 2 + algorithmGuiY,
                dialogWidth, dialogHeight);

        this.choicelabels = new JLabel[choiceLabelStringIds.length];
        this.comboBoxes = new JComboBox[choiceLabelStringIds.length];
        for (int i = 0; i < choiceLabelStringIds.length; i++) {
            this.choicelabels[i] = new JLabel(Translator.translateOutputMessage(choiceLabelStringIds[i]));
            this.add(this.choicelabels[i]);
            this.choicelabels[i].setVisible(true);
            this.choicelabels[i].setBounds(25, 25 + 50 * i, 75, 25);
            this.comboBoxes[i] = new JComboBox<>(comboboxCoices[i]);
            this.add(this.comboBoxes[i]);
            this.comboBoxes[i].setBounds(100, 25 + 50 * i, 250, 25);
        }
        
        this.textFieldlabels = new JLabel[textFieldlabelStringIds.length];
        this.textFields = new JTextField[textFieldlabelStringIds.length];
        for (int i = 0; i < textFieldlabelStringIds.length; i++) {
            this.textFieldlabels[i] = new JLabel(Translator.translateOutputMessage(textFieldlabelStringIds[i]));
            this.add(this.textFieldlabels[i]);
            this.textFieldlabels[i].setVisible(true);
            this.textFieldlabels[i].setBounds(25, 25 + 50*(choiceLabelStringIds.length + i), 75, 25);
            this.textFields[i] = new JTextField();
            this.add(this.textFields[i]);
            this.textFields[i].setBounds(100, 25 + 50*(choiceLabelStringIds.length + i), 250, 25);
        }

        this.generateButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_CODE_GENERATE_COMMAND_BUTTON));
        this.add(this.generateButton);
        this.generateButton.setBounds(25, 25 + 50*(choiceLabelStringIds.length + textFieldlabelStringIds.length), 200, 30);
        this.generateButton.setEnabled(false);
    }

}
