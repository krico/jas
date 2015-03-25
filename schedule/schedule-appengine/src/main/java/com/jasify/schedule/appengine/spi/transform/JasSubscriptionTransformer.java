package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasSubscription;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 26/01/15.
 */
public class JasSubscriptionTransformer implements Transformer<Subscription, JasSubscription> {
    private final JasUserTransformer userTransformer = new JasUserTransformer();

    @Override
    public JasSubscription transformTo(Subscription internal) {
        JasSubscription external = new JasSubscription();
        BeanUtil.copyProperties(external, internal);
        User user = internal.getUserRef().getModel();
        if (user != null) {
            external.setUser(userTransformer.transformTo(user));
        }
        return external;
    }

    @Override
    public Subscription transformFrom(JasSubscription external) {
        Subscription internal = new Subscription();
        BeanUtil.copyProperties(internal, external);
        if (external.getUser() != null) {
            internal.getUserRef().setModel(userTransformer.transformFrom(external.getUser()));
        }
        return internal;
    }
}
