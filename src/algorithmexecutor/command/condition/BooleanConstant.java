package algorithmexecutor.command.condition;

public class BooleanConstant extends BooleanCondition {

    private final boolean value;
    
    public BooleanConstant(boolean value) {
        this.value = value;
    }
    
    @Override
    public boolean evaluate() {
        return value;
    }
    
}
