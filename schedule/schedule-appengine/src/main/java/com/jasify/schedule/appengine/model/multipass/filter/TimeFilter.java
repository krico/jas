package com.jasify.schedule.appengine.model.multipass.filter;

import java.io.Serializable;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class TimeFilter implements Serializable {
    public enum ComparisonTypeEnum {
        Before,
        After
    }

    private ComparisonTypeEnum comparisonType;

    // TODO: This it not a good way to represent time :(
    private int hour;
    private int minute;

    public ComparisonTypeEnum getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonTypeEnum comparisonType) {
        this.comparisonType = comparisonType;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Invalid hour: " + hour);
        }
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minute: " + minute);
        }
        this.minute = minute;
    }
}
