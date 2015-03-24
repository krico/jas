package com.jasify.schedule.appengine.spi.dm;

import java.util.Date;

/**
 * @author krico
 * @since 26/01/15.
 */
public class JasSubscription implements JasEndpointEntity {
    private String id;

    private Date created;

    private JasUser user;

    private JasTransaction transaction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public JasUser getUser() { return user; }

    public void setUser(JasUser user) { this.user = user; }

    public JasTransaction getTransaction() { return transaction; }

    public void setTransaction(JasTransaction transaction) { this.transaction = transaction; }
}
