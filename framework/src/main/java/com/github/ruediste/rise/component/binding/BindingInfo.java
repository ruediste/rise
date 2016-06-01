package com.github.ruediste.rise.component.binding;

import java.util.function.Supplier;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.fragment.ValidationStateBearer;
import com.github.ruediste.rise.component.fragment.ValueHandle;

/**
 * Encapsulates information about a binding
 */
public class BindingInfo<T> {
    public PropertyInfo modelProperty;
    public BindingTransformer<?, ?> transformer;
    public boolean isTwoWay;
    public boolean accessesController;
    public Supplier<T> lambda;

    /**
     * Supplier of the instance owing the model property
     */
    public Supplier<?> propertyOwnerSupplier;

    public Binding createBinding(ValueHandle<T> viewValueHandle) {
        return new Binding() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void pushDown() {
                // get view value
                Object obj = viewValueHandle.get();

                // process by transformer
                if (transformer != null) {
                    obj = ((TwoWayBindingTransformer) transformer).transformInv(obj);
                }

                // apply to model
                modelProperty.setValue(propertyOwnerSupplier.get(), obj);
            }

            @Override
            public void pullUp() {
                viewValueHandle.set(lambda.get());
            }

            @Override
            public ValidationStateBearer getValidationStateBearer() {
                return null;
            }

            @Override
            public String getModelPath() {
                return null;
            }
        };

    }
}
