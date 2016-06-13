package com.github.ruediste.rise.component.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste1.i18n.lString.LString;

/**
 * Component containing a label and an icon. It is also possible to show only
 * the icon and present the label as tooltip.
 * 
 * <p>
 * The icon and label can also be represented by a method.
 */
@DefaultTemplate(CIconLabelTemplate.class)
public class CIconLabel extends ComponentBase<CIconLabel> {

    private Renderable<Html5Canvas<?>> icon;
    private LString label;

    private Method method;

    private boolean showIconOnly;
    private Class<? extends Annotation> actionAnnotation;

    public CIconLabel() {
    }

    public CIconLabel showIconOnly() {
        this.showIconOnly = true;
        return this;
    }

    public CIconLabel setShowIconOnly(boolean value) {
        this.showIconOnly = value;
        return this;
    }

    public boolean isShowIconOnly() {
        return showIconOnly;
    }

    public Method getMethod() {
        return method;
    }

    public CIconLabel setMethod(Method method) {
        this.method = method;
        return this;
    }

    public LString getLabel() {
        return label;
    }

    public CIconLabel setLabel(LString label) {
        this.label = label;
        return this;
    }

    public Renderable<Html5Canvas<?>> getIcon() {
        return icon;
    }

    public CIconLabel setIcon(Renderable<Html5Canvas<?>> icon) {
        this.icon = icon;
        return this;
    }

    public <T> CIconLabel setMethod(Class<T> cls, Consumer<T> target) {
        method = MethodInvocationRecorder.getLastInvocation(cls, target).getMethod();
        return this;
    }

    public CIconLabel setActionAnnotation(Class<? extends Annotation> actionAnnotation) {
        this.actionAnnotation = actionAnnotation;
        return this;
    }

    public Class<? extends Annotation> getActionAnnotation() {
        return actionAnnotation;
    }
}
