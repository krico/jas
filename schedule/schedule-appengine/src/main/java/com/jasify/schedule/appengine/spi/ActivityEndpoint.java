package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.transform.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author krico
 * @since 07/01/15.
 */
@Api(name = "jasify",
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class ActivityEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ActivityEndpoint.class);
    private static final Random random = new Random();

    @ApiMethod(name = "activityTypes.query", path = "activity-types", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ActivityType> getActivityTypes(User caller,
                                               @Nullable @Named("partner") String partner) {
        ActivityType type = new ActivityType();
        type.setId(Datastore.createKey(ActivityType.class, 101));
        type.setName("Meta FIT");
        type.setDescription("This activity is called Meta FIT.\nIt is an activity with lorem ipsum no nono.\nAnd is lorem ipsum porem tutor.");
        return Collections.singletonList(type);
    }

    @ApiMethod(name = "activities.query", path = "activities", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Activity> getActivities(User caller,
                                        @Nullable @Named("partner") String partner,
                                        @Nullable @Named("activityTypeId") Key activityTypeId,
                                        @Nullable @Named("fromDate") Date fromDate,
                                        @Nullable @Named("toDate") Date toDate,
                                        @Nullable @Named("offset") Integer offset,
                                        @Nullable @Named("limit") Integer limit) {

        if (fromDate == null) fromDate = new Date();
        if (toDate == null) toDate = new Date(fromDate.getTime() + TimeUnit.DAYS.toMillis(7));

        ArrayList<Activity> ret = new ArrayList<>();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fromDate);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        int startId = random.nextInt(1000000);

        ActivityType activityType = getActivityTypes(null, null).get(0); //big hack :-)

        for (int i = 0; i < 20; i++) {
            Activity activity = new Activity();
            ret.add(activity);
            activity.setId(Datastore.createKey(ActivityType.class, startId + i));
            activity.getActivityTypeRef().setModel(activityType);
            activity.setCurrency("CHF");
            if (i % 3 == 0) {
                activity.setLocation("Some gym");
                activity.setDescription("This is Meta FIT at some gym.  It's going to be very nice.");
            } else {
                activity.setLocation("Another gym");
                activity.setDescription("This is Meta FIT at another gym.  It's going to be slow and steady.");
            }

            activity.setStart(calendar.getTime());
            if (random.nextInt() % 3 == 0) {
                calendar.add(GregorianCalendar.MINUTE, 45);
            } else {
                calendar.add(GregorianCalendar.HOUR, 1);
            }
            activity.setFinish(calendar.getTime());
            activity.setPrice(50.00);
            activity.setMaxSubscriptions(1 + random.nextInt(25));
            activity.setSubscriptionCount(random.nextInt(activity.getMaxSubscriptions()));

            calendar.set(GregorianCalendar.MINUTE, 0);
            calendar.add(GregorianCalendar.HOUR, random.nextInt(3) + 1);
        }

        if (limit == null) limit = 10;
        if (offset == null) offset = 0;


        if (offset > 0 || limit > 0) {
            if (offset < ret.size()) {
                if (limit <= 0) limit = ret.size();
                return new ArrayList<>(ret.subList(offset, Math.min(offset + limit, ret.size())));
            } else {
                return Collections.emptyList();
            }
        }

        return ret;
    }


}
