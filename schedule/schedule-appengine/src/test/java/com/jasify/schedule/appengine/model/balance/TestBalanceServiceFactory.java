package com.jasify.schedule.appengine.model.balance;

import org.easymock.EasyMock;

/**
 * @author krico
 * @since 20/02/15.
 */
public class TestBalanceServiceFactory extends BalanceServiceFactory {
    private BalanceService balanceServiceMock;

    public void setUp() {
        balanceServiceMock = EasyMock.createMock(BalanceService.class);
        setInstance(balanceServiceMock);
    }

    public void tearDown() {
        setInstance(null);
        EasyMock.verify(balanceServiceMock);
        balanceServiceMock = null;
    }

    public BalanceService getBalanceServiceMock() {
        return balanceServiceMock;
    }

    public void replay() {
        EasyMock.replay(balanceServiceMock);
    }
}
