package mathtool;

public class Command {

    private TypeCommand typeCommand;
    private Object[] params;
    
    //Getter und Setter
    //Getter
    public TypeCommand getTypeCommand(){
        return this.typeCommand;
    }
    
    public Object[] getParams(){
        return this.params;
    }
    
    //Setter
    public void setTypeCommand(TypeCommand typeCommand){
        this.typeCommand = typeCommand;
    }
    
    public void setParams(Object[] params){
        this.params = params;
    }
            
    
}
