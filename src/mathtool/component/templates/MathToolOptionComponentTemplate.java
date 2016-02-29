package mathtool.component.templates;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lang.translator.Translator;

public abstract class MathToolOptionComponentTemplate extends JDialog {

    private final JPanel headerPanel;
    private final JLabel headerLabel;
    private final ImageIcon headerImage;
    private final int numberOfColums;
    private final JLabel optionGroupLabel;
    private final ArrayList<JCheckBox> optionCheckBoxes;
    private final ArrayList<JComboBox<String>> optionDropDowns;
    private final JButton saveButton;
    private final JButton cancelButton;

    private final int stub = 30;

    /**
     * Aufbau:<br>
     * (1) Ganz oben: Logo.<br>
     * (2) Eine Reihe von Informationstexten.<br>
     * (3) Eine Reihe von Informationstexten.<br>
     * (4) Eine Reihe von Menüpunkten.<br>
     * (5) Eine Reihe von Menüpunkten als DropDowns.<br>
     * (6) Eine TextArea, die mal ein-, mal ausgeblendet werden kann.
     */
    public MathToolOptionComponentTemplate(int mathtoolformX, int mathtoolformY,
            int mathtoolformWidth, int mathtoolformHeight,
            String titleID, String headerImageFilePath,
            int numberOfColumns, String optionGroupName, ArrayList<String> optionCheckBoxes,
            ArrayList<String[]> optionsDropDowns, String saveButtonLabel, String cancelButtonLabel) {

        setTitle(Translator.translateMessage(titleID));
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

        int numberOfOptions = 0;
        int numberOfOptionLabels = 0;
        if (optionCheckBoxes != null) {
            numberOfOptions = optionCheckBoxes.size();
            numberOfOptionLabels = optionCheckBoxes.size();
        }
        if (optionsDropDowns != null) {
            numberOfOptions += optionsDropDowns.size();
        }
        int numberOfOptionRows;
        if (numberOfOptions % numberOfColums == 0) {
            numberOfOptionRows = numberOfOptions / numberOfColums;
        } else {
            numberOfOptionRows = numberOfOptions / numberOfColums + 1;
        }

        // Größe der Komponente festlegen.
        int height = 170 + 30 * numberOfOptionRows, width;
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
        this.optionGroupLabel.setBounds(10, currentComponentLevel, width - 10, 30);
        currentComponentLevel += 30;
        // Checkboxen
        this.optionCheckBoxes = new ArrayList<>();
        if (optionCheckBoxes != null) {
            JCheckBox optionBox;
            for (int i = 0; i < numberOfOptionLabels; i++) {
                optionBox = new JCheckBox(Translator.translateMessage(optionCheckBoxes.get(i)));
                optionBox.setOpaque(false);
                this.optionCheckBoxes.add(optionBox);
                this.add(this.optionCheckBoxes.get(i));
                this.optionCheckBoxes.get(i).setBounds(10 + 300 * (i % numberOfColumns), currentComponentLevel, 300, 30);
                if ((i + 1) % numberOfColumns == 0 && i + 1 < numberOfOptions) {
                    currentComponentLevel += 30;
                }
            }
        }
        // DropDowns
        this.optionDropDowns = new ArrayList<>();
        if (optionsDropDowns != null) {
            JComboBox<String> optionDropDown;
            for (int i = numberOfOptionLabels; i < numberOfOptions; i++) {
                optionDropDown = new JComboBox();
                // DrowDown mit Werten füllen
                for (String opt : optionsDropDowns.get(i - numberOfOptionLabels)) {
                    optionDropDown.addItem(opt);
                }
                optionDropDown.setOpaque(false);
                this.optionDropDowns.add(optionDropDown);
                this.add(this.optionDropDowns.get(i - numberOfOptionLabels));
                this.optionDropDowns.get(i - numberOfOptionLabels).setBounds(10 + 300 * (i % numberOfColumns), currentComponentLevel, 300, 30);
                if ((i + 1) % numberOfColumns == 0 && i + 1 < numberOfOptions) {
                    currentComponentLevel += 30;
                }
            }
            currentComponentLevel += 30;
        }

        currentComponentLevel += this.stub;

        this.saveButton = new JButton(saveButtonLabel);
        this.cancelButton = new JButton(cancelButtonLabel);
        this.add(this.saveButton);
        this.add(this.cancelButton);
        this.saveButton.setBounds(10, currentComponentLevel, 200, 30);
        this.cancelButton.setBounds(220, currentComponentLevel, 200, 30);

        this.saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveOptions();
                dispose();
            }
        });

        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

    }

    public JButton getSaveButton(){
        return this.saveButton;
    }
    
    public JButton getCancelButton(){
        return this.cancelButton;
    }
    
    public ArrayList<JCheckBox> getOptionCheckBoxes() {
        return optionCheckBoxes;
    }

    public ArrayList<JComboBox<String>> getOptionDropDowns() {
        return optionDropDowns;
    }
    
    public abstract void loadOptions();
    
    public abstract void saveOptions();

}
