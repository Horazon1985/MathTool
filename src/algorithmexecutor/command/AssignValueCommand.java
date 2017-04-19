package algorithmexecutor.command;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.exceptions.ExecutionExecptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.memory.AlgorithmMemory;
import exceptions.EvaluationException;
import java.util.Set;

public class AssignValueCommand extends AlgorithmCommand {

    private final Identifier identifierSrc;
    private final AbstractExpression targetExpression;

    public AssignValueCommand(Algorithm algorithm, Identifier identifierSrc, AbstractExpression targetExpression) throws AlgorithmCompileException {
        super(algorithm);
        if (!areTypesCompatible(identifierSrc, targetExpression)) {
            throw new AlgorithmCompileException(CompileExceptionTexts.INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = targetExpression;
    }

    private boolean areTypesCompatible(Identifier identifierSrc, AbstractExpression targetExpression) {
        return identifierSrc.getType().isSameOrGeneralTypeOf(IdentifierTypes.identifierTypeOf(targetExpression));
    }

    public AbstractExpression getIdentifierTarget() {
        return this.targetExpression;
    }

    public Identifier getIdentifierSrc() {
        return this.identifierSrc;
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        Algorithm alg = getAlgorithm();
        Set<String> varsInTargetExpr = this.targetExpression.getContainedIndeterminates();
        checkForUnknownIdentifier(alg, varsInTargetExpr);
        AbstractExpression targetExprSimplified = simplifyTargetExpression(alg);
        this.identifierSrc.setValue(targetExprSimplified);
        AlgorithmExecutor.getMemoryMap().get(alg).addToMemoryInRuntime(this.identifierSrc);
        return this.identifierSrc;
    }

    private void checkForUnknownIdentifier(Algorithm alg, Set<String> varsInTargetExpr) throws AlgorithmExecutionException {
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        for (String var : varsInTargetExpr) {
            if (!memory.containsIdentifierWithGivenName(var)) {
                throw new AlgorithmExecutionException(ExecutionExecptionTexts.UNKNOWS_IDENTIFIER);
            }
        }
    }

    private AbstractExpression simplifyTargetExpression(Algorithm alg) throws EvaluationException {
        AbstractExpression targetExprSimplified;
        if (this.targetExpression instanceof Expression) {
            Expression exprSimplified = (Expression) this.targetExpression;
            for (Identifier identifier : AlgorithmExecutor.getMemoryMap().get(alg).getMemory()) {
                if (identifier.getValue() instanceof Expression) {
                    exprSimplified = exprSimplified.replaceVariable(identifier.getName(), (Expression) identifier.getValue());
                }
            }
            targetExprSimplified = exprSimplified;
        } else if (this.targetExpression instanceof LogicalExpression) {
            LogicalExpression logExprSimplified = (LogicalExpression) this.targetExpression;
            for (Identifier identifier : AlgorithmExecutor.getMemoryMap().get(alg).getMemory()) {
                if (identifier.getValue() instanceof LogicalExpression) {
                    logExprSimplified = logExprSimplified.replaceVariable(identifier.getName(), (LogicalExpression) identifier.getValue());
                }
            }
            targetExprSimplified = logExprSimplified;
        } else {
            MatrixExpression matExprSimplified = (MatrixExpression) this.targetExpression;
            for (Identifier identifier : AlgorithmExecutor.getMemoryMap().get(alg).getMemory()) {
//                if (identifier.getValue() instanceof MatrixExpression) {
//                    matExprSimplified = matExprSimplified.replaceVariable(identifier.getName(), (Expression) identifier.getValue());
//                }
            }
            targetExprSimplified = matExprSimplified;
        }

        if (targetExprSimplified instanceof Expression) {
            return ((Expression) targetExprSimplified).simplify();
        } else if (targetExprSimplified instanceof LogicalExpression) {
            return ((LogicalExpression) targetExprSimplified).simplify();
        }
        return ((MatrixExpression) targetExprSimplified).simplify();
        
    }

}
