package com.jasify.schedule.appengine.template;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * @author krico
 * @since 17/08/15.
 */
public class VelocityTemplateEngine implements TemplateEngine {

    public static final String TEMPLATES_PATH = "/templates";
    public static final String MACROS_PATH = "/templates/Macros.vm";

    private static final ThreadLocal<VelocityEngine> ENGINE_CACHE = new ThreadLocal<VelocityEngine>() {
        @Override
        protected VelocityEngine initialValue() {
            return createEngine();
        }
    };

    private static VelocityEngine createEngine() {
        VelocityEngine ve = new VelocityEngine();
        // Logging
        ve.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, new Slf4jLogger());

        // Resource loading
        ve.setProperty("resource.loader", "vte");
        ve.setProperty("vte.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.setProperty("vte.resource.loader.cache", "true");

        ve.setProperty(RuntimeConstants.VM_LIBRARY, MACROS_PATH);


        ve.init();
        return ve;
    }

    private static VelocityEngine getEngine() {
        return ENGINE_CACHE.get();
    }

    @Override
    public String render(String templateName, Map<String, ?> context) throws TemplateEngineException {
        StringWriter writer = new StringWriter();
        render(templateName, context, writer);
        return writer.toString();
    }

    @Override
    public void render(String templateName, Map<String, ?> context, Writer writer) throws TemplateEngineException {
        try {
            String templatePath = templatePath(templateName);
            Template template = getEngine().getTemplate(templatePath);
            template.merge(new VelocityContext(context), writer);
        } catch (ResourceNotFoundException e) {
            throw new TemplateEngineException("Template not found: " + templateName, e);
        } catch (ParseErrorException e) {
            throw new TemplateEngineException("Failed to parse template: " + templateName, e);
        } catch (MethodInvocationException e) {
            throw new TemplateEngineException("Failed to invoke a method on an object of template: " + templateName, e);
        }
    }

    private String templatePath(String templateName) {
        if (StringUtils.isBlank(templateName)) return templateName;
        if (!StringUtils.startsWith(templateName, "/")) templateName = "/" + templateName;
        if (StringUtils.startsWith(templateName, TEMPLATES_PATH)) return templateName;
        return TEMPLATES_PATH + templateName;
    }

    private static class Slf4jLogger implements LogChute {
        private static final Logger log = LoggerFactory.getLogger(VelocityTemplateEngine.class);

        @Override
        public void init(RuntimeServices rs) throws Exception {
            log.debug("init");
        }

        @Override
        public void log(int level, String message) {
            switch (level) {
                case LogChute.WARN_ID:
                    log.warn(message);
                    break;
                case LogChute.INFO_ID:
                    log.info(message);
                    break;
                case LogChute.TRACE_ID:
                    log.trace(message);
                    break;
                case LogChute.ERROR_ID:
                    log.error(message);
                    break;
                case LogChute.DEBUG_ID:
                default:
                    log.debug(message);
                    break;
            }
        }

        @Override
        public void log(int level, String message, Throwable t) {
            switch (level) {
                case LogChute.WARN_ID:
                    log.warn(message, t);
                    break;
                case LogChute.INFO_ID:
                    log.info(message, t);
                    break;
                case LogChute.TRACE_ID:
                    log.trace(message, t);
                    break;
                case LogChute.ERROR_ID:
                    log.error(message, t);
                    break;
                case LogChute.DEBUG_ID:
                default:
                    log.debug(message, t);
                    break;
            }
        }

        @Override
        public boolean isLevelEnabled(int level) {
            switch (level) {
                case LogChute.WARN_ID:
                    return log.isWarnEnabled();
                case LogChute.INFO_ID:
                    return log.isInfoEnabled();
                case LogChute.TRACE_ID:
                    return log.isTraceEnabled();
                case LogChute.ERROR_ID:
                    return log.isErrorEnabled();
                case LogChute.DEBUG_ID:
                default:
                    return log.isDebugEnabled();
            }
        }
    }
}
