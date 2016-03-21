package com.github.ruediste.rise.core.persistence.time;

import java.time.Instant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.ruediste.rise.nonReloadable.persistence.AnyUnit;

@Converter(autoApply = true)
@AnyUnit
public class InstantConverter implements AttributeConverter<Instant, Long> {

    @Override
    public Long convertToDatabaseColumn(Instant attribute) {
        if (attribute == null)
            return null;
        return attribute.toEpochMilli();
    }

    @Override
    public Instant convertToEntityAttribute(Long dbData) {
        if (dbData == null)
            return null;
        return Instant.ofEpochMilli(dbData);
    }

}
