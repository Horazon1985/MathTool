package algorithmexecuter.exceptions;

public class ParseVoidException extends AlgorithmCompileException {
    
    public ParseVoidException(Exception e) {
        super(e);
    }
    
    public ParseVoidException(String message, Object... params) {
        super(message, params);
    }
    
    
}
