package com.jasify.schedule.appengine.communication;

import org.apache.velocity.context.Context;

/**
 * @author krico
 * @since 19/08/15.
 */
public interface ApplicationContext extends Context {
    String APP_KEY = "app";
    String STRING_UTILS_KEY = "sut";
    String KEY_UTIL_KEY = "kut";

    interface App {

        String getLogo();

        String getUrl();
    }
}
