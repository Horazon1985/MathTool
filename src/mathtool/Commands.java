package mathtool;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import expressionbuilder.*;
import java.util.HashSet;

public class Commands {

    //Anfangsinitialisierung.
    private double value = 0;
    private double[] valueArray = new double[1]; 
    private TypeOutput typeOutput = TypeOutput.VALUE;
    private Expression expression = new Constant(0);
    
 
    //Getter und Setter
    //Getter
    public double getValue(){
        return this.value;
    }
    
    public double[] getValueArray(){
        return this.valueArray;
    }
    
    public TypeOutput getTypeOutput(){
        return this.typeOutput;
    }
    
    public Expression getExpression(){
        return this.expression;
    }

    //Setter
    public void setValue(double value){
        this.value = value;
    }
    
    public void setValueArray(double[] valueArray){
        this.valueArray = new double[valueArray.length];
        for (int i = 0; i < valueArray.length; i++){
            this.valueArray[i] = valueArray[i];
        }
    }
    
    public void setTypeOutput(TypeOutput typeOutput){
        this.typeOutput = typeOutput;
    }
    
    public void setExpression(Expression expression){
        this.expression = expression;
    }

    
    
    
    public Commands diff(String[] params) throws ExpressionException, WrongParameterException {
        
        if (params.length < 2){
            throw new WrongParameterException("Zu wenig Parameter.");
        } else
        if (params.length > 3){
            throw new WrongParameterException("Zu viele Parameter.");
        }
        
        Commands c = new Commands();
        c.setTypeOutput(TypeOutput.EXPRESSION);

        try{

            HashSet vars = new HashSet();
            Expression expr = Expression.build(params[0], vars);

            if (params.length == 2){
                expr = expr.diff(params[1], 0);
                expr = expr.simplify();
            } else {
                int k = Integer.parseInt(params[2]);
                for (int i = 0; i < k; i++){
                    expr = expr.diff(params[1], 0);
                }
                expr = expr.simplify();
            }
            
            c.setExpression(expr);
            
        } catch (ExpressionException e){
            JOptionPane.showMessageDialog(null, "Fehler! " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
        
        return c;

    }
    

    public void integral(String[] params, JTextArea area){
    
        
    
    
    }
    
    
    public void output(JTextArea area, GraphicMethods2D graphicMethods2D,
            GraphicMethods3D graphicMethods3D) {
 
        if (this.typeOutput.equals(typeOutput.VALUE)){
            area.append(String.valueOf(this.value) + "\n");
        } else 
        if (this.typeOutput.equals(typeOutput.ONEDIMARRAY)){

        } else 
        if (this.typeOutput.equals(typeOutput.TWODIMARRAY)){

        } else 
        if (this.typeOutput.equals(typeOutput.EXPRESSION)){
            area.append(this.getExpression().writeFormula() + "\n");
        } else 
        if (this.typeOutput.equals(typeOutput.TWODIMGRAPH)){

        } else 
        if (this.typeOutput.equals(typeOutput.THREEDIMGRAPH)){

        }
    
    }
            
            
            
    
}
