package algorithmexecuter.model;

import algorithmexecuter.enums.IdentifierType;
import java.util.Arrays;
import java.util.Objects;

public class Signature implements Comparable {

    private final String name;
    private final IdentifierType[] parameterTypes;
    private final IdentifierType returnType;

    public Signature(IdentifierType returnType, String name, IdentifierType[] parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public IdentifierType[] getParameterTypes() {
        return parameterTypes;
    }

    public IdentifierType getReturnType() {
        return returnType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Arrays.deepHashCode(this.parameterTypes);
        hash = 41 * hash + Objects.hashCode(this.returnType);
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
        final Signature other = (Signature) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Arrays.deepEquals(this.parameterTypes, other.parameterTypes)) {
            return false;
        }
        return this.returnType == other.returnType;
    }

    @Override
    public String toString() {
        String signature = this.returnType + " " + this.name + "(";
        for (int i = 0; i < this.parameterTypes.length; i++) {
            signature += this.parameterTypes[i];
            if (i < this.parameterTypes.length - 1) {
                signature += ",";
            }
        }
        return signature + ")";
    }

    @Override
    public int compareTo(Object t) {
        if (t instanceof Signature) {
            if (this.name.contains(((Signature) t).name)) {
                return -1;
            } else if (((Signature) t).name.contains(this.name)) {
                return 1;
            }
        }
        return 0;
    }
    
}
