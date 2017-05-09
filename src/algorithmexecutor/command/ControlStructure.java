package algorithmexecutor.command;

import java.util.List;

public abstract class ControlStructure extends AlgorithmCommand {
    
    protected List<AlgorithmCommand>[] commandBlocks;

    public List<AlgorithmCommand>[] getCommandBlocks() {
        return commandBlocks;
    }
    
}
