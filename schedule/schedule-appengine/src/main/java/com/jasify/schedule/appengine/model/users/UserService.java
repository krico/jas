package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 08/11/14.
 */
public interface UserService {
    /**
     * @return a new user with a pre allocated key
     */
    User newUser();

    User create(User user, String password) throws UsernameExistsException;

    void save(User user) throws EntityNotFoundException, FieldValueException;

    User get(long id);

    User findByName(String name);

    boolean exists(String name);

    @Nonnull
    User login(String name, String password) throws LoginFailedException;
}
