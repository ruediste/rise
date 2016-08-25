package com.github.ruediste.rise.component.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.ruediste.rise.component.validation.ValidationState;
import com.github.ruediste.rise.component.validation.ValidationStatus;
import com.github.ruediste.rise.core.i18n.ValidationFailure;

public class ValidationStateBearer {

    private boolean isDirectlyValidated;
    private final List<ValidationFailure> directValidationFailures = new ArrayList<>();

    public ValidationStatus getDirectValidationState() {
        if (!isDirectlyValidated)
            return new ValidationStatus(ValidationState.NOT_VALIDATED, Collections.emptyList());
        else if (directValidationFailures.isEmpty())
            return new ValidationStatus(ValidationState.SUCCESS, directValidationFailures);
        else
            return new ValidationStatus(ValidationState.FAILED, directValidationFailures);
    }

    public void clearDirectValidationFailures() {
        directValidationFailures.clear();
    }

    public List<ValidationFailure> getDirectValidationFailures() {
        return directValidationFailures;
    }

    public boolean isDirectlyValidated() {
        return isDirectlyValidated;
    }

    public void setValidated(boolean isValidated) {
        this.isDirectlyValidated = isValidated;
    }

    public void addFailures(List<ValidationFailure> failures) {
        directValidationFailures.addAll(failures);
    }

}
