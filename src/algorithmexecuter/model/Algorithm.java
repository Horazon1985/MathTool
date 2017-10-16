package algorithmexecuter.model;

import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.model.command.AlgorithmCommand;
import algorithmexecuter.model.command.IfElseControlStructure;
import algorithmexecuter.model.command.WhileControlStructure;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.exceptions.constants.ExecutionExceptionTexts;
import algorithmexecuter.model.command.DoWhileControlStructure;
import algorithmexecuter.model.command.ForControlStructure;
import algorithmexecuter.model.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    private final String name;
    private final Identifier[] inputParameters;
    private final IdentifierType returnType;
    private final List<AlgorithmCommand> commands;

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
                    WhileControlStructure whileCommand = (WhileControlStructure) c;
                    appendCommands(whileCommand.getCommands(), false);
                } else if (c.isDoWhileControlStructure()) {
                    DoWhileControlStructure doWhileCommand = (DoWhileControlStructure) c;
                    appendCommands(doWhileCommand.getCommands(), false);
                } else if (c.isForControlStructure()) {
                    ForControlStructure forCommand = (ForControlStructure) c;
                    appendCommands(forCommand.getInitialization(), false);
                    appendCommands(forCommand.getEndLoopCommands(), false);
                    appendCommands(forCommand.getLoopAssignment(), false);
                    appendCommands(forCommand.getCommands(), false);
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

    public String toCommandString() {
        String commandString = "";
        if (this.returnType != null) {
            commandString += this.returnType.getValue() + " ";
        }
        commandString += this.name + ReservedChars.OPEN_BRACKET.getStringValue();
        for (int i = 0; i < this.inputParameters.length; i++) {
            commandString += this.inputParameters[i].getType().getValue() + " " + this.inputParameters[i].getName();
            if (i < this.inputParameters.length - 1) {
                commandString += ReservedChars.ARGUMENT_SEPARATOR.getStringValue() + " ";
            }
        }
        commandString += ReservedChars.CLOSE_BRACKET.getStringValue() + ReservedChars.BEGIN.getStringValue();
        
        for (AlgorithmCommand command : this.commands) {
            commandString += command.toCommandString();
        }
        
        return commandString + ReservedChars.END.getStringValue();
        
    }

}
