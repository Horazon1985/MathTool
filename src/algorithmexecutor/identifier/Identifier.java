package algorithmexecutor.identifier;

import algorithmexecutor.enums.IdentifierTypes;

public class Identifier {

    private final IdentifierTypes type;
    private final String name;
    
    public Identifier(IdentifierTypes type, String name) {
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
