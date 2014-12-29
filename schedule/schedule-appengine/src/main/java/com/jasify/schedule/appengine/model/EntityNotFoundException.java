package com.jasify.schedule.appengine.model;

/**
 * @author krico
 * @since 09/11/14.
 */
public class EntityNotFoundException extends ModelException {
    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
