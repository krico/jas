package com.jasify.schedule.appengine.spi;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.*;
import org.easymock.*;
import org.junit.*;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class JasifyEndpointTest {
    private UserService userService;

    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();

    private JasifyEndpoint endpoint = new JasifyEndpoint();

    static JasifyEndpointUser newCaller(long id, boolean admin) {
        return new JasifyEndpointUser("a@b", id, admin);
    }

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void setUpTestServiceFactory() {
        testUserServiceFactory.setUp();
        userService = testUserServiceFactory.getUserServiceMock();
    }

    @After
    public void cleanup() {
        UserContext.clearContext();
        testUserServiceFactory.tearDown();
    }


    @Test
    public void testApiInfoNoUser() throws Exception {
        replay(userService);
        JasApiInfo info = endpoint.getApiInfo(null);
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertFalse(info.isAuthenticated());
    }

    @Test
    public void testApiInfoWithUser() throws Exception {
        replay(userService);
        JasApiInfo info = endpoint.getApiInfo(newCaller(1, false));
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertTrue(info.isAuthenticated());
        assertFalse(info.isAdmin());
    }

    @Test
    public void testApiInfoWithAdmin() throws Exception {
        replay(userService);
        JasApiInfo info = endpoint.getApiInfo(newCaller(1, true));
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertTrue(info.isAuthenticated());
        assertTrue(info.isAdmin());
    }
}