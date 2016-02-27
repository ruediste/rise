package com.github.ruediste.rise.component.components;

import com.github.ruediste.c3java.properties.NoPropertyAccessor;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste1.i18n.lString.FixedLString;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

/**
 * Status for the {@link LabeledComponentTrait}
 */
public class LabeledComponentStatus {

    private LString label;

    /**
     * Property to use to generate a label, if {@link #label} is null
     */
    private PropertyInfo labelProperty;

    public LString getLabel() {
        return label;
    }

    public void setLabel(LString label) {
        this.label = label;
    }

    @NoPropertyAccessor
    public void setLabel(String label) {
        this.label = new FixedLString(label);
    }

    public PropertyInfo getLabelProperty() {
        return labelProperty;
    }

    public LString getLabel(LabelUtil labelUtil) {
        if (label != null)
            return label;
        if (getLabelProperty() != null)
            return labelUtil.property(getLabelProperty()).label();
        return null;
    }

    public void setLabelProperty(PropertyInfo labelProperty) {
        this.labelProperty = labelProperty;
    }
}
