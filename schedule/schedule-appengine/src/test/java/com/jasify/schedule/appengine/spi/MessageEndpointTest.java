package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.message.ContactMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * @author wszarmach
 * @since 08/11/15.
 */
public class MessageEndpointTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MessageEndpoint endpoint;

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        endpoint = new MessageEndpoint();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetContactMessageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.getContactMessage(newCaller(1), Datastore.allocateId(ContactMessage.class));
    }

    @Test
    public void testGetContactMessageByNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.getContactMessage(newAdminCaller(1), null);
    }

    @Test
    public void testGetContactMessageByUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key key = Datastore.allocateId(ContactMessage.class);
        thrown.expectMessage("No entity was found matching the key: " + key);
        endpoint.getContactMessage(newAdminCaller(1), key);
    }

    @Test
    public void testGetContactMessage() throws Exception {
        ContactMessage contactMessage = TestHelper.createContactMessage(true);
        ContactMessage result = endpoint.getContactMessage(newAdminCaller(1), contactMessage.getId());
        assertEquals(contactMessage.getCreated(), result.getCreated());
        assertEquals(contactMessage.getEmail(), result.getEmail());
        assertEquals(contactMessage.getFirstName(), result.getFirstName());
        assertEquals(contactMessage.getId(), result.getId());
        assertEquals(contactMessage.getLastName(), result.getLastName());
        assertEquals(contactMessage.getMessage(), result.getMessage());
        assertEquals(contactMessage.getSubject(), result.getSubject());
    }

    @Test
    public void testGetContactMessagesNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.getContactMessages(newCaller(1));
    }

    @Test
    public void testGetContactMessages() throws Exception {
        List<ContactMessage> result = endpoint.getContactMessages(newAdminCaller(1));
        assert(result.isEmpty());
        for (int i = 0; i < 3; i++) {
            TestHelper.createContactMessage(true);
        }
        result = endpoint.getContactMessages(newAdminCaller(1));
        assertEquals(3, result.size());
    }

    @Test
    public void testRemoveContactMessageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeContactMessage(newCaller(1), Datastore.allocateId(ContactMessage.class));
    }

    @Test
    public void testRemoveContactMessageInvalidId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.removeContactMessage(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveContactMessageUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key key = Datastore.allocateId(ContactMessage.class);
        thrown.expectMessage("No entity was found matching the key: " + key);
        endpoint.removeContactMessage(newAdminCaller(1), key);
    }

    @Test
    public void testRemoveContactMessage() throws Exception {
        ContactMessage contactMessage = TestHelper.createContactMessage(true);
        endpoint.removeContactMessage(newAdminCaller(1), contactMessage.getId());
        assertNull(Datastore.getOrNull(ContactMessage.class, contactMessage.getId()));
    }

    @Test
    public void testSendContactMessageInvalidMessage() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ContactMessage must have First Name");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setFirstName(null);
        endpoint.sendContactMessage(newCaller(1), contactMessage);
    }

    @Test
    public void testSendContactMessage() throws Exception {
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        endpoint.sendContactMessage(newCaller(1), contactMessage);
        List<ContactMessage> result = endpoint.getContactMessages(newAdminCaller(1));
        assertEquals(1, result.size());
    }
}
