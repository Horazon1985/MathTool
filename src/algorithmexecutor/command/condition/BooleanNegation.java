package algorithmexecutor.command.condition;

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
    
}
