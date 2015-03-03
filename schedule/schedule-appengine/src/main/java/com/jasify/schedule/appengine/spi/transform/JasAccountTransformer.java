package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.spi.dm.JasAccount;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 03/03/15.
 */
public class JasAccountTransformer implements Transformer<Account, JasAccount> {
    @Override
    public JasAccount transformTo(Account internal) {
        JasAccount external = new JasAccount();
        BeanUtil.copyProperties(external, internal);
        external.setType(internal.getClass().getSimpleName());
        return external;
    }

    @Override
    public Account transformFrom(JasAccount external) {
        throw new UnsupportedOperationException("transformFrom(JasAccount)");
    }
}
