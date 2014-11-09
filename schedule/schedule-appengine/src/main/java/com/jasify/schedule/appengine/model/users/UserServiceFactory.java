package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
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

        private DefaultUserService() {
        }

        @Override
        public User newUser() {
            User newUser = new User();
            newUser.setId(Datastore.allocateId(User.class));
            return newUser;
        }

        @Override
        public void create(User user, String password) {
        }
    }

}
