package components;

import graphic.Exportable;
import javax.swing.JFileChooser;

public class MathToolSaveGraphicDialog extends JFileChooser {

    private final Exportable image;
    
    public MathToolSaveGraphicDialog(Exportable image){
        this.image = image;
    }

    public void save(String path){
        image.export(path + ".png");
    }
    
    
}
