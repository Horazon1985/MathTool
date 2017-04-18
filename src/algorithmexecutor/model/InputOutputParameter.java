package algorithmexecutor.model;

import algorithmexecutor.enums.IdentifierTypes;

public class InputOutputParameter {

    private final IdentifierTypes type;
    private final String name;
    
    public InputOutputParameter(IdentifierTypes type, String name) {
        this.type = type;
        this.name = name;
    }

    public IdentifierTypes getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
}
