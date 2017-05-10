package algorithmexecutor.output;

import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public abstract class AlgorithmOutputPrinter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    private static final String GUI_START_COMPILING_ALGORITHM = "GUI_START_COMPILING_ALGORITHM";
    private static final String GUI_COMPILING_ALGORITHM_SUCCESSFUL = "GUI_COMPILING_ALGORITHM_SUCCESSFUL";
    private static final String GUI_START_EXECUTING_ALGORITHM = "GUI_START_EXECUTING_ALGORITHM";
    private static final String GUI_OUTPUT_OF_ALGORITHM = "GUI_OUTPUT_OF_ALGORITHM";
    private static final String GUI_EXECUTION_OF_ALGORITHM_SUCCESSFUL = "GUI_EXECUTION_OF_ALGORITHM_SUCCESSFUL";
    private static final String GUI_EXCEPTION_IN_ALGORITHM_OCCURRED = "GUI_EXCEPTION_IN_ALGORITHM_OCCURRED";
    
    private static JTextPane outputArea;

    public static void setOutputArea(JTextPane outputArea) {
        AlgorithmOutputPrinter.outputArea = outputArea;
    }

    public static void clearOutput() {
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
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, "Start parsing algorithms");
    }

    public static void printEndParsingAlgorithms() {
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, "Parsing algorithms successful");
    }

    public static void printStartAlgorithmData() {
//        outputArea.setText(withDate("Start algorithm execution."));
        StyledDocument doc = outputArea.getStyledDocument();

        //  Define a keyword attribute
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
//        StyleConstants.setForeground(keyWord, Color.RED);
//        StyleConstants.setBackground(keyWord, Color.YELLOW);
//        StyleConstants.setBold(keyWord, true);

        println(doc, keyWord, "Start algorithm execution");
        try {
//            doc.insertString(0, "Start of text\n", null);
//            doc.insertString(doc.getLength(), "\nEnd of text", keyWord);
        } catch (Exception e) {
        }
    }

    public static void printOutput(Algorithm alg, Identifier identifier) {
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, "Output of algorithm " + alg.getName() + ": " + identifier.getValue());
    }

    public static void printEndAlgorithmData() {
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, "Terminate algorithm execution");
    }

    public static void printException(Exception e) {
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        println(doc, keyWord, "Exception occurred: " + e.getMessage());
    }

    private static String withDate(String s) {
        return DATE_FORMAT.format(new Date()) + ": " + s;
    }

}
