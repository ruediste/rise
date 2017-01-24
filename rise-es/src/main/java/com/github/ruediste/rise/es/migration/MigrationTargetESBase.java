package com.github.ruediste.rise.es.migration;

import java.time.Instant;

import javax.inject.Inject;

import org.apache.commons.codec.Charsets;

import com.github.ruediste.rise.es.EsHelper;
import com.github.ruediste.rise.migration.MigrationTarget;
import com.github.ruediste.rise.migration.MigrationTaskId;
import com.google.common.io.BaseEncoding;

public class MigrationTargetESBase extends MigrationTarget<MigrationTaskESBase> {

    @Inject
    EsHelper es;
    private Class<? extends MigrationRecordEsBase<?>> recordClass;

    public MigrationTargetESBase(Class<? extends MigrationRecordEsBase<?>> recordClass) {
        this.recordClass = recordClass;
    }

    private String createIdString(MigrationTaskId id) {
        return BaseEncoding.base64Url().omitPadding()
                .encode((id.timestamp.toEpochMilli() + "." + id.author).getBytes(Charsets.UTF_8));
    }

    @Override
    public boolean isAlreadyExecuted(MigrationTaskId id) {
        return es.get(recordClass, createIdString(id)).isPresent();
    }

    @Override
    public void execute(MigrationTaskESBase task) {
        try {
            // execute task
            task.execute();

            // mark task as executed
            MigrationRecordEsBase<?> record = recordClass.newInstance();
            record.setId(createIdString(task.id));
            record.taskTimeStamp = task.id.timestamp;
            record.taskAuthor = task.id.author;
            record.executionTime = Instant.now();
            record.description = task.description;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
