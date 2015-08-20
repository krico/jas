package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

/**
 * @author krico
 * @since 20/08/15.
 */
public class ApplicationContextImpl extends VelocityContext {
    public static final String LOGO_PATH = "/build/img/jasify-logo-color.png";

    public ApplicationContextImpl() {
        put(ApplicationContext.APP_KEY, createApp());
        put(ApplicationContext.STRING_UTILS_KEY, new StringUtils());
        put(ApplicationContext.KEY_UTIL_KEY, KeyUtil.INSTANCE);
    }

    protected ApplicationContext.App createApp() {
        return new AppImpl();
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
