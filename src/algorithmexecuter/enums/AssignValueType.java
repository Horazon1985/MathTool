package algorithmexecuter.enums;

public enum AssignValueType {
    
    NEW ("new"), CHANGE("change");
    
    private final String value;
    
    AssignValueType(String value){
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
}
