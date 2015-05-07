package com.jasify.schedule.appengine.model;

import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationService;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Methods that initialize the datastore with data.  Used only in development
 *
 * @author krico
 * @since 25/04/15.
 */
class SchemaMigrationInitialLoad {
    private static final Logger log = LoggerFactory.getLogger(SchemaMigrationInitialLoad.class);

    private final static String SAMPLES = "samples";
    private final static String E2E = "e2e";

    private SchemaMigrationInitialLoad() {
    }

    static SchemaMigrationInitialLoad instance() {
        return new SchemaMigrationInitialLoad();
    }

    void createInitialLoad(String loadType) throws Exception {
        switch (loadType) {
            case E2E:
                createInitialLoadE2E();
                return;
            case SAMPLES:
                createInitialLoadSamples();
                return;
            default:
                log.warn("INVALID LOAD TYPE [{}]", loadType);
        }
    }

    private void createInitialLoadE2E() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Organization> organizations = new ArrayList<>();

        UserService userService = UserServiceFactory.getUserService();
        for (int i = 0; i < 100; ++i) {
            String name = String.format("Sample%d", i);
            User user = userService.create(new User(name, name + "@jasify.com", name + " User"), String.format("secret%d", i));
            users.add(user);
        }

        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        for (int i = 0; i < 20; ++i) {
            Organization organization = new Organization(String.format("Organization%d", i));
            User user = users.get(i);
            organization.setDescription("Organization administered by " + user);
            organizationService.addOrganization(organization);
            organizationService.addUserToOrganization(organization, user);
            organizations.add(organization);
        }
    }

    private void createInitialLoadSamples() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Organization> organizations = new ArrayList<>();

        UserService userService = UserServiceFactory.getUserService();
        for (int i = 0; i < 10; ++i) {
            String name = String.format("Sample%d", i);
            User user = userService.create(new User(name, name + "@jasify.com", name + " User"), String.format("secret%d", i));
            users.add(user);
        }

        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        for (int i = 0; i < 5; ++i) {
            Organization organization = new Organization(String.format("Organization%d", i));
            User user = users.get(i);
            organization.setDescription("Organization administered by " + user);
            organizationService.addOrganization(organization);
            organizationService.addUserToOrganization(organization, user);
            organizations.add(organization);
        }

        ActivityService activityService = ActivityServiceFactory.getActivityService();
        int count = 0;
        for (Organization organization : organizations) {
            ++count;
            ActivityType activityType = new ActivityType(String.format(organization.getName() + " ActivityType " + count));
            if ((count % 5) != 0) {
                activityType.setDescription("This is the activity " + count);
            }

            activityService.addActivityType(organization, activityType);

            Activity activity = new Activity(activityType);
            activity.setName(activityType.getName());
            activity.setDescription(activityType.getDescription());
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.MINUTE, 15);
            calendar.set(Calendar.HOUR, 10);

            if (calendar.getTimeInMillis() <= (System.currentTimeMillis() + 10000)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            activity.setStart(calendar.getTime());

            calendar.set(Calendar.MINUTE, 45);
            activity.setFinish(calendar.getTime());

            RepeatDetails repeatDetails = new RepeatDetails();
            repeatDetails.setUntilCount(10);
            repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
            repeatDetails.setRepeatType(RepeatDetails.RepeatType.Weekly);
            repeatDetails.setMondayEnabled(true);
            repeatDetails.setTuesdayEnabled(true);
            repeatDetails.setWednesdayEnabled(true);
            repeatDetails.setThursdayEnabled(true);
            repeatDetails.setFridayEnabled(true);


            activityService.addActivity(activity, repeatDetails);
        }
    }

}
