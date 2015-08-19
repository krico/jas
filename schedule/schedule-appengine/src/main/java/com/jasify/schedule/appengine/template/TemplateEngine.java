package com.jasify.schedule.appengine.template;

import org.apache.velocity.context.Context;

import java.io.Writer;
import java.util.Map;

/**
 * @author krico
 * @since 17/08/15.
 */
public interface TemplateEngine {
    void render(String templateName, Map<String, ?> context, Writer writer) throws TemplateEngineException;

    String render(String templateName, Map<String, ?> context) throws TemplateEngineException;

    void render(String templateName, Context context, Writer writer) throws TemplateEngineException;

    String render(String templateName, Context context) throws TemplateEngineException;
}
