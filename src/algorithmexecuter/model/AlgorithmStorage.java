package algorithmexecuter.model;

import algorithmexecuter.enums.FixedAlgorithmNames;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmStorage {
    
    private final List<Algorithm> algorithmStorage = new ArrayList<>();

    public List<Algorithm> getAlgorithmStorage() {
        return algorithmStorage;
    }

    public void clearAlgorithmStorage() {
        this.algorithmStorage.clear();
    }
    
    public void add(Algorithm alg) {
        this.algorithmStorage.add(alg);
    }

    public AlgorithmStorage() {
    }
    
    public AlgorithmStorage(List<Algorithm> algorithmStorage) {
        this.algorithmStorage.clear();
        this.algorithmStorage.addAll(algorithmStorage);
    }
    
    public Algorithm getMainAlgorithm() {
        return getAlgorithmByName(FixedAlgorithmNames.MAIN.getValue());
    }
    
    public Algorithm getAlgorithmByName(String name) {
        for (Algorithm alg : this.algorithmStorage) {
            if (alg.getName().equals(name)) {
                return alg;
            }
        }
        return null;
    }
    
}
