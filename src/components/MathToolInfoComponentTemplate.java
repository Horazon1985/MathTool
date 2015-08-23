package components;

import java.awt.Color;
import java.util.ArrayList;
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
    private final ArrayList<JLabel> infoLabels;
    private final ArrayList<Color> colors;
    private final ArrayList<JLabel> coloredInfoLabels;
    private final ArrayList<JLabel> menuLabels;
    private final JEditorPane infoEditorPane;
    
    private final int stub = 20;

    /**
     * Aufbau:<br>
     * (1) Ganz oben: Logo.<br>
     * (2) Eine Reihe von Informationstexten.<br>
     * (3) Eine Reihe von Informationstexten.<br>
     * (4) Eine Reihe von Menüpunkten.<br>
     * (5) Eine TextArea, die mal ein-, mal ausgeblendet werden kann.
     */
    public MathToolInfoComponentTemplate(int mathtoolformX, int mathtoolformY,
            int mathtoolformWidth, int mathtoolformHeight,
            String titleID, String headerImageFilePath,
            ArrayList<String> information, ArrayList<String> coloredInformation,
            ArrayList<Color> colors, ArrayList<String> menuText, ArrayList<String> fileName) {

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
            numberOfInfoLabels = information.size();
        }
        if (coloredInformation == null) {
            numberOfColoredInfoLabels = 0;
        } else {
            numberOfColoredInfoLabels = coloredInformation.size();
        }
        if (menuText == null) {
            numberOfMenuLabels = 0;
        } else {
            numberOfMenuLabels = menuText.size();
        }

        int numberOfLabels = numberOfInfoLabels + numberOfColoredInfoLabels + numberOfMenuLabels;
        int heightTextArea;
        if (fileName == null) {
            heightTextArea = 0;
        } else {
            heightTextArea = 300;
        }

        // Größe der Komponente festlegen.
        this.setBounds((mathtoolformWidth - 500) / 2 + mathtoolformX,
                (mathtoolformHeight - 165 - 20 * numberOfLabels) / 2 + mathtoolformY,
                500, 130 + 20 * numberOfLabels + heightTextArea);

        // Info-Labels einfügen
        this.infoLabels = new ArrayList<>();
        if (information != null) {
            for (int i = 0; i < numberOfInfoLabels; i++) {
                this.infoLabels.add(new JLabel(information.get(i)));
                this.add(this.infoLabels.get(i));
                this.infoLabels.get(i).setBounds(10, 70 + 20 * i, 470, 25);
            }
        }

        // Farbige Info-Labels einfügen
        this.colors = colors;
        this.coloredInfoLabels = new ArrayList<>();
        if (coloredInformation != null) {
            for (int i = 0; i < numberOfColoredInfoLabels; i++) {
                this.coloredInfoLabels.add(new JLabel(coloredInformation.get(i)));
                this.add(this.coloredInfoLabels.get(i));
                this.coloredInfoLabels.get(i).setForeground(this.colors.get(i));
                this.coloredInfoLabels.get(i).setBounds(10, 70 + 20 * (numberOfInfoLabels + i), 470, 25);
            }
        }

        // Menütext-Labels einfügen
        this.menuLabels = new ArrayList<>();
        if (menuText != null) {
            for (int i = 0; i < numberOfMenuLabels; i++) {
                this.menuLabels.add(new JLabel(menuText.get(i)));
                this.menuLabels.get(i).setBounds(10, 90
                        + 20 * (numberOfMenuLabels + numberOfInfoLabels + i), 470, 25);
                // Für jeden Menüpunkt Listener und html-Seite definieren.

            }
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

        // Zum Schluss: Komponenten korrekt ausrichten und alles nachzeichnen.
        validate();
        repaint();

    }

}
