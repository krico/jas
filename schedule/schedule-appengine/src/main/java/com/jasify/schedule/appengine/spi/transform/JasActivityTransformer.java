package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.spi.dm.JasActivity;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 07/01/15.
 */
public class JasActivityTransformer implements Transformer<Activity, JasActivity> {
    private static final Logger log = LoggerFactory.getLogger(JasActivityTransformer.class);

    private final JasActivityTypeTransformer typeTransformer = new JasActivityTypeTransformer();
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasActivity
     */
    @Override
    public JasActivity transformTo(Activity internal) {
        JasActivity external = new JasActivity();
        ActivityType activityType = getActivityType(internal);
        if (activityType != null) {
            external.setActivityType(typeTransformer.transformTo(activityType));
        }
        external.setBookItUrl("https://jasify-schedule.appspot.com/book-it.html#/" + internal.getId());
        external.setCurrency(internal.getCurrency());
        external.setDescription(internal.getDescription());
        external.setFinish(internal.getFinish());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setLocation(internal.getLocation());
        external.setMaxSubscriptions(internal.getMaxSubscriptions());
        external.setPrice(internal.getPrice());
        external.setStart(internal.getStart());
        external.setSubscriptionCount(internal.getSubscriptionCount());
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return Activity
     */
    @Override
    public Activity transformFrom(JasActivity external) {
        Activity internal = new Activity();
        if (external.getActivityType() != null) {
            internal.getActivityTypeRef().setModel(typeTransformer.transformFrom(external.getActivityType()));
        }
        internal.setCurrency(external.getCurrency());
        internal.setDescription(external.getDescription());
        internal.setFinish(external.getFinish());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setLocation(external.getLocation());
        internal.setMaxSubscriptions(external.getMaxSubscriptions());
        internal.setPrice(external.getPrice());
        internal.setStart(external.getStart());
        internal.setSubscriptionCount(external.getSubscriptionCount());
        return internal;
    }

    private ActivityType getActivityType(Activity activity) {
        try {
            return activityTypeDao.get(activity.getActivityTypeRef().getKey());
        } catch (EntityNotFoundException e) {
            log.error("Entity not found", e);
            return null;
        }
    }
}
