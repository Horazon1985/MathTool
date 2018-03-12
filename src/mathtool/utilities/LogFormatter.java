package mathtool.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
    
    private LogFormatter() {
    }

    public static Formatter getFormatter() {
        return new LogFormatter();
    }
    
    @Override
    public String format(LogRecord record) {
        return DATE_FORMAT.format(new Date(record.getMillis())) 
                + " - "
                + formatMessage(record);
    }
    
}
