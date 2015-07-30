package com.github.ruediste.rise.component.components;

import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.PathInfo;

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

    @Override
    public HttpRenderResult handleAjaxRequest(T component, String suffix)
            throws Throwable {
        throw new UnsupportedOperationException();
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

    public Optional<String> getParameterValue(Component component, String key) {
        return util.getParameterValue(component, key);
    }

    public boolean isParameterDefined(Component component, String key) {
        return util.isParameterDefined(component, key);
    }

    public String getAjaxUrl(Component component) {
        return util.getAjaxUrl(component);
    }

}
