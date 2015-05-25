package com.jasify.schedule.appengine.model.dao;

import com.google.appengine.api.datastore.DeleteContext;
import com.google.appengine.api.datastore.PostDelete;
import com.google.appengine.api.datastore.PostPut;
import com.google.appengine.api.datastore.PutContext;
import com.jasify.schedule.appengine.memcache.Memcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 25/05/15.
 */
class DaoDatastoreCallbacks {
    private static final Logger log = LoggerFactory.getLogger(DaoDatastoreCallbacks.class);

    @PostPut
    void postPut(PutContext context) {
        //TODO: this is very very very very minimalistic, more of a proof of concept really
        Memcache.delete(context.getCurrentElement().getKey());
    }

    @PostDelete
    void postDelete(DeleteContext context) {
        //TODO: this is very very very very minimalistic, more of a proof of concept really
        Memcache.delete(context.getCurrentElement());
    }
}
