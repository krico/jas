package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.application.ApplicationProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;
import org.slim3.datastore.StringAttributeMeta;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Class to implement unique constraints on a property.
 * Use it with care :-)
 * <p/>
 *
 * @author krico
 * @since 09/11/14.
 */
public class UniqueConstraint {
    private static final String KIND_CLASSIFIER_SEPARATOR = ".";
    private static final Logger log = LoggerFactory.getLogger(UniqueConstraint.class);
    private static final String COUNT_PROPERTY_NAME = "jasify.UniqueConstraint.count";
    private final ModelMeta<?> meta;
    private final String uniquePropertyName;
    private final String uniqueClassifierPropertyName;
    private final String uniqueKind;
    private final boolean ignoreNullValues;

    UniqueConstraint(ModelMeta<?> meta, String uniquePropertyName, String uniqueClassifierPropertyName, boolean ignoreNullValues, boolean createIfMissing) throws UniqueConstraintException {
        this.meta = meta;
        this.uniquePropertyName = uniquePropertyName;
        this.uniqueClassifierPropertyName = uniqueClassifierPropertyName;
        this.ignoreNullValues = ignoreNullValues;
        this.uniqueKind = determineKind(createIfMissing);
    }

    public static <T> UniqueConstraint create(ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty) throws RuntimeException {
        return create(meta, uniqueProperty, null, false);
    }

    public static <T> UniqueConstraint createAllowingNullValues(ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty) throws RuntimeException {
        return create(meta, uniqueProperty, null, true);
    }

    public static <T> UniqueConstraint create(ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty, StringAttributeMeta<T> uniqueClassifierProperty) throws RuntimeException {
        return create(meta, uniqueProperty, uniqueClassifierProperty, false);
    }

    public static <T> UniqueConstraint create(ModelMeta<T> meta, StringAttributeMeta<T> uniqueProperty, StringAttributeMeta<T> uniqueClassifierProperty, boolean allowNullValues) throws RuntimeException {
        try {
            return new UniqueConstraint(meta, uniqueProperty.getName(), uniqueClassifierProperty == null ? null : uniqueClassifierProperty.getName(), allowNullValues, true);
        } catch (UniqueConstraintException e) {
            throw Throwables.propagate(e);
        }
    }

    public String getUniqueKind() {
        return uniqueKind;
    }

    private String getEntityKindPropertyName() {
        return "jasify.UniqueConstraint." + meta.getKind() + "#" + uniquePropertyName + (uniqueClassifierPropertyName == null ? "" : "+" + uniqueClassifierPropertyName);
    }

    private String determineKind(boolean createIfMissing) throws UniqueConstraintException {
        String name = getEntityKindPropertyName();
        ApplicationData applicationData = ApplicationData.instance();
        String kind = applicationData.getProperty(name);
        if (kind != null) {
            log.debug("Existing constraint: {}, uniqueKind = [{}]", name, kind);
            return kind;
        }

        if (!createIfMissing) {
            throw new UniqueConstraintException("Unique doesn't exist constraint: " +name);
        }

        Preconditions.checkState(Datastore.getCurrentTransaction() == null,
                "UniqueConstraint should never be initialized inside a Transaction");

        ApplicationProperty indexProperty;
        Transaction txn = Datastore.beginTransaction();
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
            log.warn("The existing data for: {} is non-unique, failed to create: {}", getEntityKindPropertyName(), e.getMessage());
        }

        txn.commit();

        if (uce != null) {
            log.warn("Will try to cleanup stale index {}", indexProperty.getValue());
            deleteIndex(indexProperty.<String>getValue());
            throw uce;
        }
        applicationData.reload();

