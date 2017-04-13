package algorithmexecutor.enums;

public enum Operators {

    DEFINE("="),
    EQUALS("=="),
    PLUS("+"),
    MODULO("%"),
    NOT("!"),
    AND("&"),
    OR("|");
    
    private final String value;
    
    Operators(String keyword){
        this.value = keyword;
    }
    
    public String getValue() {
        return this.value;
    }
    
}
