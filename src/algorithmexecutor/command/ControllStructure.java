package algorithmexecutor.command;

import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import java.util.List;

public class ControllStructure extends AlgorithmCommand {

    protected final List<AlgorithmCommand> commands;
    
    public ControllStructure(List<AlgorithmCommand> commands) {
        this.commands = commands;
    }
    
    @Override
    public Identifier execute() throws AlgorithmExecutionException {

        return null;
    }
    
}
