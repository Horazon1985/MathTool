package algorithmexecutor.exceptions;

import mathtool.lang.translator.Translator;

public class BooleanExpressionException extends AlgorithmException {
    
    public BooleanExpressionException(String message, Object... params) {
        super(Translator.translateOutputMessage(message, params));
    }
    
}
