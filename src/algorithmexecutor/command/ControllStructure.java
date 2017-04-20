package algorithmexecutor.command;

import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import java.util.List;

public class ControllStructure extends AlgorithmCommand {

    protected final List<AlgorithmCommand> commands;
    
    public ControllStructure(Algorithm algorithm, List<AlgorithmCommand> commands) {
        super(algorithm);
        this.commands = commands;
    }
    
    @Override
    public Identifier execute() throws AlgorithmExecutionException {

        return null;
    }
    
}
