package com.jasify.schedule.appengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author krico
 * @since 21/03/15.
 */
public final class Version {
    private static final Logger log = LoggerFactory.getLogger(Version.class);
    private static final boolean failed;
    private static final String number;
    private static final String branch;
    private static final long timestamp;
    private static final String version;
    private static final String timestampVersion;

    static {
        boolean _failed = false;
        String _number;
        String _branch;
        long _timestamp;
        String _version;
        log.info("Loading version information");
        try (InputStream in = Version.class.getResourceAsStream(Version.class.getSimpleName() + ".properties")) {
            Properties properties = new Properties();
            properties.load(in);
            _number = properties.getProperty("build.number");
            _branch = properties.getProperty("build.branch");
            _version = properties.getProperty("build.version");
            _timestamp = Long.parseLong(properties.getProperty("build.timestamp"));
        } catch (Exception e) {
            log.warn("Failed to read Version information.  (in dev it might be just a case of running mvn compile once)", e);
            _failed = true;
            _number = "unknown";
            _branch = "none";
            _timestamp = System.currentTimeMillis();
            _version = "0.0-DEV";
        }

        failed = _failed;
        number = _number;
        branch = _branch;
        timestamp = _timestamp;
        version = _version;
        timestampVersion = new SimpleDateFormat("yy.MM.dd-HHmmss").format(new Date(_timestamp));
        log.debug("Version loaded: {}", toVersionString());
    }

    private Version() {
    }

    public static String getNumber() {
        return number;
    }

    public static String getBranch() {
        return branch;
    }

    public static long getTimestamp() {
        return timestamp;
    }

    public static String getVersion() {
        return version;
    }

    public static String getTimestampVersion() {
        return timestampVersion;
    }

    public static boolean isFailed() {
        return failed;
    }

    public static String toShortVersionString() {
        return getVersion() + " (" + getTimestampVersion() + ")";
    }

    public static String toVersionString() {
        return getVersion() + " #" + getNumber() + "/" + getBranch() + " @" + getTimestampVersion();
    }
}
