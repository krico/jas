package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.config.Nullable;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.jasify.schedule.appengine.model.activity.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author wszarmach
 * @since 06/07/15.
 */
public class ActivityFilter {
    private List<Activity> filterByDate(List<Activity> activities, final Date fromDate, final Date toDate) {
        if (fromDate == null && toDate == null) {
            return activities;
        }

        ArrayList<Activity> result = new ArrayList<>();
        result.addAll(Collections2.filter(activities, new Predicate<Activity>() {
            @Override
            public boolean apply(@Nullable Activity input) {
                if (fromDate != null && input.getStart() != null && fromDate.after(input.getStart())) {
                    return false;
                }
                if (toDate != null && input.getFinish() != null && toDate.before(input.getFinish())) {
                    return false;
                }
                return true;
            }
        }));
        return result;
    }

    public List<Activity> filter(List<Activity> activities, Date fromDate,
                                 Date toDate,
                                 Integer offset,
                                 Integer limit) {
        if (activities.isEmpty()) {
            return Collections.emptyList();
        }

        List<Activity> filtered = filterByDate(activities, fromDate, toDate);

        if (offset == null) offset = 0;
        if (limit == null) limit = 0;

        if (offset > 0 || limit > 0) {
            if (offset < filtered.size()) {
                if (limit <= 0) limit = filtered.size();
                return new ArrayList<>(filtered.subList(offset, Math.min(offset + limit, filtered.size())));
            } else {
                return Collections.emptyList();
            }
        }

        return filtered;
    }
}
