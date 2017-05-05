package algorithmexecutor.command.condition;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.enums.ComparingOperators;
import exceptions.EvaluationException;
import java.util.Map;
import java.util.Set;

public class BooleanBuildingBlock extends BooleanExpression {

    private final Expression left;
    private final Expression right;
    private final MatrixExpression matLeft;
    private final MatrixExpression matRight;
    private final ComparingOperators comparingOperator;

    public BooleanBuildingBlock(Expression left, Expression right, ComparingOperators comparingOperator) {
        this.left = left;
        this.right = right;
        this.matLeft = null;
        this.matRight = null;
        this.comparingOperator = comparingOperator;
    }

    public BooleanBuildingBlock(MatrixExpression matLeft, MatrixExpression matRight) {
        this.left = null;
        this.right = null;
        this.matLeft = matLeft;
        this.matRight = matRight;
        this.comparingOperator = ComparingOperators.EQUALS;
    }

    @Override
    public boolean evaluate(Map<String, AbstractExpression> valuesMap) {
        if (isComparisonOfExpressions()) {
            /* 
            Wenn arithmetische Fehler auftreten, dann werden diese nicht geworfen, 
            sondern der Vergleich liefert stets 'false'
             */
            try {
                Expression exprLeft = (Expression) replaceVarsByIdentifierValues(this.left, valuesMap);
                Expression exprRight = (Expression) replaceVarsByIdentifierValues(this.right, valuesMap);
                double valueLeft = exprLeft.evaluate();
                double valueRight = exprRight.evaluate();
                switch (this.comparingOperator) {
                    case EQUALS:
                        if (exprLeft.equivalent(exprRight)) {
                            return true;
                        } else if (exprLeft.getContainedIndeterminates().isEmpty() && exprRight.getContainedIndeterminates().isEmpty()) {
                            return valueLeft == valueRight;
                        }
                        break;
                    case NOT_EQUALS:
                        return !new BooleanBuildingBlock(exprLeft, exprRight, ComparingOperators.EQUALS).evaluate(valuesMap);
                    case GREATER:
                        if (exprLeft.getContainedIndeterminates().isEmpty() && exprRight.getContainedIndeterminates().isEmpty()) {
                            return valueLeft > valueRight;
                        }
                        break;
                    case GREATER_OR_EQUALS:
                        if (exprLeft.getContainedIndeterminates().isEmpty() && exprRight.getContainedIndeterminates().isEmpty()) {
                            return valueLeft >= valueRight;
                        }
                        break;
                    case SMALLER:
                        if (exprLeft.getContainedIndeterminates().isEmpty() && exprRight.getContainedIndeterminates().isEmpty()) {
                            return valueLeft < valueRight;
                        }
                        break;
                    case SMALLER_OR_EQUALS:
                        if (exprLeft.getContainedIndeterminates().isEmpty() && exprRight.getContainedIndeterminates().isEmpty()) {
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

    private boolean isComparisonOfExpressions() {
        return this.left != null && this.right != null && this.comparingOperator != null;
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        if (isComparisonOfExpressions()) {
            vars.addAll(this.left.getContainedIndeterminates());
            vars.addAll(this.right.getContainedIndeterminates());
        } else {
            vars.addAll(this.matLeft.getContainedIndeterminates());
            vars.addAll(this.matRight.getContainedIndeterminates());
        }
    }

    @Override
    public String toString() {
        if (this.left != null && this.right != null) {
            return this.left.toString() + " " + this.comparingOperator.getValue() + " " + this.right.toString();
        }
        return this.matLeft.toString() + " " + this.comparingOperator.getValue() + " " + this.matRight.toString();
    }

    private static AbstractExpression replaceVarsByIdentifierValues(AbstractExpression abstrExpr, Map<String, AbstractExpression> valuesMap) {
        // TO DO: Falsche Ermittlung von vars!
        Set<String> vars = abstrExpr.getContainedVars();
        for (String var : vars) {
            if (abstrExpr instanceof Expression && valuesMap.get(var) instanceof Expression) {
                abstrExpr = ((Expression) abstrExpr).replaceVariable(var, (Expression) valuesMap.get(var));
            } else if (abstrExpr instanceof LogicalExpression && valuesMap.get(var) instanceof LogicalExpression) {
                abstrExpr = ((LogicalExpression) abstrExpr).replaceVariable(var, (LogicalExpression) valuesMap.get(var));
            } else if (abstrExpr instanceof MatrixExpression && valuesMap.get(var) instanceof Expression) {
                abstrExpr = ((MatrixExpression) abstrExpr).replaceVariable(var, (Expression) valuesMap.get(var));
            } else if (abstrExpr instanceof MatrixExpression && valuesMap.get(var) instanceof MatrixExpression) {
                abstrExpr = ((MatrixExpression) abstrExpr).replaceMatrixVariable(var, (MatrixExpression) valuesMap.get(var));
            }
        }
        return abstrExpr;
    }

}
