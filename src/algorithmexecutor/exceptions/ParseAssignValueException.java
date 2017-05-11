package algorithmexecutor.exceptions;

public class ParseAssignValueException extends AlgorithmCompileException {
    
    public ParseAssignValueException(Exception e) {
        super(e);
    }
    
    public ParseAssignValueException(String message, Object... params) {
        super(message, params);
    }
    
    
}
