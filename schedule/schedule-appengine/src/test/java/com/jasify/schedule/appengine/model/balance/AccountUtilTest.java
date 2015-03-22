package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.balance.AccountMeta;
import com.jasify.schedule.appengine.meta.balance.OrganizationAccountMeta;
import com.jasify.schedule.appengine.meta.balance.UserAccountMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Objects;

import static junit.framework.TestCase.*;

public class AccountUtilTest {
    @BeforeClass
    public static void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(AccountUtil.class);
    }

    @Test
    public void testCreateCustodialAccountKey() {
        Key key = AccountUtil.custodialAccountKey();
        assertNotNull(key);
        assertEquals(AccountMeta.get().getKind(), key.getKind());
        assertEquals(AccountUtil.CUSTODIAL_ACCOUNT, key.getName());
    }

    @Test
    public void testProfitAndLossAccountKey() {
        Key key = AccountUtil.profitAndLossAccountKey();
        assertNotNull(key);
        assertEquals(AccountMeta.get().getKind(), key.getKind());
        assertEquals(AccountUtil.PROFIT_AND_LOSS_ACCOUNT, key.getName());
    }

    @Test
    public void testProfitAndLossAccount() {
        Account account = AccountUtil.profitAndLossAccount();
        assertNotNull(account);
        assertEquals(AccountUtil.profitAndLossAccountKey(), account.getId());
    }

    @Test
    public void testMemberIdToAccountIdWithUser() {
        Key memberId = Datastore.allocateId(UserMeta.get());
        Key key = AccountUtil.memberIdToAccountId(memberId);
        assertNotNull(key);
        assertEquals(UserAccountMeta.get().getKind(), key.getKind());
        assertTrue(StringUtils.startsWith(key.getName(), AccountUtil.USER_ACCOUNT_PREFIX));
    }

    @Test
    public void testAccountIdToMemberIdUser() {
        Key memberId = Datastore.allocateId(UserMeta.get());
        Key key = AccountUtil.memberIdToAccountId(memberId);
        assertEquals(memberId, AccountUtil.accountIdToMemberId(key));
    }

    @Test
    public void testMemberIdToAccountIdWithOrganization() {
        Key memberId = Datastore.allocateId(OrganizationMeta.get());
        Key key = AccountUtil.memberIdToAccountId(memberId);
        assertNotNull(key);
        assertEquals(OrganizationAccountMeta.get().getKind(), key.getKind());
        assertTrue(StringUtils.startsWith(key.getName(), AccountUtil.ORGANIZATION_ACCOUNT_PREFIX));
    }

    @Test
    public void testAccountIdToMemberIdOrganization() {
        Key memberId = Datastore.allocateId(OrganizationMeta.get());
        Key key = AccountUtil.memberIdToAccountId(memberId);
        assertEquals(memberId, AccountUtil.accountIdToMemberId(key));
    }


    @Test
    public void testNewMemberAccountForUser() {
        Key memberId = Datastore.allocateId(UserMeta.get());
        Account memberAccount = AccountUtil.newMemberAccount(memberId);
        assertTrue(memberAccount instanceof UserAccount);
        assertEquals(memberId, ((UserAccount) memberAccount).getUserRef().getKey());
        assertEquals(AccountUtil.memberIdToAccountId(memberId), memberAccount.getId());
    }

    @Test
    public void testNewMemberAccountForOrganization() {
        Key memberId = Datastore.allocateId(OrganizationMeta.get());
        Account memberAccount = AccountUtil.newMemberAccount(memberId);
        assertTrue(memberAccount instanceof OrganizationAccount);
        assertEquals(memberId, ((OrganizationAccount) memberAccount).getOrganizationRef().getKey());
        assertEquals(AccountUtil.memberIdToAccountId(memberId), memberAccount.getId());
    }

    @Test
    public void testMemberIdsAreUnique() {
        Key userId = Datastore.allocateId(UserMeta.get());
        Key organizationId = Datastore.createKey(OrganizationMeta.get(), userId.getId());

        assertNotSame(userId, organizationId);
        Key userAccountId = AccountUtil.memberIdToAccountId(userId);
        Key organizationAccountId = AccountUtil.memberIdToAccountId(organizationId);
        assertFalse(userAccountId + " != " + organizationAccountId, Objects.equals(userAccountId, organizationAccountId));
    }

    @Test
    public void testMemberAccountMustExistUser() {
        Key userId = Datastore.allocateId(UserMeta.get());
        assertNull(Datastore.getOrNull(UserAccountMeta.get(), AccountUtil.memberIdToAccountId(userId)));
        Key accountId = AccountUtil.memberAccountIdMustExist(userId);
        UserAccount userAccount = Datastore.get(UserAccountMeta.get(), accountId);
        assertEquals(accountId, userAccount.getId());
        assertEquals(AccountUtil.memberIdToAccountId(userId), userAccount.getId());
        assertEquals(userId, userAccount.getUserRef().getKey());
    }

    @Test
    public void testMemberAccountMustExistOrganization() {
        Key organizationId = Datastore.allocateId(OrganizationMeta.get());
        assertNull(Datastore.getOrNull(UserAccountMeta.get(), AccountUtil.memberIdToAccountId(organizationId)));
        Key accountId = AccountUtil.memberAccountIdMustExist(organizationId);
        OrganizationAccount organizationAccount = Datastore.get(OrganizationAccountMeta.get(), accountId);
        assertEquals(accountId, organizationAccount.getId());
        assertEquals(AccountUtil.memberIdToAccountId(organizationId), organizationAccount.getId());
        assertEquals(organizationId, organizationAccount.getOrganizationRef().getKey());
    }

}