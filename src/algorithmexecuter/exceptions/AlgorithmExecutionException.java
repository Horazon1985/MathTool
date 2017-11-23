package algorithmexecuter.exceptions;

import mathtool.lang.translator.Translator;

public class AlgorithmExecutionException extends AlgorithmException {

    public AlgorithmExecutionException(String messageId, Object... params) {
        super(Translator.translateOutputMessage(messageId, params));
    }

}
