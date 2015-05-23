package com.github.ruediste.rise.core.actionInvocation;

import com.github.ruediste.rise.core.IController;
import com.google.common.base.Preconditions;

/**
 * Builder for {@link ActionInvocation}s with a known controller type. This
 * allows to provide the {@link #go()} method.
 */
public class ActionInvocationBuilderKnownController<TController extends IController>
        extends
        ActionInvocationBuilderBase<ActionInvocationBuilderKnownController<TController>> {

    private Class<TController> controllerClass;

    @SuppressWarnings("unchecked")
    public <T extends IController> ActionInvocationBuilderKnownController<T> initialize(
            Class<T> controllerClass) {
        Preconditions.checkNotNull(controllerClass);
        this.controllerClass = (Class<TController>) controllerClass;
        return (ActionInvocationBuilderKnownController<T>) self();
    }

    public TController go() {
        Preconditions.checkState(controllerClass != null,
                "ActionPathBuilder not initialized");
        return go(controllerClass);
    }
}
