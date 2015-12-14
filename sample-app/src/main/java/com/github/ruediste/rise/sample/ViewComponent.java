package com.github.ruediste.rise.sample;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentFactory;
import com.github.ruediste.rise.component.ComponentFactoryUtil;

/**
 * Base class for views for the component framework.
 */
public abstract class ViewComponent<TController>
        extends ViewComponentBase<TController>
        implements ComponentFactory<SampleCanvas> {

    @Inject
    ComponentFactoryUtil util;

    @Override
    public ComponentFactoryUtil internal_componentFactoryUtil() {
        return util;
    }
}
