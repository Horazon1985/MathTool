package mathtool.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OptionSettings {

    private boolean algebraicRelations;
    private boolean functionalRelations;
    private boolean expandAndCollectIfShorter;
    private FactorizeDropDownOption factorizeDropDownOption;
    private LogarithmsDropDownOption logarithmsDropDownOption;

    public enum FactorizeDropDownOption {
        factorize, expand;
    }

    public enum LogarithmsDropDownOption {
        collect, expand;
    }
    
    /**
     * @return the algebraicRelations
     */
    public boolean isAlgebraicRelations() {
        return algebraicRelations;
    }

    /**
     * @param algebraicRelations the algebraicRelations to set
     */
    @XmlElement
    public void setAlgebraicRelations(boolean algebraicRelations) {
        this.algebraicRelations = algebraicRelations;
    }

    /**
     * @return the functionalRelations
     */
    public boolean isFunctionalRelations() {
        return functionalRelations;
    }

    /**
     * @param functionalRelations the functionalRelations to set
     */
    @XmlElement
    public void setFunctionalRelations(boolean functionalRelations) {
        this.functionalRelations = functionalRelations;
    }

    /**
     * @return the expandAndCollectIfShorter
     */
    public boolean isExpandAndCollectIfShorter() {
        return expandAndCollectIfShorter;
    }

    /**
     * @param expandAndCollectIfShorter the expandAndCollectIfShorter to set
     */
    @XmlElement
    public void setExpandAndCollectIfShorter(boolean expandAndCollectIfShorter) {
        this.expandAndCollectIfShorter = expandAndCollectIfShorter;
    }

    /**
     * @return the factorizeDropDownOption
     */
    public FactorizeDropDownOption getFactorizeDropDownOption() {
        return factorizeDropDownOption;
    }

    /**
     * @param factorizeDropDownOption the factorizeDropDownOption to set
     */
    @XmlElement
    public void setFactorizeDropDownOption(FactorizeDropDownOption factorizeDropDownOption) {
        this.factorizeDropDownOption = factorizeDropDownOption;
    }

    /**
     * @return the logarithmsDropDownOption
     */
    public LogarithmsDropDownOption getLogarithmsDropDownOption() {
        return logarithmsDropDownOption;
    }

    /**
     * @param logarithmsDropDownOption the logarithmsDropDownOption to set
     */
    @XmlElement
    public void setLogarithmsDropDownOption(LogarithmsDropDownOption logarithmsDropDownOption) {
        this.logarithmsDropDownOption = logarithmsDropDownOption;
    }
    
}
