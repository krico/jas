package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * @author wszarmach
 * @since 08/11/15.
 */
public class JasContactMessageTest {

    @Test
    public void testId() {
        JasContactMessage contactMessage = new JasContactMessage();
        assertNull(contactMessage.getId());
        String id = "IdKey";
        contactMessage.setId(id);
        assertEquals(id, contactMessage.getId());
    }

    @Test
    public void testCreated() {
        JasContactMessage contactMessage = new JasContactMessage();
        assertNull(contactMessage.getCreated());
        Date date = new Date();
        contactMessage.setCreated(date);
        assertEquals(date, contactMessage.getCreated());
    }

    @Test
    public void testEmail() {
        JasContactMessage contactMessage = new JasContactMessage();
        assertNull(contactMessage.getEmail());
        String email = "a@b";
        contactMessage.setEmail(email);
        assertEquals(email, contactMessage.getEmail());
    }

    @Test
    public void testFirstName() {
        JasContactMessage contactMessage = new JasContactMessage();
        assertNull(contactMessage.getFirstName());
        String firstName = "Fred";
        contactMessage.setFirstName(firstName);
        assertEquals(firstName, contactMessage.getFirstName());
    }

    @Test
    public void testLastName() {
        JasContactMessage contactMessage = new JasContactMessage();
        assertNull(contactMessage.getLastName());
        String lastName = "Rubble";
        contactMessage.setLastName(lastName);
        assertEquals(lastName, contactMessage.getLastName());
    }

    @Test
    public void testMessage() {
        JasContactMessage contactMessage = new JasContactMessage();
        assertNull(contactMessage.getMessage());
        String message = "Hi there";
        contactMessage.setMessage(message);
        assertEquals(message, contactMessage.getMessage());
    }

    @Test
    public void testSubject() {
        JasContactMessage contactMessage = new JasContactMessage();
        assertNull(contactMessage.getSubject());
        String subject = "Top secret subject";
        contactMessage.setSubject(subject);
        assertEquals(subject, contactMessage.getSubject());
    }
}
