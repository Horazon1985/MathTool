package algorithmexecutor.enums;

public enum Keywords {
    
    MAIN("main"),
    NULL("null"),
    FALSE("false"),
    TRUE("true"),
    RETURN("return"),
    BREAK("break"),
    CONTINUE("continue"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    FOR("for"),
    TRY("try"),
    CATCH("catch"),
    FINALLY("finally");
    
    private final String value;
    
    Keywords(String keyword){
        this.value = keyword;
    }
    
    public String getValue() {
        return this.value;
    }
    
}
