package algorithmexecutor;

import abstractexpressions.interfaces.IdentifierValidator;
import static algorithmexecutor.enums.ReservedChars.LINE_SEPARATOR;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.command.AlgorithmCommand;
import java.util.ArrayList;
import java.util.List;

public abstract class AlgorithmCompiler {

    public final static IdentifierValidator VALIDATOR = new IdentifierValidatorImpl();
    
    public static List<AlgorithmCommand> parseAlgorithm(String input) throws AlgorithmCompileException {
        String[] lines = input.split(String.valueOf(LINE_SEPARATOR.getValue()));
        List<AlgorithmCommand> commands = new ArrayList<>();
        for (String line : lines) {
            commands.add(parseLine(line));
        }
        return commands;
    }
    
    private static AlgorithmCommand parseLine(String line) throws AlgorithmCompileException {
    
        
        
        
        return null;
    }
    
}
