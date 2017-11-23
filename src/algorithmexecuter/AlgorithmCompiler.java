package algorithmexecuter;

import abstractexpressions.interfaces.IdentifierValidator;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.model.command.AlgorithmCommand;
import algorithmexecuter.model.command.AssignValueCommand;
import algorithmexecuter.model.command.ControlStructure;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.exceptions.constants.AlgorithmCompileExceptionIds;
import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.model.AlgorithmSignatureStorage;
import algorithmexecuter.model.AlgorithmStorage;
import algorithmexecuter.model.Signature;
import algorithmexecuter.model.command.ForControlStructure;
import java.util.Arrays;
import java.util.List;

public abstract class AlgorithmCompiler {

    public final static IdentifierValidator VALIDATOR = new IdentifierValidatorImpl();

    public final static AlgorithmStorage ALGORITHMS = new AlgorithmStorage();

    protected final static AlgorithmSignatureStorage ALGORITHM_SIGNATURES = new AlgorithmSignatureStorage();

    private static void parseAlgorithmSignatures(String input) throws AlgorithmCompileException {
        ALGORITHM_SIGNATURES.clearAlgorithmSignatureStorage();

        if (input.isEmpty()) {
            return;
        }

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
                ALGORITHM_SIGNATURES.add(parseAlgorithmSignature(input.substring(lastEndOfAlgorithm + 1, i + 1)));
                beginPassed = false;
                lastEndOfAlgorithm = i;
            }
        }

        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }
        if (bracketCounter < 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.BEGIN.getValue());
        }

        // Prüfung, ob die Signatur des Main-Algorithmus existiert.
        CompilerUtils.checkIfMainAlgorithmSignatureExists(ALGORITHM_SIGNATURES);
        // Prüfung, ob die Signatur ein Main-Algorithmus parameterlos ist.
        CompilerUtils.checkIfMainAlgorithmSignatureContainsNoParameters(CompilerUtils.getMainAlgorithmSignature(ALGORITHM_SIGNATURES));

    }

    private static Signature parseAlgorithmSignature(String input) throws AlgorithmCompileException {

        int indexBeginParameters = input.indexOf(ReservedChars.OPEN_BRACKET.getValue());
        if (indexBeginParameters < 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_FILE_MUST_CONTAIN_A_BEGIN);
        }
        if (input.indexOf(ReservedChars.CLOSE_BRACKET.getValue()) < 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_FILE_MUST_CONTAIN_AN_END);
        }
        if (indexBeginParameters > input.indexOf(ReservedChars.CLOSE_BRACKET.getValue())) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_END_BEFORE_BEGIN);
        }

        // Rückgabewert ermitteln (führende Leerzeichen existieren nicht).
        IdentifierType returnType = CompilerUtils.getReturnTypeFromAlgorithmDeclaration(input);
        // Signatur ermitteln.
        if (returnType != null) {
            input = input.substring(returnType.toString().length());
        }
        String candidateForSignature = input.substring(0, input.indexOf(ReservedChars.BEGIN.getValue()));
        CompilerUtils.AlgorithmParseData algParseData = CompilerUtils.getAlgorithmParseData(candidateForSignature);
        String algName = algParseData.getName();
        String[] parametersAsStrings = algParseData.getParameters();

        Identifier[] parameters = getIdentifiersFromParameterStrings(parametersAsStrings, new AlgorithmMemory(null));

        // Prüfung, ob Algorithmusparameter nicht doppelt vorkommen.
        checkForTwiceOccurringParameters(parameters);

        Signature signature = CompilerUtils.getSignature(returnType, algName, parameters);

        // Falls ein Algorithmus mit derselben Signatur bereits vorhanden ist, Fehler werfen.
        if (containsAlgorithmWithSameSignature(signature)) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_ALGORITHM_ALREADY_EXISTS, signature);
        }

        return signature;

    }

    public static void parseAlgorithmFile(String input) throws AlgorithmCompileException {
        ALGORITHMS.clearAlgorithmStorage();

        if (input.isEmpty()) {
            return;
        }

        // Vorformatierung.
        input = CompilerUtils.preprocessAlgorithm(input);

        /* 
        Sämtliche Signaturen ermitteln, damit alle vorhandenen Algorithmennamen 
        bekannt sind, auch wenn diese Compilerfehler enthalten.
         */
        parseAlgorithmSignatures(input);

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
            if (bracketCounter == 0 && beginPassed || i == input.length() - 1) {
                ALGORITHMS.add(parseAlgorithm(input.substring(lastEndOfAlgorithm + 1, i + 1)));
                beginPassed = false;
                lastEndOfAlgorithm = i;
            }
        }
        
        if (bracketCounter > 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.END.getValue());
        }
        if (bracketCounter < 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_BRACKET_EXPECTED, ReservedChars.BEGIN.getValue());
        }

        // Prüfung, ob ein Main-Algorithmus existiert.
        CompilerUtils.checkIfMainAlgorithmExists(ALGORITHMS);
        // Prüfung, ob ein Main-Algorithmus parameterlos ist.
        CompilerUtils.checkIfMainAlgorithmContainsNoParameters(CompilerUtils.getMainAlgorithm(ALGORITHMS));
        // Zum Schluss: bei Bezeichnerzuordnungen Algorithmensignaturen durch Algorithmenreferenzen ersetzen.
        replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands();
    }

    private static Algorithm parseAlgorithm(String input) throws AlgorithmCompileException {

        int indexBeginParameters = input.indexOf(ReservedChars.OPEN_BRACKET.getValue());
        if (indexBeginParameters < 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_FILE_MUST_CONTAIN_A_BEGIN);
        }
        if (input.indexOf(ReservedChars.CLOSE_BRACKET.getValue()) < 0) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_FILE_MUST_CONTAIN_AN_END);
        }
        if (indexBeginParameters > input.indexOf(ReservedChars.CLOSE_BRACKET.getValue())) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_END_BEFORE_BEGIN);
        }

        // Rückgabewert ermitteln (führende Leerzeichen existieren nicht).
        IdentifierType returnType = CompilerUtils.getReturnTypeFromAlgorithmDeclaration(input);
        // Signatur ermitteln.
        if (returnType != null) {
            input = input.substring(returnType.toString().length());
        }
        String candidateForSignature = input.substring(0, input.indexOf(ReservedChars.BEGIN.getValue()));
        CompilerUtils.AlgorithmParseData algParseData = CompilerUtils.getAlgorithmParseData(candidateForSignature);
        String algName = algParseData.getName();
        String[] parametersAsStrings = algParseData.getParameters();

        AlgorithmMemory memory = new AlgorithmMemory(null);

        Identifier[] parameters = getIdentifiersFromParameterStrings(parametersAsStrings, memory);

        Signature signature = CompilerUtils.getSignature(returnType, algName, parameters);

        // Falls ein Algorithmus mit derselben Signatur bereits vorhanden ist, Fehler werfen.
        if (containsAlgorithmWithSameSignature(signature)) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_ALGORITHM_ALREADY_EXISTS, signature);
        }

        // Algorithmusparameter zum Variablenpool hinzufügen.
        addParametersToMemoryInCompileTime(parameters, memory);

        Algorithm alg = new Algorithm(algName, parameters, returnType);
        memory.setAlgorithm(alg);

        int indexEndParameters = input.indexOf(ReservedChars.CLOSE_BRACKET.getValue());

        /* 
        Algorithmusnamen und Parameter inkl. Klammern beseitigen. Es bleibt nur 
        noch ein String der Form "{...;...;...}" übrig.
         */
        input = input.substring(indexEndParameters + 1, input.length());
        // input muss mit "{" beginnen und auf "}" enden.
        if (!input.startsWith(String.valueOf(ReservedChars.BEGIN.getValue()))) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_ALGORITHM_MUST_START_WITH_BEGIN);
        }
        if (!input.endsWith(String.valueOf(ReservedChars.END.getValue()))) {
            throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_ALGORITHM_MUST_END_WITH_END);
        }
        // Öffnende {-Klammer und schließende }-Klammer am Anfang und am Ende beseitigen.
        input = input.substring(1, input.length() - 1);

        input = putSeparatorAfterBlockEnding(input);

        if (!input.isEmpty()) {
            // Alle Zeilen innerhalb des Algorithmus kompilieren.
            List<AlgorithmCommand> commands = AlgorithmCommandCompiler.parseConnectedBlockWithoutKeywords(input, memory, alg);
            // Allen Befehlen den aktuellen Algorithmus alg zuordnen.
            alg.appendCommands(commands);
        }

        // Plausibilitätschecks.
        checkAlgorithmForPlausibility(alg);

        return alg;
    }

    private static void replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands() {
        for (Algorithm alg : ALGORITHMS.getAlgorithmStorage()) {
            replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands(alg.getCommands());
        }
    }

    private static void checkForTwiceOccurringParameters(Identifier[] parameter) throws AlgorithmCompileException {
        for (int i = 0; i < parameter.length; i++) {
            for (int j = i + 1; j < parameter.length; j++) {
                if (parameter[i].getName().equals(parameter[j].getName())) {
                    throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_IDENTIFIER_ALREADY_DEFINED, parameter[i].getName());
                }
            }
        }
    }

    private static Identifier[] getIdentifiersFromParameterStrings(String[] parameterStrings, AlgorithmMemory memory) throws AlgorithmCompileException {
        Identifier[] resultIdentifiers = new Identifier[parameterStrings.length];
        IdentifierType parameterType;
        String parameterName;
        for (int i = 0; i < parameterStrings.length; i++) {

            parameterType = null;
            for (IdentifierType type : IdentifierType.values()) {
                if (parameterStrings[i].startsWith(type.toString() + " ")) {
                    parameterType = type;
                    break;
                }
            }
            if (parameterType == null) {
                throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_CANNOT_FIND_SYMBOL, parameterStrings[i]);
            }
            parameterName = parameterStrings[i].substring((parameterType.toString() + " ").length());

            // Validierung des Parameternamen.
            if (!VALIDATOR.isValidIdentifier(parameterName)) {
                throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_ILLEGAL_CHARACTER, parameterName);
            }
            // Prüfung auf doppelte Deklaration.
            if (memory.containsIdentifier(parameterName)) {
                throw new AlgorithmCompileException(AlgorithmCompileExceptionIds.AC_IDENTIFIER_ALREADY_DEFINED, parameterName);
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

    private static boolean containsAlgorithmWithSameSignature(Signature signature) {
        Signature algSignature;
        for (Algorithm alg : ALGORITHMS.getAlgorithmStorage()) {
            algSignature = CompilerUtils.getSignature(alg.getReturnType(), alg.getName(), alg.getInputParameters());
            if (algSignature.getName().equals(signature.getName())
                    && Arrays.deepEquals(algSignature.getParameterTypes(), signature.getParameterTypes())) {
                return true;
            }
        }
        return false;
    }

    private static String putSeparatorAfterBlockEnding(String input) {
        String inputWithSeparators = input.replaceAll("\\}", "\\};");
        // Ausnahme: if (...) {...} else {...}: Semikolon zwischen dem if- und dem else-Block entfernen.
        inputWithSeparators = inputWithSeparators.replaceAll("\\}" + String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()) + Keyword.ELSE.getValue(), "\\}" + Keyword.ELSE.getValue());
        // Ausnahme: do {...} while (...): Semikolon zwischen dem do-Block und dem while entfernen.
        inputWithSeparators = inputWithSeparators.replaceAll("\\}" + String.valueOf(ReservedChars.LINE_SEPARATOR.getValue()) + Keyword.WHILE.getValue(), "\\}" + Keyword.WHILE.getValue());
        return inputWithSeparators;
    }

    private static void checkAlgorithmForPlausibility(Algorithm alg) throws AlgorithmCompileException {
        // Prüfung, ob der Main-Algorithmen keine Parameter enthält.
        checkIfMainAlgorithmContainsNoParameters(alg);
        // Prüfung, ob es bei Void-Algorithmen keine zurückgegebenen Objekte gibt.
        checkIfVoidAlgorithmContainsOnlyAtMostSimpleReturns(alg);
        // Prüfung, ob es bei Algorithmen mit Rückgabewerten immer Rückgaben mit korrektem Typ gibt.
        checkIfNonVoidAlgorithmContainsAlwaysReturnsWithCorrectReturnType(alg);
        // Prüfung, ob alle eingeführten Bezeichner auch initialisiert wurden.
        checkIfAllIdentifierAreInitialized(alg);
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
        // Prüfung, ob Wertrückgabe immer erfolgt.
        CompilerUtils.checkForContainingReturnCommand(alg.getCommands(), alg.getReturnType());
        // Prüfung auf korrekten Rückgabewert.
        CompilerUtils.checkForCorrectReturnType(alg.getCommands(), alg.getReturnType());
    }

    private static void checkIfAlgorithmContainsNoDeadCode(Algorithm alg) throws AlgorithmCompileException {
        CompilerUtils.checkForUnreachableCodeInBlock(alg.getCommands(), alg);
    }

    private static void checkIfAllIdentifierAreInitialized(Algorithm alg) throws AlgorithmCompileException {
        CompilerUtils.checkIfAllIdentifierAreInitialized(alg.getCommands(), alg);
    }

    private static void replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands(List<AlgorithmCommand> commands) {
        AssignValueCommand assignValueCommand;
        for (AlgorithmCommand command : commands) {
            if (command.isAssignValueCommand() && ((AssignValueCommand) command).getTargetAlgorithmSignature() != null) {
                assignValueCommand = (AssignValueCommand) command;
                Signature signature = assignValueCommand.getTargetAlgorithmSignature();
                Algorithm calledAlg = null;
                for (Algorithm alg : ALGORITHMS.getAlgorithmStorage()) {
                    if (alg.getSignature().equals(signature)) {
                        calledAlg = alg;
                        break;
                    }
                }
                // Ab hier ist calledAlg != null (dies wurde durch andere, vorherige Prüfungen sichergestellt).
                assignValueCommand.setTargetAlgorithm(calledAlg);
            } else if (command.isControlStructure()) {
                // Analoges bei allen Unterblöcken durchführen.
                for (List<AlgorithmCommand> commandBlock : ((ControlStructure) command).getCommandBlocks()) {
                    replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands(commandBlock);
                }
                if (command.isForControlStructure()) {
                    replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands(((ForControlStructure) command).getInitialization());
                    replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands(((ForControlStructure) command).getEndLoopCommands());
                    replaceAlgorithmSignaturesByAlgorithmReferencesInAssignValueCommands(((ForControlStructure) command).getLoopAssignment());
                }
            }
        }
    }

}
