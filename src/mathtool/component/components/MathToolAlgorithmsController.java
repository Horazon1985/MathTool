package mathtool.component.components;

import algorithmexecuter.CompilerUtils;
import algorithmexecuter.enums.FixedAlgorithmNames;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.ReservedChars;
import org.jsoup.parser.Parser;

public class MathToolAlgorithmsController {

    private static final String BEGIN_HTML_DOC = "<html>\n<head>\n</head>\n<body>\n";
    private static final String END_HTML_DOC = "\n</body>\n</html>";
    private static final String BEGINNING_TAG_BOLD_BLUE = "<b><font color=\"blue\">";
    private static final String ENDING_TAG_BOLD_BLUE = "</font></b>";
    private static final String BEGINNING_TAG_ITALIC = "<i>";
    private static final String ENDING_TAG_ITALIC = "</i>";
    private static final String TAG_TAB = "&#8195;";
    private static final String TAG_NEXT_LINE = "<br>";
    private static final String SIGN_TAB = "\t";
    private static final String SIGN_NEXT_LINE = "\n";

    public static String emphasizeWordsInAlgorithmSourceCode(String inputSourceCode) {
        String formattedSourceCode = emphaziseWordsOfGivenType(inputSourceCode, Keyword.class);
        formattedSourceCode = emphaziseWordsOfGivenType(formattedSourceCode, FixedAlgorithmNames.class);
        return formattedSourceCode;
    }

    private static String emphaziseWordsOfGivenType(String inputSourceCode, Class<? extends Enum> clazz) {
        String[] tags = getTags(clazz);
        if (tags == null) {
            return inputSourceCode;
        }
        String beginningTag = tags[0], endingTag = tags[1];
        Enum[] values = getEnumValues(clazz);
        if (values == null) {
            /* 
            Kann bei momentaner Implementierung nicht passieren, dennoch 
            sicherheitshalber, falls der Code mal (unvorsichtig) erweitert wird.
             */
            return inputSourceCode;
        }

        String formattedSourceCode = inputSourceCode;
        int indexOfKeyword;
        for (Enum word : values) {
            indexOfKeyword = formattedSourceCode.indexOf(word.toString(), 0);
            while (indexOfKeyword >= 0) {
                formattedSourceCode = emphaziseWordIfNotEmphazised(inputSourceCode, word, indexOfKeyword);
                if (inputSourceCode.equals(formattedSourceCode)) {
                    indexOfKeyword = formattedSourceCode.indexOf(word.toString(), indexOfKeyword + word.toString().length() + 1);
                } else {
                    indexOfKeyword = formattedSourceCode.indexOf(word.toString(),
                            indexOfKeyword + beginningTag.length() + word.toString().length() + endingTag.length() + 1);
                }
                inputSourceCode = formattedSourceCode;
            }
        }
        return formattedSourceCode;
    }

    private static String[] getTags(Class<? extends Enum> clazz) {
        if (clazz.equals(Keyword.class)) {
            return new String[]{BEGINNING_TAG_BOLD_BLUE, ENDING_TAG_BOLD_BLUE};
        } else if (clazz.equals(FixedAlgorithmNames.class)) {
            return new String[]{BEGINNING_TAG_ITALIC, ENDING_TAG_ITALIC};
        }
        return null;
    }

    private static Enum[] getEnumValues(Class<? extends Enum> clazz) {
        if (clazz.equals(Keyword.class)) {
            return Keyword.values();
        } else if (clazz.equals(FixedAlgorithmNames.class)) {
            return FixedAlgorithmNames.values();
        }
        return null;
    }

    private static String emphaziseWordIfNotEmphazised(String inputSourceCode, Enum name, int beginningIndex) {
        String beginningTag, endingTag;
        if (name instanceof Keyword) {
            beginningTag = BEGINNING_TAG_BOLD_BLUE;
            endingTag = ENDING_TAG_BOLD_BLUE;
        } else if (name instanceof FixedAlgorithmNames) {
            beginningTag = BEGINNING_TAG_ITALIC;
            endingTag = ENDING_TAG_ITALIC;
        } else {
            return inputSourceCode;
        }
        String formattedSourceCode = inputSourceCode;
        int i = inputSourceCode.indexOf(name.toString(), beginningIndex);
        if (!isPartOfIdentifierName(inputSourceCode, i, name)
                && !getSubstring(inputSourceCode,
                        i - beginningTag.length(),
                        beginningTag.length() + name.toString().length() + endingTag.length())
                        .equals(beginningTag + name.toString() + endingTag)) {
            formattedSourceCode = inputSourceCode.substring(0, i) + beginningTag + name.toString() + endingTag
                    + inputSourceCode.substring(i + name.toString().length());
        }
        return formattedSourceCode;
    }

