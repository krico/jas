package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author krico
 * @since 08/06/15.
 */
public class ExampleDaoAncestorQuery extends BaseDaoQuery<Example, ExampleMeta> {

    public ExampleDaoAncestorQuery(ExampleMeta meta, Key ancestorKey) {
        super(meta, new Serializable[]{ancestorKey});
    }

    @Override
    public List<Key> execute() {
        Key ancestor = parameters.get(0);
        return Datastore
                .query(Datastore.getCurrentTransaction(), meta, ancestor)
                .asKeyList();
    }
}
