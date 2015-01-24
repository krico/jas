package com.jasify.schedule.appengine.validators;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.TestUserServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.expect;

public class EmailValidatorTest {

    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();
    private EmailValidator validator;

    private static void assertContains(List<String> list, String text) {
        assertTrue(text, list.contains(text));
    }

    @Before
    public void servletRunner() {
        TestHelper.initializeDatastore();
        validator = EmailValidator.INSTANCE;
        assertNotNull(validator);
        testUserServiceFactory.setUp();
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupDatastore();
        testUserServiceFactory.tearDown();
    }

    @Test
    public void testValidateGood() throws Exception {
        expect(UserServiceFactory.getUserService().emailExists("user@jasify.com")).andReturn(false);
        testUserServiceFactory.replay();
        assertTrue(validator.validate("user@jasify.com").isEmpty());
    }

    @Test
    public void testValidateExists() throws Exception {
        expect(UserServiceFactory.getUserService().emailExists("user@jasify.com")).andReturn(true);
        testUserServiceFactory.replay();
        assertContains(validator.validate("user@jasify.com"), EmailValidator.REASON_EXISTS);
    }

    @Test
    public void testValidateNull() throws Exception {
        testUserServiceFactory.replay();
        assertContains(validator.validate(null), EmailValidator.REASON_INVALID);
    }

    @Test
    public void testValidateEmpty() throws Exception {
        testUserServiceFactory.replay();
        assertContains(validator.validate(""), EmailValidator.REASON_INVALID);
    }

    @Test
    public void testValidateNoDomain() throws Exception {
        testUserServiceFactory.replay();
        assertContains(validator.validate("user"), EmailValidator.REASON_INVALID);
        assertContains(validator.validate("user@"), EmailValidator.REASON_INVALID);
    }
}