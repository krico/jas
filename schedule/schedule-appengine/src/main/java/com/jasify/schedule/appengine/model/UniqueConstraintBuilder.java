package com.jasify.schedule.appengine.model;

import com.google.common.base.Throwables;
import org.slim3.datastore.ModelMeta;
import org.slim3.datastore.StringAttributeMeta;

public class UniqueConstraintBuilder {
    private ModelMeta<?> meta;
    private String uniquePropertyName;
    private String uniqueClassifierPropertyName = null;
    private boolean ignoreNullValues = false;
    private boolean createIfMissing = false;

    UniqueConstraintBuilder() {
    }

    public UniqueConstraintBuilder forMeta(ModelMeta<?> meta) {
        this.meta = meta;
        return this;
    }

    public UniqueConstraintBuilder withUniquePropertyName(StringAttributeMeta<?> uniqueProperty) {
        return withUniquePropertyName(uniqueProperty.getName());
    }

    public UniqueConstraintBuilder withUniquePropertyName(String uniquePropertyName) {
        this.uniquePropertyName = uniquePropertyName;
        return this;
    }

    public UniqueConstraintBuilder withUniqueClassifierPropertyName(StringAttributeMeta<?> uniqueClassifierProperty) {
        return withUniqueClassifierPropertyName(uniqueClassifierProperty.getName());
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

    public UniqueConstraint create() throws UniqueConstraintException {
        return new UniqueConstraint(meta, uniquePropertyName, uniqueClassifierPropertyName, ignoreNullValues, createIfMissing);
    }

    public UniqueConstraint createNoEx() {
        try {
            return create();
        } catch (UniqueConstraintException e) {
            throw Throwables.propagate(e);
        }
    }
}