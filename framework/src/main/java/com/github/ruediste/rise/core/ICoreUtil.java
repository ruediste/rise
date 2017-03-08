package com.github.ruediste.rise.core;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.integration.AssetBundle;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.nonReloadable.lambda.LambdaExpression;
import com.github.ruediste.rise.nonReloadable.lambda.expression.MemberExpression;
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

    default ActionInvocation<String> toStringInvocation(ActionInvocation<Object> invocation) {
        return getCoreUtil().toStringInvocation(invocation);
    }

    default ActionInvocation<Supplier<Object>> toSupplierInvocation(ActionInvocation<String> stringInvocation) {
        return getCoreUtil().toSupplierInvocation(stringInvocation);
    }

    default ActionInvocation<Object> toObjectInvocation(ActionInvocation<String> stringInvocation) {
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

    default <T extends IController> ActionInvocationBuilderKnownController<T> path(Class<T> controllerClass) {
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

    default LString label(ActionResult action) {
        ActionInvocation<Object> invocation = toActionInvocation(action);
        return labelUtil().method(invocation.methodInvocation.getMethod()).label();
    }

    default LString label(@Capture Function<?, ?> function) {
        return labelOfLambda(function);
    }

    default LString label(@Capture Supplier<?> supplier) {
        return labelOfLambda(supplier);
    }

    /**
     * Return the label of the last method accessed by the given lambda method
     */
    default LString labelOfLambda(Object lambda) {
        LambdaExpression<Object> exp = LambdaExpression.parse(lambda);
        MemberExpression memberExp = exp.getMemberExpression();

        if (memberExp == null)
            throw new RuntimeException("Unable to extract accessed member");
        PropertyInfo property = PropertyUtil.getProperty(memberExp.getMember());

        return getCoreUtil().labelUtil.property(property).label();
    }

    default String toString(LString string) {
        return getCoreUtil().toString(string);
    }

    /**
     * generate the url for the given path, encoding it using
     * {@link HttpServletResponse#encodeRedirectURL(String)}
     */
    default String redirectUrl(UrlSpec path) {
        return getCoreUtil().redirectUrl(path);
    }

    /**
     * Return the referer from the HTTP request
     */
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

    default String assetUrl(AssetBundle bundle, String path) {
        return getCoreUtil().assetUrl(bundle, path);
    }

    default UrlSpec toUrlSpec(ActionInvocation<String> invocation) {
        return getCoreUtil().toUrlSpec(invocation);
    }

    default UrlSpec toUrlSpec(ActionInvocation<String> invocation, Supplier<String> sessionId) {
        return getCoreUtil().toUrlSpec(invocation, sessionId);
    }

    default UrlSpec toUrlSpec(ActionResult actionResult) {
        return getCoreUtil().toUrlSpec(actionResult);
    }

    default PathInfo toPathInfo(ActionInvocation<String> invocation) {
        return getCoreUtil().toPathInfo(invocation);
    }

    /**
     * Return the controller instance to perform authorization checks with
     */
    default <T> T getControllerAuthzInstance(Class<T> controllerClass) {
        return getCoreUtil().getControllerAuthzInstance(controllerClass);
    }

    default boolean isAutorized(ActionResult target) {
        return getCoreUtil().isAutorized(target);
    }
}
