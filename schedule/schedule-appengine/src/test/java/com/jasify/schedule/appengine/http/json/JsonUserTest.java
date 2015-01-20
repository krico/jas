package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.JSON;
import com.jasify.schedule.appengine.util.TypeUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class JsonUserTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    private User createUser() {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 55));
        user.setCreated(new Date());
        user.setModified(new Date());
        user.setName("TestName");
        user.setRealName("Test Name");
        user.setEmail("Test@Email");
        user.setAbout("TestAbout");
        user.setAdmin(false);
        return user;
    }

    @Test
    public void testWriteToOnlyChangesFieldsUserCanModify() {
        JsonUser jsonUser = new JsonUser();
        jsonUser.setAbout("about");
        jsonUser.setAdmin(true);
        jsonUser.setId(5);
        jsonUser.setName("test");
        jsonUser.setRealName("realName");
        jsonUser.setEmail("e@ma.il");
        jsonUser.setCreated(new Date(5));
        jsonUser.setModified(new Date(6));

        User user = new User();
        user.setId(Datastore.createKey(User.class, 55));
        user.setAbout("no about");
        user.setAdmin(false);
        user.setCreated(new Date(7));
        user.setModified(new Date(8));
        user.setRealName("no realName");
        user.setEmail("no.e@ma.il");
        user.setName("real");
        user.setPassword(TypeUtil.toShortBlob(new byte[]{1, 2, 3}));
        user.setEmailVerified(true);

        User user1 = jsonUser.writeTo(user);
        assertEquals(user, user1);
        assertTrue(user == user1);

        assertEquals("about", user.getAbout());
        assertEquals(false, user.isAdmin());
        assertEquals(55, user.getId().getId());
        assertEquals("real", user.getName());
        assertEquals("realName", user.getRealName());
        assertEquals("e@ma.il", user.getEmail());
        assertEquals(7, user.getCreated().getTime());
        assertEquals(8, user.getModified().getTime());
        assertEquals(true, user.isEmailVerified());
        assertTrue(Objects.deepEquals(new byte[]{1, 2, 3}, TypeUtil.toBytes(user.getPassword())));
    }

    @Test
    public void testCreateFromUser() {
        User user = createUser();
        JsonUser jsonUser = new JsonUser(user);
        assertEquals(user.getId().getId(), jsonUser.getId());
        assertEquals(user.getCreated(), jsonUser.getCreated());
        assertEquals(user.getModified(), jsonUser.getModified());
        assertEquals(user.getName(), jsonUser.getName());
        assertEquals(user.getRealName(), jsonUser.getRealName());
        assertEquals(user.getEmail(), jsonUser.getEmail());
        assertEquals(user.getAbout(), jsonUser.getAbout());
        assertEquals(user.isAdmin(), jsonUser.isAdmin());
    }

    @Test
    public void testParseJsonUser() {
        User user = createUser();
        StringWriter writer = new StringWriter();
        JSON.toJson(writer, new JsonUser(user));
        JsonUser jsonUser = JsonUser.parse(new StringReader(writer.toString()));
        assertEquals(user.getId().getId(), jsonUser.getId());
        assertEquals(user.getCreated().toString(), jsonUser.getCreated().toString());
        assertEquals(user.getModified().toString(), jsonUser.getModified().toString());
        assertEquals(user.getName(), jsonUser.getName());
        assertEquals(user.getRealName(), jsonUser.getRealName());
        assertEquals(user.getEmail(), jsonUser.getEmail());
        assertEquals(user.getAbout(), jsonUser.getAbout());
        assertEquals(user.isAdmin(), jsonUser.isAdmin());
    }

    @Test
    public void testParseString() {
        User user = createUser();
        StringWriter writer = new StringWriter();
        JSON.toJson(writer, new JsonUser(user));
        JsonUser jsonUser = JsonUser.parse(writer.toString());
        assertEquals(user.getId().getId(), jsonUser.getId());
        assertEquals(user.getCreated().toString(), jsonUser.getCreated().toString());
        assertEquals(user.getModified().toString(), jsonUser.getModified().toString());
        assertEquals(user.getName(), jsonUser.getName());
        assertEquals(user.getRealName(), jsonUser.getRealName());
        assertEquals(user.getEmail(), jsonUser.getEmail());
        assertEquals(user.getAbout(), jsonUser.getAbout());
        assertEquals(user.isAdmin(), jsonUser.isAdmin());
    }
}