package mathtool;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import expressionbuilder.*;
import java.util.HashSet;

public class Command {

    private String name;
    private String[] params;
    private Expression left = new Constant(0);
    
    //Getter und Setter
    //Getter
    public String getName(){
        return this.name;
    }
    
    public String[] getParams(){
        return this.params;
    }
    
    public Expression getLeft(){
        return this.left;
    }
    
    //Setter
    public void setName(String name){
        this.name = name;
    }
    
    public void setParams(String[] params){
        this.params = params;
    }
            
    public void setLeft(Expression left){
        this.left = left;
    }
            
    
}
