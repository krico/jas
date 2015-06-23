package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.spi.dm.JasAccount;
import com.jasify.schedule.appengine.util.FormatUtil;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 03/03/15.
 */
public class JasAccountTransformer implements Transformer<Account, JasAccount> {
    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasAccount
     */
    @Override
    public JasAccount transformTo(Account internal) {
        JasAccount external = new JasAccount();
        external.setBalance(internal.getBalance());
        external.setCreated(internal.getCreated());
        external.setCurrency(internal.getCurrency());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setModified(internal.getModified());
        external.setType(internal.getClass().getSimpleName());
        external.setDescription(FormatUtil.toString(internal));
        return external;
    }

    @Override
    public Account transformFrom(JasAccount external) {
        throw new UnsupportedOperationException("transformFrom(JasAccount)");
    }
}
