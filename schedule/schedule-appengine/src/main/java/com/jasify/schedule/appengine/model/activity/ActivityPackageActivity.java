package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * Many to many link between ActivityPackage and Activity
 *
 * @author krico
 * @since 19/04/15.
 */
@Model
public class ActivityPackageActivity {
    @Attribute(primaryKey = true)
    private Key id;

    private ModelRef<ActivityPackage> activityPackageRef = new ModelRef<>(ActivityPackage.class);

    private ModelRef<Activity> activityRef = new ModelRef<>(Activity.class);

    public ActivityPackageActivity() {
    }

    public ActivityPackageActivity(ActivityPackage activityPackage, Activity activity) {
        getActivityPackageRef().setModel(activityPackage);
        getActivityRef().setModel(activity);
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public ModelRef<ActivityPackage> getActivityPackageRef() {
        return activityPackageRef;
    }

    public ModelRef<Activity> getActivityRef() {
        return activityRef;
    }
}
