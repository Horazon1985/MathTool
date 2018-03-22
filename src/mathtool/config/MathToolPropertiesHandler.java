package mathtool.config;

import enums.TypeLanguage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;
import mathtool.component.components.ErrorDialogGUI;
import mathtool.config.DropDownEnums.FactorizeDropDownOption;
import mathtool.config.DropDownEnums.LogarithmsDropDownOption;
import mathtool.enums.TypeMode;

public class MathToolPropertiesHandler {

    private static final String PATH_PROPERTIES = "mathtool/config/mathtool_properties.properties";

    private static final String KEY_FONTSIZE_GRAPHIC = "mathtool.fontsize.graphic";
    private static final String KEY_FONTSIZE_TEXT = "mathtool.fontsize.text";
    private static final String KEY_LANGUAGE = "mathtool.language";
    private static final String KEY_MODE = "mathtool.mode";
    private static final String KEY_SCREEN_WIDTH = "mathtool.screen.width";
    private static final String KEY_SCREEN_HEIGHT = "mathtool.screen.height";
    private static final String KEY_SIMPLIFY_ALGEBRAIC_RELATIONS = "mathtool.option.algebraicRelations";
    private static final String KEY_SIMPLIFY_FUNCTIONAL_RELATIONS = "mathtool.option.functionalRelations";
    private static final String KEY_SIMPLIFY_EXPAND_COLLECT = "mathtool.option.expandAndCollectIfShorter";
    private static final String KEY_SIMPLIFY_FACTORIZE = "mathtool.option.factorizeDropDown";
    private static final String KEY_SIMPLIFY_LOGARITHMS = "mathtool.option.logarithmsDropDown";
    private static final String KEY_TIMEOUT_COMPUTATION = "mathtool.timeout.computation";
    private static final String KEY_TIMEOUT_ALGORITHM = "mathtool.timeout.algorithm";

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

    private static final Properties PROPERTIES = new Properties();

    public static void readMathToolProperties() {
        InputStream input = null;

        try {
//            Path path = Paths.get(PATH_PROPERTIES);
//            System.out.println("Properties path: " + path.toAbsolutePath());
            input = new FileInputStream(PATH_PROPERTIES);
            PROPERTIES.load(input);
        } catch (IOException e) {
            ErrorDialogGUI errorDialog = ErrorDialogGUI.createResourceNotFoundDialog(Paths.get(PATH_PROPERTIES).toAbsolutePath().toString());
            errorDialog.setVisible(true);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    ErrorDialogGUI errorDialog = ErrorDialogGUI.createCannotCloseResourceDialog(Paths.get(PATH_PROPERTIES).toAbsolutePath().toString());
                    errorDialog.setVisible(true);
                }
            }
        }
    }

    public static void writeMathToolProperties() {
        OutputStream output = null;

        try {
            output = new FileOutputStream(PATH_PROPERTIES);
            PROPERTIES.store(output, null);
        } catch (IOException e) {
            // TO DO.
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // TO DO.
                }

            }
        }
    }

    public static int getFontSizeGraphic() {
        return Integer.parseInt(PROPERTIES.getProperty(KEY_FONTSIZE_GRAPHIC, DEFAULT_VALUE_FONTSIZE_GRAPHIC));
    }

    public static int getFontSizeText() {
        return Integer.parseInt(PROPERTIES.getProperty(KEY_FONTSIZE_TEXT, DEFAULT_VALUE_FONTSIZE_TEXT));
    }

    public static TypeLanguage getLanguage() {
        return TypeLanguage.valueOf(PROPERTIES.getProperty(KEY_LANGUAGE, DEFAULT_VALUE_LANGUAGE));
    }

    public static TypeMode getMode() {
        return TypeMode.valueOf(PROPERTIES.getProperty(KEY_MODE, DEFAULT_VALUE_MODE));
    }

    public static int getScreenWidth() {
        return Integer.parseInt(PROPERTIES.getProperty(KEY_SCREEN_WIDTH, DEFAULT_VALUE_SCREEN_WIDTH));
    }

    public static int getScreenHeight() {
        return Integer.parseInt(PROPERTIES.getProperty(KEY_SCREEN_HEIGHT, DEFAULT_VALUE_SCREEN_HEIGHT));
    }

    public static boolean getAlgebraicRelations() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(KEY_SIMPLIFY_ALGEBRAIC_RELATIONS, DEFAULT_VALUE_SIMPLIFY_ALGEBRAIC_RELATIONS));
    }

    public static boolean getFunctionalRelations() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(KEY_SIMPLIFY_FUNCTIONAL_RELATIONS, DEFAULT_VALUE_SIMPLIFY_FUNCTIONAL_RELATIONS));
    }

    public static boolean getExpandAndCollectIfShorter() {
        return Boolean.parseBoolean(PROPERTIES.getProperty(KEY_SIMPLIFY_EXPAND_COLLECT, DEFAULT_VALUE_SIMPLIFY_EXPAND_COLLECT));
    }

    public static FactorizeDropDownOption getFactorizeDropDown() {
        return FactorizeDropDownOption.valueOf(PROPERTIES.getProperty(KEY_SIMPLIFY_FACTORIZE, DEFAULT_VALUE_SIMPLIFY_FACTORIZE));
    }

    public static LogarithmsDropDownOption getLogarithmsDropDown() {
        return LogarithmsDropDownOption.valueOf(PROPERTIES.getProperty(KEY_SIMPLIFY_LOGARITHMS, DEFAULT_VALUE_SIMPLIFY_LOGARITHMS));
    }

    public static int getTimeoutComputation() {
        return Integer.parseInt(PROPERTIES.getProperty(KEY_TIMEOUT_COMPUTATION, DEFAULT_VALUE_TIMEOUT_COMPUTATION));
    }
    
    public static int getTimeoutAlgorithm() {
        return Integer.parseInt(PROPERTIES.getProperty(KEY_TIMEOUT_ALGORITHM, DEFAULT_VALUE_TIMEOUT_ALGORITHM));
    }
    
}
