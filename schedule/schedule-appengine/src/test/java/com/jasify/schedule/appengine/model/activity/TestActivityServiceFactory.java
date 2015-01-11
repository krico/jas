package com.jasify.schedule.appengine.model.activity;

import org.easymock.EasyMock;

/**
 * @author krico
 * @since 09/01/15.
 */
public class TestActivityServiceFactory extends ActivityServiceFactory {
    private ActivityService activityServiceMock;

    public void setUp() {
        activityServiceMock = EasyMock.createMock(ActivityService.class);
        setInstance(activityServiceMock);
    }

    public void tearDown() {
        setInstance(null);
        EasyMock.verify(activityServiceMock);
        activityServiceMock = null;
    }

    public ActivityService getActivityServiceMock() {
        return activityServiceMock;
    }

    public void replay() {
        EasyMock.replay(activityServiceMock);
    }
}
