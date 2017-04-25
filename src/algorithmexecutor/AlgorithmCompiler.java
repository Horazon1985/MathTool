package algorithmexecutor;

import abstractexpressions.interfaces.IdentifierValidator;
import static algorithmexecutor.enums.ReservedChars.LINE_SEPARATOR;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.enums.ReservedChars;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.memory.AlgorithmMemory;
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
        boolean beginPassed = false;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
                beginPassed = true;
            } else if (input.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0 && beginPassed) {
                STORED_ALGORITHMS.add(parseAlgorithm(input.substring(0, i + 1)));
                beginPassed = false;
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
        input = removeLeadingWhitespaces(input);

        // Rückgabewert ermitteln.
        IdentifierTypes returnType = null;
        for (IdentifierTypes type : IdentifierTypes.values()) {
            if (input.startsWith(type.toString())) {
                returnType = type;
            }
        }

        // Signator ermitteln.
        if (returnType != null) {
            input = input.substring(returnType.toString().length());
        }
        input = removeLeadingWhitespaces(input);
        String candidateForSignature = input.substring(0, input.indexOf(ReservedChars.BEGIN.getValue()));

        String[] algNameAndParameters = getAlgorithmNameAndParameters(candidateForSignature);
        String algName = algNameAndParameters[0];
        String[] parametersAsStrings = getParameters(algNameAndParameters[1]);

        AlgorithmMemory memory = new AlgorithmMemory();

        Identifier[] parameters = getIdentifiersFromParameterStrings(parametersAsStrings, memory);

        String signature = algName + ReservedChars.OPEN_BRACKET.getValue();
        for (int i = 0; i < parameters.length; i++) {
            signature += parameters[i].getType();
            if (i < parameters.length - 1) {
                signature += ",";
            }
        }
        signature += ReservedChars.CLOSE_BRACKET.getValue();

        // Falls ein Algorithmus mit derselben Signatur bereits vorhanden ist, Fehler werfen.
        if (containsAlgorithmWithSameSignature(signature)) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        // Einzelne Befehlszeilen parsen;
        Algorithm alg = new Algorithm(algName, parameters, returnType);

        indexBeginParameters = input.indexOf(ReservedChars.OPEN_BRACKET.getValue());
        int indexEndParameters = input.indexOf(ReservedChars.CLOSE_BRACKET.getValue());

        // Algorithmusnamen und Parameter inkl. Klammern beseitigen
        input = input.substring(indexEndParameters + 1, input.length());

        if (!input.startsWith(String.valueOf(ReservedChars.BEGIN.getValue())) || !input.endsWith(String.valueOf(ReservedChars.END.getValue()))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        // Öffnende {-Klammer und schließende }-Klammer am Anfang und am Ende beseitigen.
        input = input.substring(1, input.length() - 1);
        
        List<String> lines = splitBySeparator(input);

        for (String line : lines) {
            alg.appendCommand(parseLine(line, memory));
        }
        return alg;
    }

    private static String removeLeadingWhitespaces(String input) {
        while (input.startsWith(" ")) {
            input = input.substring(1);
        }
        return input;
    }

    /**
     * Der Algorithmusname und die Parameter in der Befehlsklammer werden
     * ausgelesen und zurückgegeben.<br>
     * BEISPIEL: input = alg(expression x, expression y). Zurückgegeben wird ein
     * array der Länge zwei: im 0. Eintrag steht der String "alg", im 1. der
     * String "expression x, expression y".
     *
     * @throws AlgorithmCompileException
     */
    private static String[] getAlgorithmNameAndParameters(String input) throws AlgorithmCompileException {

        // Leerzeichen beseitigen
        input = input.replaceAll(" ", "");

        String[] result = new String[2];
        int i = input.indexOf("(");
        if (i == -1) {
            // Um zu verhindern, dass es eine IndexOutOfBoundsException gibt.
            i = 0;
        }
        result[0] = input.substring(0, i);

        //Wenn der Befehl leer ist -> Fehler.
        if (result[0].length() == 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        //Wenn length(result[0]) > l - 2 -> Fehler (der Befehl besitzt NICHT die Form command(...)).
        if (result[0].length() > input.length() - 2) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        //Wenn am Ende nicht ")" steht.
        if (!input.substring(input.length() - 1, input.length()).equals(")")) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        result[1] = input.substring(result[0].length() + 1, input.length() - 1);

        return result;

    }

    /**
     * Input: String input, in der NUR die Parameter (getrennt durch ein Komma)
     * stehen. Beispiel input = "expression x, expression y". Parameter sind
     * dann {expression x, expression y}. Nach einem eingelesenen Komma, welches
     * NICHT von runden Klammern umgeben ist, werden die Parameter getrennt.
     *
     * @throws AlgorithmCompileException
     */
    private static String[] getParameters(String input) throws AlgorithmCompileException {

        //Falls Parameterstring leer ist -> Fertig
        if (input.isEmpty()) {
            return new String[0];
        }

        ArrayList<String> resultParameters = new ArrayList<>();
        int startPositionOfCurrentParameter = 0;

        /*
         Differenz zwischen der Anzahl der öffnenden und der der schließenden
         Klammern (bracketCounter == 0 am Ende -> alles ok).
         */
        int bracketCounter = 0;
        int squareBracketCounter = 0;
        String currentChar;
        //Jetzt werden die einzelnen Parameter ausgelesen
        for (int i = 0; i < input.length(); i++) {

            currentChar = input.substring(i, i + 1);
            if (currentChar.equals("(")) {
                bracketCounter++;
            }
            if (currentChar.equals(")")) {
                bracketCounter--;
            }
            if (currentChar.equals("[")) {
                squareBracketCounter++;
            }
            if (currentChar.equals("]")) {
                squareBracketCounter--;
            }
            if (bracketCounter == 0 && squareBracketCounter == 0 && currentChar.equals(",")) {
                if (input.substring(startPositionOfCurrentParameter, i).isEmpty()) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                }
                resultParameters.add(input.substring(startPositionOfCurrentParameter, i));
                startPositionOfCurrentParameter = i + 1;
            }
            if (i == input.length() - 1) {
                if (startPositionOfCurrentParameter == input.length()) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                }
                resultParameters.add(input.substring(startPositionOfCurrentParameter, input.length()));
            }

        }

        if (bracketCounter != 0 || squareBracketCounter != 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        String[] resultParametersAsArray = new String[resultParameters.size()];
        for (int i = 0; i < resultParameters.size(); i++) {
            resultParametersAsArray[i] = resultParameters.get(i);
        }

        return resultParametersAsArray;

    }

    private static Identifier[] getIdentifiersFromParameterStrings(String[] parameterStrings, AlgorithmMemory memory) throws AlgorithmCompileException {
        Identifier[] resultIdentifiers = new Identifier[parameterStrings.length];
        IdentifierTypes parameterType;
        String parameterName;
        for (int i = 0; i < parameterStrings.length; i++) {

            parameterType = null;
            for (IdentifierTypes type : IdentifierTypes.values()) {
                if (parameterStrings[i].startsWith(type.toString() + " ")) {
                    parameterType = type;
                    break;
                }
            }
            if (parameterType == null) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            parameterName = parameterStrings[i].substring((parameterType.toString() + " ").length());

            // Validierung des Parameternamen.
            if (!VALIDATOR.isValidIdentifier(parameterName)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            // Prüfung auf doppelte Deklaration.
            if (memory.containsIdentifier(parameterName)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            memory.addToMemoryInCompileTime(Identifier.createIdentifier(null, parameterName, parameterType));

        }
        return resultIdentifiers;
    }

    private static boolean containsAlgorithmWithSameSignature(String signature) {
        for (Algorithm alg : STORED_ALGORITHMS) {
            if (alg.getSignature().equals(signature)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> splitBySeparator(String input) {
        List<String> lines = new ArrayList<>();
        int bracketCounter = 0;
        int lastSeparator = -1;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
            } else if (input.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            }
            if (input.charAt(i) == ReservedChars.LINE_SEPARATOR.getValue() && bracketCounter == 0) {
                lines.add(input.substring(lastSeparator + 1, i - 1));
            }
        }
        return lines;
    }

    private static AlgorithmCommand parseLine(String line, AlgorithmMemory memory) throws AlgorithmCompileException {

        return null;
    }

    private static AlgorithmCommand parseAssignValueCommand(String line, AlgorithmMemory memory) throws AlgorithmCompileException {

        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseVoidCommand(String line, AlgorithmMemory memory) throws AlgorithmCompileException {

        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseControllStructure(String line, AlgorithmMemory memory) throws AlgorithmCompileException {

        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseReturnCommand(String line, AlgorithmMemory memory) throws AlgorithmCompileException {
        if (line.startsWith(Keywords.RETURN.getValue() + " ")) {
            if (line.equals(Keywords.RETURN.getValue() + ReservedChars.LINE_SEPARATOR)) {
                return new ReturnCommand(null);
            }
            String identifierCandidate = line.substring((Keywords.RETURN.getValue() + " ").length());
            if (memory.getMemory().get(identifierCandidate) == null) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            return new ReturnCommand(memory.getMemory().get(identifierCandidate));
        }
        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    public static String preprocessAlgorithm(String input) {
        String outputFormatted = input;
        outputFormatted = replaceRepeatedly(outputFormatted, "  ", " ");
        outputFormatted = replaceRepeatedly(outputFormatted, ", ", ",");
        outputFormatted = replaceRepeatedly(outputFormatted, " ,", ",");
        outputFormatted = replaceRepeatedly(outputFormatted, "; ", ";");
        outputFormatted = replaceRepeatedly(outputFormatted, " ;", ";");
        outputFormatted = replaceRepeatedly(outputFormatted, " \\{", "\\{");
        outputFormatted = replaceRepeatedly(outputFormatted, "\\{ ", "\\{");
        outputFormatted = replaceRepeatedly(outputFormatted, " \\}", "\\}");
        outputFormatted = replaceRepeatedly(outputFormatted, "\\} ", "\\}");
        outputFormatted = replaceRepeatedly(outputFormatted, " \\(", "\\(");
        outputFormatted = replaceRepeatedly(outputFormatted, "\\( ", "\\(");
        outputFormatted = replaceRepeatedly(outputFormatted, " \\)", "\\)");
        outputFormatted = replaceRepeatedly(outputFormatted, "\\) ", "\\)");
        return outputFormatted;
    }

    private static String replaceRepeatedly(String input, String toReplace, String replaceBy) {
        String result = input;
        do {
            input = result;
            result = result.replaceAll(toReplace, replaceBy);
        } while (!result.equals(input));
        return result;
    }

}
