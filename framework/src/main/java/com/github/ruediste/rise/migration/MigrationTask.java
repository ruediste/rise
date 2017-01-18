package com.github.ruediste.rise.migration;

import java.time.ZonedDateTime;

public abstract class MigrationTask {

    public Class<? extends MigrationTarget<?>> target;
    public MigrationTaskId id;
    private String description;

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

    @Override
    public String toString() {
        return id + " (" + target.getSimpleName() + ")" + " - " + description;
    }

    public boolean runInTransaction() {
        return false;
    }

}
