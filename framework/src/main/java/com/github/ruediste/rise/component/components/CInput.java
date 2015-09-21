package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.binding.BindingUtil;
import com.google.common.base.Supplier;

@DefaultTemplate(CInputTemplate.class)
public class CInput extends CInputBase<CInput> {

    private String value;
    private InputType inputType;

    public CInput() {

    }

    public CInput(InputType inputType) {
        this.setInputType(inputType);
    }

    public String getValue() {
        return value;
    }

    public CInput setValue(String text) {
        this.value = text;
        return this;
    }

    /**
     * Bind the value to a property. In addition, the {@link #TEST_NAME()} is
     * set to the name of the property.
     * 
     * <pre>
     * {@code
     * new CInput(InputType.date).bindValue(
     *   () -> controller.data().getPerson().getBirthDate())
     * }
     * </pre>
     */
    public CInput bindValue(Supplier<String> binder) {
        setLabelProperty(BindingUtil
                .bind(this, view -> view.setValue(binder.get())).getB());
        return self();
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

}
