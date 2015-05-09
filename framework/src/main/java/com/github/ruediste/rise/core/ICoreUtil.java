package com.github.ruediste.rise.core;

import java.util.function.Supplier;

import org.rendersnake.Renderable;

import com.github.ruediste.rise.api.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;

public interface ICoreUtil {

	CoreUtil getCoreUtil();

	default HttpRequest toHttpRequest(ActionInvocation<Object> invocation) {
		return getCoreUtil().toHttpRequest(invocation);
	}

	default String url(PathInfo path) {
		return getCoreUtil().url(path);
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

	default PathInfo toPathInfo(ActionInvocation<Object> invocation) {
		return getCoreUtil().toPathInfo(invocation);
	}

	default Renderable cssLinks(AssetBundleOutput output) {
		return getCoreUtil().cssLinks(output);
	}

	default Renderable jsLinks(AssetBundleOutput output) {
		return getCoreUtil().jsLinks(output);
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
}
