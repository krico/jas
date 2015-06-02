package com.jasify.schedule.appengine.dao;

import org.slim3.datastore.ModelMeta;

import java.io.Serializable;

/**
 * @author krico
 * @since 03/06/15.
 */
public abstract class BaseDaoQuery<Model, Meta extends ModelMeta<Model>> implements DaoQuery {
    protected final Meta meta;
    protected final QueryParameters parameters;

    public BaseDaoQuery(Meta meta, Serializable[] parameters) {
        this(meta, QueryParameters.of(parameters));
    }

    public BaseDaoQuery(Meta meta, QueryParameters parameters) {
        this.meta = meta;
        this.parameters = parameters;
    }

    @Override
    public QueryParameters parameters() {
        return parameters;
    }

}
