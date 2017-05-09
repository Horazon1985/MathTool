package algorithmexecutor;

import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.AssignValueCommand;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.ExecutionExecptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.output.AlgorithmOutputPrinter;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AlgorithmExecutor {

    private static final Map<Algorithm, AlgorithmMemory> MEMORY_MAP = new HashMap<>();

    public static Map<Algorithm, AlgorithmMemory> getMemoryMap() {
        return MEMORY_MAP;
    }

    /**
     * Hauptmethode zur Ausführung eines MathTool-Algorithmus.
     *
     * @throws AlgorithmExecutionException
     * @throws EvaluationException
     */
    public static Identifier executeAlgorithm(List<Algorithm> algorithms) throws AlgorithmExecutionException, EvaluationException {
        // Alle (lokalen) Variablen und Parameter aus dem Speicher entfernen.
        MEMORY_MAP.clear();

        Algorithm mainAlg;
        try {
            mainAlg = CompilerUtils.getMainAlgorithm(algorithms);
            Identifier result = mainAlg.execute();
            AlgorithmOutputPrinter.printOutput(mainAlg, result);
            return result;
        } catch (AlgorithmCompileException e) {
            throw new AlgorithmExecutionException(ExecutionExecptionTexts.AE_MAIN_NOT_FOUND);
        }
    }

    /**
     * Gibt den Hauptalgorithmus zurück oder wirft eine
     * AlgorithmExecutionException, wenn kein Hauptalgorithmus
     * (MAIN-Algorithmus) in der Liste <code>algorithms</code> vorhanden
     * ist.<br>
     * Es können keine zwei MAIN-Algorithmen in <code>algorithms</code>
     * vorkommen, da dies bereits zur Compilezeit Fehler werfen würde.
     *
     * @throws AlgorithmExecutionException
     */
    private static Algorithm getMainAlgorithm(List<Algorithm> algorithms) throws AlgorithmExecutionException {
        for (Algorithm alg : algorithms) {
            if (alg.getName().equals(Keywords.MAIN.getValue())) {
                return alg;
            }
        }
        throw new AlgorithmExecutionException(ExecutionExecptionTexts.AE_MAIN_NOT_FOUND);
    }

    public static Identifier executeBlock(List<AlgorithmCommand> commands) throws AlgorithmExecutionException, EvaluationException {
        AlgorithmMemory scopeMemory = new AlgorithmMemory();
        AlgorithmMemory memoryBeforBlockExecution = null;
        Identifier resultIdentifier = null;
        Algorithm alg = null;
        for (AlgorithmCommand command : commands) {
            // Zuerst: Zugehörigen Algorithmus ermitteln.
            if (alg == null) {
                alg = command.getAlgorithm();
            }
            // Sobald der Algorithmus feststeht: Variablen vor der Blockausführung ermitteln.
            if (alg != null && memoryBeforBlockExecution == null) {
                memoryBeforBlockExecution = getAlgorithmMemoryBeforeExecution(alg);
            }
            resultIdentifier = command.execute();

            /* 
            Den neuen Identifier sowohl zum globalen, als auch zum lokalen Identifierpool
            hinzufügen. Am Ende der Ausführung des Blocks dann alle Identifier, welche
            nur im Block deklariert wurden, wieder aus dem globalen Pool entfernen.
             */
            if (command.isAssignValueCommand()) {
                scopeMemory.addToMemoryInRuntime(((AssignValueCommand) command).getIdentifierSrc());
            }

            /*
            Nur Return-Befehle geben echte Identifier zurück. Alle anderen
            Befehle geben null zurück.
             */
            if (resultIdentifier != null) {
                removeLocalIdentifiersFromMemory(alg, scopeMemory, memoryBeforBlockExecution);
                return resultIdentifier;
            }
        }
        removeLocalIdentifiersFromMemory(alg, scopeMemory, memoryBeforBlockExecution);
        return resultIdentifier;
    }

    private static AlgorithmMemory getAlgorithmMemoryBeforeExecution(Algorithm alg) {
        List<Identifier> identifiers = new ArrayList<>();
        identifiers.addAll(MEMORY_MAP.get(alg).getMemory().values());
        return new AlgorithmMemory(identifiers);
    }

    private static void removeLocalIdentifiersFromMemory(Algorithm alg, AlgorithmMemory scopeMemory, AlgorithmMemory memoryBeforeBlockExecution) {
        for (String identifierName : scopeMemory.getMemory().keySet()) {
            if (memoryBeforeBlockExecution == null || !memoryBeforeBlockExecution.containsIdentifier(identifierName)) {
                MEMORY_MAP.get(alg).getMemory().remove(identifierName);
            }
        }
    }

}
