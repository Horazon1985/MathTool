package algorithmexecutor.command;

import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;

public class ReturnCommand extends AlgorithmCommand {

    private final Identifier identifier;

    public ReturnCommand(Algorithm algorithm, Identifier identifier) {
        super(algorithm);
        this.identifier = identifier;
    }
    
    @Override
    public Identifier execute() {
        return identifier;
    }
    
}
