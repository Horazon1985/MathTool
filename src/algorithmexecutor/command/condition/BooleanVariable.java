package algorithmexecutor.command.condition;

import abstractexpressions.interfaces.AbstractExpression;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BooleanVariable extends BooleanExpression {

    protected static Map<String, BooleanVariable> variables = new HashMap<>();

    private String name;
    private boolean value;

    protected BooleanVariable() {
    }

    private BooleanVariable(String name) {
        this.name = name;
        this.value = false;
    }

    private BooleanVariable(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Methode create: ohne Wertzuweisung (d.h. die Variable wird automatisch
     * auf false gesetzt)
     */
    public static BooleanVariable create(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        BooleanVariable result = new BooleanVariable(name, false);
        variables.put(name, result);
        return result;
    }
    
    /**
     * Methode create: mit Wertzuweisung als exakten Ausdruck
     */
    public static BooleanVariable create(String name, boolean value) {
        if (variables.containsKey(name)) {
            variables.get(name).value = value;
            return variables.get(name);
        } else {
            BooleanVariable result = new BooleanVariable(name, value);
            variables.put(name, result);
            return result;
        }
    }
    
    public static void setValue(String name, boolean value) {
        if (variables.containsKey(name)) {
            variables.get(name).value = value;
        } else {
            BooleanVariable.create(name, value);
        }
    }

    @Override
    public boolean evaluate(Map<String, AbstractExpression> valuesMap) {
        return this.value;
    }

    @Override
    public void addContainedIdentifier(Set<String> vars) {
        vars.add(this.name);
    }

}
