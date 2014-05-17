package laf.component;

import java.io.*;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import laf.base.RenderResult;

import org.rendersnake.HtmlCanvas;

public class PageRenderResult<TController> implements RenderResult {

	private static Charset UTF8 = Charset.forName("UTF-8");
	private byte[] byteArray;

	public PageRenderResult(ComponentView<TController> view) {

		// render the view
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
		OutputStreamWriter writer = new OutputStreamWriter(stream, UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);
		try {
			view.render(canvas);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);
		}
		byteArray = stream.toByteArray();
	}

	@Override
	public void sendTo(HttpServletResponse response) throws IOException {
		response.setContentType("text/xhmtl");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getOutputStream().write(byteArray);
		response.flushBuffer();
	}
}
