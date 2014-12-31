package com.jasify.schedule.appengine.spi.dm;

/**
 * @author krico
 * @since 30/12/14.
 */
public class JasUserLogin {
    private String id;
    private String provider;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
