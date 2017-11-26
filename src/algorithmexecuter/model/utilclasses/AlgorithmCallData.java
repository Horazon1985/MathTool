package algorithmexecuter.model.utilclasses;

import algorithmexecuter.model.Signature;

public class AlgorithmCallData {

    private final Signature signature;
    private final Parameter[] parameterValues;

    public AlgorithmCallData(Signature signature, Parameter[] parameterValues) {
        this.signature = signature;
        this.parameterValues = parameterValues;
    }

    public Signature getSignature() {
        return signature;
    }

    public Parameter[] getParameterValues() {
        return parameterValues;
    }

}
