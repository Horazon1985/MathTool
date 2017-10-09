package mathtool.component.controller;

import algorithmexecuter.CompilerUtils;
import algorithmexecuter.enums.ReservedChars;

public class MathToolAlgorithmsController {

    private static final String SIGN_TAB = "\t";
    private static final String SIGN_NEXT_LINE = "\n";

    public static String formatSourceCodeFromEditor(String inputSourceCode) {
        return MathToolAlgorithmsController.formatSourceCode(getPlainCode(inputSourceCode));
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
        int wavedBracketCounter = 0;
        int squareBracketCounter = 0;
        boolean newLine = false;
        String formattedSourceCode = "";
        for (int i = 0; i < inputSourceCode.length(); i++) {
            if (inputSourceCode.charAt(i) == ReservedChars.BEGIN.getValue()) {
                formattedSourceCode += inputSourceCode.charAt(i);
                wavedBracketCounter++;
                newLine = true;
                formattedSourceCode += SIGN_NEXT_LINE;
            } else if (inputSourceCode.charAt(i) == ReservedChars.END.getValue()) {
                wavedBracketCounter--;
                newLine = true;
                formattedSourceCode += writeMultipleTabs(wavedBracketCounter);
                formattedSourceCode += inputSourceCode.charAt(i);
                formattedSourceCode += SIGN_NEXT_LINE;
                // Beim Beginn eines neuen Algorithmus eine zusätzliche Leerzeile lassen.
                if (wavedBracketCounter == 0) {
                    formattedSourceCode += SIGN_NEXT_LINE;
                }
            } else if (inputSourceCode.charAt(i) == ReservedChars.OPEN_BRACKET.getValue()) {
                bracketCounter++;
                newLine = false;
                formattedSourceCode += inputSourceCode.charAt(i);
            } else if (inputSourceCode.charAt(i) == ReservedChars.CLOSE_BRACKET.getValue()) {
                bracketCounter--;
                newLine = false;
                formattedSourceCode += inputSourceCode.charAt(i);
            } else if (inputSourceCode.charAt(i) == ReservedChars.OPEN_SQUARE_BRACKET.getValue()) {
                squareBracketCounter++;
                newLine = false;
                formattedSourceCode += inputSourceCode.charAt(i);
            } else if (inputSourceCode.charAt(i) == ReservedChars.CLOSE_SQUARE_BRACKET.getValue()) {
                squareBracketCounter--;
                newLine = false;
                formattedSourceCode += inputSourceCode.charAt(i);
            } else if (inputSourceCode.charAt(i) == ReservedChars.LINE_SEPARATOR.getValue()
                    && bracketCounter == 0 && squareBracketCounter == 0) {
                formattedSourceCode += inputSourceCode.charAt(i);
                formattedSourceCode += SIGN_NEXT_LINE;
                newLine = true;
            } else {
                if (newLine) {
                    formattedSourceCode += writeMultipleTabs(wavedBracketCounter);
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
