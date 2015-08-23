package com.jasify.schedule.appengine.communication;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
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
        ApplicationContext.App app = createApp();
        put(ApplicationContext.APP_KEY, app);
        put(ApplicationContext.STRING_UTILS_KEY, new StringUtils());
        put(ApplicationContext.KEY_UTIL_KEY, KeyUtil.INSTANCE);
        put(ApplicationContext.MODEL_UTIL_KEY, createModelUtil(app));
    }

    protected ApplicationContext.App createApp() {
        return new AppImpl();
    }

    protected ApplicationContext.ModelUtil createModelUtil(ApplicationContext.App app) {
        return new ModelUtilImpl(app);
    }

    public static final class AppImpl implements ApplicationContext.App {
        public String getLogo() {
            return getUrl() + LOGO_PATH;
        }

        public String getUrl() {
            return EnvironmentUtil.defaultVersionUrl();
        }
    }

    public static final class ModelUtilImpl implements ApplicationContext.ModelUtil {
        private final ApplicationContext.App app;

        private ModelUtilImpl(ApplicationContext.App app) {
            this.app = app;
        }

        @Override
        public String url(PasswordRecovery recovery) {
            String code = recovery.getCode().getName();
            GenericUrl recoverUrl = new GenericUrl(app.getUrl());
            recoverUrl.clear();
            recoverUrl.setRawPath("/");
            recoverUrl.setFragment("/recover-password/" + code);
            return recoverUrl.build();
        }

        @Override
        public String name(User user) {
            if (user == null) return null;
            if (StringUtils.isNotBlank(user.getRealName()))
                return user.getRealName();
            if (StringUtils.isNotBlank(user.getName()))
                return user.getName();
            return KeyUtil.keyToString(user.getId());
        }
    }
}
