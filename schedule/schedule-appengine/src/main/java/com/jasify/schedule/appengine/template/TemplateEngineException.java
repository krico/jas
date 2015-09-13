package com.jasify.schedule.appengine.template;

/**
 * @author krico
 * @since 17/08/15.
 */
public class TemplateEngineException extends Exception {
    public TemplateEngineException() {
    }

    public TemplateEngineException(String message) {
        super(message);
    }

    public TemplateEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateEngineException(Throwable cause) {
        super(cause);
    }
}
