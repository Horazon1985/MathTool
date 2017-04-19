package algorithmexecutor.identifier;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.enums.IdentifierTypes;
import java.util.Objects;

public class Identifier {

    private final IdentifierTypes type;
    private final String name;
    private AbstractExpression value;
    
    public Identifier(IdentifierTypes type, String name) {
        this.type = type;
        this.name = name;
    }

    public IdentifierTypes getType() {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
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
        return Objects.equals(this.name, other.name);
    }
    
    @Override
    public String toString() {
        return "Identifier[type = " + this.type + ", name = " + this.name + ", value = " + this.value + "]";
    }

    
}
