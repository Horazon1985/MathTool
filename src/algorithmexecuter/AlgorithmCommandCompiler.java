package algorithmexecuter;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.expression.classes.Variable;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import abstractexpressions.matrixexpression.classes.MatrixVariable;
import static algorithmexecuter.AlgorithmCompiler.VALIDATOR;
import algorithmexecuter.model.command.AlgorithmCommand;
import algorithmexecuter.model.command.AssignValueCommand;
import algorithmexecuter.model.command.DeclareIdentifierCommand;
import algorithmexecuter.model.command.IfElseControlStructure;
import algorithmexecuter.model.command.ReturnCommand;
import algorithmexecuter.model.command.WhileControlStructure;
import algorithmexecuter.booleanexpression.BooleanExpression;
import algorithmexecuter.booleanexpression.BooleanVariable;
import algorithmexecuter.enums.ComparingOperators;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.Operators;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.BlockCompileException;
import algorithmexecuter.exceptions.BooleanExpressionException;
import algorithmexecuter.exceptions.CompileExceptionTexts;
import algorithmexecuter.exceptions.DeclareIdentifierException;
import algorithmexecuter.exceptions.NotDesiredCommandException;
import algorithmexecuter.exceptions.ParseAssignValueException;
import algorithmexecuter.exceptions.ParseControlStructureException;
import algorithmexecuter.exceptions.ParseKeywordException;
import algorithmexecuter.exceptions.ParseReturnException;
import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.model.Signature;
import algorithmexecuter.model.command.DoWhileControlStructure;
import algorithmexecuter.model.command.ForControlStructure;
import algorithmexecuter.model.command.KeywordCommand;
import exceptions.ExpressionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AlgorithmCommandCompiler {

    private static final String GEN_VAR = "gen_var_";

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
    private static List<AlgorithmCommand> parseLine(String line, AlgorithmMemory memory, Algorithm alg, boolean keywordsAllowed) throws AlgorithmCompileException {
        try {
            return parseAssignValueCommand(line, memory);
        } catch (ParseAssignValueException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseDeclareIdentifierCommand(line, memory);
        } catch (ParseAssignValueException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseVoidCommand(line, memory);
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseControlStructure(line, memory, alg, keywordsAllowed);
        } catch (ParseControlStructureException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseKeywordCommand(line, keywordsAllowed);
        } catch (ParseKeywordException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        try {
            return parseReturnCommand(line, memory, alg);
        } catch (ParseReturnException e) {
            throw e;
        } catch (NotDesiredCommandException e) {
        }

        throw new AlgorithmCompileException(CompileExceptionTexts.AC_COMMAND_COUND_NOT_BE_PARSED, line);
    }

    private static List<AlgorithmCommand> parseDeclareIdentifierCommand(String line, AlgorithmMemory scopeMemory) throws AlgorithmCompileException, NotDesiredCommandException {
        // Typ ermitteln. Dieser ist != null, wenn es sich definitiv um eine Identifierdeklaration handelt.
        IdentifierType type = getTypeIfIsValidDeclareIdentifierCommand(line);
        if (type == null) {
            throw new NotDesiredCommandException();
        }

        String identifierName = line.substring(type.toString().length() + 1);
        if (!VALIDATOR.isValidIdentifier(identifierName)) {
            throw new DeclareIdentifierException(CompileExceptionTexts.AC_ILLEGAL_CHARACTER, identifierName);
        }
        // Prüfung, ob dieser Identifier bereits existiert.
        if (scopeMemory.containsIdentifier(identifierName)) {
            throw new ParseAssignValueException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED, identifierName);
        }
        Identifier identifier = Identifier.createIdentifier(scopeMemory, identifierName, type);
        scopeMemory.getMemory().put(identifierName, identifier);
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

    private static List<AlgorithmCommand> parseAssignValueCommand(String line, AlgorithmMemory scopeMemory) throws AlgorithmCompileException, NotDesiredCommandException {
        // Ermittlung der Stelle des Zuweisungsoperators "=", falls vorhanden. 
        int defineCharPosition = getPositionOfDefineCharIfIsAssignValueCommandIfValid(line);
        if (defineCharPosition < 0) {
            throw new NotDesiredCommandException();
        }

        String leftSide = line.substring(0, defineCharPosition);
        String rightSide = line.substring(defineCharPosition + 1);
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

            identifierName = leftSide.substring((type.toString() + " ").length());
            // Prüfung, ob dieser Identifier gültigen Namen besitzt.
            if (!VALIDATOR.isValidIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_ILLEGAL_CHARACTER, identifierName);
            }
            // Prüfung, ob dieser Identifier bereits existiert.
            if (scopeMemory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED, identifierName);
            }
        } else {
            identifierName = leftSide;
            // Prüfung, ob dieser Identifier bereits existiert.
            if (!scopeMemory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, identifierName);
            }
            type = scopeMemory.getMemory().get(identifierName).getType();
        }

        // Rechte Seite behandeln.
        Identifier identifier = Identifier.createIdentifier(scopeMemory, identifierName, type);
        if (type == IdentifierType.EXPRESSION) {

            // Fall: Arithmetischer / Analytischer Ausdruck.
            AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, scopeMemory);
            List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
            String rightSideReplaced = algorithmCommandReplacementList.getRightSide();
            try {
                Expression expr = Expression.build(rightSideReplaced, VALIDATOR);
                Set<String> vars = expr.getContainedVars();
                checkIfAllIdentifiersAreDefined(vars, scopeMemory);
                areIdentifiersOfCorrectType(type, vars, scopeMemory);
                scopeMemory.getMemory().put(identifierName, identifier);
                commands.add(new AssignValueCommand(identifier, expr));
                return commands;
            } catch (ExpressionException e) {
                return parseAlgorithmCall(scopeMemory, commands, rightSideReplaced, IdentifierType.EXPRESSION, identifier);
            }

        } else if (type == IdentifierType.BOOLEAN_EXPRESSION) {

            // Fall: boolscher Ausdruck.
            AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, scopeMemory);
            List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
            String rightSideReplaced = algorithmCommandReplacementList.getRightSide();
            try {
                Map<String, IdentifierType> valuesMap = CompilerUtils.extractTypesOfMemory(scopeMemory);
                BooleanExpression boolExpr = BooleanExpression.build(rightSideReplaced, VALIDATOR, valuesMap);
                Set<String> vars = boolExpr.getContainedVars();
                checkIfAllIdentifiersAreDefined(boolExpr.getContainedVars(), scopeMemory);
                areIdentifiersOfCorrectType(type, vars, scopeMemory);
                scopeMemory.getMemory().put(identifierName, identifier);
                commands.add(new AssignValueCommand(identifier, boolExpr));
                return commands;
            } catch (BooleanExpressionException e) {
                return parseAlgorithmCall(scopeMemory, commands, rightSideReplaced, IdentifierType.BOOLEAN_EXPRESSION, identifier);
            }

        }

        // Fall: Matrizenausdruck.
        AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, scopeMemory);
        List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
        String rightSideReplaced = algorithmCommandReplacementList.getRightSide();
        try {
            MatrixExpression matExpr = MatrixExpression.build(rightSideReplaced, VALIDATOR, VALIDATOR);
            checkIfAllIdentifiersAreDefined(matExpr.getContainedVars(), scopeMemory);
            areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, matExpr.getContainedExpressionVars(), scopeMemory);
            areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, matExpr.getContainedMatrixVars(), scopeMemory);
            scopeMemory.getMemory().put(identifierName, identifier);
            commands.add(new AssignValueCommand(identifier, matExpr));
            return commands;
        } catch (ExpressionException e) {
            return parseAlgorithmCall(scopeMemory, commands, rightSideReplaced, IdentifierType.MATRIX_EXPRESSION, identifier);
        }

    }

    private static int getPositionOfDefineCharIfIsAssignValueCommandIfValid(String line) {
        /*
        Prüfung, ob line einen Vergleichsoperator enthält, welcher "=" enthält.
        Im positiven Fall kann es keine Zuweisung mehr sein, sondern höchstens eine
        Kontrollstruktur.
         */
        for (ComparingOperators op : ComparingOperators.getOperatorsContainingEqualsSign()) {
            if (line.contains(op.getValue())) {
                return -1;
            }
        }
        int wavyBracketCounter = 0, bracketCounter = 0, squareBracketCounter = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ReservedChars.BEGIN.getValue()) {
                wavyBracketCounter++;
            } else if (line.charAt(i) == ReservedChars.END.getValue()) {
                wavyBracketCounter--;
            } else if (line.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
            } else if (line.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
            } else if (line.charAt(i) == ReservedChars.OPEN_SQUARE_BRACKET.getValue()) {
                squareBracketCounter++;
            } else if (line.charAt(i) == ReservedChars.CLOSE_SQUARE_BRACKET.getValue()) {
                squareBracketCounter--;
            }
            if (wavyBracketCounter == 0 && bracketCounter == 0 && squareBracketCounter == 0
                    && String.valueOf(line.charAt(i)).equals(Operators.DEFINE.getValue())) {
                return i;
            }
        }
        return -1;
    }

    private static void checkIfAllIdentifiersAreDefined(Set<String> vars, AlgorithmMemory memory) throws ParseAssignValueException {
        for (String var : vars) {
            if (!memory.getMemory().containsKey(var) || memory.getMemory().get(var).getType() != IdentifierType.EXPRESSION) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, var);
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

    private static List<AlgorithmCommand> parseAlgorithmCall(AlgorithmMemory memory, List<AlgorithmCommand> commands,
            String rightSide, IdentifierType assignType, Identifier identifier) throws AlgorithmCompileException {
        // Kompatibilitätscheck
        Signature calledAlgSignature = getAlgorithmCallDataFromAlgorithmCall(rightSide, memory, assignType).getSignature();
        // Parameter auslesen;
        Identifier[] parameterIdentifiers = getParameterFromAlgorithmCall(rightSide, memory);
        memory.getMemory().put(identifier.getName(), identifier);
        commands.add(new AssignValueCommand(identifier, calledAlgSignature, parameterIdentifiers));
        return commands;

    }

    private static Identifier[] getParameterFromAlgorithmCall(String input, AlgorithmMemory memory) throws ParseAssignValueException {
        try {
            CompilerUtils.AlgorithmParseData algParseData = CompilerUtils.getAlgorithmParseData(input);
            String[] params = algParseData.getParameters();
            Identifier[] identifiers = new Identifier[params.length];
            for (int i = 0; i < params.length; i++) {
                if (memory.getMemory().get(params[i]) == null) {
                    throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, params[i]);
                }
                identifiers[i] = memory.getMemory().get(params[i]);
            }
            return identifiers;
        } catch (AlgorithmCompileException e) {
            throw new ParseAssignValueException(e);
        }
    }

    private static AlgorithmCallData getAlgorithmCallDataFromAlgorithmCall(String input, AlgorithmMemory memory, IdentifierType returnType) throws ParseAssignValueException {
        try {
            CompilerUtils.AlgorithmParseData algParseData = CompilerUtils.getAlgorithmParseData(input);
            String algName = algParseData.getName();
            String[] params = algParseData.getParameters();

            AbstractExpression[] paramValues = new AbstractExpression[params.length];
            for (int i = 0; i < params.length; i++) {
                try {
                    paramValues[i] = Expression.build(params[i], AlgorithmCompiler.VALIDATOR);
                } catch (ExpressionException eExpr) {
                    Map<String, IdentifierType> typeMap = CompilerUtils.extractTypesOfMemory(memory);
                    try {
                        paramValues[i] = BooleanExpression.build(params[i], AlgorithmCompiler.VALIDATOR, typeMap);
                    } catch (BooleanExpressionException eBoolExpr) {
                        try {
                            paramValues[i] = MatrixExpression.build(params[i], AlgorithmCompiler.VALIDATOR, AlgorithmCompiler.VALIDATOR);
                        } catch (ExpressionException eMatExpr) {
                            throw new ParseAssignValueException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
                        }
                    }
                }
            }

            // Prüfung, ob ein Algorithmus mit diesem Namen bekannt ist.
            Signature algorithmCandidate = null;
            boolean candidateFound;
            for (Signature signature : AlgorithmCompiler.ALGORITHM_SIGNATURES.getAlgorithmSignatureStorage()) {
                if (signature.getName().equals(algName) && signature.getParameterTypes().length == params.length) {
                    candidateFound = true;
                    for (int i = 0; i < params.length; i++) {
                        if (!areAllVarsContainedInMemory(paramValues[i].getContainedVars(), memory)
                                || IdentifierType.identifierTypeOf(paramValues[i]) != signature.getParameterTypes()[i]) {
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
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, algName);
            }

            // Prüfung, ob Rückgabewert korrekt ist.
            if (!algorithmCandidate.getReturnType().equals(returnType)) {
                // TODO: Fehlermeldung korrigieren.
                throw new ParseAssignValueException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }
            return new AlgorithmCallData(algorithmCandidate, paramValues);
        } catch (AlgorithmCompileException e) {
            throw new ParseAssignValueException(e);
        }
    }

    private static boolean areAllVarsContainedInMemory(Set<String> varNames, AlgorithmMemory memory) {
        for (String varName : varNames) {
            if (memory.getMemory().get(varName) == null) {
                return false;
            }
        }
        return true;
    }

    private static List<AlgorithmCommand> parseVoidCommand(String line, AlgorithmMemory memory) throws AlgorithmCompileException, NotDesiredCommandException {

        throw new NotDesiredCommandException();
    }

    private static List<AlgorithmCommand> parseControlStructure(String line, AlgorithmMemory memory, Algorithm alg, boolean keywordsAllowed) throws AlgorithmCompileException, NotDesiredCommandException {
        // If-Else-Block
        try {
            return parseIfElseControlStructure(line, memory, alg, keywordsAllowed);
        } catch (ParseControlStructureException e) {
            /*
            Es ist zwar eine If-Else-Struktur mit korrekter Bedingung, aber der
            innere Block kann nicht kompiliert werden.
             */
            throw e;
        } catch (BooleanExpressionException | BlockCompileException e) {
            // Es ist zwar eine If-Else-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
            // Keine If-Else-Struktur. Also weiter.
        }

        // For-Block
        try {
            return parseForControlStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            /*
            Es ist zwar eine While-Struktur mit korrekter Bedingung, aber der
            innere Block kann nicht kompiliert werden.
             */
            throw e;
        } catch (BooleanExpressionException | BlockCompileException e) {
            // Es ist zwar eine While-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
            // Keine While-Struktur. Also weiter.
        }

        // While-Block
        try {
            return parseWhileControlStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            /*
            Es ist zwar eine While-Struktur mit korrekter Bedingung, aber der
            innere Block kann nicht kompiliert werden.
             */
            throw e;
        } catch (BooleanExpressionException | BlockCompileException e) {
            // Es ist zwar eine While-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
            // Keine While-Struktur. Also weiter.
        }

        // Do-While-Block
        try {
            return parseDoWhileControlStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            /*
            Es ist zwar eine Do-While-Struktur mit korrekter Bedingung, aber der
            innere Block kann nicht kompiliert werden.
             */
            throw e;
        } catch (BooleanExpressionException | BlockCompileException e) {
            // Es ist zwar eine While-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
            // Keine Do-While-Struktur. Also weiter.
        }

        throw new NotDesiredCommandException();
    }

    private static List<AlgorithmCommand> parseIfElseControlStructure(String line, AlgorithmMemory memory, Algorithm alg, boolean keywordsAllowed)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keyword.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new NotDesiredCommandException();
        }

        int bracketCounter = 1;
        int endOfBooleanCondition = -1;
        for (int i = (Keyword.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(); i < line.length(); i++) {
            if (line.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
            } else if (line.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0) {
                endOfBooleanCondition = i;
                break;
            }
        }
        if (bracketCounter > 0) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String booleanConditionString = line.substring((Keyword.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);
        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        BooleanExpression condition = BooleanExpression.build(booleanConditionString, VALIDATOR, typesMap);

        // Prüfung, ob line mit "if(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(ReservedChars.BEGIN.getStringValue())
                || !line.contains(ReservedChars.END.getStringValue())
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
                    ReservedChars.BEGIN.getValue(), ReservedChars.END.getValue());
        }

        // Block im If-Teil kompilieren.
        bracketCounter = 0;
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

        AlgorithmMemory memoryBeforeIfElsePart = memory.copyMemory();

        List<AlgorithmCommand> commandsIfPart;
        if (keywordsAllowed) {
            commandsIfPart = parseConnectedBlockWithKeywords(line.substring(beginBlockPosition, endBlockPosition), memoryBeforeIfElsePart, alg);
        } else {
            commandsIfPart = parseConnectedBlockWithoutKeywords(line.substring(beginBlockPosition, endBlockPosition), memoryBeforeIfElsePart, alg);
        }
        IfElseControlStructure ifElseControlStructure = new IfElseControlStructure(condition, commandsIfPart);

        // Block im Else-Teil kompilieren, falls vorhanden.
        if (endBlockPosition == line.length() - 1) {
            // Kein Else-Teil vorhanden.
            return Collections.singletonList((AlgorithmCommand) ifElseControlStructure);
        }

        String restLine = line.substring(endBlockPosition + 1);
        if (!restLine.startsWith(Keyword.ELSE.getValue() + ReservedChars.BEGIN.getValue())) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_KEYWORD_EXPECTED, Keyword.ELSE.getValue());
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
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, line.substring(endBlockPosition + 1));
        }

        List<AlgorithmCommand> commandsElsePart;
        if (keywordsAllowed) {
            commandsElsePart = parseConnectedBlockWithKeywords(restLine.substring(beginBlockPosition, endBlockPosition), memoryBeforeIfElsePart, alg);
        } else {
            commandsElsePart = parseConnectedBlockWithoutKeywords(restLine.substring(beginBlockPosition, endBlockPosition), memoryBeforeIfElsePart, alg);
        }
        ifElseControlStructure.setCommandsElsePart(commandsElsePart);

        return Collections.singletonList((AlgorithmCommand) ifElseControlStructure);
    }

    private static List<AlgorithmCommand> parseWhileControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keyword.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new NotDesiredCommandException();
        }

        int bracketCounter = 1;
        int endOfBooleanCondition = -1;
        for (int i = (Keyword.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(); i < line.length(); i++) {
            if (line.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
            } else if (line.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0) {
                endOfBooleanCondition = i;
                break;
            }
        }
        if (bracketCounter > 0) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String booleanConditionString = line.substring((Keyword.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);
        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        BooleanExpression condition = BooleanExpression.build(booleanConditionString, VALIDATOR, typesMap);

        // Prüfung, ob line mit "while(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(ReservedChars.BEGIN.getStringValue())
                || !line.contains(ReservedChars.END.getStringValue())
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
                    ReservedChars.BEGIN.getValue(), ReservedChars.END.getValue());
        }

        // Block im While-Teil kompilieren.
        bracketCounter = 0;
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

        AlgorithmMemory memoryBeforWhileLoop = memory.copyMemory();

        List<AlgorithmCommand> commandsWhilePart = parseConnectedBlockWithKeywords(line.substring(beginBlockPosition, endBlockPosition), memoryBeforWhileLoop, alg);
        WhileControlStructure whileControlStructure = new WhileControlStructure(condition, commandsWhilePart);

        // '}' muss als letztes Zeichen stehen, sonst ist die Struktur nicht korrekt.
        if (endBlockPosition == line.length() - 1) {
            // Kein Else-Teil vorhanden.
            return Collections.singletonList((AlgorithmCommand) whileControlStructure);
        }

        throw new ParseControlStructureException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, line.substring(endBlockPosition + 1));
    }

    private static List<AlgorithmCommand> parseDoWhileControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keyword.DO.getValue() + ReservedChars.BEGIN.getValue())) {
            throw new NotDesiredCommandException();
        }

        // Block im Do-Teil kompilieren.
        int bracketCounter = 1;
        int beginBlockPosition = line.indexOf(ReservedChars.BEGIN.getValue()) + 1;
        int endBlockPosition = -1;
        for (int i = beginBlockPosition; i < line.length(); i++) {
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

        AlgorithmMemory memoryBeforWhileLoop = memory.copyMemory();

        List<AlgorithmCommand> commandsDoPart = parseConnectedBlockWithKeywords(line.substring(beginBlockPosition, endBlockPosition), memoryBeforWhileLoop, alg);

        // While-Bedingung kompilieren
        String whilePart = line.substring(endBlockPosition + 1);
        if (!whilePart.startsWith(Keyword.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_KEYWORD_EXPECTED, Keyword.WHILE.getValue());
        }
        if (!whilePart.endsWith(String.valueOf(ReservedChars.CLOSE_BRACKET.getValue()))) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String whileConditionPart = line.substring(endBlockPosition + Keyword.WHILE.getValue().length() + 2, line.length() - 1);
        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        BooleanExpression condition = BooleanExpression.build(whileConditionPart, VALIDATOR, typesMap);

        return Collections.singletonList((AlgorithmCommand) new DoWhileControlStructure(commandsDoPart, condition));
    }

    private static List<AlgorithmCommand> parseForControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keyword.FOR.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new NotDesiredCommandException();
        }

        int bracketCounter = 1;
        int endOfForControlPart = -1;
        for (int i = (Keyword.FOR.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(); i < line.length(); i++) {
            if (line.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
            } else if (line.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0) {
                endOfForControlPart = i;
                break;
            }
        }
        if (bracketCounter > 0) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String forControlString = line.substring((Keyword.FOR.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfForControlPart);

        AlgorithmMemory memoryBeforFoorLoop = memory.copyMemory();

        // Die drei for-Anweisungen kompilieren.
        String[] forControlParts = forControlString.split(ReservedChars.ARGUMENT_SEPARATOR.getStringValue());
        List<AlgorithmCommand> initialization = parseAssignValueCommand(forControlParts[0], memoryBeforFoorLoop);
        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memoryBeforFoorLoop);
        BooleanExpression endLoopCondition = BooleanExpression.build(forControlParts[1], VALIDATOR, typesMap);
        AlgorithmMemory copyOfMemory = memoryBeforFoorLoop.copyMemory();
        List<AlgorithmCommand> loopAssignment = parseAssignValueCommand(forControlParts[2], memoryBeforFoorLoop);
        // Prüfung, ob bei loopAssignment keine weiteren Bezeichner hinzukamen.
        if (memoryBeforFoorLoop.getSize() > copyOfMemory.getSize()) {
            String newIdentifierName = getNameOfNewIdentifier(copyOfMemory, memoryBeforFoorLoop);
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CONTROL_STRUCTURE_FOR_NEW_IDENTIFIER_NOT_ALLOWED, newIdentifierName);
        }

        // Prüfung, ob line mit "for(a;b;c){ ..." beginnt.
        if (!line.contains(ReservedChars.BEGIN.getStringValue())
                || !line.contains(ReservedChars.END.getStringValue())
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfForControlPart + 1) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
                    ReservedChars.BEGIN.getValue(), ReservedChars.END.getValue());
        }

        // Block im For-Teil kompilieren.
        bracketCounter = 0;
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
        List<AlgorithmCommand> commandsForPart = parseConnectedBlockWithKeywords(line.substring(beginBlockPosition, endBlockPosition), memoryBeforFoorLoop, alg);
        ForControlStructure forControlStructure = new ForControlStructure(commandsForPart, initialization, endLoopCondition, loopAssignment);

        // Lokale Variable aus dem Speicher memory wieder herausnehmen.
        // '}' muss als letztes Zeichen stehen, sonst ist die Struktur nicht korrekt.
        if (endBlockPosition == line.length() - 1) {
            // Kein Else-Teil vorhanden.
            return Collections.singletonList((AlgorithmCommand) forControlStructure);
        }

        throw new ParseControlStructureException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, line.substring(endBlockPosition + 1));
    }

    private static String getNameOfNewIdentifier(AlgorithmMemory memoryBefore, AlgorithmMemory memoryAfter) {
        for (String identifierName : memoryAfter.getMemory().keySet()) {
            if (!memoryBefore.getMemory().containsKey(identifierName)) {
                return identifierName;
            }
        }
        return null;
    }

    private static List<AlgorithmCommand> parseKeywordCommand(String line, boolean keywordsAllowed) throws AlgorithmCompileException, NotDesiredCommandException {
        if (line.equals(Keyword.BREAK.toString())) {
            if (keywordsAllowed) {
                return Collections.singletonList(new KeywordCommand(Keyword.BREAK));
            }
            throw new ParseKeywordException(CompileExceptionTexts.AC_KEYWORD_NOT_ALLOWED_HERE, Keyword.BREAK);
        }
        if (line.equals(Keyword.CONTINUE.toString())) {
            if (keywordsAllowed) {
                return Collections.singletonList(new KeywordCommand(Keyword.CONTINUE));
            }
            throw new ParseKeywordException(CompileExceptionTexts.AC_KEYWORD_NOT_ALLOWED_HERE, Keyword.CONTINUE);
        }
        throw new NotDesiredCommandException();
    }

    private static List<AlgorithmCommand> parseReturnCommand(String line, AlgorithmMemory scopeMemory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {
        if (line.startsWith(Keyword.RETURN.getValue() + " ")) {
            if (line.equals(Keyword.RETURN.getValue() + ReservedChars.LINE_SEPARATOR)) {
                return Collections.singletonList((AlgorithmCommand) new ReturnCommand(null));
            }
            String returnValueCandidate = line.substring((Keyword.RETURN.getValue() + " ").length());

            if (scopeMemory.getMemory().get(returnValueCandidate) == null) {

                AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(returnValueCandidate, scopeMemory);
                List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
                String rightSideReplaced = algorithmCommandReplacementList.getRightSide();

                if (scopeMemory.getMemory().get(rightSideReplaced) != null) {
                    return Collections.singletonList((AlgorithmCommand) new ReturnCommand(scopeMemory.getMemory().get(returnValueCandidate)));
                }
                if (VALIDATOR.isValidIdentifier(rightSideReplaced)) {
                    throw new ParseReturnException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, returnValueCandidate);
                }
                String genVarForReturn = generateTechnicalVarName();
                String assignValueCommand = alg.getReturnType().toString() + " " + genVarForReturn + "=" + rightSideReplaced;
                List<AlgorithmCommand> additionalCommandsByAssignment = parseAssignValueCommand(assignValueCommand, scopeMemory);
                commands.addAll(additionalCommandsByAssignment);
                commands.add(new ReturnCommand(Identifier.createIdentifier(scopeMemory, genVarForReturn, alg.getReturnType())));
                return commands;
            } else {
                return Collections.singletonList((AlgorithmCommand) new ReturnCommand(Identifier.createIdentifier(scopeMemory, returnValueCandidate, alg.getReturnType())));
            }
        }
        throw new NotDesiredCommandException();
    }

    public static List<AlgorithmCommand> parseConnectedBlockWithKeywords(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        return parseCommandBlock(input, memory, alg, true, true);
    }

    public static List<AlgorithmCommand> parseConnectedBlockWithoutKeywords(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        return parseCommandBlock(input, memory, alg, true, false);
    }

    public static List<AlgorithmCommand> parseBlockWithKeywords(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        return parseCommandBlock(input, memory, alg, false, true);
    }

    public static List<AlgorithmCommand> parseBlockWithoutKeywords(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        return parseCommandBlock(input, memory, alg, false, false);
    }

    private static List<AlgorithmCommand> parseCommandBlock(String input, AlgorithmMemory memory, Algorithm alg, boolean connectedBlock, boolean keywordsAllowed) throws AlgorithmCompileException {
        if (!input.isEmpty() && !input.endsWith(String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_MISSING_LINE_SEPARATOR, ReservedChars.LINE_SEPARATOR.getValue());
        }

        AlgorithmMemory memoryBeforeBlockBeginning;
        if (connectedBlock) {
            memoryBeforeBlockBeginning = memory.copyMemory();
        } else {
            memoryBeforeBlockBeginning = memory;
        }

        List<String> linesAsList = new ArrayList<>();
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
                commands.addAll(parseLine(line, memoryBeforeBlockBeginning, alg, keywordsAllowed));
            }
        }
        return commands;
    }

    ///////////////////// Methoden für die Zerlegung eines Ausdrucks, welcher Algorithmenaufrufe enthält, in mehrere Befehle ///////////////////////
    private static AlgorithmCommandReplacementList decomposeAbstractExpressionInvolvingAlgorithmCalls(String input, AlgorithmMemory memory) {
        String inputWithGeneratedVars = input;
        List<AlgorithmCommand> commands = new ArrayList<>();

        boolean algorithmCallFound = false;
        String algorithmCallAsString;
        int beginningAlgCall;
        int endingAlgCall;
        do {
            for (Signature signature : AlgorithmCompiler.ALGORITHM_SIGNATURES.getAlgorithmSignatureStorage()) {
                if (!inputWithGeneratedVars.contains(signature.getName())) {
                    continue;
                }
                beginningAlgCall = inputWithGeneratedVars.indexOf(signature.getName(), 0);
                while (beginningAlgCall >= 0) {
                    for (int i = beginningAlgCall + signature.getName().length(); i < input.length() + 1; i++) {
                        algorithmCallAsString = inputWithGeneratedVars.substring(inputWithGeneratedVars.indexOf(signature.getName()), i);

                        // Der Algorithmusaufruf darf nicht der gesamte input sein.
                        if (algorithmCallAsString.length() == inputWithGeneratedVars.length()) {
                            return new AlgorithmCommandReplacementList(new ArrayList<AlgorithmCommand>(), input);
                        }

                        AlgorithmCallData algorithmCallData = null;
                        try {
                            algorithmCallData = getAlgorithmCallDataFromAlgorithmCall(algorithmCallAsString, memory, IdentifierType.EXPRESSION);
                        } catch (ParseAssignValueException e) {
                        }
                        if (algorithmCallData == null) {
                            try {
                                algorithmCallData = getAlgorithmCallDataFromAlgorithmCall(algorithmCallAsString, memory, IdentifierType.BOOLEAN_EXPRESSION);
                            } catch (ParseAssignValueException e) {
                            }
                        }
                        if (algorithmCallData == null) {
                            try {
                                algorithmCallData = getAlgorithmCallDataFromAlgorithmCall(algorithmCallAsString, memory, IdentifierType.MATRIX_EXPRESSION);
                            } catch (ParseAssignValueException e) {
                            }
                        }

                        if (algorithmCallData == null) {
                            continue;
                        }

                        endingAlgCall = i;

                        try {
                            inputWithGeneratedVars = addAssignValueCommandsForNonVarAlgorithmParameters(inputWithGeneratedVars, beginningAlgCall, endingAlgCall, algorithmCallData, commands, memory);
                            beginningAlgCall = inputWithGeneratedVars.indexOf(signature.getName(), endingAlgCall);
                            break;
                        } catch (ParseAssignValueException e) {
                            beginningAlgCall = inputWithGeneratedVars.indexOf(signature.getName(), beginningAlgCall + 1);
                            break;
                        }

                    }
                }

            }
        } while (algorithmCallFound);

        return new AlgorithmCommandReplacementList(commands, inputWithGeneratedVars);
    }

    private static String addAssignValueCommandsForNonVarAlgorithmParameters(String input, int beginningAlgCall, int endingAlgCall, AlgorithmCallData algorithmCallData,
            List<AlgorithmCommand> commands, AlgorithmMemory scopeMemory) throws ParseAssignValueException {

        Identifier[] inputParameters = new Identifier[algorithmCallData.getParameterValues().length];
        AbstractExpression value;
        for (int i = 0; i < algorithmCallData.getParameterValues().length; i++) {
            value = algorithmCallData.getParameterValues()[i];
            if (value instanceof Variable) {
                inputParameters[i] = Identifier.createIdentifier(scopeMemory, ((Variable) value).getName(), IdentifierType.identifierTypeOf(value));
            } else if (value instanceof BooleanVariable) {
                inputParameters[i] = Identifier.createIdentifier(scopeMemory, ((BooleanVariable) value).getName(), IdentifierType.identifierTypeOf(value));
            } else if (value instanceof MatrixVariable) {
                inputParameters[i] = Identifier.createIdentifier(scopeMemory, ((MatrixVariable) value).getName(), IdentifierType.identifierTypeOf(value));
            } else {
                String genVarName = generateTechnicalVarName();
                try {
                    Identifier genVarIdentifier = Identifier.createIdentifier(scopeMemory, genVarName, IdentifierType.identifierTypeOf(value));
                    inputParameters[i] = genVarIdentifier;
                    commands.add(new AssignValueCommand(genVarIdentifier, value));
                    scopeMemory.addToMemoryInCompileTime(genVarIdentifier);
                } catch (AlgorithmCompileException e) {
                    throw new ParseAssignValueException(e);
                }
            }
        }

        String genVarNameForCalledAlg = generateTechnicalVarName();
        Identifier identifierForCalledAlg = Identifier.createIdentifier(scopeMemory, genVarNameForCalledAlg, algorithmCallData.getSignature().getReturnType());
        try {
            commands.add(new AssignValueCommand(identifierForCalledAlg, algorithmCallData.getSignature(), inputParameters));
            scopeMemory.addToMemoryInCompileTime(identifierForCalledAlg);
        } catch (AlgorithmCompileException e) {
            throw new ParseAssignValueException(e);
        }

        String algorithmCallString = input.substring(beginningAlgCall, endingAlgCall);
        String result = input;
        while (result.contains(algorithmCallString)) {
            result = result.replace(algorithmCallString, genVarNameForCalledAlg);
        }
        return result;
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

    private static class AlgorithmCallData {

        private final Signature signature;
        private final AbstractExpression[] parameterValues;

        public AlgorithmCallData(Signature signature, AbstractExpression[] parameterValues) {
            this.signature = signature;
            this.parameterValues = parameterValues;
        }

        public Signature getSignature() {
            return signature;
        }

        public AbstractExpression[] getParameterValues() {
            return parameterValues;
        }

    }

}