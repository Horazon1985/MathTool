package algorithmexecuter.enums;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecuter.booleanexpression.BooleanExpression;

public enum IdentifierType {

    EXPRESSION("expression"), BOOLEAN_EXPRESSION("booleanexpression"), MATRIX_EXPRESSION("matrixexpression"), STRING("string");

    private final String value;
    
    IdentifierType(String value){
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    public boolean isSameOrGeneralTypeOf(IdentifierType type) {
        if (type == EXPRESSION && (this == EXPRESSION || this == MATRIX_EXPRESSION)) {
            return true;
        }
        return this == type;
    }

    public static IdentifierType identifierTypeOf(AbstractExpression abstrExpr) {
        if (abstrExpr instanceof Expression) {
            return EXPRESSION;
        }
        if (abstrExpr instanceof BooleanExpression) {
            return BOOLEAN_EXPRESSION;
        }
        if (abstrExpr instanceof MatrixExpression) {
            return MATRIX_EXPRESSION;
        }
        return null;
    }

}
