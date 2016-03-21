package com.github.ruediste.rise.core.persistence.time;

import java.time.ZoneId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.ruediste.rise.nonReloadable.persistence.AnyUnit;

@Converter(autoApply = true)
@AnyUnit
public class ZoneIdConverter implements AttributeConverter<ZoneId, String> {

    @Override
    public String convertToDatabaseColumn(ZoneId arg) {
        if (arg == null)
            return null;
        return arg.getId();
    }

    @Override
    public ZoneId convertToEntityAttribute(String arg) {
        if (arg == null)
            return null;
        return ZoneId.of(arg);
    }

}
