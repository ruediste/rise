package com.github.ruediste.rise.core.persistence.time;

import java.time.Duration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.ruediste.rise.nonReloadable.persistence.AnyUnit;

@Converter(autoApply = true)
@AnyUnit
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration attribute) {
        if (attribute == null)
            return null;
        return attribute.toNanos();
    }

    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        if (dbData == null)
            return null;
        return Duration.ofNanos(dbData);
    }

}
