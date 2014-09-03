package mathtool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public class LabelMouseListener extends MouseAdapter {
    
    public void mouseClicked(MouseEvent e) {
        System.out.println("Klick auf: " + ((JLabel) e.getSource()).getText());
    }
  
}
