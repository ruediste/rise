package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste1.i18n.lString.LString;

@DefaultTemplate(CTextTemplate.class)
public class CText extends RelationsComponent<CText> {

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

    public CText bindTextString(Supplier<String> supplier) {
        bind(x -> x.setTextString(supplier.get()));
        return this;
    }

    public CText bindText(Supplier<LString> supplier) {
        bind(x -> x.setText(supplier.get()));
        return this;
    }
}
