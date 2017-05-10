package algorithmexecutor.exceptions;

import mathtool.lang.translator.Translator;

public class AlgorithmCompileException extends AlgorithmException {

    public AlgorithmCompileException() {
    }
    
    public AlgorithmCompileException(Exception e) {
        super(e.getMessage());
    }
    
    public AlgorithmCompileException(String message, Object... params) {
        super(Translator.translateOutputMessage(message, params));
    }
    
}
