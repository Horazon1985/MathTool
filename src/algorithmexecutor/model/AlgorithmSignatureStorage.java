package algorithmexecutor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlgorithmSignatureStorage {

    private final List<Signature> algorithmSignatureStorage = new ArrayList<>();

    public List<Signature> getAlgorithmSignatureStorage() {
        return algorithmSignatureStorage;
    }

    public void clearAlgorithmSignatureStorage() {
        this.algorithmSignatureStorage.clear();
    }

    public void add(Signature sgn) {
        this.algorithmSignatureStorage.add(sgn);
        Collections.sort(this.algorithmSignatureStorage);
    }

}
