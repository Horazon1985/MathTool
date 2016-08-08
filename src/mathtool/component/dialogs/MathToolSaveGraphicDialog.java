package mathtool.component.dialogs;

import graphic.AbstractGraphicPanel;
import java.io.IOException;
import javax.swing.JFileChooser;

public class MathToolSaveGraphicDialog extends JFileChooser {

    private final AbstractGraphicPanel image;

    public MathToolSaveGraphicDialog(AbstractGraphicPanel image) {
        this.image = image;
    }

    public void save(String path) {
        try {
            image.export(path + ".png");
        } catch (IOException e) {
            // TO DO.
        }
    }

}
