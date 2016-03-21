package com.github.ruediste.rise.core.persistence.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.ruediste.rise.nonReloadable.persistence.AnyUnit;

@Converter(autoApply = true)
@AnyUnit
public class LocalDateConverter implements AttributeConverter<LocalDate, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDate attribute) {
        if (attribute == null)
            return null;
        return attribute.atStartOfDay().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    @Override
    public LocalDate convertToEntityAttribute(Long dbData) {
        if (dbData == null)
            return null;
        return Instant.ofEpochMilli(dbData).atOffset(ZoneOffset.UTC).toLocalDate();
    }

}
