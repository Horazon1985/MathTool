package algorithmexecutor.command.condition;

import algorithmexecutor.exceptions.BooleanConditionException;
import algorithmexecutor.exceptions.CompileExceptionTexts;

public abstract class BooleanCondition {
    
    public final BooleanConstant TRUE = new BooleanConstant(true);
    public final BooleanConstant FALSE = new BooleanConstant(false);
    
    public abstract boolean evaluate();
    
    public static BooleanCondition build(String input) throws BooleanConditionException {

        
        
        
        throw new BooleanConditionException(CompileExceptionTexts.UNKNOWN_ERROR);
    }
    
}
