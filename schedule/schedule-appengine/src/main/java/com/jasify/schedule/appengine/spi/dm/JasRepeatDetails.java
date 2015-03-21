package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.activity.RepeatDetails.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wszarmach
 * @since 10/03/15.
 */
public class JasRepeatDetails implements JasEndpointEntity {
    private String id;
    private RepeatType repeatType;
    private RepeatUntilType repeatUntilType;
    private int repeatEvery;
    private boolean mondayEnabled;
    private boolean tuesdayEnabled;
    private boolean wednesdayEnabled;
    private boolean thursdayEnabled;
    private boolean fridayEnabled;
    private boolean saturdayEnabled;
    private boolean sundayEnabled;
    private int untilCount;
    private Date untilDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RepeatType getRepeatType() { return repeatType; }

    public void setRepeatType(RepeatType repeatType) { this.repeatType = repeatType; }

    public RepeatUntilType getRepeatUntilType() { return repeatUntilType; }

    public void setRepeatUntilType(RepeatUntilType repeatUntilType) { this.repeatUntilType = repeatUntilType; }

    public int getRepeatEvery() { return repeatEvery; }

    public void setRepeatEvery(int repeatEvery) { this.repeatEvery = repeatEvery; }

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