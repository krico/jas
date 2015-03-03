package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.balance.Transaction;
import com.jasify.schedule.appengine.spi.dm.JasTransaction;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 03/03/15.
 */
public class JasTransactionTransformer implements Transformer<Transaction, JasTransaction> {
    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    @Override
    public JasTransaction transformTo(Transaction internal) {
        JasTransaction external = new JasTransaction();
        BeanUtil.copyProperties(external, internal);
        external.setAccountRef(keyTransformer.transformTo(internal.getAccountRef().getKey()));
        external.setTransferRef(keyTransformer.transformTo(internal.getTransferRef().getKey()));
        return external;
    }

    @Override
    public Transaction transformFrom(JasTransaction external) {
        throw new UnsupportedOperationException("transformFrom(JasTransaction)");
    }
}
