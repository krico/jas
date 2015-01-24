package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.validators.EmailValidator;
import com.jasify.schedule.appengine.validators.UsernameValidator;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class UniqueEndpointTest {
    @Mock(type = MockType.NICE)
    private UsernameValidator usernameValidator;

    @Mock(type = MockType.NICE)
    private EmailValidator emailValidator;

    @TestSubject
    private UniqueEndpoint endpoint = new UniqueEndpoint();

    @After
    public void cleanup() {
        usernameValidator = null;
        emailValidator = null;
    }

    @Test
    public void testGetUsernameValidatorReturnsInstance() throws UnauthorizedException {
        endpoint = new UniqueEndpoint(); //ignore mocks
        Validator<String> validator = endpoint.getUsernameValidator();
        assertNotNull(validator);
        assertEquals(UsernameValidator.INSTANCE, validator);
    }

    @Test(expected = ConflictException.class)
    public void testCheckUsernameThrows() throws ConflictException {
        String username = RandomStringUtils.randomAlphabetic(5);
        expect(usernameValidator.validate(username)).andReturn(Arrays.asList("Bad")).once();
        replay(usernameValidator);
        endpoint.checkUsername(username);
    }

    @Test
    public void testCheckUsername() throws ConflictException {
        expect(usernameValidator.validate(EasyMock.anyString())).andReturn(Collections.<String>emptyList()).once();
        replay(usernameValidator);
        endpoint.checkUsername(RandomStringUtils.randomAlphabetic(5));
        verify(usernameValidator);
    }

    @Test
    public void testGetEmailValidatorReturnsInstance() throws UnauthorizedException {
        endpoint = new UniqueEndpoint(); //ignore mocks
        Validator<String> validator = endpoint.getEmailValidator();
        assertNotNull(validator);
        assertEquals(EmailValidator.INSTANCE, validator);
    }

    @Test(expected = ConflictException.class)
    public void testCheckEmailThrows() throws ConflictException {
        String email = RandomStringUtils.randomAlphabetic(5) +"@jasify.com";
        expect(emailValidator.validate(email)).andReturn(Arrays.asList("Bad")).once();
        replay(emailValidator);
        endpoint.checkEmail(email);
    }

    @Test
    public void testCheckEmail() throws ConflictException {
        expect(emailValidator.validate(EasyMock.anyString())).andReturn(Collections.<String>emptyList()).once();
        replay(emailValidator);
        endpoint.checkEmail(RandomStringUtils.randomAlphabetic(5));
        verify(emailValidator);
    }

}