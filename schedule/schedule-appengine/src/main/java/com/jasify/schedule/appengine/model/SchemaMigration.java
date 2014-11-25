package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.jasify.schedule.appengine.meta.users.UserDetailMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.meta.users.User_v0Meta;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.users.UserDetail;
import com.jasify.schedule.appengine.model.users.User_v0;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

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

    public boolean executePendingMigrations() {
        ApplicationData applicationData = ApplicationData.instance();

        String user_v0_key = SchemaMigration.class.getName() + ".User_v0";
        Boolean user_v0_migrated = applicationData.getProperty(user_v0_key);
        if (Boolean.TRUE != user_v0_migrated) {
            migrateUser_v0_to_User_v1();
            applicationData.setProperty(user_v0_key, true);
            return true;
        } else {
            log.debug("No migrations pending...");
            return false;
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

    private static class Singleton {
        private static final SchemaMigration INSTANCE = new SchemaMigration();
    }

}
