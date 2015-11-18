package com.jasify.schedule.appengine.model.multipass;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.LowerCaseListener;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter;
import com.jasify.schedule.appengine.model.multipass.filter.DayFilter;
import com.jasify.schedule.appengine.model.multipass.filter.TimeFilter;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * @author wszarmach
 * @since 09/11/15.
 */
@Model
public class Multipass {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String name;

    @Attribute(listener = LowerCaseListener.class)
    private String lcName;

    private String description;

    private Double price;

    private String currency;

    private ModelRef<Organization> organizationRef = new ModelRef<>(Organization.class);

    private Integer expiresAfter;

    private Integer uses;

    // TODO: Shortcut to finish work. Filters should be somewhere else
    @Attribute(lob = true)
    private ActivityTypeFilter activityTypeFilter;

    @Attribute(lob = true)
    private DayFilter dayFilter;

    @Attribute(lob = true)
    private TimeFilter timeFilter;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
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
        setLcName(name);
    }

    public String getLcName() {
        return lcName;
    }

    public void setLcName(String lcName) {
        this.lcName = lcName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ModelRef<Organization> getOrganizationRef() {
        return organizationRef;
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
