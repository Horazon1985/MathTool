package algorithmexecuter.enums;

public enum ReservedChars {

    ARGUMENT_SEPARATOR(','),
    LINE_SEPARATOR(';'),
    BEGIN('{'),
    END('}'),
    OPEN_SQUARE_BRACKET('['),
    CLOSE_SQUARE_BRACKET(']'),
    OPEN_BRACKET('('),
    CLOSE_BRACKET(')');
    
    private final char value;
    
    ReservedChars(char c){
        this.value = c;
    }
    
    public char getValue() {
        return this.value;
    }
    
    public String getStringValue() {
        return String.valueOf(this.value);
    }
    
}
