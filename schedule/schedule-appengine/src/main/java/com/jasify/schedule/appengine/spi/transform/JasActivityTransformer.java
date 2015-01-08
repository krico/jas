package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.spi.dm.JasActivity;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 07/01/15.
 */
public class JasActivityTransformer implements Transformer<Activity, JasActivity> {
    private final JasActivityTypeTransformer typeTransformer = new JasActivityTypeTransformer();

    public JasActivityTransformer() {
    }

    @Override
    public JasActivity transformTo(Activity internal) {
        JasActivity external = new JasActivity();
        BeanUtil.copyProperties(external, internal);
        ActivityType activityType = internal.getActivityTypeRef().getModel();
        if (activityType != null) {
            external.setActivityType(typeTransformer.transformTo(activityType));
        }
        external.setBookItUrl("https://jasify-schedule.appspot.com/book-it/" + external.getId());
        return external;
    }

    @Override
    public Activity transformFrom(JasActivity external) {
        Activity internal = new Activity();
        BeanUtil.copyProperties(internal, external);
        if (external.getActivityType() != null) {
            internal.getActivityTypeRef().setModel(typeTransformer.transformFrom(external.getActivityType()));
        }
        return internal;
    }
}
