package com.github.ruediste.rise.component.components;

import java.util.Collections;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;

/**
 * Component rendering either a fixed sub view, or the view belonging to a
 * controller
 */
@DefaultTemplate(RenderChildrenTemplate.class)
public class CSubView extends ComponentBase<CSubView> {

    private ViewComponentBase<?> view;

    public CSubView() {
    }

    public CSubView(ViewComponentBase<?> view) {
        setView(view);
    }

    @Override
    public Iterable<Component> getChildren() {
        if (view == null)
            return Collections.emptyList();
        return Collections.singleton(view.getRootComponent());
    }

    @Override
    public void childRemoved(Component child) {
        throw new UnsupportedOperationException();
    }

    public ViewComponentBase<?> getView() {
        return view;
    }

    public void setView(ViewComponentBase<?> view) {
        if (this.view != null) {
            view.getRootComponent().parentChanged(null);
        }
        this.view = view;
        if (this.view != null) {
            if (view.getRootComponent().getParent() != null)
                view.getRootComponent().getParent().childRemoved(view.getRootComponent());
            view.getRootComponent().parentChanged(this);
        }
    }

}
