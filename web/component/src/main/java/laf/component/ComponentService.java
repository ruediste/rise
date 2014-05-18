package laf.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletResponse;

import org.rendersnake.HtmlCanvas;

@ApplicationScoped
public class ComponentService {

	private static Charset UTF8 = Charset.forName("UTF-8");

	public void renderPage(ComponentView<?> view, HttpServletResponse response) {
		// render the view first, to detect possible errors
		// before rendering the result
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
		OutputStreamWriter writer = new OutputStreamWriter(stream,
				ComponentService.UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);
		try {
			view.getRootComponent().render(canvas);
			writer.close();
			byte[] byteArray = stream.toByteArray();

			// send answer
			response.setContentType("application/xhtml+xml; charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().write(byteArray);
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);
		}
	}
}
