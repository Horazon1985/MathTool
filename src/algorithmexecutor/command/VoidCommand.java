package algorithmexecutor.command;

import algorithmexecutor.AlgorithmCompiler;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.ExecutionExecptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import exceptions.EvaluationException;

public class VoidCommand extends AlgorithmCommand {
    
    private final String name;
    private final Identifier[] identifiers;
    
    public VoidCommand(Algorithm algorithm, String name, Identifier... identifiers) {
        super(algorithm);
        this.name = name;
        this.identifiers = identifiers;
    }

    public String getName() {
        return name;
    }

    public Identifier[] getIdentifiers() {
        return identifiers;
    }
    
    public String getSignature() {
        String signature = this.name + "(";
        for (int i = 0; i < this.identifiers.length; i++) {
            signature += this.identifiers[i].getType();
            if (i < this.identifiers.length - 1) {
                signature += ",";
            }
        }   
        return signature + ")";
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        for (Algorithm alg : AlgorithmCompiler.STORED_ALGORITHMS) {
            if (alg.getSignature().equals(getSignature()) && alg.getOutputParameter() == null) {
                alg.execute();
                return null;
            }
        }
        throw new AlgorithmExecutionException(ExecutionExecptionTexts.NO_SUCH_COMMAND);
    }
    
}