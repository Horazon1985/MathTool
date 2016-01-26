package mathtool.config;

import enums.TypeLanguage;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import mathtool.enums.TypeMode;

@XmlRootElement
public class GeneralSettings {

    private int fontSizeGraphic;
    private int fontSizeText;
    private TypeLanguage language;
    private TypeMode mode;

    /**
     * @return the fontSizeGraphic
     */
    public int getFontSizeGraphic() {
        return fontSizeGraphic;
    }

    /**
     * @param fontSize the fontSizeGraphic to set
     */
    @XmlElement
    public void setFontSizeGraphic(int fontSize) {
        this.fontSizeGraphic = fontSize;
    }

    /**
     * @return the fontSizeText
     */
    public int getFontSizeText() {
        return fontSizeText;
    }

    /**
     * @param fontSizeText the fontSizeText to set
     */
    @XmlElement
    public void setFontSizeText(int fontSizeText) {
        this.fontSizeText = fontSizeText;
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
