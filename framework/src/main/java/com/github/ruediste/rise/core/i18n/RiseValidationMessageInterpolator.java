package com.github.ruediste.rise.core.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.MessageInterpolator;
import javax.validation.Payload;

import com.github.ruediste.rise.core.CurrentLocale;
import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.github.ruediste1.i18n.label.LabelUtil;

/**
 * Validation {@link MessageInterpolator} integrating the validation messages
 * into the Rise i18n system.
 * 
 * <p>
 * If a labeled payload is present, the label of the payload is used as
 * template. Altough not required, you are encouraged to use
 * {@link ValidationMessage} as base class for such payloads. If no labeled
 * payload is present and the message has the form "{......}", the string
 * without the brackets is used as key and the corresponding resource string is
 * used as template. Otherwise, the message is used directly as template.
 * 
 * <p>
 * When the template is determined, it is used to create a {@link PatternString}
 * . The arguments are the standard validation arguments (all attributes of the
 * constraint annotation) plus "validatedValue" containing the value which has
 * been validated.
 * 
 * <p>
 * finally the PatternString is resolved and returned as message.
 * 
 */
public class RiseValidationMessageInterpolator implements MessageInterpolator {
    @Inject
    PatternStringResolver patternStringResolver;

    @Inject
    TranslatedStringResolver translatedStringResolver;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    LabelUtil labelUtil;

    @Override
    public String interpolate(String message, Context context, Locale locale) {

        // arguments for the message
        Map<String, Object> args = new HashMap<>();
        args.putAll(context.getConstraintDescriptor().getAttributes());
        args.put("validatedValue", context.getValidatedValue());

        // check for labeled payload
        {
            Optional<TranslatedString> pattern = context
                    .getConstraintDescriptor().getPayload().stream()
                    .filter(Payload.class::isAssignableFrom)
                    .map(x -> labelUtil.tryGetTypeLabel(x))
                    .filter(x -> x.isPresent()).map(x -> x.get()).findFirst();
            if (pattern.isPresent())
                // we found a labeled payload. Use payload label as pattern
                return new PatternString(patternStringResolver, pattern.get(),
                        args).resolve(locale);
        }

        // check for resource key reference
        if (message.startsWith("{") && message.endsWith("}")) {
            return new PatternString(patternStringResolver,
                    new TranslatedString(translatedStringResolver,
                            message.substring(1, message.length() - 1)),
                    args).resolve(locale);
        }

        // fallback: just use the string as is as pattern
        return new PatternString(patternStringResolver, l -> message, args)
                .resolve(locale);

    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return interpolate(messageTemplate, context,
                currentLocale.getCurrentLocale());
    }
}
