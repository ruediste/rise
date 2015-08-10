package com.github.ruediste.rise.component.components;

import com.github.ruediste.c3java.properties.NoPropertyAccessor;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.ViolationStatus;
import com.github.ruediste.rise.component.ViolationStatusBearer;
import com.github.ruediste.rise.component.binding.Binding;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste1.i18n.lString.FixedLString;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

/**
 * 
 */
public class CInputBase<T extends RelationsComponent<T>> extends
        RelationsComponent<T> implements ViolationStatusBearer,
        LabeledComponent {

    private LString label;

    /**
     * Property to use to generate a label, if {@link #label} is null
     */
    private PropertyInfo labelProperty;

    private boolean renderFormGroup = true;

    private ViolationStatus violationStatus = new ViolationStatus();

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

    public PropertyInfo getLabelProperty() {
        return labelProperty;
    }

    /**
     * set the property used to get the label from. In addition, set the
     * {@link #TEST_NAME()}
     */
    public void setLabelProperty(PropertyInfo labelProperty) {
        this.labelProperty = labelProperty;
        TEST_NAME(labelProperty.getName());
    }

    @NoPropertyAccessor
    public void setLabelProperty(Binding<?> binding) {
        setLabelProperty(binding.modelPath.getAccessedProperty());
    }

    public boolean isRenderFormGroup() {
        return renderFormGroup;
    }

    public T setRenderFormGroup(boolean renderFormGroup) {
        this.renderFormGroup = renderFormGroup;
        return self();
    }

    @Override
    public LString getLabel(LabelUtil labelUtil) {
        if (label != null)
            return label;
        if (labelProperty != null)
            return labelUtil.getPropertyLabel(labelProperty);
        return null;
    }

    @Override
    public ViolationStatus getViolationStatus() {
        return violationStatus;
    }

}
