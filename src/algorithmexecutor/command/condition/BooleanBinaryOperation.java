package algorithmexecutor.command.condition;

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

}
