package com.jasify.schedule.appengine.dao.payment;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.dao.QueryParameters;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import org.slim3.datastore.Datastore;
import com.google.appengine.api.datastore.PreparedQuery;

import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.List;
/**
 * @author krico
 * @since 03/09/15.
 */
public class PaymentDao extends BaseCachingDao<Payment> {
    public PaymentDao() {
        super(PaymentMeta.get());
    }

    public List<Payment> list(PaymentStateEnum state) {
        return query(new StateQuery(this.<PaymentMeta>getMeta(), state));
    }

    public List<Payment> list(Date since) {
        return query(new SinceQuery(this.<PaymentMeta>getMeta(), since));
    }

    public List<Payment> list(Date since, PaymentStateEnum state) {
        return query(new SinceWithStateQuery(this.<PaymentMeta>getMeta(), since, state));
    }

    public List<Payment> list(Date start, Date end) {
        return query(new BetweenQuery(this.<PaymentMeta>getMeta(), start, end));
    }

    public List<Payment> list(Date start, Date end, PaymentStateEnum state) {
        return query(new BetweenWithStateQuery(this.<PaymentMeta>getMeta(), start, end, state));
    }

    private static class SinceQuery extends BaseDaoQuery<Payment, PaymentMeta> {
        public SinceQuery(PaymentMeta meta, Date since) {
            super(meta, QueryParameters.of(since));
        }

        @Override
        public List<Key> execute() {
            Date since = parameters.get(0);
            return Datastore
                    .query(meta)
                    .filter(meta.created.greaterThanOrEqual(since))
                    .sort(meta.created.asc)
                    .asKeyList();
        }
    }

    private static class BetweenQuery extends BaseDaoQuery<Payment, PaymentMeta> {
        public BetweenQuery(PaymentMeta meta, Date since, Date until) {
            super(meta, QueryParameters.of(since, until));
        }

        @Override
        public List<Key> execute() {
            Date since = parameters.get(0);
            Date until = parameters.get(1);
            return Datastore
                    .query(meta)
                    .filter(meta.created.greaterThanOrEqual(since),
                            meta.created.lessThanOrEqual(until))
                    .sort(meta.created.asc)
                    .asKeyList();
        }
    }

    private static class ByTransferIdQuery extends BaseDaoQuery<Payment, PaymentMeta> {
        public ByTransferIdQuery(PaymentMeta meta, Key transferId) {
            super(meta, new Serializable[]{transferId});
        }

        @Override
        public List<Key> execute() {
            Key transferId = parameters.get(0);
            return Datastore.query(meta)
                    .filter(meta.transferRef.equal(transferId))
                    .asKeyList(); // TODO: Perhaps this should be asSingle ?
        }
    }
    
    public Payment getByTransferId(Key transferId) {
        PaymentMeta meta = getMeta();
        List<Payment> list = query(new ByTransferIdQuery(meta, transferId));
        if (list.size() == 0) { // asSingle logic
            return null;
        } else if (list.size() > 1) {
            throw new PreparedQuery.TooManyResultsException();
        } else {
            return list.get(0);
        }
    }

    private static class StateQuery extends BaseDaoQuery<Payment, PaymentMeta> {
        public StateQuery(PaymentMeta meta, PaymentStateEnum state) {
            super(meta, QueryParameters.of(state));
        }

        @Override
        public List<Key> execute() {
            PaymentStateEnum state = parameters.get(0);
            return Datastore
                    .query(meta)
                    .filter(meta.state.equal(state))
                    .sort(meta.created.asc)
                    .asKeyList();
        }
    }

    private static class SinceWithStateQuery extends BaseDaoQuery<Payment, PaymentMeta> {
        public SinceWithStateQuery(PaymentMeta meta, Date since, PaymentStateEnum state) {
            super(meta, QueryParameters.of(since, state));
        }

        @Override
        public List<Key> execute() {
            Date since = parameters.get(0);
            PaymentStateEnum state = parameters.get(1);
            return Datastore
                    .query(meta)
                    .filter(meta.created.greaterThanOrEqual(since), meta.state.equal(state))
                    .sort(meta.created.asc)
                    .asKeyList();
        }
    }

    private static class BetweenWithStateQuery extends BaseDaoQuery<Payment, PaymentMeta> {
        public BetweenWithStateQuery(PaymentMeta meta, Date since, Date until, PaymentStateEnum state) {
            super(meta, QueryParameters.of(since, until, state));
        }

        @Override
        public List<Key> execute() {
            Date since = parameters.get(0);
            Date until = parameters.get(1);
            PaymentStateEnum state = parameters.get(2);
            return Datastore
                    .query(meta)
                    .filter(meta.created.greaterThanOrEqual(since),
                            meta.created.lessThanOrEqual(until),
                            meta.state.equal(state))
                    .sort(meta.created.asc)
                    .asKeyList();
        }
    }

}
