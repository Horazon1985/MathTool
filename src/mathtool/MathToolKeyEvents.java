package mathtool;

import javax.swing.JTextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener; 

public abstract class MathToolKeyEvents extends JTextArea implements KeyListener {
    
    public MathToolKeyEvents(){
        addKeyListener(this);
    }

    public void keyTyped(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UNDEFINED){
            System.out.println("Kein Unicode-Character gedrückt!");
        }
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("Tastenposition: " + e.getKeyLocation());
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            System.out.println("Programmabbruch!");
            System.exit(0);
        }    
        System.out.println("Taste: " + e.getKeyChar() + ", Code: " + e.getKeyCode());
    }     
    
}
