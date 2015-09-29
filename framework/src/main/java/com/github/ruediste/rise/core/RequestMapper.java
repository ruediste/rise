package com.github.ruediste.rise.core;

import java.util.function.Supplier;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.web.UrlSpec;

public interface RequestMapper {

    /**
     * Generate a HttpRequest for a given action invocation
     * 
     * @param sessionIdSupplier
     *            TODO
     */
    UrlSpec generate(ActionInvocation<String> invocation, Supplier<String> sessionIdSupplier);

    Class<?> getControllerImplementationClass(Class<?> controllerBaseClass);

    /**
     * Prepare this mapper for use
     */
    void initialize();

}
