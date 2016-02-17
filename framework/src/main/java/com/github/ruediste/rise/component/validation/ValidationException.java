package com.github.ruediste.rise.component.validation;

import java.util.Collections;
import java.util.List;

import com.github.ruediste.rise.core.i18n.ValidationFailure;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final List<ValidationFailure> failures;

    public ValidationException(ValidationFailure failure) {
        this(Collections.singletonList(failure));
    }

    public ValidationException(List<ValidationFailure> failures) {
        this.failures = failures;
    }

    public List<ValidationFailure> getFailures() {
        return failures;
    }

    @Override
    public String getMessage() {
        return "ValidationException(" + failures + ")";
    }
}
