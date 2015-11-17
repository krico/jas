package com.jasify.schedule.appengine.model.multipass;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.multipass.MultipassActivityTypeMeta;
import com.jasify.schedule.appengine.model.LowerCaseListener;
import com.jasify.schedule.appengine.model.common.Organization;
import org.slim3.datastore.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wszarmach
 * @since 09/11/15.
 */
@Model
public class Multipass {
    public enum DayOfWeekEnum {
        // This must exist somewhere
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday
    }

    public enum TimeBarrierEnum {
        Before,
        After
    }

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

    private List<DayOfWeekEnum> days = new ArrayList<>();

    private TimeBarrierEnum timeBarrier;

    private Date time;

    @Attribute(persistent = false)
    private InverseModelListRef<MultipassActivityType, Multipass> multipassActivityTypeListRef =
            new InverseModelListRef<>(MultipassActivityType.class, MultipassActivityTypeMeta.get().multipassRef.getName(), this);

    public Multipass() {
    }

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

    public InverseModelListRef<MultipassActivityType, Multipass> getMultipassActivityTypeListRef() {
        return multipassActivityTypeListRef;
    }
}
