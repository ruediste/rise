package com.github.ruediste.laf.mvc.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.transaction.TransactionManager;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.api.ViewMvcWeb;
import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.CoreUtil;
import com.github.ruediste.laf.core.HttpService;
import com.github.ruediste.laf.core.ICoreUtil;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.laf.core.web.ContentRenderResult;
import com.github.ruediste.laf.core.web.RedirectRenderResult;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Charsets;

public class MvcUtil implements ICoreUtil {

	@Inject
	Provider<ActionInvocationBuilder> actionPathBuilderInstance;

	@Inject
	HttpService httpService;

	@Inject
	CoreUtil coreUtil;

	@Inject
	Injector injector;

	@Inject
	TransactionManager txm;

	@Inject
	MvcWebRequestInfo info;

	@Override
	public CoreUtil getCoreUtil() {
		return coreUtil;
	}

	public <TView extends ViewMvcWeb<?, TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data) {

		TView view = injector.getInstance(viewClass);
		view.initialize(data);

		ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
		OutputStreamWriter writer = new OutputStreamWriter(stream,
				Charsets.UTF_8);
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
				coreUtil.toPathInfo((ActionInvocationResult) path));
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
