package com.github.ruediste.rise.api;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;
import com.github.ruediste.rise.mvc.web.IControllerMvcWeb;
import com.github.ruediste.rise.mvc.web.MvcUtil;
import com.google.common.reflect.TypeToken;

/**
 * Base Class for views of the MVC framework
 */
public abstract class ViewMvcWeb<TController extends IControllerMvcWeb, TData> {

	@Inject
	private MvcUtil util;

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

	public ActionInvocationBuilderKnownController<TController> path() {
		return util.path(controllerClass);
	}

	public <T extends IController> ActionInvocationBuilderKnownController<T> path(
			Class<T> controllerClass) {
		return util.path(controllerClass);
	}

	public <T extends IController> T go(Class<T> controllerClass) {
		return util.go(controllerClass);
	}

	public TController go() {
		return util.go(controllerClass);
	}

	public String url(ActionResult path) {
		return util.url(path);
	}

	public String url(PathInfo path) {
		return util.url(path);
	}

	public Renderable jsLinks(AssetBundleOutput output) {
		return util.jsLinks(output);
	}

	public Renderable cssBundle(AssetBundleOutput output) {
		return util.cssLinks(output);
	}

}