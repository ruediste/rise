package com.github.ruediste.rise.component.binding;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.ruediste.c3java.properties.PropertyInfo;

/**
 * Encapsulates information about a binding
 */
public class BindingInfo<T> {
    public PropertyInfo modelProperty;
    public BindingTransformer<?, ?> transformer;
    public boolean isTwoWay;
    public Supplier<T> lambda;
    public Optional<String> modelPropertyPath;

    /**
     * Supplier of the instance owing the model property
     */
    public Supplier<?> propertyOwnerSupplier;

    /**
     * Set the model property to the given value
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setModelProperty(Object value) {
        // process by transformer
        if (transformer != null) {
            value = ((TwoWayBindingTransformer) transformer).transformInv(value);
        }

        // apply to model
        modelProperty.setValue(propertyOwnerSupplier.get(), value);
    }

}
