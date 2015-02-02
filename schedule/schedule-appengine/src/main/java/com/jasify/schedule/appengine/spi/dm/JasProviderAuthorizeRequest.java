package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;

/**
 * @author krico
 * @since 30/01/15.
 */
public class JasProviderAuthorizeRequest {
    private OAuth2ProviderEnum provider;
    private String baseUrl;
    private String data;

    public OAuth2ProviderEnum getProvider() {
        return provider;
    }

    public void setProvider(OAuth2ProviderEnum provider) {
        this.provider = provider;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
