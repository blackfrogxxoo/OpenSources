package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GithubDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "org.wxc.opensources");
        addNote(schema);
        new DaoGenerator().generateAll(schema, "/Users/black/StudioProjects/OpenSources/app/src/main/java-gen");
    }

    /**
     * @param schema
     */
    private static void addNote(Schema schema) {
        Entity note = schema.addEntity("Contributor");
        note.addIdProperty();
        note.addStringProperty("login").notNull().unique();
        note.addIntProperty("contributions").notNull();
    }
}
