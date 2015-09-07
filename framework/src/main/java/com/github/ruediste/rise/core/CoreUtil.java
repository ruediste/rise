package com.github.ruediste.rise.core;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

@Singleton
public class CoreUtil implements ICoreUtil {

    @Inject
    LabelUtil labelUtil;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    Provider<ActionInvocationBuilder> actionPathBuilderProvider;

    @Inject
    Provider<ActionInvocationBuilderKnownController<?>> actionPathBuilderKnownController;

    String contextPath;

    @PostConstruct
    public void postConstruct(@NonRestartable ServletConfig servletConfig) {
        contextPath = servletConfig.getServletContext().getContextPath();
    }

    @Override
    public PathInfo toPathInfo(ActionInvocation<String> invocation) {
        return toUrlSpec(invocation).getPathInfo();
    }

    @Override
    public UrlSpec toUrlSpec(ActionResult actionResult) {
        return toUrlSpec(toStringInvocation(actionResult));
    }

    @Override
    public UrlSpec toUrlSpec(ActionInvocation<String> invocation,
            String sessionId) {
        return coreConfiguration.getRequestMapper(
                invocation.methodInvocation.getInstanceClass()).generate(
                invocation, sessionId);
    }

    @Override
    public UrlSpec toUrlSpec(ActionInvocation<String> invocation) {
        return toUrlSpec(invocation, coreRequestInfo.getServletRequest()
                .getSession().getId());
    }

    @Override
    public PathInfo toPathInfo(ActionResult invocation) {
        return getCoreUtil().toPathInfo(
                toStringInvocation(toActionInvocation(invocation)));
    }

    @Override
    public ActionInvocation<Object> toActionInvocation(ActionResult invocation) {
        return ((ActionInvocationResult) invocation);
    }

    @Override
    public ActionInvocation<Object> toObjectInvocation(
            ActionInvocation<String> stringInvocation) {
        return toSupplierInvocation(stringInvocation).map(Supplier::get);
    }

    @Override
    public ActionInvocation<Supplier<Object>> toSupplierInvocation(
            ActionInvocation<String> stringInvocation) {
        return stringInvocation.mapWithType(coreConfiguration::parseArgument);
    }

    public ActionInvocation<String> toStringInvocation(ActionResult actionResult) {
        return toStringInvocation(toActionInvocation(actionResult));
    }

    @Override
    public ActionInvocation<String> toStringInvocation(
            ActionInvocation<Object> invocation) {

        try {
            return invocation.mapWithType(coreConfiguration::generateArgument);
        } catch (Throwable t) {
            throw new RuntimeException("Error while generating arguments of "
                    + invocation, t);
        }
    }

    @Override
    public String url(UrlSpec spec) {
        String url = contextPath;
        url += coreRequestInfo.getServletRequest().getServletPath();
        url += spec.urlSuffix();
        return coreRequestInfo.getServletResponse().encodeURL(url);
    }

    @Override
    public String url(String pathInfo) {
        return url(new PathInfo(pathInfo));
    }

    @Override
    public String url(PathInfo path) {
        return url(new UrlSpec(path));
    }

    @Override
    public String url(ActionResult path) {
        return url(toUrlSpec(path));
    }

    @Override
    public String redirectUrl(UrlSpec path) {
        String prefix = coreRequestInfo.getServletRequest().getContextPath();
        prefix += coreRequestInfo.getServletRequest().getServletPath();
        return coreRequestInfo.getServletResponse().encodeRedirectURL(
                prefix + path.urlSuffix());
    }

    @Override
    public HttpRequest toHttpRequest(ActionInvocation<Object> invocation) {
        return new HttpRequestImpl(getCoreUtil().toPathInfo(
                toStringInvocation(invocation)));
    }

    @Override
    public CoreUtil getCoreUtil() {
        return this;
    }

    @Override
    public String combineCssClasses(String... classes) {
        return Arrays.asList(classes).stream()
                .filter(x -> !Strings.isNullOrEmpty(x))
                .map(CharMatcher.WHITESPACE::trimFrom)
                .collect(Collectors.joining(" "));
    }

    @Override
    public <T extends IController> T go(Class<T> controllerClass) {
        return path().go(controllerClass);
    }

    @Override
    public <T extends IController> ActionInvocationBuilderKnownController<T> path(
            Class<T> controllerClass) {
        return actionPathBuilderKnownController.get().initialize(
                controllerClass);
    }

    @Override
    public ActionInvocationBuilder path() {
        return actionPathBuilderProvider.get();
    }

    @Override
    public String toString(LString string) {
        return string.resolve(currentLocale.getCurrentLocale());
    }

    @Override
    public LabelUtil labelUtil() {
        return labelUtil;
    }

    @Override
    public String refererUrl() {
        return coreRequestInfo.getServletRequest().getHeader("Referer");
    }

    @Override
    public String urlStatic(PathInfo path) {
        String prefix = contextPath;
        return prefix + path.getValue();
    }
}
