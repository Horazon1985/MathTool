package mathtool.session;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DefinedVars {
 
    private String varname;
    private String value;

    /**
     * @return the varname
     */
    public String getVarname() {
        return varname;
    }

    /**
     * @param varname the varname to set
     */
    @XmlElement
    public void setVarname(String varname) {
        this.varname = varname;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    @XmlElement
    public void setValue(String value) {
        this.value = value;
    }
    
}
