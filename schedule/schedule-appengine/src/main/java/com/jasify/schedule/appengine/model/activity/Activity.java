package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * @author krico
 * @since 07/01/15.
 */
@Model
public class Activity {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private Date start;

    private Date finish;

    private Double price;

    private String currency;

    private ModelRef<ActivityType> activityTypeRef = new ModelRef<>(ActivityType.class);

    private ModelRef<RepeatDetails> repeatDetailsRef = new ModelRef<>(RepeatDetails.class);

    private String location;

    private int maxSubscriptions;

    private int subscriptionCount;

    private String name;

    private String description;

    private String timeZone;

    @Attribute(persistent = false)
    private InverseModelListRef<Subscription, Activity> subscriptionListRef =
            new InverseModelListRef<>(Subscription.class, SubscriptionMeta.get().activityRef.getName(), this);


    public Activity() {
    }

    public Activity(ActivityType activityType) {
        setName(activityType.getName());
        activityTypeRef.setModel(activityType);
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
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

    public ModelRef<ActivityType> getActivityTypeRef() {
        return activityTypeRef;
    }

    public void setRepeatDetails(RepeatDetails repeatDetails) { repeatDetailsRef.setModel(repeatDetails); }

    public ModelRef<RepeatDetails> getRepeatDetailsRef() {
        return repeatDetailsRef;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxSubscriptions() {
        return maxSubscriptions;
    }

    public void setMaxSubscriptions(int maxSubscriptions) {
        this.maxSubscriptions = maxSubscriptions;
    }

    public int getSubscriptionCount() {
        return subscriptionCount;
    }

    public void setSubscriptionCount(int subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
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

    public InverseModelListRef<Subscription, Activity> getSubscriptionListRef() {
        return subscriptionListRef;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
