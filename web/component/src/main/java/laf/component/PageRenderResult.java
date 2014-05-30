package laf.component;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.RenderResult;
import laf.component.core.ComponentCoreModule;
import laf.component.core.ComponentView;
import laf.component.core.ComponentViewRepository;
import laf.requestProcessing.CurrentController;

public class PageRenderResult implements RenderResult {

	@Inject
	PageMap pageMap;

	@Inject
	ComponentCoreModule componentCoreModule;

	@Inject
	ComponentService componentService;

	@Inject
	ComponentViewRepository repository;

	@Inject
	CurrentController currentController;

	@Override
	public void sendTo(HttpServletResponse response) throws IOException {
		// create the view
		ComponentView<? extends Object> view = repository
				.createView(currentController.get());

		// assign the page ID
		long id = pageMap.register(view);
		componentCoreModule.setPageId(id);

		// render page
		componentService.renderPage(view, response);
	}
}
