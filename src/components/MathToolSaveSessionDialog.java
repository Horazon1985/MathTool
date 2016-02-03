package components;

import javax.swing.JFileChooser;
import mathtool.MathToolController;
import mathtool.session.SessionLoader;

public class MathToolSaveSessionDialog extends JFileChooser {

    public void save(String path) {
        SessionLoader.sessionToXML(path + ".xml");
    }

    public void open(String path) {
        MathToolController.loadSession(path);
    }

}
