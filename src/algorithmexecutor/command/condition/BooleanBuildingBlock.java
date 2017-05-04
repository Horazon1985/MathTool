package algorithmexecutor.command.condition;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.enums.ComparingOperators;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import exceptions.EvaluationException;
import java.util.Set;

public class BooleanBuildingBlock extends BooleanExpression {

    private final BooleanExpression booleanExpression;
    private final Expression left;
    private final Expression right;
    private final MatrixExpression matLeft;
    private final MatrixExpression matRight;
    private final ComparingOperators comparingOperator;

    /* 
    Ein elementarer logischer Baustein ist entweder ein Identifier mit einem 
    logischer Ausdruck oder ein Vergleich von Ausdrücken.
     */
    public BooleanBuildingBlock(BooleanExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
        this.left = null;
        this.right = null;
        this.matLeft = null;
        this.matRight = null;
        this.comparingOperator = null;
    }

    public BooleanBuildingBlock(Expression left, Expression right, ComparingOperators comparingOperator) {
        this.booleanExpression = null;
        this.left = left;
        this.right = right;
        this.matLeft = null;
        this.matRight = null;
        this.comparingOperator = comparingOperator;
    }

    public BooleanBuildingBlock(MatrixExpression matLeft, MatrixExpression matRight) {
        this.booleanExpression = null;
        this.left = null;
        this.right = null;
        this.matLeft = matLeft;
        this.matRight = matRight;
        this.comparingOperator = ComparingOperators.EQUALS;
    }

    @Override
    public boolean evaluate() {
        if (isBooleanIdentifier()) {
            return this.booleanExpression.evaluate();
        } else if (isComparisonOfExpressions()) {
            /* 
            Wenn arithmetische Fehler auftreten, dann werden diese nicht geworfen, 
            sondern der Vergleich liefert stets 'false'
             */
            try {
                double valueLeft = this.left.evaluate();
                double valueRight = this.right.evaluate();
                switch (this.comparingOperator) {
                    case EQUALS:
                        if (this.left.equivalent(this.right)) {
                            return true;
                        } else if (this.left.getContainedIndeterminates().isEmpty() && this.right.getContainedIndeterminates().isEmpty()) {
                            return valueLeft == valueRight;
                        }
                        break;
                    case NOT_EQUALS:
                        return !new BooleanBuildingBlock(this.left, this.right, ComparingOperators.EQUALS).evaluate();
                    case GREATER:
                        if (this.left.getContainedIndeterminates().isEmpty() && this.right.getContainedIndeterminates().isEmpty()) {
                            return valueLeft > valueRight;
                        }
                        break;
                    case GREATER_OR_EQUALS:
                        if (this.left.getContainedIndeterminates().isEmpty() && this.right.getContainedIndeterminates().isEmpty()) {
                            return valueLeft >= valueRight;
                        }
                        break;
                    case SMALLER:
                        if (this.left.getContainedIndeterminates().isEmpty() && this.right.getContainedIndeterminates().isEmpty()) {
                            return valueLeft < valueRight;
                        }
                        break;
                    case SMALLER_OR_EQUALS:
                        if (this.left.getContainedIndeterminates().isEmpty() && this.right.getContainedIndeterminates().isEmpty()) {
                            return valueLeft <= valueRight;
                        }
                        break;
                }
            } catch (EvaluationException e) {
            }
            return false;
        }
        /* 
        Wenn arithmetische Fehler auftreten, dann werden diese nicht geworfen, 
        sondern der Vergleich liefert stets 'false'
         */
        try {
            MatrixExpression matValueLeft = this.matLeft.evaluate();
            MatrixExpression matValueRight = this.matRight.evaluate();
            switch (this.comparingOperator) {
                case EQUALS:
                    return matValueLeft.equivalent(matValueRight);
                case NOT_EQUALS:
                    return !matValueLeft.equivalent(matValueRight);
            }
        } catch (EvaluationException e) {
        }
        return false;
    }

    private boolean isBooleanIdentifier() {
        return this.booleanExpression != null;
    }

    private boolean isComparisonOfExpressions() {
        return this.left != null && this.right != null && this.comparingOperator != null;
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        if (isBooleanIdentifier()) {
            vars.addAll(this.booleanExpression.getContainedIndeterminates());
        } else if (isComparisonOfExpressions()) {
            vars.addAll(this.left.getContainedIndeterminates());
            vars.addAll(this.right.getContainedIndeterminates());
        } else {
            vars.addAll(this.matLeft.getContainedIndeterminates());
            vars.addAll(this.matRight.getContainedIndeterminates());
        }
    }

    @Override
    public String toString() {
        if (this.booleanExpression != null) {
            return this.booleanExpression.toString();
        } else if (this.left != null && this.right != null) {
            return this.left.toString() + " " + this.comparingOperator.getValue() + " " + this.right.toString();
        }
        return this.matLeft.toString() + " " + this.comparingOperator.getValue() + " " + this.matRight.toString();
    }

    private static AbstractExpression insertIdentifierValues(AbstractExpression abstrExpr, Algorithm alg) {
        Set<String> vars = abstrExpr.getContainedIndeterminates();
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        if (memory != null) {
            for (String var : vars) {
                if (abstrExpr instanceof Expression && memory.getMemory().get(var).getValue() instanceof Expression) {
                    abstrExpr = ((Expression) abstrExpr).replaceVariable(var, (Expression) memory.getMemory().get(var).getValue());
                } else if (abstrExpr instanceof LogicalExpression && memory.getMemory().get(var).getValue() instanceof LogicalExpression) {
                    abstrExpr = ((LogicalExpression) abstrExpr).replaceVariable(var, (LogicalExpression) memory.getMemory().get(var).getValue());
                } else if (abstrExpr instanceof MatrixExpression && memory.getMemory().get(var).getValue() instanceof Expression) {
                    abstrExpr = ((MatrixExpression) abstrExpr).replaceVariable(var, (Expression) memory.getMemory().get(var).getValue());
                } else if (abstrExpr instanceof MatrixExpression && memory.getMemory().get(var).getValue() instanceof MatrixExpression) {
                    abstrExpr = ((MatrixExpression) abstrExpr).replaceMatrixVariable(var, (MatrixExpression) memory.getMemory().get(var).getValue());
                }
            }
        }
        return abstrExpr;
    }

}
