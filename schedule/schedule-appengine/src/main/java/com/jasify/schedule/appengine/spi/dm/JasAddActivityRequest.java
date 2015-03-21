package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;

/**
 * @author wszarmach
 * @since 13/03/15.
 */
public class JasAddActivityRequest {
    private Activity activity;
    private RepeatDetails repeatDetails;

    public void setActivity(Activity activity) { this.activity = activity; }

    public Activity getActivity() { return activity; }

    public void setRepeatDetails(RepeatDetails repeatDetails) { this.repeatDetails = repeatDetails; }

    public RepeatDetails getRepeatDetails() { return repeatDetails; }
}
