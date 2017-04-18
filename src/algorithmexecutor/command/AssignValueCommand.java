package algorithmexecutor.command;

import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;

public class AssignValueCommand extends AlgorithmCommand {
    
    private final Identifier identifierSrc;
    private final Identifier identifierTarget;
    
    public AssignValueCommand(Algorithm algorithm, Identifier identifierSrc, Identifier identifierTarget) throws AlgorithmCompileException {
        super(algorithm);
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

    @Override
    public Identifier execute() throws AlgorithmExecutionException {
        
        
        
        return identifierSrc;
    }
    
    
}
