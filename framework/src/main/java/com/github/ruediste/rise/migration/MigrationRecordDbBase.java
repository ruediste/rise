package com.github.ruediste.rise.migration;

import java.sql.Timestamp;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class MigrationRecordDbBase {
    @Id
    public String id;

    public Timestamp executionTime;

    public Timestamp taskTimeStamp;

    public String taskAuthor;

    public String description;
}
