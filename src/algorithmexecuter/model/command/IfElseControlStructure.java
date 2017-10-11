package algorithmexecuter.model.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.booleanexpression.BooleanExpression;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.identifier.Identifier;
import exceptions.EvaluationException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IfElseControlStructure extends ControlStructure {

    private final BooleanExpression condition;
    private final List<AlgorithmCommand> commandsIfPart;
    private List<AlgorithmCommand> commandsElsePart = new ArrayList<>();

    public IfElseControlStructure(BooleanExpression condition, List<AlgorithmCommand> commandsIfPart) {
        this.condition = condition;
        this.commandsIfPart = commandsIfPart;
        this.commandBlocks = (List<AlgorithmCommand>[]) Array.newInstance(new ArrayList<>().getClass(), 2);
        this.commandBlocks[0] = commandsIfPart;
        this.commandBlocks[1] = this.commandsElsePart;
    }

    public BooleanExpression getCondition() {
        return condition;
    }

    public List<AlgorithmCommand> getCommandsIfPart() {
        return commandsIfPart;
    }

    public List<AlgorithmCommand> getCommandsElsePart() {
        return commandsElsePart;
    }

    public void setCommandsElsePart(List<AlgorithmCommand> commands) {
        this.commandsElsePart = commands;
        this.commandBlocks[1] = commandsElsePart;
    }

    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException {
        Map<String, AbstractExpression> valuesMap = CompilerUtils.extractValuesOfIdentifiers(scopeMemory);
        if (this.condition.evaluate(valuesMap)) {
            return AlgorithmExecuter.executeConnectedBlock(scopeMemory, this.commandsIfPart);
        }
        return AlgorithmExecuter.executeConnectedBlock(scopeMemory, this.commandsElsePart);
    }

    @Override
    public String toString() {
        String ifElseCommandString = "if (" + this.condition.toString() + ") {";
        for (AlgorithmCommand c : this.commandsIfPart) {
            ifElseCommandString += c.toString() + "; \n";
        }
        if (!this.commandsElsePart.isEmpty()) {
            ifElseCommandString += "} else {";
            for (AlgorithmCommand c : this.commandsElsePart) {
                ifElseCommandString += c.toString() + "; \n";
            }
        }
        return ifElseCommandString + "}";
    }

    @Override
    public String toCommandString() {
        String commandString = Keyword.IF.getValue() + ReservedChars.OPEN_BRACKET.getStringValue() + this.condition.toString()
                + ReservedChars.CLOSE_BRACKET.getStringValue() + ReservedChars.BEGIN.getStringValue();

        for (AlgorithmCommand command : this.commandsIfPart) {
            commandString += command.toCommandString();
        }
        commandString += ReservedChars.END.getStringValue();

        if (!this.commandsElsePart.isEmpty()) {
            commandString += Keyword.ELSE.getValue() + ReservedChars.BEGIN.getStringValue();
            for (AlgorithmCommand command : this.commandsElsePart) {
                commandString += command.toCommandString();
            }
            commandString += ReservedChars.END.getStringValue();
        }

        return commandString;
    }

}
