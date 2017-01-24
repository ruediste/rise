package com.github.ruediste.rise.migration;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public abstract class MigrationTask {

    @Inject
    ClassLoader cl;

    public Class<? extends MigrationTarget<?>> target;
    public MigrationTaskId id;
    public String description;

    /**
     * @param target
     *            class of the migration target
     * @param timeStamp
     *            timestamp. see {@link ZonedDateTime#parse(CharSequence)} for
     *            format
     * @param author
     *            author of the task
     */
    public void initialize(Class<? extends MigrationTarget<?>> target, String timestamp, String author,
            String description) {
        this.target = target;
        this.description = description;
        id = MigrationTaskId.of(timestamp, author);
    }

    protected void initializeFromResourceName(Class<? extends MigrationTarget<?>> target, String resourceName) {
        String fileName = resourceName.substring(resourceName.lastIndexOf('/') + 1);
        String[] parts = fileName.split("\\.");
        initialize(target, parts[0], parts[1], parts.length == 3 ? "" : parts[2]);
    }

    protected String loadResource(String resourceName) {
        try (InputStream in = cl.getResourceAsStream(resourceName)) {
            return new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return id + " (" + target.getSimpleName() + ")" + " - " + description;
    }

    public boolean runInTransaction() {
        return false;
    }

}
