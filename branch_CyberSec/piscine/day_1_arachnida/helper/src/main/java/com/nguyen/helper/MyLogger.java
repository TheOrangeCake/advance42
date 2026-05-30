package com.nguyen.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
    private static final Logger logger = LogManager.getLogger(MyLogger.class);

    public static void debug(String message) {
        logger.debug(message);
    }
    public static void info(String message) {
        logger.info(message);
    }
    public static void warn(String message) {
        logger.warn(message);
    }
    public static void error(String message) {
        logger.error(message);
    }
    public static void fatal(String message) {
        logger.fatal(message);
    }

    public static void debug(String message, Exception e) {
        logger.debug(message, e);
    }
    public static void info(String message, Exception e) {
        logger.info(message, e);
    }
    public static void warn(String message, Exception e) {
        logger.warn(message, e);
    }
    public static void error(String message, Exception e) {
        logger.error(message, e);
    }
    public static void fatal(String message, Exception e) {
        logger.fatal(message, e);
    }
}
