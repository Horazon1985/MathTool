package algorithmexecutor.model;

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
        String signature = this.getName() + "(";
        for (int i = 0; i < this.getInputParameters().length; i++) {
            signature += this.getInputParameters()[i].getType();
            if (i < this.getInputParameters().length - 1) {
                signature += ",";
            }
        }   
        return signature + ")";
    }

    public String getName() {
        return name;
    }

    public InputOutputParameter[] getInputParameters() {
        return inputParameters;
    }

    public InputOutputParameter getOutputParameter() {
        return outputParameter;
    }

    public List<AlgorithmCommand> getCommands() {
        return commands;
    }
    
}
