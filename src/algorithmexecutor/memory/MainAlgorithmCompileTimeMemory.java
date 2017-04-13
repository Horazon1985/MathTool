package algorithmexecutor.memory;

import algorithmexecutor.exceptions.AlgorithmParseException;
import algorithmexecutor.exceptions.ParseExceptionTexts;
import algorithmexecutor.identifier.Identifier;
import java.util.HashMap;
import java.util.Map;

public class MainAlgorithmCompileTimeMemory {

    private final Map<Identifier, Object> memory = new HashMap<>();

    public void clearMemory() {
        this.memory.clear();
    }

    public void addToMemory(Identifier identifier, Object value) throws AlgorithmParseException {
        if (this.memory.get(identifier) == null) {
            this.memory.put(identifier, value);
        } else {
            // Identifier existiert bereits!
            throw new AlgorithmParseException(ParseExceptionTexts.IDENTIFIER_ALREADY_DEFINED);
        }
    }

    public void removeFromMemory(Identifier identifier, Object value) {
        this.memory.remove(identifier, value);
    }

}
