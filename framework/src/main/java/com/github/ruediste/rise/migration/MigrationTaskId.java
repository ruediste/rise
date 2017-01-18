package com.github.ruediste.rise.migration;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

public class MigrationTaskId implements Comparable<MigrationTaskId> {

    public Instant timestamp;
    public String author;

    public MigrationTaskId(Instant timestamp, String author) {
        super();
        this.timestamp = timestamp;
        this.author = author;
    }

    /**
     * 
     * @param timeStamp
     *            timestamp. see {@link ZonedDateTime#parse(CharSequence)} for
     *            format
     */
    public static MigrationTaskId of(String timeStamp, String author) {
        return new MigrationTaskId(ZonedDateTime.parse(timeStamp).toInstant(), author);
    }

    @Override
    public int compareTo(MigrationTaskId other) {
        return Comparator.<MigrationTaskId, Instant> comparing(x -> x.timestamp).thenComparing(x -> x.author)
                .compare(this, other);
    }

    @Override
    public String toString() {
        return timestamp + "(" + author + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MigrationTaskId other = (MigrationTaskId) obj;
        return Objects.equals(author, other.author) && Objects.equals(timestamp, other.timestamp);
    }

}
