package com.github.ruediste.rise.component;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;

public interface ViewFactory {

    /**
     * Create a new view
     * 
     * @param controller
     *            controller to create the view for
     * @param setAsRootView
     *            set the view as the root view of the page
     * @param qualifier
     *            view qualifier to distinguish multiple views for a single
     *            controller, may be null
     */
    public <T extends SubControllerComponent> ViewComponentBase<T> createView(T controller, boolean setAsRootView,
            Class<? extends IViewQualifier> qualifier);
}
