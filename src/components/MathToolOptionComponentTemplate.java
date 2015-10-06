package components;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import translator.Translator;

public abstract class MathToolOptionComponentTemplate extends JDialog {

    private final JPanel headerPanel;
    private final JLabel headerLabel;
    private final ImageIcon headerImage;
    private final int numberOfColums;
    private final JLabel optionGroupLabel;
    private final ArrayList<JCheckBox> optionLabels;
    private final JButton saveButton;
    private final JButton cancelButton;

    private final int stub = 20;

    /**
     * Aufbau:<br>
     * (1) Ganz oben: Logo.<br>
     * (2) Eine Reihe von Informationstexten.<br>
     * (3) Eine Reihe von Informationstexten.<br>
     * (4) Eine Reihe von Menüpunkten.<br>
     * (5) Eine TextArea, die mal ein-, mal ausgeblendet werden kann.
     */
    public MathToolOptionComponentTemplate(int mathtoolformX, int mathtoolformY,
            int mathtoolformWidth, int mathtoolformHeight,
            String titleID, String headerImageFilePath,
            int numberOfColumns, String optionGroupName, ArrayList<String> options,
            String saveButtonLabel, String cancelButtonLabel) {

        setTitle(Translator.translateExceptionMessage(titleID));
        setLayout(null);
        setResizable(false);
        setModal(true);
        this.getContentPane().setBackground(Color.white);

        int currentComponentLevel;

        // Logo ganz oben laden.
        if (headerImageFilePath != null) {
            this.headerPanel = new JPanel();
            add(this.headerPanel);
            this.headerImage = new ImageIcon(getClass().getResource(headerImageFilePath));
            this.headerLabel = new JLabel(this.headerImage);
            this.headerPanel.add(this.headerLabel);
            this.headerPanel.setBounds(0, -5, this.headerImage.getIconWidth(), this.headerImage.getIconHeight());
            this.headerPanel.setVisible(true);
            currentComponentLevel = this.stub + this.headerImage.getIconHeight() - 5;
        } else {
            this.headerPanel = null;
            this.headerImage = null;
            this.headerLabel = null;
            currentComponentLevel = this.stub;
        }

        this.numberOfColums = numberOfColumns;

        int numberOfOptionLabels = 0;
        if (options != null) {
            numberOfOptionLabels = options.size();
        }
        int numberOfOptionRows;
        if (numberOfOptionLabels % numberOfColums == 0) {
            numberOfOptionRows = numberOfOptionLabels / numberOfColums;
        } else {
            numberOfOptionRows = numberOfOptionLabels / numberOfColums + 1;
        }

        // Größe der Komponente festlegen.
        int height = 160 + 20 * numberOfOptionRows, width;
        if (this.headerImage != null) {
            height = height + this.headerImage.getIconHeight();
            width = this.headerImage.getIconWidth();
        } else {
            width = 650;
        }
        this.setBounds((mathtoolformWidth - width) / 2 + mathtoolformX,
                (mathtoolformHeight - height) / 2 + mathtoolformY,
                width, height);

        // Info-Labels einfügen
        // Die Labels werden in Spalten mit einer Breite von jeweils 300 Pixel aufgeteilt.
        this.optionGroupLabel = new JLabel("<html><b>" + optionGroupName + "</b></html>");
        this.add(this.optionGroupLabel);
        this.optionGroupLabel.setBounds(10, currentComponentLevel, width - 10, 20);
        currentComponentLevel += 20 + this.stub;
        this.optionLabels = new ArrayList<>();
        if (options != null) {
            for (int i = 0; i < numberOfOptionLabels; i++) {
                this.optionLabels.add(new JCheckBox(Translator.translateExceptionMessage(options.get(i))));
                this.add(this.optionLabels.get(i));
                this.optionLabels.get(i).setBounds(10 + 300 * (i % numberOfColumns), currentComponentLevel, 300, 25);
                if ((i + 1) % numberOfColumns == 0) {
                    currentComponentLevel += 20;
                }
            }
            currentComponentLevel += 20;
        }

        currentComponentLevel += this.stub;

        this.saveButton = new JButton(saveButtonLabel);
        this.cancelButton = new JButton(cancelButtonLabel);
        this.add(this.saveButton);
        this.add(this.cancelButton);
        this.saveButton.setBounds(10, currentComponentLevel, 200, 30);
        this.cancelButton.setBounds(220, currentComponentLevel, 200, 30);

    }

}
