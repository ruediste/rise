package com.github.ruediste.laf.mvc.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.transaction.TransactionManager;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.actionInvocation.InvocationActionResult;
import com.github.ruediste.laf.core.web.ContentRenderResult;
import com.github.ruediste.laf.core.web.RedirectRenderResult;
import com.github.ruediste.salta.jsr330.Injector;

public class MvcWebControllerUtil {

	private static Charset UTF8 = Charset.forName("UTF-8");

	@Inject
	Injector injector;

	@Inject
	MvcActionInvocationUtil util;

	@Inject
	Provider<MvcWebActionPathBuilder> actionPathBuilderProvider;

	@Inject
	Provider<ActionPathBuilderKnownController<?>> actionPathBuilderKnownController;

	@Inject
	TransactionManager txm;

	@Inject
	MvcWebRequestInfo info;

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

	/**
	 * Commit the current transaction. After this method returns, the current
	 * transaction is closed. Thus views should be created before calling this
	 * method.
	 */
	public void commit() {
		if (!info.isUpdating()) {
			throw new RuntimeException(
					"Cannot commit from a method not annotated with @Updating");
		}

		info.getTransactionControl().commit();
	}
}
