package com.jasify.schedule.appengine.util;

import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import com.google.common.base.Preconditions;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 26/11/14.
 */
public final class EnvironmentUtil {
    public static final String CI_ENV_KEY = "ENV_IS_CI";
    private static final Pattern VERSION_PATTERN = Pattern.compile("^([^\\.]+)\\.(.*)$");


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
     * @return Result is of the form <major>.<minor> where <major> is the version name supplied at deploy time and <minor> is a timestamp value maintained by App Engine.
     * @see <a href="https://cloud.google.com/appengine/docs/java/javadoc/com/google/apphosting/api/ApiProxy.Environment#getVersionId()">https://cloud.google.com/appengine/docs/java/javadoc/com/google/apphosting/api/ApiProxy.Environment#getVersionId()</a>
     */
    public static String versionId() {
        return currentEnvironment().getVersionId();
    }

    public static String deployVersionName() {
        String versionId = versionId();
        Matcher matcher = VERSION_PATTERN.matcher(versionId);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return versionId;
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

    public static String defaultVersionUrl() {
        if (isDevelopment()) return "http://" + defaultVersionHostname();
        return "https://" + defaultVersionHostname();
    }

    public static boolean isDevelopment() {
        return !isProduction();
    }

    public static boolean isProduction() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
    }

    public static boolean isContinuousIntegrationEnvironment() {
        return Boolean.valueOf(System.getenv(CI_ENV_KEY));
    }

    public static File jasifyLocalConfig() {
        return new File(System.getProperty("user.home"), "jasify.json");
    }

}
