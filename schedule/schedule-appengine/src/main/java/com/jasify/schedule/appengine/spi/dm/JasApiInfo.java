package com.jasify.schedule.appengine.spi.dm;

import com.google.api.server.spi.config.Api;
import com.jasify.schedule.appengine.spi.JasifyEndpoint;

/**
 * @author krico
 * @since 27/12/14.
 */
public class JasApiInfo {
    private String version;
    private boolean authenticated;
    private boolean admin;

    public JasApiInfo(JasifyEndpoint endpoint) {
        Api annotation = endpoint.getClass().getAnnotation(Api.class);
        setVersion(annotation.version());
    }

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
