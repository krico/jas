package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.http.GenericUrl;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;

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

    public String getUserInfoUrl() {
        return provider.userInfoUrl();
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
            @Override
            public String tokenUrl() {
                return GoogleOAuthConstants.TOKEN_SERVER_URL;
            }

            @Override
            public String userInfoUrl() {
                return "https://accounts.google.com/oauth2/v2/userinfo";
            }

            @Override
            public String authorizationUrl() {
                return GoogleOAuthConstants.AUTHORIZATION_SERVER_URL;
            }

        },
        Facebook {
            @Override
            public String tokenUrl() {
                return "https://graph.facebook.com/oauth/access_token";
            }

            @Override
            public String userInfoUrl() {
                return "https://graph.facebook.com/me";
            }

            @Override
            public String authorizationUrl() {
                return "https://www.facebook.com/dialog/oauth";
            }

            @Override
            public GenericUrl additionalParams(GenericUrl url) {
                return url.set("display", "popup");
            }

            @Override
            public Collection<String> scopes() {
                return Arrays.asList("email", "public_profile");
            }
        };

        public static ProviderEnum parsePathInfo(String pathInfo) {
            if (StringUtils.startsWith(pathInfo, "/")) {
                pathInfo = pathInfo.substring(1);
            }
            return valueOf(pathInfo);
        }

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

        public abstract String userInfoUrl();

        public abstract String authorizationUrl();

        public GenericUrl additionalParams(GenericUrl url) {
            //Maybe this should go into provider config one day
            return url;
        }

        public Collection<String> scopes() {
            return Arrays.asList("email");
        }
    }
}
