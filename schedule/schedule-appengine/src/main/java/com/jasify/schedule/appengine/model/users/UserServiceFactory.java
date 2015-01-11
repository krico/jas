package com.jasify.schedule.appengine.model.users;

/**
 * @author krico
 * @since 08/11/14.
 */
public class UserServiceFactory {
    private static UserService instance;

    protected UserServiceFactory() {
    }

    public static UserService getUserService() {
        if (instance == null)
            return DefaultUserService.instance();
        return instance;
    }

    protected static void setInstance(UserService instance) {
        UserServiceFactory.instance = instance;
    }

}
