package algorithmexecuter.exceptions;

public class ParseControlStructureException extends AlgorithmCompileException {
    
    public ParseControlStructureException(Exception e) {
        super(e);
    }
    
    public ParseControlStructureException(String message, Object... params) {
        super(message, params);
    }
    
    
}
