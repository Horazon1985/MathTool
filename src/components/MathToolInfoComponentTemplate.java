package components;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import translator.Translator;

public class MathToolInfoComponentTemplate extends JDialog {

    private final JPanel headerPanel;
    private final JLabel headerLabel;
    private final ImageIcon headerImage;
    private final JLabel[] infoLabels;
    private final Color[] colors;
    private final JLabel[] coloredInfoLabels;
    private final JLabel[] menuLabels;
    private final JEditorPane infoEditorPane;

    /**
     * Aufbau:<br>
     * Ganz oben: Logo.<br>
     * Danach: Eine Reihe von Informationstexten.<br>
     * Danach: Eine Reihe von Menüpunkten.<br>
     * Schließlich: Eine TextArea, die mal ein-, mal ausgeblendet werden kann.
     */
    public MathToolInfoComponentTemplate(int mathtoolformX, int mathtoolformY,
            int mathtoolformWidth, int mathtoolformHeight,
            String titleID, String headerImageFilePath,
            String[] information, String[] coloredInformation, Color[] colors,
            String[] menuText, String[] fileName) {

        setTitle(Translator.translateExceptionMessage(titleID));
        setLayout(null);
        setResizable(false);
        setModal(true);
        this.getContentPane().setBackground(Color.white);

        // Logo ganz oben laden.
        this.headerPanel = new JPanel();
        add(this.headerPanel);
        this.headerImage = new ImageIcon(getClass().getResource(headerImageFilePath));
        this.headerLabel = new JLabel(this.headerImage);
        this.headerPanel.add(this.headerLabel);
        this.headerPanel.setBounds(0, -5, 500, 60);
        this.headerPanel.setVisible(true);

        int numberOfInfoLabels, numberOfColoredInfoLabels, numberOfMenuLabels;
        if (information == null) {
            numberOfInfoLabels = 0;
        } else {
            numberOfInfoLabels = information.length;
        }
        if (coloredInformation == null) {
            numberOfColoredInfoLabels = 0;
        } else {
            numberOfColoredInfoLabels = coloredInformation.length;
        }
        if (menuText == null) {
            numberOfMenuLabels = 0;
        } else {
            numberOfMenuLabels = menuText.length;
        }

        int numberOfLabels = numberOfInfoLabels + numberOfColoredInfoLabels + numberOfMenuLabels;
        int heightTextArea;
        if (menuText == null) {
            heightTextArea = 0;
        } else {
            heightTextArea = 300;
        }

        // Größe der Komponente festlegen.
        this.setBounds((mathtoolformWidth - 500) / 2 + mathtoolformX,
                (mathtoolformHeight - 165 - 20 * numberOfLabels) / 2 + mathtoolformY,
                500, 165 + 20 * numberOfLabels + heightTextArea);

        // Info-Labels einfügen
        this.infoLabels = new JLabel[numberOfInfoLabels];
        for (int i = 0; i < numberOfInfoLabels; i++) {
            this.infoLabels[i] = new JLabel(information[i]);
            this.infoLabels[i].setBounds(10, 90 + 20 * i, 470, 25);
        }

        // Farbige Info-Labels einfügen
        this.colors = colors;
        this.coloredInfoLabels = new JLabel[numberOfColoredInfoLabels];
        for (int i = 0; i < numberOfColoredInfoLabels; i++) {
            this.coloredInfoLabels[i] = new JLabel(coloredInformation[i]);
            this.coloredInfoLabels[i].setForeground(this.colors[i]);
            this.coloredInfoLabels[i].setBounds(10, 90 + 20 * (numberOfInfoLabels + i), 470, 25);
        }

        // Menütext-Labels einfügen
        this.menuLabels = new JLabel[numberOfMenuLabels];
        for (int i = 0; i < numberOfMenuLabels; i++) {
            this.menuLabels[i] = new JLabel(menuText[i]);
            this.menuLabels[i].setBounds(10, 90
                    + 20 * (numberOfMenuLabels + numberOfInfoLabels + i), 470, 25);
            // Für jeden Menüpunkt Listener und html-Seite definieren.

        }

        // TextArea einfügen
        if (menuText != null) {
            this.infoEditorPane = new JEditorPane();
            this.infoEditorPane.setContentType("text/html");
            this.add(this.infoEditorPane);
            this.infoEditorPane.setBounds(20, 330, 460, 300);
            this.infoEditorPane.setEditable(false);
            this.infoEditorPane.setVisible(false);
            JScrollPane scrollPaneHelp = new JScrollPane(this.infoEditorPane);
            scrollPaneHelp.setBounds(20, 330, 460, 300);
            scrollPaneHelp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            add(scrollPaneHelp);
            scrollPaneHelp.setVisible(false);
        } else {
            this.infoEditorPane = null;
        }

        this.setVisible(true);
        // Zum Schluss: Komponenten korrekt ausrichten und alles nachzeichnen.
        validate();
        repaint();

    }

}
