package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.model.UniqueConstraintException;

/**
 * @author krico
 * @since 23/12/14.
 */
public class UserLoginExistsException extends UniqueConstraintException {
    public UserLoginExistsException(String message) {
        super(message);
    }
}