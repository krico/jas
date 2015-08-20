package com.jasify.schedule.appengine.communication;

import org.apache.velocity.context.Context;

/**
 * @author krico
 * @since 19/08/15.
 */
public interface ApplicationContext extends Context {
    interface App {
        String CONTEXT_KEY = "app";

        String getLogo();

        String getUrl();
    }
}
