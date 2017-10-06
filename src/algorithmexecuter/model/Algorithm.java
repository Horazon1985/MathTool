package algorithmexecuter.model;

import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.model.command.AlgorithmCommand;
import algorithmexecuter.model.command.IfElseControlStructure;
import algorithmexecuter.model.command.WhileControlStructure;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.exceptions.constants.ExecutionExceptionTexts;
import algorithmexecuter.model.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    private final String name;
    private final Identifier[] inputParameters;
    private final IdentifierType returnType;
    private final List<AlgorithmCommand> commands;

    private Identifier[] inputParameterValues = new Identifier[0];

    private Algorithm(String name, Identifier[] inputParameters, IdentifierType returnType, List<AlgorithmCommand> commands) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.returnType = returnType;
        this.commands = commands;
    }

    public Algorithm(String name, Identifier[] inputParameters, IdentifierType returnType) {
        this(name, inputParameters, returnType, new ArrayList<AlgorithmCommand>());
    }

    public Signature getSignature() {
        IdentifierType[] identifierTypes = new IdentifierType[this.inputParameters.length];
        for (int i = 0; i < this.inputParameters.length; i++) {
            identifierTypes[i] = this.inputParameters[i].getType();
        }
        return new Signature(this.returnType, this.name, identifierTypes);
    }

    public String getName() {
        return name;
    }

    public Identifier[] getInputParameters() {
        return inputParameters;
    }

    public IdentifierType getReturnType() {
        return returnType;
    }

    public List<AlgorithmCommand> getCommands() {
        return commands;
    }

    public void setInputParameterValues(Identifier[] inputParameterValues) {
        this.inputParameterValues = inputParameterValues;
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
            if (c.isControlStructure()) {
                if (c.isIfElseControlStructure()) {
                    IfElseControlStructure ifElseCommand = (IfElseControlStructure) c;
                    appendCommands(ifElseCommand.getCommandsIfPart(), false);
                    appendCommands(ifElseCommand.getCommandsElsePart(), false);
                } else if (c.isWhileControlStructure()) {
                    WhileControlStructure ifElseCommand = (WhileControlStructure) c;
                    appendCommands(ifElseCommand.getCommands(), false);
                }
            }
            if (topLevel) {
                this.commands.add(c);
            }
        }
    }

    public AlgorithmMemory initInputParameter(Identifier[] identifiers) {
        AlgorithmMemory memory = new AlgorithmMemory(this);
        for (int i = 0; i < this.inputParameters.length; i++) {
            this.inputParameters[i].setValue(identifiers[i].getValue());
            memory.addToMemoryInRuntime(identifiers[i]);
        }
        return memory;
    }

    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        // Leeren Algorithmus nur im void-Fall akzeptieren.
        if (this.commands.isEmpty()) {
            if (this.returnType == null) {
                return null;
            } else {
                throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_RETURN_TYPE_EXPECTED);
            }
        }

        // Prüfung, ob alle Parameter Werte besitzen. Sollte eigentlich stets der Fall sein.
        checkForIdentifierWithoutValues();

        return AlgorithmExecuter.executeConnectedBlock(getInitialAlgorithmMemory(), this.commands);
    }

    private void checkForIdentifierWithoutValues() throws AlgorithmExecutionException {
        for (int i = 0; i < this.inputParameters.length; i++) {
            if (this.inputParameters[i].getValue() == null) {
                throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_ALGORITHM_NOT_ALL_INPUT_PARAMETERS_SET, i, this.getName());
            }
        }
    }

    private AlgorithmMemory getInitialAlgorithmMemory() {
        return new AlgorithmMemory(this, this.getInputParameters());
    }

}
