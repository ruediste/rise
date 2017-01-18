package com.github.ruediste.rise.migration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MigrationTaskIdTest {

    @Test
    public void testCompare() {
        MigrationTaskId id1 = MigrationTaskId.of("2017-12-01T10:00:00Z", "aaa");
        MigrationTaskId id2 = MigrationTaskId.of("2017-12-01T11:00:00Z", "aaa");
        MigrationTaskId id3 = MigrationTaskId.of("2017-12-01T11:00:00Z", "bbb");
        MigrationTaskId id4 = MigrationTaskId.of("2017-12-01T11:00:00Z", "bbb");
        assertEquals(-1, id1.compareTo(id2));
        assertEquals(-1, id2.compareTo(id3));
        assertEquals(0, id3.compareTo(id4));
    }

}
