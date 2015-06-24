package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.spi.dm.JasOrganization;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 11/01/15.
 */
public class JasOrganizationTransformer implements Transformer<Organization, JasOrganization> {

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasOrganization
     */
    @Override
    public JasOrganization transformTo(Organization internal) {
        JasOrganization external = new JasOrganization();
        external.setCreated(internal.getCreated());
        external.setDescription(internal.getDescription());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setModified(internal.getModified());
        external.setName(internal.getName());
        for (PaymentTypeEnum paymentTypeEnum : internal.getPaymentTypes()) {
            external.getPaymentTypes().add(paymentTypeEnum);
        }
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return Organization
     */
    @Override
    public Organization transformFrom(JasOrganization external) {
        Organization internal = new Organization();
        internal.setDescription(external.getDescription());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setName(external.getName());
        for (PaymentTypeEnum paymentTypeEnum : external.getPaymentTypes()) {
            internal.getPaymentTypes().add(paymentTypeEnum);
        }
        return internal;
    }
}
