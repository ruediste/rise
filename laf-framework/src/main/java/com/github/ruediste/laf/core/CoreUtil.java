package com.github.ruediste.laf.core;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.rendersnake.Renderable;

import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.actionInvocation.InvocationActionResult;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.core.web.assetPipeline.AssetRenderUtil;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

public class CoreUtil implements ICoreUtil {

	@Inject
	CoreConfiguration coreConfiguration;

	@Inject
	HttpService httpService;

	@Inject
	AssetRenderUtil assetRenderUtil;

	@Override
	public PathInfo toPathInfo(ActionInvocation<Object> invocation) {
		return invocation.strategies.generate(toStringInvocation(invocation));
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

	public String url(ActionResult path) {
		return url(toPathInfo((InvocationActionResult) path));
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
}
