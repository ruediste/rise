package com.github.ruediste.rise.component.components;

import java.util.Collection;
import java.util.Collections;

import javax.validation.ConstraintViolation;

import com.github.ruediste.c3java.properties.NoPropertyAccessor;
import com.github.ruediste.c3java.properties.PropertyPath;
import com.github.ruediste.rise.component.ConstraintViolationAware;
import com.github.ruediste.rise.component.ValidationState;
import com.github.ruediste.rise.component.binding.Binding;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste1.i18n.lString.FixedLString;
import com.github.ruediste1.i18n.lString.LString;

/**
 * 
 */
public class CFormGroup<T extends RelationsComponent<T>> extends
        RelationsComponent<T> implements ConstraintViolationAware {

    private boolean isValidated;
    private Collection<ConstraintViolation<?>> constraintViolations = Collections
            .emptyList();
    private LString label;

    /**
     * Property to use to generate a label, if not set explicitely
     */
    private PropertyPath labelProperty;

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

    public LString getLabel() {
        return label;
    }

    public T setLabel(LString label) {
        this.label = label;
        return self();
    }

    @NoPropertyAccessor
    public T setLabel(String label) {
        this.label = new FixedLString(label);
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

    public PropertyPath getLabelProperty() {
        return labelProperty;
    }

    public void setLabelProperty(PropertyPath labelProperty) {
        this.labelProperty = labelProperty;
    }

    @NoPropertyAccessor
    public void setLabelProperty(Binding<?> binding) {
        setLabelProperty(binding.modelPath);
    }

}
