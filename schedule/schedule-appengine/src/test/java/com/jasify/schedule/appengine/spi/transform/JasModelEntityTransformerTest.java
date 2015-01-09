package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelEntity;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.spi.dm.JasActivityType;
import com.jasify.schedule.appengine.spi.dm.JasEndpointEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

public class JasModelEntityTransformerTest {
    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testConstructorDeterminesProperTransformer() throws Exception {
        assertTransformer(Activity.class, JasActivityTransformer.class);
        assertTransformer(ActivityType.class, JasActivityTypeTransformer.class);
        assertTransformer(UserLogin.class, JasUserLoginTransformer.class);
        assertTransformer(User.class, JasUserTransformer.class);
    }

    private void assertTransformer(Class<? extends ModelEntity> modelType, Class<? extends Transformer<?, ?>> expectedTransformerClass) {
        JasModelEntityTransformer transformer = new JasModelEntityTransformer(modelType);
        Transformer<ModelEntity, JasEndpointEntity> subTransformer = transformer.getTransformer();
        assertNotNull(subTransformer);
        assertEquals(expectedTransformerClass, subTransformer.getClass());
    }


    @Test
    public void testTransformTo() throws Exception {
        JasModelEntityTransformer transformer = new JasModelEntityTransformer(ActivityType.class);
        ActivityType internal = new ActivityType();
        Key id = Datastore.createKey(ActivityType.class, 1);
        internal.setId(id);
        internal.setName("activity");
        internal.setDescription("Desc");
        JasEndpointEntity jasEndpointEntity = transformer.transformTo(internal);
        assertTrue(jasEndpointEntity instanceof JasActivityType);
        JasActivityType external = (JasActivityType) jasEndpointEntity;
        assertNotNull(external);
        assertEquals("activity", external.getName());
        assertEquals("Desc", external.getDescription());
        assertEquals(id, KeyFactory.stringToKey(external.getId()));
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasModelEntityTransformer transformer = new JasModelEntityTransformer(ActivityType.class);
        JasActivityType external = new JasActivityType();
        Key id = Datastore.createKey(ActivityType.class, 1);
        external.setId(KeyFactory.keyToString(id));
        external.setName("activity");
        external.setDescription("Desc");
        ModelEntity modelEntity = transformer.transformFrom(external);
        assertTrue(modelEntity instanceof ActivityType);
        ActivityType internal = (ActivityType) modelEntity;
        assertNotNull(internal);
        assertEquals("activity", internal.getName());
        assertEquals("Desc", internal.getDescription());
        assertEquals(id, internal.getId());
    }
}