package mathtool.component.controller;

import algorithmexecuter.CompilerUtils;
import algorithmexecuter.enums.FixedAlgorithmNames;
import algorithmexecuter.enums.IdentifierType;
import algorithmexecuter.enums.Keyword;
import algorithmexecuter.enums.Operators;
import algorithmexecuter.enums.ReservedChars;
import algorithmexecuter.model.Algorithm;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import mathtool.component.components.MathToolAlgorithmsGUI;

public class MathToolAlgorithmsController {

    private static final String SIGN_TAB = "\t";
    private static final String SIGN_NEXT_LINE = "\n";

    private static final String PLACEHOLFER_0 = "0";
    private static final String PLACEHOLFER_1 = "1";
    private static final String PLACEHOLFER_2 = "2";

    private static final String CODE_MAIN_ALGORITHM_WITHOUT_RETURN_TYPE = FixedAlgorithmNames.MAIN.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue() + SIGN_NEXT_LINE + SIGN_NEXT_LINE + ReservedChars.END.getStringValue();
    private static final String CODE_IF = Keyword.IF.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + PLACEHOLFER_0
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue()
            + SIGN_NEXT_LINE + SIGN_NEXT_LINE
            + ReservedChars.END.getStringValue();
    private static final String CODE_IF_ELSE = Keyword.IF.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + PLACEHOLFER_0
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue()
            + SIGN_NEXT_LINE + SIGN_NEXT_LINE
            + ReservedChars.END.getStringValue()
            + Keyword.ELSE.getValue()
            + ReservedChars.BEGIN.getStringValue()
            + SIGN_NEXT_LINE + SIGN_NEXT_LINE
            + ReservedChars.END.getStringValue();
    private static final String CODE_WHILE = Keyword.WHILE.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + PLACEHOLFER_0
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue()
            + SIGN_NEXT_LINE + SIGN_NEXT_LINE
            + ReservedChars.END.getStringValue();
    private static final String CODE_DO_WHILE = Keyword.DO.getValue()
            + ReservedChars.BEGIN.getStringValue()
            + SIGN_NEXT_LINE + SIGN_NEXT_LINE
            + ReservedChars.END.getStringValue()
            + Keyword.WHILE.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + PLACEHOLFER_0
            + ReservedChars.CLOSE_BRACKET.getStringValue();
    private static final String CODE_FOR = Keyword.FOR.getValue()
            + ReservedChars.OPEN_BRACKET.getStringValue()
            + PLACEHOLFER_0
            + ReservedChars.ARGUMENT_SEPARATOR.getStringValue()
            + PLACEHOLFER_1
            + ReservedChars.ARGUMENT_SEPARATOR.getStringValue()
            + PLACEHOLFER_2
            + ReservedChars.CLOSE_BRACKET.getStringValue()
            + ReservedChars.BEGIN.getStringValue()
            + SIGN_NEXT_LINE + SIGN_NEXT_LINE
            + ReservedChars.END.getStringValue();

    private static MathToolAlgorithmsGUI mathToolAlgorithmsGUI;
    
