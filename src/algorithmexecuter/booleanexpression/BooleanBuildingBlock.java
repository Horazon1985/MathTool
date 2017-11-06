package algorithmexecuter.booleanexpression;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecuter.enums.ComparingOperators;
import exceptions.EvaluationException;
import java.util.Map;
import java.util.Set;

public class BooleanBuildingBlock extends BooleanExpression {

    private final AbstractExpression left;
    private final AbstractExpression right;
    private final ComparingOperators comparingOperator;

    public BooleanBuildingBlock(AbstractExpression left, AbstractExpression right, ComparingOperators comparingOperator) {
        this.left = left;
        this.right = right;
        this.comparingOperator = comparingOperator;
    }

    @Override
    public boolean contains(String var) {
        return this.left.contains(var) && this.right.contains(var);
    }

    @Override
    public void addContainedVars(Set<String> vars) {
        this.left.addContainedVars(vars);
        this.right.addContainedVars(vars);
    }

    @Override
    public void addContainedIndeterminates(Set<String> vars) {
        this.left.addContainedIndeterminates(vars);
        this.right.addContainedIndeterminates(vars);
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
        } else if (isComparisonOfLogicalExpressions()) {
            /* 
            Wenn arithmetische Fehler auftreten, dann werden diese nicht geworfen, 
            sondern der Vergleich liefert stets 'false'
             */
            boolean logValueLeft = ((LogicalExpression) this.left).evaluate();
            boolean logValueRight = ((LogicalExpression) this.left).evaluate();
            switch (this.comparingOperator) {
                case EQUALS:
                    return logValueLeft == logValueRight;
                case NOT_EQUALS:
                    return logValueLeft != logValueRight;
            }
            return false;
        } else if (isComparisonOfMatrixExpressions()) {
            /* 
            Wenn arithmetische Fehler auftreten, dann werden diese nicht geworfen, 
            sondern der Vergleich liefert stets 'false'
             */
            try {
                MatrixExpression matValueLeft = ((MatrixExpression) this.left).evaluate();
                MatrixExpression matValueRight = ((MatrixExpression) this.left).evaluate();
                switch (this.comparingOperator) {
                    case EQUALS:
                        return matValueLeft.equivalent(matValueRight);
                    case NOT_EQUALS:
                        return !matValueLeft.equivalent(matValueRight);
                }
            } catch (EvaluationException e) {
            }
            return false;
        } else if (isComparisonOfBooleanExpressions()) {
            boolean boolValueLeft = ((BooleanExpression) this.left).evaluate(valuesMap);
            boolean boolValueRight = ((BooleanExpression) this.right).evaluate(valuesMap);
            switch (this.comparingOperator) {
                case EQUALS:
                    return boolValueLeft == boolValueRight;
                case NOT_EQUALS:
                    return boolValueLeft != boolValueRight;
            }
        }

        return false;
    }

    private boolean isComparisonOfExpressions() {
        return this.left instanceof Expression && this.right instanceof Expression && this.comparingOperator != null;
    }

    private boolean isComparisonOfLogicalExpressions() {
        return this.left instanceof LogicalExpression && this.right instanceof LogicalExpression && this.comparingOperator != null;
    }

    private boolean isComparisonOfMatrixExpressions() {
        return this.left instanceof MatrixExpression && this.right instanceof MatrixExpression && this.comparingOperator != null;
    }

    private boolean isComparisonOfBooleanExpressions() {
        return this.left instanceof BooleanExpression && this.right instanceof BooleanExpression && this.comparingOperator != null;
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        vars.addAll(this.left.getContainedIndeterminates());
        vars.addAll(this.right.getContainedIndeterminates());
    }

    @Override
    public String toString() {
        return this.left.toString() + this.comparingOperator.getValue() + this.right.toString();
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
