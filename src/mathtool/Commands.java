package mathtool;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import expressionbuilder.*;
import java.util.HashSet;

public class Commands {

    //Anfangsinitialisierung.
    private double value = 0;
    private double[] valueArray = new double[1]; 
    private Expression expression = new Constant(0);
    private HashSet vars = new HashSet();
    private boolean valid = true;
    private TypeOutput typeOutput = TypeOutput.VALUE;
    
 
    //Getter und Setter
    //Getter
    public double getValue(){
        return this.value;
    }
    
    public double[] getValueArray(){
        return this.valueArray;
    }
    
    public Expression getExpression(){
        return this.expression;
    }

    public HashSet getVars(){
        return this.vars;
    }

    public boolean getValid(){
        return this.valid;
    }
    
    public TypeOutput getTypeOutput(){
        return this.typeOutput;
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
    
    public void setExpression(Expression expression){
        this.expression = expression;
    }

    public void setVars(HashSet vars){
        this.vars = vars;
    }

    public void setValid(boolean valid){
        this.valid = valid;
    }

    public void setTypeOutput(TypeOutput typeOutput){
        this.typeOutput = typeOutput;
    }
    

    
    
    
    public Commands diff(String[] params) throws CompileException {
        
        if (params.length < 2){
            throw new CompileException("Zu wenig Parameter.");
        } else
        if (params.length > 3){
            throw new CompileException("Zu viele Parameter.");
        }
        
        Commands c = new Commands();
        c.setTypeOutput(TypeOutput.EXPRESSION);
        c.setValid(false);

        try{

            Expression expr = Expression.build(params[0], c.vars);

            if (params.length == 2){
                expr = expr.diff(params[1], 0);
                expr = expr.simplify();
            } else {
                int k = Integer.parseInt(params[2]);
                for (int i = 0; i < k; i++){
                    expr = expr.diff(params[1], 0);
                    expr = expr.simplify();
                }
            }
            
            c.setExpression(expr);
            c.setValid(true);
            return c;
            
        } catch (ExpressionException e){
        }
        
        
        MathCommandCompiler mcc = new MathCommandCompiler();
        Commands c_interior = new Commands();
        
        try{

            /**Versuchen, den ersten Parameter als Expression zu interpretieren und dann ableiten
             */
            c_interior = mcc.executeCommand(params[0]);
            if ((c_interior.getValid()) && (c_interior.getTypeOutput().equals(TypeOutput.EXPRESSION))){
                Expression expr = c_interior.getExpression();
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
                c.setValid(true);
            }

            /**Versuchen, den ersten Parameter als Konstante zu interpretieren. Die Ableitung
             ist dann 0, falls der dritte Parameter eine ganze Zahl ist. 
             */
            if ((c_interior.getValid()) && (c_interior.getTypeOutput().equals(TypeOutput.VALUE))){
                c.setValue(c_interior.getValue());
                c.setValid(true);
            }
            
        } catch (ExpressionException e){
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
