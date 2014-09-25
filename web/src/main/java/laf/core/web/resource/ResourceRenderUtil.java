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

	public Renderable cssBundle(Function<String, String> url, ResourceOutput css) {
		return html -> {
			css.forEach(r -> {
				try {
					html.link(rel("stylesheet").type("text/css").href(
							url.apply(r.getName().substring(1))));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		};
	}

	public Renderable jsBundle(Function<String, String> url, ResourceOutput js) {
		return html -> {
			js.forEach(r -> {
				try {
					html.script(src(url.apply(r.getName().substring(1))))
							._script();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		};
	}
}
