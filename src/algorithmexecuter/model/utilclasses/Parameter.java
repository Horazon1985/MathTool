package algorithmexecuter.model.utilclasses;

import abstractexpressions.expression.classes.Expression;
import abstractexpressions.interfaces.AbstractExpression;
import abstractexpressions.matrixexpression.classes.MatrixExpression;
import algorithmexecuter.booleanexpression.BooleanExpression;
import algorithmexecuter.enums.IdentifierType;

public class Parameter {

    private IdentifierType type;
    private AbstractExpression value;
    private MalString malString;

    public IdentifierType getType() {
        return type;
    }

    public void setType(IdentifierType type) {
        this.type = type;
    }

    public AbstractExpression getValue() {
        return value;
    }

    public void setValue(AbstractExpression value) {
        this.value = value;
    }

    public MalString getMalString() {
        return malString;
    }

    public void setMalString(MalString malString) {
        this.malString = malString;
    }

    public Parameter(AbstractExpression value) {
        this.type = IdentifierType.identifierTypeOf(value);
        this.value = value;
    }

    public Parameter(MalString malString) {
        this.type = IdentifierType.STRING;
        this.malString = malString;
    }

    @Override
    public String toString() {
        String parameter = "Parameter[type = " + this.type + ", ";
        if (this.type == IdentifierType.STRING) {
            return parameter + "string = " + this.malString + "]";
        }
        return parameter + "value = " + this.value + "]";
    }

}
