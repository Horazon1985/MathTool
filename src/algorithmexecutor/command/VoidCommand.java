package algorithmexecutor.command;

import algorithmexecutor.identifier.Identifier;

public class VoidCommand extends AlgorithmCommand {
    
    private final String name;
    private final Identifier[] identifiers;
    
    public VoidCommand(String name, Identifier... identifiers) {
        this.name = name;
        this.identifiers = identifiers;
    }

    public String getName() {
        return name;
    }

    public Identifier[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public void execute() {
        
    }
    
}
