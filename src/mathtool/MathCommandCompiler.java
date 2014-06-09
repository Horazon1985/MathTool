package mathtool;

import expressionbuilder.EvaluationException;
import expressionbuilder.Expression;
import expressionbuilder.ExpressionException;
import expressionbuilder.NumericalMethods;
import expressionbuilder.GraphicMethods2D;
import expressionbuilder.GraphicMethods3D;
import java.awt.Graphics;
import javax.swing.*;
import java.util.HashSet;
import java.util.Iterator;

public class MathCommandCompiler {

    private final String[] commands = {"plot", "solve"};
    
    
    private static Command getCommand(String command, String[] params) throws ExpressionException {

        Command result = new Command();
        
        //PLOT
        if (command.equals("plot")){
            if (params.length < 3){
                throw new ExpressionException("Zu wenig Parameter im Befehl 'plot'");
            }
            if (params.length == 4){
                throw new ExpressionException("Falsche Anzahl von Parametern im Befehl 'plot'");
            }
            if (params.length > 5){
                throw new ExpressionException("Zu viele Parameter im Befehl 'plot'");
            }
            
            if (params.length == 3){

                double x_0, x_1;
                
                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }

                try{
                    x_0 = Double.parseDouble(params[1]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    x_1 = Double.parseDouble(params[2]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                    if (vars.size() > 1){
                        throw new ExpressionException("Der Ausdruck im Befehl 'plot' darf höchstens eine Variable enthalten. Dieser enthält jedoch " 
                                + String.valueOf(vars.size()) + " Variablen.");
                    }
                    result.setName(command);
                    result.setParams(params);
                    result.setLeft(expr);
                    return result;
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }
            
            } else {
        
                double x_0, x_1, y_0, y_1;
                
                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }

                try{
                    x_0 = Double.parseDouble(params[1]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der zweite Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    x_1 = Double.parseDouble(params[2]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try{
                    y_0 = Double.parseDouble(params[3]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der dritte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }
                
                try{
                    y_1 = Double.parseDouble(params[4]);
                } catch (NumberFormatException e){
                    throw new ExpressionException("Der vierte Parameter im Befehl 'plot' muss eine reelle Zahl sein.");
                }

                try{
                    HashSet vars = new HashSet();
                    Expression expr = Expression.build(params[0], vars);
                    if (vars.size() > 2){
                        throw new ExpressionException("Der Ausdruck im Befehl 'plot' darf höchstens zwei Variablen enthalten. Dieser enthält jedoch " 
                                + String.valueOf(vars.size()) + " Variablen.");
                    }
                    result.setName(command);
                    result.setParams(params);
                    result.setLeft(expr);
                    return result;
                } catch (ExpressionException e){
                    throw new ExpressionException("Der erste Parameter im Befehl 'plot' muss ein gültiger Ausdruck sein.");
                }
        
            }
        }
        
        return result;
        
    }
            
    
    //Führt den Befehl aus.
    public void executeCommand(String commandLine, JTextArea area,
            NumericalMethods numericalMethods, GraphicMethods2D graphicMethods2D,
            GraphicMethods3D graphicMethods3D) throws ExpressionException, EvaluationException {
        
        int n = commandLine.length();

        //Leerzeichen beseitigen und alles zu Kleinbuchstaben machen
        char part;
        for(int i = 0; i < n; i++){
            part = commandLine.charAt(i);
            //Falls es ein Leerzeichen ist -> beseitigen
            if ((int)part == 32){
                commandLine = commandLine.substring(0, i)+commandLine.substring(i + 1, n);
                n--;
                i--;
            }
            //Falls es ein Großbuchstabe ist -> zu Kleinbuchstaben machen
            if (((int)part >= 65) && ((int)part <= 90)){
                part = (char)((int)part + 32);  //Macht Großbuchstaben zu Kleinbuchstaben
                commandLine = commandLine.substring(0, i)+part+commandLine.substring(i + 1, n);
            }
        } 
        
        String[] command_and_params = Expression.getOperatorAndArguments(commandLine);
        String command = command_and_params[0];
        String[] params = Expression.getArguments(command_and_params[1]);
        
        Command c = getCommand(command, params);
        
        if ((c.getName().equals("plot")) && (c.getParams().length == 3)){
            executePlot2D(c, graphicMethods2D);
        } else 
        if ((c.getName().equals("plot")) && (c.getParams().length == 5)){
            executePlot3D(c, graphicMethods3D);
        } else {
            throw new ExpressionException("Ungültiger Befehl.");
        }

    } 
    

    /** Die folgenden Prozeduren führen einzelne Befehle aus.
     * executePlot2D zeichnet einen 2D-Graphen,
     * executePlot3D zeichnet einen 3D-Graphen,
     * etc.
     */
    
    /** c Enthält genau 3 Parameter 
     * Parameter: Expression, double, double (als Strings)
     */
    private void executePlot2D(Command c, GraphicMethods2D graphicMethods2D) throws ExpressionException,
            EvaluationException {
        
        HashSet vars = new HashSet();
        Expression expr = Expression.build(c.getParams()[0], vars);
        expr = expr.simplify();
        double x_0 = Double.parseDouble(c.getParams()[1]);
        double x_1 = Double.parseDouble(c.getParams()[2]);

        Iterator iter = vars.iterator();
        String var = (String) iter.next();
        
        boolean critical_line_exists = false;
        double pos_of_critical_line = 0;
        
        graphicMethods2D.expressionToGraph(expr, var, x_0, x_1);
        Graphics g = graphicMethods2D.getGraphics();
        graphicMethods2D.setParameters(var, 0, 0, critical_line_exists, pos_of_critical_line);
        graphicMethods2D.drawGraph();
        
    }

    
    /** c Enthält genau 5 Parameter 
     * Parameter: Expression, double, double, double, double (als Strings)
     */
    private void executePlot3D(Command c, GraphicMethods3D graphicMethods3D) throws ExpressionException,
            EvaluationException {
    
        HashSet vars = new HashSet();
        Expression expr = Expression.build(c.getParams()[0], vars);
        expr = expr.simplify();
        double x_0 = Double.parseDouble(c.getParams()[1]);
        double x_1 = Double.parseDouble(c.getParams()[2]);
        double y_0 = Double.parseDouble(c.getParams()[1]);
        double y_1 = Double.parseDouble(c.getParams()[2]);

        Iterator iter = vars.iterator();
        String var1 = (String) iter.next();
        String var2 = (String) iter.next();
        
//        graphicMethods3D.expressionToGraph(expr, var, x_0, x_1);
//        Graphics g = graphicMethods3D.getGraphics();
//        graphicMethods3D.setParameters(var, 0, 0, critical_line_exists, pos_of_critical_line);
//        graphicMethods3D.drawGraph();

    }

    
    
    
}