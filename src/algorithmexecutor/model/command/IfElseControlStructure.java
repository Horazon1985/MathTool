package algorithmexecutor.model.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.CompilerUtils;
import algorithmexecutor.booleanexpression.BooleanExpression;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.model.identifier.Identifier;
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
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        Map<String, AbstractExpression> valuesMap = CompilerUtils.extractValuesOfIdentifiers(getAlgorithm());
        if (this.condition.evaluate(valuesMap)) {
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
        if (!this.commandsElsePart.isEmpty()) {
            ifElseCommandString += "} else {";
            for (AlgorithmCommand c : this.commandsElsePart) {
                ifElseCommandString += c.toString() + "; \n";
            }
        }
        return ifElseCommandString + "}";
    }

}
