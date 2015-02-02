package com.jasify.schedule.appengine.oauth2;

import java.io.Serializable;

/**
 * @author krico
 * @since 29/01/15.
 */
public class OAuth2Info {
    private OAuth2ProviderEnum provider;
    private String userId;
    private String avatar;
    private String profile;
    private String email;
    private String realName;
    private Serializable state;

    public OAuth2Info(OAuth2ProviderEnum provider, Serializable state) {
        this.provider = provider;
        this.state = state;
    }

    public OAuth2ProviderEnum getProvider() {
        return provider;
    }

    public void setProvider(OAuth2ProviderEnum provider) {
        this.provider = provider;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Serializable getState() {
        return state;
    }

    public void setState(Serializable state) {
        this.state = state;
    }
}
