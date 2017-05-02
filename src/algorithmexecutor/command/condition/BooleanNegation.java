package algorithmexecutor.command.condition;

import algorithmexecutor.enums.Operators;
import java.util.Set;

public class BooleanNegation extends BooleanExpression {

    private final BooleanExpression argument;

    public BooleanNegation(BooleanExpression argument) {
        this.argument = argument;
    }

    @Override
    public boolean evaluate() {
        return !this.argument.evaluate();
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        this.argument.addContainedIdentifier(vars);
    }

    @Override
    public String toString() {
        if (this.argument instanceof BooleanBinaryOperation || this.argument instanceof BooleanBinaryOperation) {
            return Operators.NOT.getValue() + "(" + this.argument.toString() + ")";
        }
        return Operators.NOT.getValue() + this.argument.toString();
    }

}