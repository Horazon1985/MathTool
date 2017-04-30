package algorithmexecutor.enums;

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
        return this.value;
    }
    
}
