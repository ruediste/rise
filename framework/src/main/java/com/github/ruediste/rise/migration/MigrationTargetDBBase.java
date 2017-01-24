package com.github.ruediste.rise.migration;

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.time.Instant;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.DependencyKey;

@Singleton
public abstract class MigrationTargetDBBase extends MigrationTarget<MigrationTaskDB> {

    @Inject
    TransactionControl txc;

    @Inject
    Injector injector;

    @Inject
    Logger log;

    private Class<? extends Annotation> annotatation;

    private Class<? extends MigrationRecordDbBase> recordClass;

    public MigrationTargetDBBase(Class<? extends Annotation> dbAnnotation,
            Class<? extends MigrationRecordDbBase> recordClass) {
        this.annotatation = dbAnnotation;
        this.recordClass = recordClass;
    }

    public EntityManager getEm() {
        DependencyKey<EntityManager> key = DependencyKey.of(EntityManager.class);
        if (annotatation != null)
            key = key.withAnnotations(annotatation);
        return injector.getInstance(key);
    }

    public DataSource getDataSource() {
        DependencyKey<DataSource> key = DependencyKey.of(DataSource.class);
        if (annotatation != null)
            key = key.withAnnotations(annotatation);
        return injector.getInstance(key);
    }

    @Override
    public boolean isAlreadyExecuted(MigrationTaskId id) {
        try {
            return txc.execute(() -> getEm().find(recordClass, toIdString(id)) != null);
        } catch (Throwable t) {
            log.error("Error while reading Migration Record, Table not created yet? Continuing ...", t);
            return true;
        }
    }

    private String toIdString(MigrationTaskId id) {
        return id.timestamp.toEpochMilli() + "." + id.author;
    }

    @Override
    public void execute(MigrationTaskDB task) {
        if (!task.runInTransaction())
            try {
                task.execute(this);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        txc.updating().execute(() -> {
            try {
                if (task.runInTransaction())
                    task.execute(this);

                // Mark task as executed
                MigrationRecordDbBase record = recordClass.newInstance();
                record.id = toIdString(task.id);
                record.executionTime = Timestamp.from(Instant.now());
                record.taskAuthor = task.id.author;
                record.taskTimeStamp = Timestamp.from(task.id.timestamp);
                record.description = task.description;
                getEm().persist(record);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }

}
