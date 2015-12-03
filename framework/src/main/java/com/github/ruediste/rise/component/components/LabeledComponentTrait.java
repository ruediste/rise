package com.github.ruediste.rise.component.components;

import java.util.function.Consumer;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.c3java.properties.NoPropertyAccessor;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.binding.Binding;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.tree.TestNamedComponent;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public interface LabeledComponentTrait<TSelf extends LabeledComponentTrait<TSelf>>
        extends LabeledComponent<TSelf>, TestNamedComponent<TSelf>,
        AttachedPropertyBearer {

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

    @NoPropertyAccessor
    default TSelf setLabelProperty(Binding<?> binding) {
        return setLabelProperty(binding.modelPath.getAccessedProperty());
    }

    @Override
    default LString getLabel(LabelUtil labelUtil) {
        return internal_getLabeledComponentStatus().getLabel(labelUtil);
    }

    /**
     * Bind a property, set the label to the label of the property and the
     * {@link #TEST_NAME()} to the name of the property
     */
    default TSelf bindLabelProperty(Consumer<TSelf> accessor) {
        TSelf self = self();
        setLabelProperty(BindingUtil.bind(self, accessor).getB());
        return self;
    }
}
