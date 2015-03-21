package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import java.util.*;

/**
 * @author wszarmach
 * @since 09/03/15.
 */
@Model
public class RepeatDetails {

    public enum RepeatType {
        No,
        Daily,
        Weekly
    }

    public enum RepeatUntilType {
        Count,
        Date
    }

    public RepeatDetails() {
        repeatType = RepeatType.No;
    }

    @Attribute(primaryKey = true)
    private Key id;

    private RepeatType repeatType;

    private int repeatEvery = 1; // 0 is unset

    private RepeatUntilType repeatUntilType;

    private int untilCount; // 0 is unset

    private Date untilDate;

    private boolean mondayEnabled;
    private boolean tuesdayEnabled;
    private boolean wednesdayEnabled;
    private boolean thursdayEnabled;
    private boolean fridayEnabled;
    private boolean saturdayEnabled;
    private boolean sundayEnabled;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public RepeatType getRepeatType() { return repeatType; }

    public void setRepeatType(RepeatType repeatType) { this.repeatType = repeatType; }

    public int getRepeatEvery() { return repeatEvery; }

    public void setRepeatEvery(int repeatEvery) { this.repeatEvery = repeatEvery; }

    public RepeatUntilType getRepeatUntilType() { return repeatUntilType; }

    public void setRepeatUntilType(RepeatUntilType repeatUntilType) { this.repeatUntilType = repeatUntilType; }

    public int getUntilCount() { return untilCount; }

    public void setUntilCount(int untilCount) { this.untilCount = untilCount; }

    public Date getUntilDate() { return untilDate; }

    public void setUntilDate(Date untilDate) { this.untilDate = untilDate; }

    public boolean isMondayEnabled() { return mondayEnabled; }

    public void setMondayEnabled(boolean mondayEnabled) { this.mondayEnabled = mondayEnabled; }

    public boolean isTuesdayEnabled() { return tuesdayEnabled; }

    public void setTuesdayEnabled(boolean tuesdayEnabled) { this.tuesdayEnabled = tuesdayEnabled; }

    public boolean isWednesdayEnabled() { return wednesdayEnabled; }

    public void setWednesdayEnabled(boolean wednesdayEnabled) { this.wednesdayEnabled = wednesdayEnabled; }

    public boolean isThursdayEnabled() { return thursdayEnabled; }

    public void setThursdayEnabled(boolean thursdayEnabled) { this.thursdayEnabled = thursdayEnabled; }

    public boolean isFridayEnabled() { return fridayEnabled; }

    public void setFridayEnabled(boolean fridayEnabled) { this.fridayEnabled = fridayEnabled; }

    public boolean isSaturdayEnabled() { return saturdayEnabled; }

    public void setSaturdayEnabled(boolean saturdayEnabled) { this.saturdayEnabled = saturdayEnabled; }

    public boolean isSundayEnabled() { return sundayEnabled; }

    public void setSundayEnabled(boolean sundayEnabled) { this.sundayEnabled = sundayEnabled; }
}
