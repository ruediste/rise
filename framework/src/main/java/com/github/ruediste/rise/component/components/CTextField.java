package com.github.ruediste.rise.component.components;

import com.google.common.base.Supplier;

@DefaultTemplate(CTextFieldTemplate.class)
public class CTextField extends CInputBase<CTextField> {

    private String text;
    private boolean isPassword;

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
        return bindLabelProperty(view -> view.setText(binder.get()));
    }

    public CTextField toPassword() {
        isPassword = true;
        return this;
    }

    public boolean isPassword() {
        return isPassword;
    }

}
