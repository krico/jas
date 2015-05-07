package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageSubscriptionMeta;
import com.jasify.schedule.appengine.model.balance.HasTransfer;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * This is like a subscription, but to a package
 *
 * @author krico
 * @since 19/04/15.
 */
@Model
public class ActivityPackageExecution implements HasTransfer {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private ModelRef<ActivityPackage> activityPackageRef = new ModelRef<>(ActivityPackage.class);

    private ModelRef<User> userRef = new ModelRef<>(User.class);

    private ModelRef<Transfer> transferRef = new ModelRef<>(Transfer.class);

    @Attribute(persistent = false)
    private InverseModelListRef<ActivityPackageSubscription, ActivityPackageExecution> subscriptionListRef =
            new InverseModelListRef<>(ActivityPackageSubscription.class, ActivityPackageSubscriptionMeta.get().activityPackageExecutionRef.getName(), this);

    @Override
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

    public ModelRef<ActivityPackage> getActivityPackageRef() {
        return activityPackageRef;
    }

    public ModelRef<User> getUserRef() {
        return userRef;
    }

    @Override
    public ModelRef<Transfer> getTransferRef() {
        return transferRef;
    }

    public InverseModelListRef<ActivityPackageSubscription, ActivityPackageExecution> getSubscriptionListRef() {
        return subscriptionListRef;
    }
}
