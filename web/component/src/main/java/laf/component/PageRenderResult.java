package laf.component;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.RenderResult;

public class PageRenderResult<TController> implements RenderResult {

	@Inject
	PageMap pageMap;

	@Inject
	ComponentCoreModule componentCoreModule;

	@Inject
	ComponentService componentService;

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
		long id = pageMap.register(view);
		componentCoreModule.setPageId(id);

		// render page
		componentService.renderPage(view, response);
	}
}
