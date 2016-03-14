package com.github.ruediste.rise.component.binding.transformers;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.component.binding.TwoWayBindingTransformer;
import com.github.ruediste.rise.component.validation.ValidationException;
import com.github.ruediste.rise.core.i18n.ValidationFailureImpl;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;
import com.google.common.io.BaseEncoding;

@Singleton
public class ByteArrayToHexStringTransformer extends TwoWayBindingTransformer<byte[], String> {

    public ByteArrayToHexStringTransformer() {
    }

    @Override
    protected String transformImpl(byte[] target) {
        if (target == null)
            return "<null>";
        return BaseEncoding.base16().encode(target);
    }

    @TMessages
    private static interface Messages {
        @TMessage("Cannot interpret '{source}' as hexadecimal string: {failureMessage}")
        LString transformationFailed(String source, String failureMessage);

        @TMessage("Input '{source}' must contain an even number of characters, but length is {length}")
        LString lengthDoesNotMatch(String source, int length);

        @TMessage("Input '{source}' must contain only letters in the range A-F and digits, but found {wrongLetter}")
        LString wrongCharacterFound(String source, String wrongLetter);
    }

    @Inject
    Messages messages;

    @Override
    protected byte[] transformInvImpl(String source) {
        if ("<null>".equals(source))
            return null;
        if ((source.length() % 2) != 0) {
            throw new ValidationException(
                    new ValidationFailureImpl(messages.lengthDoesNotMatch(source, source.length())));
        }
        source.codePoints().forEach(cp -> {
            if (Character.isDigit(cp))
                return;
            if (cp >= 'A' && cp <= 'F')
                return;
            if (cp >= 'a' && cp <= 'f')
                return;

            throw new ValidationException(new ValidationFailureImpl(
                    messages.wrongCharacterFound(source, new String(new int[] { cp }, 0, 1))));
        });
        String upperSource = source.toUpperCase();
        try {
            return BaseEncoding.base16().decode(upperSource);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    new ValidationFailureImpl(messages.transformationFailed(upperSource, e.getMessage())));
        }
    }

}
