package mathtool.session.classes;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Arguments {

    private List<String> arguments;

    /**
     * @return the arguments
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * @param arguments the arguments to set
     */
    @XmlElement(name="varname")
    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
    
}
