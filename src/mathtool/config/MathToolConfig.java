package mathtool.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MathToolConfig {

    private GeneralSettings generalSettings;
    private OptionSettings optionSettings;
    
    /**
     * @return the generalSettings
     */
    public GeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    /**
     * @param generalSettings the generalSettings to set
     */
    @XmlElement
    public void setGeneralSettings(GeneralSettings generalSettings) {
        this.generalSettings = generalSettings;
    }

    /**
     * @return the optionSettings
     */
    public OptionSettings getOptionSettings() {
        return optionSettings;
    }

    /**
     * @param optionSettings the optionSettings to set
     */
    @XmlElement
    public void setOptionSettings(OptionSettings optionSettings) {
        this.optionSettings = optionSettings;
    }

}
