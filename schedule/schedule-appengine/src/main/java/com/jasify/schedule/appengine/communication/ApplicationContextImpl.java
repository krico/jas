package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.util.EnvironmentUtil;
import org.apache.velocity.VelocityContext;

/**
 * @author krico
 * @since 20/08/15.
 */
public class ApplicationContextImpl extends VelocityContext {
    public static final String LOGO_PATH = "/build/img/jasify-logo-color.png";

    public ApplicationContextImpl() {
        put(AppImpl.CONTEXT_KEY, new AppImpl());
    }

    public static final class AppImpl implements ApplicationContext.App {
        public String getLogo() {
            return getUrl() + LOGO_PATH;
        }

        public String getUrl() {
            return EnvironmentUtil.defaultVersionUrl();
        }
    }

}
