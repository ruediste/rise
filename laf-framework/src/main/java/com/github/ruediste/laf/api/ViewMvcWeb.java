package com.github.ruediste.laf.api;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.mvc.web.MvcWebActionPathBuilder;
import com.github.ruediste.laf.mvc.web.MvcWebRenderUtil;

/**
 * Base Class for views of the MVC framework
 */
public abstract class ViewMvcWeb<TData> {

	@Inject
	private MvcWebRenderUtil util;

	private TData data;

	public final void initialize(TData data) {
		this.data = data;
	}

	public final TData data() {
		return data;
	}

	abstract public void render(HtmlCanvas html) throws IOException;

	public <T> T path(Class<T> controller) {
		return util.path(controller);
	}

	public String url(ActionResult path) {
		return util.url(path);
	}

	public String url(PathInfo path) {
		return util.url(path);
	}

	public MvcWebActionPathBuilder path() {
		return util.path();
	}

	public Renderable jsLinks(AssetBundleOutput output) {
		return util.jsLinks(output);
	}

	public Renderable cssBundle(AssetBundleOutput output) {
		return util.cssBundle(output);
	}

}