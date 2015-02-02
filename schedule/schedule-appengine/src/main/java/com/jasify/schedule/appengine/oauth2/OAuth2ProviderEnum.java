package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.client.http.HttpTransportFactory;
import com.jasify.schedule.appengine.util.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 29/01/15.
 */
public enum OAuth2ProviderEnum {
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

        @Override
        public TokenResponse requestToken(AuthorizationCodeResponseUrl authResponse) throws OAuth2Exception {
            OAuth2ProviderConfig providerConfig = config();
            ClientParametersAuthentication clientAuthentication = new ClientParametersAuthentication(providerConfig.getClientId(),
                    providerConfig.getClientSecret());

            GenericUrl redirectUrl = new GenericUrl(authResponse.toURI());
            redirectUrl.clear();

            AuthorizationCodeTokenRequest tokenRequest = new AuthorizationCodeTokenRequest(HttpTransportFactory.getHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    new GenericUrl(providerConfig.getTokenUrl()),
                    authResponse.getCode())
                    .setRedirectUri(redirectUrl.build())
                    .setClientAuthentication(clientAuthentication);

            try {
                return tokenRequest.execute();
            } catch (IOException e) {
                throw new OAuth2Exception.TokenRequestException(e);
            }
        }

        @Override
        public OAuth2Info requestInfo(OAuth2UserToken token) throws OAuth2Exception {
            Preconditions.checkNotNull(token);
            TokenResponse tokenResponse = Preconditions.checkNotNull(token.getTokenResponse());

            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .build().setFromTokenResponse(tokenResponse);

            try {
                Oauth2 oauth2 = new Oauth2.Builder(HttpTransportFactory.getHttpTransport(), JacksonFactory.getDefaultInstance(), credential).build();
                Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();
                Userinfoplus userInfo = oauth2.userinfo().get().execute();

                OAuth2Info ret = new OAuth2Info(this, token.getState());
                ret.setUserId(tokenInfo.getUserId());
                ret.setAvatar(userInfo.getPicture());
                ret.setProfile(userInfo.getLink());
                ret.setEmail(tokenInfo.getEmail());
                ret.setRealName(userInfo.getName());
                return ret;
            } catch (IOException e) {
                throw new OAuth2Exception.InfoException(e);
            }
        }
    },
    Facebook {
        private Pattern FB_ACCESS_TOKEN_PATTERN = Pattern.compile("^access_token=(.*)&expires=([0-9]+)$");

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
        public TokenResponse requestToken(AuthorizationCodeResponseUrl authResponse) throws OAuth2Exception {
            OAuth2ProviderConfig providerConfig = config();

            GenericUrl tokenRequestUrl = new GenericUrl(providerConfig.getTokenUrl());

            tokenRequestUrl.set("client_id", providerConfig.getClientId());
            tokenRequestUrl.set("client_secret", providerConfig.getClientSecret());
            GenericUrl redirect = new GenericUrl(authResponse.toURI());
            redirect.clear();
            tokenRequestUrl.set("redirect_uri", redirect.build());
            tokenRequestUrl.set("code", authResponse.getCode());

            HttpRequestFactory requestFactory = HttpTransportFactory.getHttpTransport().createRequestFactory();
            try {
                HttpRequest tokenRequest = requestFactory.buildGetRequest(tokenRequestUrl);
                HttpResponse tokenResponse = tokenRequest.execute();
                String accessToken;
                long expires;
                try {
                    if (!tokenResponse.isSuccessStatusCode()) {
                        throw new OAuth2Exception.TokenRequestException(tokenResponse.getStatusMessage() + " (" + tokenResponse.getStatusCode() + ")");
                    }
                    String responseData = IOUtils.toString(tokenResponse.getContent());
                    Matcher matcher = FB_ACCESS_TOKEN_PATTERN.matcher(responseData);
                    if (!matcher.matches()) {
                        throw new OAuth2Exception.TokenRequestException("Bad response data");
                    }
                    accessToken = matcher.group(1);
                    expires = Integer.parseInt(matcher.group(2));
                    return new TokenResponse()
                            .setAccessToken(accessToken)
                            .setExpiresInSeconds(expires);
                } finally {
                    tokenResponse.disconnect();
                }
            } catch (IOException e) {
                throw new OAuth2Exception.TokenRequestException(e);
            }
        }

        @Override
        public OAuth2Info requestInfo(OAuth2UserToken token) throws OAuth2Exception {
            Preconditions.checkNotNull(token);
            TokenResponse tokenResponse = Preconditions.checkNotNull(token.getTokenResponse());
            HttpRequestFactory requestFactory = HttpTransportFactory.getHttpTransport().createRequestFactory();

            OAuth2ProviderConfig providerConfig = config();
            GenericUrl infoRequestUrl = new GenericUrl(providerConfig.getUserInfoUrl());
            infoRequestUrl.set("access_token", tokenResponse.getAccessToken());

            Map infoData;
            try {
                HttpRequest infoRequest = requestFactory.buildGetRequest(infoRequestUrl);
                HttpResponse infoResponse = infoRequest.execute();
                try {
                    String data = IOUtils.toString(infoResponse.getContent());
                    infoData = JSON.fromJson(data, Map.class);
                } finally {
                    infoResponse.disconnect();
                }
            } catch (IOException e) {
                throw new OAuth2Exception.InfoException(e);
            }
            OAuth2Info ret = new OAuth2Info(this, token.getState());
            ret.setUserId(Objects.toString(Preconditions.checkNotNull(infoData.get("id"))));
            ret.setProfile(Objects.toString(infoData.get("link")));
            ret.setEmail(Objects.toString(infoData.get("email")));
            ret.setRealName(Objects.toString(infoData.get("name")));
            return ret;
        }

        @Override
        public GenericUrl additionalParams(GenericUrl url) {
//            return url.set("display", "popup");
            return url.set("display", "page");
        }

        @Override
        public Collection<String> scopes() {
            return Arrays.asList("email", "public_profile");
        }
    };

    public static OAuth2ProviderEnum parsePathInfo(String pathInfo) {
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

    public abstract TokenResponse requestToken(AuthorizationCodeResponseUrl authResponse) throws OAuth2Exception;

    public abstract OAuth2Info requestInfo(OAuth2UserToken token) throws OAuth2Exception;

    public GenericUrl additionalParams(GenericUrl url) {
        //Maybe this should go into provider config one day
        return url;
    }

    public Collection<String> scopes() {
        return Arrays.asList("email");
    }
}
