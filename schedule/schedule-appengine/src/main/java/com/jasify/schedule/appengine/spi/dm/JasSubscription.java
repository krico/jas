package com.jasify.schedule.appengine.spi.dm;

/**
 * @author krico
 * @since 26/01/15.
 */
public class JasSubscription implements JasEndpointEntity {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
