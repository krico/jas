package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.message.ContactMessage;
import com.jasify.schedule.appengine.spi.dm.JasContactMessage;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author wszarmach
 * @since 07/11/15.
 */
public class JasContactMessageTransformer implements Transformer<ContactMessage, JasContactMessage> {
    @Override
    public JasContactMessage transformTo(ContactMessage internal) {
        JasContactMessage external = new JasContactMessage();
        external.setCreated(internal.getCreated());
        external.setEmail(internal.getEmail());
        external.setFirstName(internal.getFirstName());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setLastName(internal.getLastName());
        external.setMessage(internal.getMessage());
        external.setSubject(internal.getSubject());
        return external;
    }

    @Override
    public ContactMessage transformFrom(JasContactMessage external) {
        ContactMessage internal = new ContactMessage();
        internal.setCreated(external.getCreated());
        internal.setEmail(external.getEmail());
        internal.setFirstName(external.getFirstName());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setLastName(external.getLastName());
        internal.setMessage(external.getMessage());
        internal.setSubject(external.getSubject());
        return internal;
    }
}