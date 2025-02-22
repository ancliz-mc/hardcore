package me.ancliz.hardcore.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LoggerWrapper {
    private Logger logger;
    private final int CALLING_METHOD = 3;

    public LoggerWrapper(Logger logger) {
        this.logger = logger;
        logger.trace("Logger created");
    }

    public void log(Level level, String message) {
        logger.log(level, message);
    }

    public void debug(Object obj) {
        logger.debug("{}", obj);
    }

    public void trace() {
        if(logger.isTraceEnabled()) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            logger.trace(stackTrace[2].getClassName() + "#" + stackTrace[2].getMethodName() + " called");
        }
    }

    public void trace(String pattern, Object ... obj) {
        logger.trace(pattern, obj);
    }

    public void trace(String message) {
        logger.trace(message);
    }

    public void trace(Object obj) {
        logger.trace("{}", obj);
    }

    public void debug() {
        if(logger.isDebugEnabled()) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            logger.debug(stackTrace[2].getClassName() + "#" + stackTrace[2].getMethodName() + " called");
        }
    }
    
    public void debug(String message) {
        logger.debug(message);
    }

    public void debug(String pattern, Object ... obj) {
        logger.debug(pattern, obj);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void info(String pattern, Object ... obj) {
        logger.info(pattern, obj);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void warn(String pattern, Object ... obj) {
        logger.warn(pattern, obj);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void error(String message, Throwable e) {
        logger.error(message, e);
    }

    public void error(String pattern, Throwable e, Object ... obj) {
        logger.error(pattern, obj);
        logger.error("", e);
    }

    public void error(String pattern, Object ... obj) {
        logger.error(pattern, obj);
    }

    public void error(Throwable e) {
        logger.error(e);
    }

    public String stackTrace(Exception e, int j) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] st = e.getStackTrace();

        for(int i = 0; i < j; ++i) {
            sb.append(st[i]).append("\n");
        }

        return sb.toString();
    }

    public String stackTrace(Thread thread, int offset, int depth) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] st = thread.getStackTrace();
        
        for(int i = offset; i < offset + depth; ++i) {
            sb.append(st[i]).append("\n");
        }

        return sb.toString();
    }

    public void printStackTrace(int j) {
        logger.trace(stackTrace(Thread.currentThread(), CALLING_METHOD, j));
    }

    public String stackTrace(Thread thread, int j) {
        return stackTrace(thread, CALLING_METHOD, j);
    }

}