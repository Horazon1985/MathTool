package algorithmexecutor.identifier;

import abstractexpressions.interfaces.AbstractExpression;
import algorithmexecutor.AlgorithmExecutor;
import algorithmexecutor.command.condition.BooleanExpression;
import algorithmexecutor.enums.IdentifierTypes;
import algorithmexecutor.memory.AlgorithmMemory;
import algorithmexecutor.model.Algorithm;
import java.util.Collections;
import java.util.Objects;

public class Identifier {

    private final IdentifierTypes type;
    private final String name;
    private AbstractExpression value;
    private BooleanExpression booleanExpression;
    
    private Identifier(IdentifierTypes type, String name) {
        this.type = type;
        this.name = name;
    }

    private Identifier(Algorithm alg, IdentifierTypes type, String name) {
        this.type = type;
        this.name = name;
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        if (memory == null) {
            memory = new AlgorithmMemory(Collections.singletonList(createIdentifier(alg, name, type)));
            AlgorithmExecutor.getMemoryMap().put(alg, memory);
        }
    }

    private Identifier(IdentifierTypes type, String name, AbstractExpression value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    private Identifier(Algorithm alg, IdentifierTypes type, String name, AbstractExpression value) {
        this.type = type;
        this.name = name;
        this.value = value;
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        if (memory == null) {
            memory = new AlgorithmMemory(Collections.singletonList(createIdentifier(alg, name, type)));
            AlgorithmExecutor.getMemoryMap().put(alg, memory);
        }
    }

    private Identifier(IdentifierTypes type, String name, BooleanExpression booleanExpression) {
        this.type = type;
        this.name = name;
        this.booleanExpression = booleanExpression;
    }

    private Identifier(Algorithm alg, IdentifierTypes type, String name, BooleanExpression booleanExpression) {
        this.type = type;
        this.name = name;
        this.booleanExpression = booleanExpression;
        AlgorithmMemory memory = AlgorithmExecutor.getMemoryMap().get(alg);
        if (memory == null) {
            memory = new AlgorithmMemory(Collections.singletonList(createIdentifier(alg, name, type)));
            AlgorithmExecutor.getMemoryMap().put(alg, memory);
        }
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
        this.booleanExpression = null;
    }

    public void setBooleanExpression(BooleanExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
        this.value = null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.type);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.value);
        hash = 29 * hash + Objects.hashCode(this.booleanExpression);
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
        return Objects.equals(this.booleanExpression, other.booleanExpression);
    }
    
    @Override
    public String toString() {
        return "Identifier[type = " + this.type + ", name = " + this.name 
                + ", value = " + this.value + ", boolean expression = " + this.booleanExpression + "]";
    }

    public static Identifier createIdentifier(String identifierName, IdentifierTypes type) {
        return new Identifier(type, identifierName);
    }
    
    public static Identifier createIdentifier(Algorithm alg, String identifierName, IdentifierTypes type) {
        if (AlgorithmExecutor.getMemoryMap().get(alg).containsIdentifier(identifierName)) {
            return AlgorithmExecutor.getMemoryMap().get(alg).getMemory().get(identifierName);
        }
        return new Identifier(alg, type, identifierName);
    }

    public static Identifier createIdentifier(Algorithm alg, String identifierName, IdentifierTypes type, AbstractExpression value) {
        if (AlgorithmExecutor.getMemoryMap().get(alg).containsIdentifier(identifierName)) {
            return AlgorithmExecutor.getMemoryMap().get(alg).getMemory().get(identifierName);
        }
        return new Identifier(alg, type, identifierName, value);
    }

    public static Identifier createIdentifier(Algorithm alg, String identifierName, IdentifierTypes type, BooleanExpression booleanExpression) {
        if (AlgorithmExecutor.getMemoryMap().get(alg).containsIdentifier(identifierName)) {
            return AlgorithmExecutor.getMemoryMap().get(alg).getMemory().get(identifierName);
        }
        return new Identifier(alg, type, identifierName, booleanExpression);
    }
    
}
