package algorithmexecutor.command.condition;

import abstractexpressions.expression.classes.Expression;
import algorithmexecutor.enums.ComparingOperators;
import algorithmexecutor.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.Set;

public class BooleanBuildingBlock extends BooleanExpression {

    private final Identifier identifierWithLogicalExpression;
    private final Expression left;
    private final Expression right;
    private final ComparingOperators comparingOperator;

    /* 
    Ein elementarer logischer Baustein ist entweder ein Identifier mit einem 
    logischer Ausdruck oder ein Vergleich von Ausdrücken.
     */
    public BooleanBuildingBlock(Identifier identifierWithLogicalExpression) {
        this.identifierWithLogicalExpression = identifierWithLogicalExpression;
        this.left = null;
        this.right = null;
        this.comparingOperator = null;
    }

    public BooleanBuildingBlock(Expression left, Expression right, ComparingOperators comparingOperator) {
        this.identifierWithLogicalExpression = null;
        this.left = left;
        this.right = right;
        this.comparingOperator = comparingOperator;
    }

    @Override
    public boolean evaluate() {
        if (isBooleanIdentifier()) {

        } else if (isComparisonOfExpressions()) {
            /* 
            Wenn Arithmetische Fehler auftreten, dann werden diese nicht geworfen, 
            sondern der Vergleich liefert stets 'false'
             */
            try {
                double valueLeft = this.left.evaluate();
                double valueRight = this.right.evaluate();
                switch (this.comparingOperator) {
                    case NOT_EQUALS:
                        return valueLeft != valueRight;
                    case GREATER:
                        return valueLeft > valueRight;
                    case GREATER_OR_EQUALS:
                        return valueLeft >= valueRight;
                    case SMALLER:
                        return valueLeft < valueRight;
                    case SMALLER_OR_EQUALS:
                        return valueLeft <= valueRight;
                }
            } catch (EvaluationException e) {
                return false;
            }
        }
        // Sollte nie vorkommen.
        return false;
    }

    private boolean isBooleanIdentifier() {
        return this.identifierWithLogicalExpression != null;
    }

    private boolean isComparisonOfExpressions() {
        return this.left != null && this.right != null && this.comparingOperator != null;
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        if (isBooleanIdentifier()) {
            vars.add(this.identifierWithLogicalExpression.getName());
        } else if (isComparisonOfExpressions()) {
            vars.addAll(this.left.getContainedIndeterminates());
            vars.addAll(this.right.getContainedIndeterminates());
        }
    }

    @Override
    public String toString() {
        if (this.identifierWithLogicalExpression != null) {
            return this.identifierWithLogicalExpression.getValue().toString();
        }
        return this.left.toString() + " " + this.comparingOperator.getValue() + " " + this.right.toString();
    }
    
}
