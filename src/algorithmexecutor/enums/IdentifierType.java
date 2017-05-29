package algorithmexecutor.enums;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.logicalexpression.classes.LogicalExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecutor.booleanexpression.BooleanExpression;

public enum IdentifierType {

    EXPRESSION, BOOLEAN_EXPRESSION, MATRIX_EXPRESSION;

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
