package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.util.EnvironmentUtil;
import org.apache.velocity.VelocityContext;

/**
 * @author krico
 * @since 19/08/15.
 */
public final class ApplicationContext extends VelocityContext {
    public static final String LOGO_PATH = "/build/img/jasify-logo-color.png";

    public ApplicationContext() {
        put("app", new App());
    }

    public static final class App {
        public String getLogo() {
            return EnvironmentUtil.defaultVersionUrl() + LOGO_PATH;
        }

        public String getUrl() {
            return EnvironmentUtil.defaultVersionUrl();
        }
    }
}
