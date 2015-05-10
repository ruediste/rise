package com.github.ruediste.rise.component.components;

import java.util.Collection;
import java.util.Collections;

import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.component.ConstraintViolationAware;
import com.github.ruediste.rise.component.ValidationState;
import com.github.ruediste.rise.component.tree.ComponentBase;

public class CFormGroup<T extends ComponentBase<T>> extends ComponentBase<T>
		implements ConstraintViolationAware {

	private boolean isValidated;
	private Collection<ConstraintViolation<?>> constraintViolations = Collections
			.emptyList();
	private String label;

	@Override
	public void clearConstraintViolations() {
		isValidated = false;
	}

	@Override
	public void setConstraintViolations(
			Collection<ConstraintViolation<?>> constraintViolations) {
		this.constraintViolations = constraintViolations;
		isValidated = true;
	}

	public boolean isValidated() {
		return isValidated;
	}

	public Collection<ConstraintViolation<?>> getConstraintViolations() {
		return constraintViolations;
	}

	public String getLabel() {
		return label;
	}

	public T setLabel(String label) {
		this.label = label;
		return self();
	}

	public ValidationState getValidationState() {
		if (!isValidated) {
			return ValidationState.NOT_VALIDATED;
		}
		if (constraintViolations.isEmpty()) {
			return ValidationState.SUCCESS;
		}
		return ValidationState.ERROR;
	}
}
