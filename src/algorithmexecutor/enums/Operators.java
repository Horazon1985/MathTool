package algorithmexecutor.enums;

public enum Operators {

    DEFINE("="),
    CONCAT("+"),
    MODULO("%"),
    NOT("!"),
    AND("&"),
    OR("|");
    
    private final String value;
    
    Operators(String value){
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
}
