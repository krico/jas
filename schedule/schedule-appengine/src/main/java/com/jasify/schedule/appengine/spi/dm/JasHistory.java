package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.history.HistoryTypeEnum;

import java.util.Date;

/**
 * @author krico
 * @since 10/08/15.
 */
public class JasHistory {
    private String id;

    private Date created;

    private HistoryTypeEnum type;

    private String description;

    private String currentUserId;

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

    public HistoryTypeEnum getType() {
        return type;
    }

    public void setType(HistoryTypeEnum type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}
