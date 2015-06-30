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
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import javax.annotation.Nullable;
import java.util.*;

import static junit.framework.TestCase.*;

public class ActivityServiceTest {
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
    private ActivityPackage activityPackage10Organization;
    private ActivityPackageExecution activityPackageExecution10Organization;
    //  private ActivityPackage activityPackage;
    private ActivityPackageExecution activityPackageExecution;

    private Activity createActivity(ActivityType activityType) {
        Activity activity = new Activity(activityType);
        DateTime date = new DateTime();
        date = date.plusDays(1);
        activity.setStart(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate());
        return activity;
    }

    private ActivityPackage createActivityPackage(Organization organization) {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.getOrganizationRef().setKey(organization.getId());
        activityPackage.setDescription("New Desc");
        DateTime date = new DateTime();
        activityPackage.setCreated(date.toDate());
        activityPackage.setModified(date.toDate());
        activityPackage.setItemCount(1);
        activityPackage.setPrice(999d);
        activityPackage.setExecutionCount(0);
        activityPackage.setName("New Name");
        activityPackage.setCurrency("BRL");
        activityPackage.setMaxExecutions(200);
        activityPackage.setValidFrom(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate());
        activityPackage.setValidUntil(new DateTime(date.getYear(), date.getMonthOfYear() + 1, 25, 10, 0, 0).toDate());
        return activityPackage;
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
        activityPackage10Organization = createActivityPackage(organization1);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test(expected = FieldValueException.class)
    public void testUpdateActivityTypeThrowsFieldValueException() throws Exception {
        ActivityType activityType = new ActivityType();
        activityService.updateActivityType(activityType);
    }

    @Test
    public void testRemoveActivityTypeThrowsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        activityService.removeActivityType(null);
    }

