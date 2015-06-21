package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageExecutionMeta;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;

/**
 * @author szarmawa
 * @since 21/06/15.
 */
public class ActivityPackageExecutionDao extends BaseCachingDao<ActivityPackageExecution> {
    public ActivityPackageExecutionDao() {
        super(ActivityPackageExecutionMeta.get());
    }
    
}
