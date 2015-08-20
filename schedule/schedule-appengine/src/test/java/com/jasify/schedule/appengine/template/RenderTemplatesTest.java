package com.jasify.schedule.appengine.template;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.communication.ApplicationContext;
import com.jasify.schedule.appengine.communication.ApplicationContextImpl;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

import static junit.framework.TestCase.assertTrue;

/**
 * This class serves two purposes.
 * 1) Ensure that all templates can be parsed (syntax check)
 * 2) Provide a mechanism to generate all templates into files so that we can view them while developing
 *
 * @author krico
 * @since 20/08/15.
 */
public class RenderTemplatesTest {
    private static final Logger log = LoggerFactory.getLogger(RenderTemplatesTest.class);
    private static TemplateEngine engine;
    private File templateDir;

    @BeforeClass
    public static void setup() {
        TestHelper.initializeDatastore();
        engine = new TemplateEngineBuilder().build();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void createTemplateDir() {
        templateDir = new File(TestHelper.baseDir(), "target/rendered-templates");
        assertTrue(templateDir.exists() || templateDir.mkdir());
        log.info("\n\n\tTEMPLATE DIR: {}\n\n", templateDir);
    }

    private void assertJasifyNewVersion(String templateName) throws Exception {
        VelocityContext context = new VelocityContext(new TestApplicationContext());
        context.put("version", Version.INSTANCE);
        render(templateName, context);
    }

    @Test
    public void testJasifyNewVersionHtml() throws Exception {
        assertJasifyNewVersion(TemplateNames.JASIFY_NEW_VERSION_HTML);
    }

    @Test
    public void testJasifyNewVersionTxt() throws Exception {
        assertJasifyNewVersion(TemplateNames.JASIFY_NEW_VERSION_TXT);
    }

    private void render(final String templateName, final Context context) throws Exception {
        assertTrue(templateName.endsWith(".vm"));
        String filename = templateName.substring(0, templateName.length() - 3);
        File outputFile = new File(templateDir, filename);
        File parentDir = outputFile.getParentFile();
        assertTrue(parentDir.isDirectory() || parentDir.mkdirs());
        log.info("{} -> {}", templateName, outputFile);
        try (FileWriter writer = new FileWriter(outputFile)) {
            engine.render(templateName, context, writer);
        }
    }

    public static class TestApplicationContext extends VelocityContext implements ApplicationContext {
        public TestApplicationContext() {
            put(App.CONTEXT_KEY, new App() {
                @Override
                public String getLogo() {
                    return getUrl() + ApplicationContextImpl.LOGO_PATH;
                }

                @Override
                public String getUrl() {
                    return "http://localhost:8080";
                }
            });
        }
    }
}
