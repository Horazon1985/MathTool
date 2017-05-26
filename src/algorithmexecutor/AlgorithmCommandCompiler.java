package algorithmexecutor;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import static algorithmexecutor.AlgorithmCompiler.VALIDATOR;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.command.DeclareIdentifierCommand;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.command.WhileControlStructure;
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.enums.ComparingOperators;
import algorithmexecutor.enums.IdentifierType;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.enums.Operators;
import algorithmexecutor.enums.ReservedChars;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.BlockCompileException;
import algorithmexecutor.exceptions.BooleanExpressionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.exceptions.DeclareIdentifierException;
import algorithmexecutor.exceptions.NotDesiredCommandException;
import algorithmexecutor.exceptions.ParseAssignValueException;
import algorithmexecutor.exceptions.ParseControlStructureException;
import algorithmexecutor.exceptions.ParseReturnException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.model.Signature;
import exceptions.ExpressionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AlgorithmCommandCompiler {

    private static final String GEN_VAR = "GEN_VAR_";

    /**
     * Gibt einen zufällig generierten technischen Namen für eine technische
     * (temporäre) Variable zurück.
     *
     * @return
     */
    private static String generateTechnicalVarName() {
        return GEN_VAR + UUID.randomUUID().toString().replaceAll("-", "_");
    }

    /**
     * Gibt eine Liste von Befehlen zurück, welche aus der gegebenen Zeile
     * generíert werden. Aus einer Zeile können auch mehrere Befehle generiert
     * werden.<br>
     * <b>BEISPIEL:</b> Der Algorithmus "computeggt(expression a, expression b)"
     * existiert bereits. Dann werden aus "expression x = computeggt(15,25) *
     * exp(4)" etwa die beiden folgenden Befehle generiert: <br>
     * expression GEN_VAR_b5e67f88_2006_47a4_a94f_ce25854fdbdb =
     * computeggt(15,25)<br>
     * expression x = GEN_VAR_b5e67f88_2006_47a4_a94f_ce25854fdbdb * exp(4)<br>
     *
     * @throws AlgorithmCompileException
     */
    private static List<AlgorithmCommand> parseLine(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        try {
            return parseAssignValueCommand(line, memory, alg);
        } catch (ParseAssignValueException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseDeclareIdentifierCommand(line, memory, alg);
        } catch (ParseAssignValueException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseVoidCommand(line, memory, alg);
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseControllStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseReturnCommand(line, memory);
        } catch (ParseReturnException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        throw new AlgorithmCompileException(CompileExceptionTexts.AC_COMMAND_COUND_NOT_BE_PARSED, line);
    }

    private static List<AlgorithmCommand> parseDeclareIdentifierCommand(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {
        // Typ ermitteln. Dieser ist != null, wenn es sich definitiv um eine Identifierdeklaration handelt.
        IdentifierType type = getTypeIfIsValidDeclareIdentifierCommand(line);
        if (type == null) {
            throw new NotDesiredCommandException();
        }

        String identifierName = line.substring(type.toString().length() + 1);
        if (!VALIDATOR.isValidIdentifier(identifierName)) {
            throw new DeclareIdentifierException(CompileExceptionTexts.AC_ILLEGAL_CHARACTER);
        }
        // Prüfung, ob dieser Identifier bereits existiert.
        if (memory.containsIdentifier(identifierName)) {
            throw new ParseAssignValueException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED);
        }
        Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
        memory.getMemory().put(identifierName, identifier);
        return Collections.singletonList((AlgorithmCommand) new DeclareIdentifierCommand(identifier));
    }

    private static IdentifierType getTypeIfIsValidDeclareIdentifierCommand(String line) {
        for (IdentifierType type : IdentifierType.values()) {
            if (line.startsWith(type.toString() + " ")) {
                return type;
            }
        }
        return null;
    }

    private static List<AlgorithmCommand> parseAssignValueCommand(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {
        // Erste Prüfung, ob es sich definitiv nicht um eine Zuweisung handelt.
        if (!isAssignValueCommandIfValid(line)) {
            throw new NotDesiredCommandException();
        }
        String[] assignment = line.split(Operators.DEFINE.getValue());

        String leftSide = assignment[0];
        String rightSide = assignment[1];
        // Linke Seite behandeln.
        IdentifierType type = null;
        if (leftSide.startsWith(IdentifierType.EXPRESSION.toString() + " ")) {
            type = IdentifierType.EXPRESSION;
        } else if (leftSide.startsWith(IdentifierType.BOOLEAN_EXPRESSION.toString() + " ")) {
            type = IdentifierType.BOOLEAN_EXPRESSION;
        } else if (leftSide.startsWith(IdentifierType.MATRIX_EXPRESSION.toString() + " ")) {
            type = IdentifierType.MATRIX_EXPRESSION;
        }

        String identifierName;
        if (type != null) {

            identifierName = leftSide.substring((type.toString() + " ").length(), assignment[0].length());
            // Prüfung, ob dieser Identifier gültigen Namen besitzt.
            if (!VALIDATOR.isValidIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_ILLEGAL_CHARACTER);
            }
            // Prüfung, ob dieser Identifier bereits existiert.
            if (memory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED);
            }
        } else {
            identifierName = leftSide;
            // Prüfung, ob dieser Identifier bereits existiert.
            if (!memory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
            }
            type = memory.getMemory().get(identifierName).getType();
        }

        // Rechte Seite behandeln.
        Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
        if (type == IdentifierType.EXPRESSION) {

            // Fall: Arithmetischer / Analytischer Ausdruck.
            AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, memory, alg);
            List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
            try {
                Expression expr = Expression.build(rightSide, VALIDATOR);
                Set<String> vars = expr.getContainedVars();
                checkIfAllIdentifiersAreDefined(vars, memory);
                areIdentifiersOfCorrectType(type, vars, memory);
                memory.getMemory().put(identifierName, identifier);
                commands.add(new AssignValueCommand(identifier, expr));
                return commands;
            } catch (ExpressionException e) {
                try {
                    // Kompatibilitätscheck
                    Signature calledAlgSignature = getSignatureFromAlgorithmCall(rightSide, memory, type);
                    // Parameter auslesen
                    Identifier[] parameterIdentifiers = getParameterFromAlgorithmCall(rightSide, memory);
                    memory.getMemory().put(identifierName, identifier);
                    commands.add(new AssignValueCommand(identifier, calledAlgSignature, parameterIdentifiers));
                    return commands;
                } catch (ParseAssignValueException ex) {
                    throw ex;
                }
            }

        } else if (type == IdentifierType.BOOLEAN_EXPRESSION) {

            // Fall: boolscher Ausdruck.
            try {
                Map<String, IdentifierType> valuesMap = CompilerUtils.extractTypesOfMemory(memory);
                BooleanExpression boolExpr = BooleanExpression.build(rightSide, VALIDATOR, valuesMap);
                Set<String> vars = boolExpr.getContainedVars();
                checkIfAllIdentifiersAreDefined(boolExpr.getContainedVars(), memory);
                areIdentifiersOfCorrectType(type, vars, memory);
                memory.getMemory().put(identifierName, identifier);
                return Collections.singletonList((AlgorithmCommand) new AssignValueCommand(identifier, boolExpr));
            } catch (BooleanExpressionException e) {
                try {
                    // Kompatibilitätscheck
                    Signature calledAlgSignature = getSignatureFromAlgorithmCall(rightSide, memory, type);
                    // Parameter auslesen;
                    Identifier[] parameterIdentifiers = getParameterFromAlgorithmCall(rightSide, memory);
                    memory.getMemory().put(identifierName, identifier);
                    return Collections.singletonList((AlgorithmCommand) new AssignValueCommand(identifier, calledAlgSignature, parameterIdentifiers));
                } catch (ParseAssignValueException ex) {
                    throw ex;
                }
            }

        }

        // Fall: Matrizenausdruck.
        try {
            MatrixExpression matExpr = MatrixExpression.build(rightSide, VALIDATOR, VALIDATOR);
            checkIfAllIdentifiersAreDefined(matExpr.getContainedVars(), memory);
            areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, matExpr.getContainedExpressionVars(), memory);
            areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, matExpr.getContainedMatrixVars(), memory);
            memory.getMemory().put(identifierName, identifier);
            return Collections.singletonList((AlgorithmCommand) new AssignValueCommand(identifier, matExpr));
        } catch (ExpressionException e) {
            try {
                // Kompatibilitätscheck
                Signature calledAlgSignature = getSignatureFromAlgorithmCall(rightSide, memory, type);
                // Parameter auslesen;
                Identifier[] parameterIdentifiers = getParameterFromAlgorithmCall(rightSide, memory);
                memory.getMemory().put(identifierName, identifier);
                return Collections.singletonList((AlgorithmCommand) new AssignValueCommand(identifier, calledAlgSignature, parameterIdentifiers));
            } catch (ParseAssignValueException ex) {
                throw ex;
            }
        }

    }

    private static boolean isAssignValueCommandIfValid(String line) {
        /*
        Prüfung, ob line einen Vergleichsoperator enthält, welcher "=" enthält.
        Im positiven Fall kann es keine Zuweisung mehr sein, sondern höchstens eine
        Kontrollstruktur.
         */
        for (ComparingOperators op : ComparingOperators.getOperatorsContainingEqualsSign()) {
            if (line.contains(op.getValue())) {
                return false;
            }
        }
        int bracketCounter = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
            } else if (line.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0 && String.valueOf(line.charAt(i)).equals(Operators.DEFINE.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static void checkIfAllIdentifiersAreDefined(Set<String> vars, AlgorithmMemory memory) throws ParseAssignValueException {
        for (String var : vars) {
            if (!memory.getMemory().containsKey(var) || memory.getMemory().get(var).getType() != IdentifierType.EXPRESSION) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
            }
        }
    }

    private static void areIdentifiersOfCorrectType(IdentifierType type, Set<String> vars, AlgorithmMemory memory) throws ParseAssignValueException {
        for (String var : vars) {
            if (memory.getMemory().get(var).getType() != type) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES, memory.getMemory().get(var).getType(), type);
            }
        }
    }

    private static Identifier[] getParameterFromAlgorithmCall(String input, AlgorithmMemory memory) throws ParseAssignValueException {
        try {
            String[] algNameAndParams = CompilerUtils.getAlgorithmNameAndParameters(input);
            String[] params = CompilerUtils.getParameters(algNameAndParams[1]);
            Identifier[] identifiers = new Identifier[params.length];
            for (int i = 0; i < params.length; i++) {
                if (memory.getMemory().get(params[i]) == null) {
                    throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
                }
                identifiers[i] = memory.getMemory().get(params[i]);
            }
            return identifiers;
        } catch (AlgorithmCompileException e) {
            throw new ParseAssignValueException(e);
        }
    }

    private static Signature getSignatureFromAlgorithmCall(String input, AlgorithmMemory memory, IdentifierType returnType) throws ParseAssignValueException {
        try {
            String[] algNameAndParams = CompilerUtils.getAlgorithmNameAndParameters(input);
            String algName = algNameAndParams[0];
            String[] params = CompilerUtils.getParameters(algNameAndParams[1]);

            // Prüfung, ob ein Algorithmus mit diesem Namen bekannt ist.
            Signature algorithmCandidate = null;
            boolean candidateFound;
            for (Signature signature : AlgorithmCompiler.STORED_ALGORITHM_SIGNATURES) {
                if (signature.getName().equals(algName) && signature.getParameterTypes().length == params.length) {
                    candidateFound = true;
                    for (int i = 0; i < params.length; i++) {
                        if (memory.getMemory().get(params[i]) == null
                                || !memory.getMemory().get(params[i]).getType().equals(signature.getParameterTypes()[i])) {
                            candidateFound = false;
                        }
                    }
                    if (candidateFound) {
                        // Alle Parameter sind damit automatisch gültige Bezeichner sind.
                        algorithmCandidate = signature;
                        break;
                    }
                }
            }
            if (algorithmCandidate == null) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
            }

            // Prüfung, ob Rückgabewert korrekt ist.
            if (!algorithmCandidate.getReturnType().equals(returnType)) {
                // TODO: Fehlermeldung korrigieren.
                throw new ParseAssignValueException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }
            return algorithmCandidate;
        } catch (AlgorithmCompileException e) {
            throw new ParseAssignValueException(e);
        }
    }

    private static List<AlgorithmCommand> parseVoidCommand(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {

        throw new NotDesiredCommandException();
    }

    private static List<AlgorithmCommand> parseControllStructure(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {
        // If-Else-Block
        try {
            return parseIfElseControlStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            throw e;
        } catch (BooleanExpressionException | BlockCompileException e) {
            // Es ist zwar eine If-Else-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
            /*
            Es ist zwar eine If-Else-Struktur mit korrekter Bedingung, aber der
            innere Block kann nicht kompiliert werden.
             */
        }
        // For-Block
        // While-Block
        try {
            return parseWhileControlStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            throw e;
        } catch (BooleanExpressionException | BlockCompileException e) {
            // Es ist zwar eine While-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
            /*
            Es ist zwar eine While-Struktur mit korrekter Bedingung, aber der
            innere Block kann nicht kompiliert werden.
             */
        }
        // Do-While-Block
        throw new NotDesiredCommandException();
    }

    private static List<AlgorithmCommand> parseIfElseControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keywords.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new NotDesiredCommandException();
        }

        int endOfBooleanCondition = line.indexOf(ReservedChars.CLOSE_BRACKET.getValue());
        if (endOfBooleanCondition < 0) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_MISSING, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String booleanConditionString = line.substring((Keywords.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);
        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        BooleanExpression condition = BooleanExpression.build(booleanConditionString, VALIDATOR, typesMap);

        // Prüfung, ob line mit "if(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(String.valueOf(ReservedChars.BEGIN.getValue()))
                || !line.contains(String.valueOf(ReservedChars.END.getValue()))
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
                    ReservedChars.BEGIN.getValue(), ReservedChars.END.getValue());
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
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }

        List<AlgorithmCommand> commandsIfPart = parseConnectedBlock(line.substring(beginBlockPosition, endBlockPosition), memory, alg);
        IfElseControlStructure ifElseControlStructure = new IfElseControlStructure(condition, commandsIfPart);

        // Block im Else-Teil kompilieren, falls vorhanden.
        if (endBlockPosition == line.length() - 1) {
            // Kein Else-Teil vorhanden.
            return Collections.singletonList((AlgorithmCommand) ifElseControlStructure);
        }

        String restLine = line.substring(endBlockPosition + 1);
        if (!restLine.startsWith(Keywords.ELSE.getValue() + ReservedChars.BEGIN.getValue())) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_KEYWORD_EXPECTED, Keywords.ELSE.getValue());
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
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }
        if (endBlockPosition != restLine.length() - 1) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
        }

        List<AlgorithmCommand> commandsElsePart = parseConnectedBlock(restLine.substring(beginBlockPosition, endBlockPosition), memory, alg);
        ifElseControlStructure.setCommandsElsePart(commandsElsePart);

        return Collections.singletonList((AlgorithmCommand) ifElseControlStructure);
    }

    private static List<AlgorithmCommand> parseWhileControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keywords.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new NotDesiredCommandException();
        }

        int endOfBooleanCondition = line.indexOf(ReservedChars.CLOSE_BRACKET.getValue());
        if (endOfBooleanCondition < 0) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_MISSING, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String booleanConditionString = line.substring((Keywords.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);
        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        BooleanExpression condition = BooleanExpression.build(booleanConditionString, VALIDATOR, typesMap);

        // Prüfung, ob line mit "while(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(String.valueOf(ReservedChars.BEGIN.getValue()))
                || !line.contains(String.valueOf(ReservedChars.END.getValue()))
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
                    ReservedChars.BEGIN.getValue(), ReservedChars.END.getValue());
        }

        // Block im While-Teil kompilieren.
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
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }
        List<AlgorithmCommand> commandsWhilePart = parseConnectedBlock(line.substring(beginBlockPosition, endBlockPosition), memory, alg);
        WhileControlStructure whileControlStructure = new WhileControlStructure(condition, commandsWhilePart);

        // '}' muss als letztes Zeichen stehen, sonst ist die Struktur nicht korrekt.
        if (endBlockPosition == line.length() - 1) {
            // Kein Else-Teil vorhanden.
            return Collections.singletonList((AlgorithmCommand) whileControlStructure);
        }

        throw new ParseControlStructureException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
    }

    private static List<AlgorithmCommand> parseReturnCommand(String line, AlgorithmMemory memory) throws AlgorithmCompileException, NotDesiredCommandException {
        if (line.startsWith(Keywords.RETURN.getValue() + " ")) {
            if (line.equals(Keywords.RETURN.getValue() + ReservedChars.LINE_SEPARATOR)) {
                return Collections.singletonList((AlgorithmCommand) new ReturnCommand(null));
            }
            String returnValueCandidate = line.substring((Keywords.RETURN.getValue() + " ").length());
            if (memory.getMemory().get(returnValueCandidate) == null) {
                throw new ParseReturnException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, returnValueCandidate);
            }
            return Collections.singletonList((AlgorithmCommand) new ReturnCommand(memory.getMemory().get(returnValueCandidate)));
        }
        throw new NotDesiredCommandException();
    }

    public static List<AlgorithmCommand> parseConnectedBlock(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        if (!input.isEmpty() && !input.endsWith(String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_MISSING_LINE_SEPARATOR, ReservedChars.LINE_SEPARATOR.getValue());
        }

        List<String> linesAsList = new ArrayList<>();
        // Block im While-Teil kompilieren.
        int bracketCounter = 0;
        int squareBracketCounter = 0;
        int beginBlockPosition = 0;
        int endBlockPosition = -1;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.BEGIN.getValue()) {
                bracketCounter++;
            } else if (input.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
            } else if (input.charAt(i) == ReservedChars.OPEN_SQUARE_BRACKET.getValue()) {
                squareBracketCounter++;
            } else if (input.charAt(i) == ReservedChars.CLOSE_SQUARE_BRACKET.getValue()) {
                squareBracketCounter--;
            }
            if (bracketCounter == 0 && squareBracketCounter == 0 && input.charAt(i) == ReservedChars.LINE_SEPARATOR.getValue()) {
                endBlockPosition = i;
                linesAsList.add(input.substring(beginBlockPosition, endBlockPosition));
                beginBlockPosition = i + 1;
            }
        }
        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }
        if (squareBracketCounter > 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }
        if (endBlockPosition != input.length() - 1) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }

        String[] lines = linesAsList.toArray(new String[linesAsList.size()]);

        List<AlgorithmCommand> commands = new ArrayList<>();
        for (String line : lines) {
            if (!line.isEmpty()) {
                commands.addAll(parseLine(line, memory, alg));
            }
        }
        return commands;
    }

    ///////////////////// Methoden für die Zerlegung eines Ausdrucks, welcher Algorithmenaufrufe enthält, in mehrere Befehle ///////////////////////
    private static AlgorithmCommandReplacementList decomposeAbstractExpressionInvolvingAlgorithmCalls(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        String inputWithGeneratedVars = input;
        List<AlgorithmCommand> commands = new ArrayList<>();

        boolean algorithmCallFound = false;
        String algorithmCallAsString;
        int beginning;
        do {
            for (Signature signature : AlgorithmCompiler.STORED_ALGORITHM_SIGNATURES) {
                if (!inputWithGeneratedVars.contains(signature.getName())) {
                    continue;
                }
                beginning = inputWithGeneratedVars.indexOf(signature.getName(), 0);
                while (beginning >= 0) {
                    for (int i = beginning + signature.getName().length(); i < input.length(); i++) {
                        algorithmCallAsString = inputWithGeneratedVars.substring(inputWithGeneratedVars.indexOf(signature.getName()), i);

                        // Der Algorithmusaufruf darf nicht der gesamte input sein.
                        if (algorithmCallAsString.length() == inputWithGeneratedVars.length()) {
                            break;
                        }

                        Signature calledAlgSignature = null;
                        try {
                            calledAlgSignature = getSignatureFromAlgorithmCall(algorithmCallAsString, memory, IdentifierType.EXPRESSION);
                        } catch (ParseAssignValueException e) {
                        }
                        try {
                            calledAlgSignature = getSignatureFromAlgorithmCall(algorithmCallAsString, memory, IdentifierType.BOOLEAN_EXPRESSION);
                        } catch (ParseAssignValueException e) {
                        }
                        try {
                            calledAlgSignature = getSignatureFromAlgorithmCall(algorithmCallAsString, memory, IdentifierType.MATRIX_EXPRESSION);
                        } catch (ParseAssignValueException e) {
                        }

                        if (calledAlgSignature == null) {
                            continue;
                        }

                        try {
                            Identifier[] parameterIdentifiers = getParameterFromAlgorithmCall(algorithmCallAsString, memory);
                            String genVarName = generateTechnicalVarName();
                            Identifier identifier = Identifier.createIdentifier(alg, genVarName, signature.getReturnType());
                            memory.getMemory().put(genVarName, identifier);
                            commands.add(new AssignValueCommand(identifier, calledAlgSignature, parameterIdentifiers));
                            inputWithGeneratedVars = inputWithGeneratedVars.replaceAll(algorithmCallAsString, genVarName);
                            algorithmCallFound = true;
                            beginning = -1;
                            break;
                        } catch (ParseAssignValueException e) {
                            beginning = inputWithGeneratedVars.indexOf(signature.getName(), beginning + 1);
                            break;
                        }

                    }
                }

            }
        } while (algorithmCallFound);

        return new AlgorithmCommandReplacementList(commands, inputWithGeneratedVars);
    }

    private static class AlgorithmCommandReplacementList {

        private final List<AlgorithmCommand> commands;
        private final String rightSide;

        public AlgorithmCommandReplacementList(List<AlgorithmCommand> commands, String rightSide) {
            this.commands = commands;
            this.rightSide = rightSide;
        }

        public List<AlgorithmCommand> getCommands() {
            return commands;
        }

        public String getRightSide() {
            return rightSide;
        }

        public void addCommand(AlgorithmCommand command) {
            this.commands.add(command);
        }
    }

}
