package algorithmexecuter.exceptions;

import mathtool.lang.translator.Translator;

public class AlgorithmCompileException extends AlgorithmException {

    public AlgorithmCompileException(Exception e) {
        super(e.getMessage());
    }
    
    public AlgorithmCompileException(String messageId, Object... params) {
        super(Translator.translateOutputMessage(messageId, params));
    }
    
}
