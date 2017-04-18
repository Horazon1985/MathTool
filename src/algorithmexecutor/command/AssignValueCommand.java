package algorithmexecutor.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;

public class AssignValueCommand extends AlgorithmCommand {
    
    private final Identifier identifierSrc;
    private final AbstractExpression targetExpression;
    
    public AssignValueCommand(Algorithm algorithm, Identifier identifierSrc, AbstractExpression targetExpression) throws AlgorithmCompileException {
        super(algorithm);
        if (areTypesCompatible(identifierSrc, targetExpression)) {
            throw new AlgorithmCompileException(CompileExceptionTexts.INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = targetExpression;
    }
    
    private boolean areTypesCompatible(Identifier identifierSrc, AbstractExpression targetExpression) {
        return true;
    }

    public AbstractExpression getIdentifierTarget() {
        return targetExpression;
    }

    public Identifier getIdentifierSrc() {
        return identifierSrc;
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException {
        
        
        
        return identifierSrc;
    }
    
    
}
