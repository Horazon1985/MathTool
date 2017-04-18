package algorithmexecutor;

import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.enums.Keywords;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.ExecutionExecptionTexts;
import algorithmexecutor.model.Algorithm;
import java.util.List;

public abstract class AlgorithmExecutor {
    
    public static void executeAlgorithm(List<Algorithm> algorithms) throws AlgorithmExecutionException {
        Algorithm mainAlg = getMainAlgorithm(algorithms);
        for (AlgorithmCommand c : mainAlg.getCommands()) {
            c.execute();
        }
    }
    
    private static Algorithm getMainAlgorithm(List<Algorithm> algorithms) throws AlgorithmExecutionException {
        for (Algorithm alg : algorithms) {
            if (alg.getName().equals(Keywords.MAIN.getValue())) {
                return alg;
            }
        }
        throw new AlgorithmExecutionException(ExecutionExecptionTexts.MAIN_NOT_FOUND);
    }
    
    
    
    
    
}
