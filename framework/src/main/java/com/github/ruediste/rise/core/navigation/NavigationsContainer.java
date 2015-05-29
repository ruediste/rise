package com.github.ruediste.rise.core.navigation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;

public abstract class NavigationsContainer {

    @Inject
    CoreUtil util;

    @Inject
    Provider<NavigationBuilder> builder;

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
        return builder.get();
    }

    protected abstract void initializeImpl();

}
