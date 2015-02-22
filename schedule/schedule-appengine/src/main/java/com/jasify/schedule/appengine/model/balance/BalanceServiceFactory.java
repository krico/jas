package com.jasify.schedule.appengine.model.balance;

/**
 * @author krico
 * @since 20/02/15.
 */
public class BalanceServiceFactory {
    private static BalanceService instance;

    protected BalanceServiceFactory() {
    }

    public static BalanceService getBalanceService() {
        if (instance == null)
            return DefaultBalanceService.instance();
        return instance;
    }

    protected static void setInstance(BalanceService instance) {
        BalanceServiceFactory.instance = instance;
    }
}
