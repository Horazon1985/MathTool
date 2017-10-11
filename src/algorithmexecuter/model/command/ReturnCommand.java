package algorithmexecuter.model.command;

import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.identifier.Identifier;

public class ReturnCommand extends AlgorithmCommand {

    private final Identifier identifier;

    public ReturnCommand(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return "ReturnCommand[identifier = " + this.identifier + "]";
    }
    
    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) {
        return scopeMemory.getMemory().get(this.identifier.getName());
    }
    
    @Override
    public String toCommandString() {
        return Keyword.RETURN.getValue() + " " + this.identifier.getName() + ReservedChars.LINE_SEPARATOR.getStringValue();
    }
    
}
