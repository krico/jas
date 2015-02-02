package com.jasify.schedule.appengine.spi.dm;

/**
 * @author krico
 * @since 30/01/15.
 */
public class JasProviderAuthorizeResponse {
    private String authorizeUrl;

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
    }
}
