package com.jasify.schedule.appengine.dao.payment;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.dao.QueryParameters;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.model.payment.Payment;
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

    public List<Payment> listSince(final Date when) {
        return query(new SinceQuery(this.<PaymentMeta>getMeta(), when));
    }

    public List<Payment> listBetween(final Date start, final Date end) {
        return query(new BetweenQuery(this.<PaymentMeta>getMeta(), start, end));
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
}
