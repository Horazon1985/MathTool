package algorithmexecutor.command;

import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.AlgorithmExecutor;

public class DeclareIdentifierCommand extends AlgorithmCommand {

    private final Identifier identifierSrc;
    private final Algorithm targetAlgorithm;

    public DeclareIdentifierCommand(Identifier identifierSrc) {
        this.identifierSrc = identifierSrc;
        this.targetAlgorithm = null;
    }

    public DeclareIdentifierCommand(Identifier identifierSrc, Algorithm targetAlgorithm) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, targetAlgorithm.getReturnType())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetAlgorithm = targetAlgorithm;
    }

    private boolean areTypesCompatible(Identifier identifierSrc, IdentifierTypes targetType) {
        return identifierSrc.getType().isSameOrGeneralTypeOf(targetType);
    }

    public Identifier getIdentifierSrc() {
        return this.identifierSrc;
    }

    @Override
    public String toString() {
        String command = "DeclareIdentifierCommand[identifierSrc = " + this.identifierSrc;
        if (this.targetAlgorithm != null) {
            return command + ", targetAlgorithm = " + this.targetAlgorithm.getSignature() + "]";
        }
        return command + "]";

    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException {
        Algorithm alg = getAlgorithm();
        AlgorithmExecutor.getMemoryMap().get(alg).addToMemoryInRuntime(this.identifierSrc);
        return null;
    }

}
