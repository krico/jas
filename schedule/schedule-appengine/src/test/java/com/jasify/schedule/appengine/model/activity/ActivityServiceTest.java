package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.labs.repackaged.com.google.common.base.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.DateTimeConstants;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import javax.annotation.Nullable;
import java.util.*;

import static junit.framework.TestCase.*;

public class ActivityServiceTest {
    private static final Logger log = LoggerFactory.getLogger(ActivityServiceTest.class);

    private static final String TEST_ACTIVITY_TYPE = "Test Activity Type";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ActivityService activityService;
    private User testUser1;
    private User testUser2;
    private Organization organization1;
    private Organization organization2;
    private ActivityType activityType1OfOrganization1;
    private ActivityType activityType2OfOrganization1;
    private Activity activity1Organization1;
    private Activity activity2Organization1;
    private ActivityPackage activityPackage;

    private Activity createActivity(ActivityType activityType) {
        Activity activity = new Activity(activityType);
        DateTime date = new DateTime();
        date = date.plusDays(1);
        activity.setStart(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate());
        return activity;
    }

    private Organization createOrganization(String name) {
        return new Organization(name);
    }

    private User createUser(String name) {
        User user = new User(name);
        user.setEmail(name + "@test.com");
        user.setRealName("Real " + name);
        return user;
    }

