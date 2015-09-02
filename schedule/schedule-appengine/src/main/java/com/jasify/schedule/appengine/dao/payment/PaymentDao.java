package com.jasify.schedule.appengine.dao.payment;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.model.payment.Payment;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author wszarmach
 * @since 01/09/15.
 */
public class PaymentDao extends BaseCachingDao<Payment> {
    public PaymentDao() {
        super(PaymentMeta.get());
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
}
