package com.jasify.schedule.appengine.util;

/**
 * @author krico
 * @since 18/05/15.
 */
public final class Threads {
    private Threads() {
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //don't care
        }
    }

}
