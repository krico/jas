package com.jasify.schedule.appengine.model;

import org.slim3.datastore.ModelMeta;

/**
 * Created by krico on 09/11/14.
 */
public class UniqueConstraintException extends ModelException {
    public UniqueConstraintException(String message) {
        super(message);
    }

    public UniqueConstraintException(ModelMeta<?> meta, String propertyName, String violatingValue) {
        super("Entity:" + meta.getKind() + ", property=" + propertyName + ", duplicate=" + violatingValue);
    }
}
