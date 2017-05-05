package algorithmexecutor.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.CompilerUtils;
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.List;
import java.util.Map;

public class WhileControlStructure extends ControlStructure {

    private final BooleanExpression condition;
    private final List<AlgorithmCommand> commands;

    public WhileControlStructure(BooleanExpression condition, List<AlgorithmCommand> commandsIfPart) {
        this.condition = condition;
        this.commands = commandsIfPart;
    }

    public BooleanExpression getCondition() {
        return condition;
    }

    public List<AlgorithmCommand> getCommands() {
        return commands;
    }

    @Override
    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        Map<String, AbstractExpression> valuesMap = CompilerUtils.extractValuesOfIdentifiers(getAlgorithm());
        Identifier result = null;
        while (this.condition.evaluate(valuesMap)) {
            result = AlgorithmExecutor.executeBlock(this.commands);
            if (result != null) {
                return result;
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        String ifElseCommandString = "while (" + this.condition.toString() + ") {";
        for (AlgorithmCommand c : this.commands) {
            ifElseCommandString += c.toString() + "; \n";
        }
        return ifElseCommandString + "}";
    }

}
