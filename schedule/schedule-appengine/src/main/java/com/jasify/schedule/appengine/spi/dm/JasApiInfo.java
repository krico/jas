package com.jasify.schedule.appengine.spi.dm;

import com.google.api.server.spi.config.Api;
import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.spi.JasifyEndpoint;

/**
 * @author krico
 * @since 27/12/14.
 */
public class JasApiInfo {
    private String apiVersion;
    private boolean authenticated;
    private boolean admin;
    private boolean orgMember;
    private String number = Version.getNumber();
    private String branch = Version.getBranch();
    private long timestamp = Version.getTimestamp();
    private String version = Version.getVersion();
    private String timestampVersion = Version.getTimestampVersion();

    public JasApiInfo(JasifyEndpoint endpoint) {
        Api annotation = endpoint.getClass().getAnnotation(Api.class);
        setApiVersion(annotation.version());
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
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

    public boolean isOrgMember() {
        return orgMember;
    }

    public void setOrgMember(boolean orgMember) {
        this.orgMember = orgMember;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimestampVersion() {
        return timestampVersion;
    }

    public void setTimestampVersion(String timestampVersion) {
        this.timestampVersion = timestampVersion;
    }
}
