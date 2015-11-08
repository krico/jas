package com.jasify.schedule.appengine.dao.message;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.message.ContactMessage;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author wszarmach
 * @since 08/11/15.
 */
public class ContactMessageDaoTest {
    private ContactMessageDao dao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        TestHelper.setSystemProperties();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        dao = new ContactMessageDao();
    }

    @Test
    public void testSaveNullFirstName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have First Name");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setFirstName(null);
        dao.save(contactMessage);
    }

    @Test
    public void testSaveEmptyFirstName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have First Name");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setFirstName("  ");
        dao.save(contactMessage);
    }

    @Test
    public void testSaveNullLastName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Last Name");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setLastName(null);
        dao.save(contactMessage);
    }

    @Test
    public void testSaveEmptyLastName() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Last Name");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setLastName("  ");
        dao.save(contactMessage);
    }

    @Test
    public void testSaveNullEmail() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Email");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setEmail(null);
        dao.save(contactMessage);
    }

    @Test
    public void testSaveEmptyEmail() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Email");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setEmail("  ");
        dao.save(contactMessage);
    }

    @Test
    public void testSaveNullSubject() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Subject");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setSubject(null);
        dao.save(contactMessage);
    }

    @Test
    public void testSaveEmptySubject() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Subject");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setSubject("  ");
        dao.save(contactMessage);
    }

    @Test
    public void testSaveNullMessage() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Message");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setMessage(null);
        dao.save(contactMessage);
    }

    @Test
    public void testSaveEmptyMessage() throws Exception {
        thrown.expect(FieldValueException.class);
        thrown.expectMessage("ContactMessage must have Message");
        ContactMessage contactMessage = TestHelper.createContactMessage(false);
        contactMessage.setMessage("  ");
        dao.save(contactMessage);
    }

    @Test
    public void testSaveExistingMessage() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ContactMessage can not be edited");
        ContactMessage contactMessage = TestHelper.createContactMessage(true);
        dao.save(contactMessage);
    }

    @Test
    public void testSave() throws Exception {
        ContactMessage input = TestHelper.createContactMessage(false);
        Key key = dao.save(input);
        ContactMessage result = dao.get(key);
        assertNotNull(result);
        assertNotNull(result.getCreated());
        assertEquals(input.getEmail(), result.getEmail());
        assertEquals(input.getFirstName(), result.getFirstName());
        assertEquals(input.getId(), result.getId());
        assertEquals(input.getLastName(), result.getLastName());
        assertEquals(input.getMessage(), result.getMessage());
        assertEquals(input.getSubject(), result.getSubject());
    }

    @Test
    public void testGetAll() {
        for (int i = 0; i < 3; i++) {
            TestHelper.createContactMessage(true);
        }
        assertEquals(3, dao.getAll().size());
    }
}
