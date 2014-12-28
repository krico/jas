package com.jasify.schedule.appengine.endpoints;

/**
 * @author krico
 * @since 27/12/14.
 */
public class Settings {
    private String version;
    private boolean authenticated;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
