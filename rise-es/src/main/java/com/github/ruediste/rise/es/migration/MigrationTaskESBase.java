package com.github.ruediste.rise.es.migration;

import com.github.ruediste.rise.migration.MigrationTask;

public abstract class MigrationTaskESBase extends MigrationTask {

    public abstract void execute() throws Throwable;
}
