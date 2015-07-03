package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.integration.RiseCanvas;

public abstract class ComponentTemplateBase<T extends Component> implements
        ComponentTemplate<T> {

    @Inject
    protected ComponentUtil util;

    @Override
    public void applyValues(T component) {

    }

    @Override
    public void raiseEvents(T component) {

    }

    public String url(PathInfo path) {
        return util.url(path);
    }

    public String url(String pathInfo) {
        return util.url(pathInfo);
    }

    public String url(ActionResult path) {
        return util.url(path);
    }

    public String combineCssClasses(String... classes) {
        return util.combineCssClasses(classes);
    }

    public <P extends IController> P go(Class<P> controllerClass) {
        return util.go(controllerClass);
    }

    public <P extends IController> ActionInvocationBuilderKnownController<P> path(
            Class<P> controllerClass) {
        return util.path(controllerClass);
    }

    public String getKey(Component component, String key) {
        return util.getKey(component, key);
    }

    public long getComponentNr(Component component) {
        return util.getComponentNr(component);
    }

    public ActionInvocationBuilder path() {
        return util.path();
    }

    public String getComponentId(Component component) {
        return util.getComponentId(component);
    }

    public String getParameterValue(Component component, String key) {
        return util.getParameterValue(component, key);
    }

    public boolean isParameterDefined(Component component, String key) {
        return util.isParameterDefined(component, key);
    }

    /**
     * To be used together with {@link RiseCanvas#render(Renderable)}.
     * 
     * <pre>
     * {@code
     * html.render(component(c));
     * }
     * </pre>
     */
    public Renderable<RiseCanvas<?>> component(Component component) {
        return util.component(component);
    }

    /**
     * To be used together with {@link RiseCanvas#render(Renderable)}.
     * 
     * <pre>
     * {@code
     * html.render(components(c.getChildren()));
     * }
     * </pre>
     */
    public Renderable<RiseCanvas<?>> components(Iterable<Component> components) {
        return util.components(components);
    }

    /**
     * To be used together with {@link RiseCanvas#render(Renderable)}.
     * 
     * <pre>
     * {@code
     * html.render(children(c)));
     * }
     * </pre>
     */
    public Renderable<RiseCanvas<?>> children(Component c) {
        return util.components(c.getChildren());
    }
}
