package mathtool.utilities;

import algorithmexecuter.exceptions.AlgorithmException;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import exceptions.MathToolException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MathToolLogger {

    private static final String LOG_NAME = "MathToolLogger";

    private static final String LOG_FILE_PREFIX = "log ";
    private static final String LOG_FILE_FORMAT = ".txt";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String INPUT = "Input: ";
    private static final String EXPRESSION_EXCEPTION = "Exception while expression parsing occurred: ";
    private static final String EVALUATION_EXCEPTION = "Exception while expression evaluation occurred: ";
    private static final String ALGORITHM_EXCEPTION = "Exception while algorithm execution occurred: ";
    private static final String COMPUTATION_ABORTED = "Computation aborted.";
    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception occurred: ";
    private static final String COMPUTATION_DURATION = "Computation duration: {0} milliseconds.";
    private static final String NEXT_LINE = System.lineSeparator();

    private final Logger log;

    private MathToolLogger() {
        log = Logger.getLogger(LOG_NAME);
    }

    public static MathToolLogger initLogger() {
        MathToolLogger log = new MathToolLogger();
        try {
            FileHandler fh = new FileHandler(generateLogFileName(), true);
            fh.setFormatter(LogFormatter.getFormatter());
            log.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return log;
    }

    private void addHandler(Handler hndlr) throws SecurityException {
        log.addHandler(hndlr);
    }

    private static String generateLogFileName() {
        return LOG_FILE_PREFIX + DATE_FORMAT.format(new Date()) + LOG_FILE_FORMAT;
    }

    public void logInput(String input) {
        log.log(Level.INFO, INPUT + "{0}" + NEXT_LINE, input);
    }

    public void logMathToolException(MathToolException e) {
        if (e instanceof ExpressionException) {
            logExpressionException((ExpressionException) e);
        } else if (e instanceof EvaluationException) {
            logEvaluationException((EvaluationException) e);
        }
    }

    private void logExpressionException(ExpressionException e) {
        log.log(Level.SEVERE, EXPRESSION_EXCEPTION + e.getMessage() + NEXT_LINE, e);
    }

    private void logEvaluationException(EvaluationException e) {
        log.log(Level.SEVERE, EVALUATION_EXCEPTION + e.getMessage() + NEXT_LINE, e);
    }

    public void logAlgorithmException(AlgorithmException e) {
        log.log(Level.SEVERE, ALGORITHM_EXCEPTION + e.getMessage() + NEXT_LINE, e);
    }

    public void logComputationAborted() {
        log.log(Level.INFO, COMPUTATION_ABORTED + "{0}", NEXT_LINE);
    }

    public void logException(Exception e) {
        String stackTrace = stackTraceToString(e);
        log.log(Level.SEVERE, UNEXPECTED_EXCEPTION + e.getMessage() + stackTrace + NEXT_LINE, e);
    }

    public void logComputationDuration(long beginningTime) {
        log.log(Level.INFO, COMPUTATION_DURATION + NEXT_LINE, new Date().getTime() - beginningTime);
    }

    private String stackTraceToString(Exception e) {
        StackTraceElement[] elements = e.getStackTrace();
        String stackTrace = NEXT_LINE;
        for (int i = 1; i < elements.length; i++) {
            StackTraceElement s = elements[i];
            stackTrace += "\tat " + s.getClassName() + "." + s.getMethodName()
            + "(" + s.getFileName() + ":" + s.getLineNumber() + ")" + NEXT_LINE;
        }
        return stackTrace;
    }
    
}
