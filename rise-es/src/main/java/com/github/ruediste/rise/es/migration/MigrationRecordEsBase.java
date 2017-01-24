package com.github.ruediste.rise.es.migration;

import java.time.Instant;

import com.github.ruediste.rise.es.api.EsEntity;

public class MigrationRecordEsBase<T extends MigrationRecordEsBase<T>> extends EsEntity<T> {

    public Instant executionTime;

    public Instant taskTimeStamp;

    public String taskAuthor;

    public String description;
}
