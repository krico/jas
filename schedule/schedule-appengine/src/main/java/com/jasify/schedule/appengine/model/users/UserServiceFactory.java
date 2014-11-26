package com.jasify.schedule.appengine.model.users;

/**
 * @author krico
 * @since 08/11/14.
 */
public final class UserServiceFactory {
    private UserServiceFactory() {
    }

    public static UserService getUserService() {
        return DefaultUserService.instance();
    }

}
