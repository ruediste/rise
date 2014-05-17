package laf.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import laf.base.RenderResult;

import org.rendersnake.HtmlCanvas;

public class PageRenderResult<TController> implements RenderResult {

	private static Charset UTF8 = Charset.forName("UTF-8");
	private ComponentView<TController> view;

	public static <TController> PageRenderResult<TController> create(
			TController controller,
			Class<? extends ComponentView<? super TController>> viewClass) {
		return new PageRenderResult<>(null);

	}

	private PageRenderResult(ComponentView<TController> view) {
		this.view = view;
	}

	@Override
	public void sendTo(HttpServletResponse response) throws IOException {
		// assign the page ID

		// render the view first, to detect possible errors
		// before rendering the result
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
		OutputStreamWriter writer = new OutputStreamWriter(stream,
				PageRenderResult.UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);
		try {
			view.getRootComponent().render(canvas);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);
		}
		byte[] byteArray = stream.toByteArray();

		// send answer
		response.setContentType("application/xhtml+xml; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getOutputStream().write(byteArray);
		response.flushBuffer();
	}
}
