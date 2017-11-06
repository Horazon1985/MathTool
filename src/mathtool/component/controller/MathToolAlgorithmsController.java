package mathtool.component.controller;

import algorithmexecuter.CompilerUtils;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.model.Algorithm;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JFileChooser;
import mathtool.component.components.MathToolAlgorithmsGUI;

public class MathToolAlgorithmsController {

    private static final String SIGN_TAB = "\t";
    private static final String SIGN_NEXT_LINE = "\n";

    private static final String CODE_IF = Keyword.IF.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue() + ReservedChars.END.getStringValue();
    private static final String CODE_IF_ELSE = Keyword.IF.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue() + ReservedChars.END.getStringValue()
            + Keyword.ELSE.getValue()
            + ReservedChars.BEGIN.getStringValue() + ReservedChars.END.getStringValue();
    private static final String CODE_WHILE = Keyword.WHILE.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue() + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue() + ReservedChars.END.getStringValue();
    private static final String CODE_DO_WHILE = Keyword.DO.getValue()
            + ReservedChars.BEGIN.getStringValue() + ReservedChars.END.getStringValue()
            + Keyword.WHILE.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + ReservedChars.CLOSE_BRACKET.getStringValue();
    private static final String CODE_FOR = Keyword.FOR.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + ReservedChars.ARGUMENT_SEPARATOR.getStringValue()
            + ReservedChars.ARGUMENT_SEPARATOR.getStringValue()
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue() + ReservedChars.END.getStringValue();
    ;

    private static MathToolAlgorithmsGUI mathToolAlgorithmsGUI;

    public static void setMathToolAlgorithmsGUI(MathToolAlgorithmsGUI mtAlgorithmsGUI) {
        mathToolAlgorithmsGUI = mtAlgorithmsGUI;
    }

    public static String formatSourceCodeFromEditor(String inputSourceCode) {
        return MathToolAlgorithmsController.formatSourceCode(getPlainCode(inputSourceCode));
    }

    public static String getPlainCode(String code) {
        code = code.replaceAll(SIGN_NEXT_LINE, "");
        code = code.replaceAll(SIGN_TAB, "");
        return code;
    }

    private static String formatSourceCode(String inputSourceCode) {
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

                // Im Falle eines darauffolgenden "else" keine neue Zeile!
                newLine = !(i < inputSourceCode.length() - 1 && inputSourceCode.substring(i + 1).startsWith(Keyword.ELSE.getValue()));

                formattedSourceCode += writeMultipleTabs(wavedBracketCounter);
                formattedSourceCode += inputSourceCode.charAt(i);

                if (newLine) {
                    formattedSourceCode += SIGN_NEXT_LINE;
                }
                
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

    public static String writeCompiledCode(List<Algorithm> algorithms) {
        String compiledCode = "";
        for (Algorithm alg : algorithms) {
            compiledCode += alg.toCommandString();
        }
        return formatSourceCode(compiledCode);
    }

    public static void saveAlgorithm() {
        JFileChooser saveDialog = new JFileChooser();
        saveDialog.showSaveDialog(mathToolAlgorithmsGUI);
        try {
            String path = saveDialog.getSelectedFile().getPath() + ".txt";
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(path));
                String algorithmCode = mathToolAlgorithmsGUI.getAlgorithmEditor().getText();
                String[] lines = algorithmCode.split(SIGN_NEXT_LINE);
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                }
            }
        } catch (Exception e) {
        }
    }

    public static void loadAlgorithm() {
        JFileChooser openDialog = new JFileChooser();
        openDialog.showOpenDialog(mathToolAlgorithmsGUI);
        String path = openDialog.getSelectedFile().getPath();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String algorithmCode = "";
            String line;
            while ((line = reader.readLine()) != null) {
                algorithmCode += line + SIGN_NEXT_LINE;
            }
            mathToolAlgorithmsGUI.getAlgorithmEditor().setText(algorithmCode);
        } catch (IOException e) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
    }

}
