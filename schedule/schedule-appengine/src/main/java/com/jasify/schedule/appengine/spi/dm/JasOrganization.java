package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.spi.dm.JasEndpointEntity;

import java.util.Date;

/**
 * @author krico
 * @since 11/01/15.
 */
public class JasOrganization implements JasEndpointEntity {
    private String id;
    private String name;
    private String description;
    private Date created;
    private Date modified;

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
}
