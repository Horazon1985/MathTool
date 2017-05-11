package algorithmexecutor;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.IdentifierValidator;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.command.WhileControlStructure;
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.enums.ComparingOperators;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.enums.Operators;
import algorithmexecutor.enums.ReservedChars;
import algorithmexecutor.exceptions.BlockCompileException;
import algorithmexecutor.exceptions.BooleanExpressionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.exceptions.NotDesiredCommandException;
import algorithmexecutor.exceptions.ParseAssignValueException;
import algorithmexecutor.exceptions.ParseControlStructureException;
import algorithmexecutor.exceptions.ParseReturnException;
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
        input = CompilerUtils.preprocessAlgorithm(input);

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

        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.BEGIN.getValue());
        }
        if (bracketCounter < 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }

        // Prüfung, ob ein Main-Algorithmus existiert.
        CompilerUtils.checkIfMainAlgorithmExists(STORED_ALGORITHMS);
        // Prüfung, ob ein Main-Algorithmus parameterlos ist.
        CompilerUtils.checkIfMainAlgorithmContainsNoParameters(CompilerUtils.getMainAlgorithm(STORED_ALGORITHMS));

    }

    public static Algorithm parseAlgorithm(String input) throws AlgorithmCompileException {

        int indexBeginParameters = input.indexOf(ReservedChars.OPEN_BRACKET.getValue());
        if (indexBeginParameters < 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_FILE_MUST_CONTAIN_A_BEGIN);
        }
        if (input.indexOf(ReservedChars.CLOSE_BRACKET.getValue()) < 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_FILE_MUST_CONTAIN_AN_END);
        }
        if (indexBeginParameters > input.indexOf(ReservedChars.CLOSE_BRACKET.getValue())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_END_BEFORE_BEGIN);
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

        // Signatur ermitteln.
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

        String signature = getSignature(algName, parameters);

        // Falls ein Algorithmus mit derselben Signatur bereits vorhanden ist, Fehler werfen.
        if (containsAlgorithmWithSameSignature(signature)) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_ALGORITHM_ALREADY_EXISTS, signature);
        }

        // Algorithmusparameter zum Variablenpool hinzufügen.
        addParametersToMemoryInCompileTime(parameters, memory);

        Algorithm alg = new Algorithm(algName, parameters, returnType);

        int indexEndParameters = input.indexOf(ReservedChars.CLOSE_BRACKET.getValue());

        /* 
        Algorithmusnamen und Parameter inkl. Klammern beseitigen. Es bleibt nur 
        noch ein String der Form "{...;...;...}" übrig.
         */
        input = input.substring(indexEndParameters + 1, input.length());
        // input muss mit "{" beginnen und auf "}" enden.
        if (!input.startsWith(String.valueOf(ReservedChars.BEGIN.getValue()))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_ALGORITHM_MUST_START_WITH_BEGIN);
        }
        if (!input.endsWith(String.valueOf(ReservedChars.END.getValue()))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_ALGORITHM_MUST_END_WITH_END);
        }
        // Öffnende {-Klammer und schließende }-Klammer am Anfang und am Ende beseitigen.
        input = input.substring(1, input.length() - 1);

        input = putSeparatorAfterBlockEnding(input);

        if (!input.isEmpty()) {
            // Alle Zeilen innerhalb des Algorithmus kompilieren.
            List<AlgorithmCommand> commands = parseConnectedBlock(input, memory, alg);
            // Allen Befehlen den aktuellen Algorithmus alg zuordnen.
            alg.appendCommands(commands);
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
        int i = input.indexOf(String.valueOf(ReservedChars.OPEN_BRACKET.getValue()));
        if (i == -1) {
            // Um zu verhindern, dass es eine IndexOutOfBoundsException gibt.
            i = 0;
        }
        result[0] = input.substring(0, i);

        // Wenn der Algorithmusname leer ist -> Fehler.
        if (result[0].length() == 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_ALGORITHM_HAS_NO_NAME);
        }

        // Wenn length(result[0]) > l - 2 -> Fehler (der Befehl besitzt NICHT die Form command(...)).
        if (result[0].length() > input.length() - 2) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_ALGORITHM_SIGNATURE_HAS_INCORRECT_FORM);
        }

        // Wenn am Ende nicht ")" steht.
        if (!input.substring(input.length() - 1, input.length()).equals(String.valueOf(ReservedChars.CLOSE_BRACKET.getValue()))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_ALGORITHM_SIGNATURE_MUST_END_WITH_CLOSE_BRACKET);
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

        // Falls Parameterstring leer ist -> Fertig
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
        // Jetzt werden die einzelnen Parameter ausgelesen
        for (int i = 0; i < input.length(); i++) {

            currentChar = input.substring(i, i + 1);
            if (currentChar.equals(ReservedChars.OPEN_BRACKET.getValue())) {
                bracketCounter++;
            } else if (currentChar.equals(ReservedChars.CLOSE_BRACKET.getValue())) {
                bracketCounter--;
            } else if (currentChar.equals(ReservedChars.OPEN_SQUARE_BRACKET.getValue())) {
                squareBracketCounter++;
            } else if (currentChar.equals(ReservedChars.CLOSE_SQUARE_BRACKET.getValue())) {
                squareBracketCounter--;
            }
            if (bracketCounter == 0 && squareBracketCounter == 0 && currentChar.equals(ReservedChars.ARGUMENT_SEPARATOR.getValue())) {
                if (input.substring(startPositionOfCurrentParameter, i).isEmpty()) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.AC_IDENTIFIER_EXPECTED);
                }
                resultParameters.add(input.substring(startPositionOfCurrentParameter, i));
                startPositionOfCurrentParameter = i + 1;
            }
            if (i == input.length() - 1) {
                if (startPositionOfCurrentParameter == input.length()) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.AC_IDENTIFIER_EXPECTED);
                }
                resultParameters.add(input.substring(startPositionOfCurrentParameter, input.length()));
            }

        }

        if (bracketCounter != 0 || squareBracketCounter != 0) {
            if (bracketCounter > 0) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_BRACKET.getValue());
            }
            if (bracketCounter < 0) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.OPEN_BRACKET.getValue());
            }
            if (squareBracketCounter > 0) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.CLOSE_SQUARE_BRACKET.getValue());
            }
            if (squareBracketCounter < 0) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_BRACKET_EXPECTED, ReservedChars.OPEN_SQUARE_BRACKET.getValue());
            }
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
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL, parameterStrings[i]);
            }
            parameterName = parameterStrings[i].substring((parameterType.toString() + " ").length());

            // Validierung des Parameternamen.
            if (!VALIDATOR.isValidIdentifier(parameterName)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_ILLEGAL_CHARACTER);
            }
            // Prüfung auf doppelte Deklaration.
            if (memory.containsIdentifier(parameterName)) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED);
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

    private static String getSignature(String algName, Identifier[] parameters) {
        String signature = algName + ReservedChars.OPEN_BRACKET.getValue();
        for (int i = 0; i < parameters.length; i++) {
            signature += parameters[i].getType();
            if (i < parameters.length - 1) {
                signature += ",";
            }
        }
        return signature + ReservedChars.CLOSE_BRACKET.getValue();
    }

    private static boolean containsAlgorithmWithSameSignature(String signature) {
        for (Algorithm alg : STORED_ALGORITHMS) {
            if (alg.getSignature().equals(signature)) {
                return true;
            }
        }
        return false;
    }

    private static String putSeparatorAfterBlockEnding(String input) {
        String inputWithSeparators = input.replaceAll("\\}", "\\};");
        // Ausnahme: if (...) {...} else {...}: Semikolon zwischen dem if- und dem else-Block entfernen.
        inputWithSeparators = inputWithSeparators.replaceAll("\\}" + String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()) + Keywords.ELSE.getValue(), "\\}" + Keywords.ELSE.getValue());
        // Ausnahme: do {...} while (...): Semikolon zwischen dem do-Block und dem while entfernen.
        inputWithSeparators = inputWithSeparators.replaceAll("\\}" + String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()) + Keywords.WHILE.getValue(), "\\}" + Keywords.ELSE.getValue());
        return inputWithSeparators;
    }

    private static AlgorithmCommand parseLine(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        try {
            return parseAssignValueCommand(line, memory, alg);
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

        throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseAssignValueCommand(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {
        // Erste Prüfung, ob es sich definitiv nicht um eine Zuweisung handelt.
        if (!isAssignValueCommandIfValid(line)) {
            throw new NotDesiredCommandException();
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

            identifierName = assignment[0].substring((type.toString() + " ").length(), assignment[0].length());
            // Prüfung, ob dieser Identifier gültigen Namen besitzt.
            if (!VALIDATOR.isValidIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_ILLEGAL_CHARACTER);
            }
            // Prüfung, ob dieser Identifier bereits existiert.
            if (memory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED);
            }
        } else {
            identifierName = assignment[0];
            // Prüfung, ob dieser Identifier bereits existiert.
            if (!memory.containsIdentifier(identifierName)) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
            }
            type = memory.getMemory().get(identifierName).getType();
        }

        // Rechte Seite behandeln.
        if (type == IdentifierTypes.EXPRESSION) {

            // Fall: Arithmetischer / Analytischer Ausdruck.
            try {
                Expression expr = Expression.build(assignment[1], VALIDATOR);
                Set<String> vars = expr.getContainedVars();
                checkIfAllIdentifiersAreDefined(vars, memory);
                areIdentifiersOfCorrectType(type, vars, memory);
                Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
                memory.getMemory().put(identifierName, identifier);
                return new AssignValueCommand(identifier, expr);
            } catch (ExpressionException e) {
                throw new ParseAssignValueException(e);
            }

        } else if (type == IdentifierTypes.BOOLEAN_EXPRESSION) {

            // Fall: boolscher Ausdruck.
            try {
                BooleanExpression boolExpr = BooleanExpression.build(assignment[1], VALIDATOR);
                Set<String> vars = boolExpr.getContainedVars();
                checkIfAllIdentifiersAreDefined(boolExpr.getContainedVars(), memory);
                areIdentifiersOfCorrectType(type, vars, memory);
                Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
                memory.getMemory().put(identifierName, identifier);
                return new AssignValueCommand(identifier, boolExpr);
            } catch (BooleanExpressionException e) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }

        }

        // Fall: Matrizenausdruck.
        try {
            MatrixExpression matExpr = MatrixExpression.build(assignment[1], VALIDATOR, VALIDATOR);
            checkIfAllIdentifiersAreDefined(matExpr.getContainedVars(), memory);
            areIdentifiersOfCorrectType(IdentifierTypes.EXPRESSION, matExpr.getContainedExpressionVars(), memory);
            areIdentifiersOfCorrectType(IdentifierTypes.MATRIX_EXPRESSION, matExpr.getContainedMatrixVars(), memory);
            Identifier identifier = Identifier.createIdentifier(alg, identifierName, type);
            memory.getMemory().put(identifierName, identifier);
            return new AssignValueCommand(identifier, matExpr);
        } catch (ExpressionException e) {
            throw new ParseAssignValueException(e);
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
            if (!memory.getMemory().containsKey(var) || memory.getMemory().get(var).getType() != IdentifierTypes.EXPRESSION) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_CANNOT_FIND_SYMBOL);
            }
        }
    }

    private static void areIdentifiersOfCorrectType(IdentifierTypes type, Set<String> vars, AlgorithmMemory memory) throws ParseAssignValueException {
        for (String var : vars) {
            if (memory.getMemory().get(var).getType() != type) {
                throw new ParseAssignValueException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES, memory.getMemory().get(var).getType(), type);
            }
        }
    }

    private static AlgorithmCommand parseVoidCommand(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {
        
        
        
        
        
        throw new NotDesiredCommandException();
    }

    private static AlgorithmCommand parseControllStructure(String line, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException, NotDesiredCommandException {
        // If-Else-Block
        try {
            return parseIfElseControlStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            throw e;
        } catch (BooleanExpressionException e) {
            // Es ist zwar eine If-Else-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (BlockCompileException e) {
            /* 
            Es ist zwar eine If-Else-Struktur mit korrekter Bedingung, aber der 
            innere Block kann nicht kompiliert werden.
             */
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
        }
        // For-Block
        // While-Block
        try {
            return parseWhileControlStructure(line, memory, alg);
        } catch (ParseControlStructureException e) {
            throw e;
        } catch (BooleanExpressionException e) {
            // Es ist zwar eine While-Struktur, aber die Bedingung kann nicht kompiliert werden.
            throw new AlgorithmCompileException(e);
        } catch (BlockCompileException e) {
            /* 
            Es ist zwar eine While-Struktur mit korrekter Bedingung, aber der 
            innere Block kann nicht kompiliert werden.
             */
            throw new AlgorithmCompileException(e);
        } catch (NotDesiredCommandException e) {
        }
        // Do-While-Block
        throw new NotDesiredCommandException();
    }

    private static AlgorithmCommand parseIfElseControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keywords.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new NotDesiredCommandException();
        }

        int endOfBooleanCondition = line.indexOf(ReservedChars.CLOSE_BRACKET.getValue());
        if (endOfBooleanCondition < 0) {
            throw new ParseControlStructureException(CompileExceptionTexts.AC_BRACKET_MISSING, ReservedChars.CLOSE_BRACKET.getValue());
        }

        String booleanConditionString = line.substring((Keywords.IF.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);
        BooleanExpression condition = BooleanExpression.build(booleanConditionString, VALIDATOR);

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
            return ifElseControlStructure;
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

        return ifElseControlStructure;
    }

    private static AlgorithmCommand parseWhileControlStructure(String line, AlgorithmMemory memory, Algorithm alg)
            throws AlgorithmCompileException, BooleanExpressionException, BlockCompileException, NotDesiredCommandException {

        if (!line.startsWith(Keywords.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue())) {
            throw new NotDesiredCommandException();
        }

        int endOfBooleanCondition = line.indexOf(ReservedChars.CLOSE_BRACKET.getValue());
        if (endOfBooleanCondition < 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }

        String booleanConditionString = line.substring((Keywords.WHILE.getValue() + ReservedChars.OPEN_BRACKET.getValue()).length(), endOfBooleanCondition);
        BooleanExpression condition = BooleanExpression.build(booleanConditionString, VALIDATOR);

        // Prüfung, ob line mit "while(boolsche Bedingung){ ..." beginnt.
        if (!line.contains(String.valueOf(ReservedChars.BEGIN.getValue()))
                || !line.contains(String.valueOf(ReservedChars.END.getValue()))
                || line.indexOf(ReservedChars.BEGIN.getValue()) > endOfBooleanCondition + 1) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
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
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }
        List<AlgorithmCommand> commandsWhilePart = parseConnectedBlock(line.substring(beginBlockPosition, endBlockPosition), memory, alg);
        WhileControlStructure whileControlStructure = new WhileControlStructure(condition, commandsWhilePart);

        // '}' muss als letztes Zeichen stehen, sonst ist die Struktur nicht korrekt.
        if (endBlockPosition == line.length() - 1) {
            // Kein Else-Teil vorhanden.
            return whileControlStructure;
        }

        throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
    }

    private static AlgorithmCommand parseReturnCommand(String line, AlgorithmMemory memory) throws AlgorithmCompileException, NotDesiredCommandException {
        if (line.startsWith(Keywords.RETURN.getValue() + " ")) {
            if (line.equals(Keywords.RETURN.getValue() + ReservedChars.LINE_SEPARATOR)) {
                return new ReturnCommand(null);
            }
            String returnValueCandidate = line.substring((Keywords.RETURN.getValue() + " ").length());
            if (memory.getMemory().get(returnValueCandidate) == null) {
                throw new NotDesiredCommandException();
            }
            return new ReturnCommand(memory.getMemory().get(returnValueCandidate));
        }
        throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
    }

    private static List<AlgorithmCommand> parseConnectedBlock(String input, AlgorithmMemory memory, Algorithm alg) throws AlgorithmCompileException {
        if (!input.isEmpty() && !input.endsWith(String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
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
                commands.add(parseLine(line, memory, alg));
            }
        }
        return commands;
    }

    private static void checkAlgorithmForPlausibility(Algorithm alg) throws AlgorithmCompileException {
        // Prüfung, ob der Main-Algorithmen keine Parameter enthält.
        checkIfMainAlgorithmContainsNoParameters(alg);
        // Prüfung, ob es bei Void-Algorithmen keine zurückgegebenen Objekte gibt.
        checkIfVoidAlgorithmContainsOnlyAtMostSimpleReturns(alg);
        // Prüfung, ob es bei Algorithmen mit Rückgabewerten immer Rückgaben mit korrektem Typ gibt.
        checkIfNonVoidAlgorithmContainsAlwaysReturnsWithCorrectReturnType(alg);
        // Prüfung, ob es bei (beliebigen) Algorithmen keinen Code hinter einem Return gibt.
        checkIfAlgorithmContainsNoDeadCode(alg);
    }

    private static void checkIfMainAlgorithmContainsNoParameters(Algorithm alg) throws AlgorithmCompileException {
        CompilerUtils.checkIfMainAlgorithmContainsNoParameters(alg);
    }

    private static void checkIfVoidAlgorithmContainsOnlyAtMostSimpleReturns(Algorithm alg) throws AlgorithmCompileException {
        if (alg.getReturnType() == null) {
            CompilerUtils.checkForOnlySimpleReturns(alg.getCommands());
        }
    }

    private static void checkIfNonVoidAlgorithmContainsAlwaysReturnsWithCorrectReturnType(Algorithm alg) throws AlgorithmCompileException {
        CompilerUtils.checkForCorrectReturnType(alg.getCommands(), alg.getReturnType());
    }

    private static void checkIfAlgorithmContainsNoDeadCode(Algorithm alg) throws AlgorithmCompileException {
        CompilerUtils.checkForUnreachableCodeInBlock(alg.getCommands());
    }

}
