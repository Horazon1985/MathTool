package algorithmexecutor.command.condition;

public class BooleanBinaryOperation extends BooleanCondition {

    private final BooleanCondition left, right;
    private final BooleanBinaryOperationType type;

    public BooleanBinaryOperation(BooleanCondition left, BooleanCondition right, BooleanBinaryOperationType type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    public BooleanBinaryOperationType getType() {
        return this.type;
    }

    public BooleanCondition getLeft() {
        return this.left;
    }

    public BooleanCondition getRight() {
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

}
