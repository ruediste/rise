package laf.mvc.web;

import java.io.*;
import java.nio.charset.Charset;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.core.http.ContentRenderResult;
import laf.core.http.RedirectRenderResult;
import laf.mvc.actionPath.PathActionResult;

import org.rendersnake.HtmlCanvas;

public class ControllerUtilImpl implements ControllerUtil {

	private static Charset UTF8 = Charset.forName("UTF-8");

	@Inject
	Instance<Object> viewInstance;

	@Inject
	RenderUtil renderUtil;

	@Inject
	RequestMappingUtil mappingUtil;

	@SuppressWarnings("unchecked")
	private <TData> void initializeView(MvcWebView<TData> view, Object data) {
		view.initialize((TData) data);
	}

	@Override
	public ActionResult view(Class<?> viewClass, Object data) {
		MvcWebView<?> view = (MvcWebView<?>) viewInstance.select(viewClass)
				.get();
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
