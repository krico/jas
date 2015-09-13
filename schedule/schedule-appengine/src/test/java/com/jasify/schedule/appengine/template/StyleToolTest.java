package com.jasify.schedule.appengine.template;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class StyleToolTest {
    private StyleTool styleTool;

    @Before
    public void createTool() throws Exception {
        styleTool = new StyleTool("/templates/test-styles.css");
    }

    @Test
    public void testParse() {
    }

    @Test
    public void testBody() throws Exception {
        String bodyStyle = styleTool.get("body");
        assertEquals("background-color:#008800", bodyStyle);
    }

    @Test
    public void testJasify() throws Exception {
        String style = styleTool.get(".jasify");
        assertEquals("font-family:\"Helvetica Neue Light\", \"HelveticaNeue-Light\", \"Helvetica Neue\", Calibri, Helvetica, Arial, sans-serif;font-weight:bold", style);
    }
}