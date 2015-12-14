package com.github.ruediste.rise.core.navigation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Base class to define {@link Navigation}s. Provides utility functions.
 */
public abstract class NavigationsContainer {

    @Inject
    CoreUtil util;

    @Inject
    Injector injector;

    public <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    public <T extends IController> ActionInvocationBuilderKnownController<T> path(
            Class<T> controllerClass) {
        return util.path(controllerClass);
    }

    public ActionInvocationBuilder path() {
        return util.path();
    }

    @PostConstruct
    void initialize() {
        initializeImpl();
    }

    protected NavigationBuilder build() {
        return injector.getInstance(NavigationBuilder.class);
    }

    /**
     * Perform the initialization of the navigations
     */
    protected abstract void initializeImpl();

}
