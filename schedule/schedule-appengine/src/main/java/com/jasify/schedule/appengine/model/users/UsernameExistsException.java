package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.model.UniqueConstraintException;

/**
 * @author krico
 * @since 09/11/14.
 */
public class UsernameExistsException extends UniqueConstraintException {
    public UsernameExistsException(String message) {
        super(message);
    }
}
