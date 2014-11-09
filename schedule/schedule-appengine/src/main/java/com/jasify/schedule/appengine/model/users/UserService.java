package com.jasify.schedule.appengine.model.users;

import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;

/**
 * Created by krico on 08/11/14.
 */
public interface UserService {
    /**
     * @return a new user with a pre allocated key
     */
    User newUser();

    void create(User user, String password) throws UsernameExistsException;

    void save(User user) throws EntityNotFoundException, FieldValueException;

    User get(long id);

    User findByName(String name);
}
