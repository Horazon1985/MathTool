package algorithmexecutor.output;

import algorithmexecutor.identifier.Identifier;
import algorithmexecutor.model.Algorithm;
import java.awt.Color;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public abstract class AlgorithmOutputPrinter {

    private static JTextPane outputArea;

    public static void setOutputArea(JTextPane outputArea) {
        AlgorithmOutputPrinter.outputArea = outputArea;
    }

    public static void clearOutput() {
        outputArea.setText("");
    }

    public static void printStartAlgorithmData() {
//        outputArea.setText(withDate("Start algorithm execution."));
        StyledDocument doc = outputArea.getStyledDocument();

        //  Define a keyword attribute
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
//        StyleConstants.setForeground(keyWord, Color.RED);
//        StyleConstants.setBackground(keyWord, Color.YELLOW);
//        StyleConstants.setBold(keyWord, true);

        try {
            doc.insertString(0, withDate("Start algorithm execution \n"), keyWord);
//            doc.insertString(0, "Start of text\n", null);
//            doc.insertString(doc.getLength(), "\nEnd of text", keyWord);
        } catch (Exception e) {
        }
    }

    public static void printOutput(Algorithm alg, Identifier identifier) {
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        try {
            doc.insertString(doc.getLength(), withDate("Output of algorithm " + alg.getName() + ": " + identifier.getValue() + "\n"), keyWord);
        } catch (Exception e) {
        }
    }
    
    public static void printEndAlgorithmData() {
        StyledDocument doc = outputArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        try {
            doc.insertString(doc.getLength(), withDate("Terminate algorithm execution"), keyWord);
        } catch (Exception e) {
        }
    }

    private static String withDate(String s) {
        Date now = new Date();
//        return now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds() + ": " + s;
        return now + ": " + s;
    }

}
