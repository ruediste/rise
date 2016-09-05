package com.github.ruediste.rise.component.components;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.c3java.properties.NoPropertyAccessor;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

/**
 * Trait add label functions to a component.
 * 
 * <p>
 * The label is typically not rendered directly by the component but by a
 * containing component.
 */
public interface LabeledComponentTrait<TSelf extends LabeledComponentTrait<TSelf>>
        extends LabeledComponent<TSelf>, TestNamedComponent<TSelf>, AttachedPropertyBearer {

    LabeledComponentStatus internal_getLabeledComponentStatus();

    TSelf self();

    default LString getLabel() {
        return internal_getLabeledComponentStatus().getLabel();
    }

    default TSelf setLabel(LString label) {
        internal_getLabeledComponentStatus().setLabel(label);
        return self();
    }

    @NoPropertyAccessor
    default TSelf setLabel(String label) {
        internal_getLabeledComponentStatus().setLabel(label);
        return self();
    }

    default PropertyInfo getLabelProperty() {
        return internal_getLabeledComponentStatus().getLabelProperty();
    }

    /**
     * Set the label property and the {@link #TEST_NAME()}
     */
    default TSelf setLabelProperty(PropertyInfo labelProperty) {
        internal_getLabeledComponentStatus().setLabelProperty(labelProperty);
        TEST_NAME(labelProperty.getName());
        return self();
    }

    @Override
    default LString getLabel(LabelUtil labelUtil) {
        return internal_getLabeledComponentStatus().getLabel(labelUtil);
    }

}
