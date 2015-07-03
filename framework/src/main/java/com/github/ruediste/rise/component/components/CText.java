package com.github.ruediste.rise.component.components;

import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.ComponentBase;

@DefaultTemplate(CTextTemplate.class)
public class CText extends ComponentBase<CText> {

    private String text;

    public CText() {

    }

    public CText(String text) {
        this.text = text;

    }

    public String getText() {
        return text;
    }

    public CText setText(String text) {
        this.text = text;
        return this;
    }

    public CText bindText(Supplier<String> supplier) {
        bind(x -> x.setText(supplier.get()));
        return this;
    }
}
