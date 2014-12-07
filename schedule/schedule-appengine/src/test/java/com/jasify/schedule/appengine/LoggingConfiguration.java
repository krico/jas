package com.jasify.schedule.appengine;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configure the logging
 *
 * @author krico
 * @since 07/12/14.
 */
public class LoggingConfiguration {
    public LoggingConfiguration() {
        Logger jasifyRoot = Logger.getLogger("com.jasify.schedule");
        jasifyRoot.setLevel(Level.WARNING);
    }
}
