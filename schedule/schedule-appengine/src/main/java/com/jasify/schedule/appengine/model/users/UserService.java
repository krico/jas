package com.jasify.schedule.appengine.model.users;

/**
 * Created by krico on 08/11/14.
 */
public interface UserService {
    /**
     * @return a new user with a pre allocated key
     */
    User newUser();

    void create(User user, String password) throws UsernameExistsException;

    User getUser(long id);
}
