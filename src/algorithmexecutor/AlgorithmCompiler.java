package algorithmexecutor;

import abstractexpressions.interfaces.IdentifierValidator;
import static algorithmexecutor.enums.ReservedChars.LINE_SEPARATOR;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.enums.ReservedChars;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.model.Algorithm;
import java.util.ArrayList;
import java.util.List;

public abstract class AlgorithmCompiler {

    public final static IdentifierValidator VALIDATOR = new IdentifierValidatorImpl();

    public final static List<Algorithm> STORED_ALGORITHMS = new ArrayList<>();

    public static void parseAlgorithmFile(String input) throws AlgorithmCompileException {
        STORED_ALGORITHMS.clear();
        
        if (input.isEmpty()) {
            return;
        }

        int bracketCounter = 0;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
            } else if (input.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0) {
                STORED_ALGORITHMS.add(parseAlgorithm(input.substring(0, i + 1)));
                input = input.substring(i + 1, input.length());
            }
        }

    }

    public static Algorithm parseAlgorithm(String input) throws AlgorithmCompileException {

        int indexBeginParameters = input.indexOf(ReservedChars.OPEN_BRACKET.getValue());
        if (indexBeginParameters < 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
        if (input.indexOf(ReservedChars.CLOSE_BRACKET.getValue()) < 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
        if (indexBeginParameters > input.indexOf(ReservedChars.CLOSE_BRACKET.getValue())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        // Rückgabewert und Signatur des Algorithmus parsen.



        
        int bracketCounter = 0;
        int indexEndParameters = indexBeginParameters + 1;
        for (int i = indexEndParameters; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
            } else if (input.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter < 0) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            if (bracketCounter == 0) {
                STORED_ALGORITHMS.add(parseAlgorithm(input.substring(0, i + 1)));
                input = input.substring(i + 1, input.length());
            }

        }
        
        
        

        String[] lines = input.split(String.valueOf(LINE_SEPARATOR.getValue()));
        List<AlgorithmCommand> commands = new ArrayList<>();
        for (String line : lines) {
            commands.add(parseLine(line));
        }
        return null;
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
