package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * Class to implement unique constraints on a property.
 * Use it with care :-)
 * <p/>
 * Created by krico on 09/11/14.
 */
public class UniqueConstraint {
    private final DatastoreService datastore;

    private UniqueConstraint(Class<?> model, String propertyName) {
        datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        try {

        } finally {
            txn.commit();
        }
    }
}
