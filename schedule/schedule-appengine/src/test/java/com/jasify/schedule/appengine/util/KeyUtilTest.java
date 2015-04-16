package com.jasify.schedule.appengine.util;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.Reflections;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;

import java.lang.reflect.Method;
import java.util.*;

import static junit.framework.TestCase.*;

public class KeyUtilTest {
    private static List<ModelMeta<?>> allModels = new ArrayList<>();

    @BeforeClass
    public static void initializeDatastore() throws Exception {
        TestHelper.initializeDatastore();
        Reflections reflections = new Reflections("com.jasify.schedule.appengine.meta");
        Set<Class<? extends ModelMeta>> models = reflections.getSubTypesOf(ModelMeta.class);
        assertNotNull(models);
        assertTrue(models.size() > 5);

        for (Class<? extends ModelMeta> model : models) {
            Method get = model.getMethod("get");
            ModelMeta modelInstance = (ModelMeta) get.invoke(null);
            allModels.add(modelInstance);
        }
        Collections.sort(allModels, new Comparator<ModelMeta<?>>() {
            @Override
            public int compare(ModelMeta<?> o1, ModelMeta<?> o2) {
                int kind = o1.getKind().compareTo(o2.getKind());
                return kind == 0 ? o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName()) : kind;
            }
        });

//        for (ModelMeta<?> model : allModels) {
//            System.out.println("put(" + model.getClass().getSimpleName() + ".get().getKind(), \"" + model.getKind().charAt(0) + "\"); //" + model.getKind());
//        }
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(KeyUtil.class);
    }

    @Test
    public void testToHumanReadableKnowsAllModels() throws Exception {
        Random random = new Random(19760715);
        for (ModelMeta<?> model : allModels) {
            KeyUtil.toHumanReadableString(Datastore.createKey(model, random.nextLong()));
        }
    }

    @Test
    public void testToHumanReadable() throws Exception {
        Map<String, String> prefixes = new HashMap<>();
        Random random = new Random(19760715);
        for (ModelMeta<?> model : allModels) {
            Key key = Datastore.createKey(model, random.nextLong());
            String keyStr = KeyUtil.toHumanReadableString(key);
            assertNotNull(keyStr);
            char[] chars = keyStr.toCharArray();
            assertTrue(chars.length > 1);
            assertFalse(Character.isDigit(chars[0]));
            boolean hasDigit = false;
            String prefix = "";
            for (char aChar : chars) {
                if (!Character.isDigit(aChar)) {
                    assertFalse("Cannot have characters after digits", hasDigit);
                    prefix += aChar;
                } else {
                    hasDigit = true;
                }
            }
            assertTrue("Must have at least one digit", hasDigit);
            String seen = prefixes.put(prefix, model.getKind());
            assertTrue("Prefix[" + prefix + "] already used by kind: " + seen, seen == null || seen.equals(model.getKind()));
        }
    }

    @Test
    public void testParseHumanReadable() throws Exception {
        Random random = new Random(19760715);
        for (ModelMeta<?> model : allModels) {
            Key key = Datastore.createKey(model, random.nextLong());
            Key parsedKey = KeyUtil.parseHumanReadableString(KeyUtil.toHumanReadableString(key));
            assertNotNull(parsedKey);
            assertEquals(key, parsedKey);
        }
    }

    @Test
    public void testParseHumanReadableWithParent() throws Exception {
        Random random = new Random(19760715);
        for (ModelMeta<?> model : allModels) {
            ModelMeta<?> parentModel = allModels.get(random.nextInt(allModels.size()));
            Key parentKey = Datastore.createKey(parentModel, random.nextLong());
            Key key = Datastore.createKey(parentKey, model, random.nextLong());
            String encoded = KeyUtil.toHumanReadableString(key);
            Key parsedKey = KeyUtil.parseHumanReadableString(encoded);
            assertNotNull(parsedKey);
            assertEquals(key, parsedKey);
        }
    }

    @Test
    public void testParseHumanReadableWithRandomDepth() throws Exception {
        Random random = new Random(19760715);
        for (ModelMeta<?> model : allModels) {
            int depth = random.nextInt(6) + 1;
            Key parent = null;
            for (int i = 0; i < depth; ++i) {
                ModelMeta<?> parentModel = allModels.get(random.nextInt(allModels.size()));
                if (parent == null) {
                    parent = Datastore.createKey(parentModel, random.nextInt(1000000));
                } else {
                    parent = Datastore.createKey(parent, parentModel, random.nextInt(1000000));
                }
            }
            Key key = Datastore.createKey(parent, model, random.nextLong());
            String encoded = KeyUtil.toHumanReadableString(key);
            Key parsedKey = KeyUtil.parseHumanReadableString(encoded);
            assertNotNull(parsedKey);
            assertEquals(key, parsedKey);
        }
    }

    @Test
    public void testToHumanReadableNegative1digit() throws Exception {
        Key key = Datastore.createKey(User.class, -1);
        String encoded = KeyUtil.toHumanReadableString(key);
        assertEquals(key, KeyUtil.parseHumanReadableString(encoded));
    }

    @Test
    public void testToHumanReadableNegative() throws Exception {
        Key key = Datastore.createKey(User.class, -5018);
        String encoded = KeyUtil.toHumanReadableString(key);
        assertEquals(key, KeyUtil.parseHumanReadableString(encoded));
    }

    @Test
    public void testNonNumericKey() throws Exception {
        Key key = Datastore.createKey(User.class, "level1");
        String encoded = KeyUtil.toHumanReadableString(key);
        assertEquals(key, KeyUtil.parseHumanReadableString(encoded));
    }

    @Test
    public void testNonNumericKeyRandomDepth() throws Exception {
        Random random = new Random(19760715);
        for (ModelMeta<?> model : allModels) {
            int depth = random.nextInt(6) + 1;
            Key parent = null;
            for (int i = 0; i < depth; ++i) {
                ModelMeta<?> parentModel = allModels.get(random.nextInt(allModels.size()));
                if (parent == null) {
                    parent = Datastore.createKey(parentModel, randomString(random));
                } else {
                    if (random.nextBoolean()) {
                        parent = Datastore.createKey(parent, parentModel, random.nextInt(1000000));
                    } else {
                        parent = Datastore.createKey(parent, parentModel, randomString(random));
                    }
                }
            }
            Key key = Datastore.createKey(parent, model, randomString(random));
            String encoded = KeyUtil.toHumanReadableString(key);
            Key parsedKey = KeyUtil.parseHumanReadableString(encoded);
            assertNotNull(parsedKey);
            assertEquals(key, parsedKey);
        }
    }

    private String randomString(Random random) {
        return RandomStringUtils.randomAscii(random.nextInt(32) + 1);
    }
}