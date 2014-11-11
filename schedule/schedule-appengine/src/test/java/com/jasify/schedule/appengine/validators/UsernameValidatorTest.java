package com.jasify.schedule.appengine.validators;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;

public class UsernameValidatorTest {
    private UsernameValidator validator;

    private static void assertContains(List<String> list, String text) {
        assertTrue(text, list.contains(text));
    }

    @Before
    public void servletRunner() {
        TestHelper.initializeJasify();
        validator = UsernameValidator.INSTANCE;
        assertNotNull(validator);
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testValidateGood() throws Exception {
        List<String> list = validator.validate("krico");
        assertTrue(list.isEmpty());
    }

    @Test
    public void testValidateUserExists() throws Exception {

        List<String> list = validator.validate("krico");
        assertTrue(list.isEmpty());

        User user = UserServiceFactory.getUserService().newUser();
        user.setName("krico");
        UserServiceFactory.getUserService().create(user, "password");
        list = validator.validate("krico");
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        list = validator.validate("KriCo");
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    public void testValidateEmpty() throws Exception {
        List<String> list = validator.validate("");
        assertContains(list, UsernameValidator.REASON_LENGTH);
        assertContains(list, UsernameValidator.REASON_VALID_CHARS);
    }

    @Test
    public void testValidateShort() throws Exception {
        List<String> list = validator.validate("k");
        assertContains(list, UsernameValidator.REASON_LENGTH);

        list = validator.validate("kr");
        assertContains(list, UsernameValidator.REASON_LENGTH);

        list = validator.validate("kri");
        assertTrue(list.toString(), list.isEmpty());
    }


    @Test
    public void testValidateCharacters() throws Exception {
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