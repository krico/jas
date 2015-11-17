package com.jasify.schedule.appengine.model.multipass;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
@Model
public class MultipassActivityType {
    @Attribute(primaryKey = true)
    private Key id;

    private ModelRef<Multipass> multipassRef = new ModelRef<>(Multipass.class);

    private ModelRef<ActivityType> activityTypeRef = new ModelRef<>(ActivityType.class);

    public MultipassActivityType() {
    }

    public MultipassActivityType(Multipass multipass, ActivityType activityType) {
        getMultipassRef().setModel(multipass);
        getActivityTypeRef().setModel(activityType);
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public ModelRef<Multipass> getMultipassRef() {
        return multipassRef;
    }

    public ModelRef<ActivityType> getActivityTypeRef() {
        return activityTypeRef;
    }
}
