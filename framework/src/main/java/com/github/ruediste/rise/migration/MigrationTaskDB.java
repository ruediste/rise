package com.github.ruediste.rise.migration;

import javax.inject.Inject;

import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.salta.jsr330.Injector;

public abstract class MigrationTaskDB extends MigrationTask {

    @Inject
    Injector injector;

    @Inject
    protected TransactionControl txc;

    abstract public void execute(MigrationTargetDBBase target) throws Throwable;

}