    private static String getSubstring(String s, int beginningIndex, int endingIndex) {
        if (beginningIndex >= s.length() || endingIndex < 0 || beginningIndex > endingIndex) {
            return "";
        }
        return s.substring(Math.max(beginningIndex, 0), Math.min(endingIndex, s.length()));
    }

    private static boolean isPartOfIdentifierName(String input, int i, Enum word) {
        if (i > 0) {
            int asciiValue = (int) input.charAt(i - 1);
            if (asciiValue >= 97 && asciiValue <= 122
                    || asciiValue >= 65 && asciiValue <= 90
                    || asciiValue >= 48 && asciiValue <= 57
                    || asciiValue == 95) {
                return true;
            }
        }
        if (i + word.toString().length() < input.length() - 1) {
            int asciiValue = (int) input.charAt(i + word.toString().length());
            if (asciiValue >= 97 && asciiValue <= 122
                    || asciiValue >= 65 && asciiValue <= 90
                    || asciiValue >= 48 && asciiValue <= 57
                    || asciiValue == 95) {
                return true;
            }
        }
        return false;
    }

    public static String formatSourceCodeFromEditor(String inputSourceCode) {
        String algString = getPlainCode(inputSourceCode);
        String algStringWithoutUTF8 = Parser.unescapeEntities(algString, false);
        String formattedCode = MathToolAlgorithmsController.formatSourceCode(algStringWithoutUTF8);
        formattedCode = emphasizeWordsInAlgorithmSourceCode(formattedCode);
        return formattedCode;
    }

    public static String getPlainCode(String htmlCode) {
        String code = htmlCode;
        String modifiedCode = code;
        String tag;
        do {
            code = modifiedCode;
            if (modifiedCode.contains("<")) {
                tag = modifiedCode.substring(modifiedCode.indexOf("<"), modifiedCode.indexOf(">") + 1);
                modifiedCode = modifiedCode.replaceAll(tag, "");
            }
        } while (modifiedCode.length() < code.length());
        // Zeilenumbrüche und Tabulatoren beseitigen.
        code = code.replaceAll(SIGN_NEXT_LINE, "");
        code = code.replaceAll(SIGN_TAB, "");
        code = code.replaceAll(TAG_NEXT_LINE, "");
        code = code.replaceAll(TAG_TAB, "");
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
        String formattedSourceCode = BEGIN_HTML_DOC;
        for (int i = 0; i < inputSourceCode.length(); i++) {
            if (inputSourceCode.charAt(i) == ReservedChars.BEGIN.getValue()) {
                formattedSourceCode += inputSourceCode.charAt(i);
                bracketCounter++;
                newLine = true;
                formattedSourceCode += TAG_NEXT_LINE;
            } else if (inputSourceCode.charAt(i) == ReservedChars.END.getValue()) {
                bracketCounter--;
                newLine = true;
                formattedSourceCode += writeMultipleTabs(bracketCounter);
                formattedSourceCode += inputSourceCode.charAt(i);
                formattedSourceCode += TAG_NEXT_LINE;
            } else if (inputSourceCode.charAt(i) == ReservedChars.LINE_SEPARATOR.getValue()) {
                formattedSourceCode += inputSourceCode.charAt(i);
                formattedSourceCode += TAG_NEXT_LINE;
                newLine = true;
            } else {
                if (newLine) {
                    formattedSourceCode += writeMultipleTabs(bracketCounter);
                }
                newLine = false;
                formattedSourceCode += inputSourceCode.charAt(i);
            }
        }

        return formattedSourceCode + END_HTML_DOC;
    }

    private static String writeMultipleTabs(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += TAG_TAB;
        }
        return result;
    }

}
