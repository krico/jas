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
        ActivityPackage activityPackage = TestHelper.createActivityPackage(TestHelper.createOrganization(true), true);
        ActivityPackageExecution activityPackageExecution = TestHelper.createActivityPackageExecution(TestHelper.createUser(true), activityPackage, true);
        ActivityPackageExecution result = dao.get(activityPackageExecution.getId());
        assertNotNull(result);
        assertEquals(activityPackageExecution.getCreated(), result.getCreated());
        assertEquals(activityPackageExecution.getModified(), result.getModified());
    }
}
