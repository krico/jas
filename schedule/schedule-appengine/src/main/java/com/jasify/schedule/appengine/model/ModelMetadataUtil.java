package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Entities;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.jasify.schedule.appengine.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityQuery;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author krico
 * @since 25/12/14.
 */
public final class ModelMetadataUtil {
    private static final Logger log = LoggerFactory.getLogger(ModelMetadataUtil.class);

    private ModelMetadataUtil() {
    }

    public static Set<String> queryAllKinds() {
        log.info("Querying all kinds");
        EntityQuery query = Datastore.query(Entities.KIND_METADATA_KIND);
        Set<String> ret = new TreeSet<>();
        for (Key k : query.asKeyList()) {
            if (!ret.add(k.getName())) {
                log.warn("Duplicate KIND: {} in metadata query", k);
            }
        }
        return ret;
    }

    public static Set<String> queryKindsThatStartWith(String prefix) {
        log.info("Querying kinds that start with: {}", prefix);
        EntityQuery query = Datastore.query(Entities.KIND_METADATA_KIND)
                .filter(
                        Query.FilterOperator.GREATER_THAN_OR_EQUAL.of(Entity.KEY_RESERVED_PROPERTY, Entities.createKindKey(prefix)),
                        Query.FilterOperator.LESS_THAN.of(Entity.KEY_RESERVED_PROPERTY, Entities.createKindKey(prefix + Constants.DATASTORE_HIGH_VALUE))
                );
        Set<String> ret = new TreeSet<>();
        for (Key k : query.asKeyList()) {
            if (!ret.add(k.getName())) {
                log.warn("Duplicate KIND: {} in metadata query", k);
            }
        }
        return ret;
    }
}
