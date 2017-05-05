package algorithmexecutor;

import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.ExecutionExecptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import exceptions.EvaluationException;
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
        Algorithm mainAlg = getMainAlgorithm(algorithms);
        Identifier result = mainAlg.execute();
        return result;
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
        throw new AlgorithmExecutionException(ExecutionExecptionTexts.MAIN_NOT_FOUND);
    }
    
    public static Identifier executeBlock(List<AlgorithmCommand> commands) throws AlgorithmExecutionException, EvaluationException {
        Identifier resultIdentifier = null;
        for (int i = 0; i < commands.size(); i++) {
            resultIdentifier = commands.get(i).execute();
            /*
            Nur Return-Befehle geben echte Identifier zurück. Alle anderen
            Befehle geben null zurück.
             */
            if (resultIdentifier != null) {
                return resultIdentifier;
            }
        }
        return resultIdentifier;
    }

}
