package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.mail.MailParser;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.meta.users.UserDetailMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.meta.users.User_v0Meta;
import com.jasify.schedule.appengine.meta.users.User_v1Meta;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jasify.schedule.appengine.Constants.SCHEMA_VERSION_NAME;

/**
 * Central class responsible for handling schema migrations
 *
 * @author krico
 * @since 22/11/14.
 */
public final class SchemaMigration {
    private static final Logger log = LoggerFactory.getLogger(SchemaMigration.class);

    private SchemaMigration() {
    }

    public static SchemaMigration instance() {
        return Singleton.INSTANCE;
    }

    public boolean notifyOfNewVersion() {
        String currentVersion = Version.toVersionString();
        ApplicationData applicationData = ApplicationData.instance();
        String propertyName = Version.class.getName();
        String version = applicationData.getProperty(propertyName);
        if (StringUtils.equals(currentVersion, version)) {
            return false;
        }
        log.info("New version installed: {}", currentVersion);
        String subject = String.format("[Jasify] New Version In Prod [%s]", Version.toShortVersionString());
        try {
            MailParser mailParser = MailParser.createNewVersionEmail(Version.getVersion(), Version.getTimestampVersion(),
                    Version.getBranch(), Version.getNumber(), EnvironmentUtil.defaultVersionUrl());
            MailServiceFactory.getMailService().sendToApplicationOwners(subject, mailParser.getHtml(), mailParser.getText());
        } catch (Exception e) {
            log.warn("Failed to notify jasify", e);
        }
        applicationData.setProperty(propertyName, currentVersion);
        return true;
    }

    public boolean executePendingMigrations() {
        ApplicationData applicationData = ApplicationData.instance();
        boolean executed = false;

        String user_v0_key = SchemaMigration.class.getName() + ".User_v0";
        Boolean user_v0_migrated = applicationData.getProperty(user_v0_key);
        if (Boolean.TRUE != user_v0_migrated) {
            migrateUser_v0_to_User_v1();
            applicationData.setProperty(user_v0_key, true);
            executed = true;
        }

        String user_v1_key = SchemaMigration.class.getName() + ".User_v1";
        Boolean user_v1_migrated = applicationData.getProperty(user_v1_key);
        if (Boolean.TRUE != user_v1_migrated) {
            migrateUser_v1_to_User_v2();
            applicationData.setProperty(user_v1_key, true);
            executed = true;
        }

        String userLoginUniqueConstraintKey = SchemaMigration.class.getName() + ".UserLoginUniqueConstraint";
        Boolean userLoginUniqueConstraintMigrated = applicationData.getProperty(userLoginUniqueConstraintKey);
        if (Boolean.TRUE != userLoginUniqueConstraintMigrated) {
            migrateUserLoginUniqueConstraintKey(applicationData);
            applicationData.setProperty(userLoginUniqueConstraintKey, true);
            executed = true;
        }

        if (EnvironmentUtil.isDevelopment()) {
            String devInitialize = SchemaMigration.class.getName() + ".DevInitialize";
            Boolean devInitialized = applicationData.getProperty(devInitialize);
            if (Boolean.TRUE != devInitialized) {
                initializeDevSystem();
                applicationData.setProperty(devInitialize, true);
                executed = true;
            } else {
                log.debug("No dev initialization pending...");
            }
        }

        executed |= createOauthProviderConfig();

        return executed;
    }

    private void migrateUserLoginUniqueConstraintKey(ApplicationData applicationData) {
        //This is the wrongly created constraint (the correct one is userId+provider and was fixed in DefaultUserService)
        final String UC_NAME = "jasify.UniqueConstraint.UserLogin#provider+userId";
        Object property = applicationData.getProperty(UC_NAME);
        if (property == null) return;
        String uniqueConstraint = (String) property;
        if (!uniqueConstraint.startsWith("UC"))
            throw new RuntimeException("Something went wrong! UC=" + uniqueConstraint);
        Set<String> kinds = ModelMetadataUtil.queryKindsThatStartWith(uniqueConstraint);
        for (String kind : kinds) {
            if (!kind.startsWith("UC"))
                throw new RuntimeException("Something went wrong! UC=" + kind);
            List<Key> keys = Datastore.query(kind).asKeyList();
            log.warn("Deleting {} ({} entries)", kind, keys.size());
            Datastore.delete(keys);
        }
    }

    private boolean createOauthProviderConfig() {
        boolean executed = false;
        ApplicationData applicationData = ApplicationData.instance();
        for (OAuth2ProviderEnum provider : OAuth2ProviderEnum.values()) {
            Object property1 = applicationData.getProperty(provider.clientIdKey());
            if (property1 == null) {
                executed |= true;
                applicationData.setProperty(provider.clientIdKey(), "SET-CLIENT-ID");
            }
            Object property2 = applicationData.getProperty(provider.clientSecretKey());
            if (property2 == null) {
                executed |= true;
                applicationData.setProperty(provider.clientSecretKey(), "SET-CLIENT-SECRET");
            }
        }
        return executed;
    }

