package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import com.jasify.schedule.appengine.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasifyEndpointNotMockedTest {
    private JasifyEndpoint endpoint = new JasifyEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetUserService() throws UnauthorizedException {
        UserService service = endpoint.getUserService();
        assertNotNull(service);
        assertEquals(UserServiceFactory.getUserService(), service);
    }

    @Test
    public void testGetUsernameValidator() throws UnauthorizedException {
        Validator<String> validator = endpoint.getUsernameValidator();
        assertNotNull(validator);
        assertEquals(UsernameValidator.INSTANCE, validator);
    }
}