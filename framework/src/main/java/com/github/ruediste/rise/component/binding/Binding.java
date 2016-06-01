package com.github.ruediste.rise.component.binding;

import com.github.ruediste.rise.component.fragment.ValidationStateBearer;

/**
 * Represents a general binding
 * 
 * <img src="doc-files/bindingOverview.png" alt="">
 */
public interface Binding {

    void pullUp();

    void pushDown();

    /**
     * Return the validation state bearer for this binding, used to propagate
     * validation failures. Null if no bearer is available.
     */
    ValidationStateBearer getValidationStateBearer();

    /**
     * Return the property path of the bound model path, null if not known.
     */
    String getModelPath();
}
