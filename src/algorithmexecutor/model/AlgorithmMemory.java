package algorithmexecutor.model;

import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.model.identifier.Identifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmMemory {

    private final Map<String, Identifier> memory;

    public AlgorithmMemory() {
        this.memory = new HashMap<>();
    }

    public AlgorithmMemory(List<Identifier> identifiers) {
        this();
        for (Identifier identifier : identifiers) {
            this.memory.put(identifier.getName(), identifier);
        }
    }

    public AlgorithmMemory(Identifier[] identifiers) {
        this();
        for (Identifier identifier : identifiers) {
            this.memory.put(identifier.getName(), identifier);
        }
    }

    public Map<String, Identifier> getMemory() {
        return this.memory;
    }

    public boolean containsIdentifier(String identifierName) {
        return this.memory.get(identifierName) != null;
    }

    public void clearMemory() {
        this.memory.clear();
    }

    public void addToMemoryInCompileTime(Identifier identifier) throws AlgorithmCompileException {
        if (this.memory.get(identifier.getName()) != null) {
            // Identifier existiert bereits!
            throw new AlgorithmCompileException(CompileExceptionTexts.AC_IDENTIFIER_ALREADY_DEFINED);
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
