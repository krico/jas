package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.multipass.*;
import com.jasify.schedule.appengine.spi.dm.JasMultipass;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author wszarmach
 * @since 10/11/15.
 */
// TODO: DELETE
public class JasMultipassTransformer implements Transformer<Multipass, JasMultipass> {
    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    @Override
    public JasMultipass transformTo(Multipass multipass) {
        JasMultipass jasMultipass = new JasMultipass();
        jasMultipass.setActivityTypeFilter(multipass.getActivityTypeFilter());
        jasMultipass.setCreated(multipass.getCreated());
        jasMultipass.setCurrency(multipass.getCurrency());
        jasMultipass.setDayFilter(multipass.getDayFilter());
        jasMultipass.setDescription(multipass.getDescription());
        jasMultipass.setExpiresAfter(multipass.getExpiresAfter());
        jasMultipass.setId(KeyUtil.keyToString(multipass.getId()));
        jasMultipass.setName(multipass.getName());
        if (multipass.getOrganizationRef().getKey() != null) {
            jasMultipass.setOrganizationId(keyTransformer.transformTo(multipass.getOrganizationRef().getKey()));
        }
        jasMultipass.setPrice(multipass.getPrice());
        jasMultipass.setTimeFilter(multipass.getTimeFilter());
        jasMultipass.setUses(multipass.getUses());
        return jasMultipass;
    }

    @Override
    public Multipass transformFrom(JasMultipass jasMultipass) {
        Multipass multipass = new Multipass();
        multipass.setActivityTypeFilter(jasMultipass.getActivityTypeFilter());
        multipass.setCurrency(jasMultipass.getCurrency());
        multipass.setDayFilter(jasMultipass.getDayFilter());
        multipass.setDescription(jasMultipass.getDescription());
        multipass.setExpiresAfter(jasMultipass.getExpiresAfter());
        multipass.setId(KeyUtil.stringToKey(jasMultipass.getId()));
        multipass.setName(jasMultipass.getName());
        multipass.getOrganizationRef().setKey(KeyUtil.stringToKey(jasMultipass.getOrganizationId()));
        multipass.setPrice(jasMultipass.getPrice());
        multipass.setTimeFilter(jasMultipass.getTimeFilter());
        multipass.setUses(jasMultipass.getUses());
        return multipass;
    }
}