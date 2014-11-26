package com.jasify.schedule.appengine.util;

import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * @author krico
 * @since 26/11/14.
 */
public final class EnvironmentUtil {
    private EnvironmentUtil() {
    }

    static ApiProxy.Environment currentEnvironment() {
        return Preconditions.checkNotNull(ApiProxy.getCurrentEnvironment(), "No current environment!");
    }

    /**
     * @return AppId of the current request
     */
    public static String appId() {
        return currentEnvironment().getAppId();
    }

    /**
     * @return email of the current logged in user
     */
    public static String email() {
        return currentEnvironment().getEmail();
    }

    public static String defaultVersionHostname() {
        return Objects.toString(currentEnvironment().getAttributes().get("com.google.appengine.runtime.default_version_hostname"));
    }

    public static boolean isDevelopment() {
        return !isProduction();
    }

    public static boolean isProduction() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
    }
}
