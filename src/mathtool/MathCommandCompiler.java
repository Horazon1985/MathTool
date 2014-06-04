package mathtool;

import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;
import javax.swing.*;

public class MathCommandCompiler {

    private final String[] commands = {"diff", "int", "showGraph", "solve"};
    
    
    //Führt den Befehl aus.
    public Commands executeCommand(String commandLine) throws ExpressionException {

        String[] command_and_params = new String[2];

        command_and_params = Expression.getOperatorAndArguments(commandLine);
        String command = command_and_params[0];
        String[] params = new String[1];
        params = Expression.getArguments(command_and_params[1]);
            
        boolean valid_command = false;
        for (String c : commands){
            if (command.equals(c)){
                valid_command = true;
            }
        }
            
        if (!valid_command){
            throw new ExpressionException("Unbekannter Befehl: " + command + "\n");
        }

        Commands c = new Commands();
        
        if (command.equals("diff")){
            try{
                c = c.diff(params);
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Fehler! " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        if (command.equals("int")){

        }
        
        if (command.equals("showGraph")){

        }
        
        if (command.equals("solve")){

        }

        return c;

    } 
    
    
    
    
    
    
}