package algorithmexecutor;

import algorithmexecutor.exceptions.AlgorithmParseException;
import algorithmexecutor.utils.AlgorithmCommand;
import java.util.ArrayList;
import java.util.List;

public abstract class AlgorithmParser {
    
    public static final String LINE_SEPARATOR = ";";
    
    public static List<AlgorithmCommand> parse(String input) throws AlgorithmParseException {
        String[] lines = input.split(LINE_SEPARATOR);
        List<AlgorithmCommand> commands = new ArrayList<>();
        for (String line : lines) {
            commands.add(parseLine(line));
        }
        return commands;
    }
    
    private static AlgorithmCommand parseLine(String line) throws AlgorithmParseException {
    
        
        
        
        return null;
    }
    
}
