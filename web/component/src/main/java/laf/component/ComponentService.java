package laf.component;

import java.io.*;
import java.nio.charset.Charset;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.component.core.Component;
import laf.component.core.ComponentView;
import laf.component.html.RenderUtilImpl;

import org.rendersnake.HtmlCanvas;

@ApplicationScoped
public class ComponentService {

	private static Charset UTF8 = Charset.forName("UTF-8");

	@Inject
	Instance<RenderUtilImpl> renderUtilInstance;

	public String calculateKey(Component component, String key) {
		return "c_" + component.getComponentId() + "_" + key;
	}

	public void renderPage(ComponentView<?> view, HttpServletResponse response) {
		// render the view first, to detect possible errors
		// before rendering the result
		ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
		OutputStreamWriter writer = new OutputStreamWriter(stream,
				ComponentService.UTF8);
		HtmlCanvas canvas = new HtmlCanvas(writer);
		try {
			RenderUtilImpl renderUtil = renderUtilInstance.get();
			renderUtil.setComponent(view.getRootComponent());
			view.getRootComponent().render(canvas, renderUtil);
			writer.close();
			byte[] byteArray = stream.toByteArray();

			// send answer
			response.setContentType("text/html; charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().write(byteArray);
			response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException("Error while rendering view", e);
		}
	}
}
