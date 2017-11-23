package algorithmexecuter.model.identifier;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.model.AlgorithmMemory;
import java.util.Arrays;
import java.util.Objects;

public class Identifier {

    private final IdentifierType type;
    private final String name;
    private AbstractExpression value;
    private Object[] stringValue;

    private Identifier(IdentifierType type, String name) {
        this.type = type;
        this.name = name;
    }

    public IdentifierType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public AbstractExpression getValue() {
        return value;
    }

    public void setValue(AbstractExpression value) {
        this.value = value;
    }

    public Object[] getStringValue() {
        return stringValue;
    }

    public void setStringValue(Object[] stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.type);
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.value);
        hash = 79 * hash + Arrays.deepHashCode(this.stringValue);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Identifier other = (Identifier) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Arrays.deepEquals(this.stringValue, other.stringValue)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (this.type == IdentifierType.STRING) {
            String result = "Identifier[type = " + this.type + ", name = " + this.name
                    + ", stringValue = ";
            if (this.stringValue != null) {
                return result + stringArrayToString(this.stringValue) + "]";
            } else {
                return result + "null]";
            }
        }
        return "Identifier[type = " + this.type + ", name = " + this.name
                + ", value = " + this.value + "]";
    }

    private String stringArrayToString(Object[] objects) {
        String result = "(";
        for (int i = 0; i < objects.length; i++) {
            result += objects[i];
            if (i < objects.length - 1) {
                result += ", ";
            }
        }
        return result + ")";
    }

    public static Identifier createIdentifier(String identifierName, IdentifierType type) {
        return new Identifier(type, identifierName);
    }

    public static Identifier createIdentifier(AlgorithmMemory scopeMemory, String identifierName, IdentifierType type) {
        if (scopeMemory.containsIdentifier(identifierName)) {
            return scopeMemory.getMemory().get(identifierName);
        }
        return new Identifier(type, identifierName);
    }

}
