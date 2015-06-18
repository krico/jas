package com.jasify.schedule.appengine.model.consistency;

import com.jasify.schedule.appengine.model.ModelException;

/**
 * @author krico
 * @since 17/06/15.
 */
public class InconsistentModelStateException extends ModelException {
    public InconsistentModelStateException(String message) {
        super(message);
    }

    public InconsistentModelStateException(String message, Exception e) {
        super(message, e);
    }
}
