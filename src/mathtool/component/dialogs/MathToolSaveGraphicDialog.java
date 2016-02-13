package mathtool.component.dialogs;

import graphic.Exportable;
import java.io.IOException;
import javax.swing.JFileChooser;

public class MathToolSaveGraphicDialog extends JFileChooser {

    private final Exportable image;

    public MathToolSaveGraphicDialog(Exportable image) {
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
