package com.github.ruediste.rise.component.components;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvasCss.B_ButtonArgs;
import com.github.ruediste.rise.component.tree.Component;

/**
 * Represents a button.
 * 
 * <p>
 * If no children are present, the handler will be used to determine the invoked
 * proxy method on the controller. The label (mandatory) and icon (optional)
 * present on that method are shown.
 */
@DefaultTemplate(CButtonHtmlTemplate.class)
public class CButton extends MultiChildrenComponent<CButton> {
    private Runnable handler;
    private Method invokedMethod;
    private Consumer<B_ButtonArgs> args = x -> {
    };

    private boolean iconOnly;

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
     * argument.
     * 
     * <p>
     * The button is rendered using the label an icon of the method invoked by
     * the handler.
     * 
     */
    @SuppressWarnings("unchecked")
    public <T> CButton(T target, Consumer<T> handler) {
        this.handler = () -> handler.accept(target);
        invokedMethod = MethodInvocationRecorder.getLastInvocation(
                (Class<T>) target.getClass(), handler).getMethod();
    }

    public CButton handler(Runnable handler) {
        this.handler = handler;
        return this;
    }

    public Runnable getHandler() {
        return handler;
    }

    /**
     * Return the method which get's invoked by this button. If non-null, the
     * optional icon and the label should be rendered instead of the children.
     */
    public Method getInvokedMethod() {
        return invokedMethod;
    }

    public CButton args(Consumer<B_ButtonArgs> args) {
        this.args = args;
        return this;
    }

    public Consumer<B_ButtonArgs> getArgs() {
        return args;
    }

    public boolean isIconOnly() {
        return iconOnly;
    }

    /**
     * If set to true, only the icon will be shown if the {@link #invokedMethod}
     * is set.
     */
    public CButton setIconOnly(boolean iconOnly) {
        this.iconOnly = iconOnly;
        return this;
    }

}
