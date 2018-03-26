package mathtool.config;

import abstractexpressions.expression.classes.Expression;
import enums.TypeLanguage;
import java.io.File;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mathtool.MathToolController;
import mathtool.config.DropDownEnums.FactorizeDropDownOption;
import mathtool.config.DropDownEnums.LogarithmsDropDownOption;
import mathtool.config.classes.MathToolConfig;
import mathtool.enums.TypeMode;
import mathtool.utilities.MathToolLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigLoader {

    private static final String CONFIG_PATH = "mathtool/config/config.xml";

    private static final String ELEMENT_MATHTOOL_CONFIG = "mathToolConfig";
    private static final String ELEMENT_FONTSIZE_GRAPHIC = "fontSizeGraphic";
    private static final String ELEMENT_FONTSIZE_TEXT = "fontSizeText";
    private static final String ELEMENT_LANGUAGE = "language";
    private static final String ELEMENT_MODE = "mode";
    private static final String ELEMENT_SCREEN_WIDTH = "screenWidth";
    private static final String ELEMENT_SCREEN_HEIGHT = "screenHeight";
    private static final String ELEMENT_OPTION_ALGEBRAIC_RELATIONS = "optionAlgebraicRelations";
    private static final String ELEMENT_OPTION_FUNCTIONAL_RELATIONS = "optionFunctionalRelations";
    private static final String ELEMENT_OPTION_COLLECT_AND_SIMPLIFY_IF_SHORTER = "optionExpandAndCollectIfShorter";
    private static final String ELEMENT_OPTION_FACTORIZE = "optionFactorizeDropDown";
    private static final String ELEMENT_OPTION_LOGARITHMS = "optionLogarithmsDropDown";
    private static final String ELEMENT_TIMEOUT_COMPUTATION = "timeoutComputation";
    private static final String ELEMENT_TIMEOUT_ALGORITHM = "timeoutAlgorithm";
    
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
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url.openStream());

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(ELEMENT_MATHTOOL_CONFIG);

            Node nNode = nList.item(0);

            MathToolConfig config = new MathToolConfig();
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                config.setFontSizeGraphic(Integer.parseInt(eElement.getElementsByTagName(ELEMENT_FONTSIZE_GRAPHIC).item(0).getTextContent()));
                config.setFontSizeText(Integer.parseInt(eElement.getElementsByTagName(ELEMENT_FONTSIZE_TEXT).item(0).getTextContent()));
                config.setLanguage(TypeLanguage.valueOf(eElement.getElementsByTagName(ELEMENT_LANGUAGE).item(0).getTextContent()));
                config.setMode(TypeMode.valueOf(eElement.getElementsByTagName(ELEMENT_MODE).item(0).getTextContent()));
                config.setScreenWidth(Integer.parseInt(eElement.getElementsByTagName(ELEMENT_SCREEN_WIDTH).item(0).getTextContent()));
                config.setScreenHeight(Integer.parseInt(eElement.getElementsByTagName(ELEMENT_SCREEN_HEIGHT).item(0).getTextContent()));
                config.setOptionAlgebraicRelations(Boolean.parseBoolean(eElement.getElementsByTagName(ELEMENT_OPTION_ALGEBRAIC_RELATIONS).item(0).getTextContent()));
                config.setOptionFunctionalRelations(Boolean.parseBoolean(eElement.getElementsByTagName(ELEMENT_OPTION_FUNCTIONAL_RELATIONS).item(0).getTextContent()));
                config.setOptionExpandAndCollectIfShorter(Boolean.parseBoolean(eElement.getElementsByTagName(ELEMENT_OPTION_COLLECT_AND_SIMPLIFY_IF_SHORTER).item(0).getTextContent()));
                config.setOptionFactorizeDropDown(DropDownEnums.FactorizeDropDownOption.valueOf(eElement.getElementsByTagName(ELEMENT_OPTION_FACTORIZE).item(0).getTextContent()));
                config.setOptionLogarithmsDropDown(DropDownEnums.LogarithmsDropDownOption.valueOf(eElement.getElementsByTagName(ELEMENT_OPTION_LOGARITHMS).item(0).getTextContent()));
                config.setTimeoutComputation(Integer.parseInt(eElement.getElementsByTagName(ELEMENT_TIMEOUT_COMPUTATION).item(0).getTextContent()));
                config.setTimeoutAlgorithm(Integer.parseInt(eElement.getElementsByTagName(ELEMENT_TIMEOUT_ALGORITHM).item(0).getTextContent()));
            }

            return config;
        } catch (Exception e) {
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
