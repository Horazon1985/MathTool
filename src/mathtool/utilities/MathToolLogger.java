package mathtool.utilities;

import algorithmexecuter.exceptions.AlgorithmException;
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
    private static final String EXCEPTION = "Following exception occurred: ";
    private static final String UNEXPECTED_EXCEPTION = "Following unexpected exception occurred: ";
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
        log.log(Level.SEVERE, EXCEPTION + "{0}", e.getMessage());
    }

    public void logAlgorithmException(AlgorithmException e) {
        log.log(Level.SEVERE, EXCEPTION + "{0}", e.getMessage());
    }

    public void logException(Exception e) {
        log.log(Level.SEVERE, UNEXPECTED_EXCEPTION + e.getStackTrace());
    }

}
