package com.github.ruediste.rise.component;

import java.util.Collection;
import java.util.Collections;

import javax.validation.ConstraintViolation;

public class ViolationStatus {

    private boolean isValidated;
    private Collection<ConstraintViolation<?>> constraintViolations = Collections
            .emptyList();

    public ValidationState getValidationState() {
        if (!isValidated) {
            return ValidationState.NOT_VALIDATED;
        }
        if (constraintViolations.isEmpty()) {
            return ValidationState.SUCCESS;
        }
        return ValidationState.ERROR;
    }

    public void clearConstraintViolations() {
        constraintViolations = Collections.emptyList();
    }

    public void setConstraintViolations(
            Collection<ConstraintViolation<?>> collection) {
        constraintViolations = collection;
    }

    public Collection<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean isValidated) {
        this.isValidated = isValidated;
    }
}
