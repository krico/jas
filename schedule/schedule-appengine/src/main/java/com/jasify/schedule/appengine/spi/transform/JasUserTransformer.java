package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasUser;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 04/01/15.
 */
public class JasUserTransformer implements Transformer<User, JasUser> {

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasUser
     */
    @Override
    public JasUser transformTo(User internal) {
        JasUser external = new JasUser();
        external.setAdmin(internal.isAdmin());
        external.setCreated(internal.getCreated());
        external.setEmail(internal.getEmail());
        external.setEmailVerified(internal.isEmailVerified());
        external.setLocale(internal.getLocale());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setModified(internal.getModified());
        external.setName(internal.getName());
        if (internal.getId() != null) {
            external.setNumericId(internal.getId().getId());
        }
        external.setRealName(internal.getRealName());
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return User
     */
    @Override
    public User transformFrom(JasUser external) {
        User internal = new User();
        internal.setAdmin(external.isAdmin());
        internal.setEmail(external.getEmail());
        internal.setEmailVerified(external.isEmailVerified()); // Maybe this is valid?
        internal.setLocale(external.getLocale());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setName(external.getName());
        internal.setRealName(external.getRealName());
        return internal;
    }
}
