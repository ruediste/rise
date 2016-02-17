package com.github.ruediste.rise.core.i18n;

import java.util.Locale;

import javax.validation.MessageInterpolator;

/**
 * Validation {@link MessageInterpolator} simply returning the template as
 * message.
 * 
 * <p>
 * The actual message generation is performed by later using
 * {@link ValidationUtil#getMessage(javax.validation.ConstraintViolation)}.
 * 
 */
public class RiseValidationMessageInterpolator implements MessageInterpolator {

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        return message;

    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return messageTemplate;
    }
}
