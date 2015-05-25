package com.jasify.schedule.appengine.model.dao;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.dao.ExampleMeta;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;

public class DaoUtilTest {

    static Example createExample() {
        Populator populator = new PopulatorBuilder().build();
        return populator.populateBean(Example.class, "id");
    }

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void isItWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(DaoUtil.class);
    }

    @Test
    public void testMarshallAndUnMarshallMemcacheEntries() {
        Example example = createExample();
        example.setId(Datastore.allocateId(Example.class));
        Example ret = DaoUtil.cachePut(example.getId(), ExampleMeta.get(), example);
        assertSame(example, ret);
        Example fetched = DaoUtil.cacheGet(example.getId(), ExampleMeta.get());
        assertEquals(example, fetched);

    }
}