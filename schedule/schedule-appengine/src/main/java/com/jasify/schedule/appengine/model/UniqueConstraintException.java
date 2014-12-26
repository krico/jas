package com.jasify.schedule.appengine.model;

import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.ModelMeta;

/**
 * @author krico
 * @since 09/11/14.
 */
public class UniqueConstraintException extends ModelException {
    public UniqueConstraintException(String message) {
        super(message);
    }

    public UniqueConstraintException(ModelMeta<?> meta, String propertyName, String violatingValue) {
        this(meta, propertyName, null, violatingValue, null);
    }

    public UniqueConstraintException(ModelMeta<?> meta, String propertyName, String classifierName, String violatingValue, String violatingClassifierValue) {
        super(buildMessage(meta, propertyName, classifierName, violatingValue, violatingClassifierValue));
    }

    private static String buildMessage(ModelMeta<?> meta, String propertyName, String classifierName, String violatingValue, String violatingClassifierValue) {
        if (StringUtils.isBlank(classifierName)) {
            return "Entity:" + meta.getKind() + ", property=" + propertyName + ", duplicate=" + violatingValue;
        } else {
            return "Entity:" + meta.getKind() + ", property=" + propertyName + ", classifier=" + classifierName + ", duplicate=" + violatingValue + ", duplicateClassifier=" + violatingClassifierValue;
        }
    }
}
