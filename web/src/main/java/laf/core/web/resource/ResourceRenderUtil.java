package laf.core.web.resource;

import static org.rendersnake.HtmlAttributesFactory.*;

import java.io.IOException;
import java.util.function.Function;

import javax.inject.Inject;

import laf.core.http.CoreRequestInfo;

import org.rendersnake.Renderable;

public class ResourceRenderUtil {

	@Inject
	CoreRequestInfo coreRequestInfo;

	public Renderable jsBundle(Function<String, String> url, String... files) {
		return html -> {
			coreRequestInfo.getResourceRequestHandler().render(
					new ResourceBundle(ResourceType.JS, files),
					path -> {
						try {
							html.script(src(url.apply(path)))._script();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
		};
	}

	public Renderable cssBundle(Function<String, String> url, String... files) {
		return html -> {
			coreRequestInfo.getResourceRequestHandler().render(
					new ResourceBundle(ResourceType.CSS, files),
					path -> {
						try {
							html.link(rel("stylesheet").type("text/css").href(
									url.apply(path)));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
		};
	}
}
