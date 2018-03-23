package mathtool.utilities;

import algorithmexecuter.exceptions.AlgorithmCompileException;
import algorithmexecuter.exceptions.AlgorithmException;
import algorithmexecuter.exceptions.AlgorithmExecutionException;
import exceptions.EvaluationException;
import exceptions.ExpressionException;
import exceptions.MathToolException;
import java.io.File;
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

    private static final String LOG_FILES_DIR = "log";
    private static final String LOG_FILE_PREFIX = "log ";
    private static final String LOG_FILE_FORMAT = ".txt";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String CONFIG_NOT_FOUND = "config.xml not found.";
    private static final String CONFIG_CANNOT_BE_PARSED = "config.xml cannot be parsed. Loading default configurations.";
    
    private static final String INPUT = "Input: ";
    private static final String EXPRESSION_EXCEPTION = "Exception while expression parsing occurred: ";
    private static final String EVALUATION_EXCEPTION = "Exception while expression evaluation occurred: ";
    private static final String ALGORITHM_COMPILATION_EXCEPTION = "Exception while compiling algorithm occurred: ";
    private static final String ALGORITHM_EXECUTION_EXCEPTION = "Exception while algorithm execution occurred: ";
    private static final String COMPUTATION_ABORTED = "Computation aborted.";
    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception occurred: ";
    private static final String COMPUTATION_DURATION = "Computation duration: {0} milliseconds.";
    
    private static final String ALGORITHM_INPUT = "Algorithm input: ";
    private static final String ALGORITHM_COMPILATION_SUCCESSFUL = "Algorithm compilation successful.";
    private static final String ALGORITHM_EXECUTION_SUCCESSFUL = "Algorithm execution successful.";
    
    private static final String NEXT_LINE = System.lineSeparator();

    private final Logger log;

    private MathToolLogger() {
        log = Logger.getLogger(LOG_NAME);
    }

    public static MathToolLogger initLogger() {
        MathToolLogger log = new MathToolLogger();
        try {
            String fileName = generateLogFileName();
            File logFile = new File(LOG_FILES_DIR);
            if (!logFile.exists()) {
                logFile.mkdir();
            }
            FileHandler fh = new FileHandler(fileName, true);
            fh.setFormatter(LogFormatter.getFormatter());
            log.addHandler(fh);
        } catch (IOException e) {
        }
        return log;
    }

    private void addHandler(Handler hndlr) throws SecurityException {
        log.addHandler(hndlr);
    }

    private static String generateLogFileName() {
        return LOG_FILES_DIR + "/" + LOG_FILE_PREFIX + DATE_FORMAT.format(new Date()) + LOG_FILE_FORMAT;
    }

    public void logConfigNotFound() {
        log.log(Level.WARNING, CONFIG_NOT_FOUND + "{0}", NEXT_LINE);
    }

    public void logConfigCannotBeParsed() {
        log.log(Level.WARNING, CONFIG_CANNOT_BE_PARSED + "{0}", NEXT_LINE);
    }

    public void logMessage(String message) {
        log.log(Level.INFO, message + "{0}", NEXT_LINE);
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
        log.log(Level.WARNING, EXPRESSION_EXCEPTION + e.getMessage() + NEXT_LINE, e);
    }

    private void logEvaluationException(EvaluationException e) {
        log.log(Level.WARNING, EVALUATION_EXCEPTION + e.getMessage() + NEXT_LINE, e);
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
    
    public void logAlgorithmInput(String algorithmInput) {
        log.log(Level.INFO, ALGORITHM_INPUT + "{0}" + NEXT_LINE, algorithmInput);
    }

    public void logAlgorithmCompilationSuccessful() {
        log.log(Level.INFO, ALGORITHM_COMPILATION_SUCCESSFUL + "{0}", NEXT_LINE);
    }
    
    public void logAlgorithmExecutionSuccessful() {
        log.log(Level.INFO, ALGORITHM_EXECUTION_SUCCESSFUL + "{0}", NEXT_LINE);
    }
    
    public void logAlgorithmException(AlgorithmException e) {
        if (e instanceof AlgorithmCompileException) {
            logAlgorithmCompileException((AlgorithmCompileException) e);
        } else if (e instanceof AlgorithmExecutionException) {
            logAlgorithmExecutionException((AlgorithmExecutionException) e);
        }
    }
    
    private void logAlgorithmCompileException(AlgorithmCompileException e) {
        log.log(Level.WARNING, ALGORITHM_COMPILATION_EXCEPTION + e.getMessage() + NEXT_LINE, e);
    }

    private void logAlgorithmExecutionException(AlgorithmExecutionException e) {
        log.log(Level.WARNING, ALGORITHM_EXECUTION_EXCEPTION + e.getMessage() + NEXT_LINE, e);
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
