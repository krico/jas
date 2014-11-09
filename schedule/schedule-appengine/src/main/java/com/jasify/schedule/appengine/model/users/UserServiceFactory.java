package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.util.DigestUtil;
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

        private DefaultUserService() {
            try {
                uniqueName = new UniqueConstraint(UserMeta.get(), "name");
            } catch (UniqueConstraintException e) {
                throw new IllegalStateException("Cannot create unique constraint for 'name'", e);
            }
        }

        @Override
        public User newUser() {
            User newUser = new User();
            newUser.setId(Datastore.allocateId(User.class));
            return newUser;
        }

        @Override
        public void create(User user, String password) throws UsernameExistsException {
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
        public User getUser(long id) {
            return Datastore.getOrNull(User.class, Datastore.createKey(User.class, id));
        }
    }

}
