package mathtool.config;

import enums.TypeLanguage;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import mathtool.enums.TypeMode;

@XmlRootElement
public class GeneralSettings {

    private int fontSize;
    private TypeLanguage language;
    private TypeMode mode;

    /**
     * @return the fontSize
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    @XmlElement
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return the typeLanguage
     */
    public TypeLanguage getLanguage() {
        return language;
    }

    /**
     * @param typeLanguage the typeLanguage to set
     */
    @XmlElement
    public void setLanguage(TypeLanguage typeLanguage) {
        this.language = typeLanguage;
    }

    /**
     * @return the mode
     */
    public TypeMode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    @XmlElement
    public void setMode(TypeMode mode) {
        this.mode = mode;
    }
    
}
