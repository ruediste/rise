package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.components.template.CTextFieldFormGroupHtmlTemplate;
import com.google.common.base.Supplier;

@DefaultTemplate(CTextFieldFormGroupHtmlTemplate.class)
public class CTextFieldFormGroup extends CFormGroup<CTextFieldFormGroup> {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CTextFieldFormGroup bindText(Supplier<String> binder) {
        setLabelProperty(BindingUtil.bind(this,
                view -> view.setText(binder.get())).getB());
        return self();
    }

}
