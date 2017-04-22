package algorithmexecutor.command;

import algorithmexecutor.identifier.Identifier;

public class ReturnCommand extends AlgorithmCommand {

    private final Identifier identifier;

    public ReturnCommand(Identifier identifier) {
        this.identifier = identifier;
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
