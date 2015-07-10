package com.jasify.schedule.appengine.model;

import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.dao.common.RepeatDetailsDao;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.ActivityCreator;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    private Set<PaymentTypeEnum> getPaymentType(int i) {
        Set<PaymentTypeEnum> paymentTypes = new HashSet<>();

        if (i % 3 == 0) {
            paymentTypes.add(PaymentTypeEnum.PayPal);
        } else if (i % 3 == 1) {
            paymentTypes.add(PaymentTypeEnum.Cash);
        }
        return paymentTypes;
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

        OrganizationDao organizationDao = new OrganizationDao();
        for (int i = 0; i < 20; ++i) {
            Organization organization = new Organization(String.format("Organization%d", i));
            User user = users.get(i);
            organization.setDescription("Organization administered by " + user);
            organization.setPaymentTypes(getPaymentType(i));
            organizationDao.save(organization);
            organizationDao.addUserToOrganization(organization.getId(), user.getId());
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

        OrganizationDao organizationDao = new OrganizationDao();
        for (int i = 0; i < 5; ++i) {
            Organization organization = new Organization(String.format("Organization%d", i));
            User user = users.get(i);
            organization.setDescription("Organization administered by " + user);
            organization.setPaymentTypes(getPaymentType(i));
            organizationDao.save(organization);
            organizationDao.addUserToOrganization(organization.getId(), user.getId());
            organizations.add(organization);
        }

        ActivityService activityService = ActivityServiceFactory.getActivityService();
        ActivityTypeDao activityTypeDao = new ActivityTypeDao();
        ActivityDao activityDao = new ActivityDao();
        int count = 0;
        for (Organization organization : organizations) {
            ++count;
            ActivityType activityType = new ActivityType(String.format(organization.getName() + " ActivityType " + count));
            if ((count % 5) != 0) {
                activityType.setDescription("This is the activity " + count);
            }

            activityTypeDao.save(activityType, organization.getId());

            Activity activity = new Activity(activityType);
            activity.setName(activityType.getName());
            activity.setCurrency("CHF");
            activity.setPrice((double) (20 + RandomUtils.nextInt(1, 5)));
            activity.setMaxSubscriptions(10);
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

            new RepeatDetailsDao().save(repeatDetails, organization.getId());
            ActivityCreator activityCreator = new ActivityCreator(activity, repeatDetails, activityType);
            List<Activity> activities = activityCreator.create();
            activityDao.save(activities);
//            List<Activity> activities = Lists.transform(activityKeys, new Function<Key, Activity>() {
//                @Nullable
//                @Override
//                public Activity apply(Key input) {
//                    return Datastore.get(Activity.class, input);
//                }
//            });

            ActivityPackage activityPackageFull = new ActivityPackage();
            activityPackageFull.getOrganizationRef().setModel(organization);
            activityPackageFull.setName("Activity Package " + KeyUtil.keyToString(organization.getId()) + " (FULL)");
            activityPackageFull.setDescription(activityPackageFull.getName() + " description");
            activityPackageFull.setCurrency("CHF");
            activityPackageFull.setPrice(215d);
            activityPackageFull.setItemCount(activities.size() / 2);

            activityService.addActivityPackage(activityPackageFull, activities);

            List<Activity> even = new ArrayList<>();
            List<Activity> odd = new ArrayList<>();

            for (int i = 0; i < activities.size(); i++) {
                if (i % 2 == 0) {
                    even.add(activities.get(i));
                } else {
                    odd.add(activities.get(i));
                }
            }

            ActivityPackage activityPackageEven = new ActivityPackage();
            activityPackageEven.getOrganizationRef().setModel(organization);
            activityPackageEven.setName("Activity Package " + KeyUtil.keyToString(organization.getId()) + " (even)");
            activityPackageEven.setDescription(activityPackageEven.getName() + " description");
            activityPackageEven.setCurrency("CHF");
            activityPackageEven.setPrice(107d);
            activityPackageEven.setItemCount(even.size() - 2);

            activityService.addActivityPackage(activityPackageEven, even);

            ActivityPackage activityPackageOdd = new ActivityPackage();
            activityPackageOdd.getOrganizationRef().setModel(organization);
            activityPackageOdd.setName("Activity Package " + KeyUtil.keyToString(organization.getId()) + " (odd)");
            activityPackageOdd.setDescription(activityPackageOdd.getName() + " description");
            activityPackageOdd.setCurrency("CHF");
            activityPackageOdd.setPrice(107d);
            activityPackageOdd.setItemCount(odd.size() - 2);

            activityService.addActivityPackage(activityPackageOdd, odd);
        }
    }

}
