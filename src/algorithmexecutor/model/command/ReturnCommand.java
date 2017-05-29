package algorithmexecutor.model.command;

import algorithmexecutor.model.identifier.Identifier;

public class ReturnCommand extends AlgorithmCommand {

    private final Identifier identifier;

    public ReturnCommand(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "ReturnCommand[identifier = " + this.identifier + "]";
    }
    
    @Override
    public Identifier execute() {
        return identifier;
    }
    
}
