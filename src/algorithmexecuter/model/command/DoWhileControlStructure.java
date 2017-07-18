package algorithmexecuter.model.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.booleanexpression.BooleanExpression;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.identifier.Identifier;
import exceptions.EvaluationException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoWhileControlStructure extends ControlStructure {

    private final BooleanExpression condition;

    public DoWhileControlStructure(List<AlgorithmCommand> commands, BooleanExpression condition) {
        this.condition = condition;
        this.commandBlocks = (List<AlgorithmCommand>[]) Array.newInstance(new ArrayList<>().getClass(), 1);
        this.commandBlocks[0] = commands;
    }

    public BooleanExpression getCondition() {
        return condition;
    }
    
    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException {
        Map<String, AbstractExpression> valuesMap;
        Identifier result = null;
        do {
            result = AlgorithmExecuter.executeBlock(scopeMemory, this.commandBlocks[0]);
            // Identifierwerte aktualisieren.
            valuesMap = CompilerUtils.extractValuesOfIdentifiers(scopeMemory);
            if (result != null) {
                return result;
            }
        } while (this.condition.evaluate(valuesMap));
        return result;
    }
    
    @Override
    public String toString() {
        
        String doWhileCommandString = "do (";
        doWhileCommandString = this.commandBlocks[0].stream().map((c) -> c.toString() + "; \n").reduce(doWhileCommandString, String::concat);
        doWhileCommandString += "} while (" + this.condition.toString() + ")";
        return doWhileCommandString;
    }

}
