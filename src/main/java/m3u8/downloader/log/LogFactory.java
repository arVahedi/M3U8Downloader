package m3u8.downloader.log;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Gladiator on 5/22/2016 AD.
 */
public class LogFactory implements Serializable {
    private static LogFactory instance = null;
    private static HashMap<Class, Logger> loggerBag = new HashMap<>();

    private LogFactory() {
    }

    public static LogFactory getInstance() {
        if (instance == null) {
            instance = new LogFactory();
        }

        return instance;
    }

    public static Logger getLogger(Class className) {
        if (loggerBag.containsKey(className)) {
            return loggerBag.get(className);
        }
        loggerBag.put(className, LogManager.getLogger(className));
        return loggerBag.get(className);
    }

    public static void methodCalled(Class className) {
        getLogger(className).trace(String.format("%s : %s() Method Called.",
                className.getSimpleName(),
                Thread.currentThread().getStackTrace()[2].getMethodName()));
    }

    public static void methodCalled(Class className, String description) {
        getLogger(className).trace(String.format("%s : %s() Method Called. [%s]",
                className.getSimpleName(),
                Thread.currentThread().getStackTrace()[2].getMethodName(), description));
    }

    public static void methodReturn(Class className) {
        getLogger(className).trace(String.format("%s : %s() Method Return.",
                className.getSimpleName(),
                Thread.currentThread().getStackTrace()[2].getMethodName()));
    }

    public static void methodReturn(Class className, String description) {
        getLogger(className).trace(String.format("%s : %s() Method Return. [%s]",
                className.getSimpleName(),
                Thread.currentThread().getStackTrace()[2].getMethodName(), description));
    }

    public static Level setLoggerLevel(String loggerName, String levelName) {
        Logger logger = Logger.getLogger(loggerName);
        logger.setLevel(Level.toLevel(levelName.toUpperCase()));

        /*switch (levelName.toUpperCase()) {
            case "OFF":
                logger.setLevel(Level.OFF);
                break;
            case "FATAL":
                logger.setLevel(Level.FATAL);
                break;
            case "ERROR":
                logger.setLevel(Level.ERROR);
                break;
            case "WARN":
                logger.setLevel(Level.WARN);
                break;
            case "INFO":
                logger.setLevel(Level.INFO);
                break;
            case "DEBUG":
                logger.setLevel(Level.DEBUG);
                break;
            case "TRACE":
                logger.setLevel(Level.TRACE);
                break;
            case "ALL":
                logger.setLevel(Level.ALL);
                break;
            default:
                getLogger(LogFactory.class).error(String.format("Invalid value for logger level. [%s]", levelName));
                break;
        }*/

        getLogger(LogFactory.class).info(String.format("Set level for [%s] logger to [%s]", logger.getName(), logger.getLevel()));
        return logger.getLevel();
    }

    public static Level getLoggerLevel(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        getLogger(LogFactory.class).info(String.format("Level for [%s] logger is [%s]", logger.getName(), logger.getLevel()));
        return logger.getLevel();
    }
}
