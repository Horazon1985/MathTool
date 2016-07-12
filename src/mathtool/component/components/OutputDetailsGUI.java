package mathtool.component.components;

import abstractexpressions.interfaces.AbstractExpression;
import lang.translator.Translator;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Ausgabedetails. Singletonklasse.
 */
public class OutputDetailsGUI extends JDialog {

    private static OutputDetailsGUI instance = null;

    private JLabel[] formulaLabels;
    private JTextField[] formulas;

    public OutputDetailsGUI(int mouseX, int mouseY, int width, AbstractExpression... abstrExprs) {

        setTitle(Translator.translateOutputMessage("GUI_OutputDetailsGUI_INFO"));
        setLayout(null);
        setResizable(false);
        setModal(true);

        this.setBounds(mouseX, mouseY, width, 50 + 35 * abstrExprs.length);
        this.getContentPane().setBackground(Color.white);

        initFormulaLabels(abstrExprs);
        initFormulas(abstrExprs);

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

    private void initFormulaLabels(AbstractExpression... abstrExpr) {
        this.formulaLabels = new JLabel[abstrExpr.length];
        for (int i = 0; i < abstrExpr.length; i++) {
            this.formulaLabels[i] = new JLabel(Translator.translateOutputMessage("GUI_OutputDetailsGUI_GENERAL_AVAILABLE_EXPRESSION", i + 1));
            add(this.formulaLabels[i]);
            this.formulaLabels[i].setBounds(10, 10 + 35 * i, 100, 25);
        }
    }

    private void initFormulas(AbstractExpression... abstrExpr) {
        this.formulas = new JTextField[abstrExpr.length];
        for (int i = 0; i < abstrExpr.length; i++) {
            this.formulas[i] = new JTextField(abstrExpr[i].toString());
            add(this.formulas[i]);
            this.formulas[i].setBounds(100, 10 + 35 * i, 100, 25);
            this.formulas[i].setEditable(false);
        }
    }

    public static OutputDetailsGUI getInstance(int mouseX, int mouseY, int width, AbstractExpression... abstrExprs) {
        if (instance == null) {
            instance = new OutputDetailsGUI(mouseX, mouseY, width, abstrExprs);
        }
        return instance;
    }

    public static void close() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

}
