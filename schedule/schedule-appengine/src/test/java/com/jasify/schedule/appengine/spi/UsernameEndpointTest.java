package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.TestUserServiceFactory;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class UsernameEndpointTest {
    @Mock
    private Validator<String> usernameValidator;

    @TestSubject
    private UsernameEndpoint endpoint = new UsernameEndpoint();

    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
        testUserServiceFactory.setUp();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
        testUserServiceFactory.tearDown();
        usernameValidator = null;
    }

    @Test
    public void testGetUsernameValidatorReturnsInstance() throws UnauthorizedException {
        testUserServiceFactory.replay();
        endpoint = new UsernameEndpoint(); //ignore mocks
        Validator<String> validator = endpoint.getUsernameValidator();
        assertNotNull(validator);
        assertEquals(UsernameValidator.INSTANCE, validator);
    }

    @Test(expected = ConflictException.class)
    public void testCheckUsernameThrows() throws ConflictException {
        expect(usernameValidator.validate(EasyMock.anyString())).andReturn(Arrays.asList("Bad")).once();
        replay(usernameValidator);
        testUserServiceFactory.replay();
        endpoint.checkUsername(RandomStringUtils.randomAlphabetic(5));
    }

    @Test
    public void testCheckUsername() throws ConflictException {
        expect(usernameValidator.validate(EasyMock.anyString())).andReturn(Collections.<String>emptyList()).once();
        replay(usernameValidator);
        testUserServiceFactory.replay();
        endpoint.checkUsername(RandomStringUtils.randomAlphabetic(5));
        verify(usernameValidator);
    }

}