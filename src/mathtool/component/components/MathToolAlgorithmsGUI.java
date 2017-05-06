package mathtool.component.components;

import algorithmexecutor.enums.Keywords;
import java.awt.Color;
import javax.swing.ImageIcon;
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

    private final int stub = 20;

    private JEditorPane algorithmEditor;
    
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
                currentComponentLevel = this.stub + this.headerImage.getIconHeight() - 5;
            } else {
                this.headerLabel = new JLabel();
                currentComponentLevel = this.stub;
            }
            
            setBounds(mathtoolGuiX, mathtoolGuiY, this.headerImage.getIconWidth(), mathtoolGuiHeight);

            this.algorithmEditor = new JEditorPane();
            add(this.algorithmEditor);
            this.algorithmEditor.setContentType("text/html; charset=UTF-8");
            this.algorithmEditor.setBounds(20, 180, this.getWidth() - 40, this.getHeight() - 400);
            this.algorithmEditor.setVisible(true);
            this.algorithmEditor.setBorder(new LineBorder(Color.black,1));
            this.algorithmEditor.setText("<b><font color=\"blue\">expression</font></b> main(){}");
            
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
