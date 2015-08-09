package com.jasify.schedule.appengine.model.history;

/**
 * @author krico
 * @since 09/08/15.
 */
public enum HistoryTypeEnum {
    Message("Message describing an event");


    private final String description;

    HistoryTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
