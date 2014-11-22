package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.InMemoryFilterCriterion;
import org.slim3.datastore.ModelQuery;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 22/11/14.
 */
class DefaultUserService implements UserService {
    static final DefaultUserService INSTANCE = new DefaultUserService();
    private static final Logger log = LoggerFactory.getLogger(DefaultUserService.class);
    private final UniqueConstraint uniqueName;
    private final UserMeta userMeta;

    private DefaultUserService() {
        uniqueName = UniqueConstraint.create(UserMeta.get(), "name");
        userMeta = UserMeta.get();
    }

    @Override
    public User newUser() {
        User newUser = new User();
        newUser.setId(Datastore.allocateId(User.class));
        return newUser;
    }

    @Override
    public User create(User user, String password) throws UsernameExistsException {
        String withCase = user.getName();
        user.setNameWithCase(withCase);
        user.setName(StringUtils.lowerCase(withCase));
        user.setPassword(new ShortBlob(DigestUtil.encrypt(password)));

        try {
            uniqueName.reserve(user.getName());
        } catch (UniqueConstraintException e) {
            throw new UsernameExistsException(e.getMessage());
        }

        Transaction tx = Datastore.beginTransaction();
        Datastore.put(tx, user);
        tx.commit();
        return user;
    }

    @Override
    public User save(User user) throws EntityNotFoundException, FieldValueException {
        Transaction tx = Datastore.beginTransaction();
        User db = Datastore.getOrNull(tx, userMeta, user.getId());
        if (db == null) {
            tx.rollback();
            throw new EntityNotFoundException();
        }

        if (!StringUtils.equals(db.getName(), user.getName())) {
            throw new FieldValueException("Cannot change 'name' with save();");
        }
        if (!StringUtils.equalsIgnoreCase(db.getName(), user.getNameWithCase())) {
            throw new FieldValueException("Cannot change 'name' casing with save();");
        }
        db.setAbout(user.getAbout());
        db.setPermissions(user.getPermissions());
        db.setEmail(user.getEmail());
        db.setNameWithCase(user.getNameWithCase());
        Datastore.put(tx, db);
        tx.commit();
        return user;
    }

    @Override
    public User get(long id) {
        return Datastore.getOrNull(User.class, Datastore.createKey(User.class, id));
    }

    @Override
    public User findByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return Datastore.query(User.class).filter(userMeta.name.equal(StringUtils.lowerCase(name))).asSingle();
    }

    @Override
    public boolean exists(String name) {
        return !Datastore.query(User.class).filter(userMeta.name.equal(StringUtils.lowerCase(name))).asKeyList().isEmpty();
    }

    @Nonnull
    @Override
    public User login(String name, String password) throws LoginFailedException {
        //TODO: add login/logout history under user entity
        User user = findByName(name);
        if (user == null) {
            log.debug("user={} not found.", name);
            throw new LoginFailedException();
        }
        if (DigestUtil.verify(user.getPassword().getBytes(), password)) {
            log.info("user={} logged in.", name);
            return user;
        }
        log.info("user={} login failed!", name);
        throw new LoginFailedException();
    }

    @Override
    public List<User> list(Query.SortDirection order, int offset, int limit) {

        ModelQuery<User> query = Datastore.query(userMeta);


        if (offset > 0) query.offset(offset);
        if (limit > 0) query.limit(limit);

        if (order == Query.SortDirection.DESCENDING) {
            query.sort(userMeta.id.desc);
        } else {
            query.sort(userMeta.id.asc);
        }

        return query.asList();

    }

    @Override
    public List<User> searchByName(String pattern, Query.SortDirection order, int offset, int limit) {
        ModelQuery<User> query = Datastore.query(userMeta);

        if (order == Query.SortDirection.DESCENDING) {
            query.sort(userMeta.name.desc);
        } else {
            query.sort(userMeta.name.asc);
        }

        if (StringUtils.isNotBlank(pattern)) {
            final Pattern pat = Pattern.compile(pattern);
            /* TODO: in memory search might be slow when we have millions of users :-) */
            query.filterInMemory(new InMemoryFilterCriterion() {
                @Override
                public boolean accept(Object model) {
                    return pat.matcher(((User) model).getName()).find();
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
        } else {

            if (offset > 0) query.offset(offset);
            if (limit > 0) query.limit(limit);

            return query.asList();
        }

    }

    @Override
    public List<User> searchByEmail(String pattern, Query.SortDirection order, int offset, int limit) {
        ModelQuery<User> query = Datastore.query(userMeta);

        if (order == Query.SortDirection.DESCENDING) {
            query.sort(userMeta.email.desc);
        } else {
            query.sort(userMeta.email.asc);
        }

        if (StringUtils.isNotBlank(pattern)) {
            final Pattern pat = Pattern.compile(pattern);
            /* TODO: in memory search might be slow when we have millions of users :-) */
            query.filterInMemory(new InMemoryFilterCriterion() {
                @Override
                public boolean accept(Object model) {
                    String emailString = TypeUtil.toString(((User) model).getEmail());
                    return pat.matcher(StringUtils.trimToEmpty(emailString)).find();
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
        } else {

            if (offset > 0) query.offset(offset);
            if (limit > 0) query.limit(limit);

            return query.asList();
        }
    }
}
