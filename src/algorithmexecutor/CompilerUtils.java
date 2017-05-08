package algorithmexecutor;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.command.WhileControlStructure;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompilerUtils {

    public static String preprocessAlgorithm(String input) {
        String outputFormatted = input;

        outputFormatted = replaceAllRepeatedly(outputFormatted, " ", "\n");
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
        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }

    public static Algorithm getMainAlgorithm(List<Algorithm> algorithms) throws AlgorithmCompileException {
        for (Algorithm alg : algorithms) {
            if (alg.getName().equals(Keywords.MAIN.getValue())) {
                return alg;
            }
        }
        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
    }
    
    public static void checkIfMainAlgorithmContainsNoParameters(Algorithm alg) throws AlgorithmCompileException {
        if (alg.getName().equals(Keywords.MAIN.getValue()) && alg.getInputParameters().length != 0) {
            throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
        }
    }

    public static void checkForUnreachableCodeInBlock(List<AlgorithmCommand> commands) throws AlgorithmCompileException {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).isReturnCommand() && i < commands.size() - 1) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            if (commands.get(i).isControlStructure()) {
                // If-Else-Kontrollstruktur
                if (commands.get(i).isIfElseControlStructure()) {
                    checkForUnreachableCodeInIfElseBlock((IfElseControlStructure) commands.get(i));
                    if (doBothPartsContainReturnStatementInIfElseBlock((IfElseControlStructure) commands.get(i)) && i < commands.size() - 1) {
                        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                    }
                } else if (commands.get(i).isWhileControlStructure()) {
                    checkForUnreachableCodeInBlock(((WhileControlStructure) commands.get(i)).getCommands());
                }
                // TO DO: Restliche Kontrollstrukturen.
            }
        }
    }

    private static void checkForUnreachableCodeInIfElseBlock(IfElseControlStructure ifElseBlock) throws AlgorithmCompileException {
        checkForUnreachableCodeInBlock(ifElseBlock.getCommandsIfPart());
        checkForUnreachableCodeInBlock(ifElseBlock.getCommandsElsePart());
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

    public static void checkIfNonVoidAlgorithmContainsAlwaysReturnsWithCorrectReturnType(Algorithm alg) throws AlgorithmCompileException {

    }

}
