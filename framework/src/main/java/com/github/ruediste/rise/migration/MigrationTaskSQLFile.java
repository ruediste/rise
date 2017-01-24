package com.github.ruediste.rise.migration;

import javax.inject.Inject;

@MigrationTaskNoAutoDiscover
public class MigrationTaskSQLFile extends MigrationTask {
    @Inject
    ClassLoader cl;

    public String sql;

    /**
     * Initialize this task
     * 
     * @param sqlResourceName
     *            name in the format
     *            {@code <path>/<timestamp>.<author>.<description>.sql}
     */
    public MigrationTaskSQLFile initialize(String sqlResourceName, Class<? extends MigrationTarget<?>> target) {
        initializeFromResourceName(target, sqlResourceName);
        sql = loadResource(sqlResourceName);
        return this;
    }

}
