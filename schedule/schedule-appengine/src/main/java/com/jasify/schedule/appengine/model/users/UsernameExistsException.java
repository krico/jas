package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.model.UniqueConstraintException;

/**
 * Created by krico on 09/11/14.
 */
public class UsernameExistsException extends UniqueConstraintException {
    public UsernameExistsException(String message) {
        super(message);
    }
}
