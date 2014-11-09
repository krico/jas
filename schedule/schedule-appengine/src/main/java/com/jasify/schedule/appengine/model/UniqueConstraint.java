package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.*;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.application.ApplicationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;

import java.util.Iterator;
import java.util.Objects;

/**
 * Class to implement unique constraints on a property.
 * Use it with care :-)
 * <p/>
 * Created by krico on 09/11/14.
 */
public class UniqueConstraint {
    private static final Logger log = LoggerFactory.getLogger(UniqueConstraint.class);
    private static final String COUNT_PROPERTY_NAME = "jasify.UniqueConstraint.count";

    private final DatastoreService datastore;
    private final ModelMeta<?> meta;
    private final String uniquePropertyName;
    private final String uniqueKind;

    UniqueConstraint(ModelMeta<?> meta, String uniquePropertyName) throws UniqueConstraintException {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
        this.meta = meta;
        this.uniquePropertyName = uniquePropertyName;
        this.uniqueKind = determineKind();
    }

    public static UniqueConstraint create(ModelMeta<?> meta, String uniquePropertyName) throws RuntimeException {
        try {
            return new UniqueConstraint(meta, uniquePropertyName);
        } catch (UniqueConstraintException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUniqueKind() {
        return uniqueKind;
    }

    private String getEntityKindPropertyName() {
        return "jasify.UniqueConstraint." + meta.getKind() + "#" + uniquePropertyName;
    }

    private String determineKind() throws UniqueConstraintException {
        String name = getEntityKindPropertyName();
        ApplicationData applicationData = ApplicationData.instance();
        String kind = applicationData.getProperty(name);
        if (kind != null) {
            log.info("Existing constraint model: {}, prop: {}, uniqueKind = [{}]", meta.getKind(), uniquePropertyName, kind);
            return kind;
        }
        ApplicationProperty indexProperty;
        Transaction txn = datastore.beginTransaction();
        Key indexKey = applicationData.createPropertyKey(getEntityKindPropertyName());
        indexProperty = Datastore.getOrNull(txn, ApplicationProperty.class, indexKey);

        assert indexProperty == null;

        indexProperty = new ApplicationProperty();
        indexProperty.setKey(indexKey);

        Key countKey = applicationData.createPropertyKey(COUNT_PROPERTY_NAME);

        ApplicationProperty countProperty = Datastore.getOrNull(txn, ApplicationProperty.class, countKey);
        if (countProperty == null) {
            countProperty = new ApplicationProperty();
            countProperty.setKey(countKey);
            countProperty.setValue(0L);
        }
        Long count = countProperty.getValue();
        countProperty.setValue(++count);

        indexProperty.setValue(String.format(Constants.UNIQUE_CONSTRAINT_PREFIX + "%03d", count));
        Datastore.put(txn, countProperty);
        Datastore.put(txn, applicationData.getApplication());
        UniqueConstraintException uce = null;
        try {
            createIndex(indexProperty.<String>getValue());

            Datastore.put(txn, indexProperty); //only put if createIndex works
        } catch (UniqueConstraintException e) {
            uce = e;
            log.warn("The existing data for {}.{} is non-unique. We failed to create index {}", meta.getKind(), uniquePropertyName, indexProperty.getValue());
        }

        txn.commit();

        if (uce != null) {
            log.warn("Will try to cleanup stale index {}", indexProperty.getValue());
            deleteIndex(indexProperty.<String>getValue());
            throw uce;
        }
        applicationData.reload();

        log.info("Allocated new index: {}", indexProperty);
        return indexProperty.getValue();
    }

    private void createIndex(String kind) throws UniqueConstraintException {
        log.info("Creating index {} for {}.{}", kind, meta.getKind(), uniquePropertyName);
        int count = 0;
        Iterator<Entity> it = Datastore.query(meta).asEntityIterator();
        while (it.hasNext()) {
            Entity next = it.next();
            String uniqueProperty = Objects.toString(next.getProperty(uniquePropertyName));
            Transaction tx = Datastore.beginTransaction();
            Entity e = new Entity(kind, uniqueProperty);
            Entity found = Datastore.getOrNull(tx, e.getKey());
            if (found != null) {
                tx.rollback();
                throw new UniqueConstraintException(meta, uniquePropertyName, uniqueProperty);
            }
            Datastore.put(tx, e);
            tx.commit();
            ++count;
        }
        log.info("Successfully created index with {} entries {} for {}.{}", count, kind, meta.getKind(), uniquePropertyName);
    }

    private void deleteIndex(String kind) {
        log.warn("Deleting index {} for {}.{}", kind, meta.getKind(), uniquePropertyName);
        int count = 0;
        Iterator<Key> it = Datastore.query(meta).asKeyIterator();
        while (it.hasNext()) {
            Datastore.delete(it.next());
            ++count;
        }
        log.info("Successfully deleted all({}) entries index {} for {}.{}", count, kind, meta.getKind(), uniquePropertyName);
    }

    public void reserve(String uniqueValue) throws UniqueConstraintException {
        Transaction tx = Datastore.beginTransaction();
        Key key = Datastore.createKey(uniqueKind, uniqueValue);
        Entity entity = Datastore.getOrNull(tx, key);
        if (entity != null) {
            tx.rollback();
            throw new UniqueConstraintException(meta, uniquePropertyName, uniqueValue);
        }
        Datastore.put(tx, new Entity(uniqueKind, uniqueValue));
        tx.commit();
    }

    public void release(String uniqueValue) {
        Transaction tx = Datastore.beginTransaction();
        Key key = Datastore.createKey(uniqueKind, uniqueValue);
        Entity entity = Datastore.getOrNull(tx, key);
        if (entity != null) {
            Datastore.delete(tx, key);
        }
        tx.commit();
    }
}
