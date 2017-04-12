package algorithmexecutor.enums;

public enum ReservedChars {

    LINE_SEPARATOR(';'),
    BEGIN('{'),
    END('}');
    
    private final char value;
    
    ReservedChars(char c){
        this.value = c;
    }
    
    public char getValue() {
        return this.value;
    }
    
}
