package com.jasify.schedule.appengine.spi;

/**
 * @author krico
 * @since 27/12/14.
 */
public class ApiInfo {
    private String version;
    private boolean authenticated;
    private boolean admin;

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
