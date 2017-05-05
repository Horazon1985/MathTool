package algorithmexecutor.model;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.expression.classes.Variable;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.logicalexpression.classes.LogicalVariable;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.AlgorithmCommand;
import algorithmexecutor.command.IfElseControlStructure;
import algorithmexecutor.enums.IdentifierTypes;
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
    private final IdentifierTypes returnType;
    private final List<AlgorithmCommand> commands;

    public Algorithm(String name, Identifier[] inputParameters, IdentifierTypes returnType, List<AlgorithmCommand> commands) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.returnType = returnType;
        this.commands = commands;
        // Speicher für Identifier allokieren.
        AlgorithmExecutor.getMemoryMap().put(this, new AlgorithmMemory(inputParameters));
    }

    public Algorithm(String name, Identifier[] inputParameters, IdentifierTypes returnType) {
        this(name, inputParameters, returnType, new ArrayList<AlgorithmCommand>());
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

    public IdentifierTypes getReturnType() {
        return returnType;
    }

    public List<AlgorithmCommand> getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        String algorithm = "";
        if (this.returnType != null) {
            algorithm += this.returnType + " ";
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

    public void appendCommands(List<AlgorithmCommand> commands) {
        appendCommands(commands, true);
    }

    private void appendCommands(List<AlgorithmCommand> commands, boolean topLevel) {
        for (AlgorithmCommand c : commands) {
            c.setAlgorithm(this);
            if (c.isControllStructure()) {
                if (c.isIfElseControllStructure()) {
                    IfElseControlStructure ifElseCommand = (IfElseControlStructure) c;
                    appendCommands(ifElseCommand.getCommandsIfPart(), false);
                    appendCommands(ifElseCommand.getCommandsElsePart(), false);
                }
            }
            if (topLevel) {
                this.commands.add(c);
            }
        }
    }

    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        // Leeren Algorithmus nur im void-Fall akzeptieren.
        if (this.commands.isEmpty()) {
            if (this.returnType == null) {
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

        Identifier resultIdentifier = AlgorithmExecutor.executeBlock(this.commands);

        // Nach Ausführung des Algorithmus: Lokale Variablen wieder löschen.
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

}
