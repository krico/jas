package com.jasify.schedule.appengine.model.message;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * @author wszarmach
 * @since 08/11/15.
 */
public class ContactMessageTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testId() {
        ContactMessage contactMessage = new ContactMessage();
        assertNull(contactMessage.getId());
        Key id = Datastore.allocateId(ContactMessage.class);
        contactMessage.setId(id);
        assertEquals(id, contactMessage.getId());
    }

    @Test
    public void testCreated() {
        ContactMessage contactMessage = new ContactMessage();
        assertNull(contactMessage.getCreated());
        Date date = new Date();
        contactMessage.setCreated(date);
        assertEquals(date, contactMessage.getCreated());
    }

    @Test
    public void testEmail() {
        ContactMessage contactMessage = new ContactMessage();
        assertNull(contactMessage.getEmail());
        String email = "a@b";
        contactMessage.setEmail(email);
        assertEquals(email, contactMessage.getEmail());
    }

    @Test
    public void testFirstName() {
        ContactMessage contactMessage = new ContactMessage();
        assertNull(contactMessage.getFirstName());
        String firstName = "Fred";
        contactMessage.setFirstName(firstName);
        assertEquals(firstName, contactMessage.getFirstName());
    }

    @Test
    public void testLastName() {
        ContactMessage contactMessage = new ContactMessage();
        assertNull(contactMessage.getLastName());
        String lastName = "Rubble";
        contactMessage.setLastName(lastName);
        assertEquals(lastName, contactMessage.getLastName());
    }

    @Test
    public void testMessage() {
        ContactMessage contactMessage = new ContactMessage();
        assertNull(contactMessage.getMessage());
        String message = "Hi there";
        contactMessage.setMessage(message);
        assertEquals(message, contactMessage.getMessage());
    }

    @Test
    public void testSubject() {
        ContactMessage contactMessage = new ContactMessage();
        assertNull(contactMessage.getSubject());
        String subject = "Top secret subject";
        contactMessage.setSubject(subject);
        assertEquals(subject, contactMessage.getSubject());
    }
}
