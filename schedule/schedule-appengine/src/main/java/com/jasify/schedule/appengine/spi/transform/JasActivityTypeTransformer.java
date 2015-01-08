package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.spi.dm.JasActivityType;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 07/01/15.
 */
public class JasActivityTypeTransformer implements Transformer<ActivityType, JasActivityType> {

    public JasActivityTypeTransformer() {
    }

    @Override
    public JasActivityType transformTo(ActivityType internal) {
        JasActivityType external = new JasActivityType();
        BeanUtil.copyProperties(external, internal);
        return external;
    }

    @Override
    public ActivityType transformFrom(JasActivityType external) {
        ActivityType internal = new ActivityType();
        BeanUtil.copyProperties(internal, external);
        return internal;
    }
}
