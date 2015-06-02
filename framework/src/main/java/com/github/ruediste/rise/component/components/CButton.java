package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.components.template.CButtonHtmlTemplate;
import com.github.ruediste.rise.component.tree.Component;

@DefaultTemplate(CButtonHtmlTemplate.class)
public class CButton extends MultiChildrenComponent<CButton> {
    private Runnable handler;

    public CButton() {
    }

    public CButton(String text) {
        add(new CText(text));
    }

    public CButton(Component child) {
        add(child);
    }

    public CButton handler(Runnable handler) {
        this.handler = handler;
        return this;
    }

    public Runnable getHandler() {
        return handler;
    }

}
