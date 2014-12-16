package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Query;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;

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

    User save(User user) throws EntityNotFoundException, FieldValueException;

    User setPassword(User login1, String newPassword) throws EntityNotFoundException;

    User get(long id);

    User findByName(String name);

    User findByEmail(String email);

    boolean exists(String name);

    @Nonnull
    User login(String name, String password) throws LoginFailedException;

    List<User> list(Query.SortDirection order, int offset, int limit);

    List<User> searchByName(Pattern pattern, Query.SortDirection order, int offset, int limit);

    List<User> searchByName(String startsWith, Query.SortDirection order, int offset, int limit);

    List<User> searchByEmail(Pattern pattern, Query.SortDirection order, int offset, int limit);

    List<User> searchByEmail(String startsWith, Query.SortDirection order, int offset, int limit);

    int getTotalUsers();
}
