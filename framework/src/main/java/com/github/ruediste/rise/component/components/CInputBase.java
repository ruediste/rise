package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste.rise.component.validation.ViolationStatus;
import com.github.ruediste.rise.component.validation.ViolationStatusBearer;

/**
 * Base class for input components. Provides a violationStatus and labels
 */
public class CInputBase<T extends CInputBase<T>> extends RelationsComponent<T>
        implements ViolationStatusBearer, LabeledComponentTrait<T> {

    private boolean renderFormGroup = true;

    private ViolationStatus violationStatus = new ViolationStatus();

    private LabeledComponentStatus labeledComponentStatus = new LabeledComponentStatus();

    @Override
    public LabeledComponentStatus internal_getLabeledComponentStatus() {
        return labeledComponentStatus;
    }

    public boolean isRenderFormGroup() {
        return renderFormGroup;
    }

    public T setRenderFormGroup(boolean renderFormGroup) {
        this.renderFormGroup = renderFormGroup;
        return self();
    }

    @Override
    public ViolationStatus getViolationStatus() {
        return violationStatus;
    }

}
