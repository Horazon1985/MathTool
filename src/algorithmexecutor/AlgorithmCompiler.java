package algorithmexecutor;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.IdentifierValidator;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.enums.Operators;
import algorithmexecutor.enums.ReservedChars;
import algorithmexecutor.exceptions.BlockCompileException;
import algorithmexecutor.exceptions.BooleanExpressionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import exceptions.ExpressionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AlgorithmCompiler {

    public final static IdentifierValidator VALIDATOR = new IdentifierValidatorImpl();

    public final static List<Algorithm> STORED_ALGORITHMS = new ArrayList<>();

    public static void parseAlgorithmFile(String input) throws AlgorithmCompileException {
        STORED_ALGORITHMS.clear();

        if (input.isEmpty()) {
            return;
        }

        // Formatierung.
        input = preprocessAlgorithm(input);

        int bracketCounter = 0;
        boolean beginPassed = false;
        int lastEndOfAlgorithm = -1;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
                beginPassed = true;
            } else if (input.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0 && beginPassed) {
                STORED_ALGORITHMS.add(parseAlgorithm(input.substring(lastEndOfAlgorithm + 1, i + 1)));
                beginPassed = false;
                lastEndOfAlgorithm = i;
            }
        }

    }

    public static String preprocessAlgorithm(String input) {
        String outputFormatted = input;
        outputFormatted = replaceAllRepeatedly(outputFormatted, " ", "  ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, ",", ", ", " ,");
        outputFormatted = replaceAllRepeatedly(outputFormatted, ";", "; ", " ;");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\{", " \\{", "\\{ ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\}", " \\}", "\\} ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\(", " \\(", "\\( ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\)", " \\)", "\\) ");
        return outputFormatted;
    }

    private static String replaceAllRepeatedly(String input, String replaceBy, String... toReplace) {
        String result = input;
        for (String s : toReplace) {
            result = replaceRepeatedly(result, s, replaceBy);
        }
        return result;
    }

    private static String replaceRepeatedly(String input, String toReplace, String replaceBy) {
        String result = input;
        do {
            input = result;
            result = result.replaceAll(toReplace, replaceBy);
        } while (!result.equals(input));
        return result;
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
                break;
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

        // Algorithmusparameter zum Variablenpool hinzufügen.
        addParametersToMemoryInCompileTime(parameters, memory);

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
            alg.appendCommand(parseLine(line, memory, alg));
        }

        // Plausibilitätschecks.
        checkAlgorithmForPlausibility(alg);

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
            resultIdentifiers[i] = Identifier.createIdentifier(parameterName, parameterType);

        }
        return resultIdentifiers;
    }

    private static void addParametersToMemoryInCompileTime(Identifier[] parameters, AlgorithmMemory memory) throws AlgorithmCompileException {
        for (Identifier parameter : parameters) {
            memory.addToMemoryInCompileTime(parameter);
        }
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
                lines.add(input.substring(lastSeparator + 1, i));
                lastSeparator = i;
            }
        }
        return lines;
    }

    private static AlgorithmCommand parseLine(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        try {
            return parseAssignValueCommand(line, memory, alg);
        } catch (AlgorithmCompileException e) {
        }
        try {
            return parseVoidCommand(line, memory, alg);
        } catch (AlgorithmCompileException e) {
        }
        try {
            return parseControllStructure(line, memory, alg);
        } catch (AlgorithmCompileException e) {
        }
        try {
            return parseReturnCommand(line, memory);
        } catch (AlgorithmCompileException e) {
        }
        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseAssignValueCommand(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        if (!line.contains(Operators.DEFINE.getValue())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
        String[] assignment = line.split(Operators.DEFINE.getValue());

        // Linke Seite behandeln.
        IdentifierTypes type = null;
        if (assignment[0].startsWith(IdentifierTypes.EXPRESSION.toString() + " ")) {
            type = IdentifierTypes.EXPRESSION;
        } else if (assignment[0].startsWith(IdentifierTypes.BOOLEAN_EXPRESSION.toString() + " ")) {
            type = IdentifierTypes.BOOLEAN_EXPRESSION;
        } else if (assignment[0].startsWith(IdentifierTypes.MATRIX_EXPRESSION.toString() + " ")) {
            type = IdentifierTypes.MATRIX_EXPRESSION;
        }

        String identifierName;
        if (type != null) {
            // Prüfung, ob dieser Identifier bereits existiert.
            identifierName = assignment[0].substring((type.toString() + " ").length(), assignment[0].length());
            if (memory.containsIdentifier(identifierName)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
        } else {
            identifierName = assignment[0];
            // Prüfung, ob dieser Identifier bereits existiert.
            if (!memory.containsIdentifier(identifierName)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            type = memory.getMemory().get(identifierName).getType();
        }

        // Rechte Seite behandeln.
        if (type == IdentifierTypes.EXPRESSION) {

            // Fall: Arithmetischer / Analytischer Ausdruck.
            try {
                Expression expr = Expression.build(assignment[1], VALIDATOR);
                Set<String> vars = expr.getContainedIndeterminates();
                if (!areIdentifiersAllDefined(vars, memory)) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                }
                if (!areIdentifiersOfCorrectType(type, vars, memory)) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                }
                Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
                memory.getMemory().put(identifierName, identifier);
                return new AssignValueCommand(identifier, expr);
            } catch (ExpressionException e) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }

        } else if (type == IdentifierTypes.BOOLEAN_EXPRESSION) {

            // Fall: boolscher Ausdruck.
            try {
                BooleanExpression boolExpr = BooleanExpression.build(assignment[1], VALIDATOR, alg);
                Set<String> vars = boolExpr.getContainedIndeterminates();
                if (!areIdentifiersAllDefined(vars, memory)) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                }
                if (!areIdentifiersOfCorrectType(type, vars, memory)) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                }
                Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
                memory.getMemory().put(identifierName, identifier);
                return new AssignValueCommand(identifier, boolExpr);
            } catch (BooleanExpressionException e) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }

        }

        // Fall: Matrizenausdruck.
        try {
            MatrixExpression matExpr = MatrixExpression.build(assignment[1], VALIDATOR, VALIDATOR);
            Set<String> vars = matExpr.getContainedIndeterminates();
            if (!areIdentifiersAllDefined(vars, memory)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            if (!areIdentifiersOfCorrectType(IdentifierTypes.EXPRESSION, vars, memory)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
            memory.getMemory().put(identifierName, identifier);
            return new AssignValueCommand(identifier, matExpr);
        } catch (ExpressionException e) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

    }

    private static boolean areIdentifiersAllDefined(Set<String> vars, AlgorithmMemory memory) {
        for (String var : vars) {
            if (!memory.getMemory().containsKey(var) || memory.getMemory().get(var).getType() != IdentifierTypes.EXPRESSION) {
                return false;
            }
        }
        return true;
    }

    private static boolean areIdentifiersOfCorrectType(IdentifierTypes type, Set<String> vars, AlgorithmMemory memory) {
        for (String var : vars) {
            if (memory.getMemory().get(var).getType() != type) {
                return false;
            }
        }
        return true;
    }

    private static AlgorithmCommand parseVoidCommand(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {

        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseControllStructure(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        // If-Else-Block
        try {
            return parseIfElseControlStructure(line, memory, alg);
        } catch (BooleanExpressionException e) {
            // Es ist zwar eine If-Else-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        } catch (BlockCompileException e) {
            /* 
            Es ist zwar eine If-Else-Struktur mit korrekter Bedingung, aber der 
            innere Block kann nicht kompiliert werden.
             */
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        } catch (AlgorithmCompileException e) {
        }
        // For-Block
        // While-Block
        // Do-While-Block
        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseIfElseControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException {

        if (!line.startsWith(Keywords.IF.getValue() + ReservedChars.BEGIN)) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        int endOfBooleanCondition = line.indexOf(ReservedChars.END.getValue());
        if (endOfBooleanCondition < 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        String booleanConditionString = line.substring((Keywords.IF.getValue() + ReservedChars.BEGIN).length(), endOfBooleanCondition);
        BooleanExpression condition = BooleanExpression.build(booleanConditionString, VALIDATOR, alg);

        // Prüfung, ob line mit "if(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(String.valueOf(ReservedChars.BEGIN.getValue()))
                || !line.contains(String.valueOf(ReservedChars.END.getValue()))
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }

        // Block im If-Teil kompilieren.
        int bracketCounter = 0;
        int beginBlockPosition = line.indexOf(ReservedChars.BEGIN.getValue()) + 1;
        int endBlockPosition = -1;
        for (int i = line.indexOf(ReservedChars.BEGIN.getValue()); i < line.length(); i++) {
            if (line.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
            } else if (line.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0) {
                endBlockPosition = i;
                break;
            }
        }
        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
        List<AlgorithmCommand> commandsIfPart = parseBlock(line.substring(beginBlockPosition, endBlockPosition), memory, alg);
        IfElseControlStructure ifElseControlStructure = new IfElseControlStructure(condition, commandsIfPart);

        // Block im Else-Teil kompilieren, falls vorhanden.
        if (endBlockPosition == line.length() - 1) {
            // Kein Else-Teil vorhanden.
            return ifElseControlStructure;
        }
        
        String restLine = line.substring(endBlockPosition + 1);
        if (!restLine.startsWith(Keywords.ELSE.getValue() + ReservedChars.BEGIN.getValue())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
        
        bracketCounter = 0;
        beginBlockPosition = restLine.indexOf(ReservedChars.BEGIN.getValue()) + 1;
        endBlockPosition = -1;
        for (int i = restLine.indexOf(ReservedChars.BEGIN.getValue()); i < restLine.length(); i++) {
            if (restLine.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
            } else if (restLine.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0) {
                endBlockPosition = i;
                break;
            }
        }
        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
        if (endBlockPosition != restLine.length() - 1) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
        
        List<AlgorithmCommand> commandsElsePart = parseBlock(restLine.substring(beginBlockPosition, endBlockPosition), memory, alg);
        ifElseControlStructure.setCommandsElsePart(commandsElsePart);

        return ifElseControlStructure;
    }

    private static AlgorithmCommand parseReturnCommand(String line, AlgorithmMemory memory) throws AlgorithmCompileException {
        if (line.startsWith(Keywords.RETURN.getValue() + " ")) {
            if (line.equals(Keywords.RETURN.getValue() + ReservedChars.LINE_SEPARATOR)) {
                return new ReturnCommand(null);
            }
            String returnValueCandidate = line.substring((Keywords.RETURN.getValue() + " ").length());
            if (memory.getMemory().get(returnValueCandidate) == null) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            return new ReturnCommand(memory.getMemory().get(returnValueCandidate));
        }
        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    private static List<AlgorithmCommand> parseBlock(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        String[] lines = input.split(String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()));
        List<AlgorithmCommand> commands = new ArrayList<>();
        for (String line : lines) {
            commands.add(parseLine(line, memory, alg));
        }
        return commands;
    }

    private static void checkAlgorithmForPlausibility(Algorithm alg) throws AlgorithmCompileException {
        // 1. Prüfung, ob es bei Void-Algorithmen keine zurückgegebenen Objekte gibt.
        checkIfVoidAlgorithmContainsOnlyAtMostSimpleReturns(alg);
        // 2. Prüfung, ob es bei Algorithmen mit Rückgabewerten immer Rückgaben mit korrektem Typ gibt.
        checkIfNonVoidAlgorithmContainsAlwaysReturnsWithCorrectReturnType(alg);
        // 3. Prüfung, ob es bei (beliebigen) Algorithmen keinen Code hinter einem Return gibt.
        checkIfAlgorithmContainsNoDeadCode(alg);
    }

    private static void checkIfVoidAlgorithmContainsOnlyAtMostSimpleReturns(Algorithm alg) throws AlgorithmCompileException {

    }

    private static void checkIfNonVoidAlgorithmContainsAlwaysReturnsWithCorrectReturnType(Algorithm alg) throws AlgorithmCompileException {

    }

    private static void checkIfAlgorithmContainsNoDeadCode(Algorithm alg) throws AlgorithmCompileException {

    }

}
