package algorithmexecutor.command.condition;

import abstractexpressions.interfaces.AbstractExpression;
import java.util.Map;
import java.util.Set;

public class BooleanConstant extends BooleanExpression {

    private final boolean value;
    
    public BooleanConstant(boolean value) {
        this.value = value;
    }
    
    @Override
    public boolean contains(String var) {
        return false;
    }

    @Override
    public void addContainedVars(Set<String> vars) {
    }

    @Override
    public void addContainedIndeterminates(Set<String> vars) {
    }

    @Override
    public boolean evaluate(Map<String, AbstractExpression> valuesMap) {
        return value;
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
    
}
