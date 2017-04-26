package algorithmexecutor.command;

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
            return executeBlock(this.commandsIfPart);
        }
        return executeBlock(this.commandsElsePart);
    }

    private Identifier executeBlock(List<AlgorithmCommand> commands) throws AlgorithmExecutionException, EvaluationException {
        Identifier resultIdentifier = null;
        for (int i = 0; i < commands.size(); i++) {
            resultIdentifier = commands.get(i).execute();
            if (commands.get(i) instanceof ReturnCommand) {
                return resultIdentifier;
            } else {
                resultIdentifier = null;
            }
        }
        return resultIdentifier;
    }

}
