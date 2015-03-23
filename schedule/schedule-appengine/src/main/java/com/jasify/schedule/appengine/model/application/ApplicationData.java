package com.jasify.schedule.appengine.model.application;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.TreeMap;

/**
 * @author krico
 * @since 09/11/14.
 */
public final class ApplicationData {
    private static final Logger log = LoggerFactory.getLogger(ApplicationData.class);

    private final Application application;

    private ApplicationData() {
        application = loadApplication();
    }

    public static ApplicationData instance() {
        return Singleton.INSTANCE; //initialize lazily
    }

    public <T> void setProperty(String keyName, T value) {
        log.debug("Setting AppProp[{}]", keyName);
        Transaction tx = Datastore.beginTransaction();
        Key key = createPropertyKey(keyName);
        ApplicationProperty property = Datastore.getOrNull(tx, ApplicationProperty.class, key);
        if (property == null) {
            log.debug("New AppProp[{}] = [{}]", keyName, value);
            property = new ApplicationProperty();
            property.setKey(key);
            property.setValue(value);
        } else {
            log.debug("Upd AppProp[{}] = [{}] -> [{}]", keyName, property.getValue(), value);
            property.setValue(value);
        }
        Datastore.put(tx, property, application);
        tx.commit();
        log.debug("Wrote: {}", property);
        //Poor man's concurrency
        TreeMap<String, Object> replace = new TreeMap<>(application.getProperties());
        replace.put(keyName, property.getValue());
        application.setProperties(replace);
    }

    public Key createPropertyKey(String keyName) {
        return application.createPropertyKey(keyName);
    }

    public Application getApplication() {
        return application;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) application.getProperties().get(key);
    }

    Application loadApplication() {
        log.debug("Loading application");
        Transaction tx = Datastore.beginTransaction();
        Key name = Datastore.createKey(Application.class, Constants.APPLICATION_NAME);
        Application application;
        try {
            application = Datastore.getOrNull(tx, Application.class, name);
            if (application == null) {
                application = new Application();
                application.setName(name);
                Datastore.put(tx, application);
                log.info("Created: {}", application);
            } else {
                log.info("Loaded: {}" + application);
            }
            application.reloadProperties(tx);
            return application;
        } finally {
            tx.commit();
        }
    }

    public ApplicationData reload() {
        log.info("Reloading application");
        Transaction tx = Datastore.beginTransaction();
        try {
            application.reloadProperties(tx);
        } finally {
            tx.commit();
        }
        log.info("Reloaded: {}", application);
        return this;
    }

    private static class Singleton {
        private static final ApplicationData INSTANCE = new ApplicationData();
    }
}
