package algorithmexecutor.command;

import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.enums.IdentifierType;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.ExecutionExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.model.Signature;
import exceptions.EvaluationException;

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
    public String toString() {
        return "VoidCommand[name = " + this.name + ", identifiers = " + this.identifiers + "]";
    }
    
    public Signature getSignature() {
        IdentifierType[] identifierTypes = new IdentifierType[this.identifiers.length];
        for (int i = 0; i < this.identifiers.length; i++) {
            identifierTypes[i] = this.identifiers[i].getType();
        }
        return new Signature(null, this.name, identifierTypes);
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        for (Algorithm alg : AlgorithmCompiler.STORED_ALGORITHMS) {
            if (alg.getSignature().equals(getSignature()) && alg.getReturnType() == null) {
                // TO DO: Algorithmenparameter durch Bezeichnerwerte ersetzen. 
                alg.execute();
                return null;
            }
        }
        throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_NO_SUCH_COMMAND);
    }
    
}