    private void setRepeatDay(RepeatDetails repeatDetails, int jodaDayOfWeek) {
        switch (jodaDayOfWeek) {
            case DateTimeConstants.MONDAY:
                repeatDetails.setMondayEnabled(true);
                break;
            case DateTimeConstants.TUESDAY:
                repeatDetails.setTuesdayEnabled(true);
                break;
            case DateTimeConstants.WEDNESDAY:
                repeatDetails.setWednesdayEnabled(true);
                break;
            case DateTimeConstants.THURSDAY:
                repeatDetails.setThursdayEnabled(true);
                break;
            case DateTimeConstants.FRIDAY:
                repeatDetails.setFridayEnabled(true);
                break;
            case DateTimeConstants.SATURDAY:
                repeatDetails.setSaturdayEnabled(true);
                break;
            case DateTimeConstants.SUNDAY:
                repeatDetails.setSundayEnabled(true);
                break;
            default:
                break;
        }
    }

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        activityService = ActivityServiceFactory.getActivityService();
        organization1 = createOrganization("Org1");
        organization2 = createOrganization("Org2");
        testUser1 = createUser("testUser1");
        testUser2 = createUser("testUser2");
        Datastore.put(organization1, organization2, testUser1, testUser2);
        activityType1OfOrganization1 = new ActivityType("AT1");
        activityType2OfOrganization1 = new ActivityType("AT2");
        activityType1OfOrganization1.setId(Datastore.allocateId(organization1.getId(), ActivityTypeMeta.get()));
        activityType1OfOrganization1.getOrganizationRef().setKey(organization1.getId());
        activityType2OfOrganization1.setId(Datastore.allocateId(organization1.getId(), ActivityTypeMeta.get()));
        activityType2OfOrganization1.getOrganizationRef().setKey(organization1.getId());
        Datastore.put(activityType1OfOrganization1, activityType2OfOrganization1);
        activity1Organization1 = createActivity(activityType1OfOrganization1);
        activity2Organization1 = createActivity(activityType2OfOrganization1);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testAddActivityType() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        assertNotNull(id);
    }

    @Test
    public void testAddActivityTypeSameNameInDifferentOrganizations() throws Exception {
        Key id1 = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        Key id2 = activityService.addActivityType(organization2, new ActivityType(TEST_ACTIVITY_TYPE));
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotSame(id1, id2);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testAddActivityTypeThrowsUniqueNameConstraint() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAddActivityTypeThrowsNotFound() throws Exception {
        activityService.addActivityType(new Organization("any"), new ActivityType(TEST_ACTIVITY_TYPE));
    }

    @Test(expected = FieldValueException.class)
    public void testAddActivityTypeThrowsFieldValueException() throws Exception {
        activityService.addActivityType(organization1, new ActivityType());
    }

    @Test
    public void testGetActivityTypeById() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        ActivityType activityType = activityService.getActivityType(id);
        assertNotNull(activityType);
        assertEquals(id, activityType.getId());
        assertEquals(TEST_ACTIVITY_TYPE, activityType.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypeByIdThrowsEntityNotFound() throws Exception {
        activityService.getActivityType(Datastore.allocateId(ActivityType.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivityTypeByIdThrowsIllegalArgumentException() throws Exception {
        activityService.getActivityType(organization1.getId());
    }

    @Test
    public void testGetActivityTypeByName() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        ActivityType activityType = activityService.getActivityType(organization1, TEST_ACTIVITY_TYPE);
        assertNotNull(activityType);
        assertEquals(id, activityType.getId());
        assertEquals(TEST_ACTIVITY_TYPE, activityType.getName());
    }

    @Test
    public void testGetActivityTypeByNameCaseInsensitive() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.getActivityType(organization1, TEST_ACTIVITY_TYPE.toLowerCase());
    }

    @Test
    public void testGetActivityTypeByNameWithTwoOrganizations() throws Exception {
        Key id1 = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        Key id2 = activityService.addActivityType(organization2, new ActivityType(TEST_ACTIVITY_TYPE));
        ActivityType activityType1 = activityService.getActivityType(organization1, TEST_ACTIVITY_TYPE.toLowerCase());
        ActivityType activityType2 = activityService.getActivityType(organization2, TEST_ACTIVITY_TYPE.toLowerCase());
        assertNotNull(activityType1);
        assertNotNull(activityType2);
        assertEquals(id1, activityType1.getId());
        assertEquals(id2, activityType2.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypeByNameWithNoOrganization() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.getActivityType(new Organization("foo"), TEST_ACTIVITY_TYPE);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypeByNameWithNoName() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.getActivityType(organization1, "x" + TEST_ACTIVITY_TYPE);
    }

    @Test
    public void testGetActivityTypes() throws Exception {
        Datastore.delete(activityType1OfOrganization1.getId(), activityType2OfOrganization1.getId()); //clean slate ;-)
        List<ActivityType> activityTypes = activityService.getActivityTypes(organization1);
        assertNotNull(activityTypes);
        assertTrue(activityTypes.isEmpty());
        int total = 20;
        Set<Key> added = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            added.add(activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE + i)));
        }

        activityTypes = activityService.getActivityTypes(organization1);
        assertNotNull(activityTypes);
        assertEquals(20, activityTypes.size());
        assertEquals(20, added.size());
        for (ActivityType activityType : activityTypes) {
            assertTrue(added.remove(activityType.getId()));
        }
        assertTrue(added.isEmpty());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypesThrowsNotFound() throws Exception {
        activityService.getActivityTypes(new Organization("Foo"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypesThrowsNotFoundWithId() throws Exception {
        Organization foo = new Organization("Foo");
        foo.setId(Datastore.allocateId(Organization.class));
        activityService.getActivityTypes(foo);
    }

    @Test
    public void testUpdateActivityType() throws Exception {
        ActivityType activityType = new ActivityType(TEST_ACTIVITY_TYPE);
        Key id = activityService.addActivityType(organization1, activityType);
        activityType.setName("New Name");
        activityType.setDescription("Description");
        activityType.setPrice(55.0);
        activityType.setCurrency("NZD");
        activityType.setLocation("Location");
        activityType.setMaxSubscriptions(6);
        ActivityType updatedActivityType = activityService.updateActivityType(activityType);
        assertNotNull(updatedActivityType);
        assertEquals(id, updatedActivityType.getId());
        assertEquals("New Name", updatedActivityType.getName());
        assertEquals("Description", updatedActivityType.getDescription());
        assertEquals(55.0, updatedActivityType.getPrice());
        assertEquals("NZD", updatedActivityType.getCurrency());
        assertEquals("Location", updatedActivityType.getLocation());
        assertEquals(6, updatedActivityType.getMaxSubscriptions());
        assertEquals("New Name", activityService.getActivityType(id).getName());
    }

    @Test(expected = UniqueConstraintException.class)
    public void testUpdateActivityTypeThrowsUniqueConstraintException() throws Exception {
        ActivityType activityType1 = new ActivityType(TEST_ACTIVITY_TYPE);
        activityService.addActivityType(organization1, activityType1);
        activityType1.setName("New Name");
        activityType1.setDescription("Description");
        activityService.updateActivityType(activityType1);

        ActivityType activityType2 = new ActivityType(TEST_ACTIVITY_TYPE);
        activityService.addActivityType(organization1, activityType2);
        activityType2.setName("New Name");
        activityType2.setDescription("Description");
        activityService.updateActivityType(activityType2);
    }

    @Test(expected = FieldValueException.class)
    public void testUpdateActivityTypeThrowsFieldValueException() throws Exception {
        ActivityType activityType = new ActivityType();
        activityService.updateActivityType(activityType);
    }

    @Test
    public void testRemoveActivityType() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.removeActivityType(id);
        assertNull(Datastore.getOrNull(id));
    }

    @Test
    public void testRemoveActivityTypeThrowsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        activityService.removeActivityType(null);
    }

    @Test
    public void testRemoveActivityTypeThrowsEntityNotFoundException() throws Exception {
        thrown.expect(EntityNotFoundException.class);
        Key key = Datastore.allocateId(ActivityType.class);
        thrown.expectMessage("ActivityType.id=" + key);
        activityService.removeActivityType(key);
    }

    @Test
    public void testRemoveActivityTypeThrowsOperationException() throws Exception {
        thrown.expect(OperationException.class);
        thrown.expectMessage("ActivityType has activities");
        activityService.addActivity(activity1Organization1, new RepeatDetails());
        activityService.removeActivityType(activityType1OfOrganization1.getId());
    }

    @Test
    public void testAddActivity() throws Exception {
        List<Key> ids = activityService.addActivity(activity1Organization1, new RepeatDetails());
        assertNotNull(ids);
        assertEquals(1, ids.size());
        Key parent = ids.get(0).getParent();
        assertNotNull(parent);
        assertEquals(organization1.getId(), parent);
    }

    @Test
    public void testAddActivityWithNullRepeatDetails() throws Exception {
        List<Key> ids = activityService.addActivity(activity1Organization1, null);
        assertNotNull(ids);
        assertEquals(1, ids.size());
    }

    @Test
    public void testAddActivityWithNullRepeatType() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.repeatType");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(null);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        activityService.addActivity(activity1Organization1, repeatDetails);
    }

    @Test
    public void testActivityStartNull() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.start");
        activity1Organization1.setStart(null);
        activityService.addActivity(activity1Organization1, null);
    }

    @Test
    public void testActivityStartInPast() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.start");
        activity1Organization1.setStart(new DateTime(2000, 1, 1, 10, 0, 0).toDate());
        activityService.addActivity(activity1Organization1, null);
    }

    @Test
    public void testActivityFinishNull() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.finish");
        activity1Organization1.setFinish(null);
        activityService.addActivity(activity1Organization1, null);
    }

    @Test
    public void testActivityFinishBeforeStart() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.finish");
        DateTime finish = new DateTime(activity1Organization1.getStart());
        finish = finish.minusDays(1);
        activity1Organization1.setFinish(finish.toDate());
        activityService.addActivity(activity1Organization1, null);
    }

    @Test
    public void testActivityNegativePrice() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.price");
        activity1Organization1.setPrice(new Double("-1"));
        activityService.addActivity(activity1Organization1, null);
    }

    @Test
    public void testActivityNegativeMaxSubscriptions() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.maxSubscriptions");
        activity1Organization1.setMaxSubscriptions(-1);
        activityService.addActivity(activity1Organization1, null);
    }

    @Test
    public void testAddActivityWithInvalidRepeatEvery() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.repeatEvery");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatEvery(0);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        activityService.addActivity(activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithNullRepeatUntilType() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.repeatUntilType");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        activityService.addActivity(activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithNullRepeatUntilDate() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.untilDate");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Date);
        activityService.addActivity(activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithRepeatUntilDateInPast() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.untilDate");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Date);
        repeatDetails.setUntilDate(new Date(20));
        activityService.addActivity(activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithInvalidRepeatUntilCount() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.untilCount");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(0);
        activityService.addActivity(activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithNoRepeatDays() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.repeatDays");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Weekly);
        repeatDetails.setUntilCount(1);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(1);
        activityService.addActivity(activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithRepeatDailyCount() throws Exception {
        DateTime date = new DateTime();
        date = date.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        date = date.plusDays(1);
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyDate() throws Exception {
        DateTime date1 = new DateTime();
        date1 = date1.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Date);
        DateTime date2 = date1.plusDays(1);
        repeatDetails.setUntilDate(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate());

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyEveryWeek() throws Exception {
        DateTime date1 = new DateTime();
        date1 = date1.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);
        repeatDetails.setRepeatEvery(7); // Every 7 days

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        DateTime date2 = date1.plusDays(7);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyEveryTwoWeeks() throws Exception {
        DateTime date1 = new DateTime();
        date1 = date1.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);
        repeatDetails.setRepeatEvery(14); // Every two weeks

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        DateTime date2 = date1.plusDays(14);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyDoesNotExceedMaximum() throws Exception {
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(50);

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(ActivityService.MaximumRepeatCounter, ids.size());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyCount() throws Exception {
        DateTime date1 = new DateTime();
        date1 = date1.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        DateTime date2 = date1.plusDays(7);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyDate() throws Exception {
        DateTime date1 = new DateTime();
        date1 = date1.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatUntilType.Date);
        DateTime date2 = date1.plusDays(8);
        repeatDetails.setUntilDate(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate());

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        DateTime date3 = date1.plusDays(7);
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyEveryTwoWeeks() throws Exception {
        DateTime date1 = new DateTime();
        date1 = date1.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);
        repeatDetails.setRepeatEvery(2); // Every two weeks

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        DateTime date3 = date1.plusDays(14);
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyTwoDaysEveryTwoWeeks() throws Exception {
        DateTime date1 = new DateTime();
        date1 = date1.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.plusDays(1).getDayOfWeek());
        setRepeatDay(repeatDetails, date1.plusDays(3).getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(4);
        repeatDetails.setRepeatEvery(2); // Every two weeks

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(4, ids.size());
        Activity activity1 = activityService.getActivity(ids.get(0));
        DateTime date2 = date1.plusDays(1);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = activityService.getActivity(ids.get(1));
        DateTime date3 = date2.plusDays(2);
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
        Activity activity3 = activityService.getActivity(ids.get(2));
        DateTime date4 = date2.plusDays(14);
        assertEquals(new DateTime(date4.getYear(), date4.getMonthOfYear(), date4.getDayOfMonth(), 10, 0, 0).toDate(), activity3.getStart());
        assertEquals(new DateTime(date4.getYear(), date4.getMonthOfYear(), date4.getDayOfMonth(), 11, 0, 0).toDate(), activity3.getFinish());
        Activity activity4 = activityService.getActivity(ids.get(3));
        DateTime date5 = date4.plusDays(2);
        assertEquals(new DateTime(date5.getYear(), date5.getMonthOfYear(), date5.getDayOfMonth(), 10, 0, 0).toDate(), activity4.getStart());
        assertEquals(new DateTime(date5.getYear(), date5.getMonthOfYear(), date5.getDayOfMonth(), 11, 0, 0).toDate(), activity4.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyDoesNotExceedMaximum() throws Exception {
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Weekly);
        repeatDetails.setFridayEnabled(true);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(50);

        List<Key> ids = activityService.addActivity(activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(ActivityService.MaximumRepeatCounter, ids.size());
    }

    @Test
    public void testGetActivity() throws Exception {
        List<Key> ids = activityService.addActivity(activity1Organization1, new RepeatDetails());
        Activity activity = activityService.getActivity(ids.get(0));
        assertNotNull(activity);
        assertEquals(activityType1OfOrganization1.getName(), activity.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityThrowsEntityNotFoundException() throws Exception {
        Key id = Datastore.allocateId(Activity.class);
        activityService.getActivity(id);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivitiesByOrganizationNotFound() throws Exception {
        try {
            Datastore.delete(organization1.getId());
        } catch (Exception e) {
            fail();
        }
        activityService.getActivities(organization1);
    }

    @Test
    public void testGetActivitiesByOrganization() throws Exception {
        List<Activity> activities = activityService.getActivities(organization1);
        assertNotNull(activities);
        assertTrue(activities.isEmpty());
        int total = 20;
        Set<Key> added = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            if (i % 3 == 0) {
                added.addAll(activityService.addActivity(activity1Organization1, new RepeatDetails()));
            } else {
                added.addAll(activityService.addActivity(activity2Organization1, new RepeatDetails()));
            }
        }

        activities = activityService.getActivities(organization1);
        assertNotNull(activities);
        assertEquals(20, activities.size());
        assertEquals(20, added.size());
        for (Activity activity : activities) {
            assertTrue(added.remove(activity.getId()));
        }
        assertTrue(added.isEmpty());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivitiesByActivityTypeNotFound() throws Exception {
        try {
            Datastore.delete(activityType1OfOrganization1.getId());
        } catch (Exception e) {
            fail();
        }
        activityService.getActivities(activityType1OfOrganization1);
    }

    @Test
    public void testGetActivitiesByActivityType() throws Exception {
        List<Activity> activities = activityService.getActivities(activityType1OfOrganization1);
        assertNotNull(activities);
        assertTrue(activities.isEmpty());

        activities = activityService.getActivities(activityType2OfOrganization1);
        assertNotNull(activities);
        assertTrue(activities.isEmpty());

        int total = 20;
        Set<Key> addedType1 = new HashSet<>();
        Set<Key> addedType2 = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            if (i % 3 == 0) {
                addedType1.addAll(activityService.addActivity(activity1Organization1, new RepeatDetails()));
            } else {
                addedType2.addAll(activityService.addActivity(activity2Organization1, new RepeatDetails()));
            }
        }

        activities = activityService.getActivities(activityType1OfOrganization1);
        assertNotNull(activities);
        assertEquals(addedType1.size(), activities.size());
        for (Activity activity : activities) {
            assertTrue(addedType1.remove(activity.getId()));
        }
        assertTrue(addedType1.isEmpty());

        activities = activityService.getActivities(activityType2OfOrganization1);
        assertNotNull(activities);
        assertEquals(addedType2.size(), activities.size());
        for (Activity activity : activities) {
            assertTrue(addedType2.remove(activity.getId()));
        }
        assertTrue(addedType2.isEmpty());
    }

    @Test
    public void testUpdateActivity() throws Exception {
        List<Key> ids = activityService.addActivity(activity1Organization1, new RepeatDetails());
        Date expected = activity1Organization1.getCreated();
        activity1Organization1.setName("New Name");
        activity1Organization1.setDescription("Description");
        activity1Organization1.setCurrency("CHF");
        activity1Organization1.setMaxSubscriptions(20);
        activity1Organization1.setSubscriptionCount(10);
        activity1Organization1.setLocation("Location");

        DateTime start = new DateTime(activity1Organization1.getStart());
        DateTime finish = new DateTime(activity1Organization1.getFinish());

        activity1Organization1.setCreated(new Date(99));
        activity1Organization1.setModified(new Date(25));
        long before = System.currentTimeMillis();

        Activity updatedActivity = activityService.updateActivity(activity1Organization1);

        assertNotNull(updatedActivity);
        assertEquals(ids.get(0), updatedActivity.getId());
        assertEquals("New Name", updatedActivity.getName());
        assertEquals("Description", updatedActivity.getDescription());

        Activity fetched = activityService.getActivity(ids.get(0));
        assertEquals("New Name", fetched.getName());
        assertEquals("Description", fetched.getDescription());
        assertEquals("CHF", fetched.getCurrency());
        assertEquals(20, fetched.getMaxSubscriptions());
        assertEquals(10, fetched.getSubscriptionCount());
        assertEquals(start.toDate(), fetched.getStart());
        assertEquals(finish.toDate(), fetched.getFinish());
        assertEquals("Location", fetched.getLocation());
        assertEquals(expected, fetched.getCreated());
        assertTrue(before <= fetched.getModified().getTime());
    }

    @Test
    public void testRemoveActivity() throws Exception {
        List<Key> ids = activityService.addActivity(activity1Organization1, new RepeatDetails());
        activityService.removeActivity(ids.get(0));
        assertNull(Datastore.getOrNull(ids.get(0)));
    }

    @Test
    public void testRemoveActivityThrowsOperationException() throws Exception {
        thrown.expect(OperationException.class);
        thrown.expectMessage("Activity has subscriptions");
        List<Key> ids = activityService.addActivity(activity1Organization1, new RepeatDetails());
        activityService.subscribe(testUser1, activity1Organization1);
        activityService.removeActivity(ids.get(0));
    }

    @Test
    public void testSubscribe() throws Exception {
        activityService.addActivity(activity1Organization1, new RepeatDetails());
        Subscription subscription = activityService.subscribe(testUser1, activity1Organization1);
        assertNotNull(subscription);
        assertEquals(testUser1.getId(), subscription.getUserRef().getKey());
        assertEquals(activity1Organization1.getId(), subscription.getActivityRef().getKey());
        assertEquals(1, activity1Organization1.getSubscriptionCount());
        List<Subscription> modelList = activity1Organization1.getSubscriptionListRef().getModelList();
        assertEquals(1, modelList.size());
        assertEquals(subscription.getId(), modelList.get(0).getId());
    }

    @Test
    public void testSubscribeNotifiesIfUserNameIsNull() throws Exception {
        activityService.addActivity(activity1Organization1, new RepeatDetails());
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        service.clearSentMessages();
        User user = new User();
        Datastore.put(user);
        activityService.subscribe(user, activity1Organization1);
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(0, sentMessages.size());
    }

    @Test
    public void testSubscribeNotifiesIfActivityNameIsNull() throws Exception {
        activity1Organization1.setName(null);
        activityService.addActivity(activity1Organization1, new RepeatDetails());
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        service.clearSentMessages();
        activityService.subscribe(testUser1, activity1Organization1);
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(0, sentMessages.size());
    }

    @Test
    public void testOversubscribe() throws Exception {
        thrown.expect(OperationException.class);
        thrown.expectMessage("Activity fully subscribed");
        activity1Organization1.setMaxSubscriptions(1);
        activityService.addActivity(activity1Organization1, null);
        activityService.subscribe(testUser1, activity1Organization1);
        activityService.subscribe(testUser2, activity1Organization1);
    }

    @Test
    public void testSubscribeForZeroMaxSubscriptions() throws Exception {
        activity1Organization1.setMaxSubscriptions(0);
        activityService.addActivity(activity1Organization1, null);
        assertNotNull(activityService.subscribe(testUser1, activity1Organization1));
        assertNotNull(activityService.subscribe(testUser2, activity1Organization1));
        assertEquals(2, activityService.getActivity(activity1Organization1.getId()).getSubscriptionCount());
    }

    @Test
    public void testSubscribeTwiceForSameUser() throws Exception {
        thrown.expect(UniqueConstraintException.class);
        thrown.expectMessage("User already subscribed");
        activityService.addActivity(activity1Organization1, null);
        activityService.subscribe(testUser1, activity1Organization1);
        activityService.subscribe(testUser1, activity1Organization1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSubscribeThrowsEntityNotFoundException() throws Exception {
        User user = new User("TestUser");
        user.setId(Datastore.allocateId(User.class));
        activityService.subscribe(user, activity1Organization1);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testSubscribeTwice() throws Exception {
        activityService.addActivity(activity1Organization1, new RepeatDetails());
        activityService.subscribe(testUser1, activity1Organization1);
        activityService.subscribe(testUser1, activity1Organization1);
    }

    @Test
    public void testCancel() throws Exception {
        activityService.addActivity(activity1Organization1, new RepeatDetails());
        Subscription subscription = activityService.subscribe(testUser1.getId(), activity1Organization1.getId());

        // cache it in
        activity1Organization1.getSubscriptionListRef().getModelList();

        activityService.cancel(subscription);

        activity1Organization1 = activityService.getActivity(activity1Organization1.getId());

        assertEquals(0, activity1Organization1.getSubscriptionCount());
        List<Subscription> modelList = activity1Organization1.getSubscriptionListRef().getModelList();
        assertTrue(modelList.isEmpty());
        assertNull(Datastore.getOrNull(subscription.getId()));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCanceThrowsEntityNotFoundExceptionl() throws Exception {
        Subscription subscription = new Subscription();
        subscription.setId(Datastore.allocateId(Subscription.class));
        activityService.cancel(subscription);
    }

    @Test
    public void testGetActivities() throws Exception {
        activityService.addActivity(activity1Organization1, new RepeatDetails());
        Subscription subscription1 = activityService.subscribe(testUser1.getId(), activity1Organization1.getId());
        Subscription subscription2 = activityService.subscribe(testUser2, activity1Organization1);
        List<Subscription> subscriptions = activityService.getSubscriptions(activity1Organization1);
        assertNotNull(subscriptions);
        assertEquals(2, subscriptions.size());
        Set<Key> ids = new HashSet<>();
        for (Subscription subscription : subscriptions) {
            ids.add(subscription.getId());
        }
        assertTrue(ids.contains(subscription1.getId()));
        assertTrue(ids.contains(subscription2.getId()));
    }

    @Test
    public void testCreateActivityPackage() throws Exception {
        activityService.addActivity(activity1Organization1, null);
        activityService.addActivity(activity2Organization1, null);

        ActivityPackage ap = new ActivityPackage();
        ap.getOrganizationRef().setKey(organization1.getId());
        ap.setCurrency("USD");
        ap.setDescription("Super package");
        ap.setMaxExecutions(0);
        ap.setName("Supa");
        ap.setPrice(20d);
        ap.setItemCount(2);

        Key id = activityService.addActivityPackage(ap, Arrays.asList(activity1Organization1, activity2Organization1));
        assertNotNull(id);
        activityPackage = activityService.getActivityPackage(id);
        Set<Key> keys = activityPackage.getActivityKeys();
        assertNotNull(keys);
        assertEquals(2, keys.size());
        assertTrue(keys.contains(activity1Organization1.getId()));
        assertTrue(keys.contains(activity2Organization1.getId()));

        List<Activity> activities = activityPackage.getActivities();
        assertNotNull(activities);
        assertEquals(2, activities.size());

        boolean found = false;
        for (Activity activity : activities) {
            found = activity.getId().equals(activity1Organization1.getId());
            if (found) break;
        }
        assertTrue(found);

        found = false;
        for (Activity activity : activities) {
            found = activity.getId().equals(activity2Organization1.getId());
            if (found) break;
        }
        assertTrue(found);
    }

    @Test
    public void testUpdateActivityPackage() throws Exception {
        testCreateActivityPackage();
        activityPackage.setDescription("New Desc");
        Date created = activityPackage.getCreated();
        activityPackage.setCreated(new Date(8282123));
        activityPackage.setModified(new Date(555));
        activityPackage.setItemCount(99);
        activityPackage.setPrice(999d);
        activityPackage.setExecutionCount(826);
        activityPackage.setName("New Name");
        activityPackage.setCurrency("BRL");
        activityPackage.setMaxExecutions(200);
        activityPackage.setValidFrom(new Date(12345678));
        activityPackage.setValidUntil(new Date(22233344));
        Key expectedOrgId = activityPackage.getOrganizationRef().getKey();
        activityPackage.getOrganizationRef().setKey(null);

        List<ActivityPackage> fetchedPackages = Lists.newArrayList();
        fetchedPackages.add(activityService.updateActivityPackage(activityPackage));

        long modified = System.currentTimeMillis();

        fetchedPackages.add(activityService.getActivityPackage(activityPackage.getId()));
        for (ActivityPackage fetched : fetchedPackages) {
            assertNotNull(fetched);
            assertEquals("New Desc", fetched.getDescription());
            assertEquals(99, fetched.getItemCount());
            assertEquals(999d, fetched.getPrice());
            //achtung
            assertEquals("Execution count should not be updated", 0, fetched.getExecutionCount());
            assertEquals("original ap unchanged", 826, activityPackage.getExecutionCount());
            assertEquals("New Name", fetched.getName());
            assertEquals("BRL", fetched.getCurrency());
            assertEquals(200, fetched.getMaxExecutions());
            assertEquals(new Date(12345678), fetched.getValidFrom());
            assertEquals(new Date(22233344), fetched.getValidUntil());

            assertEquals(created, fetched.getCreated());
            long fetchedModified = fetched.getModified().getTime();
            assertTrue(fetchedModified <= modified && fetchedModified > (modified - 1000));
            assertEquals(expectedOrgId, fetched.getOrganizationRef().getKey());
        }
    }

    @Test
    public void testCreateAddAndRemoveFromActivityPackage() throws Exception {
        testCreateActivityPackage();

        activityService.removeActivityFromActivityPackage(activityPackage, activity2Organization1);

        activityPackage = activityService.getActivityPackage(activityPackage.getId());
        Set<Key> keys = activityPackage.getActivityKeys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains(activity1Organization1.getId()));

        activityService.addActivityToActivityPackage(activityPackage, activity2Organization1);

        activityPackage = activityService.getActivityPackage(activityPackage.getId());
        keys = activityPackage.getActivityKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains(activity1Organization1.getId()));
        assertTrue(keys.contains(activity2Organization1.getId()));
    }

    @Test
    public void testSubscribeToActivityPackage() throws Exception {
        testCreateActivityPackage();
        ActivityPackageExecution ret = activityService.subscribe(testUser1, activityPackage, Arrays.asList(activity2Organization1, activity1Organization1));
        assertNotNull(ret);
        assertEquals(activityPackage.getId(), ret.getActivityPackageRef().getKey());
        assertEquals(testUser1.getId(), ret.getUserRef().getKey());
        assertNull(ret.getTransferRef().getKey());
        List<ActivityPackageSubscription> subscriptions = ret.getSubscriptionListRef().getModelList();
        assertEquals(2, subscriptions.size());
        HashSet<Key> activities = new HashSet<>();
        for (ActivityPackageSubscription subscription : subscriptions) {
            assertEquals(ret.getId(), subscription.getActivityPackageExecutionRef().getKey());
            assertNull(subscription.getTransferRef().getKey());
            assertEquals(testUser1.getId(), subscription.getUserRef().getKey());
            assertNotNull(subscription.getActivityRef().getKey());
            activities.add(subscription.getActivityRef().getKey());
        }
        assertTrue(activities.contains(activity1Organization1.getId()));
        assertTrue(activities.contains(activity2Organization1.getId()));
        log.info("{}", ModelMetadataUtil.dumpDb(new StringBuilder("DB DUMP\n")));
    }

    @Test
    public void testGetActivityPackages() throws Exception {
        testCreateActivityPackage();
        Key ap1Org1 = activityPackage.getId();
        testCreateActivityPackage();
        Key ap2Org1 = activityPackage.getId();

        ActivityPackage ap = new ActivityPackage();
        ap.getOrganizationRef().setKey(organization2.getId());
        ap.setCurrency("USD");
        ap.setDescription("Super package");
        ap.setMaxExecutions(0);
        ap.setName("Supa");
        ap.setPrice(20d);
        ap.setItemCount(2);
        ActivityType activityType = new ActivityType("ATO2");
        activityType.setId(Datastore.allocateId(organization2.getId(), ActivityTypeMeta.get()));
        activityType.getOrganizationRef().setKey(organization2.getId());
        Datastore.put(activityType);

        Activity activity = createActivity(activityType);
        Datastore.put(activity);
        Key ap1Org2 = activityService.addActivityPackage(ap, Arrays.asList(activity));


        //Now we do the real test work
        List<ActivityPackage> activityPackages = activityService.getActivityPackages(organization1);
        assertNotNull(activityPackages);
        assertEquals(2, activityPackages.size());
        List<Key> ids = Lists.transform(activityPackages, new Function<ActivityPackage, Key>() {
            @Nullable
            @Override
            public Key apply(ActivityPackage activityPackage) {
                return activityPackage.getId();
            }
        });
        assertTrue(ids.contains(ap1Org1));
        assertTrue(ids.contains(ap2Org1));

        activityPackages = activityService.getActivityPackages(organization2.getId());
        assertNotNull(activityPackages);
        assertEquals(1, activityPackages.size());
        ids = Lists.transform(activityPackages, new Function<ActivityPackage, Key>() {
            @Nullable
            @Override
            public Key apply(ActivityPackage activityPackage) {
                return activityPackage.getId();
            }
        });
        assertTrue(ids.contains(ap1Org2));
    }
}
