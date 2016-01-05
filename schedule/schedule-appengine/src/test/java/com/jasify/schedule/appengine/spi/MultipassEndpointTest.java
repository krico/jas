package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.multipass.MultipassDao;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.multipass.*;
import com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter;
import com.jasify.schedule.appengine.model.multipass.filter.DayFilter;
import com.jasify.schedule.appengine.model.multipass.filter.TimeFilter;
import com.jasify.schedule.appengine.spi.dm.JasMultipassRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;

/**
 * @author wszarmach
 * @since 12/11/15.
 */
public class MultipassEndpointTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MultipassEndpoint endpoint;

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        endpoint = new MultipassEndpoint();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    private void equals(ActivityTypeFilter activityTypeFilter1, ActivityTypeFilter activityTypeFilter2) {
        if (activityTypeFilter1 != null) {
            assertNotNull(activityTypeFilter2);
            assertEquals(activityTypeFilter1.getActivityTypeIds().size(), activityTypeFilter2.getActivityTypeIds().size());
            for (int i = 0; i < activityTypeFilter1.getActivityTypeIds().size(); i++) {
                assertEquals(activityTypeFilter1.getActivityTypeIds().get(i).getId(), activityTypeFilter2.getActivityTypeIds().get(i).getId());
            }
        } else {
            assertNull(activityTypeFilter2);
        }
    }

    private void equals(DayFilter dayFilter1, DayFilter dayFilter2) {
        if (dayFilter1 != null) {
            assertNotNull(dayFilter2);
            assertEquals(dayFilter1.getDaysOfWeek().size(), dayFilter2.getDaysOfWeek().size());
            for (int i = 0; i < dayFilter1.getDaysOfWeek().size(); i++) {
                assertEquals(dayFilter1.getDaysOfWeek().get(i), dayFilter2.getDaysOfWeek().get(i));
            }
        } else {
            assertNull(dayFilter2);
        }
    }

    private void equals(TimeFilter timeFilter1, TimeFilter timeFilter2) {
        if (timeFilter1 != null) {
            assertNotNull(timeFilter2);
            assertEquals(timeFilter1.getComparisonType(), timeFilter2.getComparisonType());
            assertEquals(timeFilter1.getHour(), timeFilter2.getHour());
            assertEquals(timeFilter1.getMinute(), timeFilter2.getMinute());

        } else {
            assertNull(timeFilter2);
        }
    }

    private void equals(Multipass multipass1, Multipass multipass2) {
        assertEquals(multipass1.getCurrency(), multipass2.getCurrency());
        equals(multipass1.getActivityTypeFilter(), multipass2.getActivityTypeFilter());
        assertEquals(multipass1.getDescription(), multipass2.getDescription());
        equals(multipass1.getDayFilter(), multipass2.getDayFilter());
        assertEquals(multipass1.getExpiresAfter(), multipass2.getExpiresAfter());
        assertEquals(multipass1.getId(), multipass2.getId());
        assertEquals(multipass1.getLcName().toLowerCase(), multipass2.getLcName());
        assertEquals(multipass1.getName(), multipass2.getName());
        assertEquals(multipass1.getOrganizationRef().getKey(), multipass2.getOrganizationRef().getKey());
        assertEquals(multipass1.getPrice(), multipass2.getPrice());
        equals(multipass1.getTimeFilter(), multipass2.getTimeFilter());
        assertEquals(multipass1.getUses(), multipass2.getUses());
    }

    @Test
    public void testAddNullJasAddMultipassRequest() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request == null");
        endpoint.add(newAdminCaller(1), null);
    }

    @Test
    public void testAddNullMultipass() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.multipass == null");
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.add(newAdminCaller(1), jasMultipassRequest);
    }

    @Test
    public void testAddNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(new Multipass());
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.add(newCaller(1), jasMultipassRequest);
    }

    @Test
    public void testAddNullOrganization() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.organizationId == null");
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(new Multipass());
        endpoint.add(newAdminCaller(1), jasMultipassRequest);
    }

    @Test
    public void testAddUnkownRequestOrganization() throws Exception {
        thrown.expect(BadRequestException.class);
        Key organizationId = Datastore.allocateId(Organization.class);
        thrown.expectMessage("No entity was found matching the key: " + organizationId);
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(new Multipass());
        jasMultipassRequest.setOrganizationId(organizationId);
        endpoint.add(newAdminCaller(1), jasMultipassRequest);
    }

    @Test
    public void testAddNullName() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Multipass.name");
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(new Multipass());
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.add(newAdminCaller(1), jasMultipassRequest);
    }

    @Test
    public void testAddDuplicate() throws Exception {
        thrown.expect(BadRequestException.class);
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        thrown.expectMessage("Multipass.name=" + jasMultipassRequest.getMultipass().getName() + ", Organization.id=" + jasMultipassRequest.getOrganizationId());
        endpoint.add(newAdminCaller(1), jasMultipassRequest);
        jasMultipassRequest.getMultipass().setId(null);
        endpoint.add(newAdminCaller(1), jasMultipassRequest);
    }

    @Test
    public void testAdd() throws Exception {
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.add(newAdminCaller(1), jasMultipassRequest);
    }

    @Test
    public void testAddWithActivityTypeFilter() throws Exception {
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        ActivityTypeFilter activityTypeFilter = new ActivityTypeFilter();
        activityTypeFilter.setActivityTypeIds(new ArrayList<Key>());
        activityTypeFilter.getActivityTypeIds().add(Datastore.allocateId(ActivityType.class));
        activityTypeFilter.getActivityTypeIds().add(Datastore.allocateId(ActivityType.class));
        jasMultipassRequest.getMultipass().setActivityTypeFilter(activityTypeFilter);
        Multipass result = endpoint.add(newAdminCaller(1), jasMultipassRequest);
        equals(activityTypeFilter, result.getActivityTypeFilter());
    }

    @Test
    public void testAddWithDayFilter() throws Exception {
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        DayFilter dayFilter = new DayFilter();
        dayFilter.setDaysOfWeek(new ArrayList<DayFilter.DayOfWeekEnum>());
        dayFilter.getDaysOfWeek().add(DayFilter.DayOfWeekEnum.Monday);
        dayFilter.getDaysOfWeek().add(DayFilter.DayOfWeekEnum.Tuesday);
        jasMultipassRequest.getMultipass().setDayFilter(dayFilter);
        Multipass result = endpoint.add(newAdminCaller(1), jasMultipassRequest);
        equals(dayFilter, result.getDayFilter());
    }

    @Test
    public void testAddWithTimeFilter() throws Exception {
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        TimeFilter timeFilter = new TimeFilter();
        timeFilter.setComparisonType(TimeFilter.ComparisonTypeEnum.After);
        timeFilter.setHour(7);
        timeFilter.setMinute(20);
        jasMultipassRequest.getMultipass().setTimeFilter(timeFilter);
        Multipass result = endpoint.add(newAdminCaller(1), jasMultipassRequest);
        equals(timeFilter, result.getTimeFilter());
    }

    @Test
    public void testAddWithMultipleFilters() throws Exception {
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        DayFilter dayFilter = new DayFilter();
        dayFilter.setDaysOfWeek(new ArrayList<DayFilter.DayOfWeekEnum>());
        dayFilter.getDaysOfWeek().add(DayFilter.DayOfWeekEnum.Monday);
        dayFilter.getDaysOfWeek().add(DayFilter.DayOfWeekEnum.Wednesday);
        jasMultipassRequest.getMultipass().setDayFilter(dayFilter);
        TimeFilter timeFilter = new TimeFilter();
        timeFilter.setComparisonType(TimeFilter.ComparisonTypeEnum.After);
        timeFilter.setHour(20);
        jasMultipassRequest.getMultipass().setTimeFilter(timeFilter);
        Multipass result = endpoint.add(newAdminCaller(1), jasMultipassRequest);
        equals(dayFilter, result.getDayFilter());
        equals(timeFilter, result.getTimeFilter());
    }

    @Test
    public void testGetNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("multipassId == null");
        endpoint.get(newAdminCaller(1), null);
    }

    @Test
    public void testGetNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.get(newCaller(1), Datastore.allocateId(Multipass.class));
    }

    @Test
    public void testGetUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(Multipass.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.get(newAdminCaller(1), id);
    }

    @Test
    public void testGet() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        Multipass result = endpoint.get(newAdminCaller(1), multipass.getId());
        equals(multipass, result);
    }

    @Test
    public void testQueryNullOrganizationId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("organizationId == null");
        endpoint.query(newCaller(1), null);
    }

    @Test
    public void testQueryUnknownOrganizationId() throws Exception {
        List<Multipass> result = endpoint.query(newCaller(1), Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testQuery() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        TestHelper.createMultipass(organization, true);
        TestHelper.createMultipass(organization, true);
        TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        List<Multipass> result = endpoint.query(newCaller(1), organization.getId());
        assertEquals(2, result.size());
    }

    @Test
    public void testRemoveNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("multipassId == null");
        endpoint.remove(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.remove(newCaller(1), Datastore.allocateId(Multipass.class));
    }

    @Test
    public void testRemoveUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(Multipass.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.remove(newAdminCaller(1), id);
    }

    @Test
    public void testRemove() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, true);
        MultipassDao multipassDao = new MultipassDao();
        assertFalse(multipassDao.getByOrganization(organization.getId()).isEmpty());
        endpoint.remove(newAdminCaller(1), multipass.getId());
        assertTrue(multipassDao.getByOrganization(organization.getId()).isEmpty());
    }

    @Test
    public void testUpdateNullRequestMultipass() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.multipass == null");
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        endpoint.update(newAdminCaller(1), Datastore.allocateId(Multipass.class), jasMultipassRequest);
    }

    @Test
    public void testUpdateNullMultipass() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.multipass == null");
        endpoint.update(newAdminCaller(1), Datastore.allocateId(Multipass.class), new JasMultipassRequest());
    }

    @Test
    public void testUpdateNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(new Multipass());
        endpoint.update(newAdminCaller(1), null, jasMultipassRequest);
    }

    @Test
    public void testUpdateNullName() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Multipass.name");
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        multipass.setName(null);
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(multipass);
        endpoint.update(newAdminCaller(1), multipass.getId(), jasMultipassRequest);
    }

    @Test
    public void testUpdateNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(multipass);
        endpoint.update(newCaller(1), multipass.getId(), jasMultipassRequest);
    }

    @Test
    public void testUpdateUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(Multipass.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(new Multipass());
        endpoint.update(newAdminCaller(1), id, jasMultipassRequest);
    }

    @Test
    public void testUpdate() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(multipass);
        Multipass result = endpoint.update(newAdminCaller(1), multipass.getId(), jasMultipassRequest);
        equals(multipass, result);
    }

    @Test
    public void testUpdateWithFilters() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        multipass.setActivityTypeFilter(new ActivityTypeFilter());
        multipass.setDayFilter(new DayFilter());
        multipass.setTimeFilter(new TimeFilter());
        Datastore.put(multipass);

        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        jasMultipassRequest.setMultipass(multipass);
        jasMultipassRequest.getMultipass().setActivityTypeFilter(new ActivityTypeFilter());
        jasMultipassRequest.getMultipass().setTimeFilter(new TimeFilter());
        Multipass result = endpoint.update(newAdminCaller(1), multipass.getId(), jasMultipassRequest);
        equals(multipass, result);
    }
}
