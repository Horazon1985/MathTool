package mathtool.component.components;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mathtool.lang.translator.Translator;

public class MathToolAlgorithmsGUI extends JDialog {
    
    private JPanel headerPanel;
    private JLabel headerLabel;
    private ImageIcon headerImage;
    private ArrayList<JLabel> infoLabels;
    private ArrayList<Color> colors;
    private ArrayList<JLabel> coloredInfoLabels;
    private JEditorPane infoEditorPane;
    private JScrollPane infoScrollPane;
    private int heightTextArea;

    private final int stub = 20;

    /**
     * Aufbau:<br>
     * (1) Ganz oben: Logo.<br>
     * (2) Eine Reihe von Informationstexten.<br>
     * (3) Eine Reihe von Informationstexten.<br>
     * (4) Eine Reihe von Menüpunkten.<br>
     * (5) Eine TextArea, die mal ein-, mal ausgeblendet werden kann.
     */
    public MathToolAlgorithmsGUI(int mathtoolGUIX, int mathtoolGUIY,
            int mathtoolGUIWidth, int mathtoolGUIHeight,
            String titleID, String headerImageFilePath,
            ArrayList<String> informations, ArrayList<String> coloredInformations,
            ArrayList<Color> colors, ArrayList<String> menuCaptions, ArrayList<String> files) {

        setTitle(Translator.translateOutputMessage(titleID));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);
        this.getContentPane().setBackground(Color.white);

        int currentComponentLevel;

        try {
            // Logo ganz oben laden.
            if (headerImageFilePath != null) {
                this.headerPanel = new JPanel();
                add(this.headerPanel);
                this.headerImage = new ImageIcon(getClass().getResource(headerImageFilePath));
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
            } else {
                this.headerPanel = null;
                this.headerImage = null;
                this.headerLabel = null;
                currentComponentLevel = this.stub;
            }

            int numberOfInfoLabels, numberOfColoredInfoLabels, numberOfMenuLabels;
            if (informations == null) {
                numberOfInfoLabels = 0;
            } else {
                numberOfInfoLabels = informations.size();
            }
            if (coloredInformations == null) {
                numberOfColoredInfoLabels = 0;
            } else {
                numberOfColoredInfoLabels = coloredInformations.size();
            }
            if (menuCaptions == null) {
                numberOfMenuLabels = 0;
            } else {
                numberOfMenuLabels = menuCaptions.size();
            }

            int numberOfLabels = numberOfInfoLabels + numberOfColoredInfoLabels + numberOfMenuLabels;
            if (files == null) {
                this.heightTextArea = 0;
            } else {
                this.heightTextArea = 300;
            }

            // Größe der Komponente festlegen.
            int height = 80 + 20 * numberOfLabels + this.heightTextArea, width;
            if (this.headerImage != null) {
                height = height + this.headerImage.getIconHeight();
                width = this.headerImage.getIconWidth();
            } else {
                // Standardbreite = 500 Pixel.
                width = 500;
            }
            this.setBounds((mathtoolGUIWidth - width) / 2 + mathtoolGUIX,
                    (mathtoolGUIHeight - height) / 2 + mathtoolGUIY,
                    width, height);

            // Info-Labels einfügen
            this.infoLabels = new ArrayList<>();
            if (informations != null) {
                for (int i = 0; i < numberOfInfoLabels; i++) {
                    this.infoLabels.add(new JLabel(informations.get(i)));
                    this.add(this.infoLabels.get(i));
                    this.infoLabels.get(i).setBounds(10, currentComponentLevel, 470, 25);
                    currentComponentLevel += 20;
                }
            }

            // Farbige Info-Labels einfügen
            this.colors = colors;
            this.coloredInfoLabels = new ArrayList<>();
            if (coloredInformations != null) {
                for (int i = 0; i < numberOfColoredInfoLabels; i++) {
                    if (i == 0 && informations != null) {
                        currentComponentLevel += this.stub;
                    }
                    this.coloredInfoLabels.add(new JLabel(coloredInformations.get(i)));
                    this.add(this.coloredInfoLabels.get(i));
                    this.coloredInfoLabels.get(i).setForeground(this.colors.get(i));
                    this.coloredInfoLabels.get(i).setBounds(10, currentComponentLevel, 470, 25);
                    currentComponentLevel += 20;
                }
            }

            // TextArea einfügen
            if (menuCaptions != null) {
                if (informations != null || coloredInformations != null || menuCaptions != null) {
                    currentComponentLevel += this.stub;
                }
                this.infoEditorPane = new JEditorPane();
                this.infoEditorPane.setContentType("text/html; charset=UTF-8");
                this.add(this.infoEditorPane);
                this.infoEditorPane.setBounds(20, currentComponentLevel, 460, 300);
                this.infoEditorPane.setEditable(false);
                this.infoEditorPane.setVisible(false);
                this.infoScrollPane = new JScrollPane(this.infoEditorPane);
                this.infoScrollPane.setBounds(20, currentComponentLevel, 460, 300);
                this.infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                add(this.infoScrollPane);
                this.infoScrollPane.setVisible(false);
                currentComponentLevel += heightTextArea;
            } else {
                this.infoEditorPane = null;
                this.infoScrollPane = null;
            }

            // Zum Schluss: Komponenten korrekt ausrichten und alles nachzeichnen.
            validate();
            repaint();

        } catch (Exception e) {
        }

    }
    
    
    
    
    
}
