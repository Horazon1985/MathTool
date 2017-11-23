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
import algorithmexecuter.enums.AssignValueType;
import algorithmexecuter.enums.ComparingOperators;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.Operators;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.BlockCompileException;
import algorithmexecuter.exceptions.BooleanExpressionException;
import algorithmexecuter.exceptions.constants.AlgorithmCompileExceptionIds;
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

public abstract class AlgorithmCommandCompiler {

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

        throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_COMMAND_COUND_NOT_BE_PARSED, line);
    }

    private static List<AlgorithmCommand> parseDeclareIdentifierCommand(String line, AlgorithmMemory scopeMemory) throws AlgorithmCompileException, NotDesiredCommandException {
        // Typ ermitteln. Dieser ist != null, wenn es sich definitiv um eine Identifierdeklaration handelt.
        IdentifierType type = getTypeIfIsValidDeclareIdentifierCommand(line);
        if (type == null) {
            throw new NotDesiredCommandException();
        }

        String identifierName = line.substring(type.toString().length() + 1);
        if (!VALIDATOR.isValidIdentifier(identifierName)) {
            throw new DeclareIdentifierException(AlgorithmCompileExceptionIds.AC_ILLEGAL_CHARACTER, identifierName);
        }
        // Prüfung, ob dieser Identifier bereits existiert.
        if (scopeMemory.containsIdentifier(identifierName)) {
            throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_IDENTIFIER_ALREADY_DEFINED, identifierName);
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
        for (IdentifierType t : IdentifierType.values()) {
            if (leftSide.startsWith(t.toString() + " ")) {
                type = t;
                break;
            }
        }

        String identifierName;
        AssignValueType assignValueType;
        if (type != null) {

            identifierName = leftSide.substring((type.toString() + " ").length());
            assignValueType = AssignValueType.NEW;
            // Prüfung, ob dieser Identifier gültigen Namen besitzt.
            if (!VALIDATOR.isValidIdentifier(identifierName)) {
                throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_ILLEGAL_CHARACTER, identifierName);
            }
            // Prüfung, ob dieser Identifier bereits existiert.
            if (scopeMemory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_IDENTIFIER_ALREADY_DEFINED, identifierName);
            }
        } else {
            identifierName = leftSide;
            assignValueType = AssignValueType.CHANGE;
            // Prüfung, ob dieser Identifier bereits existiert.
            if (!scopeMemory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, identifierName);
            }
            type = scopeMemory.getMemory().get(identifierName).getType();
        }

        // Rechte Seite behandeln.
        Identifier identifier = Identifier.createIdentifier(scopeMemory, identifierName, type);
        if (null != type) {
            switch (type) {
                case EXPRESSION: {

                    // Fall: Arithmetischer / Analytischer Ausdruck.
                    AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, scopeMemory);
                    List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
                    String rightSideReplaced = algorithmCommandReplacementList.getSubstitutedExpression();
                    try {
                        Expression expr = Expression.build(rightSideReplaced, VALIDATOR);

                        // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                        checkIfAllIdentifiersAreDefined(expr.getContainedVars(), scopeMemory);
                        areIdentifiersOfCorrectType(type, expr.getContainedVars(), scopeMemory);

                        scopeMemory.getMemory().put(identifierName, identifier);
                        commands.add(new AssignValueCommand(identifier, expr, assignValueType));
                        return commands;
                    } catch (ExpressionException e) {
                        return parseAlgorithmCall(scopeMemory, commands, rightSideReplaced, IdentifierType.EXPRESSION, identifier, assignValueType);
                    }

                }
                case BOOLEAN_EXPRESSION: {

                    // Fall: boolscher Ausdruck.
                    AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, scopeMemory);
                    List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
                    String rightSideReplaced = algorithmCommandReplacementList.getSubstitutedExpression();
                    try {
                        BooleanExpression boolExpr = BooleanExpression.build(rightSideReplaced, VALIDATOR, CompilerUtils.extractTypesOfMemory(scopeMemory));

                        // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                        checkIfAllIdentifiersAreDefined(boolExpr.getContainedVars(), scopeMemory);
                        areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, boolExpr.getContainedExpressionVars(), scopeMemory);
                        areIdentifiersOfCorrectType(IdentifierType.BOOLEAN_EXPRESSION, boolExpr.getContainedBooleanVars(scopeMemory), scopeMemory);
                        areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, boolExpr.getContainedMatrixVars(), scopeMemory);

                        scopeMemory.getMemory().put(identifierName, identifier);
                        commands.add(new AssignValueCommand(identifier, boolExpr, assignValueType));
                        return commands;
                    } catch (BooleanExpressionException e) {
                        return parseAlgorithmCall(scopeMemory, commands, rightSideReplaced, IdentifierType.BOOLEAN_EXPRESSION, identifier, assignValueType);
                    }

                }
                case MATRIX_EXPRESSION: {

                    // Fall: Matrizenausdruck.
                    AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, scopeMemory);
                    List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
                    String rightSideReplaced = algorithmCommandReplacementList.getSubstitutedExpression();
                    try {
                        MatrixExpression matExpr = MatrixExpression.build(rightSideReplaced, VALIDATOR, VALIDATOR);

                        // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                        checkIfAllIdentifiersAreDefined(matExpr.getContainedVars(), scopeMemory);
                        areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, matExpr.getContainedExpressionVars(), scopeMemory);
                        areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, matExpr.getContainedMatrixVars(), scopeMemory);

                        scopeMemory.getMemory().put(identifierName, identifier);
                        commands.add(new AssignValueCommand(identifier, matExpr, assignValueType));
                        return commands;
                    } catch (ExpressionException e) {
                        return parseAlgorithmCall(scopeMemory, commands, rightSideReplaced, IdentifierType.MATRIX_EXPRESSION, identifier, assignValueType);
                    }

                }
                case STRING: {

                    // Fall: (Zusammengesetzter) String.
                    AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(rightSide, scopeMemory);
                    List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
                    String rightSideReplaced = algorithmCommandReplacementList.getSubstitutedExpression();

                    Object[] stringValues = decomposeStringValueInObjects(rightSideReplaced, scopeMemory);
                    scopeMemory.getMemory().put(identifierName, identifier);
                    commands.add(new AssignValueCommand(identifier, stringValues, assignValueType));
                    return commands;

                }
                default:
                    break;
            }
        }

        throw new NotDesiredCommandException();

    }

    private static int getPositionOfDefineCharIfIsAssignValueCommandIfValid(String line) {
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
                /*
                Prüfung, ob das Zeichen an dieser Stelle nicht Teil eines Vergleichsoperators 
                ist, welcher "=" enthält. Im positiven Fall kann es keine Zuweisung mehr sein, 
                sondern höchstens eine Kontrollstruktur.
                 */
                for (ComparingOperators op : ComparingOperators.getOperatorsContainingEqualsSign()) {
                    if (line.substring(i - 1).startsWith(op.getValue())) {
                        return -1;
                    }
                }
                if (line.substring(i).startsWith(ComparingOperators.EQUALS.getValue())) {
                    return -1;
                }
                return i;
            }
        }
        return -1;
    }

    private static void checkIfAllIdentifiersAreDefined(Set<String> vars, AlgorithmMemory memory) throws ParseAssignValueException {
        for (String var : vars) {
            if (!memory.getMemory().containsKey(var)) {
                throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, var);
            }
        }
    }

    private static void areIdentifiersOfCorrectType(IdentifierType type, Set<String> vars, AlgorithmMemory memory) throws ParseAssignValueException {
        for (String var : vars) {
            if (memory.getMemory().get(var).getType() != type) {
                throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_INCOMPATIBLE_TYPES, memory.getMemory().get(var).getType(), type);
            }
        }
    }

    private static List<AlgorithmCommand> parseAlgorithmCall(AlgorithmMemory memory, List<AlgorithmCommand> commands,
            String rightSide, IdentifierType assignType, Identifier identifier, AssignValueType assignValueType) throws AlgorithmCompileException {
        // Kompatibilitätscheck
        Signature calledAlgSignature = getAlgorithmCallDataFromAlgorithmCall(rightSide, memory, assignType).getSignature();
        // Parameter auslesen;
        Identifier[] parameterIdentifiers = getParameterFromAlgorithmCall(rightSide, calledAlgSignature, commands, memory);
        memory.getMemory().put(identifier.getName(), identifier);
        commands.add(new AssignValueCommand(identifier, calledAlgSignature, parameterIdentifiers, assignValueType));
        return commands;

    }

    private static Identifier[] getParameterFromAlgorithmCall(String input, Signature calledAlgSignature, List<AlgorithmCommand> commands, AlgorithmMemory scopeMemory) throws ParseAssignValueException {
        try {
            CompilerUtils.AlgorithmParseData algParseData = CompilerUtils.getAlgorithmParseData(input);
            String[] params = algParseData.getParameters();
            Identifier[] identifiers = new Identifier[params.length];
            for (int i = 0; i < params.length; i++) {
                // 1. Fall: der Parameter ist ein Bezeichner (welcher bereits in der Memory liegt).
                if (scopeMemory.getMemory().get(params[i]) != null) {
                    identifiers[i] = scopeMemory.getMemory().get(params[i]);
                    continue;
                }
                // 2. Fall: der Parameter ist gültiger Ausdruck vom geforderten Typ.
                AbstractExpression argument = null;
                try {
                    switch (calledAlgSignature.getParameterTypes()[i]) {
                        case EXPRESSION:
                            argument = Expression.build(params[i], VALIDATOR);
                            // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                            checkIfAllIdentifiersAreDefined(argument.getContainedVars(), scopeMemory);
                            areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, argument.getContainedVars(), scopeMemory);
                            break;
                        case BOOLEAN_EXPRESSION:
                            argument = BooleanExpression.build(params[i], VALIDATOR, CompilerUtils.extractTypesOfMemory(scopeMemory));
                            // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                            // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                            checkIfAllIdentifiersAreDefined(argument.getContainedVars(), scopeMemory);
                            areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, ((BooleanExpression) argument).getContainedExpressionVars(), scopeMemory);
                            areIdentifiersOfCorrectType(IdentifierType.BOOLEAN_EXPRESSION, ((BooleanExpression) argument).getContainedBooleanVars(scopeMemory), scopeMemory);
                            areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, ((BooleanExpression) argument).getContainedMatrixVars(), scopeMemory);
                            break;
                        case MATRIX_EXPRESSION:
                            argument = MatrixExpression.build(params[i], VALIDATOR, VALIDATOR);
                            // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                            checkIfAllIdentifiersAreDefined(((MatrixExpression) argument).getContainedVars(), scopeMemory);
                            areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, ((MatrixExpression) argument).getContainedExpressionVars(), scopeMemory);
                            areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, ((MatrixExpression) argument).getContainedMatrixVars(), scopeMemory);
                            break;
                        default:
                            break;
                    }
                } catch (ExpressionException | BooleanExpressionException e) {
                    throw new AlgorithmCompileException(e.getMessage());
                }
                if (argument != null) {
                    String genVarName = CompilerUtils.generateTechnicalIdentifierName(scopeMemory);
                    Identifier genVarIdentifier = Identifier.createIdentifier(scopeMemory, genVarName, IdentifierType.identifierTypeOf(argument));
                    identifiers[i] = genVarIdentifier;
                    commands.add(new AssignValueCommand(genVarIdentifier, argument, AssignValueType.NEW));
                    scopeMemory.addToMemoryInCompileTime(genVarIdentifier);
                } else {
                    throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, params[i]);
                }
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
                            throw new ParseAssignValueException(eMatExpr.getMessage());
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
                throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, algName);
            }

            // Prüfung, ob Rückgabewert korrekt ist.
            if (!algorithmCandidate.getReturnType().equals(returnType)) {
                // TODO: Fehlermeldung korrigieren.
                throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_WRONG_RETURN_TYPE);
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String booleanConditionString = line.substring((Keyword.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);

        // Die boolsche Bedingung kann wieder Algorithmenaufrufe enthalten. Daher muss sie in "elementare" Teile zerlegt werden.
        BooleanExpression condition;
        AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(booleanConditionString, memory);
        List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
        String booleanConditionReplaced = algorithmCommandReplacementList.getSubstitutedExpression();

        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        condition = BooleanExpression.build(booleanConditionReplaced, VALIDATOR, typesMap);
        checkIfAllIdentifiersAreDefined(condition.getContainedVars(), memory);

        // Prüfung, ob line mit "if(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(ReservedChars.BEGIN.getStringValue())
                || !line.contains(ReservedChars.END.getStringValue())
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_KEYWORD_EXPECTED, Keyword.ELSE.getValue());
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }
        if (endBlockPosition != restLine.length() - 1) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, line.substring(endBlockPosition + 1));
        }

        List<AlgorithmCommand> commandsElsePart;
        if (keywordsAllowed) {
            commandsElsePart = parseConnectedBlockWithKeywords(restLine.substring(beginBlockPosition, endBlockPosition), memoryBeforeIfElsePart, alg);
        } else {
            commandsElsePart = parseConnectedBlockWithoutKeywords(restLine.substring(beginBlockPosition, endBlockPosition), memoryBeforeIfElsePart, alg);
        }
        ifElseControlStructure.setCommandsElsePart(commandsElsePart);

        commands.add(ifElseControlStructure);
        return commands;
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String booleanConditionString = line.substring((Keyword.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);

        // Die boolsche Bedingung kann wieder Algorithmenaufrufe enthalten. Daher muss sie in "elementare" Teile zerlegt werden.
        BooleanExpression condition;
        AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(booleanConditionString, memory);
        List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
        String booleanConditionReplaced = algorithmCommandReplacementList.getSubstitutedExpression();

        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        condition = BooleanExpression.build(booleanConditionReplaced, VALIDATOR, typesMap);
        checkIfAllIdentifiersAreDefined(condition.getContainedVars(), memory);

        // Prüfung, ob line mit "while(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(ReservedChars.BEGIN.getStringValue())
                || !line.contains(ReservedChars.END.getStringValue())
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }

        AlgorithmMemory memoryBeforWhileLoop = memory.copyMemory();

        List<AlgorithmCommand> commandsWhilePart = parseConnectedBlockWithKeywords(line.substring(beginBlockPosition, endBlockPosition), memoryBeforWhileLoop, alg);
        WhileControlStructure whileControlStructure = new WhileControlStructure(condition, commandsWhilePart);

        // '}' muss als letztes Zeichen stehen, sonst ist die Struktur nicht korrekt.
        if (endBlockPosition == line.length() - 1) {
            whileControlStructure.getCommands().addAll(commands);
            commands.add(whileControlStructure);
            return commands;
        }

        throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, line.substring(endBlockPosition + 1));
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }

        AlgorithmMemory memoryBeforWhileLoop = memory.copyMemory();

        List<AlgorithmCommand> commandsDoPart = parseConnectedBlockWithKeywords(line.substring(beginBlockPosition, endBlockPosition), memoryBeforWhileLoop, alg);

        // While-Bedingung kompilieren
        String whilePart = line.substring(endBlockPosition + 1);
        if (!whilePart.startsWith(Keyword.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_KEYWORD_EXPECTED, Keyword.WHILE.getValue());
        }
        if (!whilePart.endsWith(String.valueOf(ReservedChars.CLOSE_BRACKET.getValue()))) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String whileConditionString = line.substring(endBlockPosition + Keyword.WHILE.getValue().length() + 2, line.length() - 1);

        // Die boolsche Bedingung kann wieder Algorithmenaufrufe enthalten. Daher muss sie in "elementare" Teile zerlegt werden.
        BooleanExpression condition;
        AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(whileConditionString, memory);
        List<AlgorithmCommand> commands = algorithmCommandReplacementList.getCommands();
        String booleanConditionReplaced = algorithmCommandReplacementList.getSubstitutedExpression();

        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(memory);
        condition = BooleanExpression.build(booleanConditionReplaced, VALIDATOR, typesMap);
        checkIfAllIdentifiersAreDefined(condition.getContainedVars(), memory);

        DoWhileControlStructure doWhileControlStructure = new DoWhileControlStructure(commandsDoPart, condition);
        doWhileControlStructure.getCommands().addAll(commands);
        commands.add(doWhileControlStructure);
        return commands;
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String forControlString = line.substring((Keyword.FOR.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfForControlPart);

        AlgorithmMemory currentMemory = memory.copyMemory();

        // Die drei for-Anweisungen kompilieren.
        String[] forControlParts = CompilerUtils.splitByKomma(forControlString);

        // Es müssen genau 3 Befehle in der For-Struktur stehen.
        if (forControlParts.length < 3) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_EXPECTED, ReservedChars.ARGUMENT_SEPARATOR.getValue());
        }
        if (forControlParts.length > 3) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
        }

        List<AlgorithmCommand> initialization = parseAssignValueCommand(forControlParts[0], currentMemory);

        // Die boolsche Bedingung kann wieder Algorithmenaufrufe enthalten. Daher muss sie in "elementare" Teile zerlegt werden.
        BooleanExpression endLoopCondition;
        AlgorithmCommandReplacementList algorithmCommandReplacementList = decomposeAbstractExpressionInvolvingAlgorithmCalls(forControlParts[1], currentMemory);
        List<AlgorithmCommand> commandsEndLoopCondition = algorithmCommandReplacementList.getCommands();
        String booleanConditionReplaced = algorithmCommandReplacementList.getSubstitutedExpression();

        Map<String, IdentifierType> typesMap = CompilerUtils.extractTypesOfMemory(currentMemory);
        endLoopCondition = BooleanExpression.build(booleanConditionReplaced, VALIDATOR, typesMap);
        checkIfAllIdentifiersAreDefined(endLoopCondition.getContainedVars(), currentMemory);

        AlgorithmMemory memoryBeforeLoop = currentMemory.copyMemory();

        List<AlgorithmCommand> loopAssignment = parseAssignValueCommand(forControlParts[2], currentMemory);
        // Prüfung, ob bei loopAssignment keine weiteren Bezeichner hinzukamen, außer den Technischen.
        checkIfNewIdentifierOccur(memoryBeforeLoop, currentMemory);

        // Prüfung, ob line mit "for(a,b,c){ ..." beginnt.
        if (!line.contains(ReservedChars.BEGIN.getStringValue())
                || !line.contains(ReservedChars.END.getStringValue())
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfForControlPart + 1) {
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_CONTROL_STRUCTURE_MUST_CONTAIN_BEGIN_AND_END,
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
            throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }
        List<AlgorithmCommand> commandsForPart = parseConnectedBlockWithKeywords(line.substring(beginBlockPosition, endBlockPosition), currentMemory, alg);
        ForControlStructure forControlStructure = new ForControlStructure(commandsForPart, initialization, commandsEndLoopCondition, endLoopCondition, loopAssignment);

        // Lokale Variable aus dem Speicher memory wieder herausnehmen.
        // '}' muss als letztes Zeichen stehen, sonst ist die Struktur nicht korrekt.
        if (endBlockPosition == line.length() - 1) {
            return Collections.singletonList(forControlStructure);
        }

        throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, line.substring(endBlockPosition + 1));
    }

    private static void checkIfNewIdentifierOccur(AlgorithmMemory memoryBeforeLoop, AlgorithmMemory currentMemory) throws ParseControlStructureException {
        for (String identifierName : currentMemory.getMemory().keySet()) {
            if (!memoryBeforeLoop.getMemory().containsKey(identifierName) && !CompilerUtils.isTechnicalIdentifierName(identifierName)) {
                throw new ParseControlStructureException(AlgorithmCompileExceptionIds.AC_CONTROL_STRUCTURE_FOR_NEW_IDENTIFIER_NOT_ALLOWED, identifierName);
            }
        }
    }

    private static List<AlgorithmCommand> parseKeywordCommand(String line, boolean keywordsAllowed) throws AlgorithmCompileException, NotDesiredCommandException {
        if (line.equals(Keyword.BREAK.toString())) {
            if (keywordsAllowed) {
                return Collections.singletonList(new KeywordCommand(Keyword.BREAK));
            }
            throw new ParseKeywordException(AlgorithmCompileExceptionIds.AC_KEYWORD_NOT_ALLOWED_HERE, Keyword.BREAK);
        }
        if (line.equals(Keyword.CONTINUE.toString())) {
            if (keywordsAllowed) {
                return Collections.singletonList(new KeywordCommand(Keyword.CONTINUE));
            }
            throw new ParseKeywordException(AlgorithmCompileExceptionIds.AC_KEYWORD_NOT_ALLOWED_HERE, Keyword.CONTINUE);
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
                String returnValueReplaced = algorithmCommandReplacementList.getSubstitutedExpression();

                if (scopeMemory.getMemory().get(returnValueReplaced) != null) {
                    return Collections.singletonList((AlgorithmCommand) new ReturnCommand(scopeMemory.getMemory().get(returnValueCandidate)));
                }
                if (VALIDATOR.isValidIdentifier(returnValueReplaced)) {
                    throw new ParseReturnException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, returnValueCandidate);
                }
                String genVarForReturn = CompilerUtils.generateTechnicalIdentifierName(scopeMemory);
                String assignValueCommand = alg.getReturnType().toString() + " " + genVarForReturn + "=" + returnValueReplaced;
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
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_MISSING_LINE_SEPARATOR, ReservedChars.LINE_SEPARATOR.getValue());
        }

        AlgorithmMemory memoryBeforeBlockBeginning;
        if (connectedBlock) {
            memoryBeforeBlockBeginning = memory.copyMemory();
        } else {
            memoryBeforeBlockBeginning = memory;
        }

        List<String> linesAsList = new ArrayList<>();
        int wavedBracketCounter = 0;
        int bracketCounter = 0;
        int squareBracketCounter = 0;
        int beginBlockPosition = 0;
        int endBlockPosition = -1;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.BEGIN.getValue()) {
                wavedBracketCounter++;
            } else if (input.charAt(i) == ReservedChars.END.getValue()) {
                wavedBracketCounter--;
            } else if (input.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
            } else if (input.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
            } else if (input.charAt(i) == ReservedChars.OPEN_SQUARE_BRACKET.getValue()) {
                squareBracketCounter++;
            } else if (input.charAt(i) == ReservedChars.CLOSE_SQUARE_BRACKET.getValue()) {
                squareBracketCounter--;
            }
            if (wavedBracketCounter == 0 && squareBracketCounter == 0 && input.charAt(i) == ReservedChars.LINE_SEPARATOR.getValue()) {
                endBlockPosition = i;
                linesAsList.add(input.substring(beginBlockPosition, endBlockPosition));
                beginBlockPosition = i + 1;
            }
        }
        if (wavedBracketCounter > 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END);
        }
        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET);
        }
        if (squareBracketCounter > 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_SQUARE_BRACKET);
        }
        if (endBlockPosition != input.length() - 1) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, input.substring(endBlockPosition));
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

    ///////////////////// Methoden für die Zerlegung eines Strings ///////////////////////
    private static Object[] decomposeStringValueInObjects(String input, AlgorithmMemory scopeMemory) throws AlgorithmCompileException {
        List<String> stringValuesAsStrings = decomposeByConcat(input);
        List stringValues = new ArrayList();
        for (String s : stringValuesAsStrings) {
            if (isValidString(s)) {
                stringValues.add(s.substring(1, s.length() - 1));
            } else {
                AbstractExpression abstrExpr = null;
                try {
                    abstrExpr = Expression.build(s, VALIDATOR);
                    // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                    checkIfAllIdentifiersAreDefined(abstrExpr.getContainedVars(), scopeMemory);
                    areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, abstrExpr.getContainedVars(), scopeMemory);
                    stringValues.add(abstrExpr);
                    continue;
                } catch (ExpressionException e) {
                }
                try {
                    abstrExpr = BooleanExpression.build(s, VALIDATOR, CompilerUtils.extractTypesOfMemory(scopeMemory));
                    // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                    checkIfAllIdentifiersAreDefined(abstrExpr.getContainedVars(), scopeMemory);
                    areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, ((BooleanExpression) abstrExpr).getContainedExpressionVars(), scopeMemory);
                    areIdentifiersOfCorrectType(IdentifierType.BOOLEAN_EXPRESSION, ((BooleanExpression) abstrExpr).getContainedBooleanVars(scopeMemory), scopeMemory);
                    areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, ((BooleanExpression) abstrExpr).getContainedMatrixVars(), scopeMemory);
                    stringValues.add(abstrExpr);
                    continue;
                } catch (BooleanExpressionException e) {
                }
                try {
                    abstrExpr = MatrixExpression.build(s, VALIDATOR, VALIDATOR);
                    // Prüfung auf Wohldefiniertheit aller auftretenden Bezeichner.
                    checkIfAllIdentifiersAreDefined(abstrExpr.getContainedVars(), scopeMemory);
                    areIdentifiersOfCorrectType(IdentifierType.EXPRESSION, ((MatrixExpression) abstrExpr).getContainedExpressionVars(), scopeMemory);
                    areIdentifiersOfCorrectType(IdentifierType.MATRIX_EXPRESSION, ((MatrixExpression) abstrExpr).getContainedMatrixVars(), scopeMemory);
                    stringValues.add(abstrExpr);
                    continue;
                } catch (ExpressionException e) {
                }
                if (abstrExpr == null) {
                    throw new ParseAssignValueException(AlgorithmCompileExceptionIds.AC_NOT_A_VALID_STRING, s);
                }
            }
        }

        return stringValues.toArray();
    }

    private static List<String> decomposeByConcat(String input) throws AlgorithmCompileException {
        List<String> stringValues = new ArrayList<>();

        int bracketCounter = 0;
        int beginBlockPosition = 0;
        int endBlockPosition = -1;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
            } else if (input.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
            }
            if (bracketCounter == 0) {
                if (Operators.CONCAT.getValue().equals(String.valueOf(input.charAt(i)))) {
                    endBlockPosition = i;
                    stringValues.add(input.substring(beginBlockPosition, endBlockPosition));
                    beginBlockPosition = i + 1;
                } else if (i == input.length() - 1) {
                    endBlockPosition = input.length();
                    stringValues.add(input.substring(beginBlockPosition, endBlockPosition));
                    beginBlockPosition = i + 1;
                }
            }
        }
        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET);
        }
        if (endBlockPosition != input.length()) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, input.substring(endBlockPosition));
        }

        return stringValues;
    }

    private static boolean isValidString(String input) {
        return input.startsWith(ReservedChars.STRING_DELIMITER.getStringValue())
                && input.endsWith(ReservedChars.STRING_DELIMITER.getStringValue())
                && input.replaceAll(ReservedChars.STRING_DELIMITER.getStringValue(), "").length() == input.length() - 2;
    }

    ///////////////////// Methoden für die Zerlegung eines Ausdrucks, welcher Algorithmenaufrufe enthält, in mehrere Befehle ///////////////////////
    private static AlgorithmCommandReplacementList decomposeAbstractExpressionInvolvingAlgorithmCalls(String input, AlgorithmMemory memory) {
        String inputWithGeneratedVars = input;
        List<AlgorithmCommand> commands = new ArrayList<>();

        boolean algorithmCallFound;
        String algorithmCallAsString;
        int beginningAlgCall;
        int endingAlgCall;
        do {
            algorithmCallFound = false;
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
                            return new AlgorithmCommandReplacementList(new ArrayList<>(), input);
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
                        algorithmCallFound = true;

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
                String genVarName = CompilerUtils.generateTechnicalIdentifierName(scopeMemory);
                try {
                    Identifier genVarIdentifier = Identifier.createIdentifier(scopeMemory, genVarName, IdentifierType.identifierTypeOf(value));
                    inputParameters[i] = genVarIdentifier;
                    commands.add(new AssignValueCommand(genVarIdentifier, value, AssignValueType.NEW));
                    scopeMemory.addToMemoryInCompileTime(genVarIdentifier);
                } catch (AlgorithmCompileException e) {
                    throw new ParseAssignValueException(e);
                }
            }
        }

        String genVarNameForCalledAlg = CompilerUtils.generateTechnicalIdentifierName(scopeMemory);
        Identifier identifierForCalledAlg = Identifier.createIdentifier(scopeMemory, genVarNameForCalledAlg, algorithmCallData.getSignature().getReturnType());
        try {
            commands.add(new AssignValueCommand(identifierForCalledAlg, algorithmCallData.getSignature(), inputParameters, AssignValueType.NEW));
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
        private final String substitutedExpression;

        public AlgorithmCommandReplacementList(List<AlgorithmCommand> commands, String rightSide) {
            this.commands = commands;
            this.substitutedExpression = rightSide;
        }

        public List<AlgorithmCommand> getCommands() {
            return commands;
        }

        public String getSubstitutedExpression() {
            return substitutedExpression;
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