        log.info("Allocated new index: {} for: {}", indexProperty, getEntityKindPropertyName());
        return indexProperty.getValue();
    }

    private void createIndex(String kind) throws UniqueConstraintException {
        log.info("Creating index {} for {}", kind, getEntityKindPropertyName());
        int count = 0;
        int indexed = 0;
        Iterator<Entity> it = Datastore.query(meta).asEntityIterator();
        while (it.hasNext()) {
            ++count;
            Entity next = it.next();
            Object property = next.getProperty(uniquePropertyName);
            if (property == null && ignoreNullValues) {
                continue;
            }
            String uniqueProperty = Objects.toString(property);
            String uniqueClassifier;
            String suffix;
            if (uniqueClassifierPropertyName == null) {
                uniqueClassifier = null;
                suffix = "";
            } else {
                uniqueClassifier = Objects.toString(next.getProperty(uniqueClassifierPropertyName));
                suffix = KIND_CLASSIFIER_SEPARATOR + uniqueClassifier;
            }
            Transaction tx = Datastore.beginTransaction();
            Entity e = new Entity(kind + suffix, uniqueProperty);
            Entity found = Datastore.getOrNull(tx, e.getKey());
            if (found != null) {
                tx.rollback();
                throw new UniqueConstraintException(meta, uniquePropertyName, uniqueClassifierPropertyName, uniqueProperty, uniqueClassifier);
            }
            Datastore.put(tx, e);
            tx.commit();
            ++indexed;
        }
        log.info("Successfully created index with {}/{} entries {} for {}", indexed, count, kind, getEntityKindPropertyName());
    }

    private void deleteIndex(String kindPrefix) {
        log.warn("Deleting index {} for {}", kindPrefix, getEntityKindPropertyName());
        int count = 0;
        Set<String> kinds = ModelMetadataUtil.queryKindsThatStartWith(kindPrefix);
        for (String kind : kinds) {
            log.warn("Deleting index {} for {}", kind, getEntityKindPropertyName());
            Iterator<Key> it = Datastore.query(kind).asKeyIterator();
            while (it.hasNext()) {
                Key next = it.next();
                Datastore.delete(next);
                ++count;
            }
        }
        log.info("Successfully deleted all({}) entries index {} for {}", count, kindPrefix, getEntityKindPropertyName());
    }

    public void reserve(final String uniqueValue) throws UniqueConstraintException {
        TransactionOperator.execute(new TransactionOperation<Void, UniqueConstraintException>() {
            @Override
            public Void execute(Transaction tx) throws UniqueConstraintException {
                reserveInCurrentTransaction(uniqueValue);
                tx.commit();
                return null;
            }
        });
    }

    public void reserveInCurrentTransaction(String uniqueValue) throws UniqueConstraintException {
        Preconditions.checkArgument(uniqueClassifierPropertyName == null, "Classified UniqueConstraint MUST reserve(String, String)");

        if (StringUtils.isBlank(uniqueValue))
            throw new UniqueConstraintException("NULL uniqueValue not allowed");

        Key key = Datastore.createKey(uniqueKind, uniqueValue);
        Entity entity = Datastore.getOrNull(key);
        if (entity != null) {
            throw new UniqueConstraintException(meta, uniquePropertyName, uniqueValue);
        }
        Datastore.put(new Entity(uniqueKind, uniqueValue));
    }

    public void release(final String uniqueValue) {
        TransactionOperator.executeNoEx(new TransactionOperation<Void, Exception>() {
            @Override
            public Void execute(Transaction tx) throws Exception {
                releaseInCurrentTransaction(uniqueValue);
                tx.commit();
                return null;
            }
        });
    }

    public void releaseInCurrentTransaction(String uniqueValue) {
        Preconditions.checkArgument(uniqueClassifierPropertyName == null, "Classified UniqueConstraint MUST release(String, String)");
        Key key = Datastore.createKey(uniqueKind, uniqueValue);
        Datastore.delete(key);
    }

    public void reserve(final String uniqueValue, final String classifier) throws UniqueConstraintException {
        TransactionOperator.execute(new TransactionOperation<Void, UniqueConstraintException>() {
            @Override
            public Void execute(Transaction tx) throws UniqueConstraintException {
                reserveInCurrentTransaction(uniqueValue, classifier);
                tx.commit();
                return null;
            }
        });
    }

    public void reserveInCurrentTransaction(String uniqueValue, String classifier) throws UniqueConstraintException {
        Preconditions.checkArgument(uniqueClassifierPropertyName != null, "Non classified UniqueConstraint MUST reserve(String)");


        if (StringUtils.isBlank(uniqueValue))
            throw new UniqueConstraintException("NULL uniqueValue not allowed");

        if (StringUtils.isBlank(classifier))
            throw new UniqueConstraintException("NULL classifier not allowed");

        Key key = Datastore.createKey(uniqueKind + KIND_CLASSIFIER_SEPARATOR + classifier, uniqueValue);
        Entity entity = Datastore.getOrNull(key);
        if (entity != null) {
            throw new UniqueConstraintException(meta, uniquePropertyName, uniqueClassifierPropertyName, uniqueValue, classifier);
        }
        Datastore.put(new Entity(key));
    }

    public void release(final String uniqueValue, final String classifier) {
        TransactionOperator.executeNoEx(new TransactionOperation<Void, Exception>() {
            @Override
            public Void execute(Transaction tx) throws Exception {
                releaseInCurrentTransaction(uniqueValue, classifier);
                tx.commit();
                return null;
            }
        });
    }

    public void releaseInCurrentTransaction(String uniqueValue, String classifier) {
        Preconditions.checkArgument(uniqueClassifierPropertyName != null, "Non classified UniqueConstraint MUST release(String)");

        Key key = Datastore.createKey(uniqueKind + KIND_CLASSIFIER_SEPARATOR + classifier, uniqueValue);
        Datastore.delete(key);
    }
}
