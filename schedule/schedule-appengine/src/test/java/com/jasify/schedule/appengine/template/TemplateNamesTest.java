package com.jasify.schedule.appengine.template;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.communication.ApplicationContext;
import com.jasify.schedule.appengine.communication.ApplicationContextImpl;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import static junit.framework.TestCase.assertTrue;

/**
 * This class serves two purposes.
 * 1) Ensure that all templates can be parsed (syntax check)
 * 2) Provide a mechanism to generate all templates into files so that we can view them while developing
 *
 * @author krico
 * @since 20/08/15.
 */
public class TemplateNamesTest {
    private static final Logger log = LoggerFactory.getLogger(TemplateNamesTest.class);
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

    private void assertJasifyNewUser(String templateName) throws Exception {
        VelocityContext context = new VelocityContext(new TestApplicationContext());
        User user = new User();
        user.setId(Datastore.createKey(User.class, 19760715));
        user.setEmail("new@jasify.com");
        user.setName("new@jasify.com");
        user.setRealName("John Doe");
        user.setCreated(new Date());
        context.put("user", user);
        render(templateName, context);
    }

    @Test
    public void testJasifyNewUserHtml() throws Exception {
        assertJasifyNewUser(TemplateNames.JASIFY_NEW_USER_HTML);
    }

    @Test
    public void testJasifyNewUserTxt() throws Exception {
        assertJasifyNewUser(TemplateNames.JASIFY_NEW_USER_TXT);
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

    public static class TestApplicationContext extends ApplicationContextImpl implements ApplicationContext {
        @Override
        protected App createApp() {
            return new App() {
                @Override
                public String getLogo() {
                    return getUrl() + ApplicationContextImpl.LOGO_PATH;
                }

                @Override
                public String getUrl() {
                    return "http://localhost:8080";
                }
            };
        }
    }
}
