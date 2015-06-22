package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * @author wszarmach
 * @since 21/06/15.
 */
public class ActivityPackageExecutionDaoTest {

    private ActivityPackageExecutionDao dao;

    @BeforeClass
    public static void beforeClass() {
        TestHelper.setSystemProperties();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        dao = new ActivityPackageExecutionDao();
    }

    @Test
    public void testGetActivityPackageExecution() throws Exception {
        ActivityPackage activityPackage1 = TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        ActivityPackageExecution activityPackageExecution1 = TestHelper.createActivityPackageExecution(TestHelper.createUser(true), activityPackage1, true);
        ActivityPackageExecution result1 = dao.get(activityPackageExecution1.getId());
        assertEquals(activityPackageExecution1.getCreated(), result1.getCreated());
        assertEquals(activityPackageExecution1.getModified(), result1.getModified());

        ActivityPackage activityPackage2 = TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        ActivityPackageExecution activityPackageExecution2 = TestHelper.createActivityPackageExecution(TestHelper.createUser(true), activityPackage2, true);
        ActivityPackageExecution result2 = dao.get(activityPackageExecution2.getId());
        assertEquals(activityPackageExecution2.getCreated(), result2.getCreated());
        assertEquals(activityPackageExecution2.getModified(), result2.getModified());

        assertNotNull(dao.get(activityPackageExecution1.getId()));
    }
}
