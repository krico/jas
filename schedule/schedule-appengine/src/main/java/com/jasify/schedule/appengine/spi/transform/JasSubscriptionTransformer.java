package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasSubscription;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 26/01/15.
 */
public class JasSubscriptionTransformer implements Transformer<Subscription, JasSubscription> {
    private static final Logger log = LoggerFactory.getLogger(JasSubscriptionTransformer.class);

    private final JasUserTransformer userTransformer = new JasUserTransformer();
    private final UserDao userDao = new UserDao();
    @Override
    public JasSubscription transformTo(Subscription internal) {
        JasSubscription external = new JasSubscription();
        BeanUtil.copyProperties(external, internal);
        User user = getUser(internal);
        if (user != null) {
            external.setUser(userTransformer.transformTo(user));
        }

        return external;
    }

    private User getUser(Subscription subscription) {
        try {
            return userDao.get(subscription.getUserRef().getKey());
        } catch (EntityNotFoundException e) {
            log.error("Entity not found", e);
            return subscription.getUserRef().getModel();
        }
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
