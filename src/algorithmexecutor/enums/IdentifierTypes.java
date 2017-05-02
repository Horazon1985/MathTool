package algorithmexecutor.enums;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;

public enum IdentifierTypes {

    EXPRESSION, BOOLEAN_EXPRESSION, MATRIX_EXPRESSION;

    public boolean isSameOrGeneralTypeOf(IdentifierTypes type) {
        if (this == EXPRESSION && (type == EXPRESSION || type == MATRIX_EXPRESSION)) {
            return true;
        }
        return this == type;
    }

    public static IdentifierTypes identifierTypeOf(AbstractExpression abstrExpr) {
        if (abstrExpr instanceof Expression) {
            return EXPRESSION;
        }
        if (abstrExpr instanceof LogicalExpression) {
            return BOOLEAN_EXPRESSION;
        }
        return MATRIX_EXPRESSION;
    }

    @Override
    public String toString() {
        if (this == EXPRESSION) {
            return "expression";
        }
        if (this == BOOLEAN_EXPRESSION) {
            return "booleanexpression";
        }
        return "matrixexpression";
    }

}
