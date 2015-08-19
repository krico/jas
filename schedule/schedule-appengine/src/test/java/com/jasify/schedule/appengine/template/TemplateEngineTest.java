package com.jasify.schedule.appengine.template;

import org.apache.velocity.context.Context;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class TemplateEngineTest {

    @Test
    public void testRenderPlainStringWithNullMap() throws Exception {
        TemplateEngine te = new TemplateEngineBuilder().build();
        String rendered = te.render("plain-string.vm", (Map<String, ?>) null);
        assertEquals("Hello.", rendered);
    }

    @Test
    public void testRenderPlainStringWithNullContext() throws Exception {
        TemplateEngine te = new TemplateEngineBuilder().build();
        String rendered = te.render("plain-string.vm", (Context) null);
        assertEquals("Hello.", rendered);
    }

    @Test
    public void testRenderDynamicString() throws Exception {
        TemplateEngine te = new TemplateEngineBuilder().build();
        HashMap<String, String> context = new HashMap<>();
        context.put("dynamic", "World");
        for (int i = 0; i < 2; ++i) {
            String rendered = te.render("dynamic-string.vm", context);
            assertEquals("Hello World!", rendered);
        }
    }

    @Test
    public void testRenderMacroString() throws Exception {
        TemplateEngine te = new TemplateEngineBuilder().build();
        String rendered = te.render("macro-string.vm", (Context) null);
        assertEquals("<b>Hello.</b>", rendered);
    }
}