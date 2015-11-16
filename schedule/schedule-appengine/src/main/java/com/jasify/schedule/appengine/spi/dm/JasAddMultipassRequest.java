package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.multipass.Multipass;

/**
 * @author wszarmach
 * @since 11/11/15.
 */
public class JasAddMultipassRequest {
    private Key organizationId;
    private Multipass multipass;

    public Key getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Key organizationId) {
        this.organizationId = organizationId;
    }

    public Multipass getMultipass() {
        return multipass;
    }

    public void setMultipass(Multipass multipass) {
        this.multipass = multipass;
    }
}
