package com.github.ruediste.rise.core.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Payload;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValidationStateBearer;
import com.github.ruediste.rise.component.validation.ValidationPathUtil;
import com.github.ruediste.rise.component.validation.ValidationState;
import com.github.ruediste.rise.component.validation.ValidationStatus;
import com.github.ruediste.rise.util.Var;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.github.ruediste1.i18n.label.LabelUtil;

public class ValidationUtil {
    @Inject
    PatternStringResolver patternStringResolver;

    @Inject
    TranslatedStringResolver translatedStringResolver;

    @Inject
    LabelUtil labelUtil;

    @Inject
    ValidationPathUtil validationPathUtil;

    /**
     * Calculate a localized string message for a constraint violation.
     * <p>
     * If a labeled payload is present, the label of the payload is used as
     * template. Although not required, you are encouraged to use
     * {@link ValidationMessage} as base class for such payloads. If no labeled
     * payload is present and the message has the form "{......}", the string
     * without the brackets is used as key and the corresponding resource string
     * is used as template. Otherwise, the message is used directly as template.
     * 
     * <p>
     * When the template is determined, it is used to create a
     * {@link PatternString} . The arguments are the standard validation
     * arguments (all attributes of the constraint annotation) plus
     * "invalidValue" containing the value which has been validated.
     * 
     * <p>
     * finally the PatternString is resolved and returned as message.
     * 
     */
    public LString getMessage(ConstraintViolation<?> violation) {

        // arguments for the message
        Map<String, Object> args = new HashMap<>();
        args.putAll(violation.getConstraintDescriptor().getAttributes());
        args.put("invalidValue", violation.getInvalidValue());

        // check for labeled payload
        {
            Optional<TranslatedString> pattern = violation.getConstraintDescriptor().getPayload().stream()
                    .filter(Payload.class::isAssignableFrom).map(x -> labelUtil.type(x).tryLabel())
                    .filter(x -> x.isPresent()).map(x -> x.get()).findFirst();
            if (pattern.isPresent())
                // we found a labeled payload. Use payload label as pattern
                return new PatternString(patternStringResolver, pattern.get(), args);
        }

        // check for resource key reference
        String messageTemplate = violation.getMessageTemplate();
        if (messageTemplate.startsWith("{") && messageTemplate.endsWith("}")) {
            return new PatternString(patternStringResolver, new TranslatedString(translatedStringResolver,
                    messageTemplate.substring(1, messageTemplate.length() - 1)), args);
        }

        // fallback: just use the string as is as pattern
        return new PatternString(patternStringResolver, LString.of(messageTemplate), args);
    }

    public List<ValidationFailure> toFailures(Iterable<? extends ConstraintViolation<?>> violations) {
        return toFailures(violations, ValidationFailureSeverity.ERROR);
    }

    public List<ValidationFailure> toFailures(Iterable<? extends ConstraintViolation<?>> violations,
            ValidationFailureSeverity severity) {
        if (violations == null)
            return Collections.emptyList();
        return StreamSupport.stream(violations.spliterator(), false)
                .<ValidationFailure> map(v -> toFailure(v, severity)).collect(Collectors.toList());
    }

    public ValidationFailure toFailure(ConstraintViolation<?> violation) {
        return toFailure(violation, ValidationFailureSeverity.ERROR);
    }

    public ValidationFailure toFailure(ConstraintViolation<?> violation, ValidationFailureSeverity severity) {
        LString message = getMessage(violation);
        return new ValidationFailure() {

            @Override
            public ValidationFailureSeverity getSeverity() {
                return severity;
            }

            @Override
            public LString getMessage() {
                return message;
            }

            @Override
            public String toString() {
                return "ValidationFailure(" + severity + ";" + message + ")";
            }
        };
    }

    public ValidationStatus getValidationState(Component fragment) {
        ArrayList<ValidationFailure> failures = new ArrayList<>();
        Var<Boolean> validated = new Var<>(false);
        fragment.forEachNonValidationPresenterInSubTree(f -> {
            ValidationStateBearer bearer = f.getValidationStateBearer();
            if (bearer.isDirectlyValidated())
                validated.setValue(true);

            // add failures from fragment
            failures.addAll(bearer.getDirectValidationFailures());

            // add failures registered with the controller
            f.getBindingInfos().forEach(pair -> {
                pair.getB().modelPropertyPath
                        .flatMap(path -> Optional.ofNullable(pair.getA().getValidationFailureMap().get(path)))
                        .ifPresent(failures::addAll);
            });
        });

        if (!validated.getValue())
            return new ValidationStatus(ValidationState.NOT_VALIDATED, Collections.emptyList());
        else {
            if (failures.isEmpty())
                return new ValidationStatus(ValidationState.SUCCESS, failures);
            else
                return new ValidationStatus(ValidationState.FAILED, failures);
        }
    }
}
