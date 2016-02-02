package mathtool.session.classes;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DefinedVars {

    private List<DefinedVar> definedVars;

    /**
     * @return the definedVars
     */
    public List<DefinedVar> getDefinedVarList() {
        return definedVars;
    }

    /**
     * @param definedVars the definedVars to set
     */
    @XmlElement(name="variable", type=DefinedVar.class)
    public void setDefinedVarList(List<DefinedVar> definedVars) {
        this.definedVars = definedVars;
    }
    
}
