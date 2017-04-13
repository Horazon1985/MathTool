package algorithmexecutor;

import static algorithmexecutor.enums.ReservedChars.LINE_SEPARATOR;
import algorithmexecutor.exceptions.AlgorithmParseException;
import algorithmexecutor.command.AlgorithmCommand;
import java.util.ArrayList;
import java.util.List;

public abstract class AlgorithmParser {
    
    public static List<AlgorithmCommand> parseAlgorithm(String input) throws AlgorithmParseException {
        String[] lines = input.split(String.valueOf(LINE_SEPARATOR.getValue()));
        List<AlgorithmCommand> commands = new ArrayList<>();
        for (String line : lines) {
            commands.add(parseLine(line));
        }
        return commands;
    }
    
    private static AlgorithmCommand parseLine(String line) throws AlgorithmParseException {
    
        
        
        
        return null;
    }
    
    /**
     * Prüft, ob der Name identifier ein gültiger Bezeichner ist. Gültig
     * bedeutet, dass er nur aus Groß- und Kleinbuchstaben, Ziffern 0 bis 9 und
     * dem Unterstrich '_' bestehen darf.
     */
    private static boolean isValidIdentifierName(String identifier) {
        int asciiValue;
        for (int i = 0; i < identifier.length(); i++) {
            asciiValue = (int) identifier.charAt(i);
            if (!(asciiValue >= 97 && asciiValue <= 122 
                    || asciiValue >= 65 && asciiValue <= 90 
                    || asciiValue >= 48 && asciiValue <= 57
                    || asciiValue == 95)) {
                return false;
            }
        }
        return true;
    }
    
    
    
}
