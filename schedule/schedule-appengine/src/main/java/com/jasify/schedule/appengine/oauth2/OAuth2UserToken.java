package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.auth.oauth2.TokenResponse;

import java.io.Serializable;

/**
 * @author krico
 * @since 29/01/15.
 */
public class OAuth2UserToken {
    private final OAuth2ProviderEnum provider;
    private final TokenResponse tokenResponse;
    private final Serializable state;

    public OAuth2UserToken(OAuth2ProviderEnum provider, TokenResponse tokenResponse, Serializable state) {
        this.provider = provider;
        this.tokenResponse = tokenResponse;
        this.state = state;
    }

    public OAuth2ProviderEnum getProvider() {
        return provider;
    }

    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    public Serializable getState() {
        return state;
    }
}
