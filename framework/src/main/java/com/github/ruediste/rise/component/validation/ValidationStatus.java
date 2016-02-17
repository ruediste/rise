package com.github.ruediste.rise.component.validation;

import java.util.List;

import com.github.ruediste.rise.core.i18n.ValidationFailure;

public class ValidationStatus {

    private final ValidationState state;
    private final List<ValidationFailure> failures;

    public ValidationStatus(ValidationState state,
            List<ValidationFailure> failures) {
        super();
        this.state = state;
        this.failures = failures;
    }

    public ValidationState getState() {
        return state;
    }

    public List<ValidationFailure> getFailures() {
        return failures;
    }

}
