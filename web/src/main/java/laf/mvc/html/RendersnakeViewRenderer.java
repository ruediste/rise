package laf.mvc.html;

import java.io.*;
import java.nio.charset.Charset;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ActionResult;
import laf.http.ContentRenderResult;
import laf.mvc.ViewRenderer;

import org.rendersnake.HtmlCanvas;

public class RendersnakeViewRenderer implements ViewRenderer {

	private static Charset UTF8 = Charset.forName("UTF-8");

	@Inject
	Instance<Object> viewInstance;

	@Inject
	MvcRenderUtil util;

	@SuppressWarnings("unchecked")
	private <TData> void initializeView(RendersnakeView<TData> view, Object data) {
		view.initialize((TData) data);
	}

	@Override
	public ActionResult renderView(Class<?> viewClass, Object data)
			throws IOException {
		if (RendersnakeView.class.isAssignableFrom(viewClass)) {
			RendersnakeView<?> view = (RendersnakeView<?>) viewInstance.select(
					viewClass).get();
			initializeView(view, data);

			ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
			OutputStreamWriter writer = new OutputStreamWriter(stream, UTF8);
			HtmlCanvas canvas = new HtmlCanvas(writer);

			view.render(canvas, util);
			writer.flush();
			return new ContentRenderResult(stream.toByteArray());
		}
		return null;
	}
}
