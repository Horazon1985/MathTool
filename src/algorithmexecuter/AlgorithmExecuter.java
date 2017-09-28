package algorithmexecuter;

import algorithmexecuter.model.command.AlgorithmCommand;
import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.exceptions.constants.ExecutionExceptionTexts;
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

    /**
     * Führt einen zusammenhängenden Befehlsblock aus und gibt den
     * Ergebnisbezeichner zurück, falls ein Return-Befehl ausgeführt wurde. Nach
     * der Blockausführung werden die in diesem Block deklarierten Bezeichner
     * wieder verworfen.
     *
     * @param memoryBeforeBlockExecution
     * @param commands
     * @return
     * @throws AlgorithmExecutionException
     * @throws EvaluationException
     */
    public static Identifier executeConnectedBlock(AlgorithmMemory memoryBeforeBlockExecution, List<AlgorithmCommand> commands) throws AlgorithmExecutionException, EvaluationException {
        AlgorithmMemory scopeMemory = new AlgorithmMemory(null);
        Identifier resultIdentifier = null;
        Algorithm alg = null;
        for (AlgorithmCommand command : commands) {
            // Zuerst: Zugehörigen Algorithmus ermitteln.
            if (alg == null) {
                alg = command.getAlgorithm();
                scopeMemory = memoryBeforeBlockExecution.copyMemory();
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
        ExecutionUtils.updateMemoryBeforeBlockExecution(memoryBeforeBlockExecution, scopeMemory);
        return resultIdentifier;
    }

    /**
     * Führt einen zusammenhängenden Befehlsblock aus und gibt den
     * Ergebnisbezeichner zurück, falls ein Return-Befehl ausgeführt wurde. Nach
     * der Blockausführung werden die in diesem Block deklarierten Bezeichner
     * jedoch nicht wieder verworfen.
     *
     * @param scopeMemory
     * @param commands
     * @return
     * @throws AlgorithmExecutionException
     * @throws EvaluationException
     */
    public static Identifier executeBlock(AlgorithmMemory scopeMemory, List<AlgorithmCommand> commands) throws AlgorithmExecutionException, EvaluationException {
        Identifier resultIdentifier = null;
        for (AlgorithmCommand command : commands) {
            // Zuerst: Zugehörigen Algorithmus ermitteln.
            resultIdentifier = command.execute(scopeMemory);

            /*
            Nur Return-Befehle geben echte Identifier zurück. Alle anderen
            Befehle geben null zurück.
             */
            if (command.isReturnCommand() || resultIdentifier != null) {
                return resultIdentifier;
            }
        }
        return resultIdentifier;
    }

}