    private static final Highlighter.HighlightPainter WHITE_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE);

    private static final Highlighter.HighlightPainter ERROR_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
    
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

    //////////////////////////////// Codegenerierung //////////////////////////////////////
    public static String generateMainAlgorithm(IdentifierType returnType) {
        String mainAlgorithmCode = "";
        if (returnType != null) {
            mainAlgorithmCode += returnType.getValue() + " ";
        }
        return mainAlgorithmCode + CODE_MAIN_ALGORITHM_WITHOUT_RETURN_TYPE;
    }

    public static String generateSubroutine(IdentifierType returnType, String subroutineName, IdentifierType[] parameterTypes, String[] parameterNames) {
        String subroutineCode = "";
        if (returnType != null) {
            subroutineCode += returnType.getValue() + " ";
        }
        subroutineCode += subroutineName + ReservedChars.OPEN_BRACKET.getStringValue();
        for (int i = 0; i < parameterTypes.length; i++) {
            subroutineCode += parameterTypes[i].getValue() + " " + parameterNames[i];
            if (i < parameterTypes.length - 1) {
                subroutineCode += ReservedChars.ARGUMENT_SEPARATOR.getStringValue();
            }
        }
        subroutineCode += ReservedChars.CLOSE_BRACKET.getStringValue() + ReservedChars.BEGIN.getStringValue() + SIGN_NEXT_LINE + SIGN_NEXT_LINE + ReservedChars.END.getStringValue();
        return subroutineCode;
    }

    public static String generateControlStructureIf(String ifCondition) {
        return CODE_IF.replaceAll(PLACEHOLFER_0, ifCondition);
    }

    public static String generateControlStructureIfElse(String ifCondition) {
        return CODE_IF_ELSE.replaceAll(PLACEHOLFER_0, ifCondition);
    }

    public static String generateControlStructureWhile(String ifCondition) {
        return CODE_WHILE.replaceAll(PLACEHOLFER_0, ifCondition);
    }

    public static String generateControlStructureDoWhile(String ifCondition) {
        return CODE_DO_WHILE.replaceAll(PLACEHOLFER_0, ifCondition);
    }

    public static String generateControlStructureFor(String initialization, String endCondition, String increment) {
        return CODE_FOR.replaceAll(PLACEHOLFER_0, initialization).replaceAll(PLACEHOLFER_1, endCondition).replaceAll(PLACEHOLFER_2, increment);
    }

    public static String generateCommandDefine(IdentifierType type, String identifierName, String expr) {
        if (expr.isEmpty()) {
            return type.getValue() + " " + identifierName + ReservedChars.LINE_SEPARATOR.getValue();
        }
        return type.getValue() + " " + identifierName + Operators.DEFINE.getValue() + expr + ReservedChars.LINE_SEPARATOR.getValue();
    }

    public static String generateCommandReturn(String identifierName) {
        return Keyword.RETURN.getValue() + " " + identifierName + ReservedChars.LINE_SEPARATOR.getValue();
    }

    ///////////////////// Methoden für Fehlerrückmeldungen ////////////////////////
    public static void unmarkLinesWithInvalidCode() {
        if (mathToolAlgorithmsGUI.isComputing()) {
            return;
        }
        try {
            mathToolAlgorithmsGUI.getAlgorithmEditor().getHighlighter().removeAllHighlights();
            mathToolAlgorithmsGUI.getAlgorithmEditor().getHighlighter().addHighlight(0, mathToolAlgorithmsGUI.getAlgorithmEditor().getText().length(), WHITE_PAINTER);
        } catch (BadLocationException e) {
        }
    }

    public static void markLinesWithInvalidCode(Integer... lineNumbers) {
        if (lineNumbers == null || lineNumbers.length == 0) {
            return;
        }

        String algorithmCode = mathToolAlgorithmsGUI.getAlgorithmEditor().getText();

        List<Integer> lineEndIndices = new ArrayList<>();
        int lineBeginning;
        while (algorithmCode.contains(SIGN_NEXT_LINE)) {
            if (lineEndIndices.isEmpty()) {
                lineBeginning = 0;
            } else {
                lineBeginning = lineEndIndices.get(lineEndIndices.size() - 1) + 1;
            }
            lineEndIndices.add(lineBeginning + algorithmCode.indexOf(SIGN_NEXT_LINE));
            algorithmCode = algorithmCode.substring(algorithmCode.indexOf(SIGN_NEXT_LINE) + 1);
        }
        if (!algorithmCode.isEmpty()) {
            lineEndIndices.add(mathToolAlgorithmsGUI.getAlgorithmEditor().getText().length());
        }

        Set<Integer> linesToMark = new HashSet<>();
        linesToMark.addAll(Arrays.asList(lineNumbers));
        
        for (Integer lineNumber : linesToMark) {
            if (lineNumber < 0 || lineNumber >= lineEndIndices.size()) {
                continue;
            }
            try {
                if (lineNumber == 0) {
                    mathToolAlgorithmsGUI.getAlgorithmEditor().getHighlighter().addHighlight(0, lineEndIndices.get(1), ERROR_PAINTER);
                } else {
                    mathToolAlgorithmsGUI.getAlgorithmEditor().getHighlighter().addHighlight(lineEndIndices.get(lineNumber - 1) + 1, lineEndIndices.get(lineNumber), ERROR_PAINTER);
                }
            } catch (BadLocationException e) {
            }
        }

    }

}
