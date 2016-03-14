package com.github.ruediste.rise.component;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

/**
 * {@link ViewComponentBase} implementation used for component views provided by
 * the framework.
 */
public abstract class FrameworkViewComponent<TController> extends ViewComponentBase<TController>
        implements ComponentFactory<BootstrapRiseCanvas<?>> {

    @Inject
    ComponentFactoryUtil util;

    @Override
    public ComponentFactoryUtil internal_componentFactoryUtil() {
        return util;
    }
}