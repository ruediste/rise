package com.github.ruediste.rise.migration;

public abstract class MigrationTarget<T extends MigrationTask> {

    abstract public boolean isAlreadyExecuted(MigrationTaskId id);

    abstract public void execute(T task);
}
