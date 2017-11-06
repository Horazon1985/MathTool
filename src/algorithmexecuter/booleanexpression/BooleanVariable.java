package algorithmexecuter.booleanexpression;

import abstractexpressions.interfaces.AbstractExpression;
import java.util.Map;
import java.util.Set;

public class BooleanVariable extends BooleanExpression {

    private final String name;

    public BooleanVariable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean contains(String var) {
        return this.name.equals(var);
    }

    @Override
    public void addContainedVars(Set<String> vars) {
        vars.add(this.name);
    }

    @Override
    public void addContainedIndeterminates(Set<String> vars) {
        vars.add(this.name);
    }

    @Override
    public boolean evaluate(Map<String, AbstractExpression> valuesMap) {
        if (valuesMap.containsKey(this.name) && valuesMap.get(this.name) instanceof BooleanConstant) {
            return ((BooleanConstant) valuesMap.get(this.name)).getValue();
        }
        return false;
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        vars.add(this.name);
    }

}
