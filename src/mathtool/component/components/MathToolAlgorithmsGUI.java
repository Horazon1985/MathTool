package mathtool.component.components;

import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import exceptions.EvaluationException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import mathtool.lang.translator.Translator;

public class MathToolAlgorithmsGUI extends JDialog {

    private JPanel headerPanel;
    private JLabel headerLabel;
    private ImageIcon headerImage;

    private JButton addAlgorithmButton;
    private JButton runButton;
    private JButton debugButton;

    private final int STUB = 20;
    private final int PADDING = 20;

    private JEditorPane algorithmEditor;

    private static final String GUI_MathToolAlgorithmsGUI_ADD_ALGORITHM = "GUI_MathToolAlgorithmsGUI_ADD_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_RUN = "GUI_MathToolAlgorithmsGUI_RUN";
    private static final String GUI_MathToolAlgorithmsGUI_DEBUG = "GUI_MathToolAlgorithmsGUI_DEBUG";

    private static MathToolAlgorithmsGUI instance = null;

    public static MathToolAlgorithmsGUI getInstance(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiHeight, String titleID) {
        if (instance == null) {
            instance = new MathToolAlgorithmsGUI(mathtoolGuiX, mathtoolGuiY, mathtoolGuiHeight, titleID);
        }
        return instance;
    }

    /**
     * Aufbau:<br>
     * (1) Ganz oben: Logo.<br>
     * (2) Eine Reihe von Informationstexten.<br>
     * (3) Eine Reihe von Informationstexten.<br>
     * (4) Eine Reihe von Menüpunkten.<br>
     * (5) Eine TextArea, die mal ein-, mal ausgeblendet werden kann.
     */
    private MathToolAlgorithmsGUI(int mathtoolGuiX, int mathtoolGuiY, int mathtoolGuiHeight, String titleID) {

        setTitle(Translator.translateOutputMessage(titleID));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);
        this.getContentPane().setBackground(Color.white);

        int currentComponentLevel;

        try {
            // Logo ganz oben laden.
            this.headerPanel = new JPanel();
            add(this.headerPanel);
            this.headerImage = new ImageIcon(getClass().getResource("icons/MathToolAlgorithmsLogo.png"));
            if (this.headerImage != null) {
                this.headerLabel = new JLabel(this.headerImage);
                this.headerPanel.add(this.headerLabel);
                this.headerPanel.setBounds(0, -5, this.headerImage.getIconWidth(), this.headerImage.getIconHeight());
                this.headerPanel.setVisible(true);
                currentComponentLevel = this.STUB + this.headerImage.getIconHeight() - 5;
            } else {
                this.headerLabel = new JLabel();
                currentComponentLevel = this.STUB;
            }

            setBounds(mathtoolGuiX, mathtoolGuiY, this.headerImage.getIconWidth(), mathtoolGuiHeight);

            this.algorithmEditor = new JEditorPane();
            add(this.algorithmEditor);
            this.algorithmEditor.setContentType("text/html; charset=UTF-8");
            this.algorithmEditor.setBounds(PADDING, 180, this.getWidth() - 2 * PADDING, this.getHeight() - 400);
            this.algorithmEditor.setVisible(true);
            this.algorithmEditor.setBorder(new LineBorder(Color.black, 1));
            currentComponentLevel += this.algorithmEditor.getHeight() + STUB;
            
            this.algorithmEditor.setText("<b><font color=\"blue\">expression</font></b> main(){expression x = 4; return x;}");

            this.addAlgorithmButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_ADD_ALGORITHM));
            add(this.addAlgorithmButton);
            this.addAlgorithmButton.setBounds(PADDING, currentComponentLevel, 200, 30);

            this.runButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_RUN));
            add(this.runButton);
            this.runButton.setBounds(2 * PADDING + 200, currentComponentLevel, 200, 30);

            this.debugButton = new JButton(Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_DEBUG));
            add(this.debugButton);
            this.debugButton.setBounds(3 * PADDING + 400, currentComponentLevel, 200, 30);

            this.runButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    String algString = algorithmEditor.getText();
                    try {
                        AlgorithmCompiler.parseAlgorithmFile(algString);
                        algorithmexecutor.AlgorithmExecutor.executeAlgorithm(AlgorithmCompiler.STORED_ALGORITHMS);
                    } catch (AlgorithmCompileException | AlgorithmExecutionException | EvaluationException e) {
                        // TO DO.
                    }

                }
            });

            // Zum Schluss: Komponenten korrekt ausrichten und alles nachzeichnen.
            validate();
            repaint();

        } catch (Exception e) {
        }

    }

    private String getCodeWithBoldKeywords(String code) {
        String codeFormatted = code;
        for (Keywords keyword : Keywords.values()) {
            codeFormatted = codeFormatted.replaceAll(keyword.getValue(), "<b><font color=\"blue\">" + keyword.getValue() + "</font></b>");
        }
        return codeFormatted;
    }

}
