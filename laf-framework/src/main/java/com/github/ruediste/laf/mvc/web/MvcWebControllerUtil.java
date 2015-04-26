package com.github.ruediste.laf.mvc.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Provider;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.web.ContentRenderResult;
import com.github.ruediste.laf.core.web.RedirectRenderResult;
import com.github.ruediste.laf.mvc.InvocationActionResult;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcWebControllerUtil {

	private static Charset UTF8 = Charset.forName("UTF-8");

	@Inject
	Injector injector;

	@Inject
	ActionInvocationUtil util;

	@Inject
	Provider<MvcWebActionPathBuilder> actionPathBuilderProvider;
	@Inject
	Provider<ActionPathBuilderKnownController<?>> actionPathBuilderKnownController;

	public <TView extends ViewMvcWeb<?, TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data) {

		TView view = injector.getInstance(viewClass);
		view.initialize(data);

		ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
		OutputStreamWriter writer = new OutputStreamWriter(stream, UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);

		try {
			view.render(canvas);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);

		}
		return new ContentRenderResult(stream.toByteArray());
	}

	public ActionResult redirect(ActionResult path) {
		return new RedirectRenderResult(
				util.toPathInfo((InvocationActionResult) path));
	}

	public MvcWebActionPathBuilder path() {
		return actionPathBuilderProvider.get();
	}

	public <T extends IControllerMvcWeb> ActionPathBuilderKnownController<T> path(
			Class<T> controllerClass) {
		return actionPathBuilderKnownController.get().initialize(
				controllerClass);
	}
}
