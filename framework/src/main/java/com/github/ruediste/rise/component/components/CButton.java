package com.github.ruediste.rise.component.components;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;

/**
 * A button triggering a handler on the server. For a direct link, use
 * {@link CButtonLink}
 * 
 * <p>
 * If no children are present, the handler will be used to determine the invoked
 * proxy method on the controller. The label (mandatory) and icon (optional)
 * present on that method are shown.
 */
@DefaultTemplate(CButtonTemplate.class)
public class CButton extends MultiChildrenComponent<CButton> {
    private Runnable handler;
    private ActionResult target;

    public CButton() {
    }

    public CButton(String text) {
        add(new CText(text));
    }

    public CButton(Component child) {
        add(child);
    }

    /**
     * When the button is clicked, the handler will be called with the target as
     * argument. A {@link CIconLabel} is added as child, using the invoked
     * method to obtain a label and an (optional) icon.
     */
    public <T> CButton(T target, Consumer<T> handler) {
        this(target, handler, false);
    }

    /**
     * Create a button which will link directly to the specified target (without
     * causing a request to the containing page). A {@link CIconLabel} is added
     * as child, using the invoked action method to obtain a label and an
     * (optional) icon.
     */
    public <T> CButton(ActionResult target) {
        this(target, false);
    }

    /**
     * Create a button which will link directly to the specified target (without
     * causing a request to the containing page). A {@link CIconLabel} is added
     * as child, using the invoked action method to obtain a label and an
     * (optional) icon.
     */
    public <T> CButton(ActionResult target, boolean showIconOnly) {
        this.setTarget(target);
        Method invokedMethod = ((ActionInvocationResult) target).methodInvocation
                .getMethod();
        add(new CIconLabel().setMethod(invokedMethod).setShowIconOnly(
                showIconOnly));
    }

    /**
     * When the button is clicked, the handler will be called with the target as
     * argument. A {@link CIconLabel} is added as child, using the invoked
     * method to obtain a label and an (optional) icon.
     */
    @SuppressWarnings("unchecked")
    public <T> CButton(T target, Consumer<T> handler, boolean showIconOnly) {
        this.handler = () -> handler.accept(target);
        Method invokedMethod = MethodInvocationRecorder.getLastInvocation(
                (Class<T>) target.getClass(), handler).getMethod();
        add(new CIconLabel().setMethod(invokedMethod).setShowIconOnly(
                showIconOnly));
    }

    public CButton setHandler(Runnable handler) {
        if (target != null && handler != null)
            throw new IllegalStateException(
                    "Cannot set handler if the target is set. Clear target first");

        this.handler = handler;
        return this;
    }

    public Runnable getHandler() {
        return handler;
    }

    public ActionResult getTarget() {
        return target;
    }

    public CButton setTarget(ActionResult target) {
        if (target != null && handler != null)
            throw new IllegalStateException(
                    "Cannot set target if the handler is set. Clear handler first");
        this.target = target;
        return this;
    }

}
