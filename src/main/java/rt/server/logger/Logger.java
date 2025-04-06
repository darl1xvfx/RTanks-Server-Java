package rt.server.logger;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class Logger {

    public static final String INFO = "INFO";
    public static final String ERROR = "ERROR";
    public static final String WARNING = "WARNING";
    public static final String DEBUG = "DEBUG";

    private static final ch.qos.logback.classic.Logger INTERNAL_LOGGER =
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.class);

    public static void init() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("ROOT").setLevel(Level.ERROR);
        INTERNAL_LOGGER.setLevel(Level.DEBUG);
    }

    public static void log(String type, String message) {
        String callerClass = new Throwable().getStackTrace()[1].getClassName();
        String shortClassName = callerClass.substring(callerClass.lastIndexOf('.') + 1);

        switch (type) {
            case INFO:
                INTERNAL_LOGGER.info("[{}] {} - {}", type, shortClassName, message);
                break;
            case ERROR:
                INTERNAL_LOGGER.error("[{}] {} - {}", type, shortClassName, message);
                break;
            case WARNING:
                INTERNAL_LOGGER.warn("[{}] {} - {}", type, shortClassName, message);
                break;
            case DEBUG:
                INTERNAL_LOGGER.debug("[{}] {} - {}", type, shortClassName, message);
                break;
            default:
                INTERNAL_LOGGER.info("[UNKNOWN] {} - {}", shortClassName, message);
                break;
        }
    }
}