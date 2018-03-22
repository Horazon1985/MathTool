package mathtool.config;

import enums.TypeLanguage;
import java.io.File;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import mathtool.MathToolController;
import mathtool.config.DropDownEnums.FactorizeDropDownOption;
import mathtool.config.DropDownEnums.LogarithmsDropDownOption;
import mathtool.config.classes.MathToolConfig;
import mathtool.enums.TypeMode;
import mathtool.utilities.MathToolLogger;

public class ConfigLoader {

    private static final String CONFIG_PATH = "mathtool/config/config.xml";

    private static final String DEFAULT_VALUE_FONTSIZE_GRAPHIC = "20";
    private static final String DEFAULT_VALUE_FONTSIZE_TEXT = "15";
    private static final String DEFAULT_VALUE_LANGUAGE = "DE";
    private static final String DEFAULT_VALUE_MODE = "GRAPHIC";
    private static final String DEFAULT_VALUE_SCREEN_WIDTH = "1200";
    private static final String DEFAULT_VALUE_SCREEN_HEIGHT = "670";
    private static final String DEFAULT_VALUE_SIMPLIFY_ALGEBRAIC_RELATIONS = "true";
    private static final String DEFAULT_VALUE_SIMPLIFY_FUNCTIONAL_RELATIONS = "true";
    private static final String DEFAULT_VALUE_SIMPLIFY_EXPAND_COLLECT = "true";
    private static final String DEFAULT_VALUE_SIMPLIFY_FACTORIZE = "factorize";
    private static final String DEFAULT_VALUE_SIMPLIFY_LOGARITHMS = "collect";
    private static final String DEFAULT_VALUE_TIMEOUT_COMPUTATION = "120";
    private static final String DEFAULT_VALUE_TIMEOUT_ALGORITHM = "300";

    private static final MathToolLogger log;
    
    static {
        log = MathToolController.getLogger();
    }
    
    public static MathToolConfig loadConfig() {

        try {
            URL url = ClassLoader.getSystemResource(CONFIG_PATH);
            if (url == null) {
                log.logConfigNotFound();
                return loadDefaultConfig();
            }
            File file = new File(url.getFile());
            JAXBContext jaxbContext;
            jaxbContext = JAXBContext.newInstance(MathToolConfig.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (MathToolConfig) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            // Config mit Defaultwerten füllen.
            log.logConfigCannotBeParsed();
            return loadDefaultConfig();
        }
    }

    private static MathToolConfig loadDefaultConfig() {
        MathToolConfig config = new MathToolConfig();
        config.setFontSizeGraphic(getFontSizeGraphicDefault());
        config.setFontSizeText(getFontSizeTextDefault());
        config.setLanguage(getLanguageDefault());
        config.setMode(getModeDefault());
        config.setScreenWidth(getScreenWidthDefault());
        config.setScreenHeight(getScreenHeightDefault());
        config.setOptionAlgebraicRelations(getOptionAlgebraicRelationsDefault());
        config.setOptionFunctionalRelations(getOptionFunctionalRelationsDefault());
        config.setOptionExpandAndCollectIfShorter(getOptionExpandAndCollectIfShorterDefault());
        config.setOptionFactorizeDropDown(getOptionFactorizeDropDownDefault());
        config.setOptionLogarithmsDropDown(getOptionLogarithmsDropDownDefault());
        config.setTimeoutComputation(getTimeoutComputationDefault());
        config.setTimeoutAlgorithm(getTimeoutAlgorithmDefault());
        return config;
    }

    private static int getFontSizeGraphicDefault() {
        return Integer.parseInt(DEFAULT_VALUE_FONTSIZE_GRAPHIC);
    }

    private static int getFontSizeTextDefault() {
        return Integer.parseInt(DEFAULT_VALUE_FONTSIZE_TEXT);
    }

    private static TypeLanguage getLanguageDefault() {
        return TypeLanguage.valueOf(DEFAULT_VALUE_LANGUAGE);
    }

    private static TypeMode getModeDefault() {
        return TypeMode.valueOf(DEFAULT_VALUE_MODE);
    }

    private static int getScreenWidthDefault() {
        return Integer.parseInt(DEFAULT_VALUE_SCREEN_WIDTH);
    }

    private static int getScreenHeightDefault() {
        return Integer.parseInt(DEFAULT_VALUE_SCREEN_HEIGHT);
    }

    private static boolean getOptionAlgebraicRelationsDefault() {
        return Boolean.parseBoolean(DEFAULT_VALUE_SIMPLIFY_ALGEBRAIC_RELATIONS);
    }

    private static boolean getOptionFunctionalRelationsDefault() {
        return Boolean.parseBoolean(DEFAULT_VALUE_SIMPLIFY_FUNCTIONAL_RELATIONS);
    }

    private static boolean getOptionExpandAndCollectIfShorterDefault() {
        return Boolean.parseBoolean(DEFAULT_VALUE_SIMPLIFY_EXPAND_COLLECT);
    }

    private static FactorizeDropDownOption getOptionFactorizeDropDownDefault() {
        return DropDownEnums.FactorizeDropDownOption.valueOf(DEFAULT_VALUE_SIMPLIFY_FACTORIZE);
    }

    private static LogarithmsDropDownOption getOptionLogarithmsDropDownDefault() {
        return DropDownEnums.LogarithmsDropDownOption.valueOf(DEFAULT_VALUE_SIMPLIFY_LOGARITHMS);
    }

    private static int getTimeoutComputationDefault() {
        return Integer.parseInt(DEFAULT_VALUE_TIMEOUT_COMPUTATION);
    }

    private static int getTimeoutAlgorithmDefault() {
        return Integer.parseInt(DEFAULT_VALUE_TIMEOUT_ALGORITHM);
    }

}
