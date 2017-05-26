package algorithmexecutor.command;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.enums.IdentifierType;
import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.exceptions.ExecutionExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Signature;
import exceptions.EvaluationException;
import java.util.HashSet;
import java.util.Set;

public class AssignValueCommand extends AlgorithmCommand {

    private final Identifier identifierSrc;
    private final AbstractExpression targetExpression;
    private Signature targetAlgorithmSignature;
    private Identifier[] targetAlgorithmArguments;
    private Algorithm targetAlgorithm;

    public AssignValueCommand(Identifier identifierSrc, AbstractExpression targetExpression) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, IdentifierType.identifierTypeOf(targetExpression))) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = targetExpression;
    }

    public AssignValueCommand(Identifier identifierSrc, Signature targetAlgorithmSignature, Identifier[] targetAlgorithmArguments) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, targetAlgorithmSignature.getReturnType())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = null;
        this.targetAlgorithmSignature = targetAlgorithmSignature;
        this.targetAlgorithmArguments = targetAlgorithmArguments;
    }

    public AssignValueCommand(Identifier identifierSrc, Algorithm targetAlgorithm) throws AlgorithmCompileException {
        if (!areTypesCompatible(identifierSrc, targetAlgorithm.getReturnType())) {
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_INCOMPATIBEL_TYPES);
        }
        this.identifierSrc = identifierSrc;
        this.targetExpression = null;
        this.targetAlgorithm = targetAlgorithm;
    }

    private boolean areTypesCompatible(Identifier identifierSrc, IdentifierType targetType) {
        return identifierSrc.getType().isSameOrGeneralTypeOf(targetType);
    }

    public AbstractExpression getTargetExpression() {
        return this.targetExpression;
    }

    public Identifier getIdentifierSrc() {
        return this.identifierSrc;
    }

    public Signature getTargetAlgorithmSignature() {
        return targetAlgorithmSignature;
    }

    public Algorithm getTargetAlgorithm() {
        return targetAlgorithm;
    }

    public void setTargetAlgorithm(Algorithm targetAlgorithm) {
        this.targetAlgorithm = targetAlgorithm;
    }

    @Override
    public String toString() {
        String command = "AssignValueCommand[identifierSrc = " + this.identifierSrc
                + ", targetExpression = " + this.targetExpression;
        if (this.targetAlgorithm != null) {
            return command + ", targetAlgorithm = " + this.targetAlgorithm.getSignature().toString() + "]";
        }
        return command + "]";
    }

    private Set<String> getVarsFromAlgorithmParameters(Algorithm alg) {
        Set<String> varsInAlgorithmSignature = new HashSet<>();
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        AbstractExpression abstrExpr;
        for (Identifier identifier : alg.getInputParameters()) {
            abstrExpr = memory.getMemory().get(identifier.getName()).getValue();
            if (abstrExpr != null) {
                varsInAlgorithmSignature.addAll(abstrExpr.getContainedIndeterminates());
            }
        }
        return varsInAlgorithmSignature;
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        Algorithm alg = getAlgorithm();
        if (this.targetExpression != null) {
            Set<String> varsInTargetExpr = this.targetExpression.getContainedIndeterminates();
            checkForUnknownIdentifier(alg, varsInTargetExpr);
            AbstractExpression targetExprSimplified = simplifyTargetExpression(alg);
            this.identifierSrc.setValue(targetExprSimplified);
        } else {
            this.targetAlgorithm.initInputParameter(this.targetAlgorithmArguments);
            Set<String> varsInTargetExpr = getVarsFromAlgorithmParameters(this.targetAlgorithm);
            checkForUnknownIdentifier(alg, varsInTargetExpr);
            AbstractExpression targetExprSimplified = this.targetAlgorithm.execute().getValue();
            this.identifierSrc.setValue(targetExprSimplified);
        }
        AlgorithmExecutor.getMemoryMap().get(alg).addToMemoryInRuntime(this.identifierSrc);
        return null;
    }
    
    private void checkForUnknownIdentifier(Algorithm alg, Set<String> varsInTargetExpr) throws AlgorithmExecutionException {
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        for (String var : varsInTargetExpr) {
            if (!memory.containsIdentifier(var)) {
                throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_UNKNOWN_IDENTIFIER);
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
