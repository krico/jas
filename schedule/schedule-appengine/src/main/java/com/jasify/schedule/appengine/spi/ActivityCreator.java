package com.jasify.schedule.appengine.spi;

import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import com.jasify.schedule.appengine.util.InternationalizationUtil;

import java.util.*;

/**
 * @author wszarmach
 * @since 04/07/15.
 */
public class ActivityCreator {

    public static final int MaximumRepeatCounter = 25; // Maximum number of activities a repeat will create

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
     * @param fromDate: Date to increment
     * @param days: Number of days to increment
     * @return fromDate.plusDays(days). If increment moves over daylight saving interval will adjust hour value appropriately
     */
    private Date increment(Date fromDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        cal.add(Calendar.DATE, days);
        Date toDate = cal.getTime();

        // If the offset changes we must have moved to the next interval
        long fromOffset = InternationalizationUtil.ZURICH_DATE_TIME_ZONE.getOffset(fromDate.getTime());
        long toOffset = InternationalizationUtil.ZURICH_DATE_TIME_ZONE.getOffset(toDate.getTime());

        if (fromOffset != toOffset) {
            long offset = fromOffset - toOffset;
            toDate.setTime(toDate.getTime() + offset);
        }

        return toDate;
    }

    private List<Activity> addActivityRepeatTypeDaily() throws ModelException {
        // For DEV the local timezone is applied which breaks some tests
        Date start = activity.getStart();
        Date finish = activity.getFinish();
        long duration = finish.getTime() - start.getTime();

        List<Activity> activities = new ArrayList<>();
        while (activities.size() < MaximumRepeatCounter) {
            activities.add(createActivity(activity, activityType, repeatDetails, start, finish));
            start = increment(start, repeatDetails.getRepeatEvery());
            finish = new Date(start.getTime() + duration);
            if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Count && activities.size() == repeatDetails.getUntilCount())
                break;
            if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Date && finish.getTime() > repeatDetails.getUntilDate().getTime())
                break;
        }

        return activities;
    }

    private Set<Integer> getRepeatDays(RepeatDetails repeatDetails) {
        Set<Integer> result = new HashSet<>();
        if (repeatDetails.isMondayEnabled()) result.add(Calendar.MONDAY);
        if (repeatDetails.isTuesdayEnabled()) result.add(Calendar.TUESDAY);
        if (repeatDetails.isWednesdayEnabled()) result.add(Calendar.WEDNESDAY);
        if (repeatDetails.isThursdayEnabled()) result.add(Calendar.THURSDAY);
        if (repeatDetails.isFridayEnabled()) result.add(Calendar.FRIDAY);
        if (repeatDetails.isSaturdayEnabled()) result.add(Calendar.SATURDAY);
        if (repeatDetails.isSundayEnabled()) result.add(Calendar.SUNDAY);
        return result;
    }

    private int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    private List<Activity> addActivityRepeatTypeWeekly() throws ModelException {
        final Set<Integer> repeatDays = getRepeatDays(repeatDetails);

        final int repeatEvery;
        if (repeatDetails.getRepeatEvery() > 1) {
            repeatEvery = (repeatDetails.getRepeatEvery() - 1) * 7;
        } else {
            repeatEvery = 0;
        }

        Date start = activity.getStart();
        Date finish = activity.getFinish();
        long duration = finish.getTime() - start.getTime();

        // Find the next chosen day
        for (int day = 0; day < 7; day++) {
            if (repeatDays.contains(getDayOfWeek(start))) {
                break;
            }
            start = increment(start, 1);
            finish = new Date(start.getTime() + duration);
        }

        List<Activity> activities = new ArrayList<>();
        boolean repeatCompleted = false;
        while (!repeatCompleted && activities.size() < MaximumRepeatCounter) {
            // Run through 7 days per week
            for (int day = 0; day < 7 && !repeatCompleted; day++) {
                // Its one of the chosen days
                if (repeatDays.contains(getDayOfWeek(start))) {
                    activities.add(createActivity(activity, activityType, repeatDetails, start, finish));

                }
                // Move to the next day
                start = increment(start, 1);
                finish = new Date(start.getTime() + duration);

                if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Count && activities.size() == repeatDetails.getUntilCount())
                    repeatCompleted = true;
                if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Date && finish.getTime() > repeatDetails.getUntilDate().getTime())
                    repeatCompleted = true;
            }
            // Jump to next period
            if (repeatEvery > 0) {
                start = increment(start, repeatEvery);
                finish = new Date(start.getTime() + duration);
            }
        }
        return activities;
    }
}
