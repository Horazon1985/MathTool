package algorithmexecutor.enums;

import java.util.HashSet;
import java.util.Set;

public enum ComparingOperators {

    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER(">"),
    GREATER_OR_EQUALS(">="),
    SMALLER("<"),
    SMALLER_OR_EQUALS("<=");
    
    private final String value;
    
    ComparingOperators(String value){
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getConvertedValue() {
        if (this == EQUALS) {
            return "~";
        }
        if (this == NOT_EQUALS) {
            return "#";
        }
        return this.value;
    }
    
    public static Set<ComparingOperators> getOperatorsContainingEqualsSign() {
        Set<ComparingOperators> operators = new HashSet<>();
        for (ComparingOperators op : values()) {
            if (op.value.contains("=")) {
                operators.add(op);
            }
        }
        return operators;
    }
    
}
