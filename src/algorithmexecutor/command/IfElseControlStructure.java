package algorithmexecutor.command;

import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.condition.BooleanCondition;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.List;

public class IfElseControlStructure extends ControlStructure {

    private final BooleanCondition condition;
    private final List<AlgorithmCommand> commandsIfPart;
    private List<AlgorithmCommand> commandsElsePart = new ArrayList<>();

    public IfElseControlStructure(BooleanCondition condition, List<AlgorithmCommand> commandsIfPart) {
        this.condition = condition;
        this.commandsIfPart = commandsIfPart;
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

}
