package com.jasify.schedule.appengine.spi.dm;

import java.util.Date;

/**
 * @author krico
 * @since 04/01/15.
 */
public class JasUser implements JasEndpointEntity {
    private String id;
    private long numericId;
    private Date created;
    private Date modified;
    private String name;
    private String realName;
    private String email;
    private boolean emailVerified;
    private boolean admin = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getNumericId() {
        return numericId;
    }

    public void setNumericId(long numericId) {
        this.numericId = numericId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
