package com.jasify.schedule.appengine.model.history;

import org.slim3.datastore.Model;

/**
 * @author krico
 * @since 11/08/15.
 */
@Model
public class AuthHistory extends History {
    /**
     * Name that identifies the user involved (fall back to e-mail, etc)
     */
    private String name;

    /**
     * Remote address of the user involved
     */
    private String remoteAddress;

    /**
     * OAuth provider
     */
    private String provider;

    /**
     * The id that uniquely identifies a login with the provider
     */
    private String providerUserId;

    /**
     * The e-mail of the user with the provider
     */
    private String providerUserEmail;

    public AuthHistory() {
    }

    public AuthHistory(HistoryTypeEnum type) {
        super(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getProviderUserEmail() {
        return providerUserEmail;
    }

    public void setProviderUserEmail(String providerUserEmail) {
        this.providerUserEmail = providerUserEmail;
    }

    public String toOAuthCredentialsString() {
        return "(" + getProviderUserEmail() + '/' + getProviderUserId() + ')' + '@' + getProvider();
    }
}
