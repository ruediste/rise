package com.github.ruediste.laf.mvc.web;

import java.io.*;
import java.nio.charset.Charset;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.http.ContentRenderResult;
import com.github.ruediste.laf.core.http.RedirectRenderResult;
import com.github.ruediste.laf.mvc.core.PathActionResult;

public class ControllerUtilImpl implements MWControllerUtil {

	private static Charset UTF8 = Charset.forName("UTF-8");

	@Inject
	Instance<Object> viewInstance;

	@Inject
	MWRenderUtil renderUtil;

	@Inject
	RequestMappingUtil mappingUtil;

	@SuppressWarnings("unchecked")
	private <TData> void initializeView(MvcWebView<TData> view, Object data) {
		view.initialize((TData) data);
	}

	@Override
	public <TView extends MvcWebView<TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data) {

		MvcWebView<?> view = viewInstance.select(viewClass).get();
		initializeView(view, data);

		ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
		OutputStreamWriter writer = new OutputStreamWriter(stream, UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);

		try {
			view.render(canvas, renderUtil);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);

		}
		return new ContentRenderResult(stream.toByteArray());
	}

	@Override
	public ActionResult redirect(ActionResult path) {
		return new RedirectRenderResult(
				mappingUtil.generate((PathActionResult) path));
	}

}
