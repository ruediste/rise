package com.github.ruediste.rise.component.tree;

import java.util.ArrayList;
import java.util.List;

import com.github.ruediste.rise.component.validation.ValidationClassification;
import com.github.ruediste.rise.core.i18n.ValidationFailure;

public class ValidationStatus {

    public boolean isValidated;
    public final List<ValidationFailure> failures = new ArrayList<>();

    public ValidationClassification getClassification() {
        if (!isValidated)
            return ValidationClassification.NOT_VALIDATED;
        else if (failures.isEmpty())
            return ValidationClassification.SUCCESS;
        else
            return ValidationClassification.FAILED;
    }

    public void clear() {
        isValidated = false;
        failures.clear();
    }

    public void addFailure(ValidationFailure failure) {
        failures.add(failure);
    }

    public void addFailures(List<ValidationFailure> failures) {
        failures.addAll(failures);
    }

}
