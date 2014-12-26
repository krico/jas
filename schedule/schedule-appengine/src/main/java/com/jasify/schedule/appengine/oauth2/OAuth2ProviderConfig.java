package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.application.ApplicationData;

/**
 * Configuration for oauth2 providers.  We store secrets and id on AppEngine datastore for security reasons.
 * <p/>
 * Example:
 * <p/>
 * {@code OAuth2ProviderConfig providerConfig = OAuth2ProviderConfig.ProviderEnum.Google.config();}
 *
 * @author krico
 * @since 18/12/14.
 */
public class OAuth2ProviderConfig {
    private final ProviderEnum provider;
    private final String clientId;
    private final String clientSecret;

    private OAuth2ProviderConfig(ProviderEnum provider) {
        this.provider = provider;
        ApplicationData appData = ApplicationData.instance();
        this.clientId = Preconditions.checkNotNull((String) appData.getProperty(provider.clientIdKey()),
                "ApplicationData key missing: " + provider.clientIdKey());
        this.clientSecret = Preconditions.checkNotNull((String) appData.getProperty(provider.clientSecretKey()),
                "ApplicationData key missing: " + provider.clientSecretKey());
    }

    public String getTokenUrl() {
        return provider.tokenUrl();
    }

    public String getAuthorizationUrl() {
        return provider.authorizationUrl();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public static enum ProviderEnum {
        Google {
            public String tokenUrl() {
                return GoogleOAuthConstants.TOKEN_SERVER_URL;
            }

            public String authorizationUrl() {
                return GoogleOAuthConstants.AUTHORIZATION_SERVER_URL;
            }
        };

        public OAuth2ProviderConfig config() {
            return new OAuth2ProviderConfig(this);
        }

        public String clientIdKey() {
            return OAuth2ProviderConfig.class.getSimpleName() + "." + name() + ".ClientId";
        }

        public String clientSecretKey() {
            return OAuth2ProviderConfig.class.getSimpleName() + "." + name() + ".ClientSecret";
        }

        public abstract String tokenUrl();

        public abstract String authorizationUrl();
    }
}
