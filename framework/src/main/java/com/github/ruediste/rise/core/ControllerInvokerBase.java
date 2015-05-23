package com.github.ruediste.rise.core;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.web.HttpRenderResult;

public abstract class ControllerInvokerBase implements Runnable {

    protected abstract Object getController(
            ActionInvocation<String> stringActionInvocation);

    @Inject
    CoreRequestInfo coreInfo;

    @Inject
    CoreUtil coreUtil;

    public ControllerInvokerBase() {
        super();
    }

    @Override
    public void run() {
        ActionInvocation<String> stringActionInvocation = coreInfo
                .getStringActionInvocation();

        // instantiate controller
        Object controller = getController(stringActionInvocation);

        // convert String parameters to objects
        ActionInvocation<Object> objectInvocation = coreUtil
                .toObjectInvocation(stringActionInvocation);

        // invoke controller
        try {
            ActionResult result = (ActionResult) objectInvocation.methodInvocation
                    .getMethod().invoke(
                            controller,
                            objectInvocation.methodInvocation.getArguments()
                                    .toArray());
            coreInfo.setActionResult((HttpRenderResult) result);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("Error calling action method "
                    + objectInvocation.methodInvocation.getMethod(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                    "Error during invocation of action method "
                            + objectInvocation.methodInvocation.getMethod(),
                    e.getCause());
        }

    }

}