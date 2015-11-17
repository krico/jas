package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.multipass.Multipass.DayOfWeekEnum;
import com.jasify.schedule.appengine.model.multipass.Multipass.TimeBarrierEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wszarmach
 * @since 10/11/15.
 */
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

    private List<DayOfWeekEnum> days = new ArrayList<>();

    private TimeBarrierEnum timeBarrier;

    private Date time;

    public JasMultipass() {
    }

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

    public List<DayOfWeekEnum> getDays() { return days; }

    public void setDays(List<DayOfWeekEnum> days) { this.days = days; }

    public TimeBarrierEnum getTimeBarrier() {
        return timeBarrier;
    }

    public void setTimeBarrier(TimeBarrierEnum timeBarrier) {
        this.timeBarrier = timeBarrier;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
