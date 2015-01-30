package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.oauth2.OAuth2Exception.CodeResponseException;
import com.jasify.schedule.appengine.oauth2.OAuth2Exception.MissingStateException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author krico
 * @since 27/01/15.
 */
class DefaultOAuth2Service implements OAuth2Service {
    static final String CALLBACK_PATH_PREFIX = "/oauth2/callback/";
    static final int EXPIRE_MINUTES = 10;
    private final Random random;

    private DefaultOAuth2Service() {
        random = new SecureRandom();
    }

    static OAuth2Service instance() {
        return Singleton.INSTANCE;
    }

    @Override
    public GenericUrl createCodeRequestUrl(GenericUrl baseUrl, OAuth2ProviderEnum provider, @Nonnull Serializable state) {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        String stateKey = new BigInteger(130, random).toString(32);
        //EXPIRE_MINUTES minutes is more than enough to authenticate
        syncCache.put(stateKey, state, Expiration.byDeltaSeconds((int) TimeUnit.MINUTES.toSeconds(EXPIRE_MINUTES)));
        GenericUrl redirectUrl = new GenericUrl(baseUrl.toURI());
        redirectUrl.setRawPath(CALLBACK_PATH_PREFIX + provider.name());
        OAuth2ProviderConfig providerConfig = provider.config();

        return provider.additionalParams(new AuthorizationCodeRequestUrl(providerConfig.getAuthorizationUrl(), providerConfig.getClientId())
                .setState(stateKey)
                .setRedirectUri(redirectUrl.build())
                .setScopes(provider.scopes()));
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Override
    public OAuth2UserToken fetchUserToken(GenericUrl codeRequestResponseUrl) throws OAuth2Exception {
        OAuth2ProviderEnum provider = extractProvider(codeRequestResponseUrl);

        AuthorizationCodeResponseUrl authResponse = new AuthorizationCodeResponseUrl(codeRequestResponseUrl.toString());
        if (StringUtils.isNotBlank(authResponse.getError())) {
            throw new CodeResponseException(authResponse.getErrorDescription());
        }

        String stateKey = authResponse.getState();
        if (StringUtils.isBlank(stateKey)) {
            throw new MissingStateException();
        }

        Serializable state = restoreState(stateKey);
        TokenResponse tokenResponse = provider.requestToken(authResponse);
        return new OAuth2UserToken(provider, tokenResponse, state);
    }

    @Override
    public OAuth2Info fetchInfo(OAuth2UserToken token) throws OAuth2Exception {
        OAuth2ProviderEnum provider = Preconditions.checkNotNull(token.getProvider());
        return provider.requestInfo(token);
    }

    private OAuth2ProviderEnum extractProvider(GenericUrl responseUrl) throws OAuth2Exception.BadProviderException {
        try {
            List<String> pathParts = responseUrl.getPathParts();
            return OAuth2ProviderEnum.parsePathInfo(pathParts.get(pathParts.size() - 1));
        } catch (Exception e) {
            throw new OAuth2Exception.BadProviderException();
        }
    }

    private Serializable restoreState(String stateKey) throws OAuth2Exception.InconsistentStateException {
        MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        Object o = syncCache.get(stateKey);
        if (o instanceof Serializable) {
            syncCache.delete(stateKey);
            return (Serializable) o;
        } else {
            throw new OAuth2Exception.InconsistentStateException();
        }
    }

    private static class Singleton {
        private static final OAuth2Service INSTANCE = new DefaultOAuth2Service();
    }

}
