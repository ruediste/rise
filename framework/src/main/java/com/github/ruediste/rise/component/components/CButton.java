package com.github.ruediste.rise.component.components;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rise.component.tree.Component;

@DefaultTemplate(CButtonHtmlTemplate.class)
public class CButton extends MultiChildrenComponent<CButton> {
    private Runnable handler;

    public CButton() {
    }

    public CButton(String text) {
        add(new CText(text));
    }

    public CButton(Component child) {
        add(child);
    }

    public CButton handler(Runnable handler) {
        this.handler = handler;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> CButton handler(T target, Consumer<T> handler) {
        Method method = MethodInvocationRecorder.getLastInvocation(
                (Class<T>) target.getClass(), handler).getMethod();
        this.handler = () -> handler.accept(target);
        ;
        return this;
    }

    public Runnable getHandler() {
        return handler;
    }

}
