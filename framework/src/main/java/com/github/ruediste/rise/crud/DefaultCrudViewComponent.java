package com.github.ruediste.rise.crud;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentFactory;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public abstract class DefaultCrudViewComponent<TController> extends
        ViewComponentBase<TController> implements
        ComponentFactory<BootstrapRiseCanvas<?>> {

    @Inject
    ComponentFactoryUtil util;

    @Override
    public ComponentFactoryUtil internal_componentFactoryUtil() {
        return util;
    }
}