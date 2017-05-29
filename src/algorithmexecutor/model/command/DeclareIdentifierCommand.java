package algorithmexecutor.model.command;

import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.model.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.AlgorithmExecutor;

public class DeclareIdentifierCommand extends AlgorithmCommand {

    private final Identifier identifierSrc;

    public DeclareIdentifierCommand(Identifier identifierSrc) {
        this.identifierSrc = identifierSrc;
    }

    public Identifier getIdentifierSrc() {
        return this.identifierSrc;
    }

    @Override
    public String toString() {
        return "DeclareIdentifierCommand[identifierSrc = " + this.identifierSrc + "]";
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException {
        Algorithm alg = getAlgorithm();
        AlgorithmExecutor.getExecutionMemory().get(alg).addToMemoryInRuntime(this.identifierSrc);
        return null;
    }

}
