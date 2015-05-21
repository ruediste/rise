package com.github.ruediste.rise.component.components.template;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.IController;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilderKnownController;
import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.assetPipeline.AssetBundleOutput;

public class ComponentTemplateBase<T extends Component> implements
		CWTemplate<T> {

	@Inject
	protected ComponentUtil util;

	@Override
	public void doRender(T component, HtmlCanvas html)
			throws IOException {
		for (Component child : component.getChildren()) {
			render(child, html);
		}
	}

	@Override
	public void applyValues(T component) {

	}

	@Override
	public void raiseEvents(T component) {

	}

	public String url(PathInfo path) {
		return util.url(path);
	}

	public String url(String pathInfo) {
		return util.url(pathInfo);
	}

	public String url(ActionResult path) {
		return util.url(path);
	}

	public Renderable cssLinks(AssetBundleOutput output) {
		return util.cssLinks(output);
	}

	public Renderable jsLinks(AssetBundleOutput output) {
		return util.jsLinks(output);
	}

	public String combineCssClasses(String... classes) {
		return util.combineCssClasses(classes);
	}

	public <T extends IController> T go(Class<T> controllerClass) {
		return util.go(controllerClass);
	}

	public <T extends IController> ActionInvocationBuilderKnownController<T> path(
			Class<T> controllerClass) {
		return util.path(controllerClass);
	}

	public String getKey(Component component, String key) {
		return util.getKey(component, key);
	}

	public long getComponentNr(Component component) {
		return util.getComponentNr(component);
	}

	public ActionInvocationBuilder path() {
		return util.path();
	}

	public void render(Component component, HtmlCanvas html) {
		util.render(component, html);
	}

	public String getComponentId(Component component) {
		return util.getComponentId(component);
	}

	public String getParameterValue(Component component, String key) {
		return util.getParameterValue(component, key);
	}

	public boolean isParameterDefined(Component component, String key) {
		return util.isParameterDefined(component, key);
	}

}
