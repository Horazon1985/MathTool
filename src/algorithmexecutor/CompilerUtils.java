package algorithmexecutor;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.ControlStructure;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompilerUtils {

    public static String preprocessAlgorithm(String input) {
        String outputFormatted = input;
        outputFormatted = replaceAllRepeatedly(outputFormatted, " ", "\n", "\t");
        outputFormatted = removeLeadingWhitespaces(outputFormatted);
        outputFormatted = removeEndingWhitespaces(outputFormatted);
        outputFormatted = replaceAllRepeatedly(outputFormatted, " ", "  ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, ",", ", ", " ,");
        outputFormatted = replaceAllRepeatedly(outputFormatted, ";", "; ", " ;");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "=", " =", "= ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\{", " \\{", "\\{ ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\}", " \\}", "\\} ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\(", " \\(", "\\( ");
        outputFormatted = replaceAllRepeatedly(outputFormatted, "\\)", " \\)", "\\) ");
        return outputFormatted;
    }

    private static String removeLeadingWhitespaces(String input) {
        while (input.startsWith(" ")) {
            input = input.substring(1);
        }
        while (input.endsWith(" ")) {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }

    private static String removeEndingWhitespaces(String input) {
        while (input.endsWith(" ")) {
            input = input.substring(0, input.length() - 1);
        }
        return input;
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

    public static void checkIfMainAlgorithmExists(List<Algorithm> algorithms) throws AlgorithmCompileException {
        for (Algorithm alg : algorithms) {
            if (alg.getName().equals(Keywords.MAIN.getValue())) {
                return;
            }
        }
        throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
    }

    public static Algorithm getMainAlgorithm(List<Algorithm> algorithms) throws AlgorithmCompileException {
        for (Algorithm alg : algorithms) {
            if (alg.getName().equals(Keywords.MAIN.getValue())) {
                return alg;
            }
        }
        throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
    }

    public static void checkIfMainAlgorithmContainsNoParameters(Algorithm alg) throws AlgorithmCompileException {
        if (alg.getName().equals(Keywords.MAIN.getValue()) && alg.getInputParameters().length != 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
        }
    }

    public static void checkForOnlySimpleReturns(List<AlgorithmCommand> commands) throws AlgorithmCompileException {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).isReturnCommand() && ((ReturnCommand) commands.get(i)).getIdentifier() != null) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }
            if (commands.get(i).isControlStructure()) {
                for (List<AlgorithmCommand> commandsInBlock : ((ControlStructure) commands.get(i)).getCommandBlocks()) {
                    checkForOnlySimpleReturns(commandsInBlock);
                }
            }
        }
    }

    public static void checkForCorrectReturnType(List<AlgorithmCommand> commands, IdentifierTypes returnType) throws AlgorithmCompileException {
        Identifier returnIdentifier;
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).isReturnCommand() && ((ReturnCommand) commands.get(i)).getIdentifier() != null) {
                returnIdentifier = ((ReturnCommand) commands.get(i)).getIdentifier();
                if (returnIdentifier == null) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
                }
                if (!returnType.isSameOrGeneralTypeOf(returnIdentifier.getType())) {
                    throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
                }
            }
            if (commands.get(i).isControlStructure()) {
                for (List<AlgorithmCommand> commandsInBlock : ((ControlStructure) commands.get(i)).getCommandBlocks()) {
                    checkForCorrectReturnType(commandsInBlock, returnType);
                }
            }
        }
    }

    public static void checkForUnreachableCodeInBlock(List<AlgorithmCommand> commands) throws AlgorithmCompileException {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).isReturnCommand() && i < commands.size() - 1) {
                throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
            }
            if (commands.get(i).isControlStructure()) {
                for (List<AlgorithmCommand> commandsInBlock : ((ControlStructure) commands.get(i)).getCommandBlocks()) {
                    checkForUnreachableCodeInBlock(commandsInBlock);
                }
                if (commands.get(i).isIfElseControlStructure()) {
                    if (doBothPartsContainReturnStatementInIfElseBlock((IfElseControlStructure) commands.get(i)) && i < commands.size() - 1) {
                        throw new AlgorithmCompileException(CompileExceptionTexts.AC_UNKNOWN_ERROR);
                    }
                }
            }
        }
    }

    private static boolean doBothPartsContainReturnStatementInIfElseBlock(IfElseControlStructure ifElseBlock) throws AlgorithmCompileException {
        boolean ifPartContainsReturnStatement = false;
        boolean elsePartContainsReturnStatement = false;
        for (AlgorithmCommand c : ifElseBlock.getCommandsIfPart()) {
            if (c.isReturnCommand()) {
                ifPartContainsReturnStatement = true;
                break;
            }
        }
        for (AlgorithmCommand c : ifElseBlock.getCommandsElsePart()) {
            if (c.isReturnCommand()) {
                elsePartContainsReturnStatement = true;
                break;
            }
        }
        return ifPartContainsReturnStatement && elsePartContainsReturnStatement;
    }

    public static Map<String, AbstractExpression> extractValuesOfIdentifiers(Algorithm alg) {
        Map<String, AbstractExpression> valuesMap = new HashMap<>();
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        for (String identifierName : memory.getMemory().keySet()) {
            valuesMap.put(identifierName, memory.getMemory().get(identifierName).getValue());
        }
        return valuesMap;
    }

}
