package com.github.ruediste.rise.core;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.rendersnake.Renderable;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.core.web.assetPipeline.AssetRenderUtil;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

public class CoreUtil implements ICoreUtil {

    @Inject
    CoreConfiguration coreConfiguration;

    @Inject
    HttpService httpService;

    @Inject
    AssetRenderUtil assetRenderUtil;

    @Inject
    Provider<ActionInvocationBuilder> actionPathBuilderProvider;

    @Inject
    Provider<ActionInvocationBuilderKnownController<?>> actionPathBuilderKnownController;

    @Override
    public PathInfo toPathInfo(ActionInvocation<Object> invocation) {
        return coreConfiguration.toPathInfo(toStringInvocation(invocation));
    }

    @Override
    public PathInfo toPathInfo(ActionResult invocation) {
        return toPathInfo((ActionInvocation<Object>) ((ActionInvocationResult) invocation));
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
    public String url(String pathInfo) {
        return url(new PathInfo(pathInfo));
    }

    @Override
    public String url(PathInfo path) {
        return httpService.url(path);
    }

    @Override
    public String url(ActionResult path) {
        return url(toPathInfo(path));
    }

    @Override
    public HttpRequest toHttpRequest(ActionInvocation<Object> invocation) {
        return new HttpRequestImpl(toPathInfo(invocation));
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
    public Renderable jsLinks(AssetBundleOutput output) {
        return assetRenderUtil.renderJs(this::url, output);
    }

    @Override
    public Renderable cssLinks(AssetBundleOutput output) {
        return assetRenderUtil.renderCss(this::url, output);
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
}
