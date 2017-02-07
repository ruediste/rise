package com.github.ruediste.rise.migration;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.zeroturnaround.exec.ProcessExecutor;

import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.DependencyKey;
import com.google.common.base.Charsets;

/**
 * Command to extract all table structures from a database:
 * 
 * <p>
 * 
 * <pre>
 * {@code
 * mysqldump -d -h localhost -uuser -ppwd mvnzone --protocol=TCP > server/src/main/resources/migration/2017-01-16T18:07Z.ruediste.initial db schema.sql
 * }
 * </pre>
 */
@Singleton
public class MigrationTargetSQLFileBase extends MigrationTarget<MigrationTaskSQLFile> {

    @Inject
    Logger log;

    @Inject
    TransactionControl txc;

    @Inject
    Injector injector;

    @Inject
    DataBaseLinkRegistry dbreg;

    private Class<? extends Annotation> annotatation;

    private Class<? extends MigrationRecordDbBase> recordClass;

    private DbConnectionInfo info;

    protected void initialize(Class<? extends Annotation> dbAnnotation,
            Class<? extends MigrationRecordDbBase> recordClass, DbConnectionInfo info) {
        this.annotatation = dbAnnotation;
        this.recordClass = recordClass;
        this.info = info;
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
            return false;
        }
    }

    private String toIdString(MigrationTaskId id) {
        return id.timestamp.toEpochMilli() + "." + id.author;
    }

    @Override
    public void execute(MigrationTaskSQLFile task) {
        try {
            // execute script
            List<String> command = new ArrayList<>();
            command.add("mysql");
            command.add("--protocol=TCP");
            command.add("-h");
            command.add(info.host);
            command.add("-P");
            command.add(info.port);
            command.add("-u");
            command.add(info.user);
            command.add("-p" + info.password);
            command.add(info.database);

            log.info("executing mysql command ...");
            int exit = new ProcessExecutor(command)
                    .redirectInput(new ByteArrayInputStream(task.sql.getBytes(Charsets.UTF_8)))
                    .redirectOutput(System.out).execute().getExitValue();
            log.info("mysql execution done");

            if (exit != 0)
                throw new RuntimeException("mysql execution resulted in non-zero exit value");

            txc.updating().execute(() -> {
                try {
                    // Mark task as executed
                    MigrationRecordDbBase record = recordClass.newInstance();
                    record.id = toIdString(task.id);
                    record.executionTime = Timestamp.from(Instant.now());
                    record.taskAuthor = task.id.author;
                    record.taskTimeStamp = Timestamp.from(task.id.timestamp);
                    getEm().persist(record);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
