package mathtool;

import javax.swing.JTextField;
import listeners.MathToolValueChangeListener;

public class MathToolTextField extends JTextField {

    private MathToolValueChangeListener listener;

    public MathToolTextField(){
        super();
        listener = null;
    }
    
    public void addValueChangeListener(MathToolValueChangeListener newListener){
        listener = newListener;
    }

    public void removeValueChangeListener(){
        this.listener = null;
    }
    
}
