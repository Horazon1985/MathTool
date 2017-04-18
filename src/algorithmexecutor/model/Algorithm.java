package algorithmexecutor.model;

import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.ExecutionExecptionTexts;
import algorithmexecutor.identifier.Identifier;
import java.util.List;

public class Algorithm {

    private final String name;
    private final Identifier[] inputParameters;
    private final Identifier outputParameter;
    private final List<AlgorithmCommand> commands;
    
    public Algorithm(String name, Identifier[] inputParameters, Identifier outputParameter, List<AlgorithmCommand> commands) {
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

    public String getName() {
        return name;
    }

    public Identifier[] getInputParameters() {
        return inputParameters;
    }

    public Identifier getOutputParameter() {
        return outputParameter;
    }

    public List<AlgorithmCommand> getCommands() {
        return commands;
    }
    
    public Identifier execute() throws AlgorithmExecutionException {
        // Prüfung, ob alle Parameter Werte besitzen. Sollte eigentlich stets der Fall sein.
        for (Identifier inputParameter : this.inputParameters) {
            if (inputParameter.getValue() == null) {
                throw new AlgorithmExecutionException(ExecutionExecptionTexts.ALGORITHM_NOT_ALL_INPUT_PARAMETERS_SET);
            }
        }
        for (AlgorithmCommand command : this.commands) {
            command.execute();
        }
        return null;
    }
    
}
