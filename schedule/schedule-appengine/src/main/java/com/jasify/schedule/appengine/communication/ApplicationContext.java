package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.velocity.context.Context;

/**
 * @author krico
 * @since 19/08/15.
 */
public interface ApplicationContext extends Context {
    String APP_KEY = "app";
    String STRING_UTILS_KEY = "sut";
    String KEY_UTIL_KEY = "kut";
    String MODEL_UTIL_KEY = "mut";

    interface App {

        String getLogo();

        String getUrl();
    }

    interface ModelUtil {
        String name(User user);

        String url(PasswordRecovery recovery);
    }
}
