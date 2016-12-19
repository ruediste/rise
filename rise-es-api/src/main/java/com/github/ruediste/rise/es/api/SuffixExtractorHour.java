package com.github.ruediste.rise.es.api;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

public abstract class SuffixExtractorHour<T> implements IndexSuffixExtractor<T> {

    private static final DateTimeFormatter indexFormat = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('.').appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('.').appendValue(DAY_OF_MONTH, 2).appendLiteral("-").appendValue(ChronoField.HOUR_OF_DAY)
            .toFormatter(Locale.ENGLISH);

    @Override
    public String extract(T entity) {
        return indexFormat.format(getInstant(entity).atOffset(ZoneOffset.UTC));
    }

    protected abstract Instant getInstant(T entity);
}
