package com.jasify.schedule.appengine.model.history;

import org.slim3.datastore.Model;

/**
 * Track account creation separately.  This gives us the ability to add data to track the origin and nature
 * of logins.
 *
 * @author krico
 * @since 13/08/15.
 */
@Model
public class AccountCreationHistory extends AuthHistory {

    private String referrer;

    public AccountCreationHistory() {
    }

    public AccountCreationHistory(HistoryTypeEnum type) {
        super(type);
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
}
