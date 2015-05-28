package com.jasify.schedule.appengine.spi.dm;

import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.datastore.Key;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author szarmawa
 * @since 27/05/15.
 */
public class JasListQueryActivitiesRequest implements JasEndpointEntity {
    private List<Key> activityTypeIds = new ArrayList<>();
    private List<Key> organizationIds = new ArrayList<>();
    private Date fromDate;
    private Date toDate;
    private Integer offset;
    private Integer limit;

    public List<Key> getActivityTypeIds() {
        return activityTypeIds;
    }

    public void setActivityTypeIds(List<Key> activityTypeIds) {
        this.activityTypeIds = activityTypeIds;
    }

    public List<Key> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Key> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
