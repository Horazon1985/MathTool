package algorithmexecuter.model.command;

import algorithmexecuter.exceptions.AlgorithmExecutionException;
import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.Algorithm;
import algorithmexecuter.model.AlgorithmMemory;
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
    
    public boolean isDeclareIDentifierCommand() {
        return this instanceof DeclareIdentifierCommand;
    }
    
    public boolean isVoidCommand() {
        return this instanceof VoidCommand;
    }
    
    public boolean isControlStructure() {
        return this instanceof ControlStructure;
    }
    
    public boolean isIfElseControlStructure() {
        return this instanceof IfElseControlStructure;
    }
    
    public boolean isWhileControlStructure() {
        return this instanceof WhileControlStructure;
    }
    
    public boolean isDoWhileControlStructure() {
        return this instanceof DoWhileControlStructure;
    }
    
    public boolean isForControlStructure() {
        return this instanceof ForControlStructure;
    }
    
    public boolean isReturnCommand() {
        return this instanceof ReturnCommand;
    }
    
    public abstract Identifier execute(AlgorithmMemory scopeMemory) throws AlgorithmExecutionException, EvaluationException;
    
}
