package algorithmexecuter.model.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.booleanexpression.BooleanExpression;
import algorithmexecuter.exceptions.AlgorithmBreakException;
import algorithmexecuter.exceptions.AlgorithmContinueException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.identifier.Identifier;
import exceptions.EvaluationException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhileControlStructure extends ControlStructure {

    private final BooleanExpression condition;
    private final List<AlgorithmCommand> commands;

    public WhileControlStructure(BooleanExpression condition, List<AlgorithmCommand> commands) {
        this.condition = condition;
        this.commands = commands;
        this.commandBlocks = (List<AlgorithmCommand>[]) Array.newInstance(new ArrayList<>().getClass(), 1);
        this.commandBlocks[0] = commands;
    }

    public BooleanExpression getCondition() {
        return condition;
    }

    public List<AlgorithmCommand> getCommands() {
        return commands;
    }

    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException {
        Map<String, AbstractExpression> valuesMap = CompilerUtils.extractValuesOfIdentifiers(scopeMemory);
        Identifier result = null;
        while (this.condition.evaluate(valuesMap)) {
            try {
                result = AlgorithmExecuter.executeConnectedBlock(scopeMemory, this.commands);
                // Identifierwerte aktualisieren.
                valuesMap = CompilerUtils.extractValuesOfIdentifiers(scopeMemory);
                if (result != null) {
                    return result;
                }
            } catch (AlgorithmBreakException e) {
                return null;
            } catch (AlgorithmContinueException e) {
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String whileCommandString = "while (" + this.condition.toString() + ") {";
        for (AlgorithmCommand c : this.commands) {
            whileCommandString += c.toString() + "; \n";
        }
        return whileCommandString + "}";
    }

}
