package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Transaction;
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

import javax.annotation.Nonnull;

/**
 * Created by krico on 08/11/14.
 */
public final class UserServiceFactory {
    private static final Logger log = LoggerFactory.getLogger(UserServiceFactory.class);

    private UserServiceFactory() {
    }

    public static UserService getUserService() {
        return DefaultUserService.INSTANCE;
    }

    private static class DefaultUserService implements UserService {
        private static final DefaultUserService INSTANCE = new DefaultUserService();
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
    }

}
