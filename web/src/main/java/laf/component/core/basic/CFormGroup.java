package laf.component.core.basic;

import java.util.Collection;
import java.util.Collections;

import javax.validation.ConstraintViolation;

import laf.component.core.ConstraintViolationAware;
import laf.component.core.tree.ComponentBase;

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
