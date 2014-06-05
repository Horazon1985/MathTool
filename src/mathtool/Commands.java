package mathtool;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import expressionbuilder.*;
import java.util.HashSet;

public class Commands {

    //Anfangsinitialisierung.
    private boolean valid = true;
    private Expression expr = new Constant(0);
    private TypeOutput typeOutput = TypeOutput.EXPRESSION;
    
    
    //Konstruktor
    public Commands(){
        this.valid = true;
        this.expr = new Constant(0);
        this.typeOutput = TypeOutput.EXPRESSION;
    }
 
    //Konstruktor
    public Commands(boolean valid, Expression expr, TypeOutput typeOutput){
        this.valid = valid;
        this.expr = expr;
        this.typeOutput = typeOutput;
    }

    
    //Getter und Setter
    //Getter
    public boolean getValid(){
        return this.valid;
    }
    
    public TypeOutput getTypeOutput(){
        return this.typeOutput;
    }
    

    //Setter
    public void setValid(boolean valid){
        this.valid = valid;
    }

    public void setTypeOutput(TypeOutput typeOutput){
        this.typeOutput = typeOutput;
    }
    
    
    public void output(JTextArea area, GraphicMethods2D graphicMethods2D,
            GraphicMethods3D graphicMethods3D) {
 
        if (this.typeOutput.equals(typeOutput.EXPRESSION)){
            area.append(this.expr.writeFormula());
        } else 
        if (this.typeOutput.equals(typeOutput.ONEDIMARRAY)){

        } else 
        if (this.typeOutput.equals(typeOutput.TWODIMARRAY)){

        } else 
        if (this.typeOutput.equals(typeOutput.TWODIMGRAPH)){

        } else 
        if (this.typeOutput.equals(typeOutput.THREEDIMGRAPH)){

        }
    
    }
            
            
            
    
}
