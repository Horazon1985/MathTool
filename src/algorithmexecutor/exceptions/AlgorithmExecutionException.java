package algorithmexecutor.exceptions;

import mathtool.lang.translator.Translator;

public class AlgorithmExecutionException extends AlgorithmException {

    public AlgorithmExecutionException(String message, Object... params) {
        super(Translator.translateOutputMessage(message, params));
    }

}
