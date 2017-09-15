package algorithmexecuter.model;

import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.constants.CompileExceptionTexts;
import algorithmexecuter.model.identifier.Identifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmMemory {

    private Algorithm algorithm;
    private final Map<String, Identifier> memory;

    public AlgorithmMemory(Algorithm alg) {
        this.algorithm = alg;
        this.memory = new HashMap<>();
    }

    public AlgorithmMemory(Algorithm alg, List<Identifier> identifiers) {
        this(alg);
        for (Identifier identifier : identifiers) {
            this.memory.put(identifier.getName(), identifier);
        }
    }

    public AlgorithmMemory(Algorithm alg, Identifier[] identifiers) {
        this(alg);
        for (Identifier identifier : identifiers) {
            this.memory.put(identifier.getName(), identifier);
        }
    }

    public Map<String, Identifier> getMemory() {
        return this.memory;
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(Algorithm alg) {
        this.algorithm = alg;
    }

    @Override
    public String toString() {
        String memoryString;
        if (this.algorithm == null) {
            memoryString = "AlgorithmMemory[Algorithm = null";
        } else {
            memoryString = "AlgorithmMemory[Algorithm = " + this.algorithm.getName();
        }
        if (!this.memory.keySet().isEmpty()) {
            memoryString += ", ";
        }
        for (String identifierName : this.memory.keySet()) {
            memoryString += identifierName + ": " + this.memory.get(identifierName) + ", ";
        }
        if (!this.memory.keySet().isEmpty()) {
            return memoryString.substring(0, memoryString.length() - 2) + "]";
        }
        return memoryString + "]";
    }

    public boolean containsIdentifier(String identifierName) {
        return this.memory.get(identifierName) != null;
    }

    public void clearMemory() {
        this.memory.clear();
    }
    
    public int getSize() {
        return this.memory.size();
    }

    public AlgorithmMemory copyMemory() {
        AlgorithmMemory copyOfMemory = new AlgorithmMemory(this.algorithm);
        for (String identifierName : this.memory.keySet()) {
            copyOfMemory.getMemory().put(identifierName, this.memory.get(identifierName));
        }
        return copyOfMemory;
    }

    public void addToMemoryInCompileTime(Identifier identifier) throws AlgorithmCompileException {
        if (this.memory.get(identifier.getName()) != null) {
            // Identifier existiert bereits!
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED, identifier.getName());
        }
        this.memory.put(identifier.getName(), identifier);
    }

    public void addToMemoryInRuntime(Identifier identifier) {
        /*
        Während der Laufzeit kann es zu keinen Namensclashs kommen,
        da der Algorithmus zuvor bereits kompiliert wurde.
         */
        this.memory.put(identifier.getName(), identifier);
    }

    public void removeFromMemory(Identifier identifier) {
        this.memory.remove(identifier.getName());
    }

}
