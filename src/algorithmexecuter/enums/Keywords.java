package algorithmexecuter.enums;

public enum Keywords {
    
    NULL("null"),
    FALSE("false"),
    TRUE("true"),
    RETURN("return"),
    IF("if"),
    ELSE("else"),
//    TRY("try"),
//    CATCH("catch"),
//    FINALLY("finally"),
//    FOR("for"),
    WHILE("while"),
    BREAK("break"),
    CONTINUE("continue"),
    DO("do");
 
    
    private final String value;
    
    Keywords(String keyword){
        this.value = keyword;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
}
