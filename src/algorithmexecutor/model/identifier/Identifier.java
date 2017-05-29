package algorithmexecutor.model.identifier;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.enums.IdentifierType;
import algorithmexecutor.model.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import java.util.Collections;
import java.util.Objects;

public class Identifier {

    private final IdentifierType type;
    private final String name;
    private AbstractExpression value;
    
    private Identifier(IdentifierType type, String name) {
        this.type = type;
        this.name = name;
    }

    private Identifier(Algorithm alg, IdentifierType type, String name) {
        this.type = type;
        this.name = name;
        AlgorithmMemory memory = AlgorithmExecutor.getExecutionMemory().get(alg);
        if (memory == null) {
            memory = new AlgorithmMemory(Collections.singletonList(createIdentifier(alg, name, type)));
            AlgorithmExecutor.getExecutionMemory().put(alg, memory);
        }
    }

    private Identifier(IdentifierType type, String name, AbstractExpression value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    private Identifier(Algorithm alg, IdentifierType type, String name, AbstractExpression value) {
        this.type = type;
        this.name = name;
        this.value = value;
        AlgorithmMemory memory = AlgorithmExecutor.getExecutionMemory().get(alg);
        if (memory == null) {
            memory = new AlgorithmMemory(Collections.singletonList(createIdentifier(alg, name, type)));
            AlgorithmExecutor.getExecutionMemory().put(alg, memory);
        }
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.value);
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
        return Objects.equals(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return "Identifier[type = " + this.type + ", name = " + this.name 
                + ", value = " + this.value + "]";
    }

    public static Identifier createIdentifier(String identifierName, IdentifierType type) {
        return new Identifier(type, identifierName);
    }
    
    public static Identifier createIdentifier(Algorithm alg, String identifierName, IdentifierType type) {
        if (AlgorithmExecutor.getExecutionMemory().get(alg).containsIdentifier(identifierName)) {
            return AlgorithmExecutor.getExecutionMemory().get(alg).getMemory().get(identifierName);
        }
        return new Identifier(alg, type, identifierName);
    }

}