    @Test
    public void testAddActivity() throws Exception {
        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, new RepeatDetails());
        assertNotNull(ids);
        assertEquals(1, ids.size());
        Key parent = ids.get(0).getParent();
        assertNotNull(parent);
        assertEquals(organization1.getId(), parent);
    }

    @Test
    public void testAddActivityWithNullRepeatDetails() throws Exception {
        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
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
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
    }

    @Test
    public void testActivityStartNull() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.start");
        activity1Organization1.setStart(null);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
    }

    @Test
    public void testActivityStartInPast() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.start");
        activity1Organization1.setStart(new DateTime(2000, 1, 1, 10, 0, 0).toDate());
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
    }

    @Test
    public void testActivityFinishNull() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.finish");
        activity1Organization1.setFinish(null);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
    }

    @Test
    public void testActivityFinishBeforeStart() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.finish");
        DateTime finish = new DateTime(activity1Organization1.getStart());
        finish = finish.minusDays(1);
        activity1Organization1.setFinish(finish.toDate());
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
    }

    @Test
    public void testActivityNegativePrice() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.price");
        activity1Organization1.setPrice(new Double("-1"));
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
    }

    @Test
    public void testActivityNegativeMaxSubscriptions() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("Activity.maxSubscriptions");
        activity1Organization1.setMaxSubscriptions(-1);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
    }

    @Test
    public void testAddActivityWithInvalidRepeatEvery() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.repeatEvery");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatEvery(0);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithNullRepeatUntilType() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.repeatUntilType");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithNullRepeatUntilDate() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.untilDate");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Date);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithRepeatUntilDateInPast() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.untilDate");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Date);
        repeatDetails.setUntilDate(new Date(20));
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithInvalidRepeatUntilCount() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("RepeatDetails.untilCount");
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(0);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
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
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
    }

    @Test
    public void testAddActivityWithRepeatDailyCount() throws Exception {
        DateTime date = new DateTime();
        date = date.plusDays(1);

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(4, ids.size());
        Activity activity1 = Datastore.get(ActivityMeta.get(), ids.get(0));
        DateTime date2 = date1.plusDays(1);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());
        Activity activity2 = Datastore.get(ActivityMeta.get(), ids.get(1));
        DateTime date3 = date2.plusDays(2);
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
        Activity activity3 = Datastore.get(ActivityMeta.get(), ids.get(2));
        DateTime date4 = date2.plusDays(14);
        assertEquals(new DateTime(date4.getYear(), date4.getMonthOfYear(), date4.getDayOfMonth(), 10, 0, 0).toDate(), activity3.getStart());
        assertEquals(new DateTime(date4.getYear(), date4.getMonthOfYear(), date4.getDayOfMonth(), 11, 0, 0).toDate(), activity3.getFinish());
        Activity activity4 = Datastore.get(ActivityMeta.get(), ids.get(3));
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

        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, repeatDetails);
        assertNotNull(ids);
        assertEquals(ActivityService.MaximumRepeatCounter, ids.size());
    }

    @Test
    public void testUpdateActivity() throws Exception {
        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, new RepeatDetails());
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

        Activity fetched = Datastore.get(ActivityMeta.get(), ids.get(0));
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
        List<Key> ids = activityService.addActivity(activityType1OfOrganization1, activity1Organization1, new RepeatDetails());
        activityService.removeActivity(activity1Organization1);
        assertNull(Datastore.getOrNull(ids.get(0)));
    }

    @Test
    public void testSubscribe() throws Exception {
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, new RepeatDetails());
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
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, new RepeatDetails());
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
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, new RepeatDetails());
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
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
        activityService.subscribe(testUser1, activity1Organization1);
        activityService.subscribe(testUser2, activity1Organization1);
    }

    @Test
    public void testSubscribeForZeroMaxSubscriptions() throws Exception {
        activity1Organization1.setMaxSubscriptions(0);
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
        assertNotNull(activityService.subscribe(testUser1, activity1Organization1));
        assertNotNull(activityService.subscribe(testUser2, activity1Organization1));
        assertEquals(2, Datastore.get(ActivityMeta.get(), activity1Organization1.getId()).getSubscriptionCount());
    }

    @Test
    public void testCancel() throws Exception {
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, new RepeatDetails());
        Subscription subscription = activityService.subscribe(testUser1, activity1Organization1);

        // cache it in
        activity1Organization1.getSubscriptionListRef().getModelList();

        activityService.cancel(subscription);

        activity1Organization1 = Datastore.get(ActivityMeta.get(), activity1Organization1.getId());

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
    public void testCreateActivityPackage() throws Exception {
        activityService.addActivity(activityType1OfOrganization1, activity1Organization1, null);
        activityService.addActivity(activityType2OfOrganization1, activity2Organization1, null);

        activityPackage10Organization.setItemCount(2);

        Key id = activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1, activity2Organization1));
        assertNotNull(id);
        ActivityPackage ap = Datastore.get(ActivityPackageMeta.get(), id);
        Set<Key> keys = ap.getActivityKeys();
        assertNotNull(keys);
        assertEquals(2, keys.size());
        assertTrue(keys.contains(activity1Organization1.getId()));
        assertTrue(keys.contains(activity2Organization1.getId()));

        List<Activity> activities = ap.getActivities();
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
        activityPackage10Organization.setDescription("New Desc");
        DateTime dateTime = new DateTime();
        Date created = activityPackage10Organization.getCreated();
        activityPackage10Organization.setCreated(dateTime.plusHours(1).toDate());
        activityPackage10Organization.setModified(new Date(555));
        activityPackage10Organization.setItemCount(99);
        activityPackage10Organization.setPrice(999d);
        activityPackage10Organization.setExecutionCount(826);
        activityPackage10Organization.setName("New Name");
        activityPackage10Organization.setCurrency("BRL");
        activityPackage10Organization.setMaxExecutions(200);
        activityPackage10Organization.setValidFrom(dateTime.plusHours(2).toDate());
        activityPackage10Organization.setValidUntil(dateTime.plusHours(3).toDate());
        Key expectedOrgId = activityPackage10Organization.getOrganizationRef().getKey();
        activityPackage10Organization.getOrganizationRef().setKey(null);

        List<ActivityPackage> fetchedPackages = Lists.newArrayList();
        fetchedPackages.add(activityService.updateActivityPackage(activityPackage10Organization));

        long modified = System.currentTimeMillis();

        fetchedPackages.add(Datastore.get(ActivityPackageMeta.get(), activityPackage10Organization.getId()));
        for (ActivityPackage fetched : fetchedPackages) {
            assertNotNull(fetched);
            assertEquals("New Desc", fetched.getDescription());
            assertEquals(99, fetched.getItemCount());
            assertEquals(999d, fetched.getPrice());
            //achtung
            assertEquals("Execution count should not be updated", 0, fetched.getExecutionCount());
            assertEquals("original ap unchanged", 826, activityPackage10Organization.getExecutionCount());
            assertEquals("New Name", fetched.getName());
            assertEquals("BRL", fetched.getCurrency());
            assertEquals(200, fetched.getMaxExecutions());
            assertEquals(dateTime.plusHours(2).toDate(), fetched.getValidFrom());
            assertEquals(dateTime.plusHours(3).toDate(), fetched.getValidUntil());

            assertEquals(created, fetched.getCreated());
            long fetchedModified = fetched.getModified().getTime();
            assertTrue(fetchedModified <= modified && fetchedModified > (modified - 1000));
            assertEquals(expectedOrgId, fetched.getOrganizationRef().getKey());
        }
    }
