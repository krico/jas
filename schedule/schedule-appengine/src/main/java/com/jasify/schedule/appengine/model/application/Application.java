package com.jasify.schedule.appengine.model.application;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.meta.application.ApplicationPropertyMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.*;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Mainly used as an ancestor to all application properties
 *
 * @author krico
 * @since 09/11/14.
 */
@Model(kind = "App", schemaVersionName = Constants.SCHEMA_VERSION_NAME, schemaVersion = 0)
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Attribute(primaryKey = true)
    private Key name;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    @Attribute(persistent = false)
    private TreeMap<String, Object> properties = new TreeMap<>();

    public Key getName() {
        return name;
    }

    public void setName(Key name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    Key createPropertyKey(String keyName) {
        return Datastore.createKey(name, ApplicationProperty.class, keyName);
    }

    TreeMap<String, Object> getProperties() {
        return properties;
    }

    void setProperties(TreeMap<String, Object> properties) {
        this.properties = properties;
    }

    List<ApplicationProperty> listProperties(Transaction tx) {
        return Datastore.query(tx, ApplicationPropertyMeta.get(), getName()).asList();
    }

    void reloadProperties(Transaction tx) {
        TreeMap<String, Object> properties = new TreeMap<>();
        for (ApplicationProperty p : listProperties(tx)) {
            log.debug("Loaded: {}", p);
            String key = p.getKey().getName();
            Object value = p.getValue();
            properties.put(key, value);
        }
        setProperties(properties);
    }

    @Override
    public String toString() {
        return "Application{" +
                "name=" + name +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
