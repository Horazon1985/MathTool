package algorithmexecutor.command;

import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;

public class AssignValueCommand extends AlgorithmCommand {
    
    private final Identifier identifierSrc;
    private final Identifier identifierTarget;
    
    public AssignValueCommand(Identifier identifierSrc, Identifier identifierTarget) throws AlgorithmCompileException {
        if (identifierSrc.getType() != identifierTarget.getType()) {
            throw new AlgorithmCompileException(CompileExceptionTexts.INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.identifierTarget = identifierTarget;
    }

    public Identifier getIdentifierTarget() {
        return identifierTarget;
    }

    public Identifier getIdentifierSrc() {
        return identifierSrc;
    }
    
    
}
