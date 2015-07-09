package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste.rise.component.tree.SingleChildRelation;
import com.github.ruediste1.i18n.lString.LString;

public class CPage extends RelationsComponent<CPage> {
    private LString title;
    private final SingleChildRelation<Component, CPage> child = new SingleChildRelation<Component, CPage>(
            this);

    private CReload reload = new CReload();

    public CPage(LString title) {
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

    public LString getTitle() {
        return title;
    }

    public void setTitle(LString title) {
        this.title = title;
    }
}
