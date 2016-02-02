package components;

import javax.swing.JFileChooser;
import mathtool.session.SessionLoader;

public class MathToolSaveSessionDialog extends JFileChooser {

    public void save(String path) {
        SessionLoader.sessionToXML(path + ".xml");
    }

}
