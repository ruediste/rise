package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste1.i18n.lString.LString;

@DefaultTemplate(CTextTemplate.class)
public class CText extends ComponentBase<CText> {

    private LString text;

    public CText() {

    }

    public CText(LString text) {
        this.text = text;
    }

    public CText(String text) {
        this.text = locale -> text;

    }

    public LString getText() {
        return text;
    }

    public CText setTextString(String text) {
        this.text = locale -> text;
        return this;
    }

    public CText setText(LString text) {
        this.text = text;
        return this;
    }

}
