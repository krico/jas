package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
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
    String FORMAT_UTIL_KEY = "fut";
    String CURRENCY_UTIL_KEY = "cut";

    interface App {

        String getLogo();

        String getUrl();
    }
}
