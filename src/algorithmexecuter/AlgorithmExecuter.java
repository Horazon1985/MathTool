package algorithmexecuter;

import algorithmexecuter.model.command.AlgorithmCommand;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.exceptions.ExecutionExceptionTexts;
import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.model.AlgorithmStorage;
import algorithmexecuter.output.AlgorithmOutputPrinter;
import exceptions.EvaluationException;
import java.util.List;

public abstract class AlgorithmExecuter {

    /**
     * Hauptmethode zur Ausführung eines MathTool-Algorithmus.
     *
     * @throws AlgorithmExecutionException
     * @throws EvaluationException
     */
    public static Identifier executeAlgorithm(List<Algorithm> algorithms) throws AlgorithmExecutionException, EvaluationException {
        Algorithm mainAlg;
        try {
            mainAlg = CompilerUtils.getMainAlgorithm(new AlgorithmStorage(algorithms));
            Identifier result = mainAlg.execute();
            AlgorithmOutputPrinter.printOutput(mainAlg, result);
            return result;
        } catch (AlgorithmCompileException e) {
            throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_MAIN_NOT_FOUND);
        }
    }

    public static Identifier executeBlock(AlgorithmMemory memoryBeforBlockExecution, List<AlgorithmCommand> commands) throws AlgorithmExecutionException, EvaluationException {
        AlgorithmMemory scopeMemory = new AlgorithmMemory(null);
        Identifier resultIdentifier = null;
        Algorithm alg = null;
        for (AlgorithmCommand command : commands) {
            // Zuerst: Zugehörigen Algorithmus ermitteln.
            if (alg == null) {
                alg = command.getAlgorithm();
                scopeMemory = memoryBeforBlockExecution.copyMemory();
            }
            resultIdentifier = command.execute(scopeMemory);

            /*
            Nur Return-Befehle geben echte Identifier zurück. Alle anderen
            Befehle geben null zurück.
             */
            if (command.isReturnCommand() || resultIdentifier != null) {
                return resultIdentifier;
            }
        }
        // Speicher vor der Ausführung des Blocks aktualisieren.
        updateMemoryBeforeBlockExecution(memoryBeforBlockExecution, scopeMemory);
        return resultIdentifier;
    }
    
    private static void updateMemoryBeforeBlockExecution(AlgorithmMemory memoryBeforBlockExecution, AlgorithmMemory scopeMemory) {
        for (String identifierName : memoryBeforBlockExecution.getMemory().keySet()) {
            if (scopeMemory.getMemory().keySet().contains(identifierName)) {
                memoryBeforBlockExecution.getMemory().put(identifierName, scopeMemory.getMemory().get(identifierName));
            }
        }
    }

}
