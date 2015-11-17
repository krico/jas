package com.jasify.schedule.appengine.spi.transform;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import com.jasify.schedule.appengine.model.multipass.Multipass.DayOfWeekEnum;
import com.jasify.schedule.appengine.spi.dm.JasMultipass;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author wszarmach
 * @since 12/11/15.
 */
public class JasMultipassTransformerTest {

    private JasMultipassTransformer transformer = new JasMultipassTransformer();

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testTransformTo() throws Exception {
        Multipass internal = com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName");
        internal.setDays(new ArrayList<DayOfWeekEnum>());
        internal.getDays().add(DayOfWeekEnum.Wednesday);
        internal.getDays().add(DayOfWeekEnum.Sunday);
        internal.setId(Datastore.allocateId(Multipass.class));
        internal.getOrganizationRef().setKey(Datastore.allocateId(Organization.class));
        JasMultipass external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getCurrency(), external.getCurrency());
        assertEquals(internal.getDays().size(), external.getDays().size());
        for (int index = 0; index < external.getDays().size(); index++) {
            assertEquals(internal.getDays().get(index), external.getDays().get(index));
        }
        assertEquals(internal.getDescription(), external.getDescription());
        assertEquals(internal.getExpiresAfter(), external.getExpiresAfter());
        assertEquals(KeyUtil.keyToString(internal.getId()), external.getId());
        assertEquals(internal.getLcName(), external.getName());
        assertEquals(internal.getName(), external.getName());
        assertEquals(KeyUtil.keyToString(internal.getOrganizationRef().getKey()), external.getOrganizationId());
        assertEquals(internal.getPrice(), external.getPrice());
        assertEquals(internal.getTime(), external.getTime());
        assertEquals(internal.getTimeBarrier(), external.getTimeBarrier());
        assertEquals(internal.getUses(), external.getUses());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasMultipass external = com.jasify.schedule.appengine.TestHelper.populateBean(JasMultipass.class);
        external.setId(KeyUtil.keyToString(Datastore.allocateId(Multipass.class)));
        external.setOrganizationId(KeyUtil.keyToString(Datastore.allocateId(Organization.class)));
        external.setDays(new ArrayList<DayOfWeekEnum>());
        external.getDays().add(DayOfWeekEnum.Monday);
        external.getDays().add(DayOfWeekEnum.Friday);
        Multipass internal = transformer.transformFrom(external);
        assertNotNull(internal);
        assertEquals(external.getCreated(), internal.getCreated());
        assertEquals(external.getCurrency(), internal.getCurrency());
        assertEquals(external.getDays().size(), internal.getDays().size());
        for (int index = 0; index < internal.getDays().size(); index++) {
            assertEquals(external.getDays().get(index), internal.getDays().get(index));
        }
        assertEquals(external.getDescription(), internal.getDescription());
        assertEquals(external.getExpiresAfter(), internal.getExpiresAfter());
        assertEquals(KeyUtil.stringToKey(external.getId()), internal.getId());
        assertEquals(external.getName(), internal.getName());
        assertEquals(external.getPrice(), internal.getPrice());
        assertEquals(external.getTime(), internal.getTime());
        assertEquals(external.getTimeBarrier(), internal.getTimeBarrier());
        assertEquals(external.getUses(), internal.getUses());
    }
}
