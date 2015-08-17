package com.jasify.schedule.appengine.template;

import java.io.Writer;
import java.util.Map;

/**
 * @author krico
 * @since 17/08/15.
 */
public interface TemplateEngine {
    void render(String templateName, Map<String, ?> context, Writer writer) throws TemplateEngineException;

    String render(String templateName, Map<String, ?> context) throws TemplateEngineException;
}
