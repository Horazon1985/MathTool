package mathtool.config.classes;

import enums.TypeLanguage;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import mathtool.config.DropDownEnums.FactorizeDropDownOption;
import mathtool.config.DropDownEnums.LogarithmsDropDownOption;
import mathtool.enums.TypeMode;

@XmlRootElement
public class MathToolConfig {

    private int fontSizeGraphic;
    private int fontSizeText;
    private TypeLanguage language;
    private TypeMode mode;
    private int screenWidth;
    private int screenHeight;
    private boolean optionAlgebraicRelations;
    private boolean optionFunctionalRelations;
    private boolean optionExpandAndCollectIfShorter;
    private FactorizeDropDownOption optionFactorizeDropDown;
    private LogarithmsDropDownOption optionLogarithmsDropDown;
    private int timeoutComputation;
    private int timeoutAlgorithm;

    public int getFontSizeGraphic() {
        return fontSizeGraphic;
    }

    @XmlElement
    public void setFontSizeGraphic(int fontSizeGraphic) {
        this.fontSizeGraphic = fontSizeGraphic;
    }

    public int getFontSizeText() {
        return fontSizeText;
    }

    @XmlElement
    public void setFontSizeText(int fontSizeText) {
        this.fontSizeText = fontSizeText;
    }

    public TypeLanguage getLanguage() {
        return language;
    }

    @XmlElement
    public void setLanguage(TypeLanguage language) {
        this.language = language;
    }

    public TypeMode getMode() {
        return mode;
    }

    @XmlElement
    public void setMode(TypeMode mode) {
        this.mode = mode;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    @XmlElement
    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    @XmlElement
    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public boolean getOptionAlgebraicRelations() {
        return optionAlgebraicRelations;
    }

    @XmlElement
    public void setOptionAlgebraicRelations(boolean optionAlgebraicRelations) {
        this.optionAlgebraicRelations = optionAlgebraicRelations;
    }

    public boolean getOptionFunctionalRelations() {
        return optionFunctionalRelations;
    }

    @XmlElement
    public void setOptionFunctionalRelations(boolean optionFunctionalRelations) {
        this.optionFunctionalRelations = optionFunctionalRelations;
    }

    public boolean getOptionExpandAndCollectIfShorter() {
        return optionExpandAndCollectIfShorter;
    }

    @XmlElement
    public void setOptionExpandAndCollectIfShorter(boolean optionExpandAndCollectIfShorter) {
        this.optionExpandAndCollectIfShorter = optionExpandAndCollectIfShorter;
    }

    public FactorizeDropDownOption getOptionFactorizeDropDown() {
        return optionFactorizeDropDown;
    }

    @XmlElement
    public void setOptionFactorizeDropDown(FactorizeDropDownOption optionFactorizeDropDown) {
        this.optionFactorizeDropDown = optionFactorizeDropDown;
    }

    public LogarithmsDropDownOption getOptionLogarithmsDropDown() {
        return optionLogarithmsDropDown;
    }

    @XmlElement
    public void setOptionLogarithmsDropDown(LogarithmsDropDownOption optionLogarithmsDropDown) {
        this.optionLogarithmsDropDown = optionLogarithmsDropDown;
    }

    public int getTimeoutComputation() {
        return timeoutComputation;
    }

    @XmlElement
    public void setTimeoutComputation(int timeoutComputation) {
        this.timeoutComputation = timeoutComputation;
    }

    public int getTimeoutAlgorithm() {
        return timeoutAlgorithm;
    }

    @XmlElement
    public void setTimeoutAlgorithm(int timeoutAlgorithm) {
        this.timeoutAlgorithm = timeoutAlgorithm;
    }
    
}
