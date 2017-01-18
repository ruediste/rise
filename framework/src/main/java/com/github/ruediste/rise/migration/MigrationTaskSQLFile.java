package com.github.ruediste.rise.migration;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

@MigrationTaskNoAutoDiscover
public class MigrationTaskSQLFile extends MigrationTask {
    @Inject
    ClassLoader cl;

    public String sql;

    /**
     * Initialize this task
     * 
     * @param sqlResourceName
     *            name in the format <path>/<timestamp>.<author>.
     *            <description>.sql
     */
    public MigrationTaskSQLFile initialize(String sqlResourceName, Class<? extends MigrationTarget<?>> target) {
        String fileName = sqlResourceName.substring(sqlResourceName.lastIndexOf('/') + 1);
        String[] parts = fileName.split("\\.");
        initialize(target, parts[0], parts[1], parts.length == 3 ? "" : parts[2]);
        try (InputStream in = cl.getResourceAsStream(sqlResourceName)) {
            sql = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

}
