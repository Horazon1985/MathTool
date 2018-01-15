package mathtool.component.templates;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mathtool.lang.translator.Translator;

public abstract class MathToolInfoComponentTemplate extends JDialog {

    private JPanel headerPanel;
    private JLabel headerLabel;
    private ImageIcon headerImage;
    private List<JLabel> infoLabels;
    private List<Color> colors;
    private List<JLabel> coloredInfoLabels;
    private List<JLabel> menuLabels;
    private List<String> fileNames;
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
    public MathToolInfoComponentTemplate(int mathtoolGuiX, int mathtoolGuiY,
            int mathtoolGuiWidth, int mathtoolGuiHeight,
            String titleID, String headerImageFilePath,
            List<String> informations, List<String> coloredInformations,
            List<Color> colors, List<String> menuCaptions, List<String> files) {

        setTitle(Translator.translateOutputMessage(titleID));
        setLayout(null);
        setResizable(false);
        setAlwaysOnTop(true);
        getContentPane().setBackground(Color.white);

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

            this.fileNames = files;

            // Größe der Komponente festlegen.
            int height = 80 + 20 * numberOfLabels + this.heightTextArea, width;
            if (this.headerImage != null) {
                height = height + this.headerImage.getIconHeight();
                width = this.headerImage.getIconWidth();
            } else {
                // Standardbreite = 500 Pixel.
                width = 500;
            }
            this.setBounds((mathtoolGuiWidth - width) / 2 + mathtoolGuiX,
                    (mathtoolGuiHeight - height) / 2 + mathtoolGuiY,
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

            // Menütext-Labels einfügen
            this.menuLabels = new ArrayList<>();
            if (menuCaptions != null) {
                for (int i = 0; i < numberOfMenuLabels; i++) {
                    if (i == 0 && (informations != null || coloredInformations != null)) {
                        currentComponentLevel += this.stub;
                    }
                    this.menuLabels.add(new JLabel(menuCaptions.get(i)));
                    this.menuLabels.get(i).setBounds(10, currentComponentLevel, 470, 25);
                    this.add(this.menuLabels.get(i));
                    currentComponentLevel += 20;
                    // Für jeden Menüpunkt Listener und html-Seite definieren.
                    this.menuLabels.get(i).addMouseListener(new MouseListener() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            for (int i = 0; i < getMenuLabels().size(); i++) {
                                if (e.getSource() == getMenuLabels().get(i)) {
                                    getMenuLabels().get(i).setForeground(Color.blue);
                                    showFile(getFileNames().get(i));
                                } else {
                                    getMenuLabels().get(i).setForeground(Color.black);
                                }
                            }
                            validate();
                            repaint();
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            JLabel sourceLabel;
                            for (Iterator<JLabel> iterator = getMenuLabels().iterator(); iterator.hasNext();) {
                                sourceLabel = iterator.next();
                                if (e.getSource() == sourceLabel) {
                                    sourceLabel.setText("<html><u>" + sourceLabel.getText() + "</u></html>");
                                    break;
                                }
                            }
                            validate();
                            repaint();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            JLabel sourceLabel;
                            for (Iterator<JLabel> iterator = getMenuLabels().iterator(); iterator.hasNext();) {
                                sourceLabel = iterator.next();
                                if (e.getSource() == sourceLabel) {
                                    sourceLabel.setText(sourceLabel.getText().substring(9, sourceLabel.getText().length() - 11));
                                    break;
                                }
                            }
                            validate();
                            repaint();
                        }
                    });
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

    public abstract void showFile(String fileName);

    /**
     * @return the infoLabels
     */
    public List<JLabel> getInfoLabels() {
        return infoLabels;
    }

    /**
     * @return the colors
     */
    public List<Color> getColors() {
        return colors;
    }

    /**
     * @return the coloredInfoLabels
     */
    public List<JLabel> getColoredInfoLabels() {
        return coloredInfoLabels;
    }

    /**
     * @return the menuLabels
     */
    public List<JLabel> getMenuLabels() {
        return menuLabels;
    }

    /**
     * @return the fileNames
     */
    public List<String> getFileNames() {
        return fileNames;
    }

    /**
     * @return the infoEditorPane
     */
    public JEditorPane getInfoEditorPane() {
        return infoEditorPane;
    }

    /**
     * @return the infoScrollPane
     */
    public JScrollPane getInfoScrollPane() {
        return infoScrollPane;
    }

}
