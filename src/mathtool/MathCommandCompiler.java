package mathtool;

import expressionbuilder.ExpressionException;
import javax.swing.*;

public class MathCommandCompiler {

    //String commandLine. Ausgelesen wird der Befehl für die jeweilige math. Operation UND die Parameter in der Befehlsklammer
    public String[] getCommandAndArguments(String commandLine) throws WrongCommandFormException {
        
        int l = commandLine.length();
        
        //Leerzeichen beseitigen
        char part;
        for(int i = 0; i < l; i++){
            part = commandLine.charAt(i);
            //Falls es ein Leerzeichen ist -> beseitigen
            if ((int)part == 32){
                commandLine = commandLine.substring(0, i)+commandLine.substring(i + 1, l);
                l--;
                i--;
            }
        } 

        String[] result = new String[2];
        int i = 0;
        result[0] = "";

        while (i < l){
            if (!commandLine.substring(i, i + 1).equals("(")){
                result[0] = result[0]+commandLine.substring(i, i + 1);
                i++;
            } else {
                break;
            }
        }
        
        //Wenn der Befehl leer ist -> Fehler.
        if (result[0].length() == 0){
            throw new WrongCommandFormException("Der Befehl ist leer.");
        }
        
        //Wenn length(result[0]) > l - 2 -> Fehler (der Befehl besitzt NICHT die Form command(...)).
        if (result[0].length() > l - 2){
            throw new WrongCommandFormException("Der Befehl besitzt die falsche Form. Er muss von der Form command(...) sein.");
        }
        
        //Wenn am Ende nicht ")" steht.
        if (!commandLine.substring(l - 1, l).equals(")")){
            throw new WrongCommandFormException("Es fehlt am Ende eine schließende Klammer.");
        }
        
        result[1] = commandLine.substring(result[0].length() + 1, l - 1);
        
        return result;
    
    }
    
    
    //Input: String commandline. Nach einem eingelesenen Komma werden die Parameter getrennt.
    public String[] getArguments(String commandLine) throws NoParameterException, CompileException {

        //n ist später die Anzahl der Parameter im String commandLine
        int n = 1;
        int l = commandLine.length();
        //Differenz zwischen der Anzahl der öffnenden und der der schließenden Klammern (brackets_count == 0 am Ende -> alles ok).
        int brackets_count = 0;
        //Anzahl der Parameter im String commandLine
        int param_count = 0;
        String commandLine_part;
        
        //Leerzeichen beseitigen
        char part;
        for(int i = 0; i < l; i++){
            part = commandLine.charAt(i);
            //Falls es ein Leerzeichen ist -> beseitigen
            if ((int)part == 32){
                commandLine = commandLine.substring(0, i)+commandLine.substring(i + 1, l);
                l--;
                i--;
            }
        } 

        //Falls Parameterstring leer ist -> Fehlermeldung auswerfen.
        if (commandLine.equals("")){
            throw new NoParameterException("Keine Parameter.");
        }

        for(int i = 0; i < l; i++){
            commandLine_part = commandLine.substring(i, i + 1);
            
            if ((commandLine_part.equals(")")) && (brackets_count == 0)){
                throw new CompileException("Falsche Klammerung.");
            }
            
            if (commandLine_part.equals("(")) brackets_count++;
            if (commandLine_part.equals(")")) brackets_count--;

            //Aufteilungspunkt finden; zunächst wird nach -, +, *, /, ^ gesucht 
            //breakpoint gibt den Index in formula an, wo die Formel aufgespalten werden soll
            if ((brackets_count == 0) && (commandLine_part.equals(","))){
                n++;
            } 
        }
        
        if (brackets_count > 0){
            throw new CompileException("Falsche Klammerung.");
        }

        //Falsche Eingaben wurden bis hierhin bereits durch Fehlermeldungen aussortiert.
        String[] result = new String[n];
        int start_pos = 0;

        //Jetzt werden die einzelnen Parameter ausgelesen
        brackets_count = 0; //(eigentlich unnötig, da brackets_count == 0 ist, wenn das Programm noch keinen Fehler geworfen hat).
        for(int i = 0; i < l; i++){

            commandLine_part = commandLine.substring(i, i + 1);
            if (commandLine_part.equals("(")) brackets_count++;
            if (commandLine_part.equals(")")) brackets_count--;
            if ((brackets_count==0) && (commandLine_part.equals(","))){
                if (commandLine.substring(start_pos, i).equals("")){
                    throw new NoParameterException("Leerer Parameter.");
                }
                result[param_count] = commandLine.substring(start_pos, i);
                start_pos = i + 1;
                param_count++;
            }
            if (i == l - 1){
                if (start_pos == l){
                    throw new NoParameterException("Leerer Parameter.");
                }
                result[n - 1] = commandLine.substring(start_pos, l);
            }
        
        }
        
        return result;

    }
    

    //Führt den Befehl aus.
    public void executeCommand(String commandLine, JTextArea area) {
        
        
    
    
    } 
    
    
    
    
    
    
}