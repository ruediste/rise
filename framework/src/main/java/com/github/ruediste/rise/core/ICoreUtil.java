package com.github.ruediste.rise.core;

import java.util.function.Supplier;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;

public interface ICoreUtil {

    CoreUtil getCoreUtil();

    default HttpRequest toHttpRequest(ActionInvocation<Object> invocation) {
        return getCoreUtil().toHttpRequest(invocation);
    }

    default String url(PathInfo path) {
        return getCoreUtil().url(path);
    }

    default String url(UrlSpec spec) {
        return getCoreUtil().url(spec);
    }

    default String url(String pathInfo) {
        return getCoreUtil().url(pathInfo);
    }

    default String url(ActionResult path) {
        return getCoreUtil().url(path);
    }

    default ActionInvocation<String> toStringInvocation(
            ActionInvocation<Object> invocation) {
        return getCoreUtil().toStringInvocation(invocation);
    }

    default ActionInvocation<Supplier<Object>> toSupplierInvocation(
            ActionInvocation<String> stringInvocation) {
        return getCoreUtil().toSupplierInvocation(stringInvocation);
    }

    default ActionInvocation<Object> toObjectInvocation(
            ActionInvocation<String> stringInvocation) {
        return getCoreUtil().toObjectInvocation(stringInvocation);
    }

    default PathInfo toPathInfo(ActionResult invocation) {
        return getCoreUtil().toPathInfo(invocation);
    }

    default String combineCssClasses(String... classes) {
        return getCoreUtil().combineCssClasses(classes);
    }

    default <T extends IController> T go(Class<T> controllerClass) {
        return getCoreUtil().go(controllerClass);
    }

    default <T extends IController> ActionInvocationBuilderKnownController<T> path(
            Class<T> controllerClass) {
        return getCoreUtil().path(controllerClass);
    }

    default ActionInvocationBuilder path() {
        return getCoreUtil().path();
    }

    default ActionInvocation<Object> toActionInvocation(ActionResult invocation) {
        return getCoreUtil().toActionInvocation(invocation);
    }

    default LabelUtil labelUtil() {
        return getCoreUtil().labelUtil();
    }

    default String toString(LString string) {
        return getCoreUtil().toString(string);
    }

    default String redirectUrl(UrlSpec path) {
        return getCoreUtil().redirectUrl(path);
    }

    default String refererUrl() {
        return getCoreUtil().refererUrl();
    }

    /**
     * Return the url of the pathinfo outside of a request/response. There will
     * be no session id included.
     */
    default String urlStatic(PathInfo path) {
        return getCoreUtil().urlStatic(path);
    }

    default UrlSpec toUrlSpec(ActionInvocation<String> invocation) {
        return getCoreUtil().toUrlSpec(invocation);
    }

    default UrlSpec toUrlSpec(ActionInvocation<String> invocation,
            String sessionId) {
        return getCoreUtil().toUrlSpec(invocation, sessionId);
    }

    default UrlSpec toUrlSpec(ActionResult actionResult) {
        return getCoreUtil().toUrlSpec(actionResult);
    }

    default PathInfo toPathInfo(ActionInvocation<String> invocation) {
        return getCoreUtil().toPathInfo(invocation);
    }
}
