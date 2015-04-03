package com.jasify.schedule.appengine.util;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

/**
 * @author krico
 * @since 22/03/15.
 */
public final class KeyUtil {
    public static final char PARENT_SEPARATOR_CHAR = '-';
    /* I USE lowercase PREFIXES FOR MODELS THAT SHOULD PROBABLY NEVER BE PUBLIC */
    public static final ImmutableBiMap<String, String> PREFIXES = new ImmutableBiMap.Builder<String, String>()
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
    private static final Logger log = LoggerFactory.getLogger(KeyUtil.class);

    private KeyUtil() {
    }

    /**
     * This is a safe method that tries human readable and falls back to KeyFactory.keyToString to provide backward
     * compatibility.
     *
     * @param internal a key
     * @return a string representation of the key
     */
    public static String keyToString(Key internal) {
        if (internal == null) return null;
        try {
            return KeyUtil.toHumanReadableString(internal);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to encode in human readable, falling back to KeyFactory.  Key={}", internal);
            return KeyFactory.keyToString(internal);
        }
    }

    /**
     * This is a safe method that tries human readable and falls back to KeyFactory.stringToKey to provide backward
     * compatibility.
     *
     * @param external a string representation of the key
     * @return the key
     */
    public static Key stringToKey(String external) {
        if (external == null) return null;
        try {
            return KeyUtil.parseHumanReadableString(external);
        } catch (Exception e) {
            log.warn("Failed to parse key as human readable, will fall back to KeyFactory.  Key={}", external, e);
        }
        try {
            return KeyFactory.stringToKey(external);
        } catch (Exception e) {
            log.debug("Failed to parse key: {}", external, e);
            return null;
        }
    }

    public static String toHumanReadableString(Key key) throws IllegalArgumentException {
        Preconditions.checkNotNull(key, "key is NULL");
        Preconditions.checkState(key.isComplete(), "key is not complete");

        String kind = key.getKind();
        String prefix = PREFIXES.get(kind);
        if (prefix == null) {
            throw new IllegalArgumentException("KeyUtil is not prepared to handle keys of kind='" + kind + "'");
        }

        StringBuilder ret = new StringBuilder().append(prefix);
        if (key.getId() != 0L) {
            ret.append(key.getId());
        } else {
            String name = key.getName();
            ret.append('{').append(name.length()).append('}').append(name);
        }

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
        String lenStr = "";
        int len = 0;
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
                    if (Character.isDigit(current) || current == '-') {
                        kind = PREFIXES.inverse().get(prefix);

                        if (kind == null) {
                            throw new IllegalArgumentException("Failed to map prefix '" + prefix + "' to a kind " +
                                    "offset: " + offset + " key: '" + new String(chars) + "'");
                        }

                        if (current == '-') {
                            state = ParseState.Negate;
                        } else {
                            state = ParseState.Id;
                        }
                        id += current;
                    } else if (current == '{') {
                        kind = PREFIXES.inverse().get(prefix);

                        if (kind == null) {
                            throw new IllegalArgumentException("Failed to map prefix '" + prefix + "' to a kind " +
                                    "offset: " + offset + " key: '" + new String(chars) + "'");
                        }
                        state = ParseState.StringLength;
                    } else {
                        prefix += current;
                    }
                    break;
                case StringLength:
                    if (Character.isDigit(current)) {
                        lenStr += current;
                    } else if (current == '}') {
                        len = Integer.parseInt(lenStr);
                        state = ParseState.StringData;
                    } else {
                        throw new IllegalArgumentException("Failed to parse (expecting a digit or '}' but got=" +
                                current + " offset: " + offset + " key: '" + new String(chars) + "')");
                    }
                    break;
                case Negate:
                    if (Character.isDigit(current)) {
                        id += current;
                        state = ParseState.Id;
                    } else {
                        throw new IllegalArgumentException("Failed to parse (expecting a digit but got=" +
                                current + " offset: " + offset + " key: '" + new String(chars) + "')");
                    }
                    break;
                case StringData:
                    if (len > 0) {
                        id += current;
                        --len;
                    } else if (len == 0 && PARENT_SEPARATOR_CHAR == current) {
                        //this is also cool :-)
                        return Datastore.createKey(parse(chars, i + 1), kind, id);
                    } else {
                        throw new IllegalArgumentException("Failed to parse (was not expecting more data=" +
                                current + " offset: " + offset + " key: '" + new String(chars) + "')");
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
                                current + " offset: " + offset + " key: '" + new String(chars) + "')");
                    }
            }
        }

        if (state == ParseState.Id) {
            return Datastore.createKey(kind, Long.parseLong(id));
        }

        if (state == ParseState.StringData) {
            return Datastore.createKey(kind, id);
        }

        throw new IllegalArgumentException("Failed to parse offset:" + offset + ", key: '" + new String(chars) + "'");
    }

    private enum ParseState {
        Init, Prefix, Negate, StringLength, StringData, Id
    }
}
