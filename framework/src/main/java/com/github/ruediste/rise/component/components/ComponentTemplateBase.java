package com.github.ruediste.rise.component.components;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;

public abstract class ComponentTemplateBase<T extends Component> implements IComponentTemplate<T> {

    @Inject
    protected ComponentUtil util;

    public String url(ActionResult path) {
        return util.url(path);
    }

    public String combineCssClasses(String... classes) {
        return util.combineCssClasses(classes);
    }

    public <P extends IController> P go(Class<P> controllerClass) {
        return util.go(controllerClass);
    }

    public <P extends IController> ActionInvocationBuilderKnownController<P> path(Class<P> controllerClass) {
        return util.path(controllerClass);
    }

    public ActionInvocationBuilder path() {
        return util.path();
    }

}
