package com.jasify.schedule.appengine.model.users;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.mail.MailParser;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.meta.users.PasswordRecoveryMeta;
import com.jasify.schedule.appengine.meta.users.UserLoginMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.util.DigestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.*;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 22/11/14.
 */
final class DefaultUserService implements UserService {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserService.class);
    private final UserMeta userMeta;
    private final UserLoginMeta userLoginMeta;
    private final PasswordRecoveryMeta passwordRecoveryMeta;
    private final UniqueConstraint uniqueName;
    private final UniqueConstraint uniqueEmail;
    private final UniqueConstraint uniqueLogin;
    private final Random random;

    private DefaultUserService() {
        random = new SecureRandom();
        userMeta = UserMeta.get();
        userLoginMeta = UserLoginMeta.get();
        passwordRecoveryMeta = PasswordRecoveryMeta.get();
        uniqueName = UniqueConstraint.create(userMeta, userMeta.name);
        uniqueEmail = UniqueConstraint.create(userMeta, userMeta.email, true);
        uniqueLogin = UniqueConstraint.create(userLoginMeta, userLoginMeta.userId, userLoginMeta.provider);
    }

    static UserService instance() {
        return Singleton.INSTANCE;
    }

    @Override
    public User newUser() {
        User newUser = new User();
        newUser.setId(Datastore.allocateId(User.class));
        return newUser;
    }

    @Override
    public User create(final User user, String password) throws EmailExistsException, UsernameExistsException {
        user.setName(StringUtils.lowerCase(Preconditions.checkNotNull(StringUtils.trimToNull(user.getName()), "User.Name cannot be null")));
        user.setEmail(StringUtils.lowerCase(StringUtils.trimToNull(user.getEmail())));
        user.setPassword(new ShortBlob(DigestUtil.encrypt(password)));

        try {
            uniqueName.reserve(user.getName());
        } catch (UniqueConstraintException e) {
            throw new UsernameExistsException(e.getMessage());
        }

        if (user.getEmail() != null) {
            try {
                uniqueEmail.reserve(user.getEmail());
            } catch (UniqueConstraintException e) {
                uniqueName.release(user.getName());
                throw new EmailExistsException(e.getMessage());
            }
        }

        if (StringUtils.equalsIgnoreCase("krico", user.getName())) {
            user.setAdmin(true);//Admin for me...
        }

        TransactionOperator.executeNoEx(new ModelOperation<Object>() {
            @Override
            public Void execute(com.google.appengine.api.datastore.Transaction tx) {
                user.setId(Datastore.allocateId(userMeta));
                Datastore.put(tx, user);
                tx.commit();
                return null;
            }
        });

        notify(user);

        return user;
    }

    @Override
    public User create(final User user, final UserLogin login) throws EmailExistsException, UsernameExistsException, UserLoginExistsException {
        Preconditions.checkNotNull(login, "login cannot be NULL");
        Preconditions.checkNotNull(login.getProvider(), "login.Provider cannot be NULL");
        Preconditions.checkNotNull(login.getUserId(), "login.UserId cannot be NULL");
        user.setName(StringUtils.lowerCase(Preconditions.checkNotNull(StringUtils.trimToNull(user.getName()), "User.Name cannot be null")));
        user.setEmail(StringUtils.lowerCase(StringUtils.trimToNull(user.getEmail())));

        try {
            uniqueLogin.reserve(login.getUserId(), login.getProvider());
        } catch (UniqueConstraintException e) {
            throw new UserLoginExistsException(e.getMessage());
        }

        try {
            uniqueName.reserve(user.getName());
        } catch (UniqueConstraintException e) {
            //release the reserved login
            uniqueLogin.release(login.getUserId(), login.getProvider());
            throw new UsernameExistsException(e.getMessage());
        }

        if (user.getEmail() != null) {
            try {
                uniqueEmail.reserve(user.getEmail());
            } catch (UniqueConstraintException e) {
                uniqueLogin.release(login.getUserId(), login.getProvider());
                uniqueName.release(user.getName());
                throw new EmailExistsException(e.getMessage());
            }
        }

        TransactionOperator.executeNoEx(new ModelOperation<Object>() {
            @Override
            public Void execute(com.google.appengine.api.datastore.Transaction tx) {
                user.setId(Datastore.allocateId(userMeta));
                login.setId(Datastore.allocateId(user.getId(), userLoginMeta));
                login.getUserRef().setModel(user);
                Datastore.put(tx, user, login);
                tx.commit();
                return null;
            }
        });

        notify(user);

        return user;
    }

    private void notify(User user) {
        String subject = String.format("[Jasify] SignUp [%s]", user.getName());
        try {
            MailParser mailParser = MailParser.createJasifyUserSignUpEmail(user);
            MailServiceFactory.getMailService().sendToApplicationOwners(subject, mailParser.getHtml(), mailParser.getText());
        } catch (Exception e) {
            log.warn("Failed to notify jasify", e);
        }
    }

    @Override
    public User save(final User user) throws EntityNotFoundException, FieldValueException {
        //TODO: permissions?
        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(com.google.appengine.api.datastore.Transaction tx) throws EntityNotFoundException, FieldValueException {
                    User db = Datastore.getOrNull(tx, userMeta, user.getId());
                    if (db == null) {
                        throw new EntityNotFoundException();
                    }

                    //TODO: why did I decide to manually copy these!?  Horrible!

                    if (!StringUtils.equals(db.getName(), user.getName())) {
                        throw new FieldValueException("Cannot change 'name' with save();");
                    }

                    String email = StringUtils.trimToNull(StringUtils.lowerCase(user.getEmail()));
                    if (!StringUtils.equals(db.getEmail(), email)) {
                        if (db.getEmail() != null) uniqueEmail.releaseInCurrentTransaction(db.getEmail());
                        try {
                            if (email != null) uniqueEmail.reserveInCurrentTransaction(email);
                        } catch (UniqueConstraintException e) {
                            throw new FieldValueException("Duplicate e-mail: " + email);
                        }
                    }

                    db.setAbout(user.getAbout());
                    db.setEmail(email);
                    db.setLocale(user.getLocale());
                    db.setAdmin(user.isAdmin());
                    db.setRealName(user.getRealName());

                    UserDetail model = db.getDetailRef().getModel();
                    if (model == null) {
                        Datastore.put(tx, db);
                    } else {
                        Datastore.put(tx, db, model);
                    }
                    tx.commit();
                    return null;
                }
            });
        } catch (EntityNotFoundException | FieldValueException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
        return user;
    }

    @Override
    public User setPassword(final User user, final String newPassword) throws EntityNotFoundException {
        try {
            return TransactionOperator.execute(new ModelOperation<User>() {
                @Override
                public User execute(com.google.appengine.api.datastore.Transaction tx) throws EntityNotFoundException {
                    User db = Datastore.getOrNull(tx, userMeta, user.getId());
                    if (db == null) {
                        throw new EntityNotFoundException();
                    }
                    db.setPassword(new ShortBlob(DigestUtil.encrypt(newPassword)));
                    Datastore.put(tx, db);
                    tx.commit();
                    return db;
                }
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public UserLogin addLogin(final User user, final UserLogin login) throws UserLoginExistsException, EntityNotFoundException {
        try {
            return TransactionOperator.execute(new ModelOperation<UserLogin>() {
                @Override
                public UserLogin execute(com.google.appengine.api.datastore.Transaction tx) throws UserLoginExistsException, EntityNotFoundException {
                    try {
                        uniqueLogin.reserveInCurrentTransaction(login.getUserId(), login.getProvider());
                    } catch (UniqueConstraintException e) {
                        throw new UserLoginExistsException(e.getMessage());
                    }

                    User db = Datastore.getOrNull(tx, userMeta, user.getId());
                    if (db == null) {
                        throw new EntityNotFoundException();
                    }
                    login.setId(Datastore.allocateId(db.getId(), UserLogin.class));
                    login.getUserRef().setModel(db);

                    Datastore.put(tx, db, login);
                    tx.commit();
                    return login;
                }
            });
        } catch (UserLoginExistsException | EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void removeLogin(final Key id) throws EntityNotFoundException {
        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(com.google.appengine.api.datastore.Transaction tx) throws EntityNotFoundException {
                    UserLogin dbLogin = Datastore.getOrNull(tx, userLoginMeta, id);
                    if (dbLogin == null) {
                        throw new EntityNotFoundException("UserLogin");
                    }
                    Datastore.delete(tx, dbLogin.getId());
                    uniqueLogin.releaseInCurrentTransaction(dbLogin.getUserId(), dbLogin.getProvider());
                    tx.commit();
                    return null;
                }
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public User get(long id) {
        return get(Datastore.createKey(User.class, id));
    }

    @Override
    public User get(Key id) {
        return Datastore.getOrNull(User.class, id);
    }

    @Nonnull
    @Override
    public User getUser(Key id) throws EntityNotFoundException {
        try {
            return Datastore.get(userMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("User id=" + id);
        }
    }

    @Override
    public UserLogin getLogin(Key id) {
        return Datastore.getOrNull(userLoginMeta, id);
    }

    @Override
    public List<UserLogin> getUserLogins(long userId) {
        return getUserLogins(Datastore.createKey(User.class, userId));
    }

    @Override
    public List<UserLogin> getUserLogins(Key userId) {
        return Datastore.query(userLoginMeta, Preconditions.checkNotNull(userId)).asList();
    }

    @Override
    public List<UserLogin> getUserLogins(User user) {
        return getUserLogins(Preconditions.checkNotNull(user.getId()).getId());
    }

    @Override
    public User findByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return Datastore.query(userMeta).filter(userMeta.name.equal(StringUtils.lowerCase(name))).asSingle();
    }

    @Override
    public User findByLogin(String provider, String userId) {
        if (StringUtils.isAnyBlank(provider, userId)) {
            return null;
        }
        UserLogin userLogin = Datastore.query(userLoginMeta).filter(userLoginMeta.provider.equal(provider), userLoginMeta.userId.equal(userId)).asSingle();
        if (userLogin == null) {
            return null;
        }
        return userLogin.getUserRef().getModel();
    }

    @Override
    public User findByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }

        return Datastore.query(userMeta).filter(userMeta.email.equal(StringUtils.lowerCase(email))).asSingle();
    }

    @Override
    public PasswordRecovery registerPasswordRecovery(final String email) throws EntityNotFoundException {
        final User user = findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException(email);
        }

        return TransactionOperator.executeNoEx(new ModelOperation<PasswordRecovery>() {
            @Override
            public PasswordRecovery execute(com.google.appengine.api.datastore.Transaction tx) {
                Key key;
                do {
                    key = Datastore.createKey(passwordRecoveryMeta, new BigInteger(32, random).toString(32));
                    if (Datastore.getOrNull(tx, passwordRecoveryMeta, key) != null) {
                        //You know, random shit can repeat itself...  In that case, we generate a new key
                        key = null;
                    }
                } while (key == null);

                PasswordRecovery recovery = new PasswordRecovery();
                recovery.setCode(key);
                recovery.getUserRef().setModel(user);
                Datastore.put(tx, recovery);
                tx.commit();
                log.info("Recovery for email={}, id={} registered", email, user.getId());
                return recovery;
            }
        });
    }

    @Override
    public void recoverPassword(String passwordRecoveryCode, String newPassword) throws EntityNotFoundException {
        Preconditions.checkNotNull(passwordRecoveryCode);
        Preconditions.checkNotNull(newPassword);
        Key recoveryKey = Datastore.createKey(passwordRecoveryMeta, passwordRecoveryCode);
        PasswordRecovery recovery = Datastore.getOrNull(passwordRecoveryMeta, recoveryKey);
        if (recovery == null) {
            throw new EntityNotFoundException("PasswordRecovery");
        }
        User user = recovery.getUserRef().getModel();
        user.setPassword(new ShortBlob(DigestUtil.encrypt(newPassword)));
        Datastore.put(user);
        Datastore.delete(recoveryKey);
        log.info("Password recovered for user [{}] email [{}]", user.getName(), user.getEmail());
    }

    @Override
    public boolean usernameExists(String name) {
        return !Datastore.query(userMeta).filter(userMeta.name.equal(StringUtils.lowerCase(name))).asKeyList().isEmpty();
    }

    @Override
    public boolean emailExists(String email) {
        return !Datastore.query(userMeta).filter(userMeta.email.equal(StringUtils.lowerCase(email))).asKeyList().isEmpty();
    }

    @Nonnull
    @Override
    public User login(String name, String password) throws LoginFailedException {
        Preconditions.checkNotNull(password, "Null password not allowed on login");
        //TODO: add login/logout history under user entity
        User user = findByName(name);
        if (user == null) {
            log.debug("user={} not found.", name);
            throw new LoginFailedException();
        }
        if (user.getPassword() != null && user.getPassword().getBytes().length > 0 && DigestUtil.verify(user.getPassword().getBytes(), password)) {
            log.info("user={} logged in.", name);
            return user;
        }
        log.info("user={} login failed!", name);
        throw new LoginFailedException();
    }

    @Override
    public List<User> list(Query.SortDirection order, int offset, int limit) {
        return search(offset, limit, order == Query.SortDirection.DESCENDING ? userMeta.id.desc : userMeta.id.asc);
    }

    @Override
    public List<User> searchByName(final Pattern pattern, Query.SortDirection order, int offset, int limit) {
        if (pattern == null) {
            return search(offset, limit, order == Query.SortDirection.DESCENDING ? userMeta.name.desc : userMeta.name.asc);
        }

        ModelQuery<User> query = Datastore.query(userMeta);

        if (order == Query.SortDirection.DESCENDING) {
            query.sort(userMeta.name.desc);
        } else {
            query.sort(userMeta.name.asc);
        }

        /* TODO: in memory search might be slow when we have millions of users :-) */
        query.filterInMemory(new InMemoryFilterCriterion() {
            @Override
            public boolean accept(Object model) {
                return pattern.matcher(((User) model).getName()).find();
            }
        });
        List<User> users = query.asList();
        if (offset > 0 || limit > 0) {
            if (offset < users.size()) {
                if (limit <= 0) limit = users.size();
                return new ArrayList<>(users.subList(offset, Math.min(offset + limit, users.size())));
            } else {
                return Collections.emptyList();
            }
        }
        return users;
    }

    @Override
    public List<User> searchByName(String startsWith, Query.SortDirection order, int offset, int limit) {
        ModelQuery<User> query = Datastore.query(userMeta);

        if (StringUtils.isNotBlank(startsWith)) {
            query.filter(userMeta.name.startsWith(startsWith));
        }

        if (offset > 0) query.offset(offset);
        if (limit > 0) query.limit(limit);

        if (order == Query.SortDirection.DESCENDING) {
            query.sort(userMeta.name.desc);
        } else {
            query.sort(userMeta.name.asc);
        }

        return query.asList();
    }

    @Override
    public List<User> searchByEmail(final Pattern pattern, Query.SortDirection order, int offset, int limit) {
        if (pattern == null) {
            return search(offset, limit, order == Query.SortDirection.DESCENDING ? userMeta.email.desc : userMeta.email.asc);
        }

        ModelQuery<User> query = Datastore.query(userMeta);

        if (order == Query.SortDirection.DESCENDING) {
            query.sort(userMeta.email.desc);
        } else {
            query.sort(userMeta.email.asc);
        }

        /* TODO: in memory search might be slow when we have millions of users :-) */
        query.filterInMemory(new InMemoryFilterCriterion() {
            @Override
            public boolean accept(Object model) {
                return pattern.matcher(StringUtils.trimToEmpty(((User) model).getEmail())).find();
            }
        });
        List<User> users = query.asList();
        if (offset > 0 || limit > 0) {
            if (offset < users.size()) {
                if (limit <= 0) limit = users.size();
                return new ArrayList<>(users.subList(offset, Math.min(offset + limit, users.size())));
            } else {
                return Collections.emptyList();
            }
        }
        return users;
    }

    @Override
    public List<User> searchByEmail(String startsWith, Query.SortDirection order, int offset, int limit) {
        ModelQuery<User> query = Datastore.query(userMeta);

        if (StringUtils.isNotBlank(startsWith)) {
            query.filter(userMeta.email.startsWith(startsWith));
        }

        if (offset > 0) query.offset(offset);
        if (limit > 0) query.limit(limit);

        if (order == Query.SortDirection.DESCENDING) {
            query.sort(userMeta.email.desc);
        } else {
            query.sort(userMeta.email.asc);
        }

        return query.asList();
    }

    @Override
    public int getTotalUsers() {
        return Datastore.query(userMeta).asKeyList().size();
    }

    private List<User> search(int offset, int limit, SortCriterion criteria) {
        ModelQuery<User> query = Datastore.query(userMeta);
        if (offset > 0) query.offset(offset);
        if (limit > 0) query.limit(limit);
        return query.sort(criteria).asList();
    }

    private static final class Singleton {
        private static final UserService INSTANCE = new DefaultUserService();
    }
}
