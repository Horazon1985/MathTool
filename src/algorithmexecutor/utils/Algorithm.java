package algorithmexecutor.utils;

import algorithmexecutor.command.AlgorithmCommand;
import java.util.List;

public class Algorithm {

    private final String name;
    private final InputOutputParameter[] inputParameters;
    private final InputOutputParameter outputParameter;
    private final List<AlgorithmCommand> commands;
    
    public Algorithm(String name, InputOutputParameter outputParameter, List<AlgorithmCommand> commands, InputOutputParameter... inputParameters) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.outputParameter = outputParameter;
        this.commands = commands;
    }

    public String getSignature() {
        String signature = this.name + "(";
        for (int i = 0; i < this.inputParameters.length; i++) {
            signature += this.inputParameters[i].getType();
            if (i < this.inputParameters.length - 1) {
                signature += ",";
            }
        }   
        return signature + ")";
    }
    
}
