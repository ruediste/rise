package laf.skeleton.componentTemplates;

import static org.rendersnake.HtmlAttributesFactory.data;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.web.CWRenderUtil;
import com.github.ruediste.laf.component.web.CWTemplateBase;
import com.github.ruediste.laf.component.web.components.CPage;
import com.github.ruediste.laf.core.web.resource.ResourceOutput;
import com.github.ruediste.laf.core.web.resource.StaticWebResourceBundle;

public class CPageHtmlTemplate extends CWTemplateBase<CPage> {
	public static class Bundle extends StaticWebResourceBundle {

		private final ResourceOutput js = new ResourceOutput(this);

		@Override
		protected void initializeImpl() {

			paths("/static/js/jquery-1.11.1.js")
					.load(servletContext())
					.merge(paths("/js/componentWeb.js").load(classPath()).name(
							"/static{qname}.{ext}")).send(js);
		}

		public ResourceOutput getJs() {
			return js;
		}

	}

	@Inject
	Bundle bundle;
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CPage component, HtmlCanvas html) throws IOException {

		//@formatter:off
		html.write("<!DOCTYPE html>",false)
		.html()
			.head()
				.title().content("Yeah")
			._head()
			.body(data("reloadurl", util.getReloadUrl()));
				super.render(component, html);
				html.render(util.jsBundle(bundle.getJs()))
			._body()
		._html();
	}
}
