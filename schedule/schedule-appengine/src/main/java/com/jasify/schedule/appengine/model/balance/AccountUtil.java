package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.balance.AccountMeta;
import com.jasify.schedule.appengine.meta.balance.OrganizationAccountMeta;
import com.jasify.schedule.appengine.meta.balance.UserAccountMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

/**
 * @author krico
 * @since 22/02/15.
 */
public final class AccountUtil {
    /**
     * A custodial account is a financial account (such as a bank account, a trust fund or a brokerage account) set up
     * for the benefit of a beneficiary, and administered by a responsible person, known as a custodian, who has a
     * fiduciary obligation to the beneficiary.
     */
    public static final String CUSTODIAL_ACCOUNT = "Custodian";
    public static final String PROFIT_AND_LOSS_ACCOUNT = "P&L";
    public static final String USER_ACCOUNT_PREFIX = "u";
    public static final String ORGANIZATION_ACCOUNT_PREFIX = "o";
    private static final Logger log = LoggerFactory.getLogger(AccountUtil.class);
    public static final String DEFAULT_CURRENCY = "CHF";

    private AccountUtil() {
    }

    public static Key custodialAccountKey() {
        return Datastore.createKey(AccountMeta.get(), CUSTODIAL_ACCOUNT);
    }

    public static Key profitAndLossAccountKey() {
        return Datastore.createKey(AccountMeta.get(), PROFIT_AND_LOSS_ACCOUNT);
    }

    public static Account profitAndLossAccount() {
        com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
        try {
            Key id = profitAndLossAccountKey();
            Account account = Datastore.getOrNull(AccountMeta.get(), id);
            if (account == null) {
                account = new Account(id);
                account.setCurrency(DEFAULT_CURRENCY);
                log.info("Created P&L account:{}", account);
                Datastore.put(tx, account);
            }

            tx.commit();
            return account;
        } finally {

            if (tx.isActive())
                tx.rollback();

        }
    }

    /**
     * User/Organization ids map to (User|Organization)Account id directly, meaning
     * a you can always go from a userId to his account id and back.
     *
     * @param memberId the id of a member or an organization
     * @return the key to the account for that memberId
     */
    public static Key memberIdToAccountId(Key memberId) {
        if (UserMeta.get().getKind().equals(memberId.getKind())) {
            return Datastore.createKey(UserAccountMeta.get(), USER_ACCOUNT_PREFIX + memberId.getId());
        }
        if (OrganizationMeta.get().getKind().equals(memberId.getKind())) {
            return Datastore.createKey(OrganizationAccountMeta.get(), ORGANIZATION_ACCOUNT_PREFIX + memberId.getId());
        }
        throw new IllegalArgumentException("memberId must be User or Organization, not: " + memberId);
    }

    /**
     * Does the opposite of {@link #memberIdToAccountId}.
     * This rule should always work both ways.
     *
     * @param accountId an id for an account
     * @return the memberId that owns this accountId
     */
    public static Key accountIdToMemberId(Key accountId) {
        Preconditions.checkArgument(AccountMeta.get().getKind().equals(accountId.getKind()), "Not an accountId: " + accountId);
        String name = accountId.getName();
        if (StringUtils.startsWith(name, USER_ACCOUNT_PREFIX)) {
            return Datastore.createKey(UserMeta.get(), Long.parseLong(name.substring(1)));
        }
        if (StringUtils.startsWith(name, ORGANIZATION_ACCOUNT_PREFIX)) {
            return Datastore.createKey(OrganizationMeta.get(), Long.parseLong(name.substring(1)));
        }
        throw new IllegalArgumentException("Cannot map to memberId: " + accountId);
    }

    public static Account newMemberAccount(Key memberId) {
        Key accountId = memberIdToAccountId(memberId);

        if (UserMeta.get().getKind().equals(memberId.getKind())) {
            UserAccount ret = new UserAccount(accountId);
            ret.setCurrency(DEFAULT_CURRENCY);
            ret.getUserRef().setKey(memberId);
            return ret;
        }

        if (OrganizationMeta.get().getKind().equals(memberId.getKind())) {
            OrganizationAccount ret = new OrganizationAccount(accountId);
            ret.setCurrency(DEFAULT_CURRENCY);
            ret.getOrganizationRef().setKey(memberId);
            return ret;
        }

        throw new IllegalArgumentException("This should never happen!");
    }

    public static Key memberAccountIdMustExist(Key memberId) {
        return memberAccountMustExist(memberId).getId();
    }

    public static Account memberAccountMustExist(Key memberId) {
        Key memberAccountId = AccountUtil.memberIdToAccountId(memberId);
        com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
        Account account;
        try {

            account = Datastore.getOrNull(AccountMeta.get(), memberAccountId);
            if (account == null) {
                account = AccountUtil.newMemberAccount(memberId);
                log.info("Created member account:{} for member:{}", memberAccountId, memberId);
                Datastore.put(tx, account);
            }

            tx.commit();

        } finally {

            if (tx.isActive())
                tx.rollback();

        }

        return account;
    }
}
