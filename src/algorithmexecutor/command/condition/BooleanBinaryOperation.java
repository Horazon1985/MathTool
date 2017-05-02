package algorithmexecutor.command.condition;

import algorithmexecutor.enums.ComparingOperators;
import java.util.Set;

public class BooleanBinaryOperation extends BooleanExpression {

    private final BooleanExpression left, right;
    private final BooleanBinaryOperationType type;

    public BooleanBinaryOperation(BooleanExpression left, BooleanExpression right, BooleanBinaryOperationType type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    public BooleanBinaryOperationType getType() {
        return this.type;
    }

    public BooleanExpression getLeft() {
        return this.left;
    }

    public BooleanExpression getRight() {
        return this.right;
    }

    @Override
    public boolean evaluate() {
        switch (this.type) {
            case AND:
                return this.left.evaluate() && this.right.evaluate();
            case OR:
                return this.left.evaluate() || this.right.evaluate();
            default:
                return this.left.evaluate() == this.right.evaluate();
        }
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        this.left.addContainedIdentifier(vars);
        this.right.addContainedIdentifier(vars);
    }

    @Override
    public String toString() {
        if (this.type.equals(BooleanBinaryOperationType.EQUIVALENCE)) {
            return this.left.toString() + ComparingOperators.EQUALS.getValue() + this.right.toString();
        } else if (this.type.equals(BooleanBinaryOperationType.OR)) {

            String leftAsText, rightAsText;

            if (this.left.isEquiv()) {
                leftAsText = "(" + this.left.toString() + ")";
            } else {
                leftAsText = this.left.toString();
            }

            if (this.right.isEquiv()) {
                rightAsText = "(" + this.right.toString() + ")";
            } else {
                rightAsText = this.right.toString();
            }

            return leftAsText + "|" + rightAsText;

        }

        // Fall: AND.
        String leftAsText, rightAsText;

        if (this.left instanceof BooleanBinaryOperation && (this.left.isEquiv() || this.left.isOr() || this.left.isBuildingBlock())) {
            leftAsText = "(" + this.left.toString() + ")";
        } else {
            leftAsText = this.left.toString();
        }

        if (this.right instanceof BooleanBinaryOperation && (this.right.isEquiv() || this.right.isOr() || this.right.isBuildingBlock())) {
            rightAsText = "(" + this.right.toString() + ")";
        } else {
            rightAsText = this.right.toString();
        }

        return leftAsText + "&" + rightAsText;

    }

}