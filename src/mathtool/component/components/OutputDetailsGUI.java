package mathtool.component.components;

import abstractexpressions.interfaces.AbstractExpression;
import mathtool.lang.translator.Translator;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Ausgabedetails. Singletonklasse.
 */
public class OutputDetailsGUI extends JDialog {

    private static final String PATH_LOGO_OUTPUT_DETAILS = "icons/DetailsLogo.png";    
    
    private static final String GUI_OutputDetailsGUI_INFO = "GUI_OutputDetailsGUI_INFO";    
    private static final String GUI_OutputDetailsGUI_GENERAL_AVAILABLE_EXPRESSION = "GUI_OutputDetailsGUI_GENERAL_AVAILABLE_EXPRESSION";    
    
    private static OutputDetailsGUI instance = null;

    private JLabel[] formulaLabels;
    private JTextField[] formulas;

    public OutputDetailsGUI(int mathToolGuiX, int mathToolGuiY, int mathToolGuiWidth, int mathToolGuiHeight, int width, AbstractExpression[] abstrExprs, String[] texts) {

        setTitle(Translator.translateOutputMessage(GUI_OutputDetailsGUI_INFO));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);

        int height = 120 + 35 * (abstrExprs.length + texts.length);
        this.setBounds((mathToolGuiWidth - width) / 2 + mathToolGuiX,
                (mathToolGuiHeight - height) / 2 + mathToolGuiY, width, height);
        this.getContentPane().setBackground(Color.WHITE);

        // Logo laden
        JPanel panel = new JPanel();
        add(panel);
        panel.add(new JLabel(new ImageIcon(getClass().getResource(PATH_LOGO_OUTPUT_DETAILS))));
        panel.setBounds(0, -5, 500, 70);
        panel.setVisible(true);

        initFormulaLabels(abstrExprs, texts);
        initFormulas(abstrExprs, texts);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (instance != null) {
                    instance.dispose();
                }
                instance = null;
            }
        });

        setVisible(true);

        validate();
        repaint();
    }

    private void initFormulaLabels(AbstractExpression[] abstrExprs, String[] texts) {
        this.formulaLabels = new JLabel[abstrExprs.length + texts.length];
        for (int i = 0; i < abstrExprs.length + texts.length; i++) {
            this.formulaLabels[i] = new JLabel(Translator.translateOutputMessage(GUI_OutputDetailsGUI_GENERAL_AVAILABLE_EXPRESSION, i + 1));
            add(this.formulaLabels[i]);
            this.formulaLabels[i].setBounds(10, 80 + 35 * i, 100, 25);
        }
    }

    private void initFormulas(AbstractExpression[] abstrExprs, String[] texts) {
        this.formulas = new JTextField[abstrExprs.length + texts.length];
        for (int i = 0; i < abstrExprs.length + texts.length; i++) {
            if (i < abstrExprs.length) {
                this.formulas[i] = new JTextField(abstrExprs[i].toString());
                add(this.formulas[i]);
                this.formulas[i].setBounds(100, 80 + 35 * i, this.getWidth() - 120, 25);
                this.formulas[i].setEditable(false);
            } else {
                this.formulas[i] = new JTextField(texts[i - abstrExprs.length]);
                add(this.formulas[i]);
                this.formulas[i].setBounds(100, 80 + 35 * i, this.getWidth() - 120, 25);
                this.formulas[i].setEditable(false);
            }
        }
    }

    public static OutputDetailsGUI getInstance(int mathToolGUIX, int mathToolGUIY, int mathToolGUIWidth, int mathToolGUIHeight, int width, AbstractExpression[] abstrExprs, String[] texts) {
        if (instance == null) {
            instance = new OutputDetailsGUI(mathToolGUIX, mathToolGUIY, mathToolGUIWidth, mathToolGUIHeight, width, abstrExprs, texts);
        } else {
            close();
            instance = new OutputDetailsGUI(mathToolGUIX, mathToolGUIY, mathToolGUIWidth, mathToolGUIHeight, width, abstrExprs, texts);
        }
        instance.setVisible(true);
        return instance;
    }

    public static void close() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

}
