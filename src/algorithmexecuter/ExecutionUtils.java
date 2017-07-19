package algorithmexecuter;

import algorithmexecuter.model.AlgorithmMemory;

public final class ExecutionUtils {
    
    private ExecutionUtils() {
    }
    
    public static void updateMemoryBeforeBlockExecution(AlgorithmMemory memoryBeforBlockExecution, AlgorithmMemory scopeMemory) {
        for (String identifierName : memoryBeforBlockExecution.getMemory().keySet()) {
            if (scopeMemory.getMemory().keySet().contains(identifierName)) {
                memoryBeforBlockExecution.getMemory().put(identifierName, scopeMemory.getMemory().get(identifierName));
            }
        }
    }
    
    
}
