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
import org.slim3.datastore.Sort;

import java.io.IOException;
import java.util.*;

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

    public static void dumpDb(Appendable out) throws IOException {
        String line = "+==================================================\n";
        for (String kind : queryAllKinds()) {
            Iterable<Entity> entities = Datastore
                    .query(kind)
                    .sort(new Sort(Entity.KEY_RESERVED_PROPERTY))
                    .asIterable();
            boolean first = true;
            List<String> knownProperties = new ArrayList<>();
            for (Entity entity : entities) {
                if (first) {
                    out.append(line);
                    first = false;
                }
                out.append("| ").append(entity.getKind()).append("[key=").append(Objects.toString(entity.getKey())).append(']');


                Map<String, Object> properties = new HashMap<>(entity.getProperties());
                for (String knownProperty : knownProperties) {
                    Object property = properties.remove(knownProperty);
                    out.append('[').append(knownProperty).append('=').append(Objects.toString(property)).append(']');
                }
                TreeSet<String> newProperties = new TreeSet<String>(properties.keySet());
                for (String newProperty : newProperties) {
                    Object property = properties.remove(newProperty);
                    out.append('[').append(newProperty).append('=').append(Objects.toString(property)).append(']');
                }
                knownProperties.addAll(newProperties);

                out.append("\n");
            }
        }
        out.append(line);
    }
}
