package com.github.ruediste.rise.testApp.component;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentFactory;
import com.github.ruediste.rise.component.ComponentFactoryUtil;
import com.github.ruediste.rise.testApp.TestCanvas;

public abstract class ViewComponent<TController> extends
        ViewComponentBase<TController> implements ComponentFactory<TestCanvas> {

    @Inject
    ComponentFactoryUtil util;

    @Override
    public ComponentFactoryUtil internal_componentFactoryUtil() {
        return util;
    }
}
