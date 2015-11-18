package com.jasify.schedule.appengine.model.multipass.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class DayFilter implements Serializable {
    public enum DayOfWeekEnum {
        // This must exist somewhere already
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday
    }

    private List<DayOfWeekEnum> daysOfWeek = new ArrayList<>();

    public List<DayOfWeekEnum> getDaysOfWeek() { return daysOfWeek; }

    public void setDaysOfWeek(List<DayOfWeekEnum> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
}
