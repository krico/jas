package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.spi.dm.JasSubscription;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 26/01/15.
 */
public class JasSubscriptionTransformer implements Transformer<Subscription, JasSubscription> {
    @Override
    public JasSubscription transformTo(Subscription internal) {
        JasSubscription external = new JasSubscription();

        BeanUtil.copyProperties(external, internal);

        return external;
    }

    @Override
    public Subscription transformFrom(JasSubscription external) {
        Subscription internal = new Subscription();

        BeanUtil.copyProperties(internal, external);

        return internal;
    }
}
