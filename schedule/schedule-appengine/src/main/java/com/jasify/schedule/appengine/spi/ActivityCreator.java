package com.jasify.schedule.appengine.spi;

import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

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
                return Arrays.asList(activity);
            default: // Safety check in case someone adds a new RepeatType but forgets to update this method
                throw new FieldValueException("activity.repeatDetails.repeatType");
        }
    }

    private Activity createActivity(Activity from, ActivityType activityType, Date start, Date finish) {
        Activity newActivity = new Activity(activityType);
        BeanUtil.copyProperties(newActivity, from);
        newActivity.setStart(start);
        newActivity.setFinish(finish);
        return newActivity;
    }

    private List<Activity> addActivityRepeatTypeDaily() throws ModelException {
        activity.setRepeatDetails(repeatDetails);
        DateTime start = new DateTime(activity.getStart());
        DateTime finish = new DateTime(activity.getFinish());
        List<Activity> activities = new ArrayList<>();
        while (activities.size() < MaximumRepeatCounter) {
            activities.add(createActivity(activity, activityType, start.toDate(), finish.toDate()));
            start = start.plusDays(repeatDetails.getRepeatEvery());
            finish = finish.plusDays(repeatDetails.getRepeatEvery());
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

        activity.setRepeatDetails(repeatDetails);


        DateTime start = new DateTime(activity.getStart());
        DateTime finish = new DateTime(activity.getFinish());

        // Find the next chosen day
        for (int day = 0; day < 7; day++) {
            if (repeatDays.contains(start.getDayOfWeek())) {
                break;
            }
            start = start.plusDays(1);
            finish = finish.plusDays(1);
        }

        List<Activity> activities = new ArrayList<>();
        boolean repeatCompleted = false;
        while (!repeatCompleted && activities.size() < MaximumRepeatCounter) {
            // Run through 7 days per week
            for (int day = 0; day < 7 && !repeatCompleted; day++) {
                // Its one of the chosen days
                if (repeatDays.contains(start.getDayOfWeek())) {
                    activities.add(createActivity(activity, activityType, start.toDate(), finish.toDate()));

                }
                // Move to the next day
                start = start.plusDays(1);
                finish = finish.plusDays(1);

                if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Count && activities.size() == repeatDetails.getUntilCount())
                    repeatCompleted = true;
                if (repeatDetails.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Date && finish.toDate().getTime() > repeatDetails.getUntilDate().getTime())
                    repeatCompleted = true;
            }
            // Jump to next period
            if (repeatEvery > 0) {
                start = start.plusDays(repeatEvery);
                finish = finish.plusDays(repeatEvery);
            }
        }
        return activities;
    }
}
