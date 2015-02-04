package com.jasify.schedule.appengine.spi.dm;

/**
 * @author krico
 * @since 03/02/15.
 */
public class JasRecoverPasswordRequest {
    private String code;
    private String newPassword;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
