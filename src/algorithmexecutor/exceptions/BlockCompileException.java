package algorithmexecutor.exceptions;

import mathtool.lang.translator.Translator;

public class BlockCompileException extends AlgorithmCompileException {
    
    public BlockCompileException(String message, Object... params) {
        super(Translator.translateOutputMessage(message, params));
    }
    
}
