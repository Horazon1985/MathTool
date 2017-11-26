package algorithmexecuter.model.utilclasses;

public class MalString {

    private Object[] stringValues;

    public MalString(Object[] stringValues) {
        this.stringValues = stringValues;
    }
    
    public MalString(String s) {
        this.stringValues = new Object[]{s};
    }
    
    public Object[] getStringValues() {
        return stringValues;
    }

    public void setStringValues(Object[] stringValues) {
        this.stringValues = stringValues;
    }
    
    @Override
    public String toString(){
        String result = "(";
        for (int i = 0; i < this.stringValues.length; i++) {
            result += this.stringValues[i];
            if (i < this.stringValues.length - 1) {
                result += ", ";
            }
        }
        return result + ")";
    }
    
}
