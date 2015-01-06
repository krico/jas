package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;

/**
 * @author krico
 * @since 01/01/15.
 */
public class JasChangePasswordRequest implements JasEndpointEntity {
    private Key userId;
    private String oldPassword;
    private String newPassword;

    public JasChangePasswordRequest() {
    }

    public JasChangePasswordRequest(Key userId, String oldPassword, String newPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public Key getUserId() {
        return userId;
    }

    public void setUserId(Key userId) {
        this.userId = userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
