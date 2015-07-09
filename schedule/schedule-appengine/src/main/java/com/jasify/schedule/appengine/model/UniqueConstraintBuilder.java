package com.jasify.schedule.appengine.model;

import org.slim3.datastore.ModelMeta;

public class UniqueConstraintBuilder {
    private ModelMeta<?> meta;
    private String uniquePropertyName;
    private String uniqueClassifierPropertyName = null;
    private boolean ignoreNullValues = false;
    private boolean createIfMissing = true;

    public UniqueConstraintBuilder forMeta(ModelMeta<?> meta) {
        this.meta = meta;
        return this;
    }

    public UniqueConstraintBuilder withUniquePropertyName(String uniquePropertyName) {
        this.uniquePropertyName = uniquePropertyName;
        return this;
    }

    public UniqueConstraintBuilder withUniqueClassifierPropertyName(String uniqueClassifierPropertyName) {
        this.uniqueClassifierPropertyName = uniqueClassifierPropertyName;
        return this;
    }

    public UniqueConstraintBuilder ignoreNullValues(boolean ignoreNullValues) {
        this.ignoreNullValues = ignoreNullValues;
        return this;
    }

    public UniqueConstraintBuilder createIfMissing(boolean createIfMissing) {
        this.createIfMissing = createIfMissing;
        return this;
    }

    public UniqueConstraint createUniqueConstraint() throws UniqueConstraintException {
        return new UniqueConstraint(meta, uniquePropertyName, uniqueClassifierPropertyName, ignoreNullValues, createIfMissing);
    }
}