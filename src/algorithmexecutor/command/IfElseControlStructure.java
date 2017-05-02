package algorithmexecutor.command;

import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.List;

public class IfElseControlStructure extends ControlStructure {

    private final BooleanExpression condition;
    private final List<AlgorithmCommand> commandsIfPart;
    private List<AlgorithmCommand> commandsElsePart = new ArrayList<>();

    public IfElseControlStructure(BooleanExpression condition, List<AlgorithmCommand> commandsIfPart) {
        this.condition = condition;
        this.commandsIfPart = commandsIfPart;
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
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        if (this.condition.evaluate()) {
            return AlgorithmExecutor.executeBlock(this.commandsIfPart);
        }
        return AlgorithmExecutor.executeBlock(this.commandsElsePart);
    }

    @Override
    public String toString() {
        String ifElseCommandString = "if (" + this.condition.toString() + ") {";
        for (AlgorithmCommand c : this.commandsIfPart) {
            ifElseCommandString += c.toString() + "; \n";
        }
        if (this.commandsElsePart.isEmpty()) {
            ifElseCommandString += "} else {";
            for (AlgorithmCommand c : this.commandsElsePart) {
                ifElseCommandString += c.toString() + "; \n";
            }
        }
        return ifElseCommandString + "}";
    }

}
