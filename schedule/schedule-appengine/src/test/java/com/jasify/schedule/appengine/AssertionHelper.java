package com.jasify.schedule.appengine;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jasify.schedule.appengine.model.HasId;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.util.BeanUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author krico
 * @since 11/06/15.
 */
public final class AssertionHelper {
    private AssertionHelper() {
    }

    public static <T extends HasId> void assertIdsEqual(T expected, T actual) {
        assertNotNull("expected", expected);
        assertNotNull("actual", actual);
        assertNotNull("expected.Id is NULL", expected.getId());
        assertNotNull("actual.Id is NULL", actual.getId());
        assertEquals(expected.getId(), actual.getId());
    }

    public static <T extends HasId> void assertIdsEqual(List<T> expected, List<T> actual) {
        assertNotNull("expected is NULL", expected);
        assertNotNull("actual is NULL", actual);
        assertEquals("expected.size != actual.size", expected.size(), actual.size());

        Function<T, Key> toKey = new Function<T, Key>() {
            @Nullable
            @Override
            public Key apply(T t) {
                return t.getId();
            }
        };
        List<Key> expectedKeys = new ArrayList<>(Lists.transform(expected, toKey));
        List<Key> realKeys = new ArrayList<>(Lists.transform(actual, toKey));
        Collections.sort(expectedKeys);
        Collections.sort(realKeys);
        for (int i = 0; i < expectedKeys.size(); ++i) {
            assertEquals("key[" + i + "]", expectedKeys.get(i), realKeys.get(i));
        }
    }

    public static void assertAttributesEquals(Organization expected, Organization actual) {
        assertEquals(BeanUtil.beanMap(expected), BeanUtil.beanMap(actual));
    }
}
