package algorithmexecuter.output;

import algorithmexecuter.model.identifier.Identifier;
import algorithmexecuter.model.Algorithm;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import mathtool.lang.translator.Translator;

public abstract class AlgorithmOutputPrinter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    private static final String GUI_MathToolAlgorithmsGUI_START_COMPILING_ALGORITHM = "GUI_MathToolAlgorithmsGUI_START_COMPILING_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_COMPILING_ALGORITHM_SUCCESSFUL = "GUI_MathToolAlgorithmsGUI_COMPILING_ALGORITHM_SUCCESSFUL";
    private static final String GUI_MathToolAlgorithmsGUI_START_EXECUTING_ALGORITHM = "GUI_MathToolAlgorithmsGUI_START_EXECUTING_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_OUTPUT_OF_ALGORITHM = "GUI_MathToolAlgorithmsGUI_OUTPUT_OF_ALGORITHM";
    private static final String GUI_MathToolAlgorithmsGUI_EXECUTION_OF_ALGORITHM_SUCCESSFUL = "GUI_MathToolAlgorithmsGUI_EXECUTION_OF_ALGORITHM_SUCCESSFUL";
    private static final String GUI_MathToolAlgorithmsGUI_EXECUTION_OF_ALGORITHM_ABORTED = "GUI_MathToolAlgorithmsGUI_EXECUTION_OF_ALGORITHM_ABORTED";
    private static final String GUI_MathToolAlgorithmsGUI_EXCEPTION_IN_ALGORITHM_OCCURRED = "GUI_MathToolAlgorithmsGUI_EXCEPTION_IN_ALGORITHM_OCCURRED";
    
    private static JTextPane outputArea;

    public static void setOutputArea(JTextPane outputArea) {
        AlgorithmOutputPrinter.outputArea = outputArea;
    }

    public static void clearOutput() {
        if (outputArea == null) {
            return;
        }
        outputArea.setText("");
    }

    private static void print(StyledDocument doc, SimpleAttributeSet keyWord, String line) {
        try {
            doc.insertString(doc.getLength(), withDate(line), keyWord);
        } catch (Exception e) {
        }
    }
    
    private static void println(StyledDocument doc, SimpleAttributeSet keyWord, String line) {
        print(doc, keyWord, line + "\n");
    }

    public static void printStartParsingAlgorithms() {
        if (outputArea == null) {
            return;
        }
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_START_COMPILING_ALGORITHM));
    }

    public static void printEndParsingAlgorithms() {
        if (outputArea == null) {
            return;
        }
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_COMPILING_ALGORITHM_SUCCESSFUL));
    }

    public static void printStartAlgorithmData() {
//        outputArea.setText(withDate("Start algorithm execution."));
        if (outputArea == null) {
            return;
        }
        StyledDocument doc = outputArea.getStyledDocument();

        //  Define a keyword attribute
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
//        StyleConstants.setForeground(keyWord, Color.RED);
//        StyleConstants.setBackground(keyWord, Color.YELLOW);
//        StyleConstants.setBold(keyWord, true);

        println(doc, keyWord, Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_START_EXECUTING_ALGORITHM));
        try {
//            doc.insertString(0, "Start of text\n", null);
//            doc.insertString(doc.getLength(), "\nEnd of text", keyWord);
        } catch (Exception e) {
        }
    }

    public static void printOutput(Algorithm alg, Identifier identifier) {
        if (outputArea == null || identifier == null) {
            return;
        }
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_OUTPUT_OF_ALGORITHM, alg.getName(), identifier.getValue()));
    }

    public static void printEndAlgorithmData() {
        if (outputArea == null) {
            return;
        }
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_EXECUTION_OF_ALGORITHM_SUCCESSFUL));
    }

    public static void printAbortAlgorithm() {
        if (outputArea == null) {
            return;
        }
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_EXECUTION_OF_ALGORITHM_ABORTED));
    }

    public static void printException(Exception e) {
        if (outputArea == null) {
            return;
        }
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, Translator.translateOutputMessage(GUI_MathToolAlgorithmsGUI_EXCEPTION_IN_ALGORITHM_OCCURRED, e.getMessage()));
    }

    private static String withDate(String s) {
        return DATE_FORMAT.format(new Date()) + ": " + s;
    }

}
