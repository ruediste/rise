package com.github.ruediste.rise.core.persistence.time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeP {

    /**
     * The local date-time.
     */
    private final LocalDateTime dateTime;
    /**
     * The offset from UTC/Greenwich.
     */
    private final ZoneOffset offset;
    /**
     * The time-zone.
     */
    private final ZoneId zone;

    private ZonedDateTimeP(LocalDateTime dateTime, ZoneOffset offset, ZoneId zone) {
        super();
        this.dateTime = dateTime;
        this.offset = offset;
        this.zone = zone;
    }

    public static ZonedDateTimeP of(ZonedDateTime time) {
        if (time == null)
            return null;
        return new ZonedDateTimeP(time.toLocalDateTime(), time.getOffset(), time.getZone());
    }

    public ZonedDateTime toZonedDateTime() {
        return ZonedDateTime.ofStrict(dateTime, offset, zone);
    }
}
