package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.component.tree.SingleChildRelation;

public class CPage extends ComponentBase<CPage> {
    private String title;
    private final SingleChildRelation<Component, CPage> child = new SingleChildRelation<Component, CPage>(
            this);

    private CReload reload = new CReload();

    public CPage(String title) {
        this();
        this.title = title;
    }

    public CPage() {
        child.setChild(reload);
    }

    public CPage add(Component c) {
        reload.children.add(c);
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
