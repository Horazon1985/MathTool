package mathtool.session;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MathToolSession {

    private DefinedVars definedVars;

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
    public void setGeneralSettings(DefinedVars definedVars) {
        this.definedVars = definedVars;
    }


    
}
