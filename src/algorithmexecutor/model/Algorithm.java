package algorithmexecutor.model;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.expression.classes.Variable;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.logicalexpression.classes.LogicalVariable;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.ControllStructure;
import algorithmexecutor.command.ReturnCommand;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.exceptions.ExecutionExecptionTexts;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.memory.AlgorithmMemory;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        // Speicher für Identifier allokieren.
        AlgorithmExecutor.getMemoryMap().put(this, new AlgorithmMemory(inputParameters));
    }

    public Algorithm(String name, Identifier[] inputParameters, Identifier outputParameter) {
        this(name, inputParameters, outputParameter, new ArrayList<AlgorithmCommand>());
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

    @Override
    public String toString() {
        String algorithm = "";
        if (this.outputParameter != null) {
            algorithm += this.outputParameter.getType() + " ";
        }
        algorithm += this.name + "(";
        for (int i = 0; i < this.inputParameters.length; i++) {
            algorithm += this.inputParameters[i].getType() + " " + this.inputParameters[i].getName();
            if (i < this.inputParameters.length - 1) {
                algorithm += ", ";
            }
        }
        algorithm += ") {\n";
        for (AlgorithmCommand c : this.commands) {
            algorithm += c.toString() + "; \n";
        }
        return algorithm + "}";
    }
    
    public void appendCommand(AlgorithmCommand command) {
        command.setAlgorithm(this);
        this.commands.add(command);
    }
    
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        // Leeren Algorithmus nur im void-Fall akzeptieren.
        if (this.commands.isEmpty()) {
            if (this.outputParameter == null) {
                return null;
            } else {
                throw new AlgorithmExecutionException(ExecutionExecptionTexts.RETURN_TYPE_EXPECTED);
            }
        }
        AlgorithmExecutor.getMemoryMap().put(this, new AlgorithmMemory(this.getInputParameters()));

        // Prüfung, ob alle Parameter Werte besitzen. Sollte eigentlich stets der Fall sein.
        checkForIdentifierWithoutValues();
        // Variablenwerte erneut setzen.
        refreshVariableValues();
        
        Identifier resultIdentifier = null;

        int commandIndex = 0;
        while (true) {
            resultIdentifier = this.commands.get(commandIndex).execute();
            if (this.commands.get(commandIndex) instanceof ReturnCommand) {
                return resultIdentifier;
            } else {
                resultIdentifier = null;
            }
            if (isLastCommand(commandIndex)) {
                break;
            }
            commandIndex = getNextCommandIndex(commandIndex);
        }
        AlgorithmExecutor.getMemoryMap().get(this).clearMemory();
        return resultIdentifier;
    }

    private void checkForIdentifierWithoutValues() throws AlgorithmExecutionException {
        for (Identifier inputParameter : this.inputParameters) {
            if (inputParameter.getValue() == null) {
                throw new AlgorithmExecutionException(ExecutionExecptionTexts.ALGORITHM_NOT_ALL_INPUT_PARAMETERS_SET);
            }
        }
    }

    private void refreshVariableValues() {
        Map<String, Identifier> memory = AlgorithmExecutor.getMemoryMap().get(this).getMemory();
        for (String var : memory.keySet()) {
            if (memory.get(var).getValue() instanceof Expression) {
                Variable.setPreciseExpression(memory.get(var).getName(), (Expression) memory.get(var).getValue());
            } else if (memory.get(var).getValue() instanceof LogicalExpression) {
                LogicalVariable.setValue(memory.get(var).getName(), ((LogicalExpression) memory.get(var).getValue()).evaluate());
            }
        }
    }

    private boolean isLastCommand(int i) {
        if (i < 0 || i >= this.commands.size()) {
            return true;
        }
        return this.commands.get(i) instanceof ReturnCommand;
    }

    private int getNextCommandIndex(int i) {
        if (this.commands.get(i) instanceof ControllStructure) {
            // TO DO.
            return i + 1;
        }
        return i + 1;
    }

}
