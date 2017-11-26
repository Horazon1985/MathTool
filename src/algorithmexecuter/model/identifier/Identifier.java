package algorithmexecuter.model.identifier;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.model.AlgorithmMemory;
import algorithmexecuter.model.utilclasses.MalString;
import java.util.Objects;

public class Identifier {

    private final IdentifierType type;
    private final String name;
    private AbstractExpression value;
    private MalString stringValue;

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

    public MalString getMalString() {
        return stringValue;
    }

    public void setStringValue(MalString stringValue) {
        this.stringValue = stringValue;
    }

    public void setAllValuesFromAnotherIdentifier(Identifier identifier) {
        this.value = identifier.value;
        this.stringValue = identifier.stringValue;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.type);
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + Objects.hashCode(this.value);
        hash = 43 * hash + Objects.hashCode(this.stringValue);
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
        if (!Objects.equals(this.stringValue, other.stringValue)) {
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
                return result + malStringToString(this.stringValue) + "]";
            } else {
                return result + "null]";
            }
        }
        return "Identifier[type = " + this.type + ", name = " + this.name
                + ", value = " + this.value + "]";
    }

    private String malStringToString(MalString malString) {
        String result = "(";
        for (int i = 0; i < malString.getStringValues().length; i++) {
            result += malString.getStringValues()[i];
            if (i < malString.getStringValues().length - 1) {
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
