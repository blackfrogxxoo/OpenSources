package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class UserDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "org.wxc.opensources");
        //addContributor(schema);
        addNote(schema);
        new DaoGenerator().generateAll(schema, "/Users/black/StudioProjects/OpenSources/app/src/main/java-gen");
    }


    /**
     * @param schema
     */
    private static void addContributor(Schema schema) {
        Entity note = schema.addEntity("Contributor");
        note.addIdProperty();
        note.addStringProperty("login").notNull().unique();
        note.addIntProperty("contributions").notNull();
    }

    /**
     * @param schema
     */
    private static void addNote(Schema schema) {
        Entity note = schema.addEntity("User");
        note.addIdProperty();
        note.addStringProperty("login").notNull();
        note.addIntProperty("userId").notNull().unique();
        note.addStringProperty("avatarUrl").notNull();
        note.addStringProperty("url").notNull();
        note.addStringProperty("htmlUrl").notNull();
        note.addStringProperty("followersUrl").notNull();
        note.addStringProperty("followingUrl").notNull();
        note.addStringProperty("gistsUrl").notNull();
        note.addStringProperty("starredUrl").notNull();
        note.addStringProperty("subscriptionsUrl").notNull();
        note.addStringProperty("organizationsUrl").notNull();
        note.addStringProperty("reposUrl").notNull();
        note.addStringProperty("eventsUrl").notNull();
        note.addStringProperty("receivedEventsUrl").notNull();
        note.addStringProperty("type").notNull();
        note.addBooleanProperty("site_admin").notNull();
    }
}
