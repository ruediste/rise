package com.github.ruediste.rise.component;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;

public interface ViewFactory {

    public <T extends SubControllerComponent> ViewComponentBase<T> createView(T controller, boolean setAsRootView,
            Class<? extends IViewQualifier> qualifier);
}
