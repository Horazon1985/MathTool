package mathtool.utilities;

import algorithmexecuter.exceptions.AlgorithmException;
import exceptions.MathToolException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MathToolLogger {

    private static final String LOG_NAME = "MathToolLogger";

    private static final String INPUT = "Input: ";
    private static final String EXCEPTION = "Following Exception occurred: ";
    
    private final Logger log;

    private MathToolLogger() {
        log = Logger.getLogger(LOG_NAME);
    }
    
    public static MathToolLogger getLogger() {
        return new MathToolLogger();
    }

    public void addHandler(Handler hndlr) throws SecurityException {
        log.addHandler(hndlr);
    }
    
    public void logInput(String input) {
        log.log(Level.INFO, INPUT + input);
    }
    
    public void logMathToolException(MathToolException e) {
        log.log(Level.SEVERE, EXCEPTION + e.getMessage());
    }
    
    public void logAlgorithmException(AlgorithmException e) {
        log.log(Level.SEVERE, EXCEPTION + e.getMessage());
    }
    
    public void logException(Exception e) {
        log.log(Level.SEVERE, EXCEPTION + e.getStackTrace());
    }
    
}
