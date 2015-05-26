package com.github.ruediste.rise.integration;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.PathInfo;

/**
 * Provide utility functions for page templates
 */
public class PageTemplateBase {

    @Inject
    private CoreUtil util;

    protected PathInfo toPathInfo(ActionResult invocation) {
        return util.toPathInfo(invocation);
    }

    protected String url(String pathInfo) {
        return util.url(pathInfo);
    }

    protected String url(PathInfo path) {
        return util.url(path);
    }

    protected String url(ActionResult path) {
        return util.url(path);
    }

    protected String combineCssClasses(String... classes) {
        return util.combineCssClasses(classes);
    }

    protected <T extends IController> T go(Class<T> controllerClass) {
        return util.go(controllerClass);
    }

    protected <T extends IController> ActionInvocationBuilderKnownController<T> path(
            Class<T> controllerClass) {
        return util.path(controllerClass);
    }

    protected ActionInvocationBuilder path() {
        return util.path();
    }

}
