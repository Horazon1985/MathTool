package mathtool.session.classes;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public class DefinedFunctions {

    private List<DefinedFunction> definedFunctions;

    /**
     * @return the definedVars
     */
    public List<DefinedFunction> getDefinedFunctionList() {
        return definedFunctions;
    }

    /**
     * @param definedFunctions the definedVars to set
     */
    @XmlElement(name="function", type=DefinedFunction.class)
    public void setDefinedFunctionList(List<DefinedFunction> definedFunctions) {
        this.definedFunctions = definedFunctions;
    }
    
}
