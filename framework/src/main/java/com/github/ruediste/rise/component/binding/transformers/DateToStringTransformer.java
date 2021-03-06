package com.github.ruediste.rise.component.binding.transformers;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.github.ruediste.rise.component.binding.TwoWayBindingTransformer;
import com.google.common.base.Strings;

public class DateToStringTransformer extends TwoWayBindingTransformer<Date, String> {

    private DateFormat format;

    public DateToStringTransformer() {
        format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public String transformImpl(Date source) {
        if (source == null) {
            return "";
        }
        return format.format(source);
    }

    @Override
    protected Date transformInvImpl(String target) {
        if (Strings.isNullOrEmpty(target)) {
            return null;
        }
        try {
            return format.parse(target);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
