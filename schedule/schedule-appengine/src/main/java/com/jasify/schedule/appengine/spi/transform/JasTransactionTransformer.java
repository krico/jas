package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.balance.Transaction;
import com.jasify.schedule.appengine.spi.dm.JasTransaction;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 03/03/15.
 */
public class JasTransactionTransformer implements Transformer<Transaction, JasTransaction> {
    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasTransaction
     */
    @Override
    public JasTransaction transformTo(Transaction internal) {
        JasTransaction external = new JasTransaction();
        external.setAccountRef(keyTransformer.transformTo(internal.getAccountRef().getKey()));
        external.setAmount(internal.getAmount());
        external.setCurrency(internal.getCurrency());
        external.setCreated(internal.getCreated());
        external.setDebit(internal.isDebit());
        external.setDescription(internal.getDescription());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setReference(internal.getReference());
        external.setTransferRef(keyTransformer.transformTo(internal.getTransferRef().getKey()));
        external.setUnpaid(internal.getUnpaid());
        return external;
    }

    @Override
    public Transaction transformFrom(JasTransaction external) {
        throw new UnsupportedOperationException("transformFrom(JasTransaction)");
    }
}
