package com.github.ruediste.laf.api;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.web.PathInfo;
import com.github.ruediste.laf.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.laf.mvc.web.IControllerMvcWeb;
import com.github.ruediste.laf.mvc.web.MvcWebRenderUtil;
import com.google.common.reflect.TypeToken;

/**
 * Base Class for views of the MVC framework
 */
public abstract class ViewMvcWeb<TController extends IControllerMvcWeb, TData> {

	@Inject
	private MvcWebRenderUtil util;

	private TData data;

	private Class<TController> controllerClass;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ViewMvcWeb() {
		controllerClass = (Class) TypeToken.of(getClass())
				.resolveType(ViewMvcWeb.class.getTypeParameters()[0])
				.getRawType();
	}

	public final void initialize(TData data) {
		this.data = data;
	}

	public final TData data() {
		return data;
	}

	abstract public void render(HtmlCanvas html) throws IOException;

	public <T extends IControllerMvcWeb> T path(Class<T> controller) {
		return util.path(controller);
	}

	public String url(ActionResult path) {
		return util.url(path);
	}

	public String url(PathInfo path) {
		return util.url(path);
	}

	public TController path() {
		return util.path().go(controllerClass);
	}

	public Renderable jsLinks(AssetBundleOutput output) {
		return util.jsLinks(output);
	}

	public Renderable cssBundle(AssetBundleOutput output) {
		return util.cssBundle(output);
	}

}