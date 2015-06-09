package com.jasify.schedule.appengine;


import io.github.benas.jpopulator.impl.PopulatorImpl;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

        Logger populator = Logger.getLogger("io.github.benas.jpopulator");
        populator.setLevel(Level.INFO);
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        populator.addHandler(consoleHandler);
    }
}
