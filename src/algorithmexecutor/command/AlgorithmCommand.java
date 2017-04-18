package algorithmexecutor.command;

import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;

public abstract class AlgorithmCommand {
    
    protected final Algorithm algorithm;

    public AlgorithmCommand(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }
    
    public abstract Identifier execute() throws AlgorithmExecutionException;
    
}
