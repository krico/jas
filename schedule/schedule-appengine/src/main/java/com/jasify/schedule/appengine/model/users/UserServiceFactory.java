package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.util.DigestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

/**
 * Created by krico on 08/11/14.
 */
public final class UserServiceFactory {
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
            try {
                uniqueName = new UniqueConstraint(UserMeta.get(), "name");
            } catch (UniqueConstraintException e) {
                throw new IllegalStateException("Cannot create unique constraint for 'name'", e);
            }
            userMeta = UserMeta.get();
        }

        @Override
        public User newUser() {
            User newUser = new User();
            newUser.setId(Datastore.allocateId(User.class));
            return newUser;
        }

        @Override
        public void create(User user, String password) throws UsernameExistsException {
            String withCase = user.getName();
            user.setNameWithCase(withCase);
            user.setName(StringUtils.lowerCase(withCase));
            user.setPassword(new ShortBlob(DigestUtil.encrypt(password)));
            Transaction tx = Datastore.beginTransaction();
            Datastore.put(tx, user);
            try {
                uniqueName.reserve(user.getName());
            } catch (UniqueConstraintException e) {
                tx.rollback();
                throw new UsernameExistsException(e.getMessage());
            }
            tx.commit();
        }

        @Override
        public void save(User user) throws UsernameExistsException, EntityNotFoundException {
            Transaction tx = Datastore.beginTransaction();
            User db = Datastore.getOrNull(tx, userMeta, user.getId());
            if (db == null) {
                tx.rollback();
                throw new EntityNotFoundException();
            }
            db.setAbout(user.getAbout());
            db.setPermissions(user.getPermissions());
            db.setEmail(user.getEmail());
            db.setNameWithCase(user.getNameWithCase());
            Datastore.put(tx, db);
            tx.commit();
        }

        @Override
        public User get(long id) {
            return Datastore.getOrNull(User.class, Datastore.createKey(User.class, id));
        }

        @Override
        public User findByName(String name) {
            return Datastore.query(User.class).filter(userMeta.name.equal(StringUtils.lowerCase(name))).asSingle();
        }
    }

}
