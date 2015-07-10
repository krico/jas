package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageActivityMeta;
import com.jasify.schedule.appengine.model.common.Organization;
import org.slim3.datastore.*;

import java.util.*;

/**
 * @author krico
 * @since 19/04/15.
 */
@Model
public class ActivityPackage {

    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    /**
     * Date when this package becomes valid.
     * null means always valid
     */
    private Date validFrom;

    /**
     * Date when this package becomes invalid.
     * null means valid forever
     */
    private Date validUntil;

    /**
     * Number or times this package can be sold.
     * 0 means unlimited
     */
    private int maxExecutions;

    private int executionCount;

    private Double price;

    private String currency;

    private String name;

    private String description;

    /**
     * Number of items the subscriber is allowed to select
     */
    private int itemCount;

    private ModelRef<Organization> organizationRef = new ModelRef<>(Organization.class);

    @Attribute(persistent = false)
    private InverseModelListRef<ActivityPackageActivity, ActivityPackage> activityPackageActivityListRef =
            new InverseModelListRef<>(ActivityPackageActivity.class, ActivityPackageActivityMeta.get().activityPackageRef.getName(), this);


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

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public int getMaxExecutions() {
        return maxExecutions;
    }

    public void setMaxExecutions(int maxExecutions) {
        this.maxExecutions = maxExecutions;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(int executionCount) {
        this.executionCount = executionCount;
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

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public ModelRef<Organization> getOrganizationRef() {
        return organizationRef;
    }

    public InverseModelListRef<ActivityPackageActivity, ActivityPackage> getActivityPackageActivityListRef() {
        return activityPackageActivityListRef;
    }
}
