package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter;
import com.jasify.schedule.appengine.model.multipass.filter.DayFilter;
import com.jasify.schedule.appengine.model.multipass.filter.TimeFilter;

import java.util.Date;

/**
 * @author wszarmach
 * @since 10/11/15.
 */
// TODO: DELETE
public class JasMultipass implements JasEndpointEntity {
    private String id;

    private String organizationId;

    private Date created;

    private Date modified;

    private String name;

    private String description;

    private Double price;

    private String currency;

    private Integer expiresAfter;

    private Integer uses;

    private ActivityTypeFilter activityTypeFilter;

    private DayFilter dayFilter;

    private TimeFilter timeFilter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getExpiresAfter() { return expiresAfter; }

    public void setExpiresAfter(Integer expiresAfter) { this.expiresAfter = expiresAfter; }

    public Integer getUses() { return uses; }

    public void setUses(Integer uses) { this.uses = uses; }

    public ActivityTypeFilter getActivityTypeFilter() { return activityTypeFilter; }

    public void setActivityTypeFilter(ActivityTypeFilter activityTypeFilter) { this.activityTypeFilter = activityTypeFilter; }

    public DayFilter getDayFilter() { return dayFilter; }

    public void setDayFilter(DayFilter dayFilter) { this.dayFilter = dayFilter; }

    public TimeFilter getTimeFilter() { return timeFilter; }

    public void setTimeFilter(TimeFilter timeFilter) { this.timeFilter = timeFilter; }
}
