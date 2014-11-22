package com.jasify.schedule.appengine.model.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by krico on 08/11/14.
 */
public final class UserServiceFactory {
    private UserServiceFactory() {
    }

    public static UserService getUserService() {
        return DefaultUserService.INSTANCE;
    }

}
