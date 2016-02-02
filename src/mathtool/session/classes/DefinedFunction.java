package mathtool.session.classes;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DefinedFunction {
    
    private String functionname;
    private Arguments arguments;
    private String functionterm;

    public DefinedFunction(){
    }
    
    /**
     * @return the functionname
     */
    public String getFunctionname() {
        return functionname;
    }

    /**
     * @param functionname the functionname to set
     */
    @XmlElement(name="functionname")
    public void setFunctionname(String functionname) {
        this.functionname = functionname;
    }

    /**
     * @return the functionterm
     */
    public String getFunctionterm() {
        return functionterm;
    }

    /**
     * @param functionterm the functionterm to set
     */
    @XmlElement(name="functionterm")
    public void setFunctionterm(String functionterm) {
        this.functionterm = functionterm;
    }

    /**
     * @return the arguments
     */
    public Arguments getArguments() {
        return arguments;
    }

    /**
     * @param arguments the arguments to set
     */
    @XmlElement
    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }
    
}
