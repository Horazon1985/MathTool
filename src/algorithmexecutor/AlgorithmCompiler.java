package algorithmexecutor;

import abstractexpressions.interfaces.IdentifierValidator;
import static algorithmexecutor.enums.ReservedChars.LINE_SEPARATOR;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.model.Algorithm;
import java.util.ArrayList;
import java.util.List;

public abstract class AlgorithmCompiler {

    public final static IdentifierValidator VALIDATOR = new IdentifierValidatorImpl();
    
    public final static List<Algorithm> STORED_ALGORITHMS = new ArrayList<>();

    public static List<Algorithm> parseAlgorithmFile(String input) throws AlgorithmCompileException {
        STORED_ALGORITHMS.clear();

        
        
        return STORED_ALGORITHMS;
    }
    
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

    public static String preprocessAlgorithm(String input) {
        String outputFormatted = input;
        while (outputFormatted.contains("  ")) {
            outputFormatted = outputFormatted.replaceAll("  ", " ");
        }
        while (outputFormatted.contains(", ")) {
            outputFormatted = outputFormatted.replaceAll(", ", ",");
        }
        while (outputFormatted.contains("; ")) {
            outputFormatted = outputFormatted.replaceAll("; ", ";");
        }
        while (outputFormatted.contains(" ;")) {
            outputFormatted = outputFormatted.replaceAll(" ;", ";");
        }
        while (outputFormatted.contains(" {")) {
            outputFormatted = outputFormatted.replaceAll(" \\{", "\\{");
        }
        while (outputFormatted.contains("{ ")) {
            outputFormatted = outputFormatted.replaceAll("\\{ ", "\\{");
        }
        while (outputFormatted.contains(" }")) {
            outputFormatted = outputFormatted.replaceAll(" \\}", "\\}");
        }
        while (outputFormatted.contains("} ")) {
            outputFormatted = outputFormatted.replaceAll("\\} ", "\\}");
        }
        return outputFormatted;
    }

}
