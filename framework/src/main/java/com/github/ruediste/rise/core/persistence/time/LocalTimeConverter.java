package com.github.ruediste.rise.core.persistence.time;

import java.time.LocalTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.ruediste.rise.nonReloadable.persistence.AnyUnit;

@Converter(autoApply = true)
@AnyUnit
public class LocalTimeConverter implements AttributeConverter<LocalTime, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalTime arg) {
        if (arg == null)
            return null;
        return arg.toNanoOfDay();
    }

    @Override
    public LocalTime convertToEntityAttribute(Long arg) {
        if (arg == null)
            return null;
        return LocalTime.ofNanoOfDay(arg);
    }

}
