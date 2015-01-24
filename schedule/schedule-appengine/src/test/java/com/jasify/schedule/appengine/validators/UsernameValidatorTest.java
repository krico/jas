package com.jasify.schedule.appengine.validators;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.TestUserServiceFactory;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.expect;

public class UsernameValidatorTest {
    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();

    private UsernameValidator validator;

    private static void assertContains(List<String> list, String text) {
        assertTrue(text, list.contains(text));
    }

    @Before
    public void servletRunner() {
        TestHelper.initializeDatastore();
        validator = UsernameValidator.INSTANCE;
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
        expect(UserServiceFactory.getUserService().usernameExists("krico")).andReturn(false);
        testUserServiceFactory.replay();
        assertTrue(validator.validate("krico").isEmpty());
    }

    @Test
    public void testValidateUserExists() throws Exception {
        expect(UserServiceFactory.getUserService().usernameExists("krico")).andReturn(true);
        testUserServiceFactory.replay();
        assertContains(validator.validate("krico"), UsernameValidator.REASON_EXISTS);
    }

    @Test
    public void testValidateNull() throws Exception {
        testUserServiceFactory.replay();
        List<String> list = validator.validate(null);
        assertContains(list, UsernameValidator.REASON_LENGTH);
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);
    }

    @Test
    public void testValidateEmpty() throws Exception {
        testUserServiceFactory.replay();
        List<String> list = validator.validate("");
        assertContains(list, UsernameValidator.REASON_LENGTH);
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);
    }

    @Test
    public void testValidateShort() throws Exception {
        expect(UserServiceFactory.getUserService().usernameExists("kri")).andReturn(false);
        testUserServiceFactory.replay();
        List<String> list = validator.validate("k");
        assertContains(list, UsernameValidator.REASON_LENGTH);

        list = validator.validate("kr");
        assertContains(list, UsernameValidator.REASON_LENGTH);

        list = validator.validate("kri");
        assertTrue(list.toString(), list.isEmpty());
    }


    @Test
    public void testValidateCharacters() throws Exception {
        testUserServiceFactory.replay();
        List<String> list = validator.validate("kri@co");
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);

        list = validator.validate("kri!co");
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);

        list = validator.validate("kri*co");
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);

        list = validator.validate("kri co");
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);

        list = validator.validate("k r");
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);

        list = validator.validate("!krico");
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);
    }


}