//
//    @Test
//    public void testUpdateActivityPackage() throws Exception {
//        testCreateActivityPackage();
//        Activity thirdActivity = createActivity(activityType1OfOrganization1);
//        activityService.addActivity(thirdActivity, null);
//        activityPackage10Organization.setCreated(new Date(activityPackage10Organization.getCreated().getTime() + 1));
//        activityPackage10Organization.setPrice(999d);
//        activityPackage10Organization.setItemCount(99);
//        activityPackage10Organization.setMaxExecutions(200);
//        activityPackage10Organization.setCurrency("NZD");
//        activityPackage10Organization.setDescription("New Desc");
//        activityPackage10Organization.setExecutionCount(200);
//        activityPackage10Organization.setName("New Name");
//        activityPackage10Organization.setValidFrom(new Date(activityPackage10Organization.getCreated().getTime() + 1));
//        activityPackage10Organization.setValidUntil(new Date(activityPackage10Organization.getCreated().getTime() + 2));
//        activityPackage10Organization.setMaxExecutions(200);
//        Key expectedOrgId = activityPackage10Organization.getOrganizationRef().getKey();
//        activityPackage10Organization.getOrganizationRef().setKey(null);
//        activityPackage10Organization.getActivityPackageActivityListRef().clear();
//        Set<Key> activityKeys = activityPackage10Organization.getActivityKeys();
//        assertNotNull(activityKeys);
//        assertEquals(2, activityKeys.size());
//        assertTrue(activityKeys.contains(activity1Organization1.getId()));
//        assertTrue(activityKeys.contains(activity2Organization1.getId()));
//
//        ActivityPackage activityPackage = activityService.updateActivityPackage(activityPackage10Organization, Arrays.asList(thirdActivity, activity1Organization1));
//
//        long modified = System.currentTimeMillis();
//        assertNotNull(activityPackage);
//
//        activityKeys = activityPackage.getActivityKeys();
//        assertNotNull(activityKeys);
//        assertEquals(2, activityKeys.size());
//        assertTrue(activityKeys.contains(activity1Organization1.getId()));
//        assertTrue(activityKeys.contains(thirdActivity.getId()));
//
    // TODO: THIS PART IS NOT YET IN A SEPERATED TEST
