package com.github.ruediste.rise.component.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.lambdaInspector.LambdaInspector;
import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss.B_ButtonArgs;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * A button triggering a handler on the server. If a handler is specified, it
 * will be called using a page reload. If a target is present, the button will
 * work as link.
 * 
 * <p>
 * If no children are present, the handler will be used to determine the invoked
 * proxy method on the controller. The label (mandatory) and icon (optional)
 * present on that method are shown.
 */
@DefaultTemplate(CButtonTemplate.class)
public class CButton extends Component<CButton> {
    private Runnable handler;
    private ActionResult target;
    private Method invokedMethod;
    private boolean isDisabled;
    private Renderable<RiseCanvas<?>> body;
    private Consumer<B_ButtonArgs<?>> args = null;

    public CButton() {
    }

    public CButton(String text) {
        this.body = html -> html.write(text);
    }

    public CButton(Runnable handler) {
        this(btn -> handler.run(), false, handler);
    }

    /**
     * When the button is clicked, the handler will be called with the target as
     * argument. A {@link CIconLabel} is added as child, using the invoked
     * method to obtain a label and an (optional) icon. In addition, the
     * {@link #TEST_NAME(String)} is set to the name of the method.
     */
    public <T> CButton(T target, Consumer<T> handler) {
        this(target, handler, false);
    }

    /**
     * When the button is clicked, the handler will be called with the target as
     * argument. A {@link CIconLabel} is added as child, using the invoked
     * method to obtain a label and an (optional) icon. In addition, the
     * {@link #TEST_NAME(String)} is set to the name of the method.
     */
    public <T> CButton(T target, BiConsumer<CButton, T> handler) {
        this(target, handler, false);
    }

    /**
     * When the button is clicked, the handler will be called with the target as
     * argument. A {@link CIconLabel} is added as child, using the invoked
     * method to obtain a label and an (optional) icon. In addition, the
     * {@link #TEST_NAME(String)} is set to the name of the method.
     */
    public <T> CButton(T target, Consumer<T> handler, boolean showIconOnly) {
        this(target, (btn, t) -> handler.accept(t), showIconOnly);
    }

    public <T> CButton(Class<? extends Annotation> actionAnnotation, Runnable handler) {
        this(actionAnnotation, handler, false);
    }

    public <T> CButton(Class<? extends Annotation> actionAnnotation, Runnable handler, boolean showIconOnly) {
        this.handler = handler;
        TEST_NAME(actionAnnotation.getSimpleName());
        body = html -> html.add(new CIconLabel().setActionAnnotation(actionAnnotation).setShowIconOnly(showIconOnly));
    }

    /**
     * Create a button which will link directly to the specified target (without
     * causing a request to the containing page). A {@link CIconLabel} is added
     * as child, using the invoked action method to obtain a label and an
     * (optional) icon. In addition, the {@link #TEST_NAME(String)} is set to
     * the name of the method.
     */
    public <T> CButton(ActionResult target) {
        this(target, false);
    }

    /**
     * Create a button which will link directly to the specified target (without
     * causing a request to the containing page). A {@link CIconLabel} is added
     * as child, using the invoked action method to obtain a label and an
     * (optional) icon. In addition, the {@link #TEST_NAME(String)} is set to
     * the name of the method.
     */
    public <T> CButton(ActionResult target, boolean showIconOnly) {
        this.setTarget(target);
        setTarget(target);
        if (invokedMethod != null)
            TEST_NAME(invokedMethod.getName());
        body = html -> html.add(new CIconLabel().setMethod(invokedMethod).setShowIconOnly(showIconOnly));
    }

    /**
     * When the button is clicked, the handler will be called with the target as
     * argument. A {@link CIconLabel} is added as child, using the invoked
     * method to obtain a label and an (optional) icon. In addition, the
     * {@link #TEST_NAME(String)} is set to the name of the method.
     */
    @SuppressWarnings("unchecked")
    public <T> CButton(T target, BiConsumer<CButton, T> handler, boolean showIconOnly) {
        this.handler = () -> handler.accept(this, target);
        invokedMethod = MethodInvocationRecorder
                .getLastInvocation((Class<T>) target.getClass(), t -> handler.accept(this, t)).getMethod();
        TEST_NAME(invokedMethod.getName());
        body = html -> html.add(new CIconLabel().setMethod(invokedMethod).setShowIconOnly(showIconOnly));
    }

    public <T> CButton(Runnable handler, boolean showIconOnly) {
        this(btn -> handler.run(), showIconOnly, handler);
    }

    public <T> CButton(Consumer<CButton> handler, boolean showIconOnly) {
        this(handler, showIconOnly, handler);
    }

    /**
     * When the button is clicked, the handler will be called with the button as
     * argumen. A {@link CIconLabel} is added as child, using the invoked method
     * to obtain a label and an (optional) icon. In addition, the
     * {@link #TEST_NAME(String)} is set to the name of the method.
     */
    @SuppressWarnings("unchecked")
    private <T> CButton(Consumer<CButton> handler, boolean showIconOnly, Object lambdaObj) {

        Member member = LambdaInspector.inspect(lambdaObj).memberHandle.getInfo().member;

        if (member instanceof Method) {
            this.handler = () -> handler.accept(this);
            invokedMethod = (Method) member;
            TEST_NAME(member.getName());
            body = html -> html.add(new CIconLabel().setMethod(invokedMethod).setShowIconOnly(showIconOnly));
        } else {
            throw new RuntimeException("CButton handler has to invoke a method");
        }
    }

    public CButton setHandler(Runnable handler) {
        if (target != null && handler != null)
            throw new IllegalStateException("Cannot set handler if the target is set. Clear target first");

        this.handler = handler;
        return this;
    }

    public Runnable getHandler() {
        return handler;
    }

    /**
     * Get the target of this button. When the button is clicked, not page
     * reload is triggered.
     */
    public ActionResult getTarget() {
        return target;
    }

    /**
     * Set the target of this button. No page request will be triggered.
     */
    public CButton setTarget(ActionResult target) {
        if (target != null && handler != null)
            throw new IllegalStateException("Cannot set target if the handler is set. Clear handler first");
        this.target = target;
        if (target != null)
            invokedMethod = ((ActionInvocationResult) target).methodInvocation.getMethod();
        return this;
    }

    @Override
    public boolean isDisabled() {
        return isDisabled;
    }

    public CButton setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        return this;
    }

    /**
     * Method beeing invoked. Can be null. Can be present both if a handler or a
     * target is defined.
     */
    public Method getInvokedMethod() {
        return invokedMethod;
    }

    public void setInvokedMethod(Method invokedMethod) {
        this.invokedMethod = invokedMethod;
    }

    public Renderable<RiseCanvas<?>> getBody() {
        return body;
    }

    public CButton body(Runnable body) {
        this.body = html -> body.run();
        return this;
    }

    public Consumer<B_ButtonArgs<?>> getArgs() {
        return args;
    }

    public CButton args(Consumer<B_ButtonArgs<?>> args) {
        this.args = args;
        return this;
    }

}
