package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
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

    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();

    public JasActivityTransformer() {
    }

    @Override
    public JasActivity transformTo(Activity internal) {
        JasActivity external = new JasActivity();
        BeanUtil.copyProperties(external, internal);
        internal.getActivityTypeRef().getKey();
        ActivityType activityType = getActivityType(internal);
        if (activityType != null) {
            external.setActivityType(typeTransformer.transformTo(activityType));
        }
        external.setBookItUrl("https://jasify-schedule.appspot.com/book-it.html#/" + external.getId());
        return external;
    }

    private ActivityType getActivityType (Activity activity) {
        try {
            return activityTypeDao.get(activity.getActivityTypeRef().getKey());
        } catch (EntityNotFoundException e) {
            // Is this possible?
            return activity.getActivityTypeRef().getModel();
        }
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
