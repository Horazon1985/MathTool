package mathtool;

import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import expressionbuilder.*;
import java.util.HashSet;

public class Commands {

    
    public void diff(String[] params, JTextArea area) throws ExpressionException, WrongParameterException {
        
        if (params.length < 2){
            throw new WrongParameterException("Zu wenig Parameter.");
        } else
        if (params.length > 3){
            throw new WrongParameterException("Zu viele Parameter.");
        }
        
        try{

            HashSet vars = new HashSet();
            Expression expr = Expression.build(params[0], vars);

            if (params.length == 2){
                Expression result = expr.diff(params[1], 0);
                result = result.simplify();
                area.append(result.writeFormula() + "\n");
            } else {
                int k = Integer.parseInt(params[2]);
                for (int i = 0; i < k; i++){
                    expr = expr.diff(params[1], 0);
                }
                Expression result = expr.simplify();
                area.append(result.writeFormula() + "\n");
            }
            
        } catch (ExpressionException e){
            JOptionPane.showMessageDialog(null, "Fehler! " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    

    public void integrate(String[] params, JTextArea area){
    
        
    
    
    }
    
    
}
