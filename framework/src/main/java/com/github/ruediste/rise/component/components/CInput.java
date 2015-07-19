package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.binding.BindingUtil;
import com.google.common.base.Supplier;

@DefaultTemplate(CInputTemplate.class)
public class CInput extends CFormGroup<CInput> {

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

    public CInput bindValue(Supplier<String> binder) {
        setLabelProperty(BindingUtil.bind(this,
                view -> view.setValue(binder.get())).getB());
        return self();
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

}
