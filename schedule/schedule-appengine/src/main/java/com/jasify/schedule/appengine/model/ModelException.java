package com.jasify.schedule.appengine.model;

/**
 * Exceptions thrown by the model
 * Created by krico on 09/11/14.
 */
public class ModelException extends Exception {
    public ModelException() {
    }

    public ModelException(String message) {
        super(message);
    }

    public ModelException(Throwable cause) {
        super(cause);
    }

    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
