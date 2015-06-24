package com.jasify.schedule.appengine.http;

import com.google.common.base.Stopwatch;
import com.jasify.schedule.appengine.model.SchemaMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Called before any of our servlets.  Basically listens to our application...
 *
 * @author krico
 * @since 22/11/14.
 */
public class JasifyServletContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(JasifyServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.debug("Context initialized...");

        Stopwatch watch = Stopwatch.createStarted();

        SchemaMigration.instance().executePendingMigrations();
        SchemaMigration.instance().notifyOfNewVersion();

        log.info("Context initialized in [%s]", watch.stop());
    }

    /**
     * Docs say this method is not called by app engine.
     *
     * @param sce the event
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.warn("AppEngine is not supposed to call this method...");
    }
}
