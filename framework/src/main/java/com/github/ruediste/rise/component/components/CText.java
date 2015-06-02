package com.github.ruediste.rise.component.components;

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

    public void setText(String text) {
        this.text = text;
    }
}
