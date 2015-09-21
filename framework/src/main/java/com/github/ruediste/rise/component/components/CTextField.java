package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.binding.BindingUtil;
import com.google.common.base.Supplier;

@DefaultTemplate(CTextFieldTemplate.class)
public class CTextField extends CInputBase<CTextField> {

    private String text;

    public String getText() {
        return text;
    }

    public CTextField setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Bind the text to a property. In addition, the {@link #TEST_NAME()} is set
     * to the name of the property.
     * 
     * <pre>
     * {@code
     * new CTextField().bindText(
     *   () -> controller.data().getPerson().getFirstName())
     * }
     * </pre>
     */
    public CTextField bindText(Supplier<String> binder) {
        setLabelProperty(BindingUtil
                .bind(this, view -> view.setText(binder.get())).getB());
        return self();
    }
}
