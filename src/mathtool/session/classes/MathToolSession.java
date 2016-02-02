package mathtool.session.classes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MathToolSession {

    private DefinedVars definedVars;
    private DefinedFunctions definedFunctions;

    /**
     * @return the definedVars
     */
    public DefinedVars getDefinedVars() {
        return definedVars;
    }

    /**
     * @param definedVars the definedVars to set
     */
    @XmlElement
    public void setDefinedVars(DefinedVars definedVars) {
        this.definedVars = definedVars;
    }

    /**
     * @return the definedFunctions
     */
    public DefinedFunctions getDefinedFunctions() {
        return definedFunctions;
    }

    /**
     * @param definedFunctions the definedFunctions to set
     */
    @XmlElement
    public void setDefinedFunctions(DefinedFunctions definedFunctions) {
        this.definedFunctions = definedFunctions;
    }


    
}
