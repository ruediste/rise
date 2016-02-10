package com.github.ruediste.rise.api;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.RedirectRenderResult;

/**
 * Base class for component sub controllers
 */
public class SubControllerComponent {

    @Inject
    ComponentUtil util;

    public SubControllerComponent() {
        super();
    }

    public <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    public <T extends IController> ActionInvocationBuilderKnownController<T> path(
            Class<T> controllerClass) {
        return util.path(controllerClass);
    }

    public ActionInvocationBuilder path() {
        return util.path();
    }

    public void commit() {
        util.commit();
    }

    public void commit(Runnable inTransaction) {
        util.commit(inTransaction);
    }

    public void checkAndCommit(Runnable checker) {
        util.checkAndCommit(checker);
    }

    public void checkAndCommit(Runnable checker, Runnable inTransaction) {
        util.checkAndCommit(checker, inTransaction);
    }

    public void closePage(HttpRenderResult closePageResult) {
        util.closePage(closePageResult);
    }

    /**
     * In the next render phase the page will be closed and a redirect to the
     * given destination will be returned.
     * 
     * <pre>
     * {@code
     * redirect(go(WelcomeController.class).index());
     * }
     * </pre>
     */
    public void redirect(ActionResult destination) {
        closePage(new RedirectRenderResult(util.toUrlSpec(destination)));
    }

    public <T> void setConstraintViolations(BindingGroup<T> group,
            Set<ConstraintViolation<T>> violations) {
        util.setConstraintViolations(group, violations);
    }
}