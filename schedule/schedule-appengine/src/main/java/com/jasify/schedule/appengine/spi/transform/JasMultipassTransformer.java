package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import com.jasify.schedule.appengine.spi.dm.JasMultipass;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author wszarmach
 * @since 10/11/15.
 */
public class JasMultipassTransformer implements Transformer<Multipass, JasMultipass> {
    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    @Override
    public JasMultipass transformTo(Multipass internal) {
        JasMultipass external = new JasMultipass();
        external.setCreated(internal.getCreated());
        external.setCurrency(internal.getCurrency());
        external.setDays(internal.getDays());
        external.setDescription(internal.getDescription());
        external.setExpiresAfter(internal.getExpiresAfter());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setName(internal.getName());
        if (internal.getOrganizationRef().getKey() != null) {
            external.setOrganizationId(keyTransformer.transformTo(internal.getOrganizationRef().getKey()));
        }
        external.setPrice(internal.getPrice());
        external.setTime(internal.getTime());
        external.setTimeBarrier(internal.getTimeBarrier());
        external.setUses(internal.getUses());
        return external;
    }

    @Override
    public Multipass transformFrom(JasMultipass external) {
        Multipass internal = new Multipass();
        internal.setCreated(external.getCreated());
        internal.setCurrency(external.getCurrency());
        internal.setDays(external.getDays());
        internal.setDescription(external.getDescription());
        internal.setExpiresAfter(external.getExpiresAfter());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setName(external.getName());
        internal.getOrganizationRef().setKey(KeyUtil.stringToKey(external.getOrganizationId()));
        internal.setPrice(external.getPrice());
        internal.setTime(external.getTime());
        internal.setTimeBarrier(external.getTimeBarrier());
        internal.setUses(external.getUses());
        return internal;
    }
}