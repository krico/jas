package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.spi.dm.JasEndpointEntity;

/**
 * @author krico
 * @since 11/01/15.
 */
public class JasGroup implements JasEndpointEntity {
    private String id;
    private String name;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
