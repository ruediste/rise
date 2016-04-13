package com.github.ruediste.rise.component.binding.transformers;

import java.util.Date;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.component.binding.TwoWayBindingTransformer;
import com.github.ruediste.rise.component.validation.ValidationException;
import com.github.ruediste.rise.core.i18n.ValidationFailureImpl;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;

@Singleton
public class Transformers {

    @Inject
    public DateToStringTransformer dateToStringTransformer;

    public String dateToString(Date date) {
        return dateToStringTransformer.transform(date);
    }

    public Date stringToDate(String string) {
        return dateToStringTransformer.transformInv(string);
    }

    @Inject
    Messages messages;

    @TMessages
    interface Messages {

        @TMessage("Must be an integer: {str}")
        LString mustBeAnInteger(String str);

    }

    private class NumberTransformer<T> extends TwoWayBindingTransformer<T, String> {

        private Function<String, T> parser;

        public NumberTransformer(Function<String, T> parser) {
            this.parser = parser;
        }

        @Override
        protected T transformInvImpl(String target) {
            try {
                return target == null ? null : parser.apply(target);
            } catch (NumberFormatException e) {
                throw new ValidationException(new ValidationFailureImpl(messages.mustBeAnInteger(target)));
            }
        }

        @Override
        protected String transformImpl(T source) {
            return source == null ? null : source.toString();
        }
    }

    public final TwoWayBindingTransformer<Integer, String> intToStringTransformer = new NumberTransformer<>(
            Integer::parseInt);

    public final TwoWayBindingTransformer<Long, String> longToStringTransformer = new NumberTransformer<>(
            Long::parseLong);

    public final TwoWayBindingTransformer<Short, String> shortToStringTransformer = new NumberTransformer<>(
            Short::parseShort);

    @Inject
    public ByteArrayToHexStringTransformer byteArrayToHexStringTransformer;

    public String intToString(Integer value) {
        return intToStringTransformer.transform(value);
    }

    public Integer stringToInt(String value) {
        return intToStringTransformer.transformInv(value);
    }

    public <T> TwoWayBindingTransformer<T, T> identityTransformer() {
        return new TwoWayBindingTransformer<T, T>() {

            @Override
            protected T transformInvImpl(T target) {
                return target;
            }

            @Override
            protected T transformImpl(T source) {
                return source;
            }
        };
    }

}
