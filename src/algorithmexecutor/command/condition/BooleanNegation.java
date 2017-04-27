package algorithmexecutor.command.condition;

public class BooleanNegation extends BooleanCondition {

    private final boolean argument;
    
    public BooleanNegation(boolean argument) {
        this.argument = argument;
    }
    
    @Override
    public boolean evaluate() {
        return !this.argument;
    }
    
}