//        assertNotNull(activityPackage);
//        assertEquals("New Desc", activityPackage.getDescription());
//        assertEquals(99, activityPackage.getItemCount());
//        assertEquals(999d, activityPackage.getPrice());
//        //achtung
//        assertEquals("Execution count should not be updated", 0, activityPackage.getExecutionCount());
//        assertEquals("original ap unchanged", 200, activityPackage10Organization.getExecutionCount());
//        assertEquals("New Name", activityPackage.getName());
//        assertEquals("NZD", activityPackage.getCurrency());
//        assertEquals(200, activityPackage.getMaxExecutions());
//        assertEquals(new Date(activityPackage10Organization.getCreated().getTime() + 1), activityPackage.getValidFrom());
//        assertEquals(new Date(activityPackage10Organization.getCreated().getTime() + 2), activityPackage.getValidUntil());
//
//        assertEquals(new Date(activityPackage10Organization.getCreated().getTime() + 1).toString(), activityPackage.getCreated().toString());
//        long fetchedModified = activityPackage.getModified().getTime();
//        assertTrue(fetchedModified <= modified && fetchedModified > (modified - 1000));
//        assertEquals(expectedOrgId, activityPackage.getOrganizationRef().getKey());
//    }

    @Test
    public void testUpdateActivityPackageWithActivities() throws Exception {
        testCreateActivityPackage();
        Activity thirdActivity = createActivity(activityType1OfOrganization1);
        activityService.addActivity(activityType1OfOrganization1, thirdActivity, null);

        ActivityPackage fetched = activityService.updateActivityPackage(activityPackage10Organization, Arrays.asList(thirdActivity));

        Set<Key> activityKeys = fetched.getActivityKeys();
        assertNotNull(activityKeys);
        assertEquals(1, activityKeys.size());
        assertTrue(activityKeys.contains(thirdActivity.getId()));

        fetched = activityService.updateActivityPackage(activityPackage10Organization, Arrays.asList(thirdActivity, activity2Organization1, activity1Organization1));

        activityKeys = fetched.getActivityKeys();
        assertNotNull(activityKeys);
        assertEquals(3, activityKeys.size());
        assertTrue(activityKeys.contains(thirdActivity.getId()));
        assertTrue(activityKeys.contains(activity1Organization1.getId()));
        assertTrue(activityKeys.contains(activity2Organization1.getId()));

        fetched.getActivityPackageActivityListRef().clear();
        activityKeys = fetched.getActivityKeys();
        assertNotNull(activityKeys);
        assertEquals(3, activityKeys.size());
        assertTrue(activityKeys.contains(thirdActivity.getId()));
        assertTrue(activityKeys.contains(activity1Organization1.getId()));
        assertTrue(activityKeys.contains(activity2Organization1.getId()));
    }

    @Test
    public void testCreateAddAndRemoveFromActivityPackage() throws Exception {
        testCreateActivityPackage();

        activityService.removeActivityFromActivityPackage(activityPackage10Organization, activity2Organization1);

        ActivityPackage activityPackage = Datastore.get(ActivityPackageMeta.get(), activityPackage10Organization.getId());
        Set<Key> keys = activityPackage.getActivityKeys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains(activity1Organization1.getId()));

        activityService.addActivityToActivityPackage(activityPackage, activity2Organization1);

        activityPackage = Datastore.get(ActivityPackageMeta.get(), activityPackage.getId());
        keys = activityPackage.getActivityKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains(activity1Organization1.getId()));
        assertTrue(keys.contains(activity2Organization1.getId()));
    }

    @Test
    public void testRemoveActivityPackage() throws Exception {
        testCreateActivityPackage();
        activityService.removeActivityPackage(activityPackage10Organization.getId());
        assertNull("Activity Package must be deleted", Datastore.getOrNull(activityPackage10Organization.getId()));
        assertTrue("Junctions must be deleted", Datastore.query(ActivityPackageActivity.class).asKeyList().isEmpty());
    }

    @Test(expected = OperationException.class)
    public void testRemoveActivityPackageFailsIfSubscribed() throws Exception {
        testSubscribeToActivityPackage();
        activityService.removeActivityPackage(activityPackage10Organization.getId());
    }

    @Test
    public void testSubscribeToActivityPackage() throws Exception {
        testCreateActivityPackage();
        activityPackageExecution = activityService.subscribe(testUser1, activityPackage10Organization, Arrays.asList(activity2Organization1, activity1Organization1));
        assertNotNull(activityPackageExecution);
        assertEquals(activityPackage10Organization.getId(), activityPackageExecution.getActivityPackageRef().getKey());
        assertEquals(testUser1.getId(), activityPackageExecution.getUserRef().getKey());
        assertNull(activityPackageExecution.getTransferRef().getKey());
        List<ActivityPackageSubscription> subscriptions = activityPackageExecution.getSubscriptionListRef().getModelList();
        assertEquals(2, subscriptions.size());
        HashSet<Key> activities = new HashSet<>();
        for (ActivityPackageSubscription subscription : subscriptions) {
            assertEquals(activityPackageExecution.getId(), subscription.getActivityPackageExecutionRef().getKey());
            assertNull(subscription.getTransferRef().getKey());
            assertEquals(testUser1.getId(), subscription.getUserRef().getKey());
            assertNotNull(subscription.getActivityRef().getKey());
            activities.add(subscription.getActivityRef().getKey());
        }
        assertTrue(activities.contains(activity1Organization1.getId()));
        assertTrue(activities.contains(activity2Organization1.getId()));
    }

    @Test
    public void testCancelActivityPackageExecution() throws Exception {
        testSubscribeToActivityPackage();
        ActivityPackage activityPackage = Datastore.get(ActivityPackageMeta.get(), activityPackage10Organization.getId());
        assertEquals(1, activityPackage.getExecutionCount());

        List<ActivityPackageSubscription> subscriptions = activityPackageExecution.getSubscriptionListRef().getModelList();
        List<Activity> activities = new ArrayList<>();
        for (ActivityPackageSubscription subscription : subscriptions) {
            Activity activity = subscription.getActivityRef().getModel();
            activities.add(activity);
            assertEquals(1, activity.getSubscriptionCount());
        }
        activityService.cancelActivityPackageExecution(activityPackageExecution);

        activityPackage = Datastore.get(ActivityPackageMeta.get(), activityPackage.getId());
        assertEquals(0, activityPackage.getExecutionCount());

        for (Activity activity : activities) {
            activity = Datastore.get(ActivityMeta.get(), activity.getId());
            assertEquals(0, activity.getSubscriptionCount());
        }

        for (ActivityPackageSubscription subscription : subscriptions) {
            assertNull(Datastore.getOrNull(subscription.getId()));
        }
        assertNull(Datastore.getOrNull(activityPackageExecution.getId()));
    }

    @Test
    public void testActivityWithCreateDateInPastThrows() {

    }

    @Test
    public void testActivityPackageWithNoActivitiesThrows() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityPackage.activities");
        activityPackage10Organization.setItemCount(2);
        activityService.addActivityPackage(activityPackage10Organization, Collections.EMPTY_LIST);
    }

    @Test
    public void testActivityPackageWithZeroItemCountThrows() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityPackage.itemCount");
        activityPackage10Organization.setItemCount(0);
        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1, activity2Organization1));
    }

    @Test
    public void testActivityPackageWithDuplicateActivitiesThrows() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityPackage.activities");
        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1, activity1Organization1));
    }

    @Test
    public void testActivityPackageLessActivitiesThanItemCountThrows() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityPackage.activities");
        activityPackage10Organization.setItemCount(2);
        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1));
    }

    @Test
    public void testActivityPackageWithOneActivityThrows() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ActivityPackage.activities");
        activityPackage10Organization.setItemCount(1);
        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1));
    }

//    @Test
//    public void testActivityPackageWithValidUntilBeforeValidFromThrows() throws Exception {
//        thrown.expect(FieldValueException.class);
//        thrown.expectMessage("ActivityPackage.validUntil");
//        activityPackage10Organization.setItemCount(2);
//        DateTime datetime = new DateTime();
//        activityPackage10Organization.setValidFrom(datetime.plusDays(2).toDate());
//        activityPackage10Organization.setValidUntil(datetime.plusDays(1).toDate());
//        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1, activity2Organization1));
//    }

//    @Test
//    public void testActivityPackageWithValidFromInPastThrows() throws Exception {
//        thrown.expect(FieldValueException.class);
//        thrown.expectMessage("ActivityPackage.validFrom");
//        activityPackage10Organization.setItemCount(2);
//        activityPackage10Organization.setValidFrom(new Date(20));
//        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1, activity2Organization1));
//    }
}
