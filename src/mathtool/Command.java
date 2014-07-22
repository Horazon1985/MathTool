package mathtool;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import expressionbuilder.*;
import java.util.HashSet;
import java.util.Hashtable;

public class Command {

    private String name;
    private Object[] params;
    
    //Getter und Setter
    //Getter
    public String getName(){
        return this.name;
    }
    
    public Object[] getParams(){
        return this.params;
    }
    
    //Setter
    public void setName(String name){
        this.name = name;
    }
    
    public void setParams(Object[] params){
        this.params = params;
    }
            
    
}
