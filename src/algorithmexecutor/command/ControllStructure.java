package algorithmexecutor.command;

import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;

public class ControllStructure extends AlgorithmCommand {

    public ControllStructure(Algorithm algorithm) {
        super(algorithm);
    
    }
    
    @Override
    public Identifier execute() throws AlgorithmExecutionException {

        return null;
    }
    
}
