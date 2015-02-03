package com.jasify.schedule.appengine.spi.dm;

/**
 * @author krico
 * @since 03/02/15.
 */
public class JasForgotPasswordRequest {
    private String email;
    private String url;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
