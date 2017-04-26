package algorithmexecutor.command;

import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.condition.BooleanCondition;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.List;

public class IfElseControlStructure extends ControlStructure {

    private BooleanCondition condition;
    private final List<AlgorithmCommand> commandsIfPart = new ArrayList<>();
    private final List<AlgorithmCommand> commandsElsePart = new ArrayList<>();

    public void appendCommandInIfPart(AlgorithmCommand command) {
        this.commandsIfPart.add(command);
    }

    public void appendCommandInElsePart(AlgorithmCommand command) {
        this.commandsElsePart.add(command);
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        if (this.condition.evaluate()) {
            return AlgorithmExecutor.executeBlock(this.commandsIfPart);
        }
        return AlgorithmExecutor.executeBlock(this.commandsElsePart);
    }

}
