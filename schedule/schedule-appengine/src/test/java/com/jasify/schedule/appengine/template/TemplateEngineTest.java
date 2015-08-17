package com.jasify.schedule.appengine.template;

import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class TemplateEngineTest {

    @Test
    public void testRenderPlainStringWithNullContext() throws Exception {
        TemplateEngine te = new TemplateEngineBuilder().build();
        String rendered = te.render("plain-string.vm", null);
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
        String rendered = te.render("macro-string.vm", null);
        assertEquals("<b>Hello.</b>", rendered);
    }
}