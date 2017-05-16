package algorithmexecutor.exceptions;

public class DeclareIdentifierException extends AlgorithmCompileException {
    
    public DeclareIdentifierException(Exception e) {
        super(e);
    }
    
    public DeclareIdentifierException(String message, Object... params) {
        super(message, params);
    }
    
    
}
