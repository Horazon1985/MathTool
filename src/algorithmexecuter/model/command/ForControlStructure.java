package algorithmexecuter.model.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.ExecutionUtils;
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

public class ForControlStructure extends ControlStructure {

    private final List<AlgorithmCommand> initialization;
    private final BooleanExpression endLoopCondition;
    private final List<AlgorithmCommand> loopAssignment;

    public ForControlStructure(List<AlgorithmCommand> commands, List<AlgorithmCommand> initialization, BooleanExpression endLoopCondition, List<AlgorithmCommand> loopAssignment) {
        this.initialization = initialization;
        this.endLoopCondition = endLoopCondition;
        this.loopAssignment = loopAssignment;
        this.commandBlocks = (List<AlgorithmCommand>[]) Array.newInstance(new ArrayList<>().getClass(), 1);
        this.commandBlocks[0] = commands;
    }

    public List<AlgorithmCommand> getInitialization() {
        return this.initialization;
    }

    public BooleanExpression getEndLoopCondition() {
        return this.endLoopCondition;
    }

    public List<AlgorithmCommand> getLoopAssignment() {
        return this.loopAssignment;
    }

    public List<AlgorithmCommand> getCommands() {
        return this.commandBlocks[0];
    }

    @Override
    public Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException {
        Identifier result = null;

        AlgorithmMemory memoryBeforeForLoopExecution = scopeMemory.copyMemory();

        AlgorithmExecuter.executeBlock(memoryBeforeForLoopExecution, this.initialization);
        Map<String, AbstractExpression> valuesMap = CompilerUtils.extractValuesOfIdentifiers(memoryBeforeForLoopExecution);
        while (this.endLoopCondition.evaluate(valuesMap)) {
            try {
                result = AlgorithmExecuter.executeBlock(memoryBeforeForLoopExecution, this.commandBlocks[0]);
                if (result != null) {
                    return result;
                }
            } catch (AlgorithmBreakException e) {
                return null;
            } catch (AlgorithmContinueException e) {
            }
            AlgorithmExecuter.executeBlock(memoryBeforeForLoopExecution, this.loopAssignment);
            // Identifierwerte aktualisieren.
            valuesMap = CompilerUtils.extractValuesOfIdentifiers(memoryBeforeForLoopExecution);
        }

        // Speicher vor der Ausführung des Blocks aktualisieren.
        ExecutionUtils.updateMemoryBeforeBlockExecution(scopeMemory, memoryBeforeForLoopExecution);

        return result;
    }

    @Override
    public String toString() {
        String doWhileCommandString = "for (" + this.initialization + "; " + this.endLoopCondition + "; " + this.loopAssignment + "; " + "){";
        doWhileCommandString = this.commandBlocks[0].stream().map((c) -> c.toString() + "; \n").reduce(doWhileCommandString, String::concat);
        return doWhileCommandString + "}";
    }

}
