package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
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

    User create(User user, String password) throws EmailExistsException, UsernameExistsException;

    User create(User user, UserLogin login) throws EmailExistsException, UsernameExistsException, UserLoginExistsException;

    User save(User user) throws EntityNotFoundException, FieldValueException;

    User setPassword(User user, String newPassword) throws EntityNotFoundException;

    UserLogin addLogin(User user, UserLogin login) throws EntityNotFoundException, UserLoginExistsException;

    UserLogin getLogin(Key id);

    void removeLogin(Key id) throws EntityNotFoundException;

    User get(long id);

    // TODO: Perhaps this should be renamed for findUser to indicate that it can return null?
    User get(Key id);

    @Nonnull
    User getUser(Key id) throws EntityNotFoundException;

    User findByLogin(String provider, String userId);

    User findByName(String name);

    User findByEmail(String email);

    PasswordRecovery registerPasswordRecovery(String email) throws EntityNotFoundException;

    void recoverPassword(String recoveryCode, String newPassword) throws EntityNotFoundException;

    boolean usernameExists(String username);

    boolean emailExists(String email);

    @Nonnull
    User login(String name, String password) throws LoginFailedException;

    List<User> list(Query.SortDirection order, int offset, int limit);

    List<User> searchByName(Pattern pattern, Query.SortDirection order, int offset, int limit);

    List<User> searchByName(String startsWith, Query.SortDirection order, int offset, int limit);

    List<User> searchByEmail(Pattern pattern, Query.SortDirection order, int offset, int limit);

    List<User> searchByEmail(String startsWith, Query.SortDirection order, int offset, int limit);

    List<UserLogin> getUserLogins(User user);

    List<UserLogin> getUserLogins(long userId);

    List<UserLogin> getUserLogins(Key userId);

    int getTotalUsers();
}
