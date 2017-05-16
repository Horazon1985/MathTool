package algorithmexecutor;

import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.interfaces.IdentifierValidator;
import java.util.Map;

public interface IdentifierConditionValidator extends IdentifierValidator {
    
    public boolean isValidIdentifier(String identifierName, Map<String, AbstractExpression> valuesMap);
    
}
