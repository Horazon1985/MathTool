package algorithmexecuter.model.command;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.CompilerUtils;
import algorithmexecuter.ExecutionUtils;
import algorithmexecuter.booleanexpression.BooleanExpression;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.ReservedChars;
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
    private final List<AlgorithmCommand> endLoopCommands;
    private final BooleanExpression endLoopCondition;
    private final List<AlgorithmCommand> loopAssignment;

    public ForControlStructure(List<AlgorithmCommand> commands, List<AlgorithmCommand> initialization, List<AlgorithmCommand> endLoopCommands, BooleanExpression endLoopCondition, List<AlgorithmCommand> loopAssignment) {
        this.initialization = initialization;
        this.endLoopCommands = endLoopCommands;
        this.endLoopCondition = endLoopCondition;
        this.loopAssignment = loopAssignment;
        this.commandBlocks = (List<AlgorithmCommand>[]) Array.newInstance(new ArrayList<>().getClass(), 1);
        this.commandBlocks[0] = commands;
    }

    public List<AlgorithmCommand> getInitialization() {
        return this.initialization;
    }

    public List<AlgorithmCommand> getEndLoopCommands() {
        return this.endLoopCommands;
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

        AlgorithmMemory currentMemory = scopeMemory.copyMemory();

        AlgorithmExecuter.executeBlock(currentMemory, this.initialization);
        AlgorithmExecuter.executeBlock(currentMemory, this.endLoopCommands);
        Map<String, AbstractExpression> valuesMap = CompilerUtils.extractValuesOfIdentifiers(currentMemory);
        while (this.endLoopCondition.evaluate(valuesMap)) {
            try {
                result = AlgorithmExecuter.executeBlock(currentMemory, this.commandBlocks[0]);
                if (result != null) {
                    return result;
                }
            } catch (AlgorithmBreakException e) {
                return null;
            } catch (AlgorithmContinueException e) {
            }
            AlgorithmExecuter.executeBlock(currentMemory, this.loopAssignment);
            AlgorithmExecuter.executeBlock(currentMemory, this.endLoopCommands);
            // Identifierwerte aktualisieren.
            valuesMap = CompilerUtils.extractValuesOfIdentifiers(currentMemory);
        }

        // Speicher vor der Ausführung des Blocks aktualisieren.
        ExecutionUtils.updateMemoryBeforeBlockExecution(scopeMemory, currentMemory);

        return result;
    }

    @Override
    public String toString() {
        String forCommandString = "for (" + this.initialization + "; " + this.endLoopCommands + ", " + this.endLoopCondition + "; " + this.loopAssignment + "){";
        forCommandString = this.commandBlocks[0].stream().map((c) -> c.toString() + "; \n").reduce(forCommandString, String::concat);
        return forCommandString + "}";
    }

    @Override
    public String toCommandString() {

        String commandString = Keyword.FOR.getValue() + ReservedChars.OPEN_BRACKET.getStringValue();

        for (AlgorithmCommand command : this.initialization) {
            commandString += command.toCommandString();
        }
        for (AlgorithmCommand command : this.endLoopCommands) {
            commandString += command.toCommandString();
        }
        commandString += this.endLoopCondition.toString() + ReservedChars.LINE_SEPARATOR.getStringValue();
        for (AlgorithmCommand command : this.loopAssignment) {
            commandString += command.toCommandString();
        }

        commandString += ReservedChars.CLOSE_BRACKET.getStringValue() + ReservedChars.BEGIN.getStringValue();
        for (AlgorithmCommand command : this.commandBlocks[0]) {
            commandString += command.toCommandString();
        }
        return commandString + ReservedChars.END.getStringValue();
    }

}
