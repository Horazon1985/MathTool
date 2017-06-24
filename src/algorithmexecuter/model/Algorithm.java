package algorithmexecuter.model;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.expression.classes.Variable;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.logicalexpression.classes.LogicalVariable;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import abstractexpressions.matrixexpression.classes.MatrixVariable;
import algorithmexecuter.AlgorithmExecuter;
import algorithmexecuter.model.command.AlgorithmCommand;
import algorithmexecuter.model.command.IfElseControlStructure;
import algorithmexecuter.model.command.WhileControlStructure;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.exceptions.ExecutionExceptionTexts;
import algorithmexecuter.model.identifier.Identifier;
import exceptions.EvaluationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Algorithm {

    private final String name;
    private final Identifier[] inputParameters;
    private final IdentifierType returnType;
    private final List<AlgorithmCommand> commands;

    private Identifier[] inputParameterValues = new Identifier[0];

    private Algorithm(String name, Identifier[] inputParameters, IdentifierType returnType, List<AlgorithmCommand> commands) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.returnType = returnType;
        this.commands = commands;
    }

    public Algorithm(String name, Identifier[] inputParameters, IdentifierType returnType) {
        this(name, inputParameters, returnType, new ArrayList<AlgorithmCommand>());
    }

    public Signature getSignature() {
        IdentifierType[] identifierTypes = new IdentifierType[this.inputParameters.length];
        for (int i = 0; i < this.inputParameters.length; i++) {
            identifierTypes[i] = this.inputParameters[i].getType();
        }
        return new Signature(this.returnType, this.name, identifierTypes);
    }

    public String getName() {
        return name;
    }

    public Identifier[] getInputParameters() {
        return inputParameters;
    }

    public IdentifierType getReturnType() {
        return returnType;
    }

    public List<AlgorithmCommand> getCommands() {
        return commands;
    }

    public void setInputParameterValues(Identifier[] inputParameterValues) {
        this.inputParameterValues = inputParameterValues;
    }

    @Override
    public String toString() {
        String algorithm = "";
        if (this.returnType != null) {
            algorithm += this.returnType + " ";
        }
        algorithm += this.name + "(";
        for (int i = 0; i < this.inputParameters.length; i++) {
            algorithm += this.inputParameters[i].getType() + " " + this.inputParameters[i].getName();
            if (i < this.inputParameters.length - 1) {
                algorithm += ", ";
            }
        }
        algorithm += ") {\n";
        for (AlgorithmCommand c : this.commands) {
            algorithm += c.toString() + "; \n";
        }
        return algorithm + "}";
    }

    public void appendCommand(AlgorithmCommand command) {
        command.setAlgorithm(this);
        this.commands.add(command);
    }

    public void appendCommands(List<AlgorithmCommand> commands) {
        appendCommands(commands, true);
    }

    private void appendCommands(List<AlgorithmCommand> commands, boolean topLevel) {
        for (AlgorithmCommand c : commands) {
            c.setAlgorithm(this);
            if (c.isControlStructure()) {
                if (c.isIfElseControlStructure()) {
                    IfElseControlStructure ifElseCommand = (IfElseControlStructure) c;
                    appendCommands(ifElseCommand.getCommandsIfPart(), false);
                    appendCommands(ifElseCommand.getCommandsElsePart(), false);
                } else if (c.isWhileControlStructure()) {
                    WhileControlStructure ifElseCommand = (WhileControlStructure) c;
                    appendCommands(ifElseCommand.getCommands(), false);
                }
            }
            if (topLevel) {
                this.commands.add(c);
            }
        }
    }

    public AlgorithmMemory initInputParameter(Identifier[] identifiers) {
        AlgorithmMemory memory = new AlgorithmMemory(this);
        for (int i = 0; i < this.inputParameters.length; i++) {
            this.inputParameters[i].setValue(identifiers[i].getValue());
            memory.addToMemoryInRuntime(identifiers[i]);
        }
        return memory;
    }

    public Identifier execute() throws AlgorithmExecutionException, EvaluationException {
        // Leeren Algorithmus nur im void-Fall akzeptieren.
        if (this.commands.isEmpty()) {
            if (this.returnType == null) {
                return null;
            } else {
                throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_RETURN_TYPE_EXPECTED);
            }
        }

        /* 
        Speicherinitialisierung dürfte eigentlich nur in main() erforderlich sein.
        Restliche Ausführungen geschehen per Ausruf aus anderen Algorithmen und 
        dort werden entsprechende Speicher initialisiert.
         */
//        if (AlgorithmExecutor.getExecutionMemory().get(this) == null) {
//            AlgorithmExecutor.getExecutionMemory().put(this, new AlgorithmMemory(this, this.inputParameters));
//        }

        // Prüfung, ob alle Parameter Werte besitzen. Sollte eigentlich stets der Fall sein.
        checkForIdentifierWithoutValues();
        // Variablenwerte erneut setzen.
//        refreshVariableValues(null);

        return AlgorithmExecuter.executeBlock(getInitialAlgorithmMemory(), this.commands);
    }

    private void checkForIdentifierWithoutValues() throws AlgorithmExecutionException {
        for (Identifier inputParameter : this.inputParameters) {
            if (inputParameter.getValue() == null) {
                throw new AlgorithmExecutionException(ExecutionExceptionTexts.AE_ALGORITHM_NOT_ALL_INPUT_PARAMETERS_SET);
            }
        }
    }

//    private void refreshVariableValues(AlgorithmMemory scopeMemory) {
//        Map<String, Identifier> memoryMap = scopeMemory.getMemory();
//        for (String var : memoryMap.keySet()) {
//            if (memoryMap.get(var).getValue() instanceof Expression) {
//                Variable.setPreciseExpression(memoryMap.get(var).getName(), (Expression) memoryMap.get(var).getValue());
//            } else if (memoryMap.get(var).getValue() instanceof LogicalExpression) {
//                LogicalVariable.setValue(memoryMap.get(var).getName(), ((LogicalExpression) memoryMap.get(var).getValue()).evaluate());
//            } else if (memoryMap.get(var).getValue() instanceof MatrixExpression) {
//                MatrixVariable.setValue(memoryMap.get(var).getName(), (MatrixExpression) memoryMap.get(var).getValue());
//            }
//        }
//    }
    
    private AlgorithmMemory getInitialAlgorithmMemory() {
        return new AlgorithmMemory(this, this.getInputParameters());
    }

}
