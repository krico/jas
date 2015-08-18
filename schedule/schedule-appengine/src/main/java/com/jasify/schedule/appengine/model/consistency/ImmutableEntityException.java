package com.jasify.schedule.appengine.model.consistency;

import com.jasify.schedule.appengine.model.ModelException;

/**
 * @author krico
 * @since 09/08/15.
 */
public class ImmutableEntityException extends ModelException {
    public ImmutableEntityException(String message) {
        super(message);
    }
}
