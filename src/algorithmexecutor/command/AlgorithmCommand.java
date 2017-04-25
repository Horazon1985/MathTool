package algorithmexecutor.command;

import algorithmexecutor.exceptions.AlgorithmExecutionException;
import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import exceptions.EvaluationException;

public abstract class AlgorithmCommand {
    
    protected Algorithm algorithm;

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public Algorithm getAlgorithm() {
        return algorithm;
    }
    
    public boolean isAssignValueCommand() {
        return this instanceof AssignValueCommand;
    }
    
    public boolean isVoidCommand() {
        return this instanceof VoidCommand;
    }
    
    public boolean isControllStructure() {
        return this instanceof ControllStructure;
    }
    
    public boolean isReturnCommand() {
        return this instanceof ReturnCommand;
    }
    
    public abstract Identifier execute() throws AlgorithmExecutionException, EvaluationException;
    
}
