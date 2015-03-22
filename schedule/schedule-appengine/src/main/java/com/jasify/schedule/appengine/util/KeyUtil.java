package com.jasify.schedule.appengine.util;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.jasify.schedule.appengine.meta.MailMessageMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.meta.activity.RepeatDetailsMeta;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.application.ApplicationMeta;
import com.jasify.schedule.appengine.meta.application.ApplicationPropertyMeta;
import com.jasify.schedule.appengine.meta.balance.AccountMeta;
import com.jasify.schedule.appengine.meta.balance.TransactionMeta;
import com.jasify.schedule.appengine.meta.balance.TransferMeta;
import com.jasify.schedule.appengine.meta.common.GroupMeta;
import com.jasify.schedule.appengine.meta.common.GroupUserMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMemberMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.meta.users.PasswordRecoveryMeta;
import com.jasify.schedule.appengine.meta.users.UserDetailMeta;
import com.jasify.schedule.appengine.meta.users.UserLoginMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import org.slim3.datastore.Datastore;

/**
 * @author krico
 * @since 22/03/15.
 */
public final class KeyUtil {
    public static final char PARENT_SEPARATOR_CHAR = '-';

    /* I USE lowercase PREFIXES FOR MODELS THAT SHOULD PROBABLY NEVER BE PUBLIC */
    private static final ImmutableBiMap<String, String> PREFIXES = new ImmutableBiMap.Builder<String, String>()
            .put(AccountMeta.get().getKind(), "$") //Account
//                    .put(OrganizationAccountMeta.get().getKind(), "A") //Account
//                    .put(UserAccountMeta.get().getKind(), "A") //Account
            .put(ActivityMeta.get().getKind(), "A") //Activity
            .put(ActivityTypeMeta.get().getKind(), "AT") //ActivityType
            .put(ApplicationMeta.get().getKind(), "a") //App
            .put(ApplicationPropertyMeta.get().getKind(), "ap") //AppProp
            .put(GroupMeta.get().getKind(), "G") //Group
            .put(GroupUserMeta.get().getKind(), "gu") //GroupUser
            .put(MailMessageMeta.get().getKind(), "M") //Msg
            .put(OrganizationMeta.get().getKind(), "O") //Organization
            .put(OrganizationMemberMeta.get().getKind(), "om") //OrganizationMember
            .put(PasswordRecoveryMeta.get().getKind(), "pr") //PasswordRecovery
//                    .put(PayPalPaymentMeta.get().getKind(), "P") //Payment
            .put(PaymentMeta.get().getKind(), "P") //Payment
            .put(RepeatDetailsMeta.get().getKind(), "R") //RepeatDetails
            .put(SubscriptionMeta.get().getKind(), "S") //Subscription
            .put(TransactionMeta.get().getKind(), "T") //Transaction
            .put(TransferMeta.get().getKind(), "TR") //Transfer
            .put(UserMeta.get().getKind(), "U") //User
//                    .put(User_v0Meta.get().getKind(), "U") //User
//                    .put(User_v1Meta.get().getKind(), "U") //User
            .put(UserDetailMeta.get().getKind(), "UD") //UserDetail
            .put(UserLoginMeta.get().getKind(), "UL") //UserLogin
            .build();

    private KeyUtil() {
    }

    public static String toHumanReadableString(Key key) throws IllegalArgumentException {
        Preconditions.checkNotNull(key, "key is NULL");

        String kind = key.getKind();
        String prefix = PREFIXES.get(kind);
        if (prefix == null) {
            throw new IllegalArgumentException("KeyUtil is not prepared to handle keys of kind='" + kind + "'");
        }
        StringBuilder ret = new StringBuilder()
                .append(prefix).append(key.getId());

        if (key.getParent() != null) {
            ret.append(PARENT_SEPARATOR_CHAR).append(toHumanReadableString(key.getParent()));
        }
        return ret.toString();
    }

    public static Key parseHumanReadableString(String encoded) throws IllegalArgumentException {
        Preconditions.checkNotNull(encoded, "encoded is NULL");
        return parse(encoded.toCharArray(), 0);
    }

    //This is cool :-)
    private static Key parse(char[] chars, int offset) {
        ParseState state = ParseState.Init;
        String prefix = "";
        String id = "";
        String kind = null;
        for (int i = Preconditions.checkElementIndex(offset, chars.length); i < chars.length; ++i) {
            char current = chars[i];
            switch (state) {
                case Init:
                    Preconditions.checkState(!Character.isDigit(current));
                    state = ParseState.Prefix;
                    prefix += current;
                    break;
                case Prefix:
                    if (Character.isDigit(current)) {

                        if (prefix.length() > 1 && prefix.endsWith("-")) {//handle negative ids
                            prefix = prefix.substring(0, prefix.length() - 1);
                            id += '-';
                        }
                        kind = PREFIXES.inverse().get(prefix);

                        if (kind == null) {
                            throw new IllegalArgumentException("Failed to map prefix '" + prefix + "' to a kind " +
                                    "offset: " + offset + " key: '" + new String(chars) + "'");
                        }

                        state = ParseState.Id;
                        id += current;
                    } else {
                        prefix += current;
                    }
                    break;
                case Id:
                    if (Character.isDigit(current)) {
                        id += current;
                    } else if (PARENT_SEPARATOR_CHAR == current) {
                        //this is also cool :-)
                        return Datastore.createKey(parse(chars, i + 1), kind, Long.parseLong(id));
                    } else {
                        throw new IllegalArgumentException("Failed to parse (expecting a digit or '-' but got=" +
                                current + " offset: " + offset + " key: '" + new String(chars) + "'");
                    }
            }
        }

        if (state != ParseState.Id) {
            throw new IllegalArgumentException("Failed to parse offset:" + offset + ", key: '" + new String(chars) + "'");
        }

        return Datastore.createKey(kind, Long.parseLong(id));
    }

    private enum ParseState {
        Init, Prefix, Id
    }
}
