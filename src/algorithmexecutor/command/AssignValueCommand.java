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
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.memory.AlgorithmMemory;
import exceptions.EvaluationException;
import java.util.HashSet;
import java.util.Set;

public class AssignValueCommand extends AlgorithmCommand {

    private final Identifier identifierSrc;
    private final AbstractExpression targetExpression;
    private final Algorithm targetAlgorithm;

    public AssignValueCommand(Identifier identifierSrc, AbstractExpression targetExpression) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, IdentifierTypes.identifierTypeOf(targetExpression))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = targetExpression;
        this.targetAlgorithm = null;
    }

    public AssignValueCommand(Identifier identifierSrc, Algorithm targetAlgorithm) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, targetAlgorithm.getReturnType())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = null;
        this.targetAlgorithm = targetAlgorithm;
    }

    private boolean areTypesCompatible(Identifier identifierSrc, IdentifierTypes targetType) {
        return identifierSrc.getType().isSameOrGeneralTypeOf(targetType);
    }

    public AbstractExpression getTargetExpression() {
        return this.targetExpression;
    }

    public Identifier getIdentifierSrc() {
        return this.identifierSrc;
    }

    @Override
    public String toString() {
        String command = "AssignValueCommand[identifierSrc = " + this.identifierSrc
                + ", targetExpression = " + this.targetExpression;
        if (this.targetAlgorithm != null) {
            return command + ", targetAlgorithm = " + this.targetAlgorithm.getSignature() + "]";
        }
        return command + "]";

    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        Algorithm alg = getAlgorithm();
        Set<String> varsInTargetExpr = this.targetExpression.getContainedIndeterminates();
        checkForUnknownIdentifier(alg, varsInTargetExpr);
        AbstractExpression targetExprSimplified = simplifyTargetExpression(alg);
        this.identifierSrc.setValue(targetExprSimplified);
        AlgorithmExecutor.getMemoryMap().get(alg).addToMemoryInRuntime(this.identifierSrc);
        return null;
    }

    private void checkForUnknownIdentifier(Algorithm alg, Set<String> varsInTargetExpr) throws AlgorithmExecutionException {
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        for (String var : varsInTargetExpr) {
            if (!memory.containsIdentifier(var)) {
                throw new AlgorithmExecutionException(ExecutionExecptionTexts.UNKNOWS_IDENTIFIER);
            }
        }
    }

    private AbstractExpression simplifyTargetExpression(Algorithm alg) throws EvaluationException {
        AbstractExpression targetExprSimplified;

        if (this.targetExpression instanceof Expression) {
            Expression exprSimplified = (Expression) this.targetExpression;
            for (Identifier identifier : AlgorithmExecutor.getMemoryMap().get(alg).getMemory().values()) {
                if (identifier.getValue() instanceof Expression) {
                    exprSimplified = exprSimplified.replaceVariable(identifier.getName(), (Expression) identifier.getValue());
                }
            }
            targetExprSimplified = exprSimplified;
        } else if (this.targetExpression instanceof LogicalExpression) {
            LogicalExpression logExprSimplified = (LogicalExpression) this.targetExpression;
            for (Identifier identifier : AlgorithmExecutor.getMemoryMap().get(alg).getMemory().values()) {
                if (identifier.getValue() instanceof LogicalExpression) {
                    logExprSimplified = logExprSimplified.replaceVariable(identifier.getName(), (LogicalExpression) identifier.getValue());
                }
            }
            targetExprSimplified = logExprSimplified;
        } else if (this.targetExpression instanceof MatrixExpression) {
            MatrixExpression matExprSimplified = (MatrixExpression) this.targetExpression;
            for (Identifier identifier : AlgorithmExecutor.getMemoryMap().get(alg).getMemory().values()) {
                if (identifier.getValue() instanceof Expression) {
                    matExprSimplified = matExprSimplified.replaceVariable(identifier.getName(), (Expression) identifier.getValue());
                }
            }
            targetExprSimplified = matExprSimplified;
        } else {
            targetExprSimplified = (BooleanExpression) this.targetExpression;
        }

        if (targetExprSimplified instanceof Expression) {
            return ((Expression) targetExprSimplified).simplify();
        } else if (targetExprSimplified instanceof LogicalExpression) {
            return ((LogicalExpression) targetExprSimplified).simplify();
        } else if (targetExprSimplified instanceof MatrixExpression) {
            return ((MatrixExpression) targetExprSimplified).simplify();
        }
        return targetExprSimplified;

    }

}
