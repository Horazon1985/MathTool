package algorithmexecutor.enums;

public enum ComparingOperators {

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
    
}
