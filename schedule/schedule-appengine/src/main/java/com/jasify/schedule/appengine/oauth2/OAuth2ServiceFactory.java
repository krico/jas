package com.jasify.schedule.appengine.oauth2;

/**
 * @author krico
 * @since 27/01/15.
 */
public class OAuth2ServiceFactory {
    private static OAuth2Service instance;

    protected OAuth2ServiceFactory() {
    }

    public static OAuth2Service getOAuth2Service() {
        if (instance == null)
            return DefaultOAuth2Service.instance();
        return instance;
    }

    protected static void setInstance(OAuth2Service instance) {
        OAuth2ServiceFactory.instance = instance;
    }
}
