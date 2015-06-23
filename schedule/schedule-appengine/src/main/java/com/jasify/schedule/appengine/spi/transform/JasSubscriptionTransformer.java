package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasSubscription;
import com.jasify.schedule.appengine.util.KeyUtil;
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

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasSubscription
     */
    @Override
    public JasSubscription transformTo(Subscription internal) {
        JasSubscription external = new JasSubscription();
        external.setCreated(internal.getCreated());
        external.setId(KeyUtil.keyToString(internal.getId()));
        // TODO: external.setTransaction(???);
        User user = getUser(internal);
        if (user != null) {
            external.setUser(userTransformer.transformTo(user));
        }
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return Subscription
     */
    @Override
    public Subscription transformFrom(JasSubscription external) {
        Subscription internal = new Subscription();
        internal.setId(KeyUtil.stringToKey(external.getId()));
        if (external.getUser() != null) {
            internal.getUserRef().setModel(userTransformer.transformFrom(external.getUser()));
        }
        return internal;
    }

    private User getUser(Subscription subscription) {
        try {
            return userDao.get(subscription.getUserRef().getKey());
        } catch (EntityNotFoundException e) {
            log.error("Entity not found", e);
            return null;
        }
    }
}
