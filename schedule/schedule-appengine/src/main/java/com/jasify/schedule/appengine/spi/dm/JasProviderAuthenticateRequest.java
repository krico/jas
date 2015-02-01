package com.jasify.schedule.appengine.spi.dm;

/**
 * @author krico
 * @since 31/01/15.
 */
public class JasProviderAuthenticateRequest {
    private String callbackUrl;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
