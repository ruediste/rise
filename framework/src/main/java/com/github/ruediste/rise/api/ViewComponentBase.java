package com.github.ruediste.rise.api;

import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.FragmentCanvas;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste1.i18n.lString.LString;

/**
 * Base class for view displaying {@link Component}s
 */
public abstract class ViewComponentBase<TController extends SubControllerComponent> extends AttachedPropertyBearerBase {

    @Inject
    ComponentUtil componentUtil;

    @Inject
    public CoreConfiguration config;

    protected TController controller;
    private Component rootFragment;

    public TController getController() {
        return controller;
    }

    /**
     * Initialize this view. To be called after instantiation
     */
    public final void initialize(TController controller) {
        this.controller = controller;
        FragmentCanvas<?> html = (FragmentCanvas<?>) config.createApplicationCanvas();
        html.internal_target().setController(controller);
        render(html);
        html.internal_target().commitAttributes();
        html.internal_target().flush();
        rootFragment = html.internal_target().getParentFragment();
    }

    /**
     * Render this view to the provided canvas. This method is called after the
     * instantiation of the view. The result is used to initialize
     */
    abstract protected void render(FragmentCanvas<?> html);

    protected LString label(Class<?> clazz) {
        return componentUtil.labelUtil().type(clazz).label();
    }

    protected LString label(Enum<?> e) {
        return componentUtil.labelUtil().enumMember(e).label();
    }

    protected LString label(Object obj) {
        return label(obj.getClass());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected TController go() {
        return (TController) componentUtil.go((Class) controller.getClass());
    }

    protected <T extends IController> T go(Class<T> cls) {
        return componentUtil.go(cls);
    }

    protected ActionInvocationBuilder path() {
        return componentUtil.path();
    }

    public Component getRootFragment() {
        return rootFragment;
    }

}
