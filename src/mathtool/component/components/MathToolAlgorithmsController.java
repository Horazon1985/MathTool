package mathtool.component.components;

import algorithmexecuter.CompilerUtils;
import algorithmexecuter.enums.ReservedChars;

public class MathToolAlgorithmsController {

    private static final String SIGN_TAB = "\t";
    private static final String SIGN_NEXT_LINE = "\n";

    public static String formatSourceCodeFromEditor(String inputSourceCode) {
        String algString = getPlainCode(inputSourceCode);
        String formattedCode = MathToolAlgorithmsController.formatSourceCode(algString);
        return formattedCode;
    }

    public static String getPlainCode(String code) {
        code = code.replaceAll(SIGN_NEXT_LINE, "");
        code = code.replaceAll(SIGN_TAB, "");
        return code;
    }

    public static String formatSourceCode(String inputSourceCode) {
        if (inputSourceCode.isEmpty()) {
            return inputSourceCode;
        }

        // Vorformatierung.
        inputSourceCode = CompilerUtils.preprocessAlgorithm(inputSourceCode);

        int bracketCounter = 0;
        boolean newLine = false;
        String formattedSourceCode = "";
        for (int i = 0; i < inputSourceCode.length(); i++) {
            if (inputSourceCode.charAt(i) == ReservedChars.BEGIN.getValue()) {
                formattedSourceCode += inputSourceCode.charAt(i);
                bracketCounter++;
                newLine = true;
                formattedSourceCode += SIGN_NEXT_LINE;
            } else if (inputSourceCode.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
                newLine = true;
                formattedSourceCode += writeMultipleTabs(bracketCounter);
                formattedSourceCode += inputSourceCode.charAt(i);
                formattedSourceCode += SIGN_NEXT_LINE;
            } else if (inputSourceCode.charAt(i) == ReservedChars.LINE_SEPARATOR.getValue()) {
                formattedSourceCode += inputSourceCode.charAt(i);
                formattedSourceCode += SIGN_NEXT_LINE;
                newLine = true;
            } else {
                if (newLine) {
                    formattedSourceCode += writeMultipleTabs(bracketCounter);
                }
                newLine = false;
                formattedSourceCode += inputSourceCode.charAt(i);
            }
        }

        return formattedSourceCode;
    }

    private static String writeMultipleTabs(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += SIGN_TAB;
        }
        return result;
    }

}
