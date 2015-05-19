package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.http.GenericUrl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author krico
 * @since 01/02/15.
 */
public final class OAuth2Util {
    public static final ImmutableBiMap<String, String> APP_CODE_TO_APP_PATH = ImmutableBiMap.<String, String>builder()
            .put("/", "r007")
            .put("/index.html", "1d3X")
            .put("/book-it.html", "b0oK")
            .put("/booking-via-jasify.html", "v1aJ")
            .build();
    private static final Logger log = LoggerFactory.getLogger(OAuth2Util.class);
    private static final Random random = new SecureRandom();

    static {
        for (Map.Entry<String, String> entry : APP_CODE_TO_APP_PATH.entrySet()) {
            assert entry.getValue().length() == 4 : "code length must be 4";
        }
    }

    private OAuth2Util() {
    }

    public static String createStateKey(GenericUrl baseUrl) {
        String appPath = "/";
        List<String> parts = baseUrl.getPathParts();
        if (parts != null) {
            appPath += parts.get(parts.size() - 1);
        }
        String code = APP_CODE_TO_APP_PATH.get(appPath);
        if (code == null) {
            log.warn("NO CODE baseUr = [{}]", baseUrl);
            code = APP_CODE_TO_APP_PATH.get("/");
        }
        return String.format("%s%s", code, new BigInteger(130, random).toString(32));
    }

    public static String appPath(String stateKey) {
        Preconditions.checkNotNull(stateKey);
        Preconditions.checkArgument(stateKey.length() > 4);
        String appPath = APP_CODE_TO_APP_PATH.inverse().get(stateKey.substring(0, 4));
        if (appPath == null) {
            log.warn("No path for code = [{}]", stateKey);
            return "/";
        }
        return appPath;
    }
}
