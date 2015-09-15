package com.jasify.schedule.appengine.spi;

import com.google.appengine.repackaged.org.joda.time.DateTimeZone;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import com.jasify.schedule.appengine.util.InternationalizationUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.chrono.ISOChronology;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wszarmach
 * @since 04/07/15.
 */
public class ActivityCreator {

    public static final int MaximumRepeatCounter = 25; // Maximum number of activities a repeat will create
    private static final DateTimeZone ZurichZone = DateTimeZone.forID(InternationalizationUtil.ZURICH_TIME_ZONE_ID);

    private final Activity activity;
    private final ActivityType activityType;
    private final RepeatDetails repeatDetails;

    public ActivityCreator(Activity activity, RepeatDetails repeatDetails, ActivityType activityType) {
        this.activity = activity;
        this.activityType = activityType;
        if (repeatDetails == null) {
            repeatDetails = new RepeatDetails();
        }
        this.repeatDetails = repeatDetails;
    }

    public List<Activity> create() throws ModelException {
        switch (repeatDetails.getRepeatType()) {
            case Daily:
                return addActivityRepeatTypeDaily();
            case Weekly:
                return addActivityRepeatTypeWeekly();
            case No:
                return Collections.singletonList(activity);
            default: // Safety check in case someone adds a new RepeatType but forgets to update this method
                throw new FieldValueException("activity.repeatDetails.repeatType");
        }
    }

    private Activity createActivity(Activity from, ActivityType activityType, RepeatDetails repeatDetails, Date start, Date finish) {
        Activity newActivity = new Activity(activityType);
        newActivity.setCurrency(from.getCurrency());
        newActivity.setDescription(from.getDescription());
        newActivity.setFinish(finish);
        newActivity.setLocation(from.getLocation());
        newActivity.setMaxSubscriptions(from.getMaxSubscriptions());
        newActivity.setName(from.getName());
        newActivity.setPrice(from.getPrice());
        newActivity.getRepeatDetailsRef().setKey(repeatDetails.getId());
        newActivity.setStart(start);
        newActivity.setSubscriptionCount(from.getSubscriptionCount());
        return newActivity;
    }

    /**
     *
     * @param fromDateTime: DateTime to increment
     * @param days: Number of days to increment
     * @return fromDateTime.plusDays(days). If increment moves over daylight saving interval will adjust hour value appropriately
     */
    private DateTime increment(DateTime fromDateTime, int days) {
        DateTime toDateTime = fromDateTime.plusDays(days);

        // If the offset changes we must have moved to the next interval
        long fromHourOffset = TimeUnit.MILLISECONDS.toHours(ZurichZone.getOffset(fromDateTime.getMillis()));
        long toHourOffset = TimeUnit.MILLISECONDS.toHours(ZurichZone.getOffset(toDateTime.getMillis()));

        if (fromHourOffset != toHourOffset) {
            int offset = (int) (fromHourOffset - toHourOffset);
            toDateTime = toDateTime.plusHours(offset);
        }

        return toDateTime;
    }

    private List<Activity> addActivityRepeatTypeDaily() throws ModelException {
        // For DEV the local timezone is applied which breaks some tests
        DateTime start = new DateTime(activity.getStart(), ISOChronology.getInstanceUTC());
        DateTime finish = new DateTime(activity.getFinish(), ISOChronology.getInstanceUTC());

        List<Activity> activities = new ArrayList<>();
        while (activities.size() < MaximumRepeatCounter) {
            activities.add(createActivity(activity, activityType, repeatDetails, start.toDate(), finish.toDate()));
            start = increment(start, repeatDetails.getRepeatEvery());
            finish = increment(finish, repeatDetails.getRepeatEvery());
            if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Count && activities.size() == repeatDetails.getUntilCount())
                break;
            if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Date && finish.toDate().getTime() > repeatDetails.getUntilDate().getTime())
                break;
        }

        return activities;
    }

    private Set<Integer> getRepeatDays(RepeatDetails repeatDetails) {
        Set<Integer> result = new HashSet<>();
        if (repeatDetails.isMondayEnabled()) result.add(DateTimeConstants.MONDAY);
        if (repeatDetails.isTuesdayEnabled()) result.add(DateTimeConstants.TUESDAY);
        if (repeatDetails.isWednesdayEnabled()) result.add(DateTimeConstants.WEDNESDAY);
        if (repeatDetails.isThursdayEnabled()) result.add(DateTimeConstants.THURSDAY);
        if (repeatDetails.isFridayEnabled()) result.add(DateTimeConstants.FRIDAY);
        if (repeatDetails.isSaturdayEnabled()) result.add(DateTimeConstants.SATURDAY);
        if (repeatDetails.isSundayEnabled()) result.add(DateTimeConstants.SUNDAY);
        return result;
    }

    private List<Activity> addActivityRepeatTypeWeekly() throws ModelException {
        final Set<Integer> repeatDays = getRepeatDays(repeatDetails);

        final int repeatEvery;
        if (repeatDetails.getRepeatEvery() > 1) {
            repeatEvery = (repeatDetails.getRepeatEvery() - 1) * 7;
        } else {
            repeatEvery = 0;
        }

        DateTime start = new DateTime(activity.getStart());
        DateTime finish = new DateTime(activity.getFinish());

        // Find the next chosen day
        for (int day = 0; day < 7; day++) {
            if (repeatDays.contains(start.getDayOfWeek())) {
                break;
            }
            start = increment(start, 1);
            finish = increment(finish, 1);
        }

        List<Activity> activities = new ArrayList<>();
        boolean repeatCompleted = false;
        while (!repeatCompleted && activities.size() < MaximumRepeatCounter) {
            // Run through 7 days per week
            for (int day = 0; day < 7 && !repeatCompleted; day++) {
                // Its one of the chosen days
                if (repeatDays.contains(start.getDayOfWeek())) {
                    activities.add(createActivity(activity, activityType, repeatDetails, start.toDate(), finish.toDate()));

                }
                // Move to the next day
                start = increment(start, 1);
                finish = increment(finish, 1);

                if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Count && activities.size() == repeatDetails.getUntilCount())
                    repeatCompleted = true;
                if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Date && finish.toDate().getTime() > repeatDetails.getUntilDate().getTime())
                    repeatCompleted = true;
            }
            // Jump to next period
            if (repeatEvery > 0) {
                start = increment(start, repeatEvery);
                finish = increment(finish, repeatEvery);
            }
        }
        return activities;
    }
}
