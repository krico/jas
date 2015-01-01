package com.jasify.schedule.appengine.spi.dm;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static junit.framework.TestCase.*;

/**
 * @author krico
 * @since 01/01/15.
 */
public class DmTestHelper {

    public static final Class<JasEndpointEntity> ENTITY_INTERFACE = JasEndpointEntity.class;

    public static void assertWellFormedEntity(Class<?> entityClass) {
        assertEquals("Entities should be on dm package", DmTestHelper.class.getPackage().getName(), entityClass.getPackage().getName());
        assertTrue("Entities must start with Jas", StringUtils.startsWith(entityClass.getSimpleName(), "Jas"));
        boolean jasEntityFound = false;
        for (Class<?> aClass : entityClass.getInterfaces()) {
            if (ENTITY_INTERFACE == aClass) {
                jasEntityFound = true;
                break;
            }
        }
        assertTrue("Entities must implement " + ENTITY_INTERFACE.getName(), jasEntityFound);
        Constructor<?> constructor = null;
        try {
            constructor = entityClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fail("No default constructor");
        }
        assertTrue("Entity constructor must be public", Modifier.isPublic(constructor.getModifiers()));
    }
}
