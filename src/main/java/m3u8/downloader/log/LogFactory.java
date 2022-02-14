package m3u8.downloader.log;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
