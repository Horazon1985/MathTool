package algorithmexecutor.identifier;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.model.Algorithm;
import java.util.Objects;

public class Identifier {

    private final IdentifierTypes type;
    private final String name;
    private AbstractExpression value;
    
    private Identifier(IdentifierTypes type, String name) {
        this.type = type;
        this.name = name;
    }

    private Identifier(IdentifierTypes type, String name, AbstractExpression value) {
        this.type = type;
        this.name = name;
        this.value = value;
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
    
    public static Identifier createIdentifier(Algorithm alg, String identifierName, IdentifierTypes type) {
        if (AlgorithmExecutor.getMemoryMap().get(alg).containsIdentifier(identifierName)) {
            return AlgorithmExecutor.getMemoryMap().get(alg).getMemory().get(identifierName);
        }
        return new Identifier(type, identifierName);
    }

    public static Identifier createIdentifier(Algorithm alg, String identifierName, IdentifierTypes type, AbstractExpression value) {
        if (AlgorithmExecutor.getMemoryMap().get(alg).containsIdentifier(identifierName)) {
            return AlgorithmExecutor.getMemoryMap().get(alg).getMemory().get(identifierName);
        }
        return new Identifier(type, identifierName, value);
    }

    
}
