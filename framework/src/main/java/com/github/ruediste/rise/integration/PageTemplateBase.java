package com.github.ruediste.rise.integration;

import javax.inject.Inject;

import org.rendersnake.Renderable;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;

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

    protected Renderable jsLinks(AssetBundleOutput output) {
        return util.jsLinks(output);
    }

    protected Renderable cssLinks(AssetBundleOutput output) {
        return util.cssLinks(output);
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
