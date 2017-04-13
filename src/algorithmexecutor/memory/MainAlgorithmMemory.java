package algorithmexecutor.memory;

import algorithmexecutor.exceptions.AlgorithmCompileException;
import algorithmexecutor.exceptions.CompileExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import java.util.HashSet;
import java.util.Set;

public class MainAlgorithmMemory {

    private final Set<Identifier> memory = new HashSet<>();

    public boolean containsIdentifier(Identifier identifier) {
        /*
        Da equals() in identifier die Attribute type und value nicht einbezieht,
        wird also nur überprüft, ob memory einen Identifier mit demselben Namen 
        enthält.
         */
        return this.memory.contains(identifier);
    }

    public void clearMemory() {
        this.memory.clear();
    }

    public void addToMemoryInCompileTime(Identifier identifier) throws AlgorithmCompileException {
        if (!this.memory.contains(identifier)) {
            this.memory.add(identifier);
        } else {
            // Identifier existiert bereits!
            throw new AlgorithmCompileException(CompileExceptionTexts.IDENTIFIER_ALREADY_DEFINED);
        }
    }

    public void addToMemoryInRuntime(Identifier identifier) {
        /*
        Während der Laufzeit kann es zu keinen Namensclashs kommen,
        da der Algorithmus zuvor bereits kompiliert wurde
         */
        this.memory.add(identifier);
    }

    public void removeFromMemory(Identifier identifier) {
        this.memory.remove(identifier);
    }

}
