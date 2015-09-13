package com.jasify.schedule.appengine.template;

/**
 * @author krico
 * @since 18/08/15.
 */
public class TemplateEngineBuilder {
    public TemplateEngine build() {
        return new VelocityTemplateEngine();
    }
}