    void initializeDevSystem() {
        Preconditions.checkState(!EnvironmentUtil.isProduction(), "Cannot initializeDev in prod!");
        User admin = new User();
        admin.setName("admin");
        admin.setAdmin(true);
        try {
            UserServiceFactory.getUserService().create(admin, "admin");
        } catch (UsernameExistsException | EmailExistsException e) {
            // Don't really care
        }
        if (EnvironmentUtil.isContinuousIntegrationEnvironment()) {
            log.warn("CONTINUOUS INTEGRATION: Creating test values for OAuth2ProviderConfig");
            for (OAuth2ProviderEnum provider : OAuth2ProviderEnum.values()) {
                ApplicationData.instance().setProperty(provider.clientIdKey(), RandomStringUtils.randomAscii(16));
                ApplicationData.instance().setProperty(provider.clientSecretKey(), RandomStringUtils.randomAscii(32));
            }
        } else {
            File jasifyLocalConfig = new File(System.getProperty("user.home"), "jasify.json");
            if (!jasifyLocalConfig.exists()) {
                log.error("You MUST create jasify.json (check DEVELOPER.md)!");
                throw new IllegalStateException("You MUST create jasify.json (check DEVELOPER.md)!");
            }
            try (FileReader reader = new FileReader(jasifyLocalConfig)) {
                Map map = JSON.fromJson(reader, Map.class);
                Map applicationConfig = (Map) map.get("ApplicationConfig");
                for (Object key : applicationConfig.keySet()) {
                    Object value = applicationConfig.get(key);
                    log.info("Setting Application property '{}' = '{}'", key, value);
                    ApplicationData.instance().setProperty(key.toString(), value);
                }
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    int migrateUser_v0_to_User_v1() {
        log.warn("Starting schema migration User_v0 to User_v1");

        final User_v0Meta user_v0Meta = User_v0Meta.get();
        final UserMeta userMeta = UserMeta.get();
        final UserDetailMeta userDetailMeta = UserDetailMeta.get();

        List<Entity> entities = Datastore.query(user_v0Meta.getKind()).asList();
        List<Entity> usersToUpgrade = new ArrayList<>();
        for (Entity e : entities) {
            Object schemaVersion = e.getProperty(SCHEMA_VERSION_NAME);
            if (schemaVersion != null) {
                log.debug("Skipping {} since it has {} = {}", e.getKey(), SCHEMA_VERSION_NAME, schemaVersion);
                continue;
            }

            log.info("Upgrading {}/{} to schema version 1", e.getKey(), e.getProperty(user_v0Meta.name.getName()));
            usersToUpgrade.add(e);

            User_v0 user_v0 = user_v0Meta.entityToModel(e);

            e.removeProperty(user_v0Meta.permissions.getName());
            e.removeProperty(user_v0Meta.email.getName());
            e.removeProperty(user_v0Meta.about.getName());

            if (user_v0.hasPermission(new Category("Admin")) || StringUtils.equalsIgnoreCase("krico", user_v0.getName())) {
                log.info("Upgrading {}/{} admin = true", user_v0.getId(), user_v0.getName());
                e.setProperty(userMeta.admin.getName(), true);
            }

            Email email = user_v0.getEmail();
            if (email != null) {
                String stringValue = StringUtils.lowerCase(email.getEmail());
                log.info("Upgrading {}/{} email = {}", user_v0.getId(), user_v0.getName(), stringValue);
                e.setProperty(userMeta.email.getName(), stringValue);
            }

            Text about = user_v0.getAbout();
            if (about != null) {
                UserDetail userDetail = new UserDetail(Datastore.allocateId(e.getKey(), UserDetail.class));
                userDetail.setAbout(about);
                log.info("Upgrading {}/{} moving about to {}", user_v0.getId(), user_v0.getName(), userDetail);
                Datastore.put(userDetail);
                e.setProperty("detailRef", userDetail.getId());
            }


            e.setProperty(SCHEMA_VERSION_NAME, 1);
        }

        Datastore.put(usersToUpgrade);

        return usersToUpgrade.size();
    }

    int migrateUser_v1_to_User_v2() {
        log.warn("Starting schema migration User_v1 to User_v2");

        final User_v1Meta user_v1Meta = User_v1Meta.get();
        final UserMeta userMeta = UserMeta.get();

        List<Entity> entities = Datastore.query(user_v1Meta.getKind()).asList();
        List<Entity> usersToUpgrade = new ArrayList<>();
        for (Entity e : entities) {
            Object schemaVersion = e.getProperty(SCHEMA_VERSION_NAME);
            if (schemaVersion instanceof Long && schemaVersion.equals(1L)) {
                log.info("Upgrading {}/{} to schema version 1", e.getKey(), e.getProperty(user_v1Meta.name.getName()));
                usersToUpgrade.add(e);

                e.removeProperty(user_v1Meta.nameWithCase.getName());

                e.setProperty(SCHEMA_VERSION_NAME, 2);
            } else {
                log.debug("Skipping {} since it has {} = {}", e.getKey(), SCHEMA_VERSION_NAME, schemaVersion);
            }

        }

        Datastore.put(usersToUpgrade);

        return usersToUpgrade.size();
    }

    private static class Singleton {
        private static final SchemaMigration INSTANCE = new SchemaMigration();
    }

}
