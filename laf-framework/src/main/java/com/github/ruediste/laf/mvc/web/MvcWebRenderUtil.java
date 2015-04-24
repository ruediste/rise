package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;
import javax.inject.Provider;

import org.rendersnake.Renderable;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.HttpService;
import com.github.ruediste.laf.core.scopes.RequestScoped;
import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.core.web.assetPipeline.AssetRenderUtil;
import com.github.ruediste.laf.mvc.InvocationActionResult;

@RequestScoped
public class MvcWebRenderUtil {

	@Inject
	Provider<MvcWebActionPathBuilder> actionPathBuilderInstance;

	@Inject
	HttpService httpService;

	@Inject
	ActionInvocationUtil util;

	@Inject
	AssetRenderUtil assetRenderUtil;

	public <T> T path(Class<T> controller) {
		return path().controller(controller);
	}

	public String url(ActionResult path) {
		return url(util.toPathInfo((InvocationActionResult) path));
	}

	public String url(PathInfo path) {
		return httpService.url(path);
	}

	public MvcWebActionPathBuilder path() {
		return actionPathBuilderInstance.get();
	}

	public Renderable jsLinks(AssetBundleOutput output) {
		return assetRenderUtil.renderJs(this::url, output);
	}

	public Renderable cssBundle(AssetBundleOutput output) {
		return assetRenderUtil.renderCss(this::url, output);
	}
}
