package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.meta.users.UserLoginMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.util.DigestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.InMemoryFilterCriterion;
import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.SortCriterion;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 22/11/14.
 */
final class DefaultUserService implements UserService {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserService.class);
    private final UserMeta userMeta;
    private final UserLoginMeta userLoginMeta;
    private final UniqueConstraint uniqueName;
    private final UniqueConstraint uniqueEmail;
    private final UniqueConstraint uniqueLogin;

    private DefaultUserService() {
        userMeta = UserMeta.get();
        userLoginMeta = UserLoginMeta.get();
        uniqueName = UniqueConstraint.create(userMeta, userMeta.name);
        uniqueEmail = UniqueConstraint.create(userMeta, userMeta.email);
        uniqueLogin = UniqueConstraint.create(userLoginMeta, userLoginMeta.provider, userLoginMeta.userId);
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
    public User create(User user, String password) throws UsernameExistsException, EmailExistsException {
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

        user.setId(Datastore.allocateId(userMeta));

        Transaction tx = Datastore.beginTransaction();
        Datastore.put(tx, user);
        tx.commit();

        notify(user);

        return user;
    }

    @Override
    public User create(User user, UserLogin login) throws UsernameExistsException, UserLoginExistsException, EmailExistsException {
        login = Preconditions.checkNotNull(login, "login cannot be NULL");
        Preconditions.checkNotNull(login.getProvider(), "login.Provider cannot be NULL");
        Preconditions.checkNotNull(login.getUserId(), "login.UserId cannot be NULL");
        user.setName(StringUtils.lowerCase(Preconditions.checkNotNull(StringUtils.trimToNull(user.getName()), "User.Name cannot be null")));
        user.setEmail(StringUtils.lowerCase(StringUtils.trimToNull(user.getEmail())));

        try {
            uniqueLogin.reserve(login.getProvider(), login.getUserId());
        } catch (UniqueConstraintException e) {
            throw new UserLoginExistsException(e.getMessage());
        }

        try {
            uniqueName.reserve(user.getName());
        } catch (UniqueConstraintException e) {
            //release the reserved login
            uniqueLogin.release(login.getProvider(), login.getUserId());
            throw new UsernameExistsException(e.getMessage());
        }

        if (user.getEmail() != null) {
            try {
                uniqueEmail.reserve(user.getEmail());
            } catch (UniqueConstraintException e) {
                uniqueLogin.release(login.getProvider(), login.getUserId());
                uniqueName.release(user.getName());
                throw new EmailExistsException(e.getMessage());
            }
        }


        Transaction tx = Datastore.beginTransaction();
        user.setId(Datastore.allocateId(userMeta));
        login.setId(Datastore.allocateId(user.getId(), userLoginMeta));
        login.getUserRef().setModel(user);
        Datastore.put(tx, user, login);
        tx.commit();

        notify(user);

        return user;
    }

    private void notify(User user) {
        try {
            //TODO: I guess this should come from some kind of template
            String subject = String.format("[Jasify] SignUp [%s]", user.getName());
            StringBuilder body = new StringBuilder()
                    .append("<h1>User: ").append(user.getName()).append("</h1>")
                    .append("<p>")
                    .append("Id: ").append(user.getId()).append("<br/>")
                    .append("Created: ").append(user.getCreated()).append("<br/>")
                    .append("</p>")
                    .append("<p>Regards,<br>Jasify</p>");

            MailServiceFactory.getMailService().sendToApplicationOwners(subject, body.toString());
        } catch (Exception e) {
            log.warn("Failed to notify", e);
        }
    }

    @Override
    public User save(User user) throws EntityNotFoundException, FieldValueException {
        //TODO: permissions?

        Transaction tx = Datastore.beginTransaction();
        User db = Datastore.getOrNull(tx, userMeta, user.getId());
        if (db == null) {
            tx.rollback();
            throw new EntityNotFoundException();
        }

        //TODO: why did I decide to manually copy these!?  Horrible!


        if (!StringUtils.equals(db.getName(), user.getName())) {
            tx.rollback();
            throw new FieldValueException("Cannot change 'name' with save();");
        }
        db.setAbout(user.getAbout());
        db.setEmail(StringUtils.lowerCase(user.getEmail()));
        db.setAdmin(user.isAdmin());
        db.setRealName(user.getRealName());

        UserDetail model = db.getDetailRef().getModel();
        if (model == null) {
            Datastore.put(tx, db);
        } else {
            Datastore.put(tx, db, model);
        }
        tx.commit();
        return user;
    }

    @Override
    public User setPassword(User user, String newPassword) throws EntityNotFoundException {
        Transaction tx = Datastore.beginTransaction();
        User db = Datastore.getOrNull(tx, userMeta, user.getId());
        if (db == null) {
            tx.rollback();
            throw new EntityNotFoundException();
        }
        db.setPassword(new ShortBlob(DigestUtil.encrypt(newPassword)));
        Datastore.put(tx, db);
        tx.commit();
        return db;
    }

    @Override
    public UserLogin addLogin(User user, UserLogin login) throws EntityNotFoundException, UserLoginExistsException {
        try {
            uniqueLogin.reserve(login.getProvider(), login.getUserId());
        } catch (UniqueConstraintException e) {
            throw new UserLoginExistsException(e.getMessage());
        }

        Transaction tx = Datastore.beginTransaction();
        User db = Datastore.getOrNull(tx, userMeta, user.getId());
        if (db == null) {
            tx.rollback();
            uniqueLogin.release(login.getProvider(), login.getUserId());
            throw new EntityNotFoundException();
        }
        login.setId(Datastore.allocateId(db.getId(), UserLogin.class));
        login.getUserRef().setModel(db);

        Datastore.put(tx, db, login);
        tx.commit();
        return login;
    }

    @Override
    public void removeLogin(Key id) throws EntityNotFoundException {
        Transaction tx = Datastore.beginTransaction();
        UserLogin dbLogin = Datastore.getOrNull(tx, userLoginMeta, id);
        if (dbLogin == null) {
            tx.rollback();
            throw new EntityNotFoundException("UserLogin");
        }
        Datastore.delete(tx, dbLogin.getId());
        tx.commit();

        uniqueLogin.release(dbLogin.getProvider(), dbLogin.getUserId());

    }

    @Override
    public User get(long id) {
        return get(Datastore.createKey(User.class, id));
    }

    @Override
    public User get(Key id) {
        return Datastore.getOrNull(User.class, id);
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
