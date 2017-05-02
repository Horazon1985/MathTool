package algorithmexecutor;

import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import java.util.List;

public class CompilerUtils {

    public static void checkForUnreachableCodeInBlock(List<AlgorithmCommand> commands) throws AlgorithmCompileException {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).isReturnCommand() && i < commands.size() - 1) {
                throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
            }
            if (commands.get(i).isControllStructure()) {
                // If-Else-Kontrollstruktur
                if (commands.get(i).isIfElseControllStructure()) {
                    checkForUnreachableCodeInIfElseBlock((IfElseControlStructure) commands.get(i));
                    if (doBothPartsContainReturnStatementInIfElseBlock((IfElseControlStructure) commands.get(i)) && i < commands.size() - 1) {
                        throw new AlgorithmCompileException(CompileExceptionTexts.UNKNOWN_ERROR);
                    }
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

}
