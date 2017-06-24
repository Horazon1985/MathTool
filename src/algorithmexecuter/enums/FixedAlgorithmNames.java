package algorithmexecuter.enums;

public enum FixedAlgorithmNames {

    MAIN("main"),
    INC("inc"),
    DEC("dec"),
    PRINT("print");
    
    private final String value;
    
    FixedAlgorithmNames(String name){
        this.value = name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }

    
}
