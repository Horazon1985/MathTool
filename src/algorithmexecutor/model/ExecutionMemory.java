package algorithmexecutor.model;

import java.util.HashMap;
import java.util.Map;

public class ExecutionMemory {

    private final Map<Algorithm, AlgorithmMemory> executionMemory = new HashMap<>();

    public Map<Algorithm, AlgorithmMemory> getExecutionMemory() {
        return this.executionMemory;
    }

    public void clearExecutionMemory() {
        this.executionMemory.clear();
    }
    
    public AlgorithmMemory get(Algorithm alg) {
        return this.executionMemory.get(alg);
    }
    
    public void put(Algorithm alg, AlgorithmMemory memory) {
        this.executionMemory.put(alg, memory);
    }
    
